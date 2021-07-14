package nhn.test.webserver.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nhn.test.webserver.config.ConfigManager;
import nhn.test.webserver.exception.ResourceNotFoundException;

public class FileUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class.getCanonicalName());
	/**
	 * 리소스 호출의 경우 경로 파일만 확인하여 위치를 넘겨준다. 
	 * 이후 Writer에서 실제 resource read/socket write 수행함
	 * 
	 * @param header
	 */
	public static InputStream getStaticResourceInputStream(String host, String url) throws IOException, ResourceNotFoundException{
		String webRoot	= ConfigManager.getConfig().getWebRoot();
		Path path 		= FileSystems.getDefault().getPath(webRoot + File.separator + ConfigManager.getConfig().getDocumentRootPath(host) + File.separator + url);
		logger.debug("Path [{}] ", path);
		if(!Files.exists(path)) {
			throw new ResourceNotFoundException();
		}
		return Files.newInputStream(path);
	}
	
	/**
	 * 파일을 읽어서 byteArrayOutputStream으로 전달한다.
	 * 
	 * @param staticResourceInputStream
	 * @return
	 * @throws IOException
	 */
	public static ByteArrayOutputStream readStaticResource(InputStream staticResourceInputStream) throws IOException {
		ByteArrayOutputStream arrayBuff = new ByteArrayOutputStream();
	
		// Static resource의 InputStream을 가져온다.
		InputStream is 		= new BufferedInputStream(staticResourceInputStream);
		
		//TODO chunked 구현
		int readLength 		= 0;
		byte[] buffer 		= new byte[1024];
		while ((readLength = is.read(buffer)) > 0) {
			arrayBuff.write(buffer, 0, readLength);
		}	
		
		return arrayBuff;
	}
}
