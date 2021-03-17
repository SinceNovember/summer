package com.summer.core.springmvc.resolver;

import com.summer.annotation.springmvc.RequestParam;
import com.summer.common.util.ObjectUtil;
import com.summer.core.springmvc.entity.MethodDetail;

import java.lang.reflect.Parameter;

public class RequestParamParameterResolver implements ParameterResolver {
    @Override
    public Object resolve(MethodDetail methodDetail, Parameter parameter) {
        RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);
        String requestParameter = requestParam.value();
        String requestParameterValue = methodDetail.getQueryParameterMappings().get(requestParameter);
        if (requestParameterValue == null) {
            throw new IllegalArgumentException("The specified parameter " + requestParameter + " can not be null!");
        }
        // convert the parameter to the specified type
        return ObjectUtil.convert(parameter.getType(), requestParameterValue);

    }
}
