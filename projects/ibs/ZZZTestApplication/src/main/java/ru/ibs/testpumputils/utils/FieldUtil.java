package ru.ibs.testpumputils.utils;

import java.lang.reflect.Field;

/**
 * @author NAnishhenko
 */
public class FieldUtil {

    public static void setField(Object target, Object object, String fieldName) {
        try {
            Field sessionFactoryField = target.getClass().getDeclaredField(fieldName);
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(target, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Object target, Class targetClass, Object object, String fieldName) {
        try {
            Field sessionFactoryField = targetClass.getDeclaredField(fieldName);
            sessionFactoryField.setAccessible(true);
            sessionFactoryField.set(target, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(Object target, String fieldName) {
        try {
            Field sessionFactoryField = target.getClass().getDeclaredField(fieldName);
            sessionFactoryField.setAccessible(true);
            return sessionFactoryField.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
