package io.github.emacsist.netty.httpdemo;

import io.github.emacsist.netty.httpdemo.config.AppInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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
    private AppInitializer appInitializer;

    public static void main(final String[] args) {
        SpringApplication.run(HttpDemoApplication.class, args);
    }

    @Override
    public void run(final String... args) {
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final EventLoopGroup master = new NioEventLoopGroup();
        final EventLoopGroup worker = new NioEventLoopGroup();
        try {
            serverBootstrap
                    .option(ChannelOption.SO_BACKLOG, 4 * KB)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .group(master, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(appInitializer);
            final Channel ch = serverBootstrap.bind(port).sync().channel();
            System.out.println("start app ok...");
            ch.closeFuture().sync();
        } catch (final InterruptedException e) {
            //ignore
        } finally {
            master.shutdownGracefully();
            worker.shutdownGracefully();
            System.out.println("stop app ok...");
        }
    }
}
