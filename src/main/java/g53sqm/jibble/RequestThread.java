package g53sqm.jibble;
/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of Jibble Web Server / WebServerLite.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: RequestThread.java,v 1.2 2004/02/01 13:37:35 pjm2 Exp $

*/


import java.io.*;
import java.net.*;
import java.util.*;

import org.slf4j.LoggerFactory;


/**
 * A thread which deals with an individual request to the web server.
 * This is passed a socket from the WebServer when a connection is
 * accepted.
 * 
 * @author Copyright Paul Mutton, http://www.jibble.org/
 */
public class RequestThread implements Runnable {

	static org.slf4j.Logger logger = LoggerFactory.getLogger(RequestThread.class);
	
    public RequestThread(Socket socket, File rootDir) {
        _socket = socket;
        _rootDir = rootDir;
    }

    public String readFirstLine(String request)
    {
    	if (request != null && request.startsWith("GET ") && (request.endsWith(" HTTP/1.0") || request.endsWith("HTTP/1.1"))) {
    		return request.substring(4, request.length() - 9);
    	}
    	else
    		return "";
    }
    
    public HashMap<String, String> getHashMap(BufferedReader in)
    {
    	HashMap <String, String> headers = new HashMap<String, String>();
    	String line = null;
		try {
			while ((line = in.readLine()) != null) {
				String[] output = getHashHeader(line);
				if (output[0]=="") {
					break;
				}
				headers.put(output[0], output[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return headers;
    }

    public String[] getHashHeader(String line)
    {
    	String[] output = new String[2];
    	line = line.trim();
    	if (line.equals("")) {
    		output[0] = "";
    		return output;
    	}
    	int colonPos = line.indexOf(":");
    	if (colonPos > 0) {
    		// Key
    		output[0] = line.substring(0, colonPos);
    		// Value
    		output[1] = line.substring(colonPos + 1);    
    	}
    	return output;
    }
    
    public int checkFile(File file)
    {
    	if (!file.toString().startsWith(_rootDir.toString()))
    		return 1;    	
    	if (!file.exists())
    		return 2;
    	if (file.isDirectory())
    		return 3;
    	
		return 0;
    }

    // handles a connection from a client.
    public void run() {
    	String ip = "unknown";
    	String request = "unknown";
    	int bytesSent = 0;
    	BufferedInputStream reader = null;
    	try {
    		ip = _socket.getInetAddress().getHostAddress();
    		BufferedReader in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
    		BufferedOutputStream out = new BufferedOutputStream(_socket.getOutputStream());

    		String path = "";
    		// Read the first line from the client.
    		request = in.readLine();
    		path = readFirstLine(request);
    		if (path==""){
    			// Invalid request type (no "GET")
    			logger.error("IP: " + ip + " | Request: " + request + " | Code: 405");
    			_socket.close();
    			return;
    		}

    		//Read in and store all the headers.

    		// Specify String types of HashMap for safety - TJB
    		// HashMap headers = new HashMap();
    		HashMap <String, String> headers = getHashMap(in);

    		// URLDecocer.decode(String) is deprecated - added "UTF-8"  -  TJB
    		File file = new File(_rootDir, URLDecoder.decode(path, "UTF-8"));

            file = file.getCanonicalFile();
            
            int fileType = checkFile(file);
            
            if (fileType == 1) {
                // The file was not found.
            	logger.error("IP: " + ip + " | Request: " + request + " | Code: 404");
                out.write(("HTTP/1.0 404 File Not Found\r\n" + 
                           "Content-Type: text/html\r\n" +
                           "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                           "\r\n" +
                           "<h1>404 File Not Found</h1><code>" + path  + "</code><p><hr>" +
                           "<i>" + WebServerConfig.VERSION + "</i>").getBytes());
                out.flush();
                _socket.close();
                return;
            }
            
            if (fileType == 2) {
                // Uh-oh, it looks like some lamer is trying to take a peek
                // outside of our web root directory.
            	logger.error("IP: " + ip + " | Request: " + request + " | Code: 404");
                out.write(("HTTP/1.0 403 Forbidden\r\n" +
                           "Content-Type: text/html\r\n" + 
                           "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                           "\r\n" +
                           "<h1>403 Forbidden</h1><code>" + path  + "</code><p><hr>" +
                           "<i>" + WebServerConfig.VERSION + "</i>").getBytes());
                out.flush();
                _socket.close();
                return;
            }
            
            if (fileType == 3) {
                // Check to see if there are any index files in the directory.
                for (int i = 0; i < WebServerConfig.DEFAULT_FILES.length; i++) {
                    File indexFile = new File(file, WebServerConfig.DEFAULT_FILES[i]);
                    if (indexFile.exists() && !indexFile.isDirectory()) {
                        file = indexFile;
                        break;
                    }
                }
                if (file.isDirectory()) {
                    // print directory listing
                	logger.info("IP: " + ip + " | Request: " + request + " | Code: 200");
                    if (!path.endsWith("/")) {
                        path = path + "/";
                    }
                    File[] files = file.listFiles();
                    out.write(("HTTP/1.0 200 OK\r\n" +
                               "Content-Type: text/html\r\n" +
                               "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                               "\r\n" +
                               "<h1>Directory Listing</h1>" +
                               "<h3>" + path + "</h3>" +
                               "<table border=\"0\" cellspacing=\"8\">" +
                               "<tr><td><b>Filename</b><br></td><td align=\"right\"><b>Size</b></td><td><b>Last Modified</b></td></tr>" +
                               "<tr><td><b><a href=\"../\">../</b><br></td><td></td><td></td></tr>").getBytes());
                    for (int i = 0; i < files.length; i++) {
                        file = files[i];
                        if (file.isDirectory()) {
                            out.write(("<tr><td><b><a href=\"" + path + file.getName() + "/\">" + file.getName() + "/</a></b></td><td></td><td></td></tr>").getBytes());
                        }
                        else {
                            out.write(("<tr><td><a href=\"" + path + file.getName() + "\">" + file.getName() + "</a></td><td align=\"right\">" + file.length() + "</td><td>" + new Date(file.lastModified()).toString() + "</td></tr>").getBytes());
                        }
                    }
                    out.write(("</table><hr>" + 
                               "<i>" + WebServerConfig.VERSION + "</i>").getBytes());
                    out.flush();
                    _socket.close();
                    return;
                }
            }
            
            String extension = WebServerConfig.getExtension(file);
            
            // Execute any files in any cgi-bin directories under the web root.
            if (file.getParent().indexOf("cgi-bin") >= 0) {
                try {
                    out.write("HTTP/1.0 200 OK\r\n".getBytes());
                    ServerSideScriptEngine.execute(out, headers, file, path);
                    out.flush();
                    logger.info("IP: " + ip + " | Path: " + path + " | Code: 200");
                }
                catch (Throwable t) {
                    // Internal server error!
                	logger.error("IP: " + ip + " | Request: " + request + " | Code: 500");
                    out.write(("Content-Type: text/html\r\n\r\n" +
                               "<h1>Internal Server Error</h1><code>" + path  + "</code><hr>Your script produced the following error: -<p><pre>" +
                               t.toString() + 
                               "</pre><hr><i>" + WebServerConfig.VERSION + "</i>").getBytes());
                    out.flush();
                    _socket.close();
                    return;
                }
                out.flush();
                _socket.close();
                return;
            }

            reader = new BufferedInputStream(new FileInputStream(file));
            
            logger.info("IP: " + ip + " | Request: " + request + " | Code: 200");
            String contentType = (String)WebServerConfig.MIME_TYPES.get(extension);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            out.write(("HTTP/1.0 200 OK\r\n" + 
                       "Date: " + new Date().toString() + "\r\n" +
                       "Server: JibbleWebServer/1.0\r\n" +
                       "Content-Type: " + contentType + "\r\n" +
                       "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                       "Content-Length: " + file.length() + "\r\n" +
                       "Last-modified: " + new Date(file.lastModified()).toString() + "\r\n" +
                       "\r\n").getBytes());

            if (WebServerConfig.SSI_EXTENSIONS.contains(extension)) {
                reader.close();
                ServerSideIncludeEngine.deliverDocument(out, file);
                _socket.close();
                return;
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = reader.read(buffer, 0, 4096)) != -1) {
                out.write(buffer, 0, bytesRead);
                bytesSent += bytesRead;
            }
            out.flush();
            reader.close();
            _socket.close();
            
        }
        catch (IOException e) {
        	logger.error("IP: " + ip + " | Error: " + e.toString() + " | Request: " + request + " | Code: 0");
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception anye) {
                    // Do nothing.
                }
            }
        }
    }
    
    private Socket _socket;
    private File _rootDir;

}