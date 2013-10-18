package railo.runtime.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import railo.commons.digest.MD5;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.Log;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.log.LogAndSourceImpl;
import railo.commons.io.log.LogConsole;
import railo.commons.io.log.LogResource;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.Mapping;
import railo.runtime.exp.SecurityException;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.listener.ClassicAppListener;
import railo.runtime.listener.MixedAppListener;
import railo.runtime.listener.ModernAppListener;
import railo.runtime.listener.NoneAppListener;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.security.SecurityManager;
import railo.runtime.type.Collection.Key;
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

    
    /*public static String replacePlaceholder(String str, Config config) {
    	if(StringUtil.isEmpty(str)) return str;
    	if(str.indexOf("railo-pcw-web")!=-1){
    		print.out(str);
    		str=_replacePlaceholder(str, config);
    		print.out(str);
    		return str;
    	}
    	return _replacePlaceholder(str, config);
    }*/
    
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
                //if(cw instanceof ConfigServer) cw=null;
                //if(config instanceof ConfigWeb) {
                    if(str.startsWith("}",10)) str=checkResult(str,config.getConfigDir().getReal(str.substring(11)));
                    else if(str.startsWith("-dir}",10)) str=checkResult(str,config.getConfigDir().getReal(str.substring(15)));
                    else if(str.startsWith("-directory}",10)) str=checkResult(str,config.getConfigDir().getReal(str.substring(21)));
                //}
            }
            // Web Root
            else if(str.startsWith("{web-root")) {
                //if(cw instanceof ConfigServer) cw=null;
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
            	//Collection.Key[] arr = constants.keys();
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
    /*public static File getFile(File file, int level, short type) {
		
        boolean asDir=type==TYPE_DIR;
        // File
		if(level>=LEVEL_FILE && file.exists() && ((file.isDirectory() && asDir)||(file.isFile() && !asDir))) {
		    return FileUtil.getCanonicalFileEL(file);
		}
		
		// Parent
		File parent=file.getParentFile();
		if(level>=LEVEL_PARENT && parent!=null && parent.exists() && FileUtil.canRW(parent)) {
            if(asDir) {
		        if(file.mkdirs()) return FileUtil.getCanonicalFileEL(file);
		    }
		    else {
		        if(FileUtil.createNewFileEL(file))return FileUtil.getCanonicalFileEL(file);
		    }
			return FileUtil.getCanonicalFileEL(file);
		}    
		
		// Grand Parent
		if(level>=LEVEL_GRAND_PARENT && parent!=null) {
			File gparent=parent.getParentFile();
			if(gparent!=null && gparent.exists() && FileUtil.canRW(gparent)) {
			    if(asDir) {
			        if(file.mkdirs())return FileUtil.getCanonicalFileEL(file);
			    }
			    else {
			        if(parent.mkdirs() && FileUtil.createNewFileEL(file))
			            return FileUtil.getCanonicalFileEL(file);
			    }
			}        
		}
		return null;
    }*/
    
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
        //try {
            return parent.mkdirs() && file.createNewFile();
        /*} catch (IOException e) {
            return false;
        }*/  
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

    /**
     * loads log
     * @param configServer 
     * @param config 
     * @param strLogger
     * @param hasAccess 
     * @param logLevel 
     * @return log
     * @throws IOException
    */
    public static LogAndSource getLogAndSource( ConfigServer configServer, Config config, String strLogger, boolean hasAccess, int logLevel) throws IOException {
        if(logLevel==-1)logLevel=Log.LEVEL_ERROR;
    	//boolean isCS=config instanceof ConfigServer;
        if(!StringUtil.isEmpty(strLogger) && hasAccess && !"console".equalsIgnoreCase(strLogger)) {
        	return ConfigWebUtil.getLogAndSource(config,strLogger,logLevel);
        }
        return new LogAndSourceImpl(LogConsole.getInstance(config,logLevel),strLogger);
    }
    private static LogAndSource getLogAndSource(Config config, String strLogger, int logLevel)  {
        if(strLogger==null) return new LogAndSourceImpl(LogConsole.getInstance(config,logLevel),"");
        
        // File
        strLogger=translateOldPath(strLogger);
        Resource file=ConfigWebUtil.getFile(config, config.getConfigDir(),strLogger, ResourceUtil.TYPE_FILE);
        if(file!=null && ResourceUtil.canRW(file)) {
            try {
				return new LogAndSourceImpl(new LogResource(file,logLevel,config.getResourceCharset()),strLogger);
			} catch (IOException e) {
				SystemOut.printDate(config.getErrWriter(),e.getMessage());
			}
        }
        
        if(file==null)SystemOut.printDate(config.getErrWriter(),"can't create logger from file ["+strLogger+"], invalid path");
        else SystemOut.printDate(config.getErrWriter(),"can't create logger from file ["+strLogger+"], no write access");
    
        return new LogAndSourceImpl(LogConsole.getInstance(config,logLevel),strLogger);
    
    }

    public static String translateOldPath(String path) {
        if(path==null) return path;
        if(path.startsWith("/WEB-INF/railo/")) {
            path="{web-root}"+path;
        }
        //print.ln(path);
        return path;
    }

	public static Object getIdMapping(Mapping m) {
		StringBuffer id=new StringBuffer(m.getVirtualLowerCase());
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

    public static void checkPassword(ConfigImpl config, String type,String password) throws SecurityException {
    	if(!config.hasPassword())
            throw new SecurityException("can't access, no password is defined");
        //print.ln(config.getPassword()+".equalsIgnoreCase("+password+")");
        if(!config.isPasswordEqual(password,true)){
        	if(StringUtil.isEmpty(password)){
        		if(type==null)
        			throw new SecurityException("Access is protected",
                    		"to access the configuration without a password, you need to change the access to [open] in the Server Administrator");
        		throw new SecurityException(type +" access is protected",
                		"to access the configuration without a password, you need to change the "+type+" access to [open] in the Server Administrator");
        	}
            throw new SecurityException("No access, password is invalid");
        }
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
    
}