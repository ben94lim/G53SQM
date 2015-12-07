/**
 * 
 */
package g53sqm.jibble;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import junitparams.*;

import static org.mockito.Mockito.*;

/**
 * @author User
 *
 */
@RunWith(JUnitParamsRunner.class)
public class RequestThreadTest {
	Socket socket;
	RequestThread testClass;
	
	@Before
	public void setUp() throws Exception {
		Socket socket = mock(Socket.class);
		File rootDir = new File("/");
		testClass = new RequestThread(socket, rootDir);
	}

	private Object[] requests()
	{
		return new Object[]
		{
				new Object[] {"GET usable HTTP/1.0", "usable"},
				new Object[] {"GET usable HTTP/1.1", "usable"},
				new Object[] {"usable HTTP/1.0", ""},
				new Object[] {"NOTGET usable HTTP/1.0", ""},
				new Object[] {"GET usable HTTP/1.5", ""}
		};
	}
	@Test
	@Parameters(method = "requests")
	public void readFirstLineTest(String requests, String expected) {
		assertEquals(expected, testClass.readFirstLine(requests));
	}
	
	@Test
	public void getHashHeaderTest() {
		String[] output = new String[2];
		
		String emptyLine = "";		
		output = testClass.getHashHeader(emptyLine);
		assertEquals("", output[0]);
		
		String usableLine = "Tag:123456";
		
		output = testClass.getHashHeader(usableLine);
		assertEquals("Tag", output[0]);
		assertEquals("123456", output[1]);
	}

	@Test
	public void getHashMapTest() throws UnsupportedEncodingException {
		String response = "Tag1:1234 \n Tag2:5678";
		String encoding = "UTF-8";


		InputStream inputStream = new ByteArrayInputStream(response.getBytes(encoding));

		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

		HashMap <String, String> headers = testClass.getHashMap(in);
		assertEquals("1234", headers.get("Tag1")); // message sent and got a response
		assertEquals("5678", headers.get("Tag2"));
	}
	
	private Object[] files()
	{
		return new Object[]
		{				
				new Object[] {new File ("src/main/resources/webfiles"), 1},
				new Object[] {new File ("/nonExistent.txt"), 2},
				new Object[] {new File ("/"), 3}
		};
	}
	@Test
	@Parameters(method = "files")
	public void checkFileTest(File files, int expected) {
		assertEquals(expected, testClass.checkFile(files));
	}
}