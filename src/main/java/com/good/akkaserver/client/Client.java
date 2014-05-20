package com.good.akkaserver.client;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

import com.good.akkaserver.event.ClientEvent;
import com.good.akkaserver.message.SendMessage;
import com.good.akkaserver.room.ChatEvent;
import com.good.akkaserver.server.ProtocolDefine;

public class Client {

	public static void main(String[] args) throws InterruptedException {
		String host = "localhost";
		int port = 11000;

		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = pipeline();
				pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(
						10240, 0, 2, -2, 0));
				pipeline.addLast("handler", new ClientHandler());
				return pipeline;
			}
		});

	
		for (int i = 0; i < 1; i++) {
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(
					host, port));

			final List<SendMessage> sms = createms();

			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					for (int i = 0; i < sms.size(); i++) {
						Channel channel = future.getChannel();
						SendMessage msg = sms.get(i);
						byte[] bytes = msg.encode();
						ChannelBuffer cb = ChannelBuffers.buffer(bytes.length);
						cb.writeBytes(bytes);
						channel.write(cb);
					}
				}
			});

			future.getChannel().getCloseFuture().awaitUninterruptibly();
		}
		
		Thread.sleep(10000);
		bootstrap.releaseExternalResources(); // 服务端退出后，客户端会自动退出

	}

	private static List<SendMessage> createms() {

		List<SendMessage> sms = new ArrayList<SendMessage>();
		
		SendMessage timesync =new SendMessage();
		
		timesync.setCommand(ProtocolDefine.TIME_SYNC); // 网络对时
		long nt = System.currentTimeMillis();
		timesync.putLong(nt);
		sms.add(timesync);

		SendMessage reg = new SendMessage(); // 注册
		reg.setCommand(ProtocolDefine.ACCOUNT_REG);
		reg.putString("alick");
		reg.putString("123");

		// sms.add(reg);

		SendMessage login = new SendMessage();
		login.setCommand(ProtocolDefine.ACCOUNT_LOGIN);
		login.putString("alick");
		login.putString("123");
		sms.add(login);

		SendMessage role = new SendMessage(); // 创建
		role.setCommand(ProtocolDefine.CREATE_PLAYER_ROLE);
		role.putString("alick");
		sms.add(role);

		SendMessage chat = new SendMessage(); // 聊天
		chat.setCommand(ProtocolDefine.CHAT);
		chat.putString("I am chating");
		chat.putByte(ChatEvent.TO_AREAN);
		chat.putInt(1);
		sms.add(chat);
		
		SendMessage move = new SendMessage(); // 移动
		move.setCommand(ProtocolDefine.MOVE);
		move.putInt(1);
		move.putInt(2);
		move.putInt(3);
		sms.add(move);

		return sms;
	}

	public static class ClientHandler extends SimpleChannelUpstreamHandler {

		
		long netTimeModified = 0; 								// 网络传输时间
		
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			ChannelBuffer buf = (ChannelBuffer) e.getMessage();
			byte[] gets = new byte[0];
			if (buf.hasArray()) {
				gets = buf.array();
			}

			ClientEvent serverEvent = new ClientEvent();
			serverEvent.decode(gets);

			switch (serverEvent.getCommand()) {
			case ProtocolDefine.CREATE_PLAYER_ROLE:
				System.out.println(serverEvent.getCommand() + " : "
						+ serverEvent.getBoolean());
				break;
			case ProtocolDefine.CHAT:
				System.out.println(serverEvent.getCommand() + " : "
						+ serverEvent.getString());
				break;
			case ProtocolDefine.TIME_SYNC:
				long st = serverEvent.getLong();
				long nt = System.currentTimeMillis();
				netTimeModified = (nt - st)/2;
				System.out.println("网络传输时间: " + netTimeModified);
				break;
			case ProtocolDefine.MOVE:
				System.out.println(" I am moving now");
			
			default:
				break;
			}

			System.out
					.println("client get info from the server about creating role");
			// byte[] result = serverEvent.getBytes();
			// com.good.akkaserver.protolbuf.LoginProto.MsgInfo msg =
			// com.good.akkaserver.protolbuf.LoginProto.MsgInfo.parseFrom(result);

		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)

		throws Exception {

			super.exceptionCaught(ctx, e);

		}

		@Override
		public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
				throws Exception {
			super.channelClosed(ctx, e);
			System.out.println("远程主机关闭");
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			System.out.println("connect server");
		}

	}
}
