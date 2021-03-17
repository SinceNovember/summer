package com.summer.core.springmvc.factory;

import com.summer.annotation.springmvc.PathVariable;
import com.summer.annotation.springmvc.RequestBody;
import com.summer.annotation.springmvc.RequestParam;
import com.summer.core.springmvc.resolver.ParameterResolver;
import com.summer.core.springmvc.resolver.PathVariableParameterResolver;
import com.summer.core.springmvc.resolver.RequestBodyParameterResolver;
import com.summer.core.springmvc.resolver.RequestParamParameterResolver;

import java.lang.reflect.Parameter;

public class ParameterResolverFactory {
    public static ParameterResolver get(Parameter parameter) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            return new RequestParamParameterResolver();
        }
        if (parameter.isAnnotationPresent(PathVariable.class)) {
            return new PathVariableParameterResolver();
        }
        if (parameter.isAnnotationPresent(RequestBody.class)) {
            return new RequestBodyParameterResolver();
        }
        return null;
    }
}
