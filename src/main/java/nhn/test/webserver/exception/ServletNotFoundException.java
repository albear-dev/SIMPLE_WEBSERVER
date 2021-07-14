package nhn.test.webserver.exception;

/**
 * 주소에 없는 서블릿을 호출하였을떄 발생하는 Exception
 * 
 * @author Master
 *
 */
public class ServletNotFoundException extends Exception implements ExpectedException{

	private static final long serialVersionUID = 5894699573398768866L;
	public ServletNotFoundException() {}
	public ServletNotFoundException(String message) {
		super(message);
	}
	public ServletNotFoundException(Exception e) {
		super(e);
	}
	
}
