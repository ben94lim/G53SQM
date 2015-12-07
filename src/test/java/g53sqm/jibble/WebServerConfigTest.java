/**
 * 
 */
package g53sqm.jibble;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.*;

import static org.mockito.Mockito.*;

/**
 * @author Benjamin Lim
 *
 */
@RunWith(JUnitParamsRunner.class)
public class WebServerConfigTest {

	private Object[] files()
	{
		return new Object[]
		{
				new Object[] {new File("image.jpg"), ".jpg"},
				new Object[] {new File("shtml.shtml"), ".shtml"},
				new Object[] {new File("shtm.shtm"), ".shtm"},
				new Object[] {new File("none"), ""},
		};
	}
	
	@Test
	@Parameters(method = "files")
	public void getExtensionTest(File files, String expected) {
		assertEquals(expected, WebServerConfig.getExtension(files));
	}
}
