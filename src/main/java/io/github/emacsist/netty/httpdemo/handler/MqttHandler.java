package io.github.emacsist.netty.httpdemo.handler;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageIdAndPropertiesVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author emacsist
 */
@Component
@ChannelHandler.Sharable
public class MqttHandler extends SimpleChannelInboundHandler<MqttMessage> {
    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final Logger log = LoggerFactory.getLogger(MqttHandler.class);
    private static final AtomicInteger id = new AtomicInteger(1);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端连上了 " + ctx.channel());
        channels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final MqttMessage mqttMessage) {
        System.out.println("收到 mqtt " + mqttMessage);
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNECT:
                final MqttMessage ackMsg = MqttMessageBuilders.connAck().returnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED).build();
                ctx.write(ackMsg);
                System.out.println("connect Ack...");
                break;
            case SUBSCRIBE:
                MqttMessageIdAndPropertiesVariableHeader varHeader = (MqttMessageIdAndPropertiesVariableHeader) mqttMessage.variableHeader();
                MqttSubscribePayload subscribePayload = (MqttSubscribePayload) mqttMessage.payload();
                final List<MqttQoS> qosList = subscribePayload.topicSubscriptions().stream().map(e -> MqttQoS.AT_LEAST_ONCE).collect(Collectors.toList());
                final MqttSubAckMessage subAck = MqttMessageBuilders
                        .subAck()
                        .packetId(varHeader.messageId())
                        .addGrantedQoses(qosList.toArray(new MqttQoS[0]))
                        .build();
                ctx.writeAndFlush(subAck);
                System.out.println("sub ack...");
                break;
            case UNSUBSCRIBE:
                final MqttUnsubscribeMessage unsubMqttMessage = (MqttUnsubscribeMessage) mqttMessage;
                final int unsubId = unsubMqttMessage.variableHeader().messageId();
                final List<Short> reasons = unsubMqttMessage.payload().topics().stream().map(e -> (short) 0).collect(Collectors.toList());
                final MqttUnsubAckMessage unsubAck = MqttMessageBuilders.unsubAck().addReasonCodes(reasons.toArray(new Short[0])).packetId(unsubId).build();
                ctx.writeAndFlush(unsubAck);
                break;
            case PUBLISH:
                final MqttPublishMessage pubMqttMessage = (MqttPublishMessage) mqttMessage;
                MqttPublishVariableHeader varHeader1 = (MqttPublishVariableHeader) mqttMessage.variableHeader();
                final MqttMessage pubAck = MqttMessageBuilders.pubAck()
                        .packetId(varHeader1.packetId() < 0 ? 1 : varHeader1.packetId())
                        .build();
                ctx.writeAndFlush(pubAck);
                final String pubTopicName = pubMqttMessage.variableHeader().topicName();
                System.out.println("收到发布的内容: " + pubMqttMessage.payload().toString(StandardCharsets.UTF_8));
                final MqttPublishMessage serverPubMsg = MqttMessageBuilders.publish().payload(pubMqttMessage.payload().retain()).qos(MqttQoS.AT_MOST_ONCE).topicName(pubTopicName).build();
                channels.writeAndFlush(serverPubMsg);
                System.out.println("publish Ack...");
                break;
            case PINGREQ:
                MqttFixedHeader pingRespHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
                MqttMessage pingResp = new MqttMessage(pingRespHeader, null);
                ctx.writeAndFlush(pingResp);
                System.out.println("心跳OK...");
                break;
            default:
                break;
        }
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
