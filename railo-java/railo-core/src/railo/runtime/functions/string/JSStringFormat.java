/**
 * Implements the CFML Function jsstringformat
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class JSStringFormat implements Function {
	public static String call(PageContext pc , String str) {
		return invoke(str);
	}
	public static String invoke(String str) {
	    int len=str.length();
		StringBuffer rtn=new StringBuffer(len+10);
		//char[] arr=str.toCharArray();
		//StringBuffer rtn=new StringBuffer(arr.length);
		char c;
		for(int i=0;i<len;i++) {
		    c=str.charAt(i);
			switch(c) {
				case '\\': rtn.append("\\\\"); break;
				case '\n': rtn.append("\\n"); break;
				case '\r': rtn.append("\\r"); break;
				case '\f': rtn.append("\\f"); break;
				case '\b': rtn.append("\\b"); break;
				case '\t': rtn.append("\\t"); break;
				case '"' : rtn.append("\\\""); break;
				case '\'': rtn.append("\\\'"); break;
				default : rtn.append(c); break;
			}
		}
		return rtn.toString();
	}
	
	
	public static String callx(PageContext pc , String jsString) {// MUST ????
		int len=jsString.length();
		//StringBuffer sb=new StringBuffer(len);
		int plus=0;
		
		for(int pos=0;pos<len;pos++) {
            char chr = jsString.charAt(pos);
            
            switch(chr){
				case '\\': 
				case '\n': 
				case '\r': 
				case '\f': 
				case '\b': 
				case '\t': 
				case '"' : 
				case '\'': plus++; break;
            }
		}
        if(plus==0) return jsString;
        
        char[] chars=new char[len+plus];
        int count=0;
        
		for(int pos=0;pos<len;pos++) {
            char chr = jsString.charAt(pos);
            switch(chr){
	        	case '\\': 
	        	    chars[count++]='\\';
	        	    chars[count++]='\\';
	        	break;
	        	case '\'': 
	        	    chars[count++]='\\';
	        	    chars[count++]='\'';
	        	break;
	        	case '"': 
	        	    chars[count++]='\\';
	        	    chars[count++]='"';
	        	break;
            	case '\n': 
            	    chars[count++]='\\';
            	    chars[count++]='n';
            	break;
            	case '\r': 
            	    chars[count++]='\\';
            	    chars[count++]='r';
            	break;
            	case '\f': 
            	    chars[count++]='\\';
            	    chars[count++]='f';
            	break;
            	case '\b': 
            	    chars[count++]='\\';
            	    chars[count++]='b';
            	break;
            	case '\t': 
            	    chars[count++]='\\';
            	    chars[count++]='t';
            	break;
            	default: 
            	    chars[count++]=chr;
            	break;
            }
		}
        return new String(chars);
	}
}