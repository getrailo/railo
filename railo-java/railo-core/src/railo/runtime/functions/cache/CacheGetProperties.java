package railo.runtime.functions.cache;

import java.io.IOException;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.util.ListUtil;

/**
 * 
 */
public final class CacheGetProperties implements Function {
	
	private static final long serialVersionUID = -8665995702411192700L;

	public static Array call(PageContext pc) throws PageException {
		return call(pc, null);
	}
	
	public static Array call(PageContext pc, String cacheName) throws PageException {
		Array arr = new ArrayImpl();
		try {
			if(StringUtil.isEmpty(cacheName)){
				addDefault(pc,ConfigImpl.CACHE_DEFAULT_OBJECT,arr);
				addDefault(pc,ConfigImpl.CACHE_DEFAULT_TEMPLATE,arr);
				addDefault(pc,ConfigImpl.CACHE_DEFAULT_QUERY,arr);
				addDefault(pc,ConfigImpl.CACHE_DEFAULT_RESOURCE,arr);
				addDefault(pc,ConfigImpl.CACHE_DEFAULT_FUNCTION,arr);
				//arr.appendEL(Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_TEMPLATE).getCustomInfo());
				//arr.appendEL(Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_QUERY).getCustomInfo());
				//arr.appendEL(Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_RESOURCE).getCustomInfo());
				// MUST welcher muss zuers sein
			}
			else{
				String name;
				String[] names=ListUtil.listToStringArray(cacheName, ',');
				for(int i=0;i<names.length;i++){
					name=names[i].trim();
					if(name.equalsIgnoreCase("template"))
						arr.appendEL(Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_TEMPLATE).getCustomInfo());
					else if(name.equalsIgnoreCase("object"))
						arr.appendEL(Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_OBJECT).getCustomInfo());
					else if(name.equalsIgnoreCase("query"))
						arr.appendEL(Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_QUERY).getCustomInfo());
					else if(name.equalsIgnoreCase("resource"))
						arr.appendEL(Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_RESOURCE).getCustomInfo());
					else if(name.equalsIgnoreCase("function"))
						arr.appendEL(Util.getDefault(pc,ConfigImpl.CACHE_DEFAULT_FUNCTION).getCustomInfo());
					else
						arr.appendEL(Util.getCache(pc.getConfig(),name).getCustomInfo());
				}
			}
			
			
			return arr;
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	private static void addDefault(PageContext pc, int type, Array arr) {
		try {
			arr.appendEL(Util.getDefault(pc,type).getCustomInfo());
		} catch (IOException e) {}
	}
}