package utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class MethodHandleCache extends AbstractMethodsCache<MethodHandle, MethodHandle>{

    public MethodHandleCache(Class clazz) {
        super(clazz);
    }

    @Override
    protected MethodHandle createSetter(Class clazz, Method method) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            method.setAccessible(true);
            return lookup.findVirtual(clazz, method.getName(), MethodType.methodType(void.class, method.getParameterTypes()[0]));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected MethodHandle createGetter(Class clazz, Method method) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            method.setAccessible(true);
            return lookup.findVirtual(clazz, method.getName(), MethodType.methodType(method.getReturnType()));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getValue(String property, Object object) {
        try {
            return findGetter(property, object).invoke(object);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    public void setValue(String property, Object value, Object object) {
        try {
            findSetter(property, object).invoke(object, value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
