package railo.runtime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.io.FileUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.lang.ArchiveClassLoader;
import railo.commons.lang.PhysicalClassLoader;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpTablePro;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.op.Caster;

/**  
 * Mapping class
 */
public final class MappingImpl implements Mapping {

    



	private static final Object NULL = new Object();
	private String virtual;
    private String lcVirtual;
    private boolean topLevel;
    private boolean trusted;
    private final boolean physicalFirst;
    private ArchiveClassLoader archiveClassLoader;
    private PhysicalClassLoader physicalClassLoader;
    private Resource archive;
    
    private boolean hasArchive;
    private ConfigImpl config;
    private Resource classRootDirectory;
    private PageSourcePool pageSourcePool=new PageSourcePool();
    
    private boolean readonly=false;
    private boolean hidden=false;
    private String strArchive;
    
    private String strPhysical;
    private Resource physical;
    //private boolean hasPhysical;
    
    private String lcVirtualWithSlash;
    //private Resource classRoot;
    private Map<String,Object> customTagPath=new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
    //private final Map<String,Object> customTagPath=new HashMap<String, Object>();
	private int classLoaderMaxElements=1000;
	/**
	 * @return the classLoaderMaxElements
	 */
	public int getClassLoaderMaxElements() {
		return classLoaderMaxElements;
	}

	private boolean appMapping;
	private boolean ignoreVirtual;

    public MappingImpl(ConfigImpl config, String virtual, String strPhysical,String strArchive, boolean trusted, 
            boolean physicalFirst, boolean hidden, boolean readonly,boolean topLevel, boolean appMapping,boolean ignoreVirtual) {
    	this(config, virtual, strPhysical, strArchive, trusted, physicalFirst, hidden, readonly,topLevel,appMapping,ignoreVirtual,5000);
    	
    }

    /**
     * @param configServer 
     * @param config
     * @param virtual
     * @param strPhysical
     * @param strArchive
     * @param trusted
     * @param physicalFirst
     * @param hidden
     * @param readonly
     * @throws IOException
     */
    public MappingImpl(ConfigImpl config, String virtual, String strPhysical,String strArchive, boolean trusted, 
            boolean physicalFirst, boolean hidden, boolean readonly,boolean topLevel, boolean appMapping, boolean ignoreVirtual, int classLoaderMaxElements) {
    	this.ignoreVirtual=ignoreVirtual;
    	this.config=config;
        this.hidden=hidden;
        this.readonly=readonly;
        this.strPhysical=strPhysical;
        this.strArchive=StringUtil.isEmpty(strArchive)?null:strArchive;
        this.trusted=trusted;
        this.topLevel=topLevel;
        this.appMapping=appMapping;
        this.physicalFirst=physicalFirst;
        this.classLoaderMaxElements=classLoaderMaxElements;
        
        // virtual
        if(virtual.length()==0)virtual="/";
        if(!virtual.equals("/") && virtual.endsWith("/"))this.virtual=virtual.substring(0,virtual.length()-1);
        else this.virtual=virtual;
        this.lcVirtual=this.virtual.toLowerCase();
        this.lcVirtualWithSlash=lcVirtual.endsWith("/")?this.lcVirtual:this.lcVirtual+'/';

        if(!(config instanceof ConfigWebImpl)) return;
        ConfigWebImpl cw=(ConfigWebImpl) config;
        
        // Physical
        physical=ConfigWebUtil.getExistingResource(cw.getServletContext(),strPhysical,null,config.getConfigDir(),FileUtil.TYPE_DIR,
                config);
        // Archive
        archive=ConfigWebUtil.getExistingResource(cw.getServletContext(),strArchive,null,config.getConfigDir(),FileUtil.TYPE_FILE,
                config);
        if(archive!=null) {
            try {
                archiveClassLoader = new ArchiveClassLoader(archive,getClass().getClassLoader());
            } 
            catch (Throwable t) {
                archive=null;
            }
        }
        hasArchive=archive!=null;
        
       //if(!hasArchive && !hasPhysical) throw new IOException("missing physical and archive path, one of them must be defined");
    }
    
    /**
     * @see railo.runtime.Mapping#getClassLoaderForArchive()
     */
    public ClassLoader getClassLoaderForArchive() {
        return archiveClassLoader;
    }
    
	/**
     * @see railo.runtime.Mapping#getClassLoaderForPhysical(boolean)
     */
	public synchronized ClassLoader getClassLoaderForPhysical(boolean reload) throws IOException {//if(reload)print.ds();
		// first access
		if(physicalClassLoader==null){
			physicalClassLoader=new PhysicalClassLoader(getClassRootDirectory(),getClass().getClassLoader());
			return physicalClassLoader;
		}
		// return existing classloader when not to big
		if(!reload){
			if(classLoaderMaxElements>physicalClassLoader.count()) 
				return physicalClassLoader;
			
			getConfigImpl().getMappingLogger().info(getVirtual(), "max size ["+classLoaderMaxElements+"] for classloader reached");
		}
		// reset existing classloader
		config.resetRPCClassLoader();
		physicalClassLoader=new PhysicalClassLoader(getClassRootDirectory(),getClass().getClassLoader());
        pageSourcePool.clearPages();
		System.gc();
        return physicalClassLoader;
	}
	
	/**
	 * returns null when classlader is not created, use getClassLoaderForPhysical(boolean) to touch classloader
	 * @return
	 * @throws IOException
	 */
	public ClassLoader getClassLoaderForPhysical() throws IOException {
		return physicalClassLoader;
	}
	
	
    
    /**
     * @see railo.runtime.Mapping#getPhysical()
     */
    public Resource getPhysical() {
    	return physical;
    }

    /**
     * @see railo.runtime.Mapping#getVirtualLowerCase()
     */
    public String getVirtualLowerCase() {
        return lcVirtual;
    }
    /**
     * @see railo.runtime.Mapping#getVirtualLowerCaseWithSlash()
     */
    public String getVirtualLowerCaseWithSlash() {
        return lcVirtualWithSlash;
    }

    /**
     * @see railo.runtime.Mapping#getArchive()
     */
    public Resource getArchive() {
        //initArchive();
        return archive;
    }

    /**
     * @see railo.runtime.Mapping#hasArchive()
     */
    public boolean hasArchive() {
        return hasArchive;
    }
    
    /**
     * @see railo.runtime.Mapping#hasPhysical()
     */
    public boolean hasPhysical() {
        return physical!=null;
    }

    /**
     * @see railo.runtime.Mapping#getClassRootDirectory()
     */
    public Resource getClassRootDirectory() {
        if(classRootDirectory==null) {
        	String path=getPhysical()!=null?
        			getPhysical().getAbsolutePath():
        			getArchive().getAbsolutePath();
        	
        	classRootDirectory=config.getDeployDirectory().getRealResource(
                                        StringUtil.toIdentityVariableName(
                                        		path)
                                );
        }
        return classRootDirectory;
    }
    
    /**
     * clones a mapping and make it readOnly
     * @param config
     * @return cloned mapping
     * @throws IOException
     */
    public MappingImpl cloneReadOnly(ConfigImpl config) {
    	return new MappingImpl(config,virtual,strPhysical,strArchive,trusted,physicalFirst,hidden,true,topLevel,appMapping,ignoreVirtual,classLoaderMaxElements);
    }
    
    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		maxlevel--;
        
		DumpTable htmlBox = new DumpTablePro("mapping","#ff6600","#ffcc99","#000000");
		htmlBox.setTitle("Mapping");
		htmlBox.appendRow(1,new SimpleDumpData("virtual"),new SimpleDumpData(virtual));
		htmlBox.appendRow(1,new SimpleDumpData("physical"),DumpUtil.toDumpData(strPhysical,pageContext,maxlevel,dp));
		htmlBox.appendRow(1,new SimpleDumpData("archive"),DumpUtil.toDumpData(strArchive,pageContext,maxlevel,dp));
		htmlBox.appendRow(1,new SimpleDumpData("trusted"),new SimpleDumpData(Caster.toString(trusted)));
		htmlBox.appendRow(1,new SimpleDumpData("physicalFirst"),new SimpleDumpData(Caster.toString(physicalFirst)));
		htmlBox.appendRow(1,new SimpleDumpData("readonly"),new SimpleDumpData(Caster.toString(readonly)));
		htmlBox.appendRow(1,new SimpleDumpData("hidden"),new SimpleDumpData(Caster.toString(hidden)));
		htmlBox.appendRow(1,new SimpleDumpData("appmapping"),new SimpleDumpData(Caster.toBoolean(appMapping)));
		htmlBox.appendRow(1,new SimpleDumpData("toplevel"),new SimpleDumpData(Caster.toString(topLevel)));
		htmlBox.appendRow(1,new SimpleDumpData("ClassLoaderMaxElements"),new SimpleDumpData(Caster.toString(classLoaderMaxElements)));
		return htmlBox;
    }

    /**
     * @see railo.runtime.Mapping#getPageSource(java.lang.String)
     */
    public PageSource getPageSource(String realPath) {
    	boolean isOutSide = false;
		realPath=realPath.replace('\\','/');
		if(realPath.indexOf('/')!=0) {
		    if(realPath.startsWith("../")) {
				isOutSide=true;
			}
			else if(realPath.startsWith("./")) {
				realPath=realPath.substring(1);
			}
			else {
				realPath="/"+realPath;
			}
		}
		return getPageSource(realPath,isOutSide);
    }
    
    /**
     * @see railo.runtime.Mapping#getPageSource(java.lang.String, boolean)
     */
    public PageSource getPageSource(String path, boolean isOut) {
        PageSource source=pageSourcePool.getPageSource(path,true);
        if(source!=null) return source;

        PageSourceImpl newSource = new PageSourceImpl(this,path,isOut);
        pageSourcePool.setPage(path,newSource);
        
        return newSource;//new PageSource(this,path);
    }
    
    /**
     * @return Returns the pageSourcePool.
     */
    public PageSourcePool getPageSourcePool() {
        return pageSourcePool;
    }

    /**
     * @see railo.runtime.Mapping#check()
     */
    public void check() {
        if(config instanceof ConfigServer) return;
        ConfigWebImpl cw=(ConfigWebImpl) config;
        // Physical
        if(getPhysical()==null && strPhysical!=null && strPhysical.length()>0) {
            physical=ConfigWebUtil.getExistingResource(cw.getServletContext(),strPhysical,null,config.getConfigDir(),FileUtil.TYPE_DIR,config);
            
        }
        // Archive
        if(getArchive()==null && strArchive!=null && strArchive.length()>0) {
            try {
                archive=ConfigWebUtil.getExistingResource(cw.getServletContext(),strArchive,null,config.getConfigDir(),FileUtil.TYPE_FILE,
                        config);
                if(archive!=null) {
                    try {
                        archiveClassLoader = new ArchiveClassLoader(archive,getClass().getClassLoader());
                    } 
                    catch (MalformedURLException e) {
                        archive=null;
                    }
                }
                hasArchive=archive!=null;
            } 
            catch (IOException e) {}
        }
    }

    /**
     * @see railo.runtime.Mapping#getConfig()
     */
    public Config getConfig() {
        return config;
    }
    
    public ConfigImpl getConfigImpl() {
        return config;
    }

    /**
     * @see railo.runtime.Mapping#isHidden()
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @see railo.runtime.Mapping#isPhysicalFirst()
     */
    public boolean isPhysicalFirst() {
        return physicalFirst || archive==null;
    }

    /**
     * @see railo.runtime.Mapping#isReadonly()
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * @see railo.runtime.Mapping#getStrArchive()
     */
    public String getStrArchive() {
        return strArchive;
    }

    /**
     * @see railo.runtime.Mapping#getStrPhysical()
     */
    public String getStrPhysical() {
        return strPhysical;
    }

    /**
     * @see railo.runtime.Mapping#isTrusted()
     */
    public boolean isTrusted() {
        return trusted;
    }

    /**
     * @see railo.runtime.Mapping#getVirtual()
     */
    public String getVirtual() {
        return virtual;
    }

	public boolean isAppMapping() {
		return appMapping;
	}


	public boolean isTopLevel() {
		return topLevel;
	}

	/*public PageSource getCustomTagPath(String name, boolean doCustomTagDeepSearch) {
		String lcName=name.toLowerCase().trim();
		Object o = customTagPath.get(lcName);

		if(o==null){
			PageSource ps=searchFor(name, lcName, doCustomTagDeepSearch);
			if(ps!=null){
				customTagPath.put(lcName,ps);
				return ps;
			}
			
			customTagPath.put(lcName,NULL);
			return null;
			
		}
		else if(o==NULL) return null;
		
		return (PageSource) o;
	}*/
	
	public PageSource getCustomTagPath(String name, boolean doCustomTagDeepSearch) {
		return searchFor(name, name.toLowerCase().trim(), doCustomTagDeepSearch);
	}
	
	public boolean ignoreVirtual(){
		return ignoreVirtual;
	}
	
	
	private PageSource searchFor(String filename, String lcName, boolean doCustomTagDeepSearch) {
		if(!hasPhysical()) return null;

		
		PageSource source=getPageSource(filename);
		if(isOK(source)) {
    		return source;
    	}
    	customTagPath.remove(lcName);
    	
    	if(doCustomTagDeepSearch){
    		String path = _getRecursive(getPhysical(),null, filename);
        	if(path!=null ) {
        		source=getPageSource(path);
        		if(isOK(source)) {
            		return source;
            	}
        		customTagPath.remove(lcName);
        	}
    	}
    	return null;
	}

	public static boolean isOK(PageSource ps) {
		//return ps!=null;
		return (ps.getMapping().isTrusted() && ((PageSourceImpl)ps).isLoad()) || ps.exists();
	} 
	
	private static String _getRecursive(Resource res, String path, String filename) {
    	if(res.isDirectory()) {
    		Resource[] children = res.listResources(new ExtensionResourceFilter(new String[]{".cfm",".cfc"},true,true));
    		if(path!=null)path+=res.getName()+"/";
    		else path="";
    		String tmp;
    		for(int i=0;i<children.length;i++){
    			tmp= _getRecursive(children[i], path, filename);
    			if(tmp!=null) return tmp;
    		}
    	}
    	else if(res.isFile()) {
    		if(res.getName().equalsIgnoreCase(filename)) return path+res.getName();
    	}
    	return null;    	
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "StrPhysical:"+getStrPhysical()+";"+
		 "StrArchive:"+getStrArchive()+";"+
		 "Virtual:"+getVirtual()+";"+
		 "Archive:"+getArchive()+";"+
		 "Physical:"+getPhysical()+";"+
		 "topLevel:"+topLevel+";"+
		 "trusted:"+trusted+";"+
		 "physicalFirst:"+physicalFirst+";"+
		 "readonly:"+readonly+";"+
		 "hidden:"+hidden+";";
	}
}