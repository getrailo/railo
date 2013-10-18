package railo.runtime.component;

import railo.runtime.ComponentImpl;
import railo.runtime.InterfaceImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.exp.PageException;
import railo.runtime.type.util.ArrayUtil;

public class MetadataUtil {

    public static Page getPageWhenMetaDataStillValid(PageContext pc,ComponentImpl comp, boolean ignoreCache) throws PageException {
    	Page page = getPage(pc,comp._getPageSource());
    	if(ignoreCache) return page;

    	if(page.metaData!=null && page.metaData.get()!=null) {
    		if(hasChanged(pc,((MetaDataSoftReference)page.metaData).creationTime,comp)) {
    			page.metaData=null;
    		}
    	}
    	return page;
	}
    public static Page getPageWhenMetaDataStillValid(PageContext pc,InterfaceImpl interf, boolean ignoreCache) throws PageException {
    	Page page = getPage(pc,interf.getPageSource());
    	if(ignoreCache) return page;
    	
    	if(page.metaData!=null && page.metaData.get()!=null) {
    		if(hasChanged(pc,((MetaDataSoftReference)page.metaData).creationTime,interf))
    			page.metaData=null;
    	}
    	return page;
	}
    
	private static boolean hasChanged(PageContext pc,long lastMetaCreation, ComponentImpl cfc) throws PageException {
		if(cfc==null) return false;
		
		// check the component
		Page p = getPage(pc, cfc._getPageSource());
		if(hasChanged(p.getCompileTime(),lastMetaCreation)) return true;
		
		// check interfaces
		InterfaceCollection ic = cfc._interfaceCollection();
        if(ic!=null){
        	if(hasChanged(pc,lastMetaCreation,ic.getInterfaces())) return true;
        }
        
        // check base
		return hasChanged(pc, lastMetaCreation, (ComponentImpl)cfc._base());
	}
	
	private static boolean hasChanged(PageContext pc,long lastMetaCreation,InterfaceImpl[] interfaces) throws PageException {
		
		if(!ArrayUtil.isEmpty(interfaces)){
            for(int i=0;i<interfaces.length;i++){
            	if(hasChanged(pc,lastMetaCreation,interfaces[i])) return true;
            }
        }
		return false;
	}

	private static boolean hasChanged(PageContext pc,long lastMetaCreation, InterfaceImpl inter) throws PageException {
		Page p = getPage(pc, inter.getPageSource());
		if(hasChanged(p.getCompileTime(),lastMetaCreation)) return true;
    	return hasChanged(pc,lastMetaCreation,inter.getExtends());
	}
	

	private static boolean hasChanged(long compileTime, long lastMetaCreation) {
		return compileTime>lastMetaCreation;
	}
	
	private static Page getPage(PageContext pc,PageSource ps) throws PageException {
		Page page = ((PageSourceImpl)ps).getPage();
    	if(page==null) {
    		page = ps.loadPage(pc.getConfig());
    	}
		return page;
	}
}
