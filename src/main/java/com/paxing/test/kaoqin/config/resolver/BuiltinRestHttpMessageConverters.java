package com.paxing.test.kaoqin.config.resolver;


import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 内置的Rest
 * HttpMessageConverter集合，可以添加其他的HttpMessageConverter，如果添加的converter类名和默认的相同，则会覆盖默认的converter
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/20
 */
final class BuiltinRestHttpMessageConverters {

    private static final boolean jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder",
            BuiltinRestHttpMessageConverters.class.getClassLoader());

    private static final boolean jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper",
            BuiltinRestHttpMessageConverters.class.getClassLoader())
            && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
            BuiltinRestHttpMessageConverters.class.getClassLoader());

    private static final boolean jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XMLMapper",
            BuiltinRestHttpMessageConverters.class.getClassLoader());

    private static final boolean gsonPresent = ClassUtils.isPresent("com.google.gson.Gson",
            BuiltinRestHttpMessageConverters.class.getClassLoader());

    private List<HttpMessageConverter<?>> builtinMessageConverters = new ArrayList<>();

    public BuiltinRestHttpMessageConverters() {
        super();
        addRestMessageConverters(null);
    }

    public BuiltinRestHttpMessageConverters(List<HttpMessageConverter<?>> builtinMessageConverters) {
        super();
        addRestMessageConverters(builtinMessageConverters);
    }

    /**
     * 获取所有的支持rest 输出的HttpMessageConverter
     *
     * @return
     * @since 1.0.0
     */
    public List<HttpMessageConverter<?>> get() {
        return new ArrayList<>(this.builtinMessageConverters);
    }

    /**
     * 添加默认converter
     *
     * @since 1.0.0
     */
    private void addRestMessageConverters(List<HttpMessageConverter<?>> candidateConverters) {
        List<HttpMessageConverter<?>> defaultConverters = new ArrayList<>();
        if (jackson2XmlPresent) {
            defaultConverters.add(new MappingJackson2XmlHttpMessageConverter());
        } else if (jaxb2Present) {
            defaultConverters.add(new Jaxb2RootElementHttpMessageConverter());
        }
        if (jackson2Present) {
            defaultConverters.add(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            defaultConverters.add(new GsonHttpMessageConverter());
        }

        // 用户自定义的converter优先
        if (candidateConverters != null && !candidateConverters.isEmpty()) {
            this.builtinMessageConverters.addAll(candidateConverters);
        }
        // 最后添加默认的
        this.builtinMessageConverters.addAll(defaultConverters);
    }
}
