package railo.runtime.config;

import java.io.IOException;
import java.io.InputStream;

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
import railo.runtime.security.SecurityManager;
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
            ConfigServer cs;
            
            
            // Config Server
            if(str.startsWith("{railo-config")) {    
                if(str.startsWith("}",13)) str=config.getConfigDir().getReal(str.substring(14));
                else if(str.startsWith("-dir}",13)) str=config.getConfigDir().getReal(str.substring(18));
                else if(str.startsWith("-directory}",13)) str=config.getConfigDir().getReal(str.substring(24));
            }
            
            
            else if(str.startsWith("{railo-server")) {
                cs=((ConfigImpl)config).getConfigServerImpl();
                //if(config instanceof ConfigServer && cs==null) cs=(ConfigServer) cw;
                if(cs!=null) {
                    if(str.startsWith("}",13)) str=cs.getConfigDir().getReal(str.substring(14));
                    else if(str.startsWith("-dir}",13)) str=cs.getConfigDir().getReal(str.substring(18));
                    else if(str.startsWith("-directory}",13)) str=cs.getConfigDir().getReal(str.substring(24));
                }
            }
            // Config Web
            else if(str.startsWith("{railo-web")) {
                //if(cw instanceof ConfigServer) cw=null;
                //if(config instanceof ConfigWeb) {
                    if(str.startsWith("}",10)) str=config.getConfigDir().getReal(str.substring(11));
                    else if(str.startsWith("-dir}",10)) str=config.getConfigDir().getReal(str.substring(15));
                    else if(str.startsWith("-directory}",10)) str=config.getConfigDir().getReal(str.substring(21));
                //}
            }
            // Web Root
            else if(str.startsWith("{web-root")) {
                //if(cw instanceof ConfigServer) cw=null;
                if(config instanceof ConfigWeb) {
                    if(str.startsWith("}",9)) str=config.getRootDirectory().getReal(str.substring(10));
                    else if(str.startsWith("-dir}",9)) str=config.getRootDirectory().getReal(str.substring(14));
                    else if(str.startsWith("-directory}",9)) str=config.getRootDirectory().getReal(str.substring(20));
                }
            }
            // Temp
            else if(str.startsWith("{temp")) {
                if(str.startsWith("}",5)) str=config.getTempDirectory().getRealResource(str.substring(6)).toString();
                else if(str.startsWith("-dir}",5)) str=config.getTempDirectory().getRealResource(str.substring(10)).toString();
                else if(str.startsWith("-directory}",5)) str=config.getTempDirectory().getRealResource(str.substring(16)).toString();
            }
            else if(config instanceof ServletConfig)str=SystemUtil.parsePlaceHolder(str,((ServletConfig)config).getServletContext(),((ConfigImpl)config).getConfigServerImpl().getLabels());
            else str=SystemUtil.parsePlaceHolder(str);
            
            if(StringUtil.startsWith(str,'{')){
            	Struct constants = ((ConfigImpl)config).getConstants();
            	String[] arr = constants.keysAsString();
            	for(int i=0;i<arr.length;i++) {
            		if(StringUtil.startsWithIgnoreCase(str,"{"+arr[i]+"}")) {
            			String value=(String) constants.get(arr[i],null);
            			str=config.getResource( value)
            				.getReal(str.substring(arr[i].length()+2));
                        break;
            			
            		}
            	}
            }
        }
        return str;
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
    public static Resource getExistingResource(ServletContext sc,String strDir, String defaultDir,Resource configDir, short type, ConfigImpl config) {
        //ARP
    	
    	strDir=replacePlaceholder(strDir,config);
        if(strDir!=null && strDir.trim().length()>0) {
        	Resource res=_getExistingFile(config.getResource(ResourceUtil.merge(sc.getRealPath("/"),strDir)),type);
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
     * checks if file is a directory or not, if directory dosent exists, it will be created
     * @param directory
     * @return is directory or not
     */
	public static boolean isDirectory(Resource directory) {
        if(directory.exists()) return directory.isDirectory();
        return directory.mkdirs();
    }
    
    /**
     * checks if file is a file or not, if file dosent exists, it will be created
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
        if(config instanceof ConfigWeb)
            has=((ConfigWeb)config).getSecurityManager().getAccess(type)==SecurityManager.VALUE_YES;
        
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
    	boolean isCS=config instanceof ConfigServer;
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
        if(!config.getPassword().equalsIgnoreCase(password)){
        	if(StringUtil.isEmpty(password)){
        		if(type==null)
        			throw new SecurityException("acccess is protected",
                    		"to access the configuration without a password, you need to change the access to [open] in the Server Administrator");
        		throw new SecurityException(type +" acccess is protected",
                		"to access the configuration without a password, you need to change the "+type+" access to [open] in the Server Administrator");
        	}
            throw new SecurityException("no acccess, password is invalid");
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
    
    
}