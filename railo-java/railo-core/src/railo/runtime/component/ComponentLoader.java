package railo.runtime.component;

import java.util.Map;

import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.ComponentImpl;
import railo.runtime.ComponentPage;
import railo.runtime.InterfaceImpl;
import railo.runtime.InterfacePage;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.debug.DebugEntry;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;

public class ComponentLoader {
	
    public static ComponentImpl loadComponentImpl(PageContext pc,String rawPath) throws PageException  {
    	rawPath=rawPath.trim().replace('\\','/');
    	
    	//try{
	    	RefBoolean isRealPath=new RefBooleanImpl(false);
    		String path=getPath(rawPath, true, true);
	    	Page page=getPage(pc, path.concat(".cfc"),isRealPath);
	    	if(page==null)	{
		    	path=getPath(rawPath, false, true);
		    	page=getPage(pc, path.concat(".cfc"),isRealPath);
		    	if(page==null)	{
		    		path=getPath(rawPath, true, false);
			    	page=getPage(pc, path.concat(".cfc"),isRealPath);
			    	if(page==null)	{
			    		path=getPath(rawPath, false, false);
			    		page=getPage(pc, path.concat(".cfc"),isRealPath);
			    		
			    	}
			    }
		    }
		    
		    // test
		    if(page==null){
		    	//String detail="search for "+ps.getDisplayPath();
		        //if(psRel!=null)detail+=" and "+psRel.getDisplayPath();
		    	throw new ExpressionException("invalid component definition, can't find "+rawPath);
		    }
	    	return loadComponentImpl(pc,page,page.getPageSource(),trim(path.replace('/', '.')),isRealPath.toBooleanValue());
	
	    
	}
    
    private static String trim(String str) {
    	if(StringUtil.startsWith(str, '.'))str=str.substring(1);
		return str;
	}

	private static String getPath(String path, boolean allowRemovingExt,boolean replacePoint) {
    	// remove extension
    	if(allowRemovingExt && StringUtil.endsWithIgnoreCase(path, ".cfc")){
    		path=path.substring(0,path.length()-4);
    	}
    	
    	// replace point
    	if(replacePoint && path.indexOf("./")==-1)
    		path=path.replace('.','/');
	    
    	return path;
	}

	private static Page getPage(PageContext pc,String path, RefBoolean isRealPath) throws PageException  {
    	Page page=null;
	    isRealPath.setValue(!StringUtil.startsWith(path,'/'));
	    
	    PageSource ps=pc.getRelativePageSource(path);
	    if(ps==null) return null;
	    page=ps.loadPage(pc.getConfig(),null);
    	if(page==null && isRealPath.toBooleanValue()) {
    	    isRealPath.setValue(false);
        	ps=pc.getPageSource('/'+path);
    	    page=ps.loadPage(pc.getConfig(),null);
        }
    	return page;
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
                if(page==null)page=ps.loadPage(pc.getConfig());
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
            	if(page==null)page=ps.loadPage(pc.getConfig());
            	rtn=initComponent(pc,page,callPath,isRealPath);
            }
            finally {
                pc.removeLastPageSource(true);
            } 
        }
        //print.err("ps:"+ps);
        //print.err("curr:"+pc.getCurrentPageSource());
		
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
	    	page=res.loadPage(pc.getConfig(),null);
		    if(page==null && isRealPath) {
		    	isRealPath=false;
	        	PageSource resOld = res;
	        	res=pc.getPageSource('/'+path);
	    	    page=res.loadPage(pc.getConfig(),null);
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
            	if(page==null)page=ps.loadPage(pc.getConfig());
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
            	if(page==null)page=ps.loadPage(pc.getConfig());
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
