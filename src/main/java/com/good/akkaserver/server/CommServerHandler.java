package com.good.akkaserver.server;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.good.akkaserver.event.ClientEvent;
import com.good.akkaserver.message.SendMessage;
import com.good.akkaserver.model.PlayerSession;
import com.good.akkaserver.msg.PlayerActorCreationMsg;
import com.good.akkaserver.msg.PlayerActorStopMsg;

public class CommServerHandler extends SimpleChannelUpstreamHandler	{
	
	private static Logger logger = LoggerFactory.getLogger(CommServerHandler.class);
	
	private static AkkaServer akkaServer = AkkaServer.getInstance();
	private static ObjectPoolWorld objectPoolWorld = ObjectPoolWorld.getInstance();
	
	/** 用于生成  playerSessonID */
	private static final AtomicInteger PLAYER_SESSION = new AtomicInteger(1000);
	
	@Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) {
		System.out.println("get info from client");
		ChannelBuffer buf = (ChannelBuffer) e.getMessage();
		
		ClientEvent event = objectPoolWorld.getClientEvent(); 			// 可以使用对象池
		event.setTimestamp(System.currentTimeMillis());					// 收到时的时间
		
		byte[] gets = null;
		
		if (buf.hasArray()) {
			gets = buf.array();
		}
		
		boolean decoders = event.decode(gets);
		if (!decoders) {
			logger.error("--event.decode失败--");
		} else {
			
			if (event.getCommand() == ProtocolDefine.TIME_SYNC) { // 时间校验
				
				SendMessage message = new SendMessage();
				message.setCommand(ProtocolDefine.TIME_SYNC);
				long cc = event.getLong();
				message.putLong(cc);
				PlayerSession ps =( PlayerSession)ctx.getAttachment();
				message.putInt(ps.getId());
				Channel channel = ctx.getChannel();
				byte[] msgbyte = message.encode();
				ChannelBuffer cb = ChannelBuffers.buffer(msgbyte.length);
				cb.writeBytes(msgbyte);
				channel.write(cb);
				return;
			}
			
			
			PlayerSession session = (PlayerSession)ctx.getAttachment();
			event.setSessionID(session.getId());
			akkaServer.dispatchClientEvent(event);
		}
		
	}
	
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelOpen(ctx, e);
		CommServer.allChannels.add(e.getChannel());
	}
	
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
		PlayerSession session = new PlayerSession();
		int id = PLAYER_SESSION.getAndIncrement();
		session.setId(id);
		ctx.setAttachment(session);
		PlayerActorCreationMsg msg = new PlayerActorCreationMsg();
		msg.setSessionID(id);
		msg.setCtx(ctx);
		akkaServer.createPlayerActor(msg);
		
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelClosed(ctx,e);
		PlayerSession session = (PlayerSession)ctx.getAttachment();
		PlayerActorStopMsg msg = new PlayerActorStopMsg(session.getId());
		akkaServer.stopPlayerActor(msg);
		
	}

	@Override
    public void exceptionCaught (
            ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		logger.debug(e.toString());
		e.getChannel().close();
	}
	
}
