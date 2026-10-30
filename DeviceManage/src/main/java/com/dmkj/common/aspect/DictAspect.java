package com.dmkj.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

public class DictAspect {

    //生效范围
    @Pointcut("execution(public * com.dmkj.system..*.*Controller.*(..)) || @annotation(com.dmkj.common.aspect.annotation.Dict)")
    public void logPointCut() {

    }

    //执行方法
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long time1=System.currentTimeMillis();
        Object result = point.proceed();
        return result;
    }
}
