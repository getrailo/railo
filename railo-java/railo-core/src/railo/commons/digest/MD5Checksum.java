package railo.commons.digest;
import java.io.InputStream;
import java.security.MessageDigest;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

public class MD5Checksum {

   public static byte[] createChecksum(Resource res) throws Exception {
     InputStream is =  res.getInputStream();
     try{
	     byte[] buffer = new byte[1024];
	     MessageDigest complete = MessageDigest.getInstance("MD5");
	     int numRead;
	     do {
	      numRead = is.read(buffer);
	      if (numRead > 0) {
	        complete.update(buffer, 0, numRead);
	        }
	     }
	     while (numRead != -1);
	     
	     return complete.digest();
     }
     finally {
    	 IOUtil.closeEL(is);
     }
     
     
   }

   // see this How-to for a faster way to convert 
   // a byte array to a HEX string 
   public static String getMD5Checksum(Resource res) throws Exception {
     byte[] b = createChecksum(res);
     String result = "";
     for (int i=0; i < b.length; i++) {
       result +=
          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
      }
     return result;
   }
}