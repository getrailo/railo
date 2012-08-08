/**
 * Implements the CFML Function val
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public final class Val implements Function {
	
	public static double call(PageContext pc , Object value) throws PageException {
	    String str=Caster.toString(value);
        str=str.trim();
	    int pos=getPos(str);
	    if(pos<=0) {
		    if(Decision.isBoolean(str)) return Caster.toDoubleValue(str);
		    return 0;
		}
		return Caster.toDoubleValue(str.substring(0,pos));
	}
	
	private static int getPos(String str) { 
        if(str==null) return 0; 
        
        int pos=0; 
        int len=str.length(); 
        if(len==0) return 0; 
        char curr=str.charAt(pos); 
        
        if(curr=='+' || curr=='-') { 
                if(len==++pos) return 0; 
                curr=str.charAt(pos); 
        }
        
        // at least one digit 
        if(curr>='0' && curr<='9') { 
                curr=str.charAt(pos); 
        }
        else if(curr=='.'){
        	curr='.';
        }
        else return 0; 

        boolean hasDot=false; 
        //boolean hasExp=false; 
        for(;pos<len;pos++) { 
            curr=str.charAt(pos); 
            if(curr<'0') {
            	if(curr=='.') { 
                    if(pos+1>=len || hasDot) return pos; 
                    hasDot=true; 
                } 
            	else return pos;
            }
            else if(curr>'9') {
            	/*if(curr=='e' || curr=='E') { 
                    if(pos+1>=len || hasExp) return pos; 
                    hasExp=true; 
                    hasDot=true; 
                }
            	else */
            		return pos;
            }
        } 
        
        return pos; 
    } 
}