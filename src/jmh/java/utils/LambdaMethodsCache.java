package utils;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class LambdaMethodsCache extends AbstractMethodsCache<Function, BiConsumer>{

    public LambdaMethodsCache(Class clazz) {
        super(clazz);
    }

    @Override
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
            throw new RuntimeException(t);
        }

        return getter;
    }

    @Override
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
            throw new RuntimeException(t);
        }

        return setter;
    }

    @Override
    public Object getValue(String property, Object object) {
        return findGetter(property, object).apply(object);
    }

    @Override
    public void setValue(String property, Object value, Object object) {
        findSetter(property, object).accept(object, value);
    }
}
