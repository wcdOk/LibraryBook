package com.wcdok.comp_strengthen.tools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 7/22/21 2:40 PM
 * @desc:
 */
public class Utils {
    public static byte[] getBytes(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file,"r");
        byte[] buffer  = new byte[(int)randomAccessFile.length()];
        randomAccessFile.readFully(buffer);
        buffer.clone();
        return buffer;

    }

    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class clazz = instance.getClass();
        //反射获取
        while (clazz!=null){
            try {
                Field field = clazz.getDeclaredField(name);
                if(!field.isAccessible()){
                    field.setAccessible(true);
                }
                return field;

            } catch (NoSuchFieldException e) {
               clazz = clazz.getSuperclass();
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    public static Method findMethod(Object instance, String name, Class... parameterTypes) throws NoSuchMethodException {
        Class clazz = instance.getClass();
        while (clazz != null) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                //如果找不到往父类找
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + name + " with parameters " + Arrays.asList
                (parameterTypes) + " not found in " + instance.getClass());
    }
}
