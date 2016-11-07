package com.lecloud.skin.ui.utils;

import java.lang.reflect.Field;

import android.content.Context;

/**
 * 资源文件获取工具类
 * 
 * @author pys
 */
public class ReUtils {

    private static int getReId(Context context, String name, String defType) {

        return context.getResources().getIdentifier(name, defType, context.getPackageName());
    }

    public static int getLayoutId(Context context, String name) {
        return getReId(context, name, "layout");
    }

    public static int getDrawableId(Context context, String name) {
        return getReId(context, name, "drawable");
    }

    public static int getStringId(Context context, String name) {
        return getReId(context, name, "string");
    }

    public static int getStyleId(Context context, String name) {
        return getReId(context, name, "style");
    }

    public static int getColorId(Context context, String name) {
        return getReId(context, name, "color");
    }

    public static int getArrayId(Context context, String name) {
        return getReId(context, name, "array");
    }

    public static int getId(Context context, String name) {
        return getReId(context, name, "id");
    }

    public static int getDimen(Context context, String name) {
        return getReId(context, name, "dimen");
    }

    /**
     * 对于context.getResources().getIdentifier无法获取的数据,或者数组 资源反射值
     * 
     * @paramcontext
     * @param name
     * @param type
     * @return
     */
    private static Object getResourceId(Context context, String name, String type) {
        String className = context.getPackageName() + ".R";
        try {
            Class cls = Class.forName(className);
            for (Class childClass : cls.getClasses()) {
                String simple = childClass.getSimpleName();
                if (simple.equals(type)) {
                    for (Field field : childClass.getFields()) {
                        String fieldName = field.getName();
                        if (fieldName.equals(name)) {
                            System.out.println(fieldName);
                            return field.get(null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * context.getResources().getIdentifier无法获取到styleable的数据
     * 
     * @paramcontext
     * @param name
     * @return
     */
    public static int getStyleable(Context context, String name) {
        return ((Integer) getResourceId(context, name, "styleable")).intValue();
    }

    /**
     * 获取styleable的ID号数组
     * 
     * @paramcontext
     * @param name
     * @return
     */
    public static int[] getStyleableArray(Context context, String name) {
        return (int[]) getResourceId(context, name, "styleable");
    }

}