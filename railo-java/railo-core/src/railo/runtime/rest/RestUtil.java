package railo.runtime.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import railo.runtime.PageContext;
import railo.runtime.rest.path.Path;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;

public class RestUtil {

	public static final Collection.Key HTTP_METHOD = KeyImpl.getInstance("httpmethod");
	public static final Collection.Key REST = KeyImpl.getInstance("rest");
	public static final Collection.Key REST_PATH = KeyImpl.getInstance("restPath");
	public static final Collection.Key REST_ARG_SOURCE = KeyImpl.getInstance("restArgSource"); 

	/*public static Pair<Key, UDF> getUDFNameFor(PageContext pc, Component component, String path, Pair<Collection.Key,UDF> defaultValue) {
		String method = pc.getHttpServletRequest().getMethod();
		String[] arrPath=StringUtil.isEmpty(path)?new String[0]:List.listToStringArray(path, '/');
		Key[] keys = component.keys();
		Object value;
		UDF udf;
		FunctionArgument[] args;
		Struct meta;
		for(int i=0;i<keys.length;i++){
			value=component.get(keys[i],null);
			if(value instanceof UDF){
				udf=(UDF)value;
				try {
					meta = udf.getMetaData(pc);
					String httpMethod = Caster.toString(meta.get(HTTP_METHOD,null),null);
					if(StringUtil.isEmpty(httpMethod) || !httpMethod.equalsIgnoreCase(method)) continue;
					
					args = udf.getFunctionArguments();
					
					if(args.length==0 || arrPath.length==0){
						return new Pair<Collection.Key,UDF>(keys[i], udf);
					}
				} 
				catch (PageException e) {}
				
			}
		}
		
		return defaultValue;
	}*/
	
	
	
	
	
	public static String[] splitPath(String path) {
		return List.listToStringArray(path, '/');
	}
	
	
	/**
	 * check if caller path match the cfc path
	 * @param variables
	 * @param restPath
	 * @param callerPath
	 * @return match until which index of the given cfc path, returns -1 if there is no match
	 */
	public static int matchPath(Struct variables,Path[] restPath, String[] callerPath) {
		if(restPath.length>callerPath.length) return -1;
		
		int index=0;
		for(;index<restPath.length;index++){
			if(!restPath[index].match(variables,callerPath[index])) return -1;
		}
		return index-1;
	}


	public static void setStatus(PageContext pc,int status, String msg) {
		pc.clear();
		if(msg!=null) {
			try {
				pc.forceWrite(msg);
			} 
			catch (IOException e) {}
		}
		HttpServletResponse rsp = pc.getHttpServletResponse();
		rsp.setHeader("Connection", "close"); // IE unter IIS6, Win2K3 und Resin
		rsp.setStatus(status);
		
	}


	public static void release(Mapping[] mappings) {
		for(int i=0;i<mappings.length;i++){
			mappings[i].release();
		}
	}

	/*public static void main(String[] args) {
		Struct res=new StructImpl();
		Source src = new Source(null,null,"test1/{a: \\d+}-{b}/");
		String[] callerPath=splitPath("/test1/1-muster/mueller");
		print.e(matchPath(res,src.getPath(), callerPath));
		print.e(res);
	}*/

}
