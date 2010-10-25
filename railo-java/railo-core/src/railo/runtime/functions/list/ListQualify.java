package railo.runtime.functions.list;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.List;

/**
 * Implements the Cold Fusion Function listqualify
 */
public final class ListQualify implements Function {
	public static String call(PageContext pc , String list, String qualifier) {
		return call(pc,list,qualifier,",","all", false);
	}
	public static String call(PageContext pc , String list, String qualifier, String delimeter) {
		return call(pc,list,qualifier,delimeter,"all", false);
	}


	public static String call(PageContext pc , String list, String qualifier, String delimeter, String scope) {
		return call(pc, list, qualifier, delimeter, scope, false);
	}
	
	public static String call(PageContext pc , String list, String qualifier, String delimeter, String scope, boolean psq) {
	   		if(list.length()==0) return "";
		if(psq)list=StringUtil.replace(list, "'", "''", false);
		Array arr=List.listToArrayRemoveEmpty(list,delimeter);
		
		boolean isQChar=qualifier.length()==1;
		boolean isDChar=delimeter.length()==1;
		
		if(isQChar && isDChar) return doIt(arr,qualifier.charAt(0),delimeter.charAt(0),scope);
		else if(isQChar && !isDChar) return doIt(arr,qualifier.charAt(0),delimeter,scope);
		else if(!isQChar && isDChar) return doIt(arr,qualifier,delimeter.charAt(0),scope);
		else return doIt(arr,qualifier,delimeter,scope);
		
	}

    private static String doIt(Array arr, char qualifier, char delimeter, String scope) {
        StringBuffer rtn=new StringBuffer();
        int len=arr.size();
        
		if(StringUtil.toLowerCase(scope).equals("all")) {
			rtn.append(qualifier);
			rtn.append(arr.get(1,""));
			rtn.append(qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimeter);
				rtn.append(qualifier);
				rtn.append(arr.get(i,""));
				rtn.append(qualifier);
			}
		}
		else {
			qualifyString(rtn,arr.get(1,"").toString(),qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimeter);
				qualifyString(rtn,arr.get(i,"").toString(),qualifier);
			}
		}
		return rtn.toString();
    }
    private static String doIt(Array arr, char qualifier, String delimeter, String scope) {
        StringBuffer rtn=new StringBuffer();
        int len=arr.size();
        
		if(StringUtil.toLowerCase(scope).equals("all")) {
			rtn.append(qualifier);
			rtn.append(arr.get(1,""));
			rtn.append(qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimeter);
				rtn.append(qualifier);
				rtn.append(arr.get(i,""));
				rtn.append(qualifier);
			}
		}
		else {
			qualifyString(rtn,arr.get(1,"").toString(),qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimeter);
				qualifyString(rtn,arr.get(i,"").toString(),qualifier);
			}
		}
		return rtn.toString();
    }
    private static String doIt(Array arr, String qualifier, char delimeter, String scope) {
        StringBuffer rtn=new StringBuffer();
        int len=arr.size();
        
		if(StringUtil.toLowerCase(scope).equals("all")) {
			rtn.append(qualifier);
			rtn.append(arr.get(1,""));
			rtn.append(qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimeter);
				rtn.append(qualifier);
				rtn.append(arr.get(i,""));
				rtn.append(qualifier);
			}
		}
		else {
			qualifyString(rtn,arr.get(1,"").toString(),qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimeter);
				qualifyString(rtn,arr.get(i,"").toString(),qualifier);
			}
		}
		return rtn.toString();
    }
    private static String doIt(Array arr, String qualifier, String delimeter, String scope) {
        StringBuffer rtn=new StringBuffer();
        int len=arr.size();
        
		if(StringUtil.toLowerCase(scope).equals("all")) {
			rtn.append(qualifier);
			rtn.append(arr.get(1,""));
			rtn.append(qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimeter);
				rtn.append(qualifier);
				rtn.append(arr.get(i,""));
				rtn.append(qualifier);
			}
		}
		else {
			qualifyString(rtn,arr.get(1,"").toString(),qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimeter);
				qualifyString(rtn,arr.get(i,"").toString(),qualifier);
			}
		}
		return rtn.toString();
    }
    
    
    private static void qualifyString(StringBuffer rtn,String value,String qualifier) {
		if(Decision.isNumeric(value)) rtn.append(value);
		else {
			rtn.append(qualifier);
			rtn.append(value);
			rtn.append(qualifier);
		}
	}
	private static void qualifyString(StringBuffer rtn,String value,char qualifier) {
		if(Decision.isNumeric(value)) rtn.append(value);
		else {
			rtn.append(qualifier);
			rtn.append(value);
			rtn.append(qualifier);
		}
	}
}