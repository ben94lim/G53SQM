/**
 * 
 */
package g53sqm.jibble;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;

import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author User
 *
 */
public class RequestThreadTest {

	@Test
	public void readFirstLineTest() {
		String emptyLine = "";
		
		assertEquals("", RequestThread.readFirstLine(emptyLine));
		
		String noGET = "usable HTTP/1.0";

		assertEquals("", RequestThread.readFirstLine(noGET));

		String noHTTP = "GET usable ";

		assertEquals("", RequestThread.readFirstLine(noHTTP));
		
		String usableLine = "GET usable HTTP/1.0";

		assertEquals("usable", RequestThread.readFirstLine(usableLine));
	}
	
	@Test
	public void getHashHeaderTest() {
		String[] output = new String[2];
		
		String emptyLine = "";		
		output = RequestThread.getHashHeader(emptyLine);
		assertEquals("", output[0]);
		
		String usableLine = "Tag:123456";
		
		output = RequestThread.getHashHeader(usableLine);
		assertEquals("Tag", output[0]);
		assertEquals("123456", output[1]);
	}

	@Test
	public void getHashMapTest() throws UnsupportedEncodingException {
		String response = "Tag1:1234 \n Tag2:5678";
		String encoding = "UTF-8";


		InputStream inputStream = new ByteArrayInputStream(response.getBytes(encoding));

		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

		HashMap <String, String> headers = RequestThread.getHashMap(in);
		assertEquals("1234", headers.get("Tag1")); // message sent and got a response
		assertEquals("5678", headers.get("Tag2"));

	}
}