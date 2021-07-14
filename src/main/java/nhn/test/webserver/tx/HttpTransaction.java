package nhn.test.webserver.tx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import nhn.test.webserver.config.Config;
import nhn.test.webserver.io.SocketOutputStreamWrapper;

public class HttpTransaction {
	/** 설정 파일 */
	private Config config;
	/** 요청 Wrapping 객체 */
	private HttpRequest request;
	/** 응답 Wrapping 객체 */
	private HttpResponse response;
	/** Socket accept socket connection 객체*/
	private Socket connection;
	/** socket connection 에 대한 InputStream*/
	private InputStream inputStream;
	/** socket connection 에 대한 OutputStream*/
	private OutputStream outputStream;
	
	/**
	 * Http 요청/응답관련 거래 정보를 담아두는 트랜잭션 객체
	 * 
	 * @param config
	 * @param connection
	 * @throws IOException
	 */
	public HttpTransaction(Config config, Socket connection) throws IOException {
		this.config 		= config;
		this.connection 	= connection;
		this.request 		= new HttpRequest();
		this.request.setInputStream(connection.getInputStream());
		this.inputStream 	= connection.getInputStream();
		this.response 		= new HttpResponse();
		this.response.setOutputStream(new SocketOutputStreamWrapper(connection.getOutputStream()));
		this.outputStream 	= connection.getOutputStream();
	}
	
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	public HttpRequest getRequest() {
		return request;
	}
	public void setRequest(HttpRequest request) {
		this.request = request;
	}
	public HttpResponse getResponse() {
		return response;
	}
	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	public Socket getConnection() {
		return connection;
	}
	public void setConnection(Socket connection) {
		this.connection = connection;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public OutputStream getOutputStream() {
		return outputStream;
	}
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}	
	
}
