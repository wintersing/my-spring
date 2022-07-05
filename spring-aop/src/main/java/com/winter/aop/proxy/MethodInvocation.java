package com.winter.aop.proxy;

public interface MethodInvocation {

    Object proceed(Object[] args) throws Throwable;

}
