/**
 * Implements the CFML Function expandpath
 */
package railo.runtime.functions.system;


import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.ext.function.Function;

public final class ContractPath implements Function {
	public static String call(PageContext pc , String absPath) {
		return call(pc, absPath,false);
	}
	
	public static String call(PageContext pc , String absPath, boolean placeHolder) {
		Resource res = ResourceUtil.toResourceNotExisting(pc, absPath);
		if(!res.exists()) return absPath;
		
		if(placeHolder){
			String cp = SystemUtil.addPlaceHolder(res, null);
			if(!StringUtil.isEmpty(cp))return cp;
		}
		
		//Config config=pc.getConfig();
		PageSource ps = pc.toPageSource(res,null);
		if(ps==null) return absPath;
		
		String realPath = ps.getRealpath();
		realPath=realPath.replace('\\', '/');
		if(StringUtil.endsWith(realPath,'/'))realPath=realPath.substring(0,realPath.length()-1);
		
		String mapping=ps.getMapping().getVirtual();
		mapping=mapping.replace('\\', '/');
		if(StringUtil.endsWith(mapping,'/'))mapping=mapping.substring(0,mapping.length()-1);
		
		return mapping+realPath;
	}
}