/**
 * Implements the Cold Fusion Function listdeleteat
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class ListDeleteAt implements Function {
	
	private static char[] DEFAULT_DELIMETER=new char[]{','};
	
	public static String call(PageContext pc , String list, double pos) throws ExpressionException {
		return _call(pc,list,(int)pos,DEFAULT_DELIMETER);
	}
	

	public static String call(PageContext pc, String list, double posNumber, String del) throws ExpressionException {
		return _call(pc, list, (int)posNumber, del.toCharArray());
	}
	
	
	/*public static void main(String[] args) throws ExpressionException {
		
		print( ",,,.;;,,bbb,ccc,,,",2,",.;");
		print(",,,.;;,,aaa,,,",1,",.;");
		
		
	}
	

    private static void print(String str, int pos,String del) throws ExpressionException {
    	print.out("--------------");
    	print.out(_call(null, str, pos,del.toCharArray()));
    	print.out(_call(null, str, pos,del));
		
	}*/


	public static String _call(PageContext pc, String list, int pos, char[] del) throws ExpressionException {
    	
    	StringBuilder sb = new StringBuilder();
    	int len=list.length();
    	int index=0;
    	char last=0,c;
    	
    	if(pos<1) throw new FunctionException(pc,"ListDeleteAt",2,"index","index must be greater than 0");
    	
    	pos--;
    	
    	int i=0;
    	
    	// ignore all delimeter at start
    	for(;i<len;i++){
    		c=list.charAt(i);
    		if(!equal(del,c)) break;
    		sb.append(c);
    	}
    	
    	// before
    	for(;i<len;i++){
    		
    		c=list.charAt(i);
    		if(index==pos && !equal(del,c)) break;
    		if(equal(del,c)) {
    			if(!equal(del,last))index++;
    		}
    		sb.append(c);
    		last=c;
    	}
    	
    	
    	// supress item
    	for(;i<len;i++){
    		if(equal(del,list.charAt(i))) break;
    	}
    	
    	// ignore following delimeter
    	for(;i<len;i++){
    		if(!equal(del,list.charAt(i))) break;
    	}
    	
    	if(i==len){
    		
    		while(sb.length()>0 && equal(del,sb.charAt(sb.length()-1))) {
    			sb.delete(sb.length()-1, sb.length());
    		}
    		if(pos>index) throw new FunctionException(pc,"ListDeleteAt",2,"index","index must be a integer between 1 and "+index);
        	
    		return sb.toString();
    	}
    	
    	
    	// fill the rest
    	for(;i<len;i++){
    		sb.append(list.charAt(i));
    	}
    	
    	return sb.toString();
    }
	
	
    private static boolean equal(char[] del, char c) {
    	for(int i=0;i<del.length;i++){
    		if(del[i]==c) return true;
    	}
		return false;
	}


	
	
}