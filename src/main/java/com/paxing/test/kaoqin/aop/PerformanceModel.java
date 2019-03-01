package com.paxing.test.kaoqin.aop;

import java.util.List;

/**
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/1
 */
public class PerformanceModel {
    private String clazz;

    private String methodName;

    private Long time;

    private List<PerformanceModel> childs;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<PerformanceModel> getChilds() {
        return childs;
    }

    public void setChilds(List<PerformanceModel> childs) {
        this.childs = childs;
    }
}
