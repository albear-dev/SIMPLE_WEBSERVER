package nhn.test.webserver.servlet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.processor.RequestProcessor;
import nhn.test.webserver.tx.HttpRequest;
import nhn.test.webserver.tx.HttpResponse;

public class Hello implements SimpleServlet {
	private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class.getCanonicalName());
	
	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		logger.info("service excute start!");
		
		OutputStream raw = new BufferedOutputStream(response.getOutputStream());
		logger.debug("outputstream > " + response.getOutputStream().getClass().getName());
        Writer out = new OutputStreamWriter(raw);
        byte[] theData = "{\"status\":\"success\"}".getBytes();
        sendHeader(out, "HTTP/1.0 200 OK", "application/json", theData.length);
        
        raw.write(theData);
        raw.flush();
	}
	
	private void sendHeader(Writer out, String responseCode, String contentType, int length)
            throws IOException {
        out.write(responseCode + "\r\n");
        Date now = new Date();
        out.write("Date: " + now + "\r\n");
        out.write("Server: JHTTP 2.0\r\n");
        out.write("Content-length: " + length + "\r\n");
        out.write("Content-type: " + contentType + "\r\n\r\n");
        out.flush();
    }

}
