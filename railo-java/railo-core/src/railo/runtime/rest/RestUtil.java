package railo.runtime.rest;

import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.Collection.Key;

public class RestUtil {

	private static final Collection.Key HTTP_METHOD = KeyImpl.getInstance("httpmethod");

	public static Pair<Key, UDF> getUDFNameFor(PageContext pc, Component component, String path, Pair<Collection.Key,UDF> defaultValue) {
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
	}

	public static boolean matchPath(String path, String cfcPath) {
		if(!cfcPath.startsWith("/")) cfcPath="/"+cfcPath;
		
		return true;
	}
	
	
	public static void main(String[] args) {
		matchPath("/test1/1-muster/mueller", "test1/{a: \\d+}-{b}");
	}
	
	

}
