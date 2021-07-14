package nhn.test.webserver.tx;

import java.io.InputStream;
import java.util.Map;

/**
 * Http 요청 처리에 대한 Request Wrapping 객체
 * Http 헤더/Payload/파라메터 등에 대한 정보를 가지고 있다.
 * 
 * @author Master
 *
 */
public class HttpRequest {
	private HttpHeader httpHeader;
	private byte[] payloadContent;
	private InputStream inputStream;
	private Map<String, String> parameterMap;

	public HttpHeader getHttpHeader() {
		return httpHeader;
	}

	public void setHttpHeader(HttpHeader httpHeader) {
		this.httpHeader = httpHeader;
	}

	public byte[] getPayloadContent() {
		return payloadContent;
	}

	public void setPayloadContent(byte[] payloadContent) {
		this.payloadContent = payloadContent;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, String> parameterMap) {
		this.parameterMap = parameterMap;
	}

	public String getParameter(String key) {
		return parameterMap.get(key);
	}
	
}
