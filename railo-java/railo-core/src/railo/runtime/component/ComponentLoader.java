package railo.runtime.component;

import java.util.Map;

import javax.servlet.jsp.tagext.BodyContent;

import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.ComponentImpl;
import railo.runtime.ComponentPage;
import railo.runtime.InterfaceImpl;
import railo.runtime.InterfacePage;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PagePlus;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.config.ConfigImpl;
import railo.runtime.debug.DebugEntry;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.writer.BodyContentUtil;

public class ComponentLoader {
	
    public static ComponentImpl loadComponentImpl(PageContext pc,String rawPath, Boolean searchLocal, Boolean searchRoot) throws PageException  {//, boolean isInterfaces
    	ConfigImpl config=(ConfigImpl) pc.getConfig();
    	//print.o(rawPath);
    	String appName=pc.getApplicationContext().getName();
    	rawPath=rawPath.trim().replace('\\','/');
    	String path=(rawPath.indexOf("./")==-1)?rawPath.replace('.','/'):rawPath;
    	String pathWithCFC=path.concat(".cfc");
    	boolean isRealPath=!StringUtil.startsWith(pathWithCFC,'/');
    	PageSource currPS = pc.getCurrentPageSource();
    	Page currP=((PageSourceImpl)currPS).loadPage(pc,pc.getConfig(),null);
    	
    	PageSource ps=null;
    	Page page=null;
	    PagePlus pp=(currP instanceof PagePlus)?(PagePlus) currP:null;
    	
	    if(searchLocal==null)
	    	searchLocal=Caster.toBoolean(config.getComponentLocalSearch());
	    if(searchRoot==null)
	    	searchRoot=Caster.toBoolean(config.getComponentRootSearch());
	    
	    //boolean searchLocal=config.getComponentLocalSearch();
    // CACHE
    	// check local in cache
	    String localCacheName=null;
	    if(searchLocal && isRealPath){
		    localCacheName=currPS.getDisplayPath().replace('\\', '/');
	    	localCacheName=localCacheName.substring(0,localCacheName.lastIndexOf('/')+1).concat(pathWithCFC);
	    	page=config.getCachedPage(pc, localCacheName);
	    	if(page!=null) return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
	    }
	    
    	// check import cache
    	if(isRealPath){
    		ImportDefintion impDef = config.getComponentDefaultImport();
	    	ImportDefintion[] impDefs=pp==null?new ImportDefintion[0]:pp.getImportDefintions();
	    	int i=-1;
	    	do{
	    		
	    		if(impDef.isWildcard() || impDef.getName().equalsIgnoreCase(path)){
	    			page=config.getCachedPage(pc, "import:"+impDef.getPackageAsPath()+pathWithCFC);
	    			if(page!=null) return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
	    			
	    			page=config.getCachedPage(pc, "import:"+appName+":"+impDef.getPackageAsPath()+pathWithCFC);
	    			if(page!=null) return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
		    	}
		    	impDef=++i<impDefs.length?impDefs[i]:null;
	    	}
	    	while(impDef!=null);
    	}
    	
    	
    	// check global in cache
    	page=config.getCachedPage(pc, pathWithCFC);
    	if(page!=null) return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
    	
    	// get pages from application mappings
    	page=config.getCachedPage(pc, ":"+appName+":"+pathWithCFC);
    	if(page!=null) return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
    	
    // SEARCH
    	// search from local
    	if(searchLocal && isRealPath)	{
    		// check realpath
	    	ps=pc.getRelativePageSource(pathWithCFC);
    		if(ps!=null) {
				page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);

				if(page!=null){
					config.putCachedPageSource(localCacheName, page.getPageSource());
					return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
				}
			}
    	}
    	
    	// search with imports
    	Mapping[] cMappings = config.getComponentMappings();
    	if(isRealPath){

    		ImportDefintion impDef = config.getComponentDefaultImport();
	    	ImportDefintion[] impDefs=pp==null?new ImportDefintion[0]:pp.getImportDefintions();
	    	
    		
	    	int i=-1;
	    	do{
	    		if(impDef.isWildcard() || impDef.getName().equalsIgnoreCase(path)){
	    			
	    			// search from local first
	    			if(searchLocal){
		    			ps=pc.getRelativePageSource(impDef.getPackageAsPath()+pathWithCFC);
		    			//print.o("ps1:"+ps.getDisplayPath());
			    		page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
			    		if(page!=null)	{
			    			config.putCachedPageSource("import:"+impDef.getPackageAsPath()+pathWithCFC, page.getPageSource());
			    			return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
			        	}
	    			}
	    			
	    			// search mappings and webroot
	    	    	ps=((PageContextImpl)pc).getPageSource("/"+impDef.getPackageAsPath()+pathWithCFC);
	    	    	page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
	    	    	if(page!=null){
	    	    		String key=impDef.getPackageAsPath()+pathWithCFC;
	    	    		if(((MappingImpl)ps.getMapping()).isAppMapping())key=appName+":"+key;
	    	    		
	    	    		config.putCachedPageSource("import:"+key, page.getPageSource());
	    				return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
	    	    	}
		    		
	    			// search component mappings
		    		Mapping m;
		        	for(int y=0;y<cMappings.length;y++){
		        		m=cMappings[y];
		        		ps=m.getPageSource(impDef.getPackageAsPath()+pathWithCFC);
		        		page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
			    		if(page!=null)	{    
			    			config.putCachedPageSource("import:"+impDef.getPackageAsPath()+pathWithCFC, page.getPageSource());
			    			return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
			        	}
		        	}
		    	}
		    	impDef=++i<impDefs.length?impDefs[i]:null;
	    	}
	    	while(impDef!=null);
	    	
    	}
    				
		String p;
    	if(isRealPath)	p='/'+pathWithCFC;
    	else p=pathWithCFC;
    	
    	// search mappings and webroot
    	ps=((PageContextImpl)pc).getPageSource(p);
    	page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
    	if(page!=null){
    		String key=((MappingImpl)ps.getMapping()).isAppMapping()?":"+appName+":"+pathWithCFC:pathWithCFC;
    		config.putCachedPageSource(key, page.getPageSource());
			return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
    	}
        
		
    	// search component mappings
    	Mapping m;
    	for(int i=0;i<cMappings.length;i++){
    		m=cMappings[i];
    		ps=m.getPageSource(p);
    		page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
    		
    		if(page!=null){
        		config.putCachedPageSource(pathWithCFC, page.getPageSource());
    			return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath);
        	}
    	}
    	
    	
		
    	throw new ExpressionException("invalid component definition, can't find "+rawPath);
	}
    
    private static String trim(String str) {
    	if(StringUtil.startsWith(str, '.'))str=str.substring(1);
		return str;
	}

	private static Page getPage(PageContext pc,String path, RefBoolean isRealPath, boolean searchLocal) throws PageException  {
    	Page page=null;
	    isRealPath.setValue(!StringUtil.startsWith(path,'/'));
	    PageSource ps;
	    // search from local
	    
	    if(searchLocal && isRealPath.toBooleanValue()){
	    	ps=pc.getRelativePageSource(path);
		    if(ps==null) return null;
		    page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
	    }
	    // search from root
	    if(page==null) {
	    	if(isRealPath.toBooleanValue()){
	    		isRealPath.setValue(false);
	    		ps=pc.getPageSource('/'+path);
	    	}
	    	else {
	    		ps=pc.getPageSource(path);
	    	}
    	    page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
        }
    	return page;
	}


	//

	public static ComponentImpl loadComponentImpl(PageContext pc,Page page, PageSource ps,String callPath, boolean isRealPath, boolean silent) throws PageException  {
		if(silent) {
			// TODO is there a more direct way
			BodyContent bc =  pc.pushBody();
			try {
				return loadComponentImpl(pc,page,ps,callPath,isRealPath);
			}
			finally {
				BodyContentUtil.clearAndPop(pc, bc);
			}
		}
		return loadComponentImpl(pc,page,ps,callPath,isRealPath);
	}

	public static ComponentImpl loadComponentImpl(PageContext pc,Page page, PageSource ps,String callPath, boolean isRealPath) throws PageException  {
        ComponentImpl rtn=null;
        if(pc.getConfig().debug()) {
            DebugEntry debugEntry=pc.getDebugger().getEntry(pc,ps);
            pc.addPageSource(ps,true);
            
            int currTime=pc.getExecutionTime();
            long exeTime=0;
            long time=System.currentTimeMillis();
            try {
            	debugEntry.updateFileLoadTime((int)(System.currentTimeMillis()-time));
            	exeTime=System.currentTimeMillis();
                if(page==null)page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig());
            	rtn=initComponent(pc,page,callPath,isRealPath);
                
                
            }
            finally {
                int diff= ((int)(System.currentTimeMillis()-exeTime)-(pc.getExecutionTime()-currTime));
                pc.setExecutionTime(pc.getExecutionTime()+(int)(System.currentTimeMillis()-time));
                debugEntry.updateExeTime(diff);
                pc.removeLastPageSource(true);
            }
        }
    // no debug
        else {
            pc.addPageSource(ps,true);
            try {   
            	if(page==null)page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig());
            	rtn=initComponent(pc,page,callPath,isRealPath);
            }
            finally {
                pc.removeLastPageSource(true);
            } 
        }
       
		return rtn;
    }
    


	/**
     * load a component
     * @param rawPath
     * @return loaded component
     * @throws PageException
     */
    public static InterfaceImpl loadInterface(PageContext pc,String rawPath,boolean allowRemovingExt, Map interfaceUDFs) throws PageException  {
    	String fullName=rawPath;
    	boolean hasRemovedExt=false;
    	if(allowRemovingExt && StringUtil.endsWithIgnoreCase(fullName, ".cfc")){
    		fullName=fullName.substring(0,fullName.length()-4);
    		hasRemovedExt=true;
    	}
    	fullName=fullName.trim().replace('\\','/').replace('.','/');
	    String path=fullName.concat(".cfc");
	    
	    
	    boolean isRealPath=!StringUtil.startsWith(fullName,'/');
	    
	    PageSource res=pc.getRelativePageSource(path);
	    Page page=null;
	    //try {
	    	page=((PageSourceImpl)res).loadPage(pc,pc.getConfig(),null);
		    if(page==null && isRealPath) {
		    	isRealPath=false;
	        	PageSource resOld = res;
	        	res=pc.getPageSource('/'+path);
	    	    page=((PageSourceImpl)res).loadPage(pc,pc.getConfig(),null);
	        	if(page==null) {
			    	if(hasRemovedExt)return loadInterface(pc,rawPath, false,interfaceUDFs);
	            	String detail="search for "+res.getDisplayPath();
	            	if(resOld!=null)detail+=" and "+resOld.getDisplayPath();
	    			throw new ExpressionException("invalid interface definition, can't find "+rawPath,detail);
	    	    }
	        }
	    return loadInterface(pc,page,res,fullName.replace('/', '.'),isRealPath,interfaceUDFs);
	}
    
    public static InterfaceImpl loadInterface(PageContext pc,Page page, PageSource ps,String callPath, boolean isRealPath, Map interfaceUDFs) throws PageException  {
    	InterfaceImpl rtn=null;
        if(pc.getConfig().debug()) {
            DebugEntry debugEntry=pc.getDebugger().getEntry(pc,ps);
            pc.addPageSource(ps,true);
            
            int currTime=pc.getExecutionTime();
            long exeTime=0;
            long time=System.currentTimeMillis();
            try {
                debugEntry.updateFileLoadTime((int)(System.currentTimeMillis()-time));
                exeTime=System.currentTimeMillis();
            	if(page==null)page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig());
            	rtn=initInterface(pc,page,callPath,isRealPath,interfaceUDFs);
            }
            finally {
                int diff= ((int)(System.currentTimeMillis()-exeTime)-(pc.getExecutionTime()-currTime));
                pc.setExecutionTime(pc.getExecutionTime()+(int)(System.currentTimeMillis()-time));
                debugEntry.updateExeTime(diff);
                pc.removeLastPageSource(true);
            }
        }
    // no debug
        else {
            pc.addPageSource(ps,true);
            try {   
            	if(page==null)page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig());
            	rtn=initInterface(pc,page,callPath,isRealPath,interfaceUDFs);
            }
            finally {
                pc.removeLastPageSource(true);
            } 
        }
        return rtn;
    }
	
	

    private static InterfaceImpl initInterface(PageContext pc,Page page,String callPath,boolean isRealPath, Map interfaceUDFs) throws PageException {
    	if(!(page instanceof InterfacePage))
			throw new ApplicationException("invalid interface definition ["+callPath+"]");
		InterfacePage ip=(InterfacePage)page;
		InterfaceImpl i = ip.newInstance(callPath,isRealPath,interfaceUDFs);
		return i;
	}
    
    private static ComponentImpl initComponent(PageContext pc,Page page,String callPath,boolean isRealPath) throws PageException {
    	if(!(page instanceof ComponentPage)){
			if(page instanceof InterfacePage)
				throw new ApplicationException("can not instantiate interface ["+callPath+"] as a component");
			throw new ApplicationException("invalid component definition ["+callPath+"]");
    	}
		
    	ComponentPage cp = (ComponentPage)page;
    	
		ComponentImpl c = cp.newInstance(pc,callPath,isRealPath);
        c.setInitalized(true);
        return c;
		
	}
}
