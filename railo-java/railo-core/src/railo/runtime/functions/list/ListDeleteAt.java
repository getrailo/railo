/**
 * Implements the CFML Function listdeleteat
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;

public final class ListDeleteAt extends BIF {
	
	private static char[] DEFAULT_DELIMITER=new char[]{','};
	
	public static String call(PageContext pc , String list, double posNumber) throws ExpressionException {
		return _call(pc,list,(int)posNumber,DEFAULT_DELIMITER,false);
	}
	
	public static String call(PageContext pc, String list, double posNumber, String del) throws ExpressionException {
		return _call(pc, list, (int)posNumber, del.toCharArray(),false);
	}
	
	public static String call(PageContext pc, String list, double posNumber, String del, boolean includeEmptyFields) throws ExpressionException {
		return _call(pc, list, (int)posNumber, del.toCharArray(),includeEmptyFields);
	}


    @Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
    	if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]));
    	if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toString(args[2]));
    	if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toDoubleValue(args[1]), Caster.toString(args[2]), Caster.toBooleanValue(args[3]));
    	
		throw new FunctionException(pc, "ListDeleteAt", 2, 4, args.length);
	}
	

	

	public static String _call(PageContext pc, String list, int pos, char[] del, boolean includeEmptyFields) throws ExpressionException {
    	
    	StringBuilder sb = new StringBuilder();
    	int len=list.length();
    	int index=0;
    	char last=0,c;
    	
    	if(pos<1) throw new FunctionException(pc,"ListDeleteAt",2,"index","index must be greater than 0");
    	
    	pos--;
    	
    	int i=0;
    	
    	// ignore all delimiter at start
    	if(!includeEmptyFields)for(;i<len;i++){
    		c=list.charAt(i);
    		if(!equal(del,c)) break;
    		sb.append(c);
    	}
    	
    	// before
    	for(;i<len;i++){
    		
    		c=list.charAt(i);
    		if(index==pos && !equal(del,c)) break;
    		if(equal(del,c)) {
    			if(includeEmptyFields || !equal(del,last))
    				index++;
    		}
    		sb.append(c);
    		last=c;
    	}
    	
    	
    	// suppress item
    	for(;i<len;i++){
    		if(equal(del,list.charAt(i))) break;
    	}
    	
    	// ignore following delimiter
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