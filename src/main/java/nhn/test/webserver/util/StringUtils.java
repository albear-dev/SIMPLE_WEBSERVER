package nhn.test.webserver.util;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static Map<String, String> parseQueryString(String queryString) {
		if (StringUtils.isEmpty(queryString))
			return null;

		int startPos = queryString.indexOf('?');
		Map<String, String> map = null;
		if (startPos >= 0) {
			// ? 뒷 부분만 추출
			queryString = queryString.substring(startPos + 1);
			// & 연결자 분리
			String[] params = queryString.split("&");
			// key=value 분리
			map = new HashMap<String, String>();
			for (String param : params) {
				String[] paramArr 	= param.split("=");
				map.put(paramArr[0], paramArr[1]);
			}
		}
		
		return map;
	}
}
