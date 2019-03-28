package performance;

import utils.LambdaMethodsCache;
import utils.MethodHandleCache;
import utils.MethodsCache;

import java.util.UUID;

public class Employee {

        private static MethodsCache methodCache;
        private static LambdaMethodsCache lambdaMethodsCache;
        private static MethodHandleCache methodHandleCache;

        private UUID id;
        private String name;
        private final Class cls = getClass();


        public Employee() {
            methodCache = new MethodsCache(cls);
            methodHandleCache = new MethodHandleCache(cls);
            lambdaMethodsCache = new LambdaMethodsCache(cls);
        }


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


    public void setValueOldCache(String propertyName, Object propertyValue) {
        methodCache.setValue(propertyName, propertyValue, this);
    }

    public Object getValueOldCache(String propertyName) {
        return methodCache.getValue(propertyName, this);
    }


    public void setValueLambdaCache(String propertyName, Object propertyValue) {
        lambdaMethodsCache.setValue(propertyName, propertyValue, this);
    }

    public Object getValueLambdaCache(String propertyName) {
        return lambdaMethodsCache.getValue(propertyName, this);
    }

    public void setValueMethodHandleCache(String propertyName, Object propertyValue) {
            methodHandleCache.setValue(propertyName, propertyValue, this);
    }

    public Object getValueMethodHandleCache(String propertyName) {
        return methodHandleCache.getValue(propertyName, this);
    }


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
