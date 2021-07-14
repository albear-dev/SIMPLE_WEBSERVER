package nhn.test.webserver.processor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.config.ConfigManager;
import nhn.test.webserver.exception.ProcessexExecutionException;
import nhn.test.webserver.exception.ResourceNotFoundException;
import nhn.test.webserver.exception.SecurityViolationException;
import nhn.test.webserver.exception.ServletNotFoundException;
import nhn.test.webserver.filter.SecurityFilter;
import nhn.test.webserver.thread.ThreadCallback;
import nhn.test.webserver.tx.HttpHeader;
import nhn.test.webserver.tx.HttpRequest;
import nhn.test.webserver.tx.HttpResponse;
import nhn.test.webserver.tx.HttpTransaction;
import nhn.test.webserver.util.FileUtils;

public class RequestProcessor implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class.getCanonicalName());

	private HttpTransaction _httpTransaction;
	private ThreadCallback callback;

	public RequestProcessor(HttpTransaction httpTransaction, ThreadCallback callback) {
		this._httpTransaction 	= httpTransaction;
		this.callback 			= callback;
	}

	@Override
	public void run() {
		logger.info("Start RequestProcessor! [{}]", Thread.currentThread().getName());
		HttpTransaction httpTransaction = _httpTransaction;
		HttpRequest httpRequest 		= httpTransaction.getRequest();
		HttpResponse httpResponse 		= httpTransaction.getResponse();
		HttpHeader header 				= httpRequest.getHttpHeader();
		
		try {
			/*******************
			 * 1. 처리 시작 전 Filter를 통해 사전 필터링 작업 진행
			 *******************/
			SecurityFilter filter = new SecurityFilter();
			filter.doFilter(httpRequest, httpResponse);
			
			/*******************
			 * 2. 리소스인지 아닌지 체크 후 리소스는 바로 출력을 위한 스트림을 가져오고 아니면 ServletService 호출
			 *******************/
			if(isResource(header.getUrl())) {
				logger.info("Get Resource.");
				// static resource 의 경우 메모리에 다 내용을 읽어서 다시 write 하지 않고 InputStream을 ResponseWriter에 전달하여 바로 쏠수 있도록 한다.
				httpResponse.setStaticResourceInputStream(FileUtils.getStaticResourceInputStream(header.getHost(), header.getUrl()));
			}else {
				logger.info("Call Servlet.");
				callServlet(header, httpRequest, httpResponse);
			}
			
			logger.info("RequestProcessor run complete!");
			callback.completed(httpTransaction);
		} catch (ResourceNotFoundException | ServletNotFoundException e) {
			//logger.error("Expected Exception! ", e);
			logger.info("Cannot find resource. [{}]", e.getMessage());
			callback.failed(httpTransaction, e, HttpURLConnection.HTTP_NOT_FOUND);
		} catch (SecurityViolationException e) {
			logger.error("You are not authorized for the request!", e);
			callback.failed(httpTransaction, e, HttpURLConnection.HTTP_FORBIDDEN);
		} catch (IOException e) {
			logger.error("UnExpected Exception! (File Io Error)", e);
			callback.failed(httpTransaction, e);
		} catch (Exception e) {
			logger.error("UnExpected Exception!", e);
			callback.failed(httpTransaction, e);
		}
	}
	
	/**
	 * 서블릿 동적 호출
	 * 
	 * @param header
	 * @param httpRequest
	 * @param httpResponse
	 * @throws ServletNotFoundException
	 * @throws ProcessexExecutionException 
	 */
	public void callServlet(HttpHeader header, HttpRequest httpRequest, HttpResponse httpResponse) throws ServletNotFoundException, ProcessexExecutionException {
		String url = header.getUrl();
		logger.info("Servlet call url [{}]", url);
		
		try {
			Class<?> clazz 		= Class.forName(url.substring(1));		// 맨 앞의 '/' 빼고 읽음
		    Constructor<?> cs 	= clazz.getConstructor(new Class[]{});
		    Object instance 	= cs.newInstance();
		    Method method 		= instance.getClass().getMethod("service", new Class[]{HttpRequest.class, HttpResponse.class});
		    method.invoke(instance, new Object[] {httpRequest, httpResponse});
		}catch(ClassNotFoundException e) {
			// 클래스 못찾는건 그 경로에 없다는 의미이므로 굳이 stacktrace는 남기지 않는다.
			throw new ServletNotFoundException(url);
		}catch(Exception e) {
			logger.error("Servlet excuete error!", e);
			throw new ProcessexExecutionException();
		}
	}
	
	/**
	 * 서블릿/리소스를 구분한다.
	 * 설정에 등록된 확장자/패턴이 아니면 서블릿으로 판단한다.
	 * @return
	 */
	public boolean isResource(String resourceURL) {
		logger.debug("Input:resourceURL [{}]", resourceURL);
		
		int extDotIndex = resourceURL.lastIndexOf('.');
		String ext = "";
		if(extDotIndex != -1) {
			ext = resourceURL.substring(extDotIndex+1, resourceURL.length());
			logger.debug("URL resource extension [{}]", ext);
		}else {
			return false;
		}
		
		return ConfigManager.getConfig().getResourcePattren().contains(ext);
	}
	

}
