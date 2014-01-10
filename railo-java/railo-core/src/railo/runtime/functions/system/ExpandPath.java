/**
 * Implements the CFML Function expandpath
 */
package railo.runtime.functions.system;

import java.io.IOException;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ArrayUtil;

public final class ExpandPath implements Function {

	private static final long serialVersionUID = 6192659914120397912L;

	public static String call(PageContext pc , String realPath) throws PageException {
		ConfigWeb config=pc.getConfig();
		realPath=realPath.replace('\\','/');

        String contextPath = pc.getHttpServletRequest().getContextPath();
        if ( !StringUtil.isEmpty( contextPath ) && realPath.startsWith( contextPath ) ) {
            boolean sws=StringUtil.startsWith(realPath, '/');
        	realPath = realPath.substring( contextPath.length() );
            if(sws && !StringUtil.startsWith(realPath, '/'))
            	realPath="/"+realPath;
        }

        Resource res;
        
        if(StringUtil.startsWith(realPath,'/')) {
        	PageContextImpl pci=(PageContextImpl) pc;
        	ConfigWebImpl cwi=(ConfigWebImpl) config;
        	PageSource[] sources = cwi.getPageSources(pci, pc.getApplicationContext().getMappings(), realPath, 
        			false, pci.useSpecialMappings(), true);
        	
        	if(!ArrayUtil.isEmpty(sources)) {
        		// first check for existing
	        	for(int i=0;i<sources.length;i++){
	        		if(sources[i].exists()) {
	        			return toReturnValue(realPath,sources[i].getResource());
	        		}
	        	}
	        	
	        	// no expand needed
	        	if(!SystemUtil.isWindows() && !sources[0].exists()) {
	        		res=pc.getConfig().getResource(realPath);
	                if(res.exists()) {
	                	return toReturnValue(realPath,res);
	                }
	        	}
	        	for(int i=0;i<sources.length;i++){
	        		res=sources[i].getResource();
	        		if(res!=null) {
	        			return toReturnValue(realPath,res);
	        		}
	        	}
        	}

        	// no expand needed
        	else if(!SystemUtil.isWindows()) {
        		res=pc.getConfig().getResource(realPath);
                if(res.exists()) {
                	return toReturnValue(realPath,res);
                }
        	}
        	
        	
        	//Resource[] reses = cwi.getPhysicalResources(pc,pc.getApplicationContext().getMappings(),realPath,false,pci.useSpecialMappings(),true);
        	
        }
        realPath=ConfigWebUtil.replacePlaceholder(realPath, config);
        res=pc.getConfig().getResource(realPath);
        if(res.isAbsolute()) return toReturnValue(realPath,res);
        
        res=ResourceUtil.getResource(pc,pc.getBasePageSource());
        if(!res.isDirectory())res=res.getParentResource();
        res = res.getRealResource(realPath);
        return toReturnValue(realPath,res);
        
	}

    private static String toReturnValue(String realPath,Resource res) {
        String path;
        char pathChar='/';
        try {
            path=res.getCanonicalPath();
            pathChar=ResourceUtil.FILE_SEPERATOR;
        } catch (IOException e) {
            path= res.getAbsolutePath();
        }
        boolean pathEndsWithSep=StringUtil.endsWith(path,pathChar);
        boolean realEndsWithSep=StringUtil.endsWith(realPath,'/');
        
        if(realEndsWithSep) {
            if(!pathEndsWithSep)path=path+pathChar;
        }
        else if(pathEndsWithSep) {
            path=path.substring(0,path.length()-1);
        }
        
        return path;
    }
}