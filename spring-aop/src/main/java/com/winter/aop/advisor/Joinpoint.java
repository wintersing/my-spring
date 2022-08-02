package com.winter.aop.advisor;

public interface Joinpoint {

    Object proceed() throws Throwable;

}
