package com.good.akkaserver.server;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

public class CommServerPipelineFactory implements ChannelPipelineFactory{
	
	private static OrderedMemoryAwareThreadPoolExecutor executor = new OrderedMemoryAwareThreadPoolExecutor(32, 0, 0);//32是同步处理线程数，0/0是每个channel的event上限   
	private static ExecutionHandler  executorHandler = new ExecutionHandler(executor); 
 

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = pipeline();
    	pipeline.addLast("executor", executorHandler);//handler的executor
    	pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(10240,0,2,-2,0));
    	pipeline.addLast("handler", new CommServerHandler());
        return pipeline;
	}

}
