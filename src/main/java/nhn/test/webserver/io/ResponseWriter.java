package nhn.test.webserver.io;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.config.ConfigManager;
import nhn.test.webserver.exception.ResourceNotFoundException;
import nhn.test.webserver.thread.ThreadCallback;
import nhn.test.webserver.tx.HttpHeader;
import nhn.test.webserver.tx.HttpResponse;
import nhn.test.webserver.tx.HttpTransaction;
import nhn.test.webserver.util.FileUtils;

public class ResponseWriter implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ResponseWriter.class.getCanonicalName());

	private HttpTransaction _httpTransaction;
	private ThreadCallback callback;

	public ResponseWriter(HttpTransaction httpTransaction, ThreadCallback callback) {
		this._httpTransaction	= httpTransaction;
		this.callback 			= callback;
	}

	@Override
	public void run() {
		logger.info("Start ResponseWriter! [{}]", Thread.currentThread().getName());
		HttpTransaction httpTransaction = _httpTransaction;
		HttpResponse httpResponse 		= httpTransaction.getResponse();
		HttpHeader httpHeader 			= httpTransaction.getRequest().getHttpHeader();
		
		try {
			
			// Servlet 등에서 이미 write 작업이 수행되었다면 직접 출력하는것으로 판단하고 헤더와 데이터를 자동으로 쓰지 않는다.
			boolean isWritten = false;
			if(httpResponse.getOutputStream() instanceof SocketOutputStreamWrapper) {
				SocketOutputStreamWrapper sosw = (SocketOutputStreamWrapper)httpResponse.getOutputStream();
				if(isWritten = sosw.isWritten()) {
					logger.warn("Write is already in progress from servlet. Ignore the print job.");
				}
			}
			
			if(!isWritten) {
				/*******************
				 * 2. 출력할 데이터를 가져온다.
				 *******************/
				byte[] responsePayloadContent = getResponsePayloadContent(httpResponse);
				if (responsePayloadContent == null || responsePayloadContent.length == 0) {
					// 쓸내용이 없을때. 상태코드를 확인하고 정해진 에러 페이지를 출력한다.
					// 에러코드가 있더라도 Payload는 출력할 수 있으므로 주의.
					if (httpResponse.getStatus() != HttpURLConnection.HTTP_OK) {
						responsePayloadContent = getErrorPageContents(httpResponse, httpHeader.getHost());
					}
				}
				
				/*******************
				 * 1. header 전송
				 *******************/
				sendHeader(	 httpResponse.getOutputStream()
							,httpResponse.getResponseStatusText()
							,httpResponse.getContentType()
							,(responsePayloadContent!=null&&responsePayloadContent.length>0)?responsePayloadContent.length:0
				);
				
				
				/*******************
				 * 3. 결과 데이터를 출력한다.
				 *******************/
				writeResponsePayloadContent(httpResponse, responsePayloadContent);

			}
			
			callback.completed(httpTransaction);
		} catch (Exception e) {
			logger.error("UnExpected Exception!", e);
			callback.failed(httpTransaction, e);
		} finally {
			doConnectionClose(httpTransaction);
		}
	}
	
	/**
	 * @param httpResponse
	 * @param host
	 * @return
	 * @throws IOException
	 * 
	 * 지정된 에러페이지(html) 의 내용을 읽는다.
	 */
	private byte[] getErrorPageContents(HttpResponse httpResponse, String host) throws IOException {
		byte[] responsePayloadContent = null;
		
		// 에러페이지 위치를 가져온다.
		String errorPagePath = ConfigManager.getConfig().getErrorPagePath(httpResponse.getStatus());
		try {
			InputStream is 					= FileUtils.getStaticResourceInputStream(host, errorPagePath);
			ByteArrayOutputStream arrayBuff = FileUtils.readStaticResource(is);
			responsePayloadContent 			= arrayBuff.toByteArray();
			arrayBuff.reset();
		} catch (ResourceNotFoundException e) {
			logger.error("Cannot find html file [{}]", errorPagePath, e);
		}
		
		return responsePayloadContent;
	}
	
	/**
	 * @param httpTransaction
	 * 
	 * Socket connetion 객체를 close하고 정리한다.
	 * TODO connection pool 을 관리하면서 타임아웃 처리 기능 구현
	 */
	private void doConnectionClose(HttpTransaction httpTransaction) {
		Socket connection = httpTransaction.getConnection();
		// TODO KEEPALIVE 구현 고려..
		// TODO socket close 프로세스 정리
		if (!connection.isClosed()) {
			try {
				connection.close();
				logger.debug("socket is closed? [{}]", connection.isClosed());
				httpTransaction.getInputStream().close();
				httpTransaction.getOutputStream().close();
			} catch (IOException e) {
				logger.error("연결을 종료하던 중 오류가 발생하였습니다.", e);
			}
		}
	}

	/**
	 * @param httpResponse
	 * @param responsePayloadContent
	 * @throws IOException
	 * 
	 * 응답 데이터를 OutputStream에 출력한다.
	 */
	private void writeResponsePayloadContent(HttpResponse httpResponse, byte[] responsePayloadContent)
			throws IOException {
		if (responsePayloadContent != null && responsePayloadContent.length > 0) {
			logger.trace("payload Content >> {}", new String(responsePayloadContent));
			OutputStream raw = new BufferedOutputStream(httpResponse.getOutputStream());
			raw.write(responsePayloadContent);
			raw.flush();
		} else {
			logger.info("There is nothing to write.");
		}

	}

	/**
	 * @return
	 * @throws IOException
	 * 
	 * 이전 Process에서 설정한 응답 데이터를 확인하여 가져온다.
	 */
	private byte[] getResponsePayloadContent(HttpResponse httpResponse) throws IOException {
		ByteArrayOutputStream arrayBuff;
		byte[] payloadContent = null;

		if (httpResponse.isStaticResource()) {
			// httpTransaction에 ResourceInputStream 이 있으면 해당 InputStream에서 OutputStream 으로 바로 쓴다.
			arrayBuff	 	= FileUtils.readStaticResource(httpResponse.getStaticResourceInputStream());
			payloadContent 	= arrayBuff.toByteArray();

		} else if (httpResponse.hasBinaryContents()) {
			// httpResponse의 binaryContent를 확인하여 쓴다.
			payloadContent = httpResponse.getBinaryContents();
		} else {
			// 이도 저도 없으면 쓸 내용이 없다는 로그를 기록하고 그냥 종료(연결종료 포함)한다.
			// Servlet 의 Writer 에서 직접 썼을수도 있음..
			logger.info("There is no more data to print.");
		}

		return payloadContent;
	}

	/**
	 * @param out
	 * @param responseCodeAndMesage
	 * @param contentType
	 * @param length
	 * @throws IOException
	 * 
	 * Header String을 출력한다.
	 */
	private void sendHeader(OutputStream outputStream, String responseCodeAndMesage, String contentType, int length) throws IOException {
		
		HttpHeader header 	= new HttpHeader();
		Date now 			= new Date();
		Writer out 			= new OutputStreamWriter(new BufferedOutputStream(outputStream));

		header.setResponse(true);
		header.putRawHeaderLine(responseCodeAndMesage);
		header.setValue("Date", now.toString());
		header.setValue("Server", "JHTTP 2.0");
		if (length > 0) {
			header.setValue("Content-Length", String.valueOf(length));
		}
		header.setValue("Content-Type", contentType);

		String responseHeaderString = header.makeResponseHeader();
		logger.debug(responseHeaderString);
		out.write(responseHeaderString);
		out.write("\n");

		out.flush();
	}
}
