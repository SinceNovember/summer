package com.summer.server;

import com.summer.core.springmvc.factory.FullHttpResponseFactory;
import com.summer.core.springmvc.factory.RequestHandlerFactory;
import com.summer.core.springmvc.handler.RequestHandler;
import com.summer.core.springmvc.util.UrlUtil;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;

import java.util.StringJoiner;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String FAVICON_ICO = "/favicon.ico";
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        log.info("Handle http request:{}", fullHttpRequest);
        String uri = fullHttpRequest.uri();
        if (uri.equals(FAVICON_ICO)) {
            return;
        }
        RequestHandler requestHandler = RequestHandlerFactory.get(fullHttpRequest.method());
        FullHttpResponse fullHttpResponse;
        try{
            fullHttpResponse = requestHandler.handle(fullHttpRequest);
        } catch (Throwable e) {
            log.error("Caught an unexpected error.", e);
            String requestPath = UrlUtil.getRequestPath(fullHttpRequest.uri());
            fullHttpResponse = FullHttpResponseFactory.getErrorResponse(requestPath, e.toString(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        boolean keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
        if (!keepAlive) {
            ctx.write(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
        } else {
            fullHttpResponse.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(fullHttpResponse);
        }
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
