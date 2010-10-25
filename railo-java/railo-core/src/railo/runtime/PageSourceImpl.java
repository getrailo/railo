package railo.runtime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.PhysicalClassLoader;
import railo.commons.lang.SizeOf;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.engine.ThreadLocalPageSource;
import railo.runtime.exp.MissingIncludeException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.List;
import railo.runtime.type.Sizeable;

/**
 * represent a cfml file on the runtime system
 */
public final class PageSourceImpl implements SourceFile, PageSource, Sizeable {

    //public static final byte LOAD_NONE=1;
    public static final byte LOAD_ARCHIVE=2;
    public static final byte LOAD_PHYSICAL=3;
    
    //private byte load=LOAD_NONE;

	private final MappingImpl mapping;
    private final String realPath;
    
    private boolean isOutSide;
    
    private String className;
    private String packageName;
    private String javaName;

    private Resource physcalSource;
    private Resource archiveSource;
    private String fileName;
    private String compName;
    private PagePlus page;
	private long lastAccess;	
	private int accessCount=0;
    private boolean recompileAlways;
    private boolean recompileAfterStartUp;
    
    
    
    private PageSourceImpl() {
    	mapping=null;
        realPath=null;
    }
    
    
    /**
	 * constructor of the class
     * @param mapping
     * @param realPath
	 */
	PageSourceImpl(MappingImpl mapping,String realPath) {
		this.mapping=mapping;
        recompileAlways=mapping.getConfig().getCompileType()==Config.RECOMPILE_ALWAYS;
        recompileAfterStartUp=mapping.getConfig().getCompileType()==Config.RECOMPILE_AFTER_STARTUP || recompileAlways;
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
		this.realPath=realPath;
	    
	}
	
	/**
	 * private constructor of the class
	 * @param mapping
	 * @param realPath
	 * @param isOutSide
	 */
    PageSourceImpl(MappingImpl mapping, String realPath, boolean isOutSide) {
    	recompileAlways=mapping.getConfig().getCompileType()==Config.RECOMPILE_ALWAYS;
        recompileAfterStartUp=mapping.getConfig().getCompileType()==Config.RECOMPILE_AFTER_STARTUP || recompileAlways;
        this.mapping=mapping;
	    this.isOutSide=isOutSide;
		this.realPath=realPath;
		
	}

	
	/**
     * @see railo.runtime.PageSource#loadPage(railo.runtime.PageContext)
     */
	public Page loadPage(ConfigWeb config) throws PageException {
		return loadPage(null,config);
	}
	
	
	/**
	 * return page when already loaded, otherwise null
	 * @param pc
	 * @param config
	 * @return
	 * @throws PageException
	 */
	public Page getPage() {
		return page;
	}
	
	// FUTURE add to interface without config
	public Page loadPage(PageContext pc,ConfigWeb config) throws PageException {
		PagePlus page=this.page;
		if(mapping.isPhysicalFirst()) {
			page=loadPhysical(pc,page,config);
			if(page==null) page=loadArchive(page); 
	        if(page!=null) return page;
	    }
	    else {
	        page=loadArchive(page);
	        if(page==null)page=loadPhysical(pc,page,config);
	        if(page!=null) return page;
	    }
		throw new MissingIncludeException(this);
	    
	}
	

	/**
	 * @see railo.runtime.PageSource#loadPage(railo.runtime.PageContext, railo.runtime.Page)
	 */
	public Page loadPage(ConfigWeb config, Page defaultValue) throws PageException {
		return loadPage(null,config, defaultValue);
	}
	
	public Page loadPage(PageContext pc,ConfigWeb config, Page defaultValue) throws PageException {
		PagePlus page=this.page;
		if(mapping.isPhysicalFirst()) {
	        page=loadPhysical(pc,page,config);
	        if(page==null) page=loadArchive(page); 
	        if(page!=null) return page;
	    }
	    else {
	        page=loadArchive(page);
	        if(page==null)page=loadPhysical(pc,page,config);
	        if(page!=null) return page;
	    }
	    return defaultValue;
	}
	
    private PagePlus loadArchive(PagePlus page) {
    	if(!mapping.hasArchive()) return null;
		if(page!=null) return page;
        
        try {
            synchronized(this) {
                Class clazz=mapping.getClassLoaderForArchive().loadClass(getClazz());
                this.page=page=newInstance(clazz);
                page.setPageSource(this);
                //page.setTimeCreated(System.currentTimeMillis());
                page.setLoadType(LOAD_ARCHIVE);
    			////load=LOAD_ARCHIVE;
    			return page;
            }
        } 
        catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }
    

    private PagePlus loadPhysical(PageContext pc,PagePlus page, ConfigWeb config) throws PageException {
    	if(!mapping.hasPhysical()) return null;
    	
    	// FUTURE change interface loadPage to PageContext
    	pc=ThreadLocalPageContext.get(pc);
    	PageContextImpl pci=(PageContextImpl) pc;
    	//if(pc.isPageAlreadyUsed(page)) return page;
    	
    	if((mapping.isTrusted() || 
    			pc!=null && pci.isTrusted(page)) 
    		&& isLoad(LOAD_PHYSICAL) && !recompileAlways) return page;
        
    	Resource srcFile = getPhyscalFile();
    	
        long srcLastModified = srcFile.lastModified();
        
        if(srcLastModified==0L) return null;
    	
		// Page exists    
			if(page!=null && !recompileAlways) {
				// java file is newer !mapping.isTrusted() && 
				if(srcLastModified!=page.getSourceLastModified()) {
                	this.page=page=compile(config,mapping.getClassRootDirectory(),Boolean.TRUE);
					page.setPageSource(this);
					page.setLoadType(LOAD_PHYSICAL);
					/*if(srcLastModified!=page.getSourceLastModified()){
						srcFile.setLastModified(page.getSourceLastModified());// Future da class files die nach cfm unbenannt sind nicht den gleichen zeitstempel haben wie page.getSourceLastModified, kopiert railo die files immer wider, die klasse page braucht eine methode setSourceLastModified, dann kann dies hier entfernt werden
					}*/
				}
		    	
			}
		// page doesn't exist
			else {
                    
                ///synchronized(this) {
                    Resource classRootDir=mapping.getClassRootDirectory();
                    Resource classFile=classRootDir.getRealResource(getJavaName()+".class");
                    boolean isNew=false;
                    // new class
                    if(!classFile.exists() || recompileAfterStartUp) {
                    	this.page=page= compile(config,classRootDir,null);
                        isNew=true;
                    }
                    // load page
                    else {
                    	try {
                    		this.page=page=newInstance(mapping.getClassLoaderForPhysical(isNew).loadClass(getClazz()));
                        	
                        	
                        } 
                        // if there is a problem to load the existing version, it will be recompiled
                        catch (Throwable e) {
                        	this.page=page=compile(config,classRootDir,null);
                            isNew=true;
                        }
                        
                    }
                    
                    // check if there is a newwer version
                    if(!isNew && srcLastModified!=page.getSourceLastModified()) {
                    	isNew=true;
                    	this.page=page=compile(config,classRootDir,null);
    				}
                    
                    // check version
                    if(!isNew && page.getVersion()!=Info.getFullVersionInfo()) {
                    	isNew=true;
                    	this.page=page=compile(config,classRootDir,null);
                    }
                    
                    page.setPageSource(this);
    				page.setLoadType(LOAD_PHYSICAL);

			}
			if(pc!=null)pci.setPageUsed(page);
			return page;
    }

    private boolean isLoad(byte load) {
		return page!=null && load==page.getLoadType();
	}
    

	private synchronized PagePlus compile(ConfigWeb config,Resource classRootDir, Boolean resetCL) throws PageException {
    	try {
            ConfigWebImpl cwi=(ConfigWebImpl) config;
            byte[] barr = cwi.getCompiler().
            	compile(cwi,this,cwi.getTLDs(),cwi.getFLDs(),classRootDir,getJavaName());
           
            PhysicalClassLoader cl;
            if(resetCL==null){
            	cl = (PhysicalClassLoader)mapping.getClassLoaderForPhysical();
                resetCL=Caster.toBoolean(cl!=null && cl.isClassLoaded(getClazz()));
            }
            cl = (PhysicalClassLoader)mapping.getClassLoaderForPhysical(resetCL.booleanValue());
            
            Class clazz=null;
            clazz = cl.loadClass(getClazz(),barr);
            
            /*try {
            	clazz = cl.loadClass(getClazz(),barr);
				
			} 
            catch (Throwable t) {
				t.printStackTrace();
				clazz = cl.loadClass(getClazz(),barr);
			}*/
			return  newInstance(clazz);
        }
        catch(Throwable t) {
        	throw Caster.toPageException(t);
        }
    }

    private PagePlus newInstance(Class clazz) throws SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	try{
			Constructor c = clazz.getConstructor(new Class[]{PageSource.class});
			return (PagePlus) c.newInstance(new Object[]{this});
		}
    	// this only happens with old code from ra files
		catch(NoSuchMethodException e){
			ThreadLocalPageSource.register(this);
			try{
				return (PagePlus) clazz.newInstance();
			}
			finally {
				ThreadLocalPageSource.release();
			}
			
			
		}
	}


	/**
     * return source path as String 
     * @return source path as String
     */
    public String getDisplayPath() {
        if(!mapping.hasArchive())  	{
        	return StringUtil.toString(getPhyscalFile(), null);
        }
        else if(isLoad(LOAD_PHYSICAL))	{
        	return StringUtil.toString(getPhyscalFile(), null);
        }
        else if(isLoad(LOAD_ARCHIVE))	{
        	return StringUtil.toString(getArchiveSourcePath(), null);
        }
        else {
            boolean pse = physcalExists();
            boolean ase = archiveExists();
            
            if(mapping.isPhysicalFirst()) {
                if(pse)return getPhyscalFile().toString();
                else if(ase)return getArchiveSourcePath();
                return getPhyscalFile().toString();
            }
            if(ase)return getArchiveSourcePath();
            else if(pse)return getPhyscalFile().toString();
            return getArchiveSourcePath();
        }
    }
    
    /**
	 * return file object, based on physical path and realpath
	 * @return file Object
	 */
	private String getArchiveSourcePath() {
	    return "ra://"+mapping.getArchive().getAbsolutePath()+"!"+realPath; 
	}

    /**
	 * return file object, based on physical path and realpath
	 * @return file Object
	 */
    public Resource getPhyscalFile() {
        if(physcalSource==null) {
            if(!mapping.hasPhysical()) {
            	return null;
            }
			physcalSource=ResourceUtil.toExactResource(mapping.getPhysical().getRealResource(realPath));
        }
        return physcalSource;
	}
    
    public Resource getArchiveFile() {
    	if(archiveSource==null) {
	    	if(!mapping.hasArchive()) return null;
	    	String path="zip://"+mapping.getArchive().getAbsolutePath()+"!"+realPath;
	    	archiveSource = ThreadLocalPageContext.getConfig().getResource(path);
    	}
        return archiveSource;
	}
    

    /**
	 * merge to realpath to one
	 * @param mapping 
	 * @param parentRealPath 
	 * @param newRealPath
	 * @param isOutSide 
	 * @return merged realpath
	 */
	private static String mergeRealPathes(Mapping mapping,String parentRealPath, String newRealPath, RefBoolean isOutSide) {
		parentRealPath=pathRemoveLast(parentRealPath,isOutSide);
		while(newRealPath.startsWith("../")) {
			parentRealPath=pathRemoveLast(parentRealPath,isOutSide);
			newRealPath=newRealPath.substring(3);
		}
		
		// check if come back
		String path=parentRealPath.concat("/").concat(newRealPath);
		
		if(path.startsWith("../")) {
			int count=0;
			do {
				count++;
				path=path.substring(3);
			}while(path.startsWith("../"));
			
			String strRoot=mapping.getPhysical().getAbsolutePath().replace('\\','/');
			if(!StringUtil.endsWith(strRoot,'/')) {
				strRoot+='/';
			}
			int rootLen=strRoot.length();
			String[] arr=List.toStringArray(List.listToArray(path,'/'),"");//path.split("/");
			int tmpLen;
			for(int i=count;i>0;i--) {
				if(arr.length>i) {
					String tmp='/'+list(arr,0,i);
					tmpLen=rootLen-tmp.length();
					if(strRoot.lastIndexOf(tmp)==tmpLen && tmpLen>=0) {
						StringBuffer rtn=new StringBuffer();
						for(int y=0;i<count-i;y++) rtn.append("../");
						isOutSide.setValue(rtn.length()!=0);
						return (rtn.length()==0?"/":rtn.toString())+list(arr,i,arr.length);
					}
				}
			}
		}
		return parentRealPath.concat("/").concat(newRealPath);
	}

	/**
	 * convert a String array to a string list, but only part of it 
	 * @param arr String Array
	 * @param from start from here
	 * @param len how many element
	 * @return String list
	 */
	private static String list(String[] arr,int from, int len) {
		StringBuffer sb=new StringBuffer();
		for(int i=from;i<len;i++) {
			sb.append(arr[i]);
			if(i+1!=arr.length)sb.append('/');
		}
		return sb.toString();
	}

	
	
	/**
	 * remove the last elemtn of a path
	 * @param path path to remove last element from it
	 * @param isOutSide 
	 * @return path with removed element
	 */
	private static String pathRemoveLast(String path, RefBoolean isOutSide) {
		if(path.length()==0) {
			isOutSide.setValue(true);
			return "..";
		}
		else if(path.endsWith("..")){
		    isOutSide.setValue(true);
			return path.concat("/..");//path+"/..";
		}
		return path.substring(0,path.lastIndexOf('/'));
	}
	
	/**
     * @see railo.runtime.PageSource#getRealpath()
     */
	public String getRealpath() {
		return realPath;
	}	
	/**
     * @see railo.runtime.PageSource#getFullRealpath()
     */
	public String getFullRealpath() {
		if(mapping.getVirtual().length()==1)
			return realPath;
		return mapping.getVirtual()+realPath;
	}
	
	/**
	 * @return returns a variable string based on realpath and return it
	 */
	public String getRealPathAsVariableString() {
		return StringUtil.toIdentityVariableName(realPath);
	}
	
	/**
     * @see railo.runtime.PageSource#getClazz()
     */
	public String getClazz() {
		if(className==null) createClassAndPackage();
		if(packageName.length()>0) return packageName+'.'+className;
		return className;
	}
	
	/**
	 * @return returns the a classname matching to filename (Example: test_cfm)
	 */
	public String getClassName() {
		if(className==null) createClassAndPackage();
		return className;
	}

    /**
     * @see railo.runtime.PageSource#getFileName()
     */
    public String getFileName() {
		if(fileName==null) createClassAndPackage();
        return fileName;
    }
	
	/**
     * @see railo.runtime.PageSource#getJavaName()
     */
	public String getJavaName() {
		if(javaName==null) createClassAndPackage();
		return javaName;
	}

	/**
	 * @return returns the a package matching to file (Example: railo.web)
	 */
	public String getPackageName() {
		if(packageName==null) createClassAndPackage();
		return packageName;
	}
	/**
     * @see railo.runtime.PageSource#getComponentName()
     */
	public String getComponentName() {
		if(compName==null) createComponentName();
		return compName;
	}
	
	
	private synchronized void createClassAndPackage() {
		String str=realPath;
		StringBuffer packageName=new StringBuffer();
		StringBuffer javaName=new StringBuffer();
		
		String[] arr=List.toStringArrayEL(List.listToArrayRemoveEmpty(str,'/'));
		
		String varName;
		for(int i=0;i<arr.length;i++) {
			if(i==(arr.length-1)) {
				int index=arr[i].lastIndexOf('.');
				if(index!=-1){
					String ext=arr[i].substring(index+1);
					varName=StringUtil.toVariableName(arr[i].substring(0,index)+"_"+ext);
				}
				else varName=StringUtil.toVariableName(arr[i]);
				varName=varName+"$cf";
				className=varName.toLowerCase();
				fileName=arr[i];
			}
			else {
				varName=StringUtil.toVariableName(arr[i]);
				if(i!=0) {
				    packageName.append('.');
				}
				packageName.append(varName);
			}
			javaName.append('/');
			javaName.append(varName);
		}
		
		this.packageName=packageName.toString().toLowerCase();
		this.javaName=javaName.toString().toLowerCase();

		
		
	}
	
	

	private synchronized void createComponentName() {
		
	    String str=realPath;
		StringBuffer compName=new StringBuffer();
		String[] arr;
		

		arr=List.toStringArrayEL(List.listToArrayRemoveEmpty(mapping.getVirtual(),"\\/"));
		
		for(int i=0;i<arr.length;i++) {
			if(compName.length()>0) compName.append('.');
			compName.append(arr[i]);
		}

		arr=List.toStringArrayEL(List.listToArrayRemoveEmpty(str,'/'));
				
		for(int i=0;i<arr.length;i++) {
		    if(compName.length()>0) compName.append('.');
			if(i==(arr.length-1)) {
			    compName.append(arr[i].substring(0,arr[i].length()-4));
			}
			else compName.append(arr[i]);
		}
		this.compName=compName.toString();
	}

    /**
     * @see railo.runtime.PageSource#getMapping()
     */
    public Mapping getMapping() {
        return mapping;
    }

    /**
     * @see railo.runtime.PageSource#exists()
     */
    public boolean exists() {
    	if(mapping.isPhysicalFirst())
	        return physcalExists() || archiveExists();
	    return archiveExists() || physcalExists();
    }

    /**
     * @see railo.runtime.PageSource#physcalExists()
     */
    public boolean physcalExists() {
        return ResourceUtil.exists(getPhyscalFile());
    }
    
    private boolean archiveExists() {
        if(!mapping.hasArchive())return false;
        try {
        	String clazz = getClazz();
        	if(clazz==null) return getArchiveFile().exists();
        	mapping.getClassLoaderForArchive().loadClass(clazz);
        	return true;
        } catch (Exception e) {
            return getArchiveFile().exists();
        }
    }

    /**
     * return the inputstream of the source file
     * @return return the inputstream for the source from ohysical or archive
     * @throws FileNotFoundException
     */
    private InputStream getSourceAsInputStream() throws IOException {
        if(!mapping.hasArchive()) 		return IOUtil.toBufferedInputStream(getPhyscalFile().getInputStream());
        else if(isLoad(LOAD_PHYSICAL))	return IOUtil.toBufferedInputStream(getPhyscalFile().getInputStream());
        else if(isLoad(LOAD_ARCHIVE)) 	{
            StringBuffer name=new StringBuffer(getPackageName().replace('.','/'));
            if(name.length()>0)name.append("/");
            name.append(getFileName());
            
            return mapping.getClassLoaderForArchive().getResourceAsStream(name.toString());
        }
        else {
            return null;
        }
    }
    /**
     * @see railo.runtime.PageSource#getSource()
     */
    public String[] getSource() throws IOException {
        //if(source!=null) return source;
        InputStream is = getSourceAsInputStream();
        if(is==null) return null;
        try {
        	return IOUtil.toStringArray(IOUtil.getReader(is,getMapping().getConfig().getTemplateCharset()));
        }
        finally {
        	IOUtil.closeEL(is);
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
    	if(this==obj) return true;  
    	if(!(obj instanceof PageSource)) return false;
    	return getClassName().equals(((PageSource)obj).getClassName());
    	//return equals((PageSource)obj);
    }
    
    /**
     * is given object equal to this
     * @param other
     * @return is same
     */
    public boolean equals(PageSource other) {
        if(this==other) return true;  
        return getClassName().equals(other.getClassName());
    }

	/**
     * @see railo.runtime.PageSource#getRealPage(java.lang.String)
     */
	public PageSource getRealPage(String realPath) {
	    if(realPath.equals(".") || realPath.equals(".."))realPath+='/';
	    else realPath=realPath.replace('\\','/');
	    RefBoolean _isOutSide=new RefBooleanImpl(isOutSide);
	    
	    
		if(realPath.indexOf('/')==0) {
		    _isOutSide.setValue(false);
		}
		else if(realPath.startsWith("./")) {
			realPath=mergeRealPathes(mapping,this.realPath, realPath.substring(2),_isOutSide);
		}
		else {
			realPath=mergeRealPathes(mapping,this.realPath, realPath,_isOutSide);
		}
		return mapping.getPageSource(realPath,_isOutSide.toBooleanValue());
	}
	
	/**
     * @see railo.runtime.PageSource#setLastAccessTime(long)
     */
	public final void setLastAccessTime(long lastAccess) {
		this.lastAccess=lastAccess;
	}	
	
	/**
     * @see railo.runtime.PageSource#getLastAccessTime()
     */
	public final long getLastAccessTime() {
		return lastAccess;
	}

	/**
     * @see railo.runtime.PageSource#setLastAccessTime()
     */
	public synchronized final void setLastAccessTime() {
		accessCount++;
		this.lastAccess=System.currentTimeMillis();
	}	
	
	/**
     * @see railo.runtime.PageSource#getAccessCount()
     */
	public final int getAccessCount() {
		return accessCount;
	}

    /**
     * @see railo.runtime.SourceFile#getFile()
     */
    public Resource getFile() {
    	Resource res = getPhyscalFile();
    	if(res!=null) return res;
    	return getArchiveFile();
    }


    public void clear() {
    	if(page!=null){
    		//print.o("clear:"+getDisplayPath()+":"+hashCode()+":"+page.hashCode());
    		page=null;
    	}
    }

	/**
	 * @see railo.runtime.SourceFile#getFullClassName()
	 */
    public String getFullClassName() {
    	String s=_getFullClassName();
    	return s;
    }
    
	public String _getFullClassName() {
		String p=getPackageName();
		if(p.length()==0) return getClassName();
		return p.concat(".").concat(getClassName());
	}
	
	public boolean isLoad() {
		return page!=null;////load!=LOAD_NONE;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getDisplayPath();
	}
	
	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return SizeOf.size(page,0)+
		SizeOf.size(className)+
		SizeOf.size(packageName)+
		SizeOf.size(javaName)+
		SizeOf.size(fileName)+
		SizeOf.size(compName)+
		SizeOf.size(lastAccess)+
		SizeOf.size(accessCount)+
		SizeOf.size(recompileAlways)+
		SizeOf.size(recompileAfterStartUp);
	}
	
	
	
}