package nhn.test.webserver.thread;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Junit 에서 테스트시 실제 통신 환경을 구축하여 테스트 하기에는 제약이 있어 MockSocket을 만들어 
 * 데이터를 ByteArrayInputStream을 통하여 쓰고/받을수 있도록 하였다.
 * 
 * @author Master
 *
 */
public class MockSocket extends Socket {
	
	byte[] input;
	
	public MockSocket(byte[] input) {
		this.input 	= input;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(this.input);
	}

	public OutputStream getOutputStream() {
		return new ByteArrayOutputStream();
	}
}