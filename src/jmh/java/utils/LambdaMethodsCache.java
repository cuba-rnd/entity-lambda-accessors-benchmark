package utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LambdaMethodsCache {

    private final transient Map<String, Function> getters = new HashMap<>();
    private final transient Map<String, BiConsumer> setters = new HashMap<>();

    public LambdaMethodsCache(Class clazz) {
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterTypes().length == 0) {
                Function getter = createGetter(clazz, method);
                name = StringUtils.uncapitalize(name.substring(3));
                getters.put(name, getter);
            } else if (name.startsWith("is") && method.getParameterTypes().length == 0) {
                Function getter = createGetter(clazz, method);
                name = StringUtils.uncapitalize(name.substring(2));
                getters.put(name, getter);
            } else if (name.startsWith("set") && method.getParameterTypes().length == 1) {
                BiConsumer setter = createSetter(clazz, method);
                name = StringUtils.uncapitalize(name.substring(3));
                setters.put(name, setter);
            }
        }
    }

    protected Function createGetter(Class clazz, Method method) {
        Function getter;
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            CallSite site = LambdaMetafactory.metafactory(caller,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    caller.findVirtual(clazz, method.getName(), MethodType.methodType(method.getReturnType())),
                    MethodType.methodType(method.getReturnType(), clazz));
            MethodHandle factory = site.getTarget();
            getter = (Function) factory.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }

        return getter;
    }

    protected BiConsumer createSetter(Class clazz, Method method) {
        BiConsumer setter;
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            CallSite site = LambdaMetafactory.metafactory(caller,
                    "accept",
                    MethodType.methodType(BiConsumer.class),
                    MethodType.methodType(void.class, Object.class, Object.class),
                    caller.findVirtual(clazz, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()[0])),
                    MethodType.methodType(void.class, clazz, method.getParameterTypes()[0]));
            MethodHandle factory = site.getTarget();
            setter = (BiConsumer) factory.invoke();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }

        return setter;
    }

    public Object getValue(String property, Object object) {
        Function getter = getters.get(property);
        if (getter == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find getter for property '%s' at %s", property, object.getClass()));
        }
        return getter.apply(object);
    }

    public void setValue(String property, Object value, Object object) {
        BiConsumer setter = setters.get(property);
        if (setter == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find setter for property '%s' at %s", property, object.getClass()));
        }
        setter.accept(object, value);
    }
}
