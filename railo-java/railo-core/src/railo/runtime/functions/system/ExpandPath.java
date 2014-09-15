/**
 * Implements the CFML Function expandpath
 */
package railo.runtime.functions.system;

import java.io.IOException;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
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

	public static String call(PageContext pc , String relPath) throws PageException {
		ConfigWeb config=pc.getConfig();
		relPath=prettifyPath(pc,relPath);
		
        String contextPath = pc.getHttpServletRequest().getContextPath();
        if ( !StringUtil.isEmpty( contextPath ) && relPath.startsWith( contextPath ) ) {
            boolean sws=StringUtil.startsWith(relPath, '/');
        	relPath = relPath.substring( contextPath.length() );
            if(sws && !StringUtil.startsWith(relPath, '/'))
            	relPath="/"+relPath;
        }

        Resource res;
        
        if(StringUtil.startsWith(relPath,'/')) {
        	
        	
        	PageContextImpl pci=(PageContextImpl) pc;
        	ConfigWebImpl cwi=(ConfigWebImpl) config;
        	PageSource[] sources = cwi.getPageSources(pci, pc.getApplicationContext().getMappings(), relPath, 
        			false, pci.useSpecialMappings(), true);
        	
        	if(!ArrayUtil.isEmpty(sources)) {
        		// first check for existing
	        	for(int i=0;i<sources.length;i++){
	        		if(sources[i].exists()) {
	        			return toReturnValue(relPath,sources[i].getResource());
	        		}
	        	}
	        	
	        	// no expand needed
	        	if(!SystemUtil.isWindows() && !sources[0].exists()) {
	        		res=pc.getConfig().getResource(relPath);
	                if(res.exists()) {
	                	return toReturnValue(relPath,res);
	                }
	        	}
	        	for(int i=0;i<sources.length;i++){
	        		res=sources[i].getResource();
	        		if(res!=null) {
	        			return toReturnValue(relPath,res);
	        		}
	        	}
        	}

        	// no expand needed
        	else if(!SystemUtil.isWindows()) {
        		res=pc.getConfig().getResource(relPath);
                if(res.exists()) {
                	return toReturnValue(relPath,res);
                }
        	}
        	
        	
        	//Resource[] reses = cwi.getPhysicalResources(pc,pc.getApplicationContext().getMappings(),relPath,false,pci.useSpecialMappings(),true);
        	
        }
        relPath=ConfigWebUtil.replacePlaceholder(relPath, config);
        res=pc.getConfig().getResource(relPath);
        if(res.isAbsolute()) return toReturnValue(relPath,res);
        
        res=ResourceUtil.getResource(pc,pc.getBasePageSource());
        if(!res.isDirectory())res=res.getParentResource();
        res = res.getRealResource(relPath);
        return toReturnValue(relPath,res);
        
	}

    private static String toReturnValue(String relPath,Resource res) {
        String path;
        char pathChar='/';
        try {
            path=res.getCanonicalPath();
            pathChar=ResourceUtil.FILE_SEPERATOR;
        } catch (IOException e) {
            path= res.getAbsolutePath();
        }
        boolean pathEndsWithSep=StringUtil.endsWith(path,pathChar);
        boolean realEndsWithSep=StringUtil.endsWith(relPath,'/');
        
        if(realEndsWithSep) {
            if(!pathEndsWithSep)path=path+pathChar;
        }
        else if(pathEndsWithSep) {
            path=path.substring(0,path.length()-1);
        }
        
        return path;
    }
    
    private static String prettifyPath(PageContext pc, String path) {
		if(path==null) return null;
		
		// UNC Path
		if(path.startsWith("\\\\") && SystemUtil.isWindows()) {
			path=path.substring(2);
			path=path.replace('\\','/');
			return "//"+StringUtil.replace(path, "//", "/", false);
		}
		
		path=path.replace('\\','/');
		
		// virtual file system path
		int index=path.indexOf("://");
		if(index!=-1) {
			ResourceProvider[] providers = pc.getConfig().getResourceProviders();
			String scheme=path.substring(0,index).toLowerCase().trim();
			for(int i=0;i<providers.length;i++) {
				if(scheme.equalsIgnoreCase(providers[i].getScheme()))
					return scheme+"://"+StringUtil.replace(path.substring(index+3), "//", "/", false);
			}
		}

		return StringUtil.replace(path, "//", "/", false);
		// TODO /aaa/../bbb/
	}
}