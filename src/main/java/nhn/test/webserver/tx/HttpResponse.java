package nhn.test.webserver.tx;

import java.io.InputStream;
import java.io.OutputStream;

import nhn.test.webserver.http.HttpStatus;

public class HttpResponse {
	private HttpHeader httpHeader;
	private OutputStream outputStream;

	private int status = 200;
	private String contentType = "text/html";
	private int contentLength;
	private String characterEncoding = "UTF-8";
	
	private boolean isStaticResource;
	private InputStream staticResourceInputStream;
	private byte[] binaryContents;
	
	public void setHttpHeader(HttpHeader httpHeader) {
		this.httpHeader = httpHeader;
	}
	public HttpHeader getHttpHeader() {
		return httpHeader;
	}
	public void setHeader(String key, String value) {
		httpHeader.setValue(key, value);
	}
	public boolean containsHeader(String key) {
		return httpHeader.isContainsHeader(key);
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public OutputStream getOutputStream() {
		return outputStream;
	}
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	public byte[] getBinaryContents() {
		return binaryContents;
	}
	public void setBinaryContents(byte[] binaryContents) {
		this.binaryContents = binaryContents;
		this.contentLength 	= binaryContents.length;
	}
	public boolean hasBinaryContents() {
		return this.binaryContents!=null && this.binaryContents.length>0;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public String getCharacterEncoding() {
		return characterEncoding;
	}
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
	public String getResponseStatusText() {
		return "HTTP/1.0 " + String.valueOf(this.status) + " " + HttpStatus.fromStatusCode(this.status).toString();
	}
	public boolean isStaticResource() {
		return isStaticResource;
	}
	public void setStaticResource(boolean isStaticResource) {
		this.isStaticResource = isStaticResource;
	}
	public InputStream getStaticResourceInputStream() {
		return staticResourceInputStream;
	}
	public void setStaticResourceInputStream(InputStream staticResourceInputStream) {
		this.staticResourceInputStream = staticResourceInputStream;
		this.isStaticResource = true;
	}
	
}
