package nhn.test.webserver.thread;

import java.net.HttpURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.exception.ExpectedException;
import nhn.test.webserver.tx.HttpTransaction;

/**
 * 현 SimpelWAS 서버 구조상 각 RequestReader, RequestProcessor, ResponseWriter가 각자 쓰레드로 동작하면서 콜백으로 처리되나
 * JUnit 테스트에서는 한계가 있으므로 콜백 처리를 skip 하도록 하는 Mock 클래스를 구성함 
 * 
 * @author Master
 *
 */
public class MockThreadCallback implements ThreadCallback{
	private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class.getCanonicalName());
	
	@Override
	public void completed(HttpTransaction httpTransaction) {
		logger.info("Success requestProcessor thread run! (Mock)");
	}

	@Override
	public void failed(HttpTransaction httpTransaction, ExpectedException exc, int httpResultCode) {
		logger.error("An error occurred while executing the RequestProcessor process! (Mock)");
		httpTransaction.getResponse().setStatus(httpResultCode);
	}
	
	@Override
	public void failed(HttpTransaction httpTransaction, Throwable ex) {
		logger.error("An error occurred while executing the RequestProcessor process! (Mock)", ex);
		httpTransaction.getResponse().setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
	}
	

}
