package nhn.test.webserver.exception;

/**
 * 주로 Filter 중에서 보안 관련 체크시 위배되는 예외에 대한 처리를 하는 Exception
 * 
 * @author Master
 *
 */
public class SecurityViolationException extends Exception implements ExpectedException{
	private static final long serialVersionUID = 7137254470972518024L;
	
	public SecurityViolationException() {}
	public SecurityViolationException(String message) {
		super(message);
	}
	public SecurityViolationException(Exception e) {
		super(e);
	}
}
