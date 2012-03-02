package railo.runtime.rest;

import railo.commons.lang.Pair;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.Collection.Key;

public class RestUtil {

	private static final Collection.Key HTTP_METHOD = KeyImpl.getInstance("httpmethod");

	public static Pair<Key, UDF> getUDFNameFor(PageContext pc, Component component, String path, Pair<Collection.Key,UDF> defaultValue) {
		String method = pc.getHttpServletRequest().getMethod();
		Key[] keys = component.keys();
		Object value;
		UDF udf;
		Struct meta;
		for(int i=0;i<keys.length;i++){
			value=component.get(keys[i],null);
			if(value instanceof UDF){
				udf=(UDF)value;
				try {
					meta = udf.getMetaData(pc);
					String httpMethod = Caster.toString(meta.get(HTTP_METHOD,null),null);
					if(StringUtil.isEmpty(httpMethod) || httpMethod.equalsIgnoreCase(method)) continue;
					
					
					return new Pair<Collection.Key,UDF>(keys[i], udf);
				} 
				catch (PageException e) {}
				
			}
		}
		
		return defaultValue;
	}

}
