/**
 * 
 */
package g53sqm.jibble;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import java.util.HashSet;

import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author User
 *
 */

public class ServerSideIncludeEngineTest {
	
	@Test
	public void test() throws IOException {
		Socket socket = mock(Socket.class);
		BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
		HashSet <File> visited = new HashSet <File> ();
		File file = new File("src/test/resources/test.shtml");
		
		ServerSideIncludeEngine.testParse(out, visited, file);
        assertEquals(4, ServerSideIncludeEngine.numCalls);

	}

}
