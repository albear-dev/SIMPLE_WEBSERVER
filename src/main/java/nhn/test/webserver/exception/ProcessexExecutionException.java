package nhn.test.webserver.exception;

/**
 * 프로세스 처리중 오류 발생시 사용하는 Exception
 * 
 * @author Master
 *
 */
public class ProcessexExecutionException extends Exception implements ExpectedException{
	private static final long serialVersionUID = -7138144544258822834L;
	
	public ProcessexExecutionException() {}
	public ProcessexExecutionException(String message) {
		super(message);
	}
	public ProcessexExecutionException(Exception e) {
		super(e);
	}
}
