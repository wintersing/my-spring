package com.winter.test.aop;

import com.winter.aop.annotation.EnableAspectJAutoProxy;
import com.winter.ioc.annotation.Component;
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
        System.out.print("AfterReturning");
    }

    @Around("myPointCut()")
    public Object myAround() throws Throwable {
        System.out.println("环绕开始");

        System.out.println("环绕结束");
        return null;
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
