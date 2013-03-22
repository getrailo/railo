/**
 * Implements the Cold Fusion Function cjustify
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class CJustify implements Function {

	public static String call(PageContext pc , String string, double length) throws ExpressionException {
		int len=(int)length;
		if(len<1) throw new ExpressionException("Parameter 2 of function cJustify which is now ["+len+"] must be a non-negative integer");
		else if((len-=string.length())<=0) return string;
		else {
			
			char[] chrs=new char[string.length()+len];
			int part=len/2;
			
			for(int i=0;i<part;i++)chrs[i]=' '; 
			for(int i=string.length()-1;i>=0;i--)chrs[part+i]=string.charAt(i); 
			for(int i=part+string.length();i<chrs.length;i++)chrs[i]=' '; 
			
			return new String(chrs);
		}
	}

	/*public static void main(String[] args) throws ExpressionException {

		
		for(int i=1;i<11;i++) {
			print.ln(i+"->"+call(null,"abc",i)+"<-");
		}
		
		
		long start;
		
		start=System.currentTimeMillis();
		for(int i=1;i<100000;i++) {
			call(null,"abc",10);
		}
		print.ln(System.currentTimeMillis()-start);
	}*/
}











