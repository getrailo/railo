package railo.commons.lang;

import railo.runtime.op.Caster;

public final class ByteSizeParser {

	private static final long B=1;
	private static final long KB=1024;
	private static final long MB=KB*1024;
	private static final long GB=MB*1024;
	private static final long TB=GB*1024;
	
	public static long parseByteSizeDefinition(String value, long defaultValue) {
    	value=value.trim().toLowerCase();
    	
    	long factor=B;
    	String num=value;
    	if(value.endsWith("kb")) {
    		factor=KB;
    		num=value.substring(0,value.length()-2).trim();
    	}
    	else if(value.endsWith("k")) {
    		factor=KB;
    		num=value.substring(0,value.length()-1).trim();
    	}
    	else if(value.endsWith("mb")) {
    		factor=MB;
    		num=value.substring(0,value.length()-2).trim();
    	}
    	else if(value.endsWith("m")) {
    		factor=MB;
    		num=value.substring(0,value.length()-1).trim();
    	}
    	else if(value.endsWith("gb")) {
    		factor=GB;
    		num=value.substring(0,value.length()-2).trim();
    	}
    	else if(value.endsWith("g")) {
    		factor=GB;
    		num=value.substring(0,value.length()-1).trim();
    	}
    	else if(value.endsWith("tb")) {
    		factor=TB;
    		num=value.substring(0,value.length()-2).trim();
    	}
    	else if(value.endsWith("t")) {
    		factor=TB;
    		num=value.substring(0,value.length()-1).trim();
    	}
    	else if(value.endsWith("b")) {
    		factor=B;
    		num=value.substring(0,value.length()-1).trim();
    	}
    	
    	long tmp = Caster.toLongValue(num,Long.MIN_VALUE);
    	if(tmp==Long.MIN_VALUE) return defaultValue;
    	return tmp*factor;
	}
}
