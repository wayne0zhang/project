package com.paxing.test.kaoqin.utils;

import com.google.common.collect.Lists;
import jodd.bean.BeanCopy;
import jodd.bean.BeanUtilBean;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 使用 jodd Bean copy
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/29
 */
public class BeanUtil {

    /**
     * 复制对象
     *
     * @param source
     * @param destination
     */
    public static void copy(Object source, Object destination) {
        BeanCopy beanCopy = BeanCopy.beans(source, destination);
        beanCopy.copy();
    }

    /**
     * 复制对象
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.newInstance();
            copy(source, target);
            return target;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("copy对象时，新建示例对象失败，message：" + e.getMessage(), e);
        }
    }

    /**
     * bean to map
     *
     * @param bean
     * @return
     */
    public static HashMap beanToMap(Object bean) {
        HashMap result = new HashMap();
        copy(bean, result);
        return result;
    }

    /**
     * copy list
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> List<T> copyList(List<?> source, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(source.size());
        for (Object o : source) {
            T copy = copy(o, targetClass);
            result.add(copy);
        }
        return result;
    }

    /**
     * map to Bean
     *
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> source, T target) {
        Field[] fields = target.getClass().getDeclaredFields();
        List<Field> fieldList = Lists.newArrayList();
        fieldList.addAll(Arrays.asList(fields));
        Class<?> superClass = target.getClass().getSuperclass();
        while (superClass != null) {
            fieldList.addAll(Arrays.asList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }
        for (Field field : fieldList) {
            String name = field.getName();
            for (String key : source.keySet()) {
                if (key.equalsIgnoreCase(name)) {
                    BeanUtilBean.declaredForced.setProperty(target, name, source.get(key));
                }
            }
        }
        return target;
    }
}
