package nhn.test.webserver;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nhn.test.webserver.config.ConfigManagerTest;
import nhn.test.webserver.processor.RequestProcessorTest;
import nhn.test.webserver.tx.HttpHeaderTest;

/**
 * @SuiteClasses 전체 테스트
 * @author 
 *
 */
@RunWith(Suite.class)
@SuiteClasses({  ConfigManagerTest.class
				,HttpHeaderTest.class
				,RequestProcessorTest.class
				,HttpCallTest.class
				})
public class AllTests {
	
	@BeforeClass
	public static void globalBeforeClass() {
	}
	
	@AfterClass
	public static void globalAfterClass() {
	}
} 
