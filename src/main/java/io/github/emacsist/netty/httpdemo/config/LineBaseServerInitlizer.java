package io.github.emacsist.netty.httpdemo.config;

import io.github.emacsist.netty.httpdemo.handler.AppHandler;
import io.github.emacsist.netty.httpdemo.handler.LineBaseHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author emacsist
 */
@Component
@ChannelHandler.Sharable
public class LineBaseServerInitlizer extends ChannelInitializer {
    private static final int MB = 1024 * 1024;
    @Autowired
    private LineBaseHandler appHandler;

    @Override
    protected void initChannel(final Channel channel) {
        final ChannelPipeline p = channel.pipeline();
        p.addLast(new LineBasedFrameDecoder(1024));
        p.addLast(new StringDecoder());
        p.addLast(new StringEncoder());
        p.addLast(appHandler);
    }
}
