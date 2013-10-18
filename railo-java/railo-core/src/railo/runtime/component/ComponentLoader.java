package railo.runtime.component;

import java.util.Map;

import javax.servlet.jsp.tagext.BodyContent;

import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.DirectoryResourceFilter;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.OrResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.ComponentPage;
import railo.runtime.InterfaceImpl;
import railo.runtime.InterfacePage;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.PageSourceImpl;
import railo.runtime.config.ConfigImpl;
import railo.runtime.debug.DebugEntryTemplate;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.writer.BodyContentUtil;

public class ComponentLoader {
	

    private static final ResourceFilter DIR_OR_EXT=new OrResourceFilter(new ResourceFilter[]{DirectoryResourceFilter.FILTER,new ExtensionResourceFilter(".cfc")});

	public static ComponentImpl loadComponent(PageContext pc,PageSource child,String rawPath, Boolean searchLocal, Boolean searchRoot) throws PageException  {
    	return (ComponentImpl)load(pc,child, rawPath, searchLocal, searchRoot,null,false);
    }

    public static InterfaceImpl loadInterface(PageContext pc,PageSource child,String rawPath, Map interfaceUDFs) throws PageException  {
    	return (InterfaceImpl)load(pc,child, rawPath, Boolean.TRUE, Boolean.TRUE,interfaceUDFs,false);
    }
	
    public static Page loadPage(PageContext pc,PageSource child,String rawPath, Boolean searchLocal, Boolean searchRoot) throws PageException  {
    	return (Page)load(pc, child,rawPath, searchLocal, searchRoot,null,true);
    }
    
    
    private static Object load(PageContext pc,PageSource child,String rawPath, Boolean searchLocal, Boolean searchRoot, Map interfaceUDFs, boolean returnPage) throws PageException  {
    	ConfigImpl config=(ConfigImpl) pc.getConfig();
    	boolean doCache=config.useComponentPathCache();
    	
    	//print.o(rawPath);
    	//app-String appName=pc.getApplicationContext().getName();
    	rawPath=rawPath.trim().replace('\\','/');
    	String path=(rawPath.indexOf("./")==-1)?rawPath.replace('.','/'):rawPath;
    	String pathWithCFC=path.concat(".cfc");
    	boolean isRealPath=!StringUtil.startsWith(pathWithCFC,'/');
    	PageSource currPS = pc.getCurrentPageSource();
    	Page currP=((PageSourceImpl)currPS).loadPage(pc,(Page)null);
    	
    	PageSource ps=null;
    	Page page=null;
    	
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
	    	if(doCache){
	    		page=config.getCachedPage(pc, localCacheName);
	    		if(page!=null) return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
	    	}
	    }
	    
    	// check import cache
    	if(doCache && isRealPath){
    		ImportDefintion impDef = config.getComponentDefaultImport();
	    	ImportDefintion[] impDefs=currP==null?new ImportDefintion[0]:currP.getImportDefintions();
	    	int i=-1;
	    	do{
	    		
	    		if(impDef.isWildcard() || impDef.getName().equalsIgnoreCase(path)){
	    			page=config.getCachedPage(pc, "import:"+impDef.getPackageAsPath()+pathWithCFC);
	    			if(page!=null) return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
	    			
	    			//app-page=config.getCachedPage(pc, "import:"+appName+":"+impDef.getPackageAsPath()+pathWithCFC);
	    			//app-if(page!=null) return load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
		    	}
		    	impDef=++i<impDefs.length?impDefs[i]:null;
	    	}
	    	while(impDef!=null);
    	}
    	
    	if(doCache) {
	    	// check global in cache
	    	page=config.getCachedPage(pc, pathWithCFC);
	    	if(page!=null) return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
	    	
	    	// get pages from application mappings
	    	//app-page=config.getCachedPage(pc, ":"+appName+":"+pathWithCFC);
	    	//app-if(page!=null) return load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
    	}
    	
    // SEARCH
    	// search from local
    	if(searchLocal && isRealPath)	{
    		// check realpath
    		PageSource[] arr = ((PageContextImpl)pc).getRelativePageSources(pathWithCFC);
    		page=PageSourceImpl.loadPage(pc, arr,null);
			if(page!=null){
				if(doCache)config.putCachedPageSource(localCacheName, page.getPageSource());
				return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
			}
    	}
    	
    	// search with imports
    	Mapping[] cMappings = config.getComponentMappings();
    	
    	if(isRealPath){

    		ImportDefintion impDef = config.getComponentDefaultImport();
	    	ImportDefintion[] impDefs=currP==null?new ImportDefintion[0]:currP.getImportDefintions();
	    	PageSource[] arr;

    		
	    	int i=-1;
	    	do{
	    		if(impDef.isWildcard() || impDef.getName().equalsIgnoreCase(path)){
	    			
	    			// search from local first
	    			if(searchLocal){
	    				arr = ((PageContextImpl)pc).getRelativePageSources(impDef.getPackageAsPath()+pathWithCFC);
	    				page=PageSourceImpl.loadPage(pc, arr,null);
	    				if(page!=null)	{
			    			if(doCache)config.putCachedPageSource("import:"+impDef.getPackageAsPath()+pathWithCFC, page.getPageSource());
			    			return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
			        	}
	    			}
	    			
	    			// search local component mappings
	    			/*app-if(lcMappings!=null) {
			    		Mapping m;
			        	for(int y=0;y<lcMappings.length;y++){
			        		m=lcMappings[y];
			        		String key=appName+":"+impDef.getPackageAsPath()+pathWithCFC;
		    	    		ps=m.getPageSource(impDef.getPackageAsPath()+pathWithCFC);
			        		page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
				    		if(page!=null)	{    
				    			if(doCache)config.putCachedPageSource("import:"+key, page.getPageSource());
				    			return load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
				        	}
			        	}
		        	}*/
	    			
	    			// search mappings and webroot
	    			page=PageSourceImpl.loadPage(pc, ((PageContextImpl)pc).getPageSources("/"+impDef.getPackageAsPath()+pathWithCFC), null);
	    			if(page!=null){
	    	    		String key=impDef.getPackageAsPath()+pathWithCFC;
	    	    		if(doCache && !((MappingImpl)page.getPageSource().getMapping()).isAppMapping())
	    	    			config.putCachedPageSource("import:"+key, page.getPageSource());
	    				return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
	    	    	}
		    		
	    			// search component mappings
		    		Mapping m;
		        	for(int y=0;y<cMappings.length;y++){
		        		m=cMappings[y];
		        		ps=m.getPageSource(impDef.getPackageAsPath()+pathWithCFC);
		        		page=((PageSourceImpl)ps).loadPage(pc,(Page)null);
			    		if(page!=null)	{    
			    			if(doCache)config.putCachedPageSource("import:"+impDef.getPackageAsPath()+pathWithCFC, page.getPageSource());
			    			return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
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
    	

		
    	// search local component mappings
    	/* app-if(lcMappings!=null) {
	    	Mapping m;
	    	for(int i=0;i<lcMappings.length;i++){
	    		m=lcMappings[i];
	    		ps=m.getPageSource(p);
	    		page=((PageSourceImpl)ps).loadPage(pc,pc.getConfig(),null);
	    		String key=":"+appName+":"+pathWithCFC;
	    		if(page!=null){
	    			if(doCache)config.putCachedPageSource(key, page.getPageSource());
	    			return load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
	        	}
	    	}
    	}*/
    	
    	// search mappings and webroot
    	page=PageSourceImpl.loadPage(pc,((PageContextImpl)pc).getPageSources(p),null);
    	if(page!=null){
    		String key=pathWithCFC;
    		if(doCache && !((MappingImpl)page.getPageSource().getMapping()).isAppMapping())
    			config.putCachedPageSource(key, page.getPageSource());
			return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
    	}
		
    	// search component mappings
    	Mapping m;
    	for(int i=0;i<cMappings.length;i++){
    		m=cMappings[i];
    		ps=m.getPageSource(p);
    		page=((PageSourceImpl)ps).loadPage(pc,(Page)null);
    		
    		// recursive search
    		if(page==null && config.doComponentDeepSearch() && m.hasPhysical() && path.indexOf('/')==-1) {
    			String _path=getPagePath(pc, m.getPhysical(), null,pathWithCFC,DirectoryResourceFilter.FILTER);
    			if(_path!=null) {
    				ps=m.getPageSource(_path);
        			page=((PageSourceImpl)ps).loadPage(pc,(Page)null);
        			doCache=false;// do not cache this, it could be ambigous
    			}
    		}
    		
    		if(page!=null){
    			if(doCache)config.putCachedPageSource(pathWithCFC, page.getPageSource());
    			return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
        	}
    	}
    	

    	// search relative to active cfc (this get not cached because the cache get ambigous if we do)
    	if(searchLocal && isRealPath)	{
    		if(child==null) {
    			Component cfc = pc.getActiveComponent();
    			if(cfc!=null) child = cfc.getPageSource();
    		}
    		
    		
    		if(child!=null) {
	    		ps=child.getRealPage(pathWithCFC);
	    		if(ps!=null) {
					page=((PageSourceImpl)ps).loadPage(pc,(Page)null);
	
					if(page!=null){
						return returnPage?page:load(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath,interfaceUDFs);
					}
				}
    		}
    	}
    	
    	// translate cfide. to org.railo.cfml
    	if(StringUtil.startsWithIgnoreCase(rawPath, "cfide.")) {
    		String rpm="org.railo.cfml."+rawPath.substring(6);
    		try{
    			return load(pc,child,rpm, searchLocal, searchRoot, interfaceUDFs,returnPage);
        	}
        	catch(ExpressionException ee){
        		throw new ExpressionException("invalid "+(interfaceUDFs==null?"component":"interface")+" definition, can't find "+rawPath+" or "+rpm);
        	}
    	}
    	throw new ExpressionException("invalid "+(interfaceUDFs==null?"component":"interface")+" definition, can't find "+rawPath);
    	
		
    	
	}

    private static String getPagePath(PageContext pc, Resource res, String dir,String name, ResourceFilter filter) {
		if(res.isFile()) {
			if(res.getName().equalsIgnoreCase(name)) {
				return dir+res.getName();
			}
		}
		else if(res.isDirectory()) {
			Resource[] _dir = res.listResources(filter);
			if(_dir!=null){
				if(dir==null) dir="/";
				else dir=dir+res.getName()+"/";
				String path;
				for(int i=0;i<_dir.length;i++){
					path=getPagePath(pc, _dir[i],dir, name,DIR_OR_EXT);
					if(path!=null) return path;
				}
			}
		}
		
		return null;
	}

	private static String trim(String str) {
    	if(StringUtil.startsWith(str, '.'))str=str.substring(1);
		return str;
	}

	/*private static Page getPage(PageContext pc,String path, RefBoolean isRealPath, boolean searchLocal) throws PageException  {
    	Page page=null;
	    isRealPath.setValue(!StringUtil.startsWith(path,'/'));
	    // search from local
	    PageSource[] arr;
	    
	    if(searchLocal && isRealPath.toBooleanValue()){
	    	arr = ((PageContextImpl)pc).getRelativePageSources(path);
		    page=PageSourceImpl.loadPage(pc, arr, null);
	    }
	    // search from root
	    if(page==null) {
	    	if(isRealPath.toBooleanValue()){
	    		isRealPath.setValue(false);
	    		arr=((PageContextImpl)pc).getPageSources('/'+path);
	    	}
	    	else {
	    		arr=((PageContextImpl)pc).getPageSources(path);
	    	}
    	    page=PageSourceImpl.loadPage(pc,arr,null);
        }
    	return page;
	}*/


	//

	public static ComponentImpl loadComponent(PageContext pc,Page page, PageSource ps,String callPath, boolean isRealPath, boolean silent) throws PageException  {
		/*if(page==null && ps instanceof PageSourceImpl) {
			page=((PageSourceImpl)ps).getPage();
		}*/
		if(silent) {
			// TODO is there a more direct way
			BodyContent bc =  pc.pushBody();
			try {
				return loadComponent(pc,page,ps,callPath,isRealPath);
			}
			finally {
				BodyContentUtil.clearAndPop(pc, bc);
			}
		}
		return loadComponent(pc,page,ps,callPath,isRealPath);
	}
	

	private static Object load(PageContext pc,Page page, PageSource ps,String callPath, boolean isRealPath, Map interfaceUDFs) throws PageException  {
		if(interfaceUDFs==null) return loadComponent(pc,page, ps,callPath, isRealPath);
		return loadInterface(pc,page, ps, callPath, isRealPath, interfaceUDFs);
	}
	
	public static Page loadPage(PageContext pc,PageSource ps, boolean forceReload) throws PageException  {
		if(pc.getConfig().debug()) {
            DebugEntryTemplate debugEntry=pc.getDebugger().getEntry(pc,ps);
            pc.addPageSource(ps,true);
            
            int currTime=pc.getExecutionTime();
            long exeTime=0;
            long time=System.currentTimeMillis();
            try {
            	debugEntry.updateFileLoadTime((int)(System.currentTimeMillis()-time));
            	exeTime=System.currentTimeMillis();
                return ((PageSourceImpl)ps).loadPage(pc,forceReload);
            }
            finally {
                int diff= ((int)(System.currentTimeMillis()-exeTime)-(pc.getExecutionTime()-currTime));
                pc.setExecutionTime(pc.getExecutionTime()+(int)(System.currentTimeMillis()-time));
                debugEntry.updateExeTime(diff);
                pc.removeLastPageSource(true);
            }
        }
    // no debug
        pc.addPageSource(ps,true);
        try {   
        	return ((PageSourceImpl)ps).loadPage(pc,forceReload);
        }
        finally {
            pc.removeLastPageSource(true);
        } 
    }

	public static ComponentImpl loadComponent(PageContext pc,Page page, PageSource ps,String callPath, boolean isRealPath) throws PageException  {
        ComponentImpl rtn=null;
        if(pc.getConfig().debug()) {
            DebugEntryTemplate debugEntry=pc.getDebugger().getEntry(pc,ps);
            pc.addPageSource(ps,true);
            
            int currTime=pc.getExecutionTime();
            long exeTime=0;
            long time=System.nanoTime();
            try {
            	debugEntry.updateFileLoadTime((int)(System.nanoTime()-time));
            	exeTime=System.nanoTime();
                if(page==null)page=((PageSourceImpl)ps).loadPage(pc);
            	rtn=initComponent(pc,page,callPath,isRealPath);
                
                
            }
            finally {
            	if(rtn!=null)rtn.setLoaded(true);
            	int diff= ((int)(System.nanoTime()-exeTime)-(pc.getExecutionTime()-currTime));
                pc.setExecutionTime(pc.getExecutionTime()+(int)(System.nanoTime()-time));
                debugEntry.updateExeTime(diff);
                pc.removeLastPageSource(true);
            }
        }
    // no debug
        else {
            pc.addPageSource(ps,true);
            try {   
            	if(page==null)page=((PageSourceImpl)ps).loadPage(pc);
            	rtn=initComponent(pc,page,callPath,isRealPath);
            }
            finally {
            	if(rtn!=null)rtn.setLoaded(true);
                pc.removeLastPageSource(true);
            } 
        }
       
		return rtn;
    }
	
    public static InterfaceImpl loadInterface(PageContext pc,Page page, PageSource ps,String callPath, boolean isRealPath, Map interfaceUDFs) throws PageException  {
    	InterfaceImpl rtn=null;
        if(pc.getConfig().debug()) {
            DebugEntryTemplate debugEntry=pc.getDebugger().getEntry(pc,ps);
            pc.addPageSource(ps,true);
            
            int currTime=pc.getExecutionTime();
            long exeTime=0;
            long time=System.nanoTime();
            try {
                debugEntry.updateFileLoadTime((int)(System.nanoTime()-time));
                exeTime=System.nanoTime();
            	if(page==null)page=((PageSourceImpl)ps).loadPage(pc);
            	rtn=initInterface(pc,page,callPath,isRealPath,interfaceUDFs);
            }
            finally {
                int diff= ((int)(System.nanoTime()-exeTime)-(pc.getExecutionTime()-currTime));
                pc.setExecutionTime(pc.getExecutionTime()+(int)(System.nanoTime()-time));
                debugEntry.updateExeTime(diff);
                pc.removeLastPageSource(true);
            }
        }
    // no debug
        else {
            pc.addPageSource(ps,true);
            try {   
            	if(page==null)page=((PageSourceImpl)ps).loadPage(pc);
            	rtn=initInterface(pc,page,callPath,isRealPath,interfaceUDFs);
            }
            finally {
                pc.removeLastPageSource(true);
            } 
        }
        return rtn;
    }
    


	/* *
     * load a component
     * @param rawPath
     * @return loaded component
     * @throws PageException
     * /
    public static InterfaceImpl loadInterface(PageContext pc,String rawPath,boolean allowRemovingExt, Map interfaceUDFs) throws PageException  {
    	// MUSTMUST sync code with extends
    	
    	String fullName=rawPath;
    	boolean hasRemovedExt=false;
    	
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
	            	
	            	if(StringUtil.startsWithIgnoreCase(rawPath, "cfide.")) {
	            		String rpm="org.railo.cfml."+rawPath.substring(6);
	            		try{
	            			return loadInterface(pc,rpm,allowRemovingExt, interfaceUDFs);
		            	}
		            	catch(ExpressionException ee){
		            		throw new ExpressionException("invalid interface definition, can't find "+rawPath+" or "+rpm,detail);
		            	}
	            	}
	            	else throw new ExpressionException("invalid interface definition, can't find "+rawPath,detail);
	            	
	    			
	    	    }
	        }
	    return loadInterface(pc,page,res,fullName.replace('/', '.'),isRealPath,interfaceUDFs);
	}*/
    

	
	

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
