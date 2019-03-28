package performance;

import utils.LambdaMethodsCache;
import utils.MethodHandleCache;
import utils.MethodsCache;

import java.util.UUID;

public class Employee {

    private static final MethodsCache methodCache;
    private static final LambdaMethodsCache lambdaMethodsCache;
    private static final MethodHandleCache methodHandleCache;

    static {
        methodCache = new MethodsCache(Employee.class);
        methodHandleCache = new MethodHandleCache(Employee.class);
        lambdaMethodsCache = new LambdaMethodsCache(Employee.class);
    }

    private UUID id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setValue(String propertyName, Object propertyValue) {
        setValueNative(propertyName, propertyValue);
    }

    public Object getValue(String propertyName) {
        return getValueNative(propertyName);
    }

    //Classic reflection method access
    public void setValueReflectionCache(String propertyName, Object propertyValue) {
        methodCache.setValue(propertyName, propertyValue, this);
    }

    public Object getValueReflectionCache(String propertyName) {
        return methodCache.getValue(propertyName, this);
    }

    //LambdaMetafactory based method access
    public void setValueLambdaCache(String propertyName, Object propertyValue) {
        lambdaMethodsCache.setValue(propertyName, propertyValue, this);
    }

    public Object getValueLambdaCache(String propertyName) {
        return lambdaMethodsCache.getValue(propertyName, this);
    }

    //MethodHandle method access
    public void setValueMethodHandleCache(String propertyName, Object propertyValue) {
        methodHandleCache.setValue(propertyName, propertyValue, this);
    }

    public Object getValueMethodHandleCache(String propertyName) {
        return methodHandleCache.getValue(propertyName, this);
    }

    //"Native" method access
    public void setValueNative(String propertyName, Object propertyValue) {
        switch (propertyName) {
            case "name":
                this.setName((String) propertyValue);
                return;
            case "id":
                this.setId((UUID) propertyValue);
                return;
            default: {
                throw new RuntimeException(String.format("Property %s does not exists", propertyName));
            }
        }
    }

    public Object getValueNative(String propertyName) {
        switch (propertyName) {
            case "name":
                return this.getName();
            case "id":
                return this.getId();
            default: {
                throw new RuntimeException(String.format("Property %s does not exists", propertyName));
            }
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
