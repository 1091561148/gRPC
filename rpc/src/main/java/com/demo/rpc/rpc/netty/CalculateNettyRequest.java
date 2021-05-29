package com.demo.rpc.rpc.netty;

import com.demo.rpc.rpc.CalculateRequest;
import com.demo.rpc.rpc.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/27/18:31
 * @Description:
 */
public class CalculateNettyRequest implements CalculateRequest {
    private final static Logger logger = LoggerFactory.getLogger(CalculateNettyRequest.class);

    private final static int PORT = 8080;
    private final static String HOST = "127.0.0.1";

    /**
     * @param
     * @throws Exception
     */
    @Override
    public Object rpcInvoke(InvokerProtocol rpc) {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        final RpcProxyHandler consumerHandler = new RpcProxyHandler();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            //自定义协议解码器
                            //入参，框架最大长度，长度偏移，长度，补偿值，去除第一个字节数
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            //自定义编码器
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            //对象参数类型解码器
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            //对象参数类型编码器
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("handler", consumerHandler);
                        }
                    });

            // 发起异步连接操作
            ChannelFuture future = b.connect(HOST, PORT).sync();
            future.channel().writeAndFlush(rpc).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return consumerHandler.getResponse();
    }

    @Override
    public void rpcResponse() {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //自定义协议解码器
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            //自定义编码器
                            pipeline.addLast(new LengthFieldPrepender(4));
                            //对象参数类型解码器
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            //对象参数类型编码器
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("handler", new RegistryHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //异步连接
            ChannelFuture future = b.bind(PORT).sync();
            logger.info("RPC start listen at:" + PORT);
            future.channel().closeFuture().sync();
       }catch (Exception e){
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
