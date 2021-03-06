package com.summer.core.springmvc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodDetail {
    // target method
    private Method method;
    // url parameter mapping
    private Map<String, String> urlParameterMappings;
    // url query parameter mapping
    private Map<String, String> queryParameterMappings;
    // json type http post request data
    private String json;

    public void build(String requestPath, Map<String, Method> requestMappings, Map<String, String> urlMappings){
        requestMappings.forEach((key, value) ->{
            Pattern pattern = Pattern.compile(key);
            boolean b = pattern.matcher(requestPath).find();
            if (b){
                this.setMethod(value);
                String url = urlMappings.get(key);
                Map<String, String> urlParameterMappings = getUrlParameterMappings(requestPath, url);
                this.setUrlParameterMappings(urlParameterMappings);
            }
        });
    }

    /**
     *获取url路径中的匹配的参数
     * <p>
     * eg: requestPath="/user/1" url="/user/{id}"
     * this method will return:{"id" -> "1","user" -> "user"}
     * </p>
     */
    private Map<String, String> getUrlParameterMappings(String requestPath, String url) {
        String[] requestParams = requestPath.split("/");
        String[] urlParams = url.split("/");
        Map<String, String> urlParameterMappings = new HashMap<>();
        for (int i = 1; i < urlParams.length; i++) {
            urlParameterMappings.put(urlParams[i].replace("{", "").replace("}", ""), requestParams[i]);
        }
        return urlParameterMappings;
    }

}
