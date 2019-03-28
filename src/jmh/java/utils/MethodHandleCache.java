package utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodHandleCache {

    private final transient Map<String, MethodHandle> getters = new HashMap<>();
    private final transient Map<String, MethodHandle> setters = new HashMap<>();


    public MethodHandleCache(Class<?> clazz) {
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterTypes().length == 0) {
                name = StringUtils.uncapitalize(name.substring(3));
                MethodHandle getter = createGetter(clazz, name, method);
                getters.put(name, getter);
            } else if (name.startsWith("is") && method.getParameterTypes().length == 0) {
                name = StringUtils.uncapitalize(name.substring(2));
                MethodHandle getter = createGetter(clazz, name, method);
                getters.put(name, getter);
            } else if (name.startsWith("set") && method.getParameterTypes().length == 1) {
                name = StringUtils.uncapitalize(name.substring(3));
                MethodHandle setter = createSetter(clazz, name, method);
                setters.put(name, setter);
            }
        }
    }

    private MethodHandle createSetter(Class<?> clazz, String name, Method method) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            method.setAccessible(true);
            return lookup.findVirtual(clazz, method.getName(), MethodType.methodType(void.class, method.getParameterTypes()[0]));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private MethodHandle createGetter(Class<?> clazz, String name, Method method) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            method.setAccessible(true);
            return lookup.findVirtual(clazz, method.getName(), MethodType.methodType(method.getReturnType()));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(String property, Object object) {
        MethodHandle getter = getters.get(property);
        if (getter == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find getter for property '%s' at %s", property, object.getClass()));
        }
        try {
            return getter.invoke(object);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public void setValue(String property, Object value, Object object) {
        MethodHandle setter = setters.get(property);
        if (setter == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find setter for property '%s' at %s", property, object.getClass()));
        }
        try {
            setter.invoke(object, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
