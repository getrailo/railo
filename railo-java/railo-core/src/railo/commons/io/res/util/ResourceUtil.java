package railo.commons.io.res.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.ContentType;
import railo.commons.io.res.ContentTypeImpl;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.commons.io.res.type.http.HTTPResource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.List;
import railo.runtime.type.util.ArrayUtil;

public final class ResourceUtil {

	public static final int MIMETYPE_CHECK_EXTENSION=1;
	public static final int MIMETYPE_CHECK_HEADER=2;
	
	
	/**
     * Field <code>FILE_SEPERATOR</code>
     */
    public static final char FILE_SEPERATOR=File.separatorChar; 
    /**
     * Field <code>FILE_ANTI_SEPERATOR</code>
     */
    public static final char FILE_ANTI_SEPERATOR=(FILE_SEPERATOR=='/')?'\\':'/';
    
    /**
     * Field <code>TYPE_DIR</code>
     */
    public static final short TYPE_DIR=0;
    
    /**
     * Field <code>TYPE_FILE</code>
     */
    public static final short TYPE_FILE=1;

    /**
     * Field <code>LEVEL_FILE</code>
     */
    public static final short LEVEL_FILE=0;
    /**
     * Field <code>LEVEL_PARENT_FILE</code>
     */
    public static final short LEVEL_PARENT_FILE=1;
    /**
     * Field <code>LEVEL_GRAND_PARENT_FILE</code>
     */
    public static final short LEVEL_GRAND_PARENT_FILE=2;
    
    
    private static boolean isUnix=SystemUtil.isUnix();
    
    private static final HashMap<String, String> EXT_MT=new HashMap<String, String>();
    static {
    	EXT_MT.put("ai","application/postscript");
    	EXT_MT.put("aif","audio/x-aiff");
    	EXT_MT.put("aifc","audio/x-aiff");
    	EXT_MT.put("aiff","audio/x-aiff");
    	EXT_MT.put("au","audio/basic");
    	EXT_MT.put("avi","video/x-msvideo");
    	EXT_MT.put("bin","application/octet-stream");
    	EXT_MT.put("bmp","image/x-ms-bmp");
    	EXT_MT.put("cgm","image/cgm");
    	EXT_MT.put("cmx","image/x-cmx");
    	EXT_MT.put("csh","application/x-csh");
    	EXT_MT.put("cfm","text/html");
    	EXT_MT.put("cfml","text/html");
    	EXT_MT.put("css","text/css");
    	EXT_MT.put("doc","application/msword");
    	EXT_MT.put("docx","application/msword");
    	EXT_MT.put("eps","application/postscript");
    	EXT_MT.put("exe","application/octet-stream");
    	EXT_MT.put("gif","image/gif");
    	EXT_MT.put("gtar","application/x-gtar");
    	EXT_MT.put("hqx","application/mac-binhex40");
    	EXT_MT.put("htm","text/html");
    	EXT_MT.put("html","text/html");
    	EXT_MT.put("jpe","image/jpeg");
    	EXT_MT.put("jpeg","image/jpeg");
    	EXT_MT.put("jpg","image/jpeg");
    	EXT_MT.put("js","text/javascript");
    	EXT_MT.put("mmid","x-music/x-midi");
    	EXT_MT.put("mov","video/quicktime");
    	EXT_MT.put("mp2a","audio/x-mpeg-2");
    	EXT_MT.put("mp2v","video/mpeg-2");
    	EXT_MT.put("mp3","audio/mpeg");
    	EXT_MT.put("mp4","video/mp4");
    	EXT_MT.put("mpa","audio/x-mpeg");
    	EXT_MT.put("mpa2","audio/x-mpeg-2");
    	EXT_MT.put("mpeg","video/mpeg");
    	EXT_MT.put("mpega","audio/x-mpeg");
    	EXT_MT.put("mpg","video/mpeg");
    	EXT_MT.put("mpv2","video/mpeg-2");
    	EXT_MT.put("pbm","image/x-portable-bitmap");
    	EXT_MT.put("pcd","image/x-photo-cd");
    	EXT_MT.put("pdf","application/pdf");
    	EXT_MT.put("pgm","image/x-portable-graymap");
    	EXT_MT.put("pict","image/x-pict");
    	EXT_MT.put("pl","application/x-perl");
    	EXT_MT.put("png","image/png");
    	EXT_MT.put("php","text/html");
    	EXT_MT.put("pnm","image/x-portable-anymap");
    	EXT_MT.put("ppm","image/x-portable-pixmap");
    	EXT_MT.put("ppt","application/vnd.ms-powerpoint");
    	EXT_MT.put("pptx","application/vnd.ms-powerpoint");
    	EXT_MT.put("ps","application/postscript");
    	EXT_MT.put("qt","video/quicktime");
    	EXT_MT.put("rgb","image/rgb");
    	EXT_MT.put("rtf","application/rtf");
    	EXT_MT.put("sh","application/x-sh");
    	EXT_MT.put("sit","application/x-stuffit");
    	EXT_MT.put("swf","application/x-shockwave-flash");
    	EXT_MT.put("tar","application/x-tar");
    	EXT_MT.put("tcl","application/x-tcl");
    	EXT_MT.put("tif","image/tiff");
    	EXT_MT.put("tiff","image/tiff");
    	EXT_MT.put("txt","text/plain");
    	EXT_MT.put("wav","audio/x-wav");
    	EXT_MT.put("wma","audio/x-ms-wma");
    	EXT_MT.put("wmv","video/x-ms-wmv");
    	EXT_MT.put("xbm","image/x-xbitmap");
    	EXT_MT.put("xhtml","application/xhtml+xml");
    	EXT_MT.put("xls","application/vnd.ms-excel");
    	EXT_MT.put("xlsx","application/vnd.ms-excel");
    	EXT_MT.put("xpm","image/x-xpixmap");
    	EXT_MT.put("zip","application/zip");
    	
    }
	

    //private static Magic mimeTypeParser; 
	
    /**
     * cast a String (argumet destination) to a File Object, 
     * if destination is not a absolute, file object will be relative to current position (get from PageContext)
     * file must exist otherwise throw exception
     * @param pc Page Context to et actuell position in filesystem
     * @param path relative or absolute path for file object
     * @return file object from destination
     * @throws ExpressionException
     */
    public static Resource toResourceExisting(PageContext pc ,String path) throws ExpressionException {
    	return toResourceExisting(pc, path,pc.getConfig().allowRealPath());
    }
    public static Resource toResourceExisting(PageContext pc ,String path,boolean allowRealpath) throws ExpressionException {
    	path=path.replace('\\','/');
    	Resource res = pc.getConfig().getResource(path);
        
        // not allow realpath
        if(!allowRealpath){
        	if(res.exists()) return res;
        	throw new ExpressionException("file or directory "+path+" not exist");  
        }
        
    	if(res.isAbsolute() && res.exists()) {
            return res;
        }
    	
        //if(allowRealpath){
	        if(StringUtil.startsWith(path,'/')) {
	        	PageContextImpl pci=(PageContextImpl) pc;
	        	ConfigWebImpl cwi=(ConfigWebImpl) pc.getConfig();
	        	Resource[] reses = cwi.getPhysicalResources(pc,pc.getApplicationContext().getMappings(),path,false,pci.useSpecialMappings(),true);
	        	if(!ArrayUtil.isEmpty(reses)) {
	        		for(int i=0;i<reses.length;i++){
	        			res=reses[i];
	        			if(res.exists()) return res;
	        		}
	        	}
	        	//res = pc.getPhysical(path,true);
	            //if(res!=null && res.exists()) return res;
	        }
	        res=ResourceUtil.getCanonicalResourceEL(pc.getCurrentPageSource().getPhyscalFile().getParentResource().getRealResource(path));
	        if(res.exists()) return res;
    	//}
        
        throw new ExpressionException("file or directory "+path+" not exist");      
    }
    
    public static Resource toResourceExisting(Config config ,String path) throws ExpressionException {
    	path=path.replace('\\','/');
    	Resource res = config.getResource(path);
        
        if(res.exists()) return res;
        throw new ExpressionException("file or directory "+path+" not exist");   
    }
    
    public static Resource toResourceNotExisting(Config config ,String path) {
    	Resource res;
        path=path.replace('\\','/');  
    	res=config.getResource(path);
    	return res;
    }
    
    

    /**
     * cast a String (argumet destination) to a File Object, 
     * if destination is not a absolute, file object will be relative to current position (get from PageContext)
     * at least parent must exist
     * @param pc Page Context to et actuell position in filesystem
     * @param destination relative or absolute path for file object
     * @return file object from destination
     * @throws ExpressionException
     */

    public static Resource toResourceExistingParent(PageContext pc ,String destination) throws ExpressionException {
    	return toResourceExistingParent(pc, destination, pc.getConfig().allowRealPath());
    }
    
    public static Resource toResourceExistingParent(PageContext pc ,String destination, boolean allowRealpath) throws ExpressionException {
    	destination=destination.replace('\\','/');
        Resource res=pc.getConfig().getResource(destination);
        
        // not allow realpath
        if(!allowRealpath){
        	if(res.exists() || parentExists(res))
        		return res;
        	throw new ExpressionException("parent directory "+res.getParent()+"  for file "+destination+" doesn't exist");
            
        }
        
        // allow realpath
        if(res.isAbsolute() && (res.exists() || parentExists(res))) {
        	return res;
        }
        //if(allowRealpath){
	        if(StringUtil.startsWith(destination,'/')) {
	        	PageContextImpl pci=(PageContextImpl) pc;
	        	ConfigWebImpl cwi=(ConfigWebImpl) pc.getConfig();
	        	Resource[] reses = cwi.getPhysicalResources(pc,pc.getApplicationContext().getMappings(),destination,false,pci.useSpecialMappings(),true);
	        	if(!ArrayUtil.isEmpty(reses)) {
	        		for(int i=0;i<reses.length;i++){
	        			res=reses[i];
	        			if(res.exists() || parentExists(res)) return res;
	        		}
	        	}
	            //res = pc.getPhysical(destination,true);
	            //if(res!=null && (res.exists() || parentExists(res))) return res;
	        }
	    	res=ResourceUtil.getCanonicalResourceEL(pc.getCurrentPageSource().getPhyscalFile().getParentResource().getRealResource(destination));
	        if(res!=null && (res.exists() || parentExists(res))) return res;
        //}
    
        throw new ExpressionException("parent directory "+res.getParent()+"  for file "+destination+" doesn't exist");
           
    }
    
    /**
     * cast a String (argument destination) to a File Object, 
     * if destination is not a absolute, file object will be relative to current position (get from PageContext)
     * existing file is prefered but dont must exist
     * @param pc Page Context to et actuell position in filesystem
     * @param destination relative or absolute path for file object
     * @return file object from destination
     */

    public static Resource toResourceNotExisting(PageContext pc ,String destination) {
    	return toResourceNotExisting(pc ,destination,pc.getConfig().allowRealPath());
    }
    
    public static Resource toResourceNotExisting(PageContext pc ,String destination,boolean allowRealpath) {
    	Resource res;
        destination=destination.replace('\\','/');  
    	
    	if(!allowRealpath){
    		res=pc.getConfig().getResource(destination);
    		return res;
    	}
    	
    	boolean isUNC;
        if(!(isUNC=isUNCPath(destination)) && StringUtil.startsWith(destination,'/')) {
        	PageContextImpl pci=(PageContextImpl) pc;
        	ConfigWebImpl cwi=(ConfigWebImpl) pc.getConfig();
        	Resource[] arr = cwi.getPhysicalResources(pc,pc.getApplicationContext().getMappings(),destination,false,pci.useSpecialMappings(),SystemUtil.isWindows());
        	if(!ArrayUtil.isEmpty(arr)) return arr[0];
        	//Resource res2 = pc.getPhysical(destination,SystemUtil.isWindows());
            //if(res2!=null) return res2;
        }
        if(isUNC) {
        	res=pc.getConfig().getResource(destination.replace('/','\\'));
        }
        else res=pc.getConfig().getResource(destination);
        if(res.isAbsolute()) return res;
        
        
        try {
        	return pc.getCurrentPageSource().getPhyscalFile().getParentResource().getRealResource(destination).getCanonicalResource();
        } 
        catch (IOException e) {}
        return res;
    }
    
	

    private static boolean isUNCPath(String path) {
        return SystemUtil.isWindows() && path.startsWith("//") ;
	}
    
    /**
     * transalte the path of the file to a existing file path by changing case of letters
     * Works only on Linux, becasue 
     * 
     * Example Unix:
     * we have a existing file with path "/usr/virtual/myFile.txt"
     * now you call this method with path "/Usr/Virtual/myfile.txt"
     * the result of the method will be "/usr/virtual/myFile.txt"
     * 
     * if there are more file with rhe same name but different cases
     * Example:
     *  /usr/virtual/myFile.txt
     *  /usr/virtual/myfile.txt
     *  /Usr/Virtual/myFile.txt
     *  the nearest case wil returned
     * 
     * @param res
     * @return file
     */
    public static Resource toExactResource(Resource res) {
        res=getCanonicalResourceEL(res);
        if(isUnix) {
            if(res.exists()) return res;
            return _check(res);
            
        }
        return res;
    }
    private static Resource _check(Resource file) {
    	// todo cascade durch while ersetzten
        Resource parent=file.getParentResource();
        if(parent==null) return file;
        
        if(!parent.exists()) {
            Resource op=parent;
            parent=_check(parent);
            if(op==parent) return file;
            if((file = parent.getRealResource(file.getName())).exists()) return file;
        }
        
        String[] files = parent.list();
        if(files==null) return file;
        String name=file.getName();
        for(int i=0;i<files.length;i++) {
            if(name.equalsIgnoreCase(files[i]))
                return parent.getRealResource(files[i]);
        }
        return file;
    }
    
    /**
     * create a file if possible, return file if ok, otherwise return null 
     * @param res file to touch 
     * @param level touch also parent and grand parent
     * @param type is file or directory
     * @return file if exists, otherwise null
     */
    public static Resource createResource(Resource res, short level, short type) {
        
        boolean asDir=type==TYPE_DIR;
        // File
        if(level>=LEVEL_FILE && res.exists() && ((res.isDirectory() && asDir)||(res.isFile() && !asDir))) {
            return getCanonicalResourceEL(res);
        }
        
        // Parent
        Resource parent=res.getParentResource();
        if(level>=LEVEL_PARENT_FILE && parent!=null && parent.exists() && canRW(parent)) {
            if(asDir) {
                if(res.mkdirs()) return getCanonicalResourceEL(res);
            }
            else {
                if(createNewResourceEL(res))return getCanonicalResourceEL(res);
            }
            return getCanonicalResourceEL(res);
        }    
        
        // Grand Parent
        if(level>=LEVEL_GRAND_PARENT_FILE && parent!=null) {
            Resource gparent=parent.getParentResource();
            if(gparent!=null && gparent.exists() && canRW(gparent)) {
                if(asDir) {
                    if(res.mkdirs())return getCanonicalResourceEL(res);
                }
                else {
                    if(parent.mkdirs() && createNewResourceEL(res))
                        return getCanonicalResourceEL(res);
                }
            }        
        }
        return null;
    }
    
	public static void setAttribute(Resource res,String attributes) throws IOException {
		/*if(res instanceof File && SystemUtil.isWindows()) {
			if(attributes.length()>0) {
				attributes=ResourceUtil.translateAttribute(attributes);
				Runtime.getRuntime().exec("attrib "+attributes+" " + res.getAbsolutePath());
	    	}
		}
		else {*/
			short[] flags = strAttrToBooleanFlags(attributes);
			
			if(flags[READ_ONLY]==YES)res.setWritable(false);
			else if(flags[READ_ONLY]==NO)res.setWritable(true);
			
			if(flags[HIDDEN]==YES)		res.setAttribute(Resource.ATTRIBUTE_HIDDEN, true);//setHidden(true);
			else if(flags[HIDDEN]==NO)	res.setAttribute(Resource.ATTRIBUTE_HIDDEN, false);//res.setHidden(false);
			
			if(flags[ARCHIVE]==YES)		res.setAttribute(Resource.ATTRIBUTE_ARCHIVE, true);//res.setArchive(true);
			else if(flags[ARCHIVE]==NO)	res.setAttribute(Resource.ATTRIBUTE_ARCHIVE, false);//res.setArchive(false);
			
			if(flags[SYSTEM]==YES)		res.setAttribute(Resource.ATTRIBUTE_SYSTEM, true);//res.setSystem(true);
			else if(flags[SYSTEM]==NO)	res.setAttribute(Resource.ATTRIBUTE_SYSTEM, false);//res.setSystem(false);
			
		//}
	}

	//private static final int NORMAL=0;
	private static final int READ_ONLY=0;
	private static final int HIDDEN=1;
	private static final int ARCHIVE=2;
	private static final int SYSTEM=3;

	//private static final int IGNORE=0;
	private static final int NO=1;
	private static final int YES=2;
	
	

    private static short[] strAttrToBooleanFlags(String attributes) throws IOException {
        
        String[] arr;
		try {
			arr = List.toStringArray(List.listToArrayRemoveEmpty(attributes.toLowerCase(),','));
		} 
		catch (PageException e) {
			arr=new String[0];
		}
        
        boolean hasNormal=false;
        boolean hasReadOnly=false;
        boolean hasHidden=false;
        boolean hasArchive=false;
        boolean hasSystem=false;
        
        for(int i=0;i<arr.length;i++) {
           String str=arr[i].trim().toLowerCase();
           if(str.equals("readonly") || str.equals("read-only") || str.equals("+r")) hasReadOnly=true;
           else if(str.equals("normal") || str.equals("temporary")) hasNormal=true;
           else if(str.equals("hidden") || str.equals("+h")) hasHidden=true;
           else if(str.equals("system") || str.equals("+s")) hasSystem=true;
           else if(str.equals("archive") || str.equals("+a")) hasArchive=true;
           else throw new IOException("invalid attribute definition ["+str+"]");
        }
        
        short[] flags=new short[4];
        
        if(hasReadOnly)flags[READ_ONLY]=YES;
        else if(hasNormal)flags[READ_ONLY]=NO;
        
        if(hasHidden)flags[HIDDEN]=YES;
        else if(hasNormal)flags[HIDDEN]=NO;
        
        if(hasSystem)flags[SYSTEM]=YES;
        else if(hasNormal)flags[SYSTEM]=NO;
        
        if(hasArchive)flags[ARCHIVE]=YES;
        else if(hasNormal)flags[ARCHIVE]=NO;
        
        return flags;
    }
	
	
	/**
     * sets attributes of a file on Windows system
     * @param res
     * @param attributes
     * @throws PageException
     * @throws IOException
     */
    public static String translateAttribute(String attributes) throws IOException {
        short[] flags = strAttrToBooleanFlags(attributes);
       
        StringBuilder sb=new StringBuilder();
        if(flags[READ_ONLY]==YES)sb.append(" +R");
        else if(flags[READ_ONLY]==NO)sb.append(" -R");
        
        if(flags[HIDDEN]==YES)sb.append(" +H");
        else if(flags[HIDDEN]==NO)sb.append(" -H");
        
        if(flags[SYSTEM]==YES)sb.append(" +S");
        else if(flags[SYSTEM]==NO)sb.append(" -S");
        
        if(flags[ARCHIVE]==YES)sb.append(" +A");
        else if(flags[ARCHIVE]==NO)sb.append(" -A");

        return sb.toString();
    }

	/* *
	 * transalte a path in a proper form
	 * example susi\petere -> /susi/peter
	 * @param path
	 * @return path
	 * /
	public static String translatePath(String path) {
		/*path=prettifyPath(path);
		if(path.indexOf('/')!=0)path='/'+path;
		int index=path.lastIndexOf('/');
		// remove slash at the end
		if(index==path.length()-1) path=path.substring(0,path.length()-1);
		return path;* /
		return translatePath(path, true, false);
	}*/
	
	/* *
	 * transalte a path in a proper form
	 * example susi\petere -> susi/peter/
	 * @param path
	 * @return path
	 * /
	public static String translatePath2x(String path) {
		/*path=prettifyPath(path);
		if(path.indexOf('/')==0)path=path.substring(1);
		int index=path.lastIndexOf('/');
		// remove slash at the end
		if(index!=path.length()-1) path=path+'/';* /
		return translatePath(path, false, true);
	}*/
	

	public static String translatePath(String path, boolean slashAdBegin, boolean slashAddEnd) {
		path=prettifyPath(path);
		
		// begin
		if(slashAdBegin) {
			if(path.indexOf('/')!=0)path='/'+path;
		}
		else {
			if(path.indexOf('/')==0)path=path.substring(1);
		}
		
		// end
		int index=path.lastIndexOf('/');
		if(slashAddEnd) {
			if(index!=path.length()-1) path=path+'/';
		}
		else {
			if(index==path.length()-1 && index>-1) path=path.substring(0,path.length()-1);
		}
		return path;
	}
	
	
	

	/**
	 * transalte a path in a proper form and cut name away
	 * example susi\petere -> /susi/ and  peter
	 * @param path
	 * @return
	 */
	public static String[] translatePathName(String path) {
		path=prettifyPath(path);
		if(path.indexOf('/')!=0)path='/'+path;
		int index=path.lastIndexOf('/');
		// remove slash at the end
		if(index==path.length()-1) path=path.substring(0,path.length()-1);
		
		index=path.lastIndexOf('/');
		String name;
		if(index==-1) {
			name=path;
			path = "/";
		}
		else {
			name = path.substring(index+1);
			path = path.substring(0,index+1);
		}
		return new String[] {path,name};
	}
	
	public static String prettifyPath(String path) {
		path=path.replace('\\','/');
		return StringUtil.replace(path, "//", "/", false);
		// TODO /aaa/../bbb/
	}

	public static String removeScheme(String scheme, String path) {
		if(path.indexOf("://")==scheme.length() && StringUtil.startsWithIgnoreCase(path,scheme)) path=path.substring(3+scheme.length());
		return path;
	}

	/**
	 * merge to path parts to one
	 * @param parent
	 * @param child
	 * @return
	 */
	public static String merge(String parent, String child) {
		if(child.length()<=2) {
			if(child.length()==0) return parent;
			if(child.equals(".")) return parent;
			if(child.equals("..")) child="../";
		}
		
		
		
		parent=translatePath(parent, true, false);
		child=prettifyPath(child);//child.replace('\\', '/');
		
		if(child.startsWith("./"))child=child.substring(2);
		if(StringUtil.startsWith(child, '/'))return parent.concat(child);
		if(!StringUtil.startsWith(child, '.'))return parent.concat("/").concat(child);
		
		
		while(child.startsWith("../")) {
			parent=pathRemoveLast(parent);
			child=child.substring(3);
		}
		if(StringUtil.startsWith(child, '/'))return parent.concat(child);
		return parent.concat("/").concat(child);
	}
	
	private static String pathRemoveLast(String path) {
		if(path.length()==0) return "..";
		
		else if(path.endsWith("..")){
		    return path.concat("/..");
		}
		return path.substring(0,path.lastIndexOf('/'));
	}

	/**
     * Returns the canonical form of this abstract pathname.
     * @param res file to get canoncial form from it
     *
     * @return  The canonical pathname string denoting the same file or
     *          directory as this abstract pathname
     *
     * @throws  SecurityException
     *          If a required system property value cannot be accessed.
     */
    public static String getCanonicalPathEL(Resource res) {
        try {
            return res.getCanonicalPath();
        } catch (IOException e) {
            return res.toString();
        }
    }
    
    
    /**
     * Returns the canonical form of this abstract pathname.
     * @param res file to get canoncial form from it
     *
     * @return  The canonical pathname string denoting the same file or
     *          directory as this abstract pathname
     *
     * @throws  SecurityException
     *          If a required system property value cannot be accessed.
     */
    public static Resource getCanonicalResourceEL(Resource res) {
        if(res==null) return res;
    	try {
            return res.getCanonicalResource();
        } catch (IOException e) {
            return res;
        }
    }
    
    /**
     * creates a new File
     * @param res
     * @return was successfull
     */
    public static boolean createNewResourceEL(Resource res) {
        try {
            res.createFile(false);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean exists(Resource res) {
        return res!=null && res.exists();
    }
    

    /**
     * check if file is read and writable
     * @param res
     * @return is or not
     */
    public static boolean canRW(Resource res) {
        return res.isReadable() && res.isWriteable();
    }
    

    /**
     * similat to linux bash fuction toch, create file if not exist oherwise change last modified date
     * @param res
     * @throws IOException
     */
    public static void touch(Resource res) throws IOException {
    	if(res.exists()) {
    		res.setLastModified(System.currentTimeMillis());
	    }
	    else {
	        res.createFile(true);
	    }
    }
    
    public static void clear(Resource res) throws IOException {
    	if(res.exists()) {
    		IOUtil.write(res, new byte[0]);
	    }
	    else {
	        res.createFile(true);
	    }
    }
    	
    

    /**
     * return the mime type of a file, dont check extension
     * @param res
     * @param defaultValue 
     * @return mime type of the file
     */
    public static String getMimeType(Resource res, String defaultValue) {
        return getMimeType(res, MIMETYPE_CHECK_HEADER,defaultValue);
    }
    
    public static String getMimeType(Resource res, int checkingType, String defaultValue) {
        
    	// check Extension
    	if((checkingType&MIMETYPE_CHECK_EXTENSION)!=0) {
        	String ext = getExtension(res, null);
			if(!StringUtil.isEmpty(ext)){
        		String mt=EXT_MT.get(ext.trim().toLowerCase());
        		if(mt!=null) return mt;
			}
        }
    	
    	// check mimetype
    	if((checkingType&MIMETYPE_CHECK_HEADER)!=0) {
    		InputStream is=null;
    		try {
    			is = res.getInputStream();
    			return IOUtil.getMimeType(is, defaultValue);
			} 
    		catch (Throwable t) {
				return defaultValue;
			}
    		finally {
    			IOUtil.closeEL(is);
    		}
    	}
    	
    	return defaultValue;
    }

    
    
    
	/**
	 * check if file is a child of given directory
	 * @param file file to search
	 * @param dir directory to search
	 * @return is inside or not
	 */
	public static boolean isChildOf(Resource file, Resource dir) {
		while(file!=null) {
			if(file.equals(dir)) return true;
			file=file.getParentResource();
		}
		return false;
	}
	/**
	 * return diffrents of one file to a other if first is child of second otherwise return null
	 * @param file file to search
	 * @param dir directory to search
	 */
	public static String getPathToChild(Resource file, Resource dir) {
		if(dir==null || !file.getResourceProvider().getScheme().equals(dir.getResourceProvider().getScheme())) return null;
		boolean isFile=file.isFile();
		String str="/";
		while(file!=null) {
			if(file.equals(dir)) {
				if(isFile) return str.substring(0,str.length()-1);
				return str;
			}
			str="/"+file.getName()+str;
			file=file.getParentResource();
		}
		return null;
	}
	
    /**
     * get the Extension of a file
     * @param res
     * @return extension of file
     */
    public static String getExtension(Resource res, String defaultValue) {
        return getExtension(res.getName(),defaultValue);
    }

    /**
     * get the Extension of a file
     * @param strFile
     * @return extension of file
     */
    public static String getExtension(String strFile, String defaultValue) {
        int pos=strFile.lastIndexOf('.');
        if(pos==-1)return defaultValue;
        return strFile.substring(pos+1);
    }
    
    public static String getName(String strFileName) {
        int pos=strFileName.lastIndexOf('.');
        if(pos==-1)return strFileName;
        return strFileName.substring(0,pos);
    }
    
    /**
     * split a FileName in Parts
     * @param fileName
     * @return new String[]{name[,extension]}
     */
    public static String[] splitFileName(String fileName) {
        int pos=fileName.lastIndexOf('.');
        if(pos==-1) {
            return new String[]{fileName};
        }
        return new String[]{fileName.substring(0,pos),fileName.substring(pos+1)};
    }
    
    /**
     * change extesnion of file and return new file
     * @param file
     * @param newExtension
     * @return  file with new Extension
     */
    public static Resource changeExtension(Resource file, String newExtension) {
        String ext=getExtension(file,null);
        if(ext==null) return file.getParentResource().getRealResource(file.getName()+'.'+newExtension);
        //new File(file.getParentFile(),file.getName()+'.'+newExtension);
        String name=file.getName();
        return file.getParentResource().getRealResource(name.substring(0,name.length()-ext.length())+newExtension);
        //new File(file.getParentFile(),name.substring(0,name.length()-ext.length())+newExtension);
    }
    
    /**
     * @param res delete the content of a directory
     */

    public static void deleteContent(Resource src,ResourceFilter filter) {
    	_deleteContent(src, filter,false);
    }
    public static void _deleteContent(Resource src,ResourceFilter filter,boolean deleteDirectories) {
    	if(src.isDirectory()) {
        	Resource[] files=filter==null?src.listResources():src.listResources(filter);
            for(int i=0;i<files.length;i++) {
            	_deleteContent(files[i],filter,true);
            	if(deleteDirectories){
            		try {
						src.remove(false);
					} catch (IOException e) {}
            	}
            }
            
        }
        else if(src.isFile()) {
        	src.delete();
        }
    }
    

    /**
     * copy a file or directory recursive (with his content)
     * @param res file or directory to delete
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static void copyRecursive(Resource src,Resource trg) throws IOException {
		copyRecursive(src, trg,null);
	}
    
    
    /**
     * copy a file or directory recursive (with his content)
     * @param src
     * @param trg
     * @param filter
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static void copyRecursive(Resource src,Resource trg,ResourceFilter filter) throws IOException {
    	//print.out(src);
    	//print.out(trg);
        if(!src.exists()) return ;
        if(src.isDirectory()) {
        	if(!trg.exists())trg.createDirectory(true);
        	Resource[] files=filter==null?src.listResources():src.listResources(filter);
            for(int i=0;i<files.length;i++) {
            	copyRecursive(files[i],trg.getRealResource(files[i].getName()),filter);
            }
        }
        else if(src.isFile()) {
        	touch(trg);
        	IOUtil.copy(src,trg);
        }
    }
    
	public static void copy(Resource src, Resource trg) throws IOException {
		if(src.equals(trg)) return;
		ResourceUtil.checkCopyToOK(src,trg);
		IOUtil.copy(src,trg);
	}
    
    
    /**
     * return if parent file exists 
     * @param res file to check
     * @return parent exists?
     */
    private static boolean parentExists(Resource res) {
        res=res.getParentResource();
        return res!=null && res.exists();
    }

	public static void removeChildren(Resource res) throws IOException {
		removeChildren(res, (ResourceFilter)null);
	}

	public static void removeChildren(Resource res,ResourceNameFilter filter) throws IOException {
		Resource[] children = filter==null?res.listResources():res.listResources(filter);
		if(children==null) return;
		
		for(int i=0;i<children.length;i++) {
			children[i].remove(true);
		}
	}
	
	public static void removeChildren(Resource res,ResourceFilter filter) throws IOException {
		Resource[] children = filter==null?res.listResources():res.listResources(filter);
		if(children==null) return;
		
		for(int i=0;i<children.length;i++) {
			children[i].remove(true);
		}
	}

	public static void removeChildrenEL(Resource res,ResourceNameFilter filter) {
		try {
			removeChildren(res,filter);
		}
		catch(Throwable e) {}
	}

	public static void removeChildrenEL(Resource res,ResourceFilter filter) {
		try {
			removeChildren(res,filter);
		}
		catch(Throwable e) {}
	}
	
	public static void removeChildrenEL(Resource res) {
		try {
			removeChildren(res);
		}
		catch(Throwable e) {}
	}

	public static void removeEL(Resource res, boolean force) {
		try {
			res.remove(force);
		} 
		catch (Throwable t) {}
	}

	public static void createFileEL(Resource res, boolean force) {
		try {
			res.createFile(force);
		} 
		catch (IOException e) {}
	}

	public static void createDirectoryEL(Resource res, boolean force) {
		try {
			res.createDirectory(force);
		} 
		catch (IOException e) {}
	}

	public static ContentType getContentType(Resource resource) {
		// TODO make this part of a interface
		if(resource instanceof HTTPResource) {
			try {
				return ((HTTPResource)resource).getContentType();
			} catch (IOException e) {}
		}
		InputStream is=null;
		try {
			is = resource.getInputStream();
			return new ContentTypeImpl(is);
		}
		catch(IOException e) {
			return ContentTypeImpl.APPLICATION_UNKNOW;
		}
		finally {
			IOUtil.closeEL(is);
		}
	}
	
	public static void moveTo(Resource src, Resource dest) throws IOException {
		ResourceUtil.checkMoveToOK(src, dest);
		
		if(src.isFile()){
			if(!dest.exists()) dest.createFile(false);
			IOUtil.copy(src,dest);
			src.remove(false);
		}
		else {
			if(!dest.exists()) dest.createDirectory(false);
			Resource[] children = src.listResources();
			for(int i=0;i<children.length;i++){
				moveTo(children[i],dest.getRealResource(children[i].getName()));
			}
			src.remove(false);
		}
		dest.setLastModified(System.currentTimeMillis());
	}

	/**
	 * return the size of the Resource, other than method length of Resource this mthod return the size of all files in a directory
	 * @param collectionDir
	 * @return
	 */
	public static long getRealSize(Resource res) {
		return getRealSize(res,null);
	}
	
	/**
	 * return the size of the Resource, other than method length of Resource this mthod return the size of all files in a directory
	 * @param collectionDir
	 * @return
	 */
	public static long getRealSize(Resource res, ResourceFilter filter) {
		if(res.isFile()) {
			return res.length();
		}
		else if(res.isDirectory()) {
			long size=0;
			Resource[] children = filter==null?res.listResources():res.listResources(filter);
			for(int i=0;i<children.length;i++) {
				size+=getRealSize(children[i]);
			}
			return size;
		}
		
		return 0;
	}


	/**
	 * return if Resource is empty, means is directory and has no children or a empty file,
	 * if not exist return false.
	 * @param res
	 * @return
	 */
	public static boolean isEmpty(Resource res) {
		return isEmptyDirectory(res) || isEmptyFile(res);
	}

	public static boolean isEmptyDirectory(Resource res) {
		if(res.isDirectory()) {
			String[] children = res.list();
			return children==null || children.length==0;
		}
		return false;
	}
	
	public static boolean isEmptyFile(Resource res) {
		if(res.isFile()) {
			return res.length()==0;
		}
		return false;
	}

	public static Resource toResource(File file) {
		return ResourcesImpl.getFileResourceProvider().getResource(file.getPath());
	}


	/**
	 * list childrn of all given resources
	 * @param resources
	 * @return
	 */
	public static Resource[] listResources(Resource[] resources,ResourceFilter filter) {
		int count=0;
		Resource[] children;
		ArrayList<Resource[]> list=new ArrayList<Resource[]>();
		for(int i=0;i<resources.length;i++) {
			children=filter==null?resources[i].listResources():resources[i].listResources(filter);
			if(children!=null){
				count+=children.length;
				list.add(children);
			}
			else list.add(new Resource[0]);
		}
		Resource[] rtn=new Resource[count];
		int index=0;
		for(int i=0;i<resources.length;i++) {
			children=list.get(i);
			for(int y=0;y<children.length;y++) {
				rtn[index++]=children[y];
			}
		}
		//print.out(rtn);
		return rtn;
	}


	public static Resource[] listResources(Resource res,ResourceFilter filter) {
		return filter==null?res.listResources():res.listResources(filter);
	}
	

	public static void deleteFileOlderThan(Resource res, long date, ExtensionResourceFilter filter) {
		if(res.isFile()) {
			if(res.lastModified()<=date) res.delete();
		}
		else if(res.isDirectory()) {
			Resource[] children = filter==null?res.listResources():res.listResources(filter);
			for(int i=0;i<children.length;i++) {
				deleteFileOlderThan(children[i],date,filter);
			}
		}
	}
	
	/**
	 * check if directory creation is ok with the rules for the Resource interface, to not change this rules.
	 * @param resource
	 * @param createParentWhenNotExists
	 * @throws IOException
	 */
	public static void checkCreateDirectoryOK(Resource resource, boolean createParentWhenNotExists) throws IOException {
		if(resource.exists()) {
			if(resource.isFile()) 
				throw new IOException("can't create directory ["+resource.getPath()+"], resource already exists as a file");
			if(resource.isDirectory()) 
				throw new IOException("can't create directory ["+resource.getPath()+"], directory already exists");
		}
		
		Resource parent = resource.getParentResource();
		// when there is a parent but the parent does not exist
		if(parent!=null) {
			if(!parent.exists()) {
				if(createParentWhenNotExists)parent.createDirectory(true);
				else throw new IOException("can't create file ["+resource.getPath()+"], missng parent directory");
			}
			else if(parent.isFile()) {
				throw new IOException("can't create directory ["+resource.getPath()+"], parent is a file");
			}
		}
	}


	/**
	 * check if file creating is ok with the rules for the Resource interface, to not change this rules.
	 * @param resource
	 * @param createParentWhenNotExists
	 * @throws IOException
	 */
	public static void checkCreateFileOK(Resource resource, boolean createParentWhenNotExists) throws IOException {
		if(resource.exists()) {
			if(resource.isDirectory()) 
				throw new IOException("can't create file ["+resource.getPath()+"], resource already exists as a directory");
			if(resource.isFile()) 
				throw new IOException("can't create file ["+resource.getPath()+"], file already exists");
		}
		
		Resource parent = resource.getParentResource();
		// when there is a parent but the parent does not exist
		if(parent!=null) {
			if(!parent.exists()) {
				if(createParentWhenNotExists)parent.createDirectory(true);
				else throw new IOException("can't create file ["+resource.getPath()+"], missng parent directory");
			}
			else if(parent.isFile()) {
				throw new IOException("can't create file ["+resource.getPath()+"], parent is a file");
			}
		}
	}

	/**
	 * check if copying a file is ok with the rules for the Resource interface, to not change this rules.
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	public static void checkCopyToOK(Resource source, Resource target) throws IOException {
		if(!source.isFile()) {
			if(source.isDirectory())
				throw new IOException("can't copy ["+source.getPath()+"] to ["+target.getPath()+"], source is a directory");
			throw new IOException("can't copy ["+source.getPath()+"] to ["+target.getPath()+"], source file does not exist");
		}
		else if(target.isDirectory()) {
			throw new IOException("can't copy ["+source.getPath()+"] to ["+target.getPath()+"], target is a directory");
		}
	}

	/**
	 * check if moveing a file is ok with the rules for the Resource interface, to not change this rules.
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	public static void checkMoveToOK(Resource source, Resource target) throws IOException {
		if(!source.exists()) {
			throw new IOException("can't move ["+source.getPath()+"] to ["+target.getPath()+"], source file does not exist");
		}
		if(source.isDirectory() && target.isFile())
			throw new IOException("can't move ["+source.getPath()+"] directory to ["+target.getPath()+"], target is a file");
		if(source.isFile() && target.isDirectory())
			throw new IOException("can't move ["+source.getPath()+"] file to ["+target.getPath()+"], target is a directory");
	}

	/**
	 * check if getting a inputstream of the file is ok with the rules for the Resource interface, to not change this rules.
	 * @param resource
	 * @throws IOException
	 */
	public static void checkGetInputStreamOK(Resource resource) throws IOException {
		if(!resource.exists())
			throw new IOException("file ["+resource.getPath()+"] does not exist");
		
		if(resource.isDirectory())
			throw new IOException("can't read directory ["+resource.getPath()+"] as a file");

	}

	/**
	 * check if getting a outputstream of the file is ok with the rules for the Resource interface, to not change this rules.
	 * @param resource
	 * @throws IOException
	 */
	public static void checkGetOutputStreamOK(Resource resource) throws IOException {
		if(resource.exists() && !resource.isWriteable()) {
			throw new IOException("can't write to file ["+resource.getPath()+"],file is readonly");
		}
		if(resource.isDirectory())
			throw new IOException("can't write directory ["+resource.getPath()+"] as a file");
		if(!resource.getParentResource().exists())
			throw new IOException("can't write file ["+resource.getPath()+"] as a file, missing parent directory ["+resource.getParent()+"]");
	}

	/**
	 * check if removing the file is ok with the rules for the Resource interface, to not change this rules.
	 * @param resource
	 * @throws IOException
	 */
	public static void checkRemoveOK(Resource resource) throws IOException {
		if(!resource.exists())throw new IOException("can't delete resource "+resource+", resource does not exist");
		if(!resource.canWrite())throw new IOException("can't delete resource "+resource+", no access");
		
	}
	
	public static void deleteEmptyFolders(Resource res) throws IOException {
		if(res.isDirectory()){
			Resource[] children = res.listResources();
			for(int i=0;i<children.length;i++){
				deleteEmptyFolders(children[i]);
			}
			if(res.listResources().length==0){
				res.remove(false);
			}
		}
	}
	
	/**
     * if the pageSource is based on a archive, translate the source to a zip:// Resource
     * @return return the Resource matching this PageSource
     * @param pc the Page Context Object
     * @deprecated use instead <code>PageSource.getResourceTranslated(PageContext)</code>
     */
    public static Resource getResource(PageContext pc,PageSource ps) throws PageException {
		return ps.getResourceTranslated(pc);
	}
	
	public static Resource getResource(PageContext pc,PageSource ps, Resource defaultValue) {
		try {
			return ps.getResourceTranslated(pc);
		} 
		catch (Throwable t) {
			return defaultValue;
		}
	}
	
	public static int directrySize(Resource dir,ResourceFilter filter) {
		if(dir==null || !dir.isDirectory()) return 0;
		if(filter==null) return dir.list().length;
		return dir.list(filter).length;
	}
	
	public static int directrySize(Resource dir,ResourceNameFilter filter) {
		if(dir==null || !dir.isDirectory()) return 0;
		if(filter==null) return dir.list().length;
		return dir.list(filter).length;
	}
	
	public static String[] names(Resource[] resources) {
		String[] names=new String[resources.length];
		for(int i=0;i<names.length;i++){
			names[i]=resources[i].getName();
		}
		return names;
	}

}
