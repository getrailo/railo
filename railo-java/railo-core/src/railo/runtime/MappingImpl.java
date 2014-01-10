package railo.runtime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.io.FileUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.lang.ArchiveClassLoader;
import railo.commons.lang.PCLCollection;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ArrayUtil;

/**  
 * Mapping class
 */
public final class MappingImpl implements Mapping {

	private static final long serialVersionUID = 6431380676262041196L;
	
	//private static final Object NULL = new Object();
	private String virtual;
    private String lcVirtual;
    private boolean topLevel;
    private short inspect;
    private boolean physicalFirst;
    private ArchiveClassLoader archiveClassLoader;
    //private PhysicalClassLoader physicalClassLoader;
    private PCLCollection pclCollection;
    private Resource archive;
    
    private boolean hasArchive;
    private Config config;
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

	private ApplicationListener appListener;

    public MappingImpl(Config config, String virtual, String strPhysical,String strArchive, short inspect, 
            boolean physicalFirst, boolean hidden, boolean readonly,boolean topLevel, boolean appMapping,boolean ignoreVirtual,ApplicationListener appListener) {
    	this(config, virtual, strPhysical, strArchive, inspect, physicalFirst, hidden, readonly,topLevel,appMapping,ignoreVirtual,appListener,5000);
    	
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
    public MappingImpl(Config config, String virtual, String strPhysical,String strArchive, short inspect, 
            boolean physicalFirst, boolean hidden, boolean readonly,boolean topLevel, boolean appMapping, boolean ignoreVirtual,ApplicationListener appListener, int classLoaderMaxElements) {
    	this.ignoreVirtual=ignoreVirtual;
    	this.config=config;
        this.hidden=hidden;
        this.readonly=readonly;
        this.strPhysical=StringUtil.isEmpty(strPhysical)?null:strPhysical;
        this.strArchive=StringUtil.isEmpty(strArchive)?null:strArchive;
        this.inspect=inspect;
        this.topLevel=topLevel;
        this.appMapping=appMapping;
        this.physicalFirst=physicalFirst;
        this.appListener=appListener;
        this.classLoaderMaxElements=classLoaderMaxElements;
        
        // virtual
        if(virtual.length()==0)virtual="/";
        if(!virtual.equals("/") && virtual.endsWith("/"))this.virtual=virtual.substring(0,virtual.length()-1);
        else this.virtual=virtual;
        this.lcVirtual=this.virtual.toLowerCase();
        this.lcVirtualWithSlash=lcVirtual.endsWith("/")?this.lcVirtual:this.lcVirtual+'/';

        //if(!(config instanceof ConfigWebImpl)) return;
        //ConfigWebImpl cw=(ConfigWebImpl) config;
        ServletContext cs = (config instanceof ConfigWebImpl)?((ConfigWebImpl)config).getServletContext():null;
        
        
        // Physical
        physical=ConfigWebUtil.getExistingResource(cs,strPhysical,null,config.getConfigDir(),FileUtil.TYPE_DIR,
                config);
        // Archive
        archive=ConfigWebUtil.getExistingResource(cs,strArchive,null,config.getConfigDir(),FileUtil.TYPE_FILE,
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

        if(archive==null) this.physicalFirst=true;
        else if(physical==null) this.physicalFirst=false;
        else this.physicalFirst=physicalFirst;
        
        
        //if(!hasArchive && !hasPhysical) throw new IOException("missing physical and archive path, one of them must be defined");
    }
    
    @Override
    public ClassLoader getClassLoaderForArchive() {
        return archiveClassLoader;
    }
    
    public PCLCollection touchPCLCollection() throws IOException {
    	
    	if(pclCollection==null){
    		pclCollection=new PCLCollection(this,getClassRootDirectory(),getConfig().getClassLoader(),classLoaderMaxElements);
		}
    	return pclCollection;
    }
    
	public PCLCollection getPCLCollection() {
		return pclCollection;
	}

    
    

	/**
	 * remove all Page from Pool using this classloader
	 * @param cl
	 */
	public void clearPages(ClassLoader cl){
		pageSourcePool.clearPages(cl);
	}
	
    @Override
    public Resource getPhysical() {
    	return physical;
    }

    @Override
    public String getVirtualLowerCase() {
        return lcVirtual;
    }
    @Override
    public String getVirtualLowerCaseWithSlash() {
        return lcVirtualWithSlash;
    }

    @Override
    public Resource getArchive() {
        //initArchive();
        return archive;
    }

    @Override
    public boolean hasArchive() {
        return hasArchive;
    }
    
    @Override
    public boolean hasPhysical() {
        return physical!=null;
    }

    @Override
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
    	return new MappingImpl(config,virtual,strPhysical,strArchive,inspect,physicalFirst,hidden,true,topLevel,appMapping,ignoreVirtual,appListener,classLoaderMaxElements);
    }
    
    @Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		maxlevel--;
        
		
		
		DumpTable htmlBox = new DumpTable("mapping","#ff6600","#ffcc99","#000000");
		htmlBox.setTitle("Mapping");
		htmlBox.appendRow(1,new SimpleDumpData("virtual"),new SimpleDumpData(virtual));
		htmlBox.appendRow(1,new SimpleDumpData("physical"),DumpUtil.toDumpData(strPhysical,pageContext,maxlevel,dp));
		htmlBox.appendRow(1,new SimpleDumpData("archive"),DumpUtil.toDumpData(strArchive,pageContext,maxlevel,dp));
		htmlBox.appendRow(1,new SimpleDumpData("inspect"),new SimpleDumpData(ConfigWebUtil.inspectTemplate(getInspectTemplateRaw(),"")));
		htmlBox.appendRow(1,new SimpleDumpData("physicalFirst"),new SimpleDumpData(Caster.toString(physicalFirst)));
		htmlBox.appendRow(1,new SimpleDumpData("readonly"),new SimpleDumpData(Caster.toString(readonly)));
		htmlBox.appendRow(1,new SimpleDumpData("hidden"),new SimpleDumpData(Caster.toString(hidden)));
		htmlBox.appendRow(1,new SimpleDumpData("appmapping"),new SimpleDumpData(Caster.toBoolean(appMapping)));
		htmlBox.appendRow(1,new SimpleDumpData("toplevel"),new SimpleDumpData(Caster.toString(topLevel)));
		htmlBox.appendRow(1,new SimpleDumpData("ClassLoaderMaxElements"),new SimpleDumpData(Caster.toString(classLoaderMaxElements)));
		return htmlBox;
    }

    /**
     * inspect template setting (Config.INSPECT_*), if not defined with the mapping the config setting is returned
     * @return
     */
    public short getInspectTemplate() {
		if(inspect==ConfigImpl.INSPECT_UNDEFINED) return config.getInspectTemplate();
		return inspect;
	}
    
    /**
     * inspect template setting (Config.INSPECT_*), if not defined with the mapping, Config.INSPECT_UNDEFINED is returned
     * @return
     */
    public short getInspectTemplateRaw() {
		return inspect;
	}
    
    
	

	@Override
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
    
    @Override
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

    @Override
    public void check() {
        //if(config instanceof ConfigServer) return;
        //ConfigWebImpl cw=(ConfigWebImpl) config;
        ServletContext cs = (config instanceof ConfigWebImpl)?((ConfigWebImpl)config).getServletContext():null;
        
        
        // Physical
        if(getPhysical()==null && strPhysical!=null && strPhysical.length()>0) {
            physical=ConfigWebUtil.getExistingResource(cs,strPhysical,null,config.getConfigDir(),FileUtil.TYPE_DIR,config);
            
        }
        // Archive
        if(getArchive()==null && strArchive!=null && strArchive.length()>0) {
            try {
                archive=ConfigWebUtil.getExistingResource(cs,strArchive,null,config.getConfigDir(),FileUtil.TYPE_FILE,
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

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean isPhysicalFirst() {
        return physicalFirst;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public String getStrArchive() {
        return strArchive;
    }

    @Override
    public String getStrPhysical() {
        return strPhysical;
    }

    @Override
    @Deprecated
    public boolean isTrusted() {
        return getInspectTemplate()==ConfigImpl.INSPECT_NEVER;
    }

    @Override
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
		return (ps.getMapping().isTrusted() && ((PageSourceImpl)ps).isLoad()) || ps.exists();
	}

	public static PageSource isOK(PageSource[] arr) {
		if(ArrayUtil.isEmpty(arr)) return null;
		for(int i=0;i<arr.length;i++) {
			if(isOK(arr[i])) return arr[i];
		}
		return null;
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
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return "StrPhysical:"+getStrPhysical()+";"+
		 "StrArchive:"+getStrArchive()+";"+
		 "Virtual:"+getVirtual()+";"+
		 "Archive:"+getArchive()+";"+
		 "Physical:"+getPhysical()+";"+
		 "topLevel:"+topLevel+";"+
		 "inspect:"+ConfigWebUtil.inspectTemplate(getInspectTemplateRaw(),"")+";"+
		 "physicalFirst:"+physicalFirst+";"+
		 "readonly:"+readonly+";"+
		 "hidden:"+hidden+";";
	}

	public ApplicationListener getApplicationListener() {
		if(appListener!=null) return appListener;
		return config.getApplicationListener();
	}
}