package railo.runtime.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.rest.path.Path;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ListUtil;

public class RestUtil {
	
	public static String[] splitPath(String path) {
		return ListUtil.listToStringArray(path, '/');
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

	public static boolean isMatch(PageContext pc,Mapping mapping, Resource res) {
		Resource p = mapping.getPhysical();
		if(p!=null){
			return p.equals(res);
		}
		return ResourceUtil.toResourceNotExisting(pc, mapping.getStrPhysical()).equals(res);
	}

	/*public static void main(String[] args) {
		Struct res=new StructImpl();
		Source src = new Source(null,null,"test1/{a: \\d+}-{b}/");
		String[] callerPath=splitPath("/test1/1-muster/mueller");
		print.e(matchPath(res,src.getPath(), callerPath));
		print.e(res);
	}*/

}
