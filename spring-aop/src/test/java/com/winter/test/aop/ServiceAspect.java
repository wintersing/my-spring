package com.winter.test.aop;

import com.winter.aop.annotation.EnableAspectJAutoProxy;
import com.winter.ioc.annotation.Component;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
@Component
@EnableAspectJAutoProxy()
public class ServiceAspect {
    //定义切入点表达式
    @Pointcut("execution(* com.winter.test.aop.UserServiceImpl.*(..))")
    //使用一个返回值void、方法体为空的方法来命名切入点
    public void myPointCut() {
    }

    //    前置通知
    @Before("myPointCut()")
    public void myBefore() {
        System.out.println("myBefore..");
    }

    @AfterReturning("myPointCut()")
    public void myAfterReturning() {
        System.out.println("AfterReturning");
    }

    @Around("myPointCut()")
    public Object myAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕开始");
        Object proceed = joinPoint.proceed();
        System.out.println("环绕结束");
        return proceed;
    }

    @AfterThrowing(value = "myPointCut()")
    public void myAfterThrowing() {
        System.out.println("AfterThrowing");
    }

    @After("myPointCut()")
    public void myAfter() {
        System.out.println("After");
    }

}
