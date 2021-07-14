package nhn.test.webserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import nhn.test.webserver.config.ConfigManager;
import nhn.test.webserver.io.RequestReader;
import nhn.test.webserver.io.ResponseWriter;
import nhn.test.webserver.io.SocketOutputStreamWrapper;
import nhn.test.webserver.processor.RequestProcessor;
import nhn.test.webserver.thread.MockSocket;
import nhn.test.webserver.thread.MockThreadCallback;
import nhn.test.webserver.tx.HttpHeader;
import nhn.test.webserver.tx.HttpTransaction;

public class HttpCallTest {
	
	/**
	 * @throws IOException
	 * 정상 경로의 http 호출 테스트
	 */
	@Test
	public void httpCallsInNormalPath() throws IOException{
		/*******************
		 * 1. 요청 Contents 생성
		 *******************/
		String contents = "";

		/*******************
		 * 2. 요청 header 생성
		 *******************/
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.setMethod("POST");
		httpHeader.setUrl("/nhn.test.webserver.servlet.Hello");
		httpHeader.setVersion("HTTP/1.1");
		httpHeader.setValue("Content-Length", String.valueOf(contents.getBytes().length));
		httpHeader.setValue("Content-Type", "application/json");
		
		/*******************
		 * 4. MockRequest 요청 처리 후 응답
		 *******************/
		byte[] responseContents = callMockRequest(httpHeader, contents);
		try {
			// RequestReader의 헤더 분석기를 활용한다.
			HttpHeader responseHttpHeader = new RequestReader().readHttpHeader(new ByteArrayInputStream(responseContents));
			responseHttpHeader.convertResponseHeader();
			assertEquals(String.valueOf(HttpURLConnection.HTTP_OK), responseHttpHeader.getStatusCode());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws IOException
	 * 잘못된 경로의 http 호출 테스트
	 */
	@Test
	public void httpCallsInWrongPath() throws IOException{
		/*******************
		 * 1. 요청 Contents 생성
		 *******************/
		String contents = "";

		/*******************
		 * 2. 요청 header 생성
		 *******************/
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.setMethod("POST");
		httpHeader.setUrl("/nhn.test.webserver.servlet.Hellossssss");
		httpHeader.setVersion("HTTP/1.1");
		httpHeader.setValue("Content-Length", String.valueOf(contents.getBytes().length));
		httpHeader.setValue("Content-Type", "application/json");
		
		/*******************
		 * 4. MockRequest 요청 처리 후 응답
		 *******************/
		byte[] responseContents = callMockRequest(httpHeader, contents);
		try {
			// RequestReader의 헤더 분석기를 활용한다.
			HttpHeader responseHttpHeader = new RequestReader().readHttpHeader(new ByteArrayInputStream(responseContents));
			responseHttpHeader.convertResponseHeader();
			assertEquals(String.valueOf(HttpURLConnection.HTTP_NOT_FOUND), responseHttpHeader.getStatusCode());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @throws IOException
	 * a.com의 hello.html 호출
	 */
	@Test
	public void callAdotComPage() throws IOException{
		/*******************
		 * 1. 요청 Contents 생성
		 *******************/
		String contents = "";

		/*******************
		 * 2. 요청 header 생성
		 *******************/
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.setMethod("POST");
		httpHeader.setUrl("/hello.html");
		httpHeader.setVersion("HTTP/1.1");
		httpHeader.setValue("Host", "a.com");
		httpHeader.setValue("Content-Length", String.valueOf(contents.getBytes().length));
		httpHeader.setValue("Content-Type", "application/json");
		
		/*******************
		 * 4. MockRequest 요청 처리 후 응답
		 *******************/
		byte[] responseContents = callMockRequest(httpHeader, contents);
		try {
			// RequestReader의 헤더 분석기를 활용한다.
			InputStream inputStream 		= new ByteArrayInputStream(responseContents);
			RequestReader requestReader 	= new RequestReader();
			HttpHeader responseHttpHeader 	= requestReader.readHttpHeader(inputStream);
			responseHttpHeader.convertResponseHeader();
			
			// 결과 contents 파싱
			byte[] httpPayload 				= requestReader.readHttpPayload(inputStream, Integer.parseInt(responseHttpHeader.getValue("Content-Length")));
			String payloadString			= new String(httpPayload);
			
			// 결과 비교
			assertEquals(String.valueOf(HttpURLConnection.HTTP_OK), responseHttpHeader.getStatusCode());
			assertTrue(payloadString.indexOf("Hello a.com page") > 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @throws IOException
	 * b.com의 hello.html 호출
	 */
	@Test
	public void callBdotComPage() throws IOException{
		/*******************
		 * 1. 요청 Contents 생성
		 *******************/
		String contents = "";

		/*******************
		 * 2. 요청 header 생성
		 *******************/
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.setMethod("POST");
		httpHeader.setUrl("/hello.html");
		httpHeader.setVersion("HTTP/1.1");
		httpHeader.setValue("Host", "b.com");
		httpHeader.setValue("Content-Length", String.valueOf(contents.getBytes().length));
		httpHeader.setValue("Content-Type", "application/json");
		
		/*******************
		 * 4. MockRequest 요청 처리 후 응답
		 *******************/
		byte[] responseContents = callMockRequest(httpHeader, contents);
		try {
			// RequestReader의 헤더 분석기를 활용한다.
			InputStream inputStream 		= new ByteArrayInputStream(responseContents);
			RequestReader requestReader 	= new RequestReader();
			HttpHeader responseHttpHeader 	= requestReader.readHttpHeader(inputStream);
			responseHttpHeader.convertResponseHeader();
			
			// 결과 contents 파싱
			byte[] httpPayload 				= requestReader.readHttpPayload(inputStream, Integer.parseInt(responseHttpHeader.getValue("Content-Length")));
			String payloadString			= new String(httpPayload);
			
			// 결과 비교
			assertEquals(String.valueOf(HttpURLConnection.HTTP_OK), responseHttpHeader.getStatusCode());
			assertTrue(payloadString.indexOf("Hello b.com page") > 0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @throws IOException
	 * 상위 디렉터리 접근
	 */
	@Test
	public void checkAccessToUpperDirectory() throws IOException{
		/*******************
		 * 1. 요청 Contents 생성
		 *******************/
		String contents = "";

		/*******************
		 * 2. 요청 header 생성
		 *******************/
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.setMethod("POST");
		httpHeader.setUrl("/../../../../hello.html");
		httpHeader.setVersion("HTTP/1.1");
		httpHeader.setValue("Host", "a.com");
		httpHeader.setValue("Content-Length", String.valueOf(contents.getBytes().length));
		httpHeader.setValue("Content-Type", "application/json");
		
		/*******************
		 * 4. MockRequest 요청 처리 후 응답
		 *******************/
		byte[] responseContents = callMockRequest(httpHeader, contents);
		try {
			// RequestReader의 헤더 분석기를 활용한다.
			InputStream inputStream 		= new ByteArrayInputStream(responseContents);
			RequestReader requestReader 	= new RequestReader();
			HttpHeader responseHttpHeader 	= requestReader.readHttpHeader(inputStream);
			responseHttpHeader.convertResponseHeader();
			
			// 결과 비교
			assertEquals(String.valueOf(HttpURLConnection.HTTP_FORBIDDEN), responseHttpHeader.getStatusCode());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws IOException
	 * 잘못된 확장자(exe) 호출
	 */
	@Test
	public void checkForWrongExtension() throws IOException{
		/*******************
		 * 1. 요청 Contents 생성
		 *******************/
		String contents = "";

		/*******************
		 * 2. 요청 header 생성
		 *******************/
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.setMethod("POST");
		httpHeader.setUrl("/hello.exe");
		httpHeader.setVersion("HTTP/1.1");
		httpHeader.setValue("Host", "a.com");
		httpHeader.setValue("Content-Length", String.valueOf(contents.getBytes().length));
		httpHeader.setValue("Content-Type", "application/json");
		
		/*******************
		 * 4. MockRequest 요청 처리 후 응답
		 *******************/
		byte[] responseContents = callMockRequest(httpHeader, contents);
		try {
			// RequestReader의 헤더 분석기를 활용한다.
			InputStream inputStream 		= new ByteArrayInputStream(responseContents);
			RequestReader requestReader 	= new RequestReader();
			HttpHeader responseHttpHeader 	= requestReader.readHttpHeader(inputStream);
			responseHttpHeader.convertResponseHeader();
			
			// 결과 비교
			assertEquals(String.valueOf(HttpURLConnection.HTTP_FORBIDDEN), responseHttpHeader.getStatusCode());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @throws IOException
	 * 현재시간 출력
	 */
	@Test
	public void currentTimeOutput() throws IOException{
		/*******************
		 * 1. 요청 Contents 생성
		 *******************/
		String contents = "";

		/*******************
		 * 2. 요청 header 생성
		 *******************/
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.setMethod("POST");
		httpHeader.setUrl("/nhn.test.webserver.servlet.NowTime");
		httpHeader.setVersion("HTTP/1.1");
		httpHeader.setValue("Host", "a.com");
		httpHeader.setValue("Content-Length", String.valueOf(contents.getBytes().length));
		httpHeader.setValue("Content-Type", "application/json");
		
		/*******************
		 * 4. MockRequest 요청 처리 후 응답
		 *******************/
		byte[] responseContents = callMockRequest(httpHeader, contents);
		try {
			// RequestReader의 헤더 분석기를 활용한다.
			InputStream inputStream 		= new ByteArrayInputStream(responseContents);
			RequestReader requestReader 	= new RequestReader();
			HttpHeader responseHttpHeader 	= requestReader.readHttpHeader(inputStream);
			responseHttpHeader.convertResponseHeader();
			
			// 결과 contents 파싱
			byte[] httpPayload 				= requestReader.readHttpPayload(inputStream, Integer.parseInt(responseHttpHeader.getValue("Content-Length")));
			String payloadString			= new String(httpPayload);
						
			// 결과 비교
			assertEquals(String.valueOf(HttpURLConnection.HTTP_OK), responseHttpHeader.getStatusCode());
			
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = mapper.readValue(payloadString, new TypeReference<Map<String, String>>(){});
			assertNotNull(map.get("nowTime"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get QueryString 처리 테스트
	 * 
	 * @throws IOException
	 */
	@Test
	public void httpQueryStringParseTest() throws IOException{
		/*******************
		 * 1. 요청 Contents 생성
		 *******************/
		String contents = "";

		/*******************
		 * 2. 요청 header 생성
		 *******************/
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.setMethod("POST");
		httpHeader.setUrl("/nhn.test.webserver.servlet.Hello?key1=val1&key2=val2&key3=val3");
		httpHeader.setVersion("HTTP/1.1");
		httpHeader.setValue("Content-Length", String.valueOf(contents.getBytes().length));
		httpHeader.setValue("Content-Type", "application/json");
		
		StringBuilder requestRawData = new StringBuilder();
		requestRawData.append(httpHeader.makeRequestHeader()).append("\r\n");
		requestRawData.append(contents);
		
		/*******************
		 * 4. MockRequest 요청 처리 후 응답
		 *******************/
		HttpTransaction httpTransaction 	= new HttpTransaction(ConfigManager.getConfig(), new MockSocket(requestRawData.toString().getBytes()));
		RequestReader requetReader 			= new RequestReader(httpTransaction ,new MockThreadCallback());
		requetReader.run();
		
		try {
			assertEquals("val1", httpTransaction.getRequest().getParameter("key1"));
			assertEquals("val2", httpTransaction.getRequest().getParameter("key2"));
			assertEquals("val3", httpTransaction.getRequest().getParameter("key3"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param requestData
	 * @return
	 * @throws IOException
	 * 
	 * Http 요청 처리 및 응답 리턴
	 */
	private byte[] callMockRequest(HttpHeader httpHeader, String contents) throws IOException {
		
		/*******************
		 * 3. http 요청 데이터 조합
		 *******************/
		StringBuilder requestRawData = new StringBuilder();
		requestRawData.append(httpHeader.makeRequestHeader()).append("\r\n");
		requestRawData.append(contents);
		
		/*******************
		 * Http 요청 처리 작업 시작
		 *******************/
		HttpTransaction httpTransaction 	= new HttpTransaction(ConfigManager.getConfig(), new MockSocket(requestRawData.toString().getBytes()));
		RequestReader requetReader 			= new RequestReader(httpTransaction ,new MockThreadCallback());
		requetReader.run();
		
		RequestProcessor requestProcessor	= new RequestProcessor(httpTransaction ,new MockThreadCallback());
		requestProcessor.run();
		
		ResponseWriter responseWriter		= new ResponseWriter(httpTransaction ,new MockThreadCallback());
		responseWriter.run();
		
		/*******************
		 * 결과 리턴
		 *******************/
		SocketOutputStreamWrapper socketOutputStreamWrappter	= (SocketOutputStreamWrapper)httpTransaction.getResponse().getOutputStream();
		ByteArrayOutputStream byteArrayOutputStream 			= (ByteArrayOutputStream)socketOutputStreamWrappter.getOriginalOutputStream();
		return byteArrayOutputStream.toByteArray();
	}
}
