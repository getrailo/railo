package railo.runtime.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import railo.commons.digest.MD5;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandlerFactoryCollection;
import railo.runtime.exp.SecurityException;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.listener.ClassicAppListener;
import railo.runtime.listener.MixedAppListener;
import railo.runtime.listener.ModernAppListener;
import railo.runtime.listener.NoneAppListener;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.security.SecurityManager;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.Struct;


/**
 * 
 */
public final class ConfigWebUtil {
    
    /**
     * touch a file object by the string definition
     * @param config 
     * @param directory
     * @param path
     * @param type
     * @return matching file
     */
    public static Resource getFile(Config config, Resource directory,String path, short type) {
    	path=replacePlaceholder(path,config);
        if(!StringUtil.isEmpty(path,true)) {
            Resource file=getFile(directory.getRealResource(path),type);
            if(file!=null) return file;

            file=getFile(config.getResource(path),type);
            
            if(file!=null) return file;
        }
        return null;
    }
    
    /**
	 * generate a file object by the string definition
     * @param rootDir 
     * @param strDir 
     * @param defaultDir 
     * @param configDir 
     * @param type 
     * @param config 
     * @return file
	 */
    static Resource getFile(Resource rootDir,String strDir, String defaultDir,Resource configDir, short type, ConfigImpl config)  {
    	strDir=replacePlaceholder(strDir,config);
        if(!StringUtil.isEmpty(strDir,true)) {
        	Resource res;
        	if(strDir.indexOf("://")!=-1){ // TODO better impl.
        		res=getFile(config.getResource(strDir),type);
        		if(res!=null) return res;
        	}
        	res=getFile(rootDir.getRealResource(strDir),type);
            if(res!=null) return res;

            res=getFile(config.getResource(strDir),type);
            if(res!=null) return res;
        }
        if(defaultDir==null) return null;
        Resource file=getFile(configDir.getRealResource(defaultDir),type);
        return file;
    }

    public static String replacePlaceholder(String str, Config config) {
    	if(StringUtil.isEmpty(str)) return str;
    	
    	if(StringUtil.startsWith(str,'{')){
            
            
            // Config Server
            if(str.startsWith("{railo-config")) {    
                if(str.startsWith("}",13)) str=checkResult(str,config.getConfigDir().getReal(str.substring(14)));
                else if(str.startsWith("-dir}",13)) str=checkResult(str,config.getConfigDir().getReal(str.substring(18)));
                else if(str.startsWith("-directory}",13)) str=checkResult(str,config.getConfigDir().getReal(str.substring(24)));
            }
            
            
            else if(config!=null && str.startsWith("{railo-server")) {
            	Resource dir=config instanceof ConfigWeb?((ConfigWeb)config).getConfigServerDir():config.getConfigDir();
                //if(config instanceof ConfigServer && cs==null) cs=(ConfigServer) cw;
                if(dir!=null) {
                    if(str.startsWith("}",13)) str=checkResult(str,dir.getReal(str.substring(14)));
                    else if(str.startsWith("-dir}",13)) str=checkResult(str,dir.getReal(str.substring(18)));
                    else if(str.startsWith("-directory}",13)) str=checkResult(str,dir.getReal(str.substring(24)));
                }
            }
            // Config Web
            else if(str.startsWith("{railo-web")) {
                if(str.startsWith("}",10)) str=checkResult(str,config.getConfigDir().getReal(str.substring(11)));
                else if(str.startsWith("-dir}",10)) str=checkResult(str,config.getConfigDir().getReal(str.substring(15)));
                else if(str.startsWith("-directory}",10)) str=checkResult(str,config.getConfigDir().getReal(str.substring(21)));
            }
            // Web Root
            else if(str.startsWith("{web-root")) {
                if(config instanceof ConfigWeb) {
                    if(str.startsWith("}",9)) str=checkResult(str,config.getRootDirectory().getReal(str.substring(10)));
                    else if(str.startsWith("-dir}",9)) str=checkResult(str,config.getRootDirectory().getReal(str.substring(14)));
                    else if(str.startsWith("-directory}",9)) str=checkResult(str,config.getRootDirectory().getReal(str.substring(20)));
                }
            }
            // Temp
            else if(str.startsWith("{temp")) {
                if(str.startsWith("}",5)) str=checkResult(str,config.getTempDirectory().getRealResource(str.substring(6)).toString());
                else if(str.startsWith("-dir}",5)) str=checkResult(str,config.getTempDirectory().getRealResource(str.substring(10)).toString());
                else if(str.startsWith("-directory}",5)) str=checkResult(str,config.getTempDirectory().getRealResource(str.substring(16)).toString());
            }
            else if(config instanceof ServletConfig){
            	Map<String,String> labels=null;
            	// web
            	if(config instanceof ConfigWebImpl){
            		labels=((ConfigWebImpl)config).getAllLabels();
            	}
            	// server
            	else if(config instanceof ConfigServerImpl){
            		labels=((ConfigServerImpl)config).getLabels();
            	}
            	if(labels!=null)str=SystemUtil.parsePlaceHolder(str,((ServletConfig)config).getServletContext(),labels);
            }
            else str=SystemUtil.parsePlaceHolder(str);
            
            if(StringUtil.startsWith(str,'{')){
            	Struct constants = ((ConfigImpl)config).getConstants();
            	Iterator<Entry<Key, Object>> it = constants.entryIterator();
            	Entry<Key, Object> e;
            	while(it.hasNext()) {
            		e = it.next();
            		if(StringUtil.startsWithIgnoreCase(str,"{"+e.getKey().getString()+"}")) {
            			String value=(String) e.getValue();
            			str=checkResult(str,config.getResource( value)
            				.getReal(str.substring(e.getKey().getString().length()+2)));
                        break;
            			
            		}
            	}
            }
        }
        return str;
    }
    
    
    
    private static String checkResult(String src, String res) { 
    	boolean srcEndWithSep=StringUtil.endsWith(src, ResourceUtil.FILE_SEPERATOR) || StringUtil.endsWith(src, '/') || StringUtil.endsWith(src, '\\');
    	boolean resEndWithSep=StringUtil.endsWith(res, ResourceUtil.FILE_SEPERATOR) || StringUtil.endsWith(res, '/') || StringUtil.endsWith(res, '\\');
    	if(srcEndWithSep && !resEndWithSep) return res+ResourceUtil.FILE_SEPERATOR;
    	if(!srcEndWithSep && resEndWithSep) return res.substring(0,res.length()-1);
    	
    	return res;
	}

	/**
     * get only a existing file, dont create it
     * @param sc
     * @param strDir
     * @param defaultDir
     * @param configDir
     * @param type
     * @param config 
     * @return existing file
     */
    public static Resource getExistingResource(ServletContext sc,String strDir, String defaultDir,Resource configDir, short type, Config config) {
        //ARP
    	
    	strDir=replacePlaceholder(strDir,config);
        if(strDir!=null && strDir.trim().length()>0) {
        	Resource res=sc==null?null:_getExistingFile(config.getResource(ResourceUtil.merge(ReqRspUtil.getRootPath(sc),strDir)),type);
            if(res!=null) return res;
            
            res=_getExistingFile(config.getResource(strDir),type);
            if(res!=null) return res;
        }
        if(defaultDir==null) return null;
        return _getExistingFile(configDir.getRealResource(defaultDir),type);
        
    }

    private static Resource _getExistingFile(Resource file, short type) {
        
        boolean asDir=type==ResourceUtil.TYPE_DIR;
        // File
        if(file.exists() && ((file.isDirectory() && asDir)||(file.isFile() && !asDir))) {
            return ResourceUtil.getCanonicalResourceEL(file);
        }
        return null;
    }

    /**
     * 
     * @param file
     * @param type (FileUtil.TYPE_X)
     * @return created file
     */
    public static Resource getFile(Resource file, short type) {
        return ResourceUtil.createResource(file,ResourceUtil.LEVEL_GRAND_PARENT_FILE,type);
    }

    /**
     * checks if file is a directory or not, if directory doesn't exist, it will be created
     * @param directory
     * @return is directory or not
     */
	public static boolean isDirectory(Resource directory) {
        if(directory.exists()) return directory.isDirectory();
        return directory.mkdirs();
    }

    /**
     * checks if file is a file or not, if file doesn't exist, it will be created
     * @param file
     * @return is file or not
     */
    public static boolean isFile(Resource file) {
        if(file.exists()) return file.isFile();
        Resource parent=file.getParentResource();
        return parent.mkdirs() && file.createNewFile();
    }
    
    /**
     * has access checks if config object has access to given type
     * @param config
     * @param type
     * @return has access
     */
    public static boolean hasAccess(Config config, int type) {
        
    	boolean has=true;
        if(config instanceof ConfigWeb) {
            has=((ConfigWeb)config).getSecurityManager().getAccess(type)!=SecurityManager.VALUE_NO;
        }
        return has;
    }

    public static String translateOldPath(String path) {
        if(path==null) return path;
        if(path.startsWith("/WEB-INF/railo/")) {
            path="{web-root}"+path;
        }
        return path;
    }

	public static Object getIdMapping(Mapping m) {
		StringBuilder id=new StringBuilder(m.getVirtualLowerCase());
        if(m.hasPhysical())id.append(m.getStrPhysical());
        if(m.hasArchive())id.append(m.getStrPhysical());
        return m.toString().toLowerCase();
	}

	public static void checkGeneralReadAccess(ConfigImpl config, String password) throws SecurityException {
		SecurityManager sm = config.getSecurityManager();
    	short access = sm.getAccess(SecurityManager.TYPE_ACCESS_READ);
    	if(config instanceof ConfigServer)access=SecurityManager.ACCESS_PROTECTED;
    	if(access==SecurityManager.ACCESS_PROTECTED) {
    		checkPassword(config,"read",password);
    	}
    	else if(access==SecurityManager.ACCESS_CLOSE) {
    		throw new SecurityException("can't access, read access is disabled");
    	}
	}
	
	public static void checkGeneralWriteAccess(ConfigImpl config, String password) throws SecurityException {
    	SecurityManager sm = config.getSecurityManager();
    	short access = sm.getAccess(SecurityManager.TYPE_ACCESS_WRITE);
    	
    	if(config instanceof ConfigServer)access=SecurityManager.ACCESS_PROTECTED;
    	if(access==SecurityManager.ACCESS_PROTECTED) {
    		checkPassword(config,"write",password);
    	}
    	else if(access==SecurityManager.ACCESS_CLOSE) {
    		throw new SecurityException("can't access, write access is disabled");
    	}
	}

    public static Password checkPassword(ConfigImpl config, String type,String password) throws SecurityException {
    	if(!config.hasPassword())
            throw new SecurityException("can't access, no password is defined");
    	Password pw = config.isPasswordEqual(password,true);
        if(pw==null){
        	if(StringUtil.isEmpty(password)){
        		if(type==null)
        			throw new SecurityException("Access is protected",
                    		"to access the configuration without a password, you need to change the access to [open] in the Server Administrator");
        		throw new SecurityException(type +" access is protected",
                		"to access the configuration without a password, you need to change the "+type+" access to [open] in the Server Administrator");
        	}
            throw new SecurityException("No access, password is invalid");
        }
        return pw;
    }
    
    public static String createMD5FromResource(Resource resource) throws IOException {
    	InputStream is=null;
    	try{
    		is=resource.getInputStream();	
    		byte[] barr = IOUtil.toBytes(is);
    		return MD5.getDigestAsString(barr);
    	}
    	finally{
    		IOUtil.closeEL(is);
    	}
    }

	public static int toListenerMode(String strListenerMode, int defaultValue) {
		if(StringUtil.isEmpty(strListenerMode,true)) return defaultValue;
		strListenerMode=strListenerMode.trim();
		
		if("current".equalsIgnoreCase(strListenerMode) || "curr".equalsIgnoreCase(strListenerMode))		
        	return ApplicationListener.MODE_CURRENT;
        else if("current2root".equalsIgnoreCase(strListenerMode) || "curr2root".equalsIgnoreCase(strListenerMode))		
        	return ApplicationListener.MODE_CURRENT2ROOT;
        else if("root".equalsIgnoreCase(strListenerMode))		
        	return ApplicationListener.MODE_ROOT;
        
		return defaultValue;
	}

	public static ApplicationListener loadListener(String type, ApplicationListener defaultValue) {
		 if(StringUtil.isEmpty(type,true)) return defaultValue;
		 type=type.trim();
		 
		 // none
		 if("none".equalsIgnoreCase(type))	
			 return new NoneAppListener();
		 // classic
		 if("classic".equalsIgnoreCase(type))
			 return new ClassicAppListener();
		 // modern
		 if("modern".equalsIgnoreCase(type))	
			 return new ModernAppListener();
		 // mixed
		 if("mixed".equalsIgnoreCase(type))	
			 return new MixedAppListener();
        
		return defaultValue;
	}

	public static short inspectTemplate(String str, short defaultValue) { 
		if(str==null) return defaultValue;
		str = str.trim().toLowerCase();
		if (str.equals("always")) return ConfigImpl.INSPECT_ALWAYS;
		else if (str.equals("never"))return ConfigImpl.INSPECT_NEVER;
		else if (str.equals("once"))return ConfigImpl.INSPECT_ONCE;
		return defaultValue;
	}


    public static String inspectTemplate(short s,String defaultValue) {
    	switch(s){
    		case ConfigImpl.INSPECT_ALWAYS: return "always";
    		case ConfigImpl.INSPECT_NEVER: return "never";
    		case ConfigImpl.INSPECT_ONCE: return "once";
    		default: return defaultValue;
    	}
	}

	public static short toScopeCascading(String type, short defaultValue) {
		if(StringUtil.isEmpty(type)) return defaultValue;
        if(type.equalsIgnoreCase("strict")) return Config.SCOPE_STRICT;
        else if(type.equalsIgnoreCase("small")) return Config.SCOPE_SMALL;
        else if(type.equalsIgnoreCase("standard"))return Config.SCOPE_STANDARD;
        else if(type.equalsIgnoreCase("standart"))return Config.SCOPE_STANDARD;
        return defaultValue;
	}

	public static String toScopeCascading(short type, String defaultValue) {
		switch(type){
			case Config.SCOPE_STRICT: return "strict";
			case Config.SCOPE_SMALL: return "small";
			case Config.SCOPE_STANDARD: return "standard";
			default: return defaultValue;
		}
	}
	

	public static Mapping[] getAllMappings(PageContext pc) {
		List<Mapping> list=new ArrayList<Mapping>();
		getAllMappings(list,pc.getConfig().getMappings());
		getAllMappings(list,pc.getConfig().getCustomTagMappings());
		getAllMappings(list,pc.getConfig().getComponentMappings());
		getAllMappings(list,pc.getApplicationContext().getMappings());
		return list.toArray(new Mapping[list.size()]);
	}
	
	public static Mapping[] getAllMappings(Config c) {
		
		List<Mapping> list=new ArrayList<Mapping>();
		getAllMappings(list,c.getMappings());
		getAllMappings(list,c.getCustomTagMappings());
		getAllMappings(list,c.getComponentMappings());
		return list.toArray(new Mapping[list.size()]);
	}

	private static void getAllMappings(List<Mapping> list, Mapping[] mappings) {
		if(!ArrayUtil.isEmpty(mappings))for(int i=0;i<mappings.length;i++)	{
			list.add(mappings[i]);
		}
	}

	public static CacheHandlerFactoryCollection getCacheHandlerFactories(ConfigWeb config) { 
		return ((ConfigWebImpl)config).getCacheHandlerFactories();
	}
}