package com.winter.aop.advisor;

import com.winter.aop.proxy.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public abstract class AbstractAspectJAdvice implements Advice {

    protected final Method aspectJAdviceMethod;

    protected final Object aspectJObject;

    private int joinPointArgumentIndex = -1;
    private int parameterSize = 0;

    public AbstractAspectJAdvice(Method aspectJAdviceMethod, Object aspectJObject) {
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.aspectJObject = aspectJObject;

        Parameter[] parameters = aspectJAdviceMethod.getParameters();
        if (parameters.length > 0) {
            parameterSize = parameters.length;
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getType().getName().equals(Joinpoint.class.getName())
                        || parameters[i].getType().getName().equals(ProceedingJoinPoint.class.getName())) {
                    joinPointArgumentIndex = i;
                }
            }
        }
    }

    /**
     * 统一调用通知方法
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    public Object invokeAdviceMethod(MethodInvocation invocation) throws Throwable {
        Object[] args;
        if (joinPointArgumentIndex == -1) {
            args = null;
        } else {
            args = new Object[parameterSize];
            args[joinPointArgumentIndex] = new MethodInvocationProceedingJoinPoint(invocation);
        }

        return aspectJAdviceMethod.invoke(aspectJObject, args);
    }

}
