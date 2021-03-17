package com.summer.core.springmvc.util;

import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;

public class UrlUtil {

    /**
     * 获取解密的路径
     * @param uri
     * @return
     */
    public static String getRequestPath(String uri) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        return queryDecoder.path();
    }
}
