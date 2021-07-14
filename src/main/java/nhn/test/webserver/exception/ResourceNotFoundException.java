package nhn.test.webserver.exception;

/**
 * Static Resource를 찾기 못하였을때 발생하는 Exception
 * 
 * @author Master
 *
 */
public class ResourceNotFoundException extends Exception implements ExpectedException{
	private static final long serialVersionUID = -2476913594392861022L;
	
	public ResourceNotFoundException() {}
	public ResourceNotFoundException(String message) {
		super(message);
	}
	public ResourceNotFoundException(Exception e) {
		super(e);
	}
	
}
