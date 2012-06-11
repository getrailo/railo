/**
 * Implements the CFML Function expandpath
 */
package railo.runtime.functions.system;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ArrayUtil;

public final class ExpandPath implements Function {

	private static final long serialVersionUID = 6192659914120397912L;

	public static String call(PageContext pc , String realPath) throws ExpressionException {
		
		ConfigWeb config=pc.getConfig();
		realPath=realPath.replace('\\','/');
        Resource res;
        
        if(StringUtil.startsWith(realPath,'/')) {
        	PageContextImpl pci=(PageContextImpl) pc;
        	ConfigWebImpl cwi=(ConfigWebImpl) config;
        	Resource[] reses = cwi.getPhysicalResources(pc,pc.getApplicationContext().getMappings(),realPath,false,pci.useSpecialMappings(),true);
        	if(!ArrayUtil.isEmpty(reses)) {
        		// first check for existing
	        	for(int i=0;i<reses.length;i++){
	        		if(reses[i].exists()) {
	        			return toReturnValue(realPath,reses[i]);
	        		}
	        	}
	        	return toReturnValue(realPath,reses[0]);
        	}
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