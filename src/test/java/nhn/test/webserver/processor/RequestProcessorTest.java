package nhn.test.webserver.processor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import nhn.test.webserver.config.ConfigManager;
import nhn.test.webserver.thread.MockSocket;
import nhn.test.webserver.thread.MockThreadCallback;
import nhn.test.webserver.tx.HttpTransaction;
public class RequestProcessorTest {
//	private static final Logger logger = LoggerFactory.getLogger(RequestProcessorTest.class.getCanonicalName()); 
	
	// 호스트에 따른 DocumentRoot 경로 체크 (a.com)
	@Test
	public void isResourceTest() throws IOException {
		HttpTransaction httpTransaction 	= new HttpTransaction(ConfigManager.getConfig(), new MockSocket("".toString().getBytes()));
		RequestProcessor requestProcessor	= new RequestProcessor(httpTransaction ,new MockThreadCallback());
		File currentPath = new File(".");
		
		assertTrue(requestProcessor.isResource(currentPath.getAbsolutePath() + "web\\www\\default\\error\\404.html"));
	}
	
}
