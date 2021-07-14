package nhn.test.webserver.thread;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.config.ConfigManager;
import nhn.test.webserver.exception.ExpectedException;
import nhn.test.webserver.io.RequestReader;
import nhn.test.webserver.io.ResponseWriter;
import nhn.test.webserver.processor.RequestProcessor;
import nhn.test.webserver.tx.HttpTransaction;

/**
 * Thread 풀 관리와 각 Thread 기동-종료 라이프라이클을 관리한다.
 * callback 방식으로 각 Thread간의 처리를 연계함
 * 첫 Thread 작업은 accept 처리 작업을 수행하는 HttpServer class에서 최초로 시작한다.
 * 
 * 1. 정상 Case
 *   RequestReader -> RequestProcessor -> RequestWriter
 * 2. 오류 Case
 * 	 RequestReader -> RequestWriter
 * 
 * @author Master
 *
 */
public class ProcessHandler {
	private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class.getCanonicalName());
	 
	// TODO ThreadPool 별도 분리/관리 고려
	public static final ExecutorService reqReaderPool  = Executors.newFixedThreadPool(ConfigManager.getConfig().getRequestReaderThreadMaxCount());
	public static final ExecutorService reqProcessPool = Executors.newFixedThreadPool(ConfigManager.getConfig().getRequestProcessorThreadMaxCount());
	public static final ExecutorService resWriterPool  = Executors.newFixedThreadPool(ConfigManager.getConfig().getResponseWriterThreadMaxCount());
	
	/**
	 * RequestReader 실행후 처리할 Callback
	 */
	private ThreadCallback requestReaderCallback = new ThreadCallback() {
		@Override
		public void completed(HttpTransaction httpTransaction) {
			logger.info("Success requestReader thread run!");
			reqProcessPool.submit(new RequestProcessor(httpTransaction, requestProcessorCallback));
		}
		
		@Override
		public void failed(HttpTransaction httpTransaction, ExpectedException ex, int httpResultCode) {
			logger.error("An error occurred while executing the RequestReader process!");
			httpTransaction.getResponse().setStatus(httpResultCode);
			resWriterPool.submit(new ResponseWriter(httpTransaction, responseWriterCallback));
		}

		@Override
		public void failed(HttpTransaction httpTransaction, Throwable ex) {
			logger.error("An error occurred while executing the RequestProcessor process!", ex);
			httpTransaction.getResponse().setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
			resWriterPool.submit(new ResponseWriter(httpTransaction, responseWriterCallback));
		}
	};
	
	/**
	 * RequestProcessor 실행후 처리할 Callback
	 */
	private ThreadCallback requestProcessorCallback = new ThreadCallback() {
		@Override
		public void completed(HttpTransaction httpTransaction) {
			logger.info("Success requestProcessor thread run!");
			// RequestProcessor -> ResponseWrite 실행
			resWriterPool.submit(new ResponseWriter(httpTransaction, responseWriterCallback));
		}

		@Override
		public void failed(HttpTransaction httpTransaction, ExpectedException exc, int httpResultCode) {
			logger.error("An error occurred while executing the RequestProcessor process!");
			httpTransaction.getResponse().setStatus(httpResultCode);
			resWriterPool.submit(new ResponseWriter(httpTransaction, responseWriterCallback));
		}
		
		@Override
		public void failed(HttpTransaction httpTransaction, Throwable ex) {
			logger.error("An error occurred while executing the RequestProcessor process!", ex);
			httpTransaction.getResponse().setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
			resWriterPool.submit(new ResponseWriter(httpTransaction, responseWriterCallback));
		}
	};
	
	
	/**
	 * ResponseWriter 실행후 처리할 Callback
	 */
	private ThreadCallback responseWriterCallback = new ThreadCallback() {
		@Override
		public void completed(HttpTransaction httpTransaction) {
			logger.info("Success responseWriter thread run!");
		}
		
		@Override
		public void failed(HttpTransaction httpTransaction, ExpectedException exc, int httpResultCode) {
			logger.error("An error occurred while executing the ResponseWriter process!");
			// TODO. 결과 출력 오류시 어떻게 할지 생각해 볼것...
		}

		@Override
		public void failed(HttpTransaction httpTransaction, Throwable ex) {
			logger.error("An error occurred while executing the ResponseWriter process!", ex);
			// TODO. 결과 출력 오류시 어떻게 할지 생각해 볼것...
		}

		
	};
	
	public void startRequestProcess(HttpTransaction httpTransaction) {
		reqReaderPool.submit(new RequestReader(httpTransaction, requestReaderCallback));
	}
//	public void excuteRequestProcessor(HttpTransaction httpTransaction) {
//		reqProcessPool.submit(new RequestProcessor(httpTransaction));
//	}
//	public void excuteResponseWriter(HttpTransaction httpTransaction) {
//		resWriterPool.submit(new ResponseWriter(httpTransaction));
//	}

	public ThreadCallback getRequestReaderCallback() {
		return requestReaderCallback;
	}

//	public ThreadCallback getRequestProcessorCallback() {
//		return requestProcessorCallback;
//	}

//	public ThreadCallback getResponseWriterCallback() {
//		return responseWriterCallback;
//	}
	
	
	
}
