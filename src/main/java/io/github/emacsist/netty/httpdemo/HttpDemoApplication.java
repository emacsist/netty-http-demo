package io.github.emacsist.netty.httpdemo;

import io.github.emacsist.netty.httpdemo.config.HttpServerInitlizer;
import io.github.emacsist.netty.httpdemo.config.LineBaseServerInitlizer;
import io.github.emacsist.netty.httpdemo.config.MqttServerInitlizer;
import io.github.emacsist.netty.httpdemo.config.WebSocketServerInitlizer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author emacsist
 */
@SpringBootApplication
public class HttpDemoApplication implements CommandLineRunner {
    @Value("${server.port}")
    private int port;
    private static final int KB = 1024;
    @Autowired
    private HttpServerInitlizer httpServerInitlizer;
    @Autowired
    private LineBaseServerInitlizer lineBaseServerInitlizer;
    @Autowired
    private WebSocketServerInitlizer webSocketServerInitlizer;
    @Autowired
    private MqttServerInitlizer mqttServerInitlizer;

    public static void main(final String[] args) {
        SpringApplication.run(HttpDemoApplication.class, args);
    }

    @Override
    public void run(final String... args) throws InterruptedException {
        final ServerBootstrap httpServer = new ServerBootstrap();
        final ServerBootstrap websocketServer = new ServerBootstrap();
        final ServerBootstrap lineBaseServer = new ServerBootstrap();
        final ServerBootstrap mqttServer = new ServerBootstrap();
        final EventLoopGroup master = new NioEventLoopGroup();
        final EventLoopGroup worker = new NioEventLoopGroup();
        ChannelFuture httpCf = null;
        ChannelFuture wsCf = null;
        ChannelFuture lineCf = null;
        ChannelFuture mqttCf = null;
        try {
            httpServer.option(ChannelOption.SO_BACKLOG, 4 * KB).option(ChannelOption.TCP_NODELAY, true).group(master, worker).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(httpServerInitlizer);
            websocketServer.option(ChannelOption.SO_BACKLOG, 4 * KB).option(ChannelOption.TCP_NODELAY, true).group(master, worker).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(webSocketServerInitlizer);
            lineBaseServer.option(ChannelOption.SO_BACKLOG, 4 * KB).option(ChannelOption.TCP_NODELAY, true).group(master, worker).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(lineBaseServerInitlizer);
            mqttServer.option(ChannelOption.SO_BACKLOG, 4 * KB).option(ChannelOption.TCP_NODELAY, true).group(master, worker).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(mqttServerInitlizer);
            httpCf = httpServer.bind(port).sync();
            System.out.println("http bind " + httpCf);
            wsCf = websocketServer.bind(port + 1).sync();
            System.out.println("ws bind " + wsCf);
            lineCf = lineBaseServer.bind(port + 2).sync();
            System.out.println("line bind " + lineCf);
            mqttCf = mqttServer.bind(port + 3).sync();
            System.out.println("mqtt bind " + mqttCf);
            //ignore
        } finally {
            if (httpCf != null) {
                httpCf.channel().closeFuture().sync();
            }
            if (wsCf != null) {
                wsCf.channel().closeFuture().sync();
            }
            if (lineCf != null) {
                lineCf.channel().closeFuture().sync();
            }
            if (mqttCf != null) {
                mqttCf.channel().closeFuture().sync();
            }
            master.shutdownGracefully();
            worker.shutdownGracefully();
            System.out.println("stop app ok...");
        }
    }
}
