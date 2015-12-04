/**
 * 
 */
package g53sqm.jibble;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Benjamin Lim
 *
 */
public class WebServerConfigTest {

	@Test
	public void extractExtensionTest() {
		String image = "image.jpg";
		
		assertEquals(".jpg",WebServerConfig.extractExtension(image));
		
		String shtml = "shtml.shtml";
		
		assertEquals(".shtml",WebServerConfig.extractExtension(shtml));
		
		String shtm = "shtm.shtm";
		
		assertEquals(".shtm",WebServerConfig.extractExtension(shtm));
		
		String none = "none";
		
		assertEquals("",WebServerConfig.extractExtension(none));
	}

}
