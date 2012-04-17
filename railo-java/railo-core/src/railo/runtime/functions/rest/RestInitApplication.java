package railo.runtime.functions.rest;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.op.Caster;
import railo.runtime.rest.Mapping;

public class RestInitApplication {

	public static String call(PageContext pc , String dirPath) throws PageException {
		return call(pc, dirPath, null);
	}
	public static String call(PageContext pc , String dirPath, String serviceMapping) throws PageException {
		if(StringUtil.isEmpty(serviceMapping,true)){
			serviceMapping=pc.getApplicationContext().getName();
		}
		
		
		Resource dir = Caster.toResource(dirPath,false);
		pc.getConfig().getSecurityManager().checkFileLocation(dir);
		if(!dir.isDirectory())
			throw new FunctionException(pc, "RestInitApplication", 1, "dirPath", "argument value ["+dirPath+"] must contain a existing directory");
		
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
				if(!dir.equals(mapping.getPhysical())) {
					change(mapping,dir);
				}
				mapping.reset(pc);
				hasResetted=true;
			}
		}
		if(!hasResetted) {
			create(dir,serviceMapping);
		}
	
		return null;
	}
	private static void create(Resource dir, String serviceMapping) throws SecurityException {
		throw new SecurityException("You cannot create REST mappings by using the function RestInitApplication, instead; please use the Railo Administrator");
	}
	private static void change(Mapping mapping, Resource dir) throws SecurityException {
		throw new SecurityException("You cannot modify REST mappings by using the function RestInitApplication, instead; please use the Railo Administrator");
	}
	
}
