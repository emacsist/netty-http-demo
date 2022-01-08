package io.github.emacsist.netty.httpdemo.config;

import io.github.emacsist.netty.httpdemo.handler.MqttHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author emacsist
 */
@Component
@ChannelHandler.Sharable
public class MqttServerInitlizer extends ChannelInitializer {
    @Autowired
    private MqttHandler appHandler;

    @Override
    protected void initChannel(final Channel channel) {
        final ChannelPipeline p = channel.pipeline();
        p.addLast(new MqttDecoder());
        p.addLast(MqttEncoder.INSTANCE);
        p.addLast(appHandler);
    }
}
