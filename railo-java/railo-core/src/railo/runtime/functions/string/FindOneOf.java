/**
 * Implements the CFML Function findoneof
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class FindOneOf implements Function {
	public static double call(PageContext pc , String set, String str) {
		return call(pc,set,str,1);
	}
	public static double call(PageContext pc , String strSet, String strData, double number) {
		// strData
		char[] data=strData.toCharArray();
		// set
		char[] set=strSet.toCharArray();
		// start
		int start=(int)number-1;
		if(start<0)start=0;
		
		if( start>=data.length || set.length==0) return 0;
		//else {
			for(int i=start;i<data.length;i++) {
				for(int y=0;y<set.length;y++) {
					if(data[i]==set[y])return i+1;
				}
			}
		//}
		return 0;
	}

}