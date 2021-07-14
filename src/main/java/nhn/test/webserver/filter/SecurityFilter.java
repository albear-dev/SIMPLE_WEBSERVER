package nhn.test.webserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.exception.SecurityViolationException;
import nhn.test.webserver.processor.RequestProcessor;
import nhn.test.webserver.tx.HttpRequest;
import nhn.test.webserver.tx.HttpResponse;

/**
 * RequestProcessor 에서 전처리 작업을 수행하기 위한 Filter 클래스
 * 미리 Request/Response 데이터에 대한 선작업을 하거나
 * 체크하는 로직을 추가하여 처리할수 있도록 한다.
 * 
 * @author Master
 *
 */
public class SecurityFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class.getCanonicalName());
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response) throws Exception{
		
		// 요청 경로가 올바르지 않음(docRoot보다 상위 폴더 조회)
		String url = request.getHttpHeader().getUrl();
		logger.debug("url > " +url);
		if(url.indexOf("/..") > -1) {
			throw new SecurityViolationException();
		}
		
		// 확장자가 exe인 요청을 받았을때
		if(url.lastIndexOf(".exe") > -1) {
			throw new SecurityViolationException();
		}
		
	}

	
}
