/**
 * 
 */
package g53sqm.jibble;

import static org.junit.Assert.*;

import org.junit.Test;

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

}
