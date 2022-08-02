package com.winter.aop.proxy;

import com.winter.aop.advisor.Advice;
import com.winter.aop.advisor.Advisor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdkDynamicAopProxy implements InvocationHandler, AopProxy {

    private List<Advisor> advisors;
    private Object target;

    public JdkDynamicAopProxy(Object target, List<Advisor> advisors) {
        this.target = target;
        this.advisors = advisors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        List<Advice> chain = getInterceptorsAndDynamicInterceptionAdvice(method);

        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(this.target, method, args, chain);

        if (Objects.isNull(args)) {
            args = new Object[0];
        }
        return methodInvocation.proceed();
    }

    private List<Advice> getInterceptorsAndDynamicInterceptionAdvice(Method method) {
        List<Advice> chain = new ArrayList<>();
        for (Advisor advisor : this.advisors) {
            if (advisor.matches(method)) {
                chain.add(advisor.getAdvice());
            }
        }
        return chain;
    }


    @Override
    public Object getProxy() {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, this);
    }

}
