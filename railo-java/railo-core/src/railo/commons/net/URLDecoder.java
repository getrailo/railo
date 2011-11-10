package railo.commons.net;

import java.io.UnsupportedEncodingException;

import railo.commons.io.SystemUtil;
import railo.runtime.net.http.ReqRspUtil;

public class URLDecoder {

	private URLDecoder(){}

	/**
	 * @param string
	 * @return
	 */
	public static String decode(String str, boolean force) {
		try {
			return decode(str,SystemUtil.getCharset(), force);
		} 
		catch (UnsupportedEncodingException e) {
			return str;
		}
	}

    /**
     * Decodes a <code>application/x-www-form-urlencoded</code> string using a specific 
     * encoding scheme.
     * The supplied encoding is used to determine
     * what characters are represented by any consecutive sequences of the
     * form "<code>%<i>xy</i></code>".
     * <p>
     * <em><strong>Note:</strong> The <a href=
     * "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
     * World Wide Web Consortium Recommendation</a> states that
     * UTF-8 should be used. Not doing so may introduce
     * incompatibilites.</em>
     *
     * @param s the <code>String</code> to decode
     * @param enc   The name of a supported 
     *    <a href="../lang/package-summary.html#charenc">character
     *    encoding</a>. 
     * @param force if set to false Railo only encodes when there is at least one  "%<2-digit-hex-value>" in string, means string with only + inside are not encoded
     * @return the newly decoded <code>String</code>
     * @throws UnsupportedEncodingException 
     * @see URLEncoder#encode(java.lang.String, java.lang.String)
     */
    public static String decode(String s, String enc, boolean force) throws UnsupportedEncodingException {
    	if(!force && !ReqRspUtil.needDecoding(s)) return s;
    	//if(true) return java.net.URLDecoder.decode(s, enc);
    	
	boolean needToChange = false;
	StringBuilder sb = new StringBuilder();
	int numChars = s.length();
	int i = 0;

	

	while (i < numChars) {
            char c = s.charAt(i);
            switch (c) {
	    case '+':
		sb.append(' ');
		i++;
		needToChange = true;
		break;
	    case '%':
		
		try {
		    byte[] bytes = new byte[(numChars-i)/3];
		    int pos = 0;
		    
		    while ( ((i+2) < numChars) && 
			    (c=='%')) {
			bytes[pos++] = (byte)Integer.parseInt(s.substring(i+1,i+3),16);
			i+= 3;
			if (i < numChars)
			    c = s.charAt(i);
		    }

		    if ((i < numChars) && (c=='%')){
		    	needToChange = true;
				sb.append(c); 
				i++;
				continue;
		    	//throw new IOException("Incomplete trailing escape (%) pattern");
		    }
		    sb.append(new String(bytes, 0, pos, enc));
		} catch (NumberFormatException e) {
			needToChange = true;
			sb.append(c); 
			i++;
		    //throw new IOException("Illegal hex characters in escape (%) pattern - " + e.getMessage());
		}
		needToChange = true;
		break;
	    default: 
		sb.append(c); 
		i++;
		break; 
            }
        }

        return (needToChange? sb.toString() : s);
    }
}


