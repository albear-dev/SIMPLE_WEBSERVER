package nhn.test.webserver.tx;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHeaderTest {
	private static final Logger logger = LoggerFactory.getLogger(HttpHeaderTest.class.getCanonicalName());
	
	HttpHeader header;
	
	@Before
	public void setUp() {
		header = new HttpHeader();
		header.putRawHeaderLine("POST /nhn.test.webserver.servlet.NowTime HTTP/1.1");
		header.putRawHeaderLine("Host: 127.0.0.1");
		header.putRawHeaderLine("Content-Type: application/json");
		header.putRawHeaderLine("Content-Length: 10");
    }

	/**
	 * Http 헤더 객체 파싱 수행 테스트
	 */
	@Test
	public void objectHeaderParsingTest() {
		assertEquals("POST", header.getMethod());
		assertEquals("/nhn.test.webserver.servlet.NowTime", header.getUrl());
		assertEquals("HTTP/1.1", header.getVersion());
		assertEquals("127.0.0.1", header.getValue("Host"));
		assertEquals("application/json", header.getValue("Content-Type"));
		assertEquals("10", header.getValue("Content-Length"));
	}
	
	/**
	 * 요청 헤더 Raw Format 으로 생성 테스트
	 */
	@Test
	public void makeRequestHeaderTest() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("POST /nhn.test.webserver.servlet.NowTime HTTP/1.1").append("\r\n");
		sb.append("Host: 127.0.0.1").append("\r\n");
		sb.append("Content-Length: 10").append("\r\n");
		sb.append("Content-Type: application/json").append("\r\n");
		
		String requestHeader = header.makeRequestHeader();
		assertEquals(sb.toString(), requestHeader);
	}
	
	/**
	 * 응답 헤더 Raw Format 으로 생성 테스트
	 */
	@Test
	public void makeResponseHeaderTest() {
		StringBuilder sb = new StringBuilder();		
		sb.append("HTTP/1.1 200 OK").append("\r\n");
		sb.append("Host: 127.0.0.1").append("\r\n");
		sb.append("Content-Length: 10").append("\r\n");
		sb.append("Content-Type: application/json").append("\r\n");
		
		header.setResponse(true);
		header.putRawHeaderLine("HTTP/1.1 200 OK");
		String responseHeader = header.makeResponseHeader();
		logger.debug("responseHeader >>" + responseHeader);
		assertEquals(sb.toString(), responseHeader);
	}
	
}
