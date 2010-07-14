/**
 * Implements the Cold Fusion Function listrest
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListRest implements Function {
	public static String call(PageContext pc , String string) {
		return call(pc,string,",");
	}
	public static String call(PageContext pc , String list, String delimeter) {
		return List.rest(list, delimeter);
		
		/*
		//String[] arr=List.listToArrayRemoveEmptyItem(list,delimeter);
		Array arr=List.listToArrayRemoveEmpty(list,delimeter);
		
		if(arr.size()<2) return "";
		//arr[0]="";
		arr.setEL(1,"");
		
		//return List.arrayToList(List.trim(arr),delimeter);
		return List.arrayToList(arr,delimeter).substring(delimeter.length());*/
		
	}
}