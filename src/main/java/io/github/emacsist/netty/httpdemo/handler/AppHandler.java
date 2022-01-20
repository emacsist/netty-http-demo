package io.github.emacsist.netty.httpdemo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author emacsist
 */
@Component
@ChannelHandler.Sharable
public class AppHandler extends SimpleChannelInboundHandler<ByteBuf> {


    private static final Logger log = LoggerFactory.getLogger(AppHandler.class);


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final ByteBuf msg) {
        System.out.println("接收到 ");
        System.out.println(ByteBufUtil.prettyHexDump(msg));
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
