package railo.runtime.listener;

import railo.runtime.exp.PageException;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.List;
import railo.runtime.util.ApplicationContext;

public class ApplicationContextUtil {
	/**
	 * translate int definition of script protect to string definition
	 * @param scriptProtect
	 * @return
	 */
	public static String translateScriptProtect(int scriptProtect) {
		if(scriptProtect==ApplicationContext.SCRIPT_PROTECT_NONE) return "none";
		if(scriptProtect==ApplicationContext.SCRIPT_PROTECT_ALL) return "all";
		
		ArrayImpl arr=new ArrayImpl();
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_CGI)>0) arr.add("cgi");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_COOKIE)>0) arr.add("cookie");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)>0) arr.add("form");
		if((scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)>0) arr.add("url");
		
		
		
		try {
			return List.arrayToList(arr, ",");
		} catch (PageException e) {
			return "none";
		} 
	}
	

	/**
	 * translate string definition of script protect to int definition
	 * @param scriptProtect
	 * @return
	 */
	public static int translateScriptProtect(String strScriptProtect) {
		strScriptProtect=strScriptProtect.toLowerCase().trim();
		
		if("none".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		if("no".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		if("false".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_NONE;
		
		if("all".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		if("true".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		if("yes".equals(strScriptProtect)) return ApplicationContext.SCRIPT_PROTECT_ALL;
		
		String[] arr = List.listToStringArray(strScriptProtect, ',');
		String item;
		int scriptProtect=0;
		for(int i=0;i<arr.length;i++) {
			item=arr[i].trim();
			if("cgi".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_CGI)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_CGI;
			else if("cookie".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_COOKIE)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_COOKIE;
			else if("form".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_FORM)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_FORM;
			else if("url".equals(item) && (scriptProtect&ApplicationContext.SCRIPT_PROTECT_URL)==0)
				scriptProtect+=ApplicationContext.SCRIPT_PROTECT_URL;
			
		}
		
		return scriptProtect;
	}
}
