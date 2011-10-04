package railo.commons.net;

import java.io.UnsupportedEncodingException;

import railo.commons.lang.StringUtil;

/**
 * Utility class for HTML form encoding. This class contains static methods
 * for converting a String to the <CODE>application/x-www-form-urlencoded</CODE> MIME
 * format. For more information about HTML form encoding, consult the HTML 
 * <A HREF="http://www.w3.org/TR/html4/">specification</A>. 
 *
 * <p>
 * When encoding a String, the following rules apply:
 *
 * <p>
 * <ul>
 * <li>The alphanumeric characters &quot;<code>a</code>&quot; through
 *     &quot;<code>z</code>&quot;, &quot;<code>A</code>&quot; through
 *     &quot;<code>Z</code>&quot; and &quot;<code>0</code>&quot; 
 *     through &quot;<code>9</code>&quot; remain the same.
 * <li>The special characters &quot;<code>.</code>&quot;,
 *     &quot;<code>-</code>&quot;, &quot;<code>*</code>&quot;, and
 *     &quot;<code>_</code>&quot; remain the same. 
 * <li>The space character &quot;<code>&nbsp;</code>&quot; is
 *     converted into a plus sign &quot;<code>+</code>&quot;.
 * <li>All other characters are unsafe and are first converted into
 *     one or more bytes using some encoding scheme. Then each byte is
 *     represented by the 3-character string
 *     &quot;<code>%<i>xy</i></code>&quot;, where <i>xy</i> is the
 *     two-digit hexadecimal representation of the byte. 
 *     The recommended encoding scheme to use is UTF-8. However, 
 *     for compatibility reasons, if an encoding is not specified, 
 *     then the default encoding of the platform is used.
 * </ul>
 *
 * <p>
 * For example using UTF-8 as the encoding scheme the string &quot;The
 * string &#252;@foo-bar&quot; would get converted to
 * &quot;The+string+%C3%BC%40foo-bar&quot; because in UTF-8 the character
 * &#252; is encoded as two bytes C3 (hex) and BC (hex), and the
 * character @ is encoded as one byte 40 (hex).
 *
 * @author  Herb Jellinek
 * @version 1.25, 12/03/01
 * @since   JDK1.0
 */
public class URLEncoder {
    
    /**
     * You can't call the constructor.
     */
    private URLEncoder() { }

    /**
     * Translates a string into <code>x-www-form-urlencoded</code>
     * format. This method uses the platform's default encoding
     * as the encoding scheme to obtain the bytes for unsafe characters.
     *
     * @param   s   <code>String</code> to be translated.
     * @deprecated The resulting string may vary depending on the platform's
     *             default encoding. Instead, use the encode(String,String)
     *             method to specify the encoding.
     * @return  the translated <code>String</code>.
     */
    public static String encode(String s) {
    	return java.net.URLEncoder.encode(s);
    }

    /**
     * Translates a string into <code>application/x-www-form-urlencoded</code>
     * format using a specific encoding scheme. This method uses the
     * supplied encoding scheme to obtain the bytes for unsafe
     * characters.
     * <p>
     * <em><strong>Note:</strong> The <a href=
     * "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
     * World Wide Web Consortium Recommendation</a> states that
     * UTF-8 should be used. Not doing so may introduce
     * incompatibilites.</em>
     *
     * @param   s   <code>String</code> to be translated.
     * @param   enc   The name of a supported 
     *    <a href="../lang/package-summary.html#charenc">character
     *    encoding</a>.
     * @return  the translated <code>String</code>.
     * @exception  UnsupportedEncodingException
     *             If the named encoding is not supported
     * @see URLDecoder#decode(java.lang.String, java.lang.String)
     */
    public static String encode(String s, String enc) throws UnsupportedEncodingException {
    	if(StringUtil.isAscci(s)) return s;
    	return java.net.URLEncoder.encode(s, enc);
    }
    
    
    // Only alphanumerics [0-9a-zA-Z], the special characters ""
    public static boolean needEncoding(String str){
    	if(StringUtil.isEmpty(str,false)) return false;
    	
    	int len=str.length();
    	char c;
    	for(int i=0;i<len;i++){
    		c=str.charAt(i);
    		if(c >='0' && c <= '9') continue;
    		if(c >='a' && c <= 'z') continue;
    		if(c >='A' && c <= 'Z') continue;
    		
    		// _-.*
    		if(c =='-') continue;
    		if(c =='_') continue;
    		if(c =='.') continue;
    		if(c =='*') continue;
    		if(c =='+') continue;
    		/*
    		if(c =='$') continue;
    		if(c =='+') continue;
    		if(c =='!') continue;
    		if(c =='\'') continue;
    		if(c =='(') continue;
    		if(c ==')') continue;
    		if(c ==',') continue;
    		*/
    		//  # % &  / : ; = ? @ [ ]
    		
    		// [A-Z, a-z], Ziffern [0-9] und
    		// - _ . ~

    		if(c =='%') {
    			if(i+2>=len) return true;
    			try{
    				Integer.parseInt(str.substring(i+1,i+3),16);
    			}
    			catch(NumberFormatException nfe){
    				return true;
    			}
    			i+=3;
    			continue;
    		}
    		return true;
    	}
    	
    	
    	
    	return false;
    }
    
}
