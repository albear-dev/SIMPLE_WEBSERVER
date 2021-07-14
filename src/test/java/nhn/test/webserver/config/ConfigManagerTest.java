package nhn.test.webserver.config;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;

import org.junit.Test;
public class ConfigManagerTest {
	
	// 호스트에 따른 DocumentRoot 경로 체크 (a.com)
	@Test
	public void documentRootCheckAccordingToHostA() {
		String aDotPath = ConfigManager.getConfig().getDocumentRootPath("a.com");
		assertEquals(aDotPath,"www/a.com");
	}
	
	// 호스트에 따른 DocumentRoot 경로 체크 (b.com)
	@Test
	public void documentRootCheckAccordingToHostB() {
		String aDotPath = ConfigManager.getConfig().getDocumentRootPath("b.com");
		assertEquals(aDotPath,"www/b.com");
	}
	
	/**
	 * 에러페이지 상대경로 체크
	 */
	@Test
	public void getStaticResourceErrorPage() {
		String errorPagePath = ConfigManager.getConfig().getErrorPagePath(HttpURLConnection.HTTP_NOT_FOUND);
		assertEquals(errorPagePath,"error/404.html");
	}
	
}
