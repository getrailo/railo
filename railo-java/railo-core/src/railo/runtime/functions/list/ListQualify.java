package railo.runtime.functions.list;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

/**
 * Implements the CFML Function listqualify
 */
public final class ListQualify implements Function {
	
	private static final long serialVersionUID = -7450079285934992224L;
	
	public static String call(PageContext pc , String list, String qualifier) {
		return call(pc,list,qualifier,",","all", false, false);
	}

	public static String call(PageContext pc , String list, String qualifier, String delimiter) {
		return call(pc,list,qualifier,delimiter,"all", false, false);
	}

	public static String call(PageContext pc , String list, String qualifier, String delimiter, String elements) {
		return call(pc, list, qualifier, delimiter, elements, false, false);
	}
	
	public static String call(PageContext pc , String list, String qualifier, String delimiter, String elements, boolean includeEmptyFields) {
		return call(pc, list, qualifier, delimiter, elements, includeEmptyFields, false);
	}
	
	public static String call(PageContext pc , String list, String qualifier, String delimiter, String elements, boolean includeEmptyFields, 
			boolean psq // this is used only internally by railo, search for "PSQ-BIF" in code
			) {
	   	
		if(list.length()==0) return "";
		if(psq)list=StringUtil.replace(list, "'", "''", false);
		
	   	Array arr=includeEmptyFields?ListUtil.listToArray(list,delimiter):ListUtil.listToArrayRemoveEmpty(list,delimiter);
		
		boolean isQChar=qualifier.length()==1;
		boolean isDChar=delimiter.length()==1;
		
		if(isQChar && isDChar) return doIt(arr,qualifier.charAt(0),delimiter.charAt(0),elements);
		else if(isQChar && !isDChar) return doIt(arr,qualifier.charAt(0),delimiter,elements);
		else if(!isQChar && isDChar) return doIt(arr,qualifier,delimiter.charAt(0),elements);
		else return doIt(arr,qualifier,delimiter,elements);
		
	}

    private static String doIt(Array arr, char qualifier, char delimiter, String elements) {
        StringBuffer rtn=new StringBuffer();
        int len=arr.size();
        
		if(StringUtil.toLowerCase(elements).equals("all")) {
			rtn.append(qualifier);
			rtn.append(arr.get(1,""));
			rtn.append(qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimiter);
				rtn.append(qualifier);
				rtn.append(arr.get(i,""));
				rtn.append(qualifier);
			}
		}
		else {
			qualifyString(rtn,arr.get(1,"").toString(),qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimiter);
				qualifyString(rtn,arr.get(i,"").toString(),qualifier);
			}
		}
		return rtn.toString();
    }
    private static String doIt(Array arr, char qualifier, String delimiter, String scope) {
        StringBuffer rtn=new StringBuffer();
        int len=arr.size();
        
		if(StringUtil.toLowerCase(scope).equals("all")) {
			rtn.append(qualifier);
			rtn.append(arr.get(1,""));
			rtn.append(qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimiter);
				rtn.append(qualifier);
				rtn.append(arr.get(i,""));
				rtn.append(qualifier);
			}
		}
		else {
			qualifyString(rtn,arr.get(1,"").toString(),qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimiter);
				qualifyString(rtn,arr.get(i,"").toString(),qualifier);
			}
		}
		return rtn.toString();
    }
    private static String doIt(Array arr, String qualifier, char delimiter, String scope) {
        StringBuffer rtn=new StringBuffer();
        int len=arr.size();
        
		if(StringUtil.toLowerCase(scope).equals("all")) {
			rtn.append(qualifier);
			rtn.append(arr.get(1,""));
			rtn.append(qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimiter);
				rtn.append(qualifier);
				rtn.append(arr.get(i,""));
				rtn.append(qualifier);
			}
		}
		else {
			qualifyString(rtn,arr.get(1,"").toString(),qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimiter);
				qualifyString(rtn,arr.get(i,"").toString(),qualifier);
			}
		}
		return rtn.toString();
    }
    private static String doIt(Array arr, String qualifier, String delimiter, String scope) {
        StringBuffer rtn=new StringBuffer();
        int len=arr.size();
        
		if(StringUtil.toLowerCase(scope).equals("all")) {
			rtn.append(qualifier);
			rtn.append(arr.get(1,""));
			rtn.append(qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimiter);
				rtn.append(qualifier);
				rtn.append(arr.get(i,""));
				rtn.append(qualifier);
			}
		}
		else {
			qualifyString(rtn,arr.get(1,"").toString(),qualifier);
			for(int i=2;i<=len;i++) {
				rtn.append(delimiter);
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