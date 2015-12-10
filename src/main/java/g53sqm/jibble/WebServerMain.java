package g53sqm.jibble;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of Jibble Web Server / WebServerLite.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: WebServerMain.java,v 1.2 2004/02/01 13:37:35 pjm2 Exp $

*/


/**
 * This class contains the main method for the Jibble Web Server.
 * 
 * @author Copyright Paul Mutton, http://www.jibble.org/
 */
public class WebServerMain {

	static String rootDir = WebServerConfig.DEFAULT_ROOT_DIRECTORY;
    static int port = WebServerConfig.DEFAULT_PORT;
    
    public static void main(String[] args) {
        
        if (args.length > 0) {
            rootDir = args[0];
        }
        
        if (args.length > 1) {
            try {
            	port = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
            	// Stick with the default value.y
            }
        }

        try {
        	WebServer server = new WebServer(rootDir, port);

        	Properties prop = new Properties();
        	try
        	{
        		// Configuration file name
        		String fileName = "server.config";            
        		InputStream is = new FileInputStream(fileName);

        		// Properties File
        		prop.load(is);

        		WebServerConfig.rootDir = prop.getProperty("server.rootDir");
        		WebServerConfig.binDir = prop.getProperty("server.binDir");
        		WebServerConfig.port = Integer.parseInt((String) prop.get("server.port"));
        		String logFile = prop.getProperty("server.logFile");
        		WebServerConfig.log = prop.getProperty("server.log");
        	} 
        	catch (FileNotFoundException e)
        	{
        		e.printStackTrace();
        	}
        	catch (IOException e)
        	{
        		e.printStackTrace();
        	}
        	server.activate();
        }
        catch (WebServerException e) {
        	System.out.println(e.toString());
        }
    }

}