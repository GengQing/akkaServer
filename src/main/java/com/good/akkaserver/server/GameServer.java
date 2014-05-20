package com.good.akkaserver.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description:游戏服务器的启动接口，业务层需要继承并且实现serverStart和serverStop的方法，
 * 用于游戏启动时的业务层需要完成的业务逻辑 游戏的启动方式是通过第一个参数注入具体的实现类
 */
public abstract class GameServer {

	private static Logger logger = LoggerFactory.getLogger(GameServer.class);

	private String serverIp = "10.6.6.288";
	private int serverPort = 11000;
	private long lineid = 0;

	private static AbstractApplicationContext ctx;


	/**
	 * Function name:init Description:初始化方法，用于初始化平台
	 * @return：成功返回0，其他值对应不同的错误
	 */
	public int init() {

		// socket启动
		CommServer socketServer = CommServer.getInstance();
		socketServer.startServer(serverIp, serverPort);
		return 0;
	}
	
	/**
	 * 系统启动运行的逻辑，比如加载一些东西
	 */
	public abstract void serverStart();

	public abstract void serverStop();

	public abstract void doSomething(String str);

	public static void main(String[] args) {
		
		ctx = new ClassPathXmlApplicationContext(
				"bean.xml");
		ctx.registerShutdownHook();
		
		GameServer gs = (GameServer) ctx.getBean("gameServer");
		gs.init();
		gs.serverStart();
		
		logger.info("服务器已经已经启动:" + new Date());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("输入命令:<start/stop/help(了解更多指令)>");
			try {
				String str = br.readLine().trim();
				if (str.equals("start")) {
					System.out.println("服务器正在运行");
				} else if (str.equals("stop")) {
					gs.serverStop();
					System.out.println("服务器已经安全关闭:" + (new Date()).toString());
					break;
				} else {	// 有业务处处理
					gs.doSomething(str);
				}
				Thread.sleep(1000);
			}  catch (Exception e) {
				e.printStackTrace();
				logger.error("执行命令失败:遇到致命错误");
			}
		}

		System.exit(0);
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}


	public long getLineid() {
		return lineid;
	}

	public void setLineid(long lineid) {
		this.lineid = lineid;
	}


}