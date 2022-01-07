package io.github.emacsist.netty.httpdemo.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author emacsist
 */
@Component
@ChannelHandler.Sharable
public class LineBaseHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(LineBaseHandler.class);

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final String msg) {
        ctx.write("收到 .." + msg + "\n");
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
