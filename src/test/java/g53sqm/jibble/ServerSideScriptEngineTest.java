/**
 * 
 */
package g53sqm.jibble;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import junitparams.Parameters;

/**
 * @author Benjamin Lim
 *
 */
public class ServerSideScriptEngineTest {


	@Test
	public void extractVariableTest() {
		HashMap <String, String> variables = new HashMap<String, String>();
		variables.put("Host","net.tutsplus.com");
		variables.put("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)");
		variables.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		variables.put("Accept-Language","en-us,en;q=0.5");
		variables.put("Accept-Encoding","gzip,deflate");
		variables.put("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		variables.put("Keep-Alive","300");
		variables.put("Connection","keep-alive");
		
		String[] expected = {"Accept-Language=en-us,en;q=0.5",
							"Host=net.tutsplus.com",
							"Accept-Charset=ISO-8859-1,utf-8;q=0.7,*;q=0.7",
							"Accept-Encoding=gzip,deflate",
							"Keep-Alive=300",
							"User-Agent=Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)",
							"Connection=keep-alive",
							"Accept=text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"};
		
				String[] result = ServerSideScriptEngine.extractVariables(variables);

		assertArrayEquals(expected, result);
	}
	
	@Test
	public void getCommandTest() {
		String[] fileNames = {"file1.pl", "file2.php", "FILE3.php", "file4.jpg"};
		String[] expected1 = {"perl","php","php","file4.jpg"};
		String[] expected2 = {"file1.pl","file2.php","FILE3.php",""};
		
		for(int i=0; i<4; i++)
		{		
			String[]result = ServerSideScriptEngine.getCommand(fileNames[i]);
			assertEquals(expected1[i], result[0]);
			if(result[0]!=fileNames[i])
				assertEquals(expected2[i], result[1]);			
		}		
	}
}
