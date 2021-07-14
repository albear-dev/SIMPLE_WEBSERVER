package nhn.test.webserver;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.config.Config;
import nhn.test.webserver.config.ConfigManager;
import nhn.test.webserver.thread.ProcessHandler;
import nhn.test.webserver.tx.HttpTransaction;

public class HttpServer {
	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class.getCanonicalName());
	private boolean isStop = false;
	
	public static void main(String[] args) throws Exception{
		HttpServer webserver = new HttpServer();
		webserver.start();
	}

	public void start() {
		logger.info("Starting server.....");
		
		Config config 					= ConfigManager.getConfig();
		
		// 서버 소켓 오픈
		try (ServerSocket serverSocket = new ServerSocket(config.getListenPort())) {
			logger.info("Accepting connections on port [{}]", serverSocket.getLocalPort());
			
			// 서버 종료 전까지 요청을 기다린다.
			// 처리 주기를 관리하는 ProcessHandler로 새로운 HttpTransaction객체를 생성하여 전달한
			while (!isStop) {
				try {
					new ProcessHandler().startRequestProcess(new HttpTransaction(config, serverSocket.accept()));
				} catch (IOException ex) {
					logger.warn("Error accepting connection", ex);
				}
			}
			
			logger.info("Server is stooping.....");
		}catch(Exception e) {
			logger.error("Server running error!", e);
		}
	}
	
	public void stop() throws Exception {
		logger.info("Stoping server.....");
		this.isStop = true;
	}
}
