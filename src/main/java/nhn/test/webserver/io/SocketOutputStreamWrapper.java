package nhn.test.webserver.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Socket OutputStream에 대한 Wrapping 객체.
 * > 해당 객체가 필요한 이유
 * 	 Static Resource 전송 같은 일반적인 경우 현 SimpleWAS 아키텍저 구조상 ResponseWriter 에서 헤더를 출력하고 컨텐츠를 출력하게 되나
 *   서블릿에서 직접 response.getOutputStream 을 통하여 소켓에 쓰게 되었을때 먼저 데이터를 쏜 내용이 있으므로
 *   ResponseWriter에서 자동으로 Header등을 생성하여 데이터를 쓰면 안된다.
 *   그래서 해당 Wrapper 클래스를 만들어 Servlet 등에서 직접 소켓에 데이터를 쓰게 되면 
 *   이미 해당 OutputStream에 쓴 적이 있다고 isWritten 의 상태를 변경해 놓고
 *   이후 ResponseWriter에서 소켓에 쓴 적이 있는지 체크하여 추가로 쓰기 작업을 수행하지 않도록 한다.
 * 
 * @author Master
 *
 */
public class SocketOutputStreamWrapper extends FilterOutputStream {
	private boolean isWritten = false;
	/**
	 * Mock에서 사용하기 위해 원본 OutputStream을 가져올수 있도록 함
	 */
	private OutputStream originalOutputStream;
	
	public SocketOutputStreamWrapper(OutputStream out) {
		super(out);
		originalOutputStream = out;
	}

	@Override
	public void write(byte[] arg0, int arg1, int arg2) throws IOException {
		super.write(arg0, arg1, arg2);
		this.isWritten = true;
	}

	@Override
	public void write(byte[] arg0) throws IOException {
		super.write(arg0);
		this.isWritten = true;
	}

	@Override
	public void write(int arg0) throws IOException {
		super.write(arg0);
		this.isWritten = true;
	}

	public boolean isWritten() {
		return isWritten;
	}

	public OutputStream getOriginalOutputStream() {
		return originalOutputStream;
	}
	
	

	
}
