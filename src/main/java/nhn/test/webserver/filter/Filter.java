package nhn.test.webserver.filter;

import nhn.test.webserver.tx.HttpRequest;
import nhn.test.webserver.tx.HttpResponse;

public interface Filter {
	public void doFilter(HttpRequest request, HttpResponse response) throws Exception;
}
