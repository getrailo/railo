package railo.commons.lang;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import org.apache.commons.codec.net.URLCodec;

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
        // special chars
        //WWW_FORM_URL.set('-');
        //WWW_FORM_URL.set('_');
        //WWW_FORM_URL.set('.');
        //WWW_FORM_URL.set('*');
    }

	public static String encode(String str, String encoding) throws UnsupportedEncodingException {
		return new String(URLCodec.encodeUrl(WWW_FORM_URL, str.getBytes(encoding)),"us-ascii");
	}
	
	public static String encode(String str) throws UnsupportedEncodingException {
		return new String(URLCodec.encodeUrl(WWW_FORM_URL, str.getBytes("UTF-8")),"us-ascii");
	}
}
