package utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodsCache extends AbstractMethodsCache<Method, Method> {

    public MethodsCache(Class clazz) {
        super(clazz);
    }

    @Override
    protected Method createGetter(Class clazz, Method method) {
        method.setAccessible(true);
        return method;
    }

    @Override
    protected Method createSetter(Class clazz, Method method) {
        method.setAccessible(true);
        return method;
    }

    @Override
    public Object getValue(String property, Object object) {
        try {
            return findGetter(property, object).invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setValue(String property, Object value, Object object) {
        try {
            findSetter(property, object).invoke(object, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}