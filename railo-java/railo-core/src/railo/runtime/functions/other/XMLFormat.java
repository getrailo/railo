package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function xmlformat
 */
public final class XMLFormat implements Function {
	public static String call(PageContext pc , String xmlString) {
		int len=xmlString.length();
		//StringBuffer sb=new StringBuffer(len);
		int plus=0;
		
		for(int pos=0;pos<len;pos++) {
            char chr = xmlString.charAt(pos);
            switch(chr){
            	case '<':	plus+=3;	break;
            	case '>':	plus+=3;	break;
            	case '&':	plus+=4;	break;
            	case '"':	plus+=5;	break;
            	case '\'':	plus+=5;	break;
            }
		}
        if(plus==0) return xmlString;
        
        char[] chars=new char[len+plus];
        int count=0;
        
		for(int pos=0;pos<len;pos++) {
            char chr = xmlString.charAt(pos);
            switch(chr){
            	case '<': 
            	    chars[count++]='&';
            	    chars[count++]='l';
            	    chars[count++]='t';
            	    chars[count++]=';';
            	break;
            	case '>': 
            	    chars[count++]='&';
            	    chars[count++]='g';
            	    chars[count++]='t';
            	    chars[count++]=';';
            	break;
            	case '&': 
            	    chars[count++]='&';
            	    chars[count++]='a';
            	    chars[count++]='m';
            	    chars[count++]='p';
            	    chars[count++]=';';
            	break;
            	case '"': 
            	    chars[count++]='&';
            	    chars[count++]='q';
            	    chars[count++]='u';
            	    chars[count++]='o';
            	    chars[count++]='t';
            	    chars[count++]=';';
            	break;
            	case '\'': 
            	    chars[count++]='&';
            	    chars[count++]='a';
            	    chars[count++]='p';
            	    chars[count++]='o';
            	    chars[count++]='s';
            	    chars[count++]=';';
            	break;
            	default: 
            	    chars[count++]=chr;
            	break;
            }
		}
        
        
		//if(start<len)sb.append(xmlString.substring(start,len));
		return new String(chars);
	}




}