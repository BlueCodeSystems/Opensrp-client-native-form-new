package com.vijay.jsonwizard.testutils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight reflection utilities for tests, replacing legacy PowerMock Whitebox usage.
 */
public final class TestReflectionHelpers {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = new HashMap<>();

    static {
        PRIMITIVE_WRAPPERS.put(boolean.class, Boolean.class);
        PRIMITIVE_WRAPPERS.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPERS.put(short.class, Short.class);
        PRIMITIVE_WRAPPERS.put(char.class, Character.class);
        PRIMITIVE_WRAPPERS.put(int.class, Integer.class);
        PRIMITIVE_WRAPPERS.put(long.class, Long.class);
        PRIMITIVE_WRAPPERS.put(float.class, Float.class);
        PRIMITIVE_WRAPPERS.put(double.class, Double.class);
    }

    private TestReflectionHelpers() {
    }

    public static void setInternalState(Object target, String fieldName, Object value) {
        setField(target, fieldName, value);
    }

    public static void setInternalState(Class<?> clazz, String fieldName, Object value) {
        setStaticField(clazz, fieldName, value);
    }

    public static void setField(Object target, String fieldName, Object value) {
        if (target == null) {
            throw new IllegalArgumentException("Target object must not be null");
        }
        Field field = findField(target.getClass(), fieldName);
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to set field '" + fieldName + "'", e);
        }
    }

    public static void setStaticField(Class<?> clazz, String fieldName, Object value) {
        Field field = findField(clazz, fieldName);
        try {
            field.setAccessible(true);
            field.set(null, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to set static field '" + fieldName + "'", e);
        }
    }

    public static <T> T getInternalState(Object target, String fieldName) {
        return getField(target, fieldName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(Object target, String fieldName) {
        if (target == null) {
            throw new IllegalArgumentException("Target object must not be null");
        }
        Field field = findField(target.getClass(), fieldName);
        try {
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to read field '" + fieldName + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStaticField(Class<?> clazz, String fieldName) {
        Field field = findField(clazz, fieldName);
        try {
            field.setAccessible(true);
            return (T) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to read static field '" + fieldName + "'", e);
        }
    }

    public static <T> T invokeMethod(Object target, String methodName, Object... args) {
        if (target == null) {
            throw new IllegalArgumentException("Target object must not be null");
        }
        Class<?>[] parameterTypes = args != null ? inferParameterTypes(args) : new Class<?>[0];
        return invoke(target, methodName, parameterTypes, args == null ? new Object[0] : args);
    }

    public static <T> T callInstanceMethod(Object target, String methodName, ClassParameter<?>... parameters) {
        if (target == null) {
            throw new IllegalArgumentException("Target object must not be null");
        }
        Class<?>[] parameterTypes = new Class<?>[parameters.length];
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = parameters[i].clazz;
            args[i] = parameters[i].value;
        }
        return invoke(target, methodName, parameterTypes, args);
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Object target, String methodName, Class<?>[] parameterTypes, Object[] args) {
        Method method = findMethod(target.getClass(), methodName, parameterTypes, args);
        try {
            method.setAccessible(true);
            return (T) method.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to invoke method '" + methodName + "'", e);
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new RuntimeException(new NoSuchFieldException(fieldName));
    }

    private static Method findMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] args) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                Method method = current.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (NoSuchMethodException ignored) {
                current = current.getSuperclass();
            }
        }
        // Fall back to lenient search when exact signature not found
        current = clazz;
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (!method.getName().equals(methodName)) {
                    continue;
                }
                if (parametersMatch(method.getParameterTypes(), args)) {
                    return method;
                }
            }
            current = current.getSuperclass();
        }
        throw new RuntimeException(new NoSuchMethodError(methodName));
    }

    private static boolean parametersMatch(Class<?>[] parameterTypes, Object[] args) {
        if (parameterTypes.length != (args == null ? 0 : args.length)) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            Object arg = args[i];
            Class<?> parameterType = parameterTypes[i];
            if (arg == null) {
                if (parameterType.isPrimitive()) {
                    return false;
                }
                continue;
            }
            Class<?> argClass = arg.getClass();
            if (parameterType.isPrimitive()) {
                Class<?> wrapper = PRIMITIVE_WRAPPERS.get(parameterType);
                if (wrapper == null || !wrapper.isAssignableFrom(argClass)) {
                    return false;
                }
            } else if (!parameterType.isAssignableFrom(argClass)) {
                return false;
            }
        }
        return true;
    }

    private static Class<?>[] inferParameterTypes(Object[] args) {
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            types[i] = arg == null ? Object.class : arg.getClass();
        }
        return types;
    }

    public static final class ClassParameter<T> {
        public final Class<T> clazz;
        public final T value;

        private ClassParameter(Class<T> clazz, T value) {
            this.clazz = clazz;
            this.value = value;
        }

        public static <T> ClassParameter<T> from(Class<T> clazz, T value) {
            return new ClassParameter<>(clazz, value);
        }
    }
}
