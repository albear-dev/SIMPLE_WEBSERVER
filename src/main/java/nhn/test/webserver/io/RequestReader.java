package nhn.test.webserver.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.thread.ThreadCallback;
import nhn.test.webserver.tx.HttpHeader;
import nhn.test.webserver.tx.HttpRequest;
import nhn.test.webserver.tx.HttpTransaction;
import nhn.test.webserver.util.StringUtils;

public class RequestReader implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RequestReader.class.getCanonicalName());

	private HttpTransaction _httpTransaction;
	private ThreadCallback callback;

	public RequestReader() {}
	public RequestReader(HttpTransaction httpTransaction, ThreadCallback callback) {
		this._httpTransaction 	= httpTransaction;
		this.callback 			= callback;
	}

	@Override
	public void run() {
		logger.info("start RequestReader! > " + Thread.currentThread().getName());
		HttpTransaction httpTransaction = _httpTransaction;
		HttpRequest httpRequest 		= httpTransaction.getRequest();
		try {
			/*******************
			 * 1. InputStream 준비
			 *******************/
			InputStream is = new BufferedInputStream(httpRequest.getInputStream());

			/*******************
			 * 2. httpHeader 읽기
			 *******************/
			logger.info("Start read http header");
			HttpHeader httpHeader = readHttpHeader(is);
			// 헤더 객체를 request 에 지정한다.
			httpRequest.setHttpHeader(httpHeader);
			logger.trace("Header Data [{}]", httpHeader.getHeaderMap());

			/*******************
			 * 3. 헤더에 기술된 contentLenth 확인
			 *******************/
			int contentLength = 0;
			try {
				contentLength = Integer.parseInt(httpHeader.getValue("Content-Length"));
			} catch (NumberFormatException e) {
				logger.info("Not contains payload data.");
			}

			/*******************
			 * 4. payload 데이터 읽기
			 *******************/
			if (contentLength > 0) {
				logger.trace("Start read http payload. contentLength is [{}]", contentLength);
				// httpContents(Paylod) 부 읽기
				byte[] payloadBinaryData = readHttpPayload(is, contentLength);
				httpRequest.setPayloadContent(payloadBinaryData);
				logger.trace("Payload Data [{}]", new String(payloadBinaryData, "UTF-8"));
			}

			/*******************
			 * 5. QueryString 파싱
			 *******************/
			// TODO http post 폼데이터 파싱 구현 필요.
			httpRequest.setParameterMap(StringUtils.parseQueryString(httpHeader.getQueryString()));

			callback.completed(httpTransaction);
		} catch (IOException e) {
			logger.error("UnExpected Exception! (File Io Error)", e);
			callback.failed(httpTransaction, e);
		} catch (Exception e) {
			logger.error("UnExpected Exception!", e);
			callback.failed(httpTransaction, e);
		}
	}

	/**
	 * InputStream 에서 byte 데이터를  http header부분만큼 읽어서 내부 관리 객체로 파싱 수행
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 * 
	 * 
	 */
	public HttpHeader readHttpHeader(InputStream is) throws Exception {
		HttpHeader httpHeader = new HttpHeader();

		int oneInt;
		ByteArrayOutputStream arrayBuff = new ByteArrayOutputStream();
		String line;
		int carrageReturnCount = 0;

		logger.debug("socket read start!");
		while ((oneInt = is.read()) != -1) {

			// header 읽을때 carragrReturn 체크
			// body는 Content-Length 만큼 무조건 읽을것이므로 체크필요 없음
			if (oneInt == '\r') {
				continue;
			}
			if (oneInt == '\n') {
//				logger.debug("find carriage return!");
				line = new String(arrayBuff.toByteArray(), "UTF-8");
//				logger.trace("header read >> " + line);
				httpHeader.putRawHeaderLine(line);
				arrayBuff.reset();

				if (++carrageReturnCount > 1) {
					logger.trace("Header Data [{}]", httpHeader.getHeaderMap());
					// 헤더 읽기를 중지한다. 두번째 \n 까지 읽었으므로 다음부터는 body 영역이다.
					break;
				}
			} else {
				if (carrageReturnCount > 0) {
					carrageReturnCount = 0;
				}
				arrayBuff.write((byte) oneInt);
			}
		}

		return httpHeader;
	}

	/**
	 * Http 헤더 읽기 수행 후 나머지 Payload 부분을 읽는다.
	 * 
	 * @param is
	 * @param contentLength
	 * @return
	 * @throws Exception
	 * 
	 * 
	 */
	public byte[] readHttpPayload(InputStream is, int contentLength) throws Exception {
		int oneInt;
		int bodyReadLength = 0;
		ByteArrayOutputStream arrayBuff = new ByteArrayOutputStream();

		while ((oneInt = is.read()) != -1) {
//			logger.trace("read >> "+String.valueOf((char)oneInt));
			arrayBuff.write((byte) oneInt);
			bodyReadLength++;

			// Content-Length 길이에 따라 Body 전부 읽음
			if (contentLength == bodyReadLength) {
				break;
			}
		}
		return arrayBuff.toByteArray();
	}
}
