package io.github.emacsist.netty.httpdemo.config;

import io.github.emacsist.netty.httpdemo.handler.AppHandler;
import io.github.emacsist.netty.httpdemo.handler.AppIdleHandler;
import io.github.emacsist.netty.httpdemo.handler.CustomLengthDecoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author emacsist
 */
@Component
@ChannelHandler.Sharable
public class AppInitializer extends ChannelInitializer {
    private static final int MB = 1024 * 1024;
    @Autowired
    private AppHandler appHandler;

    @Override
    protected void initChannel(final Channel channel) {
        final ChannelPipeline p = channel.pipeline();
        p.addLast(new IdleStateHandler(5, 0, 0));
        p.addLast(new AppIdleHandler());
        p.addLast(new CustomLengthDecoder(2048, 2, 4, 6, 0, true));
        p.addLast(new DelimiterBasedFrameDecoder(2048, false, Unpooled.wrappedBuffer(new byte[]{'\r', '\n'})));
        p.addLast(appHandler);
    }
}
