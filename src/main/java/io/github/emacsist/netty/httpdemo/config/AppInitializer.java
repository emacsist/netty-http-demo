package io.github.emacsist.netty.httpdemo.config;

import io.github.emacsist.netty.httpdemo.handler.AppHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
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
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1 * MB));
        p.addLast(appHandler);
    }
}