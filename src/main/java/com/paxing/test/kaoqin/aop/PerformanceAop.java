package com.paxing.test.kaoqin.aop;

import com.paxing.test.kaoqin.web.controller.TestController;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试方法执行时间的监控
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/3/1
 */
@Slf4j
@Aspect
@Component
public class PerformanceAop implements Ordered {

    /**
     * 存放需要监控的controller类
     */
    static List<String> clazzs = new ArrayList<>();

    static {
        clazzs.add(TestController.class.getName());
    }

    ThreadLocal stack = new ThreadLocal();
    Map<String, PerformanceModel> map = new HashMap<>();
    PerformanceModel last;

//    public Object doBasicProfiling1(ProceedingJoinPoint pjp) throws Throwable {
//        String declaringType = pjp.getSignature().getDeclaringType().getName();
//        String targetName = pjp.getTarget().getClass().getName();
//        String clazz = declaringType.endsWith("Mapper") ? declaringType : targetName;
//        String name = pjp.getSignature().getName();
//
//        if (clazzs.contains(clazz)) {
//            // 仅保存需要监控的入口
//            List<PerformanceModel> nodes = new ArrayList<>();
//            PerformanceModel root = new PerformanceModel();
//            root.setChilds(new ArrayList<>());
//            nodes.add(root);
//            stack.set(nodes);
//        }
//
//        List<PerformanceModel> nodes = (List<PerformanceModel>) stack.get();
//
//        PerformanceModel parentModel = null;
//        PerformanceModel current = null;
//        if (nodes != null) {
//            parentModel = nodes.get(nodes.size() - 1);
//            current = new PerformanceModel();
//            nodes.add(current);
//        }
//
//        Long start = System.currentTimeMillis();
//        try {
//            Object proceed = pjp.proceed();
//            return proceed;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        } finally {
//            Long end = System.currentTimeMillis();
//            if (current != null) {
//                current.setClazz(clazz);
//                current.setMethodName(name);
//                current.setTime(end - start);
//                parentModel.getChilds().add(current);
//                nodes.remove(current);
//            }
//
//            if (clazzs.contains(clazz)) {
//                stack.remove();
//                PerformanceModel parentNode = nodes.get(0);
//                parentNode.setTime(end - start);
//                parentNode.setMethodName(name);
//                parentNode.setClazz(clazz);
//                if (!CollectionUtils.isEmpty(parentNode.getChilds())) {
//                    printLog(parentNode.getChilds().get(0), 4, null);
//                }
//            }
//        }
//    }

    @Around(value = "execution(* com.paxing.test.kaoqin..*(..)) ")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        String clazzDeclare = pjp.getSignature().getDeclaringType().getName();
        String clazzTarget = pjp.getTarget().getClass().getName();
        String clazz = clazzDeclare.endsWith("Mapper") ? clazzDeclare : clazzTarget;
        String name = pjp.getSignature().getName();

        if (clazzs.contains(clazz)) {
            List<PerformanceModel> nodes = new ArrayList<>();
            PerformanceModel root = new PerformanceModel();
            nodes.add(root);
            stack.set(nodes);
        }

        List<PerformanceModel> nodes = (List) stack.get();

        PerformanceModel parentModel = null;
        PerformanceModel current = null;
        if (nodes != null) {
            parentModel = nodes.get(nodes.size() - 1);//最后一个为父节点
            current = new PerformanceModel();
            nodes.add(current);//入栈
        }

        //执行业务
        Long start = System.currentTimeMillis();
        try {
            Object object = pjp.proceed();//执行该方法
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            Long end = System.currentTimeMillis();
            //存储到父结点
            if (current != null) {
                current.setTime(end - start);
                current.setMethodName(name);
                current.setClazz(clazz);
                parentModel.getChilds().add(current);//父子关联
                nodes.remove(current);//出栈
            }

            if (clazzs.contains(clazz)) {
                stack.remove();

                PerformanceModel root = nodes.get(0);
                if (root != null && root.getChilds().size() > 0) {
                    root.setMethodName(name);
                    root.setClazz(clazz);
                    root.setTime(end - start);
                    printLog(root.getChilds().get(0), 4, null);
                }

                Object[] args = pjp.getArgs();
                if (args != null) {
                    for (Object arg : args) {
                        // 分析其他业务
                        if (arg != null && arg instanceof Number) {
                            Integer docTypeInt = ((Number) arg).intValue();
                            String docType = docTypeInt + "";
                            PerformanceModel old = map.get(docType);
                            if (old == null) {
                                map.put(docType, root);
                            } else {
                                Long oldTime = old.getTime();
                                Long newTime = root.getTime();
                                if (oldTime < newTime) {
                                    map.put(docType, root);
                                }
                            }
                        }
                    }
                }


                //保存最后一次，用于前端测试
                last = root;
            }
        }
    }


    private void printLog(PerformanceModel performanceModel, int sj, StringBuilder sb) {
        if (performanceModel == null) {
            return;
        }
        String rs = "";
        for (int i = 0; i < sj; i++) {
            if (sb != null) {
                rs += "&nbsp;";
            } else {
                rs += " ";
            }
        }
        rs += "class:" + performanceModel.getClazz() + "." + performanceModel.getMethodName() + "() 执行时间 : " + performanceModel.getTime() + "ms";
        if (sb != null) {
            sb.append("<br>");
            sb.append(rs);
        }
        log.info(rs);

        if (performanceModel.getChilds() != null && performanceModel.getChilds().size() > 0) {
            for (PerformanceModel performanceModel2 : performanceModel.getChilds()) {
                printLog(performanceModel2, sj + 8, sb);
            }
        }

    }

    public PerformanceModel getLast() {
        return last;
    }

    public Map<String, PerformanceModel> getAll() {
        return map;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
