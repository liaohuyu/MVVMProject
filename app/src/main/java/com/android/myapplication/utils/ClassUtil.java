package com.android.myapplication.utils;

import android.arch.lifecycle.AndroidViewModel;


import com.android.myapplication.viewmodel.menu.NoViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by jingbin on 2018/12/26.
 */

public class ClassUtil {

    //todo  有没有可能简化？
    /**
     * 获取泛型ViewModel的class对象
     */
    public static <T> Class<T> getViewModel(Object obj) {
        Class<?> currentClass = obj.getClass();
        Class<T> tClass = getGenericClass(currentClass);
        if (tClass == null || tClass == AndroidViewModel.class || tClass == NoViewModel.class) {
            return null;
        }
        return tClass;
    }

    private static <T> Class<T> getGenericClass(Class<?> klass) {
        Type type = klass.getGenericSuperclass();//获得超类
        if (type == null || !(type instanceof ParameterizedType)) return null;
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        for (Type t : types) {
            Class<T> tClass = (Class<T>) t;
            //1.class1是不是class2的类,或者是class2的父类，接口
            //2.Object是所有类的父类
            if (AndroidViewModel.class.isAssignableFrom(tClass)) {
                return tClass;
            }
        }
        return null;
    }
}
