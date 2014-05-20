package com.good.akkaserver.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CommServer {

	private static Logger logger = LoggerFactory.getLogger(CommServer.class);

	private static CommServer server = null;
	
	static final ChannelGroup allChannels = new DefaultChannelGroup("time-server");


	private CommServer() {
	}

	public static synchronized CommServer getInstance() {
		if (server == null) {
			server = new CommServer();
		}
		return server;
	}
	
	private  ServerBootstrap bootstrap;

	public int startServer(String serverIp, int serverPort) {
		
		// Configure the server.
        bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        CommServerPipelineFactory factory = new CommServerPipelineFactory();//使用了特别工厂类
        
        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(factory);
        
    	bootstrap.setOption("child.receiveBufferSize", 8*1024);
        // 设置相关参数
		bootstrap.setOption("child.tcpNoDelay", true);
		// 设置相关参数
		bootstrap.setOption("child.keepAlive", true);


        // Bind and start to accept incoming connections.
		Channel channel = bootstrap.bind(new InetSocketAddress(serverPort));
		allChannels.add(channel);

		logger.info("服务器Socket监听启动，端口为: " + serverPort);
		
		
		return 0;
	}

	// 关闭通信服务
	public boolean stopServer() {
		
		ChannelGroupFuture future = allChannels.close();
		future.awaitUninterruptibly();
		bootstrap.releaseExternalResources();
		return true;
	}
	
	public static void main(String[] args) throws Exception {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new CommServerHandler());
            }
        });

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(10000));
    }

}
