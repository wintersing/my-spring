package com.winter.aop.advisor;

import org.aspectj.weaver.tools.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AspectJExpressionPointcut {

    private final String expression;

    private final Class<?> pointcutDeclarationScope;

    private final PointcutExpression pointcutExpression;

    private ShadowMatch shadowMatch;

    public AspectJExpressionPointcut(Class<?> pointcutDeclarationScope, String expression) {
        this.pointcutDeclarationScope = pointcutDeclarationScope;
        this.expression = expression;
        this.pointcutExpression = buildPointcutExpression();
    }

    public boolean matches(Method method) {
        ShadowMatch shadowMatch = getShadowMatch(method);
        if (shadowMatch.alwaysMatches()) {
            return true;
        }

        if (shadowMatch.neverMatches()) {
            return false;
        }

        return shadowMatch.maybeMatches();
    }


    private ShadowMatch getShadowMatch(Method targetMethod) {
        if (Objects.isNull(shadowMatch)) {
            shadowMatch = this.pointcutExpression.matchesMethodExecution(targetMethod);
        }
        return shadowMatch;
    }

    private PointcutExpression buildPointcutExpression() {
        PointcutParser parser = PointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                        SUPPORTED_PRIMITIVES, this.getClass().getClassLoader());

        /*PointcutParameter[] pointcutParameters = new PointcutParameter[this.pointcutParameterNames.length];
        for (int i = 0; i < pointcutParameters.length; i++) {
            pointcutParameters[i] = parser.createPointcutParameter(
                    this.pointcutParameterNames[i], this.pointcutParameterTypes[i]);
        }*/

        return parser.parsePointcutExpression(getExpression(),
                this.pointcutDeclarationScope, new PointcutParameter[0]);
    }

    public String getExpression() {
        return expression;
    }

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
    }

}
