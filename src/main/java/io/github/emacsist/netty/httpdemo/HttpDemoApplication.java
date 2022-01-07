package io.github.emacsist.netty.httpdemo;

import io.github.emacsist.netty.httpdemo.config.HttpServerInitlizer;
import io.github.emacsist.netty.httpdemo.config.LineBaseServerInitlizer;
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

    public static void main(final String[] args) {
        SpringApplication.run(HttpDemoApplication.class, args);
    }

    @Override
    public void run(final String... args) throws InterruptedException {
        final ServerBootstrap httpServer = new ServerBootstrap();
        final ServerBootstrap websocketServer = new ServerBootstrap();
        final ServerBootstrap lineBaseServer = new ServerBootstrap();
        final EventLoopGroup master = new NioEventLoopGroup();
        final EventLoopGroup worker = new NioEventLoopGroup();
        ChannelFuture ch = null;
        ChannelFuture ch1 = null;
        ChannelFuture ch2 = null;
        try {
            httpServer.option(ChannelOption.SO_BACKLOG, 4 * KB).option(ChannelOption.TCP_NODELAY, true).group(master, worker).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(httpServerInitlizer);
            websocketServer.option(ChannelOption.SO_BACKLOG, 4 * KB).option(ChannelOption.TCP_NODELAY, true).group(master, worker).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(webSocketServerInitlizer);
            lineBaseServer.option(ChannelOption.SO_BACKLOG, 4 * KB).option(ChannelOption.TCP_NODELAY, true).group(master, worker).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(lineBaseServerInitlizer);
            ch = httpServer.bind(port).sync();
            System.out.println("http bind " + ch);
            ch1 = websocketServer.bind(port + 1).sync();
            System.out.println("ws bind " + ch1);
            ch2 = lineBaseServer.bind(port + 2).sync();
            System.out.println("line bind " + ch2);
            //ignore
        } finally {
            if (ch != null) {
                ch.channel().closeFuture().sync();
            }
            if (ch1 != null) {
                ch1.channel().closeFuture().sync();
            }
            if (ch2 != null) {
                ch2.channel().closeFuture().sync();
            }
            master.shutdownGracefully();
            worker.shutdownGracefully();
            System.out.println("stop app ok...");
        }
    }
}
