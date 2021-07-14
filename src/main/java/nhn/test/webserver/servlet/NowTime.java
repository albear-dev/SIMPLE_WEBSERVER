package nhn.test.webserver.servlet;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.processor.RequestProcessor;
import nhn.test.webserver.tx.HttpRequest;
import nhn.test.webserver.tx.HttpResponse;

/**
 * 응답으로 현재 시각을 리턴하는 Servlet
 * 
 * @author Master
 *
 */
public class NowTime implements SimpleServlet {
private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class.getCanonicalName());
	
	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		logger.info("service excute start!");
		
        String resultData 	= "{\"nowTime\":\""+new Date()+"\"}";
        response.setContentType("application/json");
        response.setBinaryContents(resultData.getBytes());
        
	}
}
