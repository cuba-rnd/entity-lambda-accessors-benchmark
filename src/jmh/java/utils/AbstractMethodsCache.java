package utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMethodsCache<G, S> {

    protected final Map<String, G> getters = new HashMap<>();
    protected final Map<String, S> setters = new HashMap<>();


    protected abstract G createGetter(Class clazz, Method method);

    protected abstract S createSetter(Class clazz, Method method);

    public abstract Object getValue(String property, Object object);

    public abstract void setValue(String property, Object value, Object object);

    public AbstractMethodsCache(Class clazz) {
        final Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.startsWith("get") && method.getParameterTypes().length == 0) {
                G getter = createGetter(clazz, method);
                name = StringUtils.uncapitalize(name.substring(3));
                getters.put(name, getter);
            } else if (name.startsWith("is") && method.getParameterTypes().length == 0) {
                G getter = createGetter(clazz, method);
                name = StringUtils.uncapitalize(name.substring(2));
                getters.put(name, getter);
            } else if (name.startsWith("set") && method.getParameterTypes().length == 1) {
                S setter = createSetter(clazz, method);
                name = StringUtils.uncapitalize(name.substring(3));
                setters.put(name, setter);
            }
        }
    }

    protected G findGetter(String property, Object object) {
        G getter = getters.get(property);
        if (getter == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find getter for property '%s' at %s", property, object.getClass()));
        }
        return getter;
    }

    protected S findSetter(String property, Object object) {
        S setter = setters.get(property);
        if (setter == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find setter for property '%s' at %s", property, object.getClass()));
        }
        return setter;
    }


}
