package com.winter.aop.advice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class Advisor {

    private Annotation annotation;

    private Method advice;

    private Method pointCut;

    private Object aspectObject;

    public Advisor(Annotation annotation, Method advice, Method pointCut, Object aspectObject) {
        this.annotation = annotation;
        this.advice = advice;
        this.pointCut = pointCut;
        this.aspectObject = aspectObject;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Method getAdvice() {
        return advice;
    }

    public Method getPointCut() {
        return pointCut;
    }

    public void setPointCut(Method pointCut) {
        this.pointCut = pointCut;
    }

    public Object getAspectObject() {
        return aspectObject;
    }

}
