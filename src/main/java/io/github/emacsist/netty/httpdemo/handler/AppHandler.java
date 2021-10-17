package io.github.emacsist.netty.httpdemo.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author emacsist
 */
@Component
@ChannelHandler.Sharable
public class AppHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private static final Logger log = LoggerFactory.getLogger(AppHandler.class);

    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString TEXT_PLAIN = AsciiString.cached("text/plain; charset=utf-8");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest fullHttpRequest) {
        final String uri = fullHttpRequest.uri();
        final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        final String requestPath = queryStringDecoder.path();


        String body = "";
        switch (requestPath) {
            case "/netty":
                log.info("in netty => {}", queryStringDecoder.parameters());
                body = "Hello Netty.";
                break;
            case "/hello":
                body = "Hello world.";
                break;
            default:
                break;
        }

        final DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(body.getBytes()));
        defaultFullHttpResponse.headers().set(CONTENT_TYPE, TEXT_PLAIN);
        defaultFullHttpResponse.headers().set(CONTENT_LENGTH, defaultFullHttpResponse.content().readableBytes());
        ctx.write(defaultFullHttpResponse);
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}