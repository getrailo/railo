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
			return decode(str,SystemUtil.getCharset().name(), force);
		} 
		catch (UnsupportedEncodingException e) {
			return str;
		}
	}

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


