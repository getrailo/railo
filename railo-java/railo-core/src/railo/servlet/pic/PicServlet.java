package railo.servlet.pic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import railo.commons.io.IOUtil;


/**
 * Die Klasse PicServlet wird verwendet um Bilder darzustellen, 
 * alle Bilder die innerhalb des Deployer angezeigt werden, 
 * werden ueber diese Klasse aus der railo.jar Datei geladen, 
 * das macht die Applikation flexibler 
 * und verlangt nicht das die Bilder fuer die Applikation an einem bestimmten Ort abgelegt sein muessen. 
 */
public final class PicServlet extends HttpServlet {
	
	/**
	 * Verzeichnis in welchem die bilder liegen
	 */
	public final static String PIC_SOURCE=	"/resource/img/";
	
	/**
	 * Interpretiert den Script-Name und laedt das entsprechende Bild aus den internen Resourcen.
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse rsp)
		throws ServletException, IOException {
			// get out Stream
			
			// pic
			String[] arrPath=(req.getServletPath()).split("\\.");
			String pic=PIC_SOURCE+"404.gif";
			if(arrPath.length>=3)	{
				pic=PIC_SOURCE+((arrPath[arrPath.length-3]+"."+arrPath[arrPath.length-2]).replaceFirst("/",""));
				
				// mime type
				String mime="image/"+arrPath[arrPath.length-2];
				rsp.setContentType(mime);
			}
			
			// write data from pic input to response output
			OutputStream os=null; 
			InputStream is=null;   
			try {                
				os = rsp.getOutputStream();      
				is = getClass().getResourceAsStream(pic); 
				if(is==null) {
					is = getClass().getResourceAsStream(PIC_SOURCE+"404.gif"); 
				}   

				byte[] buf = new byte[4*1024];                
				int nread = 0;                
				while ((nread = is.read(buf)) >= 0) {                    
					os.write(buf, 0, nread);                
				}            
			} catch (FileNotFoundException e) {                           
			} catch (IOException e) {       
			} finally {  
				IOUtil.closeEL(is, os);  
			}
	}
	
	
}