package com.winter.aop.proxy;

import com.winter.aop.advisor.Advice;
import com.winter.aop.advisor.Advisor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjenesisCglibAopProxy implements AopProxy, net.sf.cglib.proxy.MethodInterceptor {

    private List<Advisor> advisors;
    private Object target;

    public ObjenesisCglibAopProxy(Object target, List<Advisor> advisors) {
        this.advisors = advisors;
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        List<Advice> chain = getInterceptorsAndDynamicInterceptionAdvice(method);


        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(this.target, method, objects, chain);

        if (Objects.isNull(objects)) {
            objects = new Object[0];
        }
        return methodInvocation.proceed(objects);
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
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

}
