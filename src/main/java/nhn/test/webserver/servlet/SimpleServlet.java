package nhn.test.webserver.servlet;

import java.io.IOException;

import nhn.test.webserver.tx.HttpRequest;
import nhn.test.webserver.tx.HttpResponse;

public interface SimpleServlet {
	public void service(HttpRequest req, HttpResponse res) throws IOException;
}
