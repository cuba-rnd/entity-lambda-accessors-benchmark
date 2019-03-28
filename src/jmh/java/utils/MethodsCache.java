package utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodsCache {

    private final transient Map<String, Method> getters = new HashMap<>();
    private final transient Map<String, Method> setters = new HashMap<>();

    public MethodsCache(Class clazz) {
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterTypes().length == 0) {
                name = StringUtils.uncapitalize(name.substring(3));
                method.setAccessible(true);
                getters.put(name, method);
            } else if (name.startsWith("is") && method.getParameterTypes().length == 0) {
                name = StringUtils.uncapitalize(name.substring(2));
                method.setAccessible(true);
                getters.put(name, method);
            } else if (name.startsWith("set") && method.getParameterTypes().length == 1) {
                name = StringUtils.uncapitalize(name.substring(3));
                method.setAccessible(true);
                setters.put(name, method);
            }
        }
    }

    public void invokeSetter(Object object, String property, Object value) {
        final Method method = setters.get(property);
        if (method == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find setter for property '%s' at %s", property, object.getClass()));
        }
        try {
            method.invoke(object, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Object invokeGetter(Object object, String property) {
        final Method method = getters.get(property);
        if (method == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find getter for property '%s' at %s", property, object.getClass()));
        }
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}