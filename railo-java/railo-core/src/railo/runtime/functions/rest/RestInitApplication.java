package railo.runtime.functions.rest;


import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebAdmin;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.rest.Mapping;
import railo.runtime.rest.RestUtil;

public class RestInitApplication {

	public static String call(PageContext pc , String dirPath) throws PageException {
		return _call(pc, dirPath, null,null,null);
	}
	
	public static String call(PageContext pc , String dirPath, String serviceMapping) throws PageException {
		return _call(pc, dirPath, serviceMapping, null,null);
	}

	public static String call(PageContext pc , String dirPath, String serviceMapping, boolean defaultMapping) throws PageException {
		return _call(pc, dirPath, serviceMapping, defaultMapping, null);
	}

	public static String call(PageContext pc , String dirPath, String serviceMapping, boolean defaultMapping, String webAdminPassword) throws PageException {
		return _call(pc, dirPath, serviceMapping, defaultMapping, webAdminPassword);
	}
	
	public static String _call(PageContext pc , String dirPath, String serviceMapping, Boolean defaultMapping, String webAdminPassword) throws PageException {
		if(StringUtil.isEmpty(serviceMapping,true)){
			serviceMapping=pc.getApplicationContext().getName();
		}
		Resource dir=RestDeleteApplication.toResource(pc,dirPath);
		
		ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();
		Mapping[] mappings = config.getRestMappings();
		Mapping mapping;
		
		// id is mapping name
		
		String virtual=serviceMapping.trim();
		if(!virtual.startsWith("/")) virtual="/"+virtual;
		if(!virtual.endsWith("/")) virtual+="/";
		boolean hasResetted=false;
		for(int i=0;i<mappings.length;i++){
			mapping=mappings[i];
			if(mapping.getVirtualWithSlash().equals(virtual)){
				// directory has changed
				if(!RestUtil.isMatch(pc, mapping, dir) || (defaultMapping!=null && mapping.isDefault()!=defaultMapping.booleanValue())) {
					update(pc,dir,virtual,RestDeleteApplication.getPassword(pc,webAdminPassword),defaultMapping==null?mapping.isDefault():defaultMapping.booleanValue());
				}
				mapping.reset(pc);
				hasResetted=true;
			}
		}
		if(!hasResetted) {
			update(pc,dir,virtual,RestDeleteApplication.getPassword(pc,webAdminPassword),defaultMapping==null?false:defaultMapping.booleanValue());
		}
	
		return null;
	}

	private static void update(PageContext pc,Resource dir, String virtual, String webAdminPassword, boolean defaultMapping) throws PageException {
		try {
			ConfigWebAdmin admin = ConfigWebAdmin.newInstance((ConfigWebImpl)pc.getConfig(),webAdminPassword);
			admin.updateRestMapping(virtual, dir.getAbsolutePath(), defaultMapping);
			admin.store();
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
	
}
