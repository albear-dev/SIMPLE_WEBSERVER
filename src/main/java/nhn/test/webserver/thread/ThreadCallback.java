package nhn.test.webserver.thread;

import nhn.test.webserver.exception.ExpectedException;
import nhn.test.webserver.tx.HttpTransaction;

public interface ThreadCallback {
	/**
	 * @param httpTransaction
	 * 
	 * 작업 성공 처리
	 */
	public void completed(HttpTransaction httpTransaction);
	
	/**
	 * @param httpTransaction
	 * @param exc
	 * @param httpResultCode
	 * 
	 * 이미 예상된 오류 처리.
	 */
	public void failed(HttpTransaction httpTransaction, ExpectedException exc, int httpResultCode);
	
	/**
	 * @param httpTransaction
	 * @param exc
	 * 
	 * 예상하지 못한 오류 처리
	 */
	public void failed(HttpTransaction httpTransaction, Throwable exc);
}
