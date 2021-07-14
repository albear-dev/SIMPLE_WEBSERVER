package nhn.test.webserver.tx;

import java.util.HashMap;
import java.util.Map;

/**
 * Http 헤더 데이터를 Wrapping 하여 관리하는 객체
 * 
 * @author Master
 *
 */
public class HttpHeader {

	private Map<String, String> headerMap;
	private String method;
	private String url;
	private String statusCode;
	private String statusMessage;
	private String version;
	private String queryString;
	private boolean isResponse = false;

	public HttpHeader() {
		headerMap = new HashMap<String, String>();
	}

	public String getValue(String key) {
		return (String) headerMap.get(key);
	}

	public String toString() {
		return headerMap.toString();
	}

	public Map<String, String> getHeaderMap() {
		return headerMap;
	}

	public void setHeaderMap(Map<String, String> headerMap) {
		this.headerMap = headerMap;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	

	public boolean isResponse() {
		return isResponse;
	}

	public void setResponse(boolean isResponse) {
		this.isResponse = isResponse;
	}

	public void setValue(String key, String value) {
		headerMap.put(key, value);
	}

	public boolean isContainsHeader(String key) {
		return headerMap.containsKey(key);
	}

	public String getHost() {
		return headerMap.get("Host");
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	/**
	 * http 헤더 부분의 String line을 분석하여 내부 객체에 할당함
	 * 
	 * @param rawHeaderLine
	 */
	public void putRawHeaderLine(String rawHeaderLine) {
		if (rawHeaderLine == null || "".contentEquals(rawHeaderLine.trim())) {
			return;
		}

		String[] token = rawHeaderLine.split(":");
		if (token.length == 1) {
			headerMap.put("", token[0]);
			String[] subToken = token[0].split(" ");
			
			if(!isResponse) {
				method 	= subToken[0];
				url 	= subToken[1];
				// url 에서 querystring은 분리
				int startPos = url.indexOf('?');
				if (startPos >= 0) {
					queryString = url.substring(startPos + 1);
					url 		= url.substring(0, startPos);
				}
				version = subToken[2];
			}else {
				// HTTP/1.0 404 NOT_FOUND
				version 		= subToken[0];
				statusCode 		= subToken[1];
				statusMessage 	= subToken[2];
			}
		} else {
			headerMap.put(token[0], token[1].trim());
		}
	}
	
	/**
	 * 현재 요청 데이터가 들어있는 헤더 정보를 응답용으로 변환한다.
	 */
	public void convertResponseHeader() {
		String resultLine = headerMap.get("");
		String[] subToken = resultLine.split(" ");
		
		method			= null;
		url				= null;
		queryString		= null;
		version 		= subToken[0];
		statusCode 		= subToken[1];
		statusMessage 	= subToken[2];
		
	}
	
	/**
	 * HttpHeader 객체를 실제 Write 하기 위한 String 문자열로 변환한다.
	 * 요청 처리용 헤더 포맷으로 변환함
	 * 
	 * @return
	 */
	public String makeRequestHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getMethod()).append(" ").append(this.getUrl()).append(" ").append(this.getVersion()).append("\r\n");
		for (Map.Entry<String, String> elem : headerMap.entrySet()) {
			if("".equals(elem.getKey())) continue;
			sb.append(elem.getKey() + ": " + elem.getValue()).append("\r\n");
		}
		return sb.toString();
	}
	
	/**
	 * HttpHeader 객체를 실제 Write 하기 위한 String 문자열로 변환한다.
	 * 응답 처리용 헤더 포맷으로 변환함
	 * 
	 * @return
	 */
	public String makeResponseHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getVersion()).append(" ").append(this.getStatusCode()).append(" ").append(this.getStatusMessage()).append("\r\n");
		for (Map.Entry<String, String> elem : headerMap.entrySet()) {
			if("".equals(elem.getKey())) continue;
			sb.append(elem.getKey() + ": " + elem.getValue()).append("\r\n");
		}
		
		return sb.toString();
	}

}
