package com.winter.aop.advisor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class Advisor {

    private Annotation annotation;

    private Advice advice;

    private AspectJExpressionPointcut expressionPointcut;

    private Object aspectObject;

    public Advisor(Annotation annotation, Advice advice, Object aspectObject,String pointcut) {
        this.annotation = annotation;
        this.advice = advice;
        this.aspectObject = aspectObject;
        this.expressionPointcut = new AspectJExpressionPointcut(aspectObject.getClass(), pointcut);
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Advice getAdvice() {
        return advice;
    }

    public Object getAspectObject() {
        return aspectObject;
    }

    public boolean matches(Method method) {
        return expressionPointcut.matches(method);
    }

}
