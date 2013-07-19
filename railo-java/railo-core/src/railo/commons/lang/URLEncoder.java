package railo.commons.lang;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import org.apache.commons.codec.net.URLCodec;

import railo.commons.io.CharsetUtil;
/** 
 * @deprecated use instead railo.commons.net.URLEncoder
 * 
 */
public class URLEncoder {
	
	private static final BitSet WWW_FORM_URL = new BitSet(256);
    
    static {
        // alpha characters
        for (int i = 'a'; i <= 'z'; i++) {
            WWW_FORM_URL.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            WWW_FORM_URL.set(i);
        }
        // numeric characters
        for (int i = '0'; i <= '9'; i++) {
            WWW_FORM_URL.set(i);
        }
    }


	public static String encode(String str, java.nio.charset.Charset charset) throws UnsupportedEncodingException {
		return new String(URLCodec.encodeUrl(WWW_FORM_URL, str.getBytes(charset)),"us-ascii");
	}
	
	public static String encode(String str, String encoding) throws UnsupportedEncodingException {
		return new String(URLCodec.encodeUrl(WWW_FORM_URL, str.getBytes(encoding)),"us-ascii");
	}
	
    
	
	public static String encode(String str) throws UnsupportedEncodingException {
		return encode(str,CharsetUtil.UTF8);
	}
}
