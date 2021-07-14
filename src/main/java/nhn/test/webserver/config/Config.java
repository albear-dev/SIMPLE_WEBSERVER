package nhn.test.webserver.config;

import java.util.List;
import java.util.Map;

import nhn.test.webserver.util.StringUtils;

public class Config {
	public int listenPort  						= 8080;
	public Map<String, String> errorPage;
	public List<String> resourcePattren;
	public String webRoot;
	public Map<String, String> documentRoot;
	
	public int requestReaderThreadMaxCount 		= 5;
	public int responseWriterThreadMaxCount 	= 5;
	public int requestProcessorThreadMaxCount 	= 5;
	
	public Config(){}
	
	public int getListenPort() {
		return listenPort;
	}
	public String getWebRoot() {
		return webRoot;
	}
	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public String getDocumentRootPath(String host) {
		String documentRootPath = documentRoot.get(host);
		if(StringUtils.isEmpty(documentRootPath)) {
			return documentRoot.get("*");
		}else {
			return documentRootPath;
		}
	}
	public int getRequestReaderThreadMaxCount() {
		return requestReaderThreadMaxCount;
	}
	public int getResponseWriterThreadMaxCount() {
		return responseWriterThreadMaxCount;
	}
	public int getRequestProcessorThreadMaxCount() {
		return requestProcessorThreadMaxCount;
	}
	public List<String> getResourcePattren() {
		return resourcePattren;
	}
	public void setResourcePattren(List<String> resourcePattren) {
		this.resourcePattren = resourcePattren;
	}
	public Map<String, String> getErrorPage() {
		return errorPage;
	}
	public void setErrorPage(Map<String, String> errorPage) {
		this.errorPage = errorPage;
	}
	public String getErrorPagePath(int statusCode) {
		return errorPage.get(String.valueOf(statusCode));
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n");
		sb.append("listenPort=").append(listenPort).append("\r\n");
		sb.append("documentRoot=").append(documentRoot).append("\r\n");
		sb.append("errorPage=").append(errorPage).append("\r\n");
		sb.append("resourcePattren=").append(resourcePattren).append("\r\n");
		sb.append("requestReaderThreadMaxCount=").append(requestReaderThreadMaxCount).append("\r\n");
		sb.append("requestProcessorThreadMaxCount=").append(requestProcessorThreadMaxCount).append("\r\n");
		sb.append("responseWriterThreadMaxCount=").append(responseWriterThreadMaxCount).append("\r\n");
		
		return sb.toString();
	}
	
}
