package com.good.akkaserver.server;

import static org.jboss.netty.channel.Channels.pipeline;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.jboss.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.CharsetUtil;

public class WebSocketServer {

	private final int port;

	public WebSocketServer(int port) {
		this.port = port;
	}

	public void run() {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new WebSocketServerPipelineFactory());

		bootstrap.bind(new InetSocketAddress(port));

		// 打印提示信息
		System.out.println("Web socket server started at port " + port + '.');
		System.out
				.println("Open your browser and navigate to http://localhost:"
						+ port + '/');

	}

	public static class WebSocketServerPipelineFactory implements
			ChannelPipelineFactory {

		@Override
		public ChannelPipeline getPipeline() throws Exception {
			ChannelPipeline pipeline = pipeline();
			pipeline.addLast("decoder", new HttpRequestDecoder());
			pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
			pipeline.addLast("encoder", new HttpResponseEncoder());
			pipeline.addLast("handler", new WebSocketServerHandler());
			return null;
		}
	}

	public static class WebSocketServerHandler extends
			SimpleChannelUpstreamHandler {
		private static final InternalLogger logger = InternalLoggerFactory
				.getInstance(WebSocketServerHandler.class);

		private static final String WEBSOCKET_PATH = "/websocket";

		private WebSocketServerHandshaker handshaker;

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			Object msg = e.getMessage();
			if (msg instanceof HttpRequest) {
				 handleHttpRequest(ctx, (HttpRequest) msg);  
			} else {
				handleWebSocketFrame(ctx, (WebSocketFrame) msg);  
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
				throws Exception {
			// 处理异常情况
			e.getCause().printStackTrace();
			e.getChannel().close();
		}

		private void handleHttpRequest(ChannelHandlerContext ctx,
				HttpRequest req) {
			if (req.getMethod() != HttpMethod.GET) {
				sendHttpResponse(ctx, req, new DefaultHttpResponse(
						HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
				return;
			}
			String  hstr = getWebSocketLocation(req);
			System.out.println(hstr);
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
					hstr, null, false);
			handshaker = wsFactory.newHandshaker(req);
			if (handshaker == null) {
				wsFactory.sendUnsupportedWebSocketVersionResponse(ctx
						.getChannel());
			} else {
				handshaker.handshake(ctx.getChannel(), req).addListener(
						WebSocketServerHandshaker.HANDSHAKE_LISTENER);
			}
		}

		private void handleWebSocketFrame(ChannelHandlerContext ctx,
				WebSocketFrame frame) {
			// Websocket 握手结束
			if (frame instanceof CloseWebSocketFrame) {
				handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
				return;
			} else if (frame instanceof PingWebSocketFrame) {
				ctx.getChannel().write(
						new PongWebSocketFrame(frame.getBinaryData()));
				return;
			} else if (!(frame instanceof TextWebSocketFrame)) {
				throw new UnsupportedOperationException(String.format(
						"%s frame types not supported", frame.getClass()
								.getName()));
			}

			// 处理接受到的数据（转成大写）并返回
			String request = ((TextWebSocketFrame) frame).getText();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Channel %s received %s", ctx
						.getChannel().getId(), request));
			}
			ctx.getChannel().write(
					new TextWebSocketFrame(request.toUpperCase()));
		}

		private static void sendHttpResponse(ChannelHandlerContext ctx,
				HttpRequest req, HttpResponse res) {
			// 返回 HTTP 错误页面
			if (res.getStatus().getCode() != 200) {
				res.setContent(ChannelBuffers.copiedBuffer(res.getStatus()
						.toString(), CharsetUtil.UTF_8));
				setContentLength(res, res.getContent().readableBytes());
			}

			// 发送返回信息并关闭连接
			ChannelFuture f = ctx.getChannel().write(res);
			if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		}

//		private static void setContentLength(HttpResponse res, int readableBytes) {
//			// TODO Auto-generated method stub
//
//		}

//		private static boolean isKeepAlive(HttpRequest req) {
//			// TODO Auto-generated method stub
//			return true;
//		}

		private static String getWebSocketLocation(HttpRequest req) {
			return "ws://" + req.getHeader(HttpHeaders.Names.HOST)
					+ WEBSOCKET_PATH;
		}

	}

	public static void main (String[] args) {
		WebSocketServer server = new WebSocketServer(8080);
		server.run();
	}
}
