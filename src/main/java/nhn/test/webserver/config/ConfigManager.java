package nhn.test.webserver.config;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigManager {
	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class.getCanonicalName());

	private final static String configName = "Config.json";
	private static Config config;

	private ConfigManager() {}

	public static Config getConfig() {
		synchronized (Config.class) {
			if (config == null) {
				config = parseConfigFromFile();
			}
		}
		return config;
	}

	/**
	 * 상대경로로 config 파일을 읽는다.
	 * 
	 * @return
	 */
	public static Config parseConfigFromFile(String path) {
		Config config = null;

		try {
			File finalPath = new File(path);
			
			logger.info("Load config file from [{}]", finalPath.getAbsolutePath());
			// create object mapper instance
			ObjectMapper mapper = new ObjectMapper();

			// convert JSON file to map
			config = mapper.readValue(finalPath, Config.class);

			logger.info("config info >> "+ config);
		} catch (Exception ex) {
			logger.error("config parse error!", ex);
		}

		return config;
	}
	public static Config parseConfigFromFile() {
		
		File path = new File(".");
		
		return parseConfigFromFile(path.getAbsolutePath() + File.separator + configName);
	}

}
