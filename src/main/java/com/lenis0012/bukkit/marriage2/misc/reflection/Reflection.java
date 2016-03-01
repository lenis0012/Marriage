package com.lenis0012.bukkit.marriage2.misc.reflection;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

public class Reflection {
    private static final String NMS_VERSION;
    private static final String NMS_ROOT;
    private static final String CB_ROOT;

    static {
        String fullname = Bukkit.getServer().getClass().getName();
        NMS_VERSION = fullname.substring("org.bukkit.craftbukkit.".length()).split(Pattern.quote("."))[0];
        NMS_ROOT = "net.minecraft.server." + NMS_VERSION + ".";
        CB_ROOT = "org.bukkit.craftbukkit." + NMS_VERSION + ".";
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName(NMS_ROOT + name);
        } catch(ClassNotFoundException e) {
            return null;
        }
    }

    public static Class<?> getCBClass(String name) {
        try {
            return Class.forName(CB_ROOT + name);
        } catch(ClassNotFoundException e) {
            return null;
        }
    }

    public static Field getField(Class<?> host, String name) {
        try {
            Field field = host.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch(Exception e) {
            return null;
        }
    }

    public static Field getNMSField(String hostName, String fieldName) {
        return getField(getNMSClass(hostName), fieldName);
    }

    public static Field getCBField(String hostName, String fieldName) {
        return getField(getCBClass(hostName), fieldName);
    }

    public static void setFieldValue(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getFieldValue(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getFieldValue(Field field, Object instance, Class<T> type) {
        return type.cast(getFieldValue(field, instance));
    }

    public static Method getMethod(Class<?> host, String methodName, Class<?>... params) {
        try {
            Method method = host.getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            return method;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getNMSMethod(String hostName, String methodName, Class<?>... params) {
        return getMethod(getNMSClass(hostName), methodName, params);
    }

    public static Method getCBMethod(String hostName, String methodName, Class<?>... params) {
        return getMethod(getCBClass(hostName), methodName, params);
    }

    public static Object invokeMethod(Method method, Object instance, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T invokeMethod(Class<T> type, Method method, Object instance, Object... args) {
        return type.cast(invokeMethod(method, instance, args));
    }

    public static final class ClassReflection {
        private final Class<?> handle;
        private final Map<String, Field> fields = Maps.newConcurrentMap();
        private final Map<String, Method> methods = Maps.newConcurrentMap();

        public ClassReflection(Class<?> handle) {
            this.handle = handle;
            scanFields(handle);
            scanMethods(handle);
        }

        private void scanFields(Class<?> host) {
            if(host.getSuperclass() != null) {
                scanFields(host.getSuperclass());
            }

            for(Field field : host.getDeclaredFields()) {
                field.setAccessible(true);
                fields.put(field.getName(), field);
            }
        }

        private void scanMethods(Class<?> host) {
            if(host.getSuperclass() != null) {
                scanMethods(host.getSuperclass());
            }

            for(Method method : host.getDeclaredMethods()) {
                method.setAccessible(true);
                methods.put(method.getName(), method);
            }
        }

        public Object newInstance() {
            try {
                return handle.newInstance();
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public Field getField(String name) {
            return fields.get(name);
        }

        public void setFieldValue(String fieldName, Object instance, Object value) {
            Reflection.setFieldValue(getField(fieldName), instance, value);
        }

        public <T> T getFieldValue(String fieldName, Object instance, Class<T> type) {
            return Reflection.getFieldValue(getField(fieldName), instance, type);
        }

        public Method getMethod(String name) {
            return methods.get(name);
        }
    }
}
