package railo.runtime.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import railo.print;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.LogAndSource;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.util.FileWrapper;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.Info;
import railo.runtime.config.Config;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public class DeployHandler {
	
	

	private static final ResourceFilter ALL_EXT = new ExtensionResourceFilter(new String[]{".re",".ra",".ras"});
	//private static final ResourceFilter ARCHIVE_EXT = new ExtensionResourceFilter(new String[]{".ra",".ras"});


	public static void deploy(Config config){
		Resource dir = config.getConfigDir().getRealResource("deploy");
		int ma = Info.getMajorVersion();
		int mi = Info.getMinorVersion();
		if(!dir.exists()) {
			if(ma>4 || ma==4 && mi>1) {// FUTURE remove the if contition
				dir.mkdirs();
			}
			return;
		}
		
		Resource[] children = dir.listResources(ALL_EXT);
		Resource child;
		String ext;
		for(int i=0;i<children.length;i++){
			child=children[i];
			try {
			// Railo archives
			ext=ResourceUtil.getExtension(child, null);
			if("ra".equalsIgnoreCase(ext) || "ras".equalsIgnoreCase(ext)) {
				deployArchive(config,child);
			}
			
			// Railo Extensions
			else if("re".equalsIgnoreCase(ext))
				deployExtension(config, child);
			}
			catch (ZipException e) {
				SystemOut.printDate(config.getErrWriter(),ExceptionUtil.getStacktrace(e, true));
			}
			catch (IOException e) {
				SystemOut.printDate(config.getErrWriter(),ExceptionUtil.getStacktrace(e, true));
			}
		}
	}

	private static void deployArchive(Config config,Resource archive) throws ZipException, IOException {
		ZipFile file=new ZipFile(FileWrapper.toFile(archive));
		ZipEntry entry = file.getEntry("META-INF/MANIFEST.MF");
		
		LogAndSource log = ((ConfigImpl)config).getDeployLogger();
		// no manifest
		if(entry==null) {
			log.error("archive","cannot deploy Railo Archive ["+archive+"], file is to old, the file does not have a MANIFEST.");
			moveToFailedFolder(archive);
			return;
		}
		String type=null,virtual=null,name=null;
		boolean readOnly,topLevel,trusted,hidden,physicalFirst;
		InputStream is = null;
		try{
			is = file.getInputStream(entry);
			Manifest manifest = new Manifest(is);
		    Attributes attr = manifest.getMainAttributes();
		    
		    //id = unwrap(attr.getValue("mapping-id"));
		    type = unwrap(attr.getValue("mapping-type"));
		    virtual = unwrap(attr.getValue("mapping-virtual-path"));
		    name = ListUtil.trim(virtual, "/");
		    readOnly = Caster.toBooleanValue(unwrap(attr.getValue("mapping-readonly")),false);
		    topLevel = Caster.toBooleanValue(unwrap(attr.getValue("mapping-top-level")),false);
		    trusted = Caster.toBooleanValue(unwrap(attr.getValue("mapping-trusted")),false);
		    hidden = Caster.toBooleanValue(unwrap(attr.getValue("mapping-hidden")),false);
		    physicalFirst = Caster.toBooleanValue(unwrap(attr.getValue("mapping-physical-first")),false);
		}
		finally{
			IOUtil.closeEL(is);
		}
		Resource trgDir = config.getConfigDir().getRealResource("archives").getRealResource(type).getRealResource(name);
		Resource trgFile = trgDir.getRealResource(archive.getName());
		trgDir.mkdirs();
		
		// delete existing files
		ResourceUtil.deleteContent(trgDir, null);
		ResourceUtil.moveTo(archive, trgFile);
		
		try {
			log.info("archive","add "+type+" mapping ["+virtual+"] with archive ["+trgFile.getAbsolutePath()+"]");
			if("regular".equalsIgnoreCase(type))
				ConfigWebAdmin.updateMapping((ConfigImpl)config,virtual, null, trgFile.getAbsolutePath(), "archive", trusted, topLevel);
			else if("cfc".equalsIgnoreCase(type))
				ConfigWebAdmin.updateComponentMapping((ConfigImpl)config,virtual, null, trgFile.getAbsolutePath(), "archive", trusted);
			else if("ct".equalsIgnoreCase(type))
				ConfigWebAdmin.updateCustomTagMapping((ConfigImpl)config,virtual, null, trgFile.getAbsolutePath(), "archive", trusted);
			
		    
		}
		catch (Throwable t) {
			moveToFailedFolder(archive);
			log.error("archive",ExceptionUtil.getStacktrace(t, true));
		}
	}
	

	private static void deployExtension(Config config, Resource ext) {
		boolean isWeb=config instanceof ConfigWeb;
		String type=isWeb?"web":"server";
		LogAndSource log = ((ConfigImpl)config).getDeployLogger();
		
		// Manifest
		Manifest manifest = null;
		ZipInputStream zis=null;
        try {
	        zis = new ZipInputStream( IOUtil.toBufferedInputStream(ext.getInputStream()) ) ;     
	        ZipEntry entry;
	        String name;
	        while ( ( entry = zis.getNextEntry()) != null ) {
	        	name=entry.getName();
	        	if(!entry.isDirectory() && name.equalsIgnoreCase("META-INF/MANIFEST.MF")) {
	        		manifest = toManifest(config,zis,null);
	        	}
	            zis.closeEntry() ;
	        }
        }
        catch(Throwable t){
        	log.error("extension", ExceptionUtil.getStacktrace(t, true));
			moveToFailedFolder(ext);
			return;
        }
        finally {
        	IOUtil.closeEL(zis);
        }
        
        int minCoreVersion=0;
        double minLoaderVersion=0;
        String strMinCoreVersion="",strMinLoaderVersion="",version=null,name=null;
        
        if(manifest!=null) {
        	Attributes attr = manifest.getMainAttributes();
        	// version
        	version=unwrap(attr.getValue("version"));

        	// name
        	name=unwrap(attr.getValue("name"));

        	// core version
        	strMinCoreVersion=unwrap(attr.getValue("railo-core-version"));
        	minCoreVersion=Info.toIntVersion(strMinCoreVersion,minCoreVersion);
        	
        	// loader version
        	strMinLoaderVersion=unwrap(attr.getValue("railo-loader-version"));
        	minLoaderVersion=Caster.toDoubleValue(strMinLoaderVersion,minLoaderVersion);
        	
        }
        if(StringUtil.isEmpty(name,true)) {
        	name=ext.getName();
        	int index=name.lastIndexOf('.');
        	name=name.substring(0,index-1);
        }
        name=name.trim();
        
        
        // check core version
		if(minCoreVersion>Info.getVersionAsInt()) {
			log.error("extension", "cannot deploy Railo Extension ["+ext+"], Railo Version must be at least ["+strMinCoreVersion+"].");
			moveToFailedFolder(ext);
			return;
		}
		
		// check loader version
		if(minLoaderVersion>SystemUtil.getLoaderVersion()) {
			log.error("extension", "cannot deploy Railo Extension ["+ext+"], Railo Loader Version must be at least ["+strMinLoaderVersion+"], update the railo.jar first.");
			moveToFailedFolder(ext);
			return;
		}
		
		Resource trgFile=null;
		try{
			Resource trgDir = config.getConfigDir().getRealResource("extensions").getRealResource(type).getRealResource(name);
			trgFile = trgDir.getRealResource(ext.getName());
			trgDir.mkdirs();
			ResourceUtil.moveTo(ext, trgFile);
		}
	    catch(Throwable t){
	    	log.error("extension", ExceptionUtil.getStacktrace(t, true));
			moveToFailedFolder(ext);
			return;
	    }
	    
		try {
	        zis = new ZipInputStream( IOUtil.toBufferedInputStream(trgFile.getInputStream()) ) ;     
	        ZipEntry entry;
	        String path;
	        String fileName;
	        while ( ( entry = zis.getNextEntry()) != null ) {
	        	path=entry.getName();
	        	print.e(path);
	        	fileName=fileName(entry);
	        	// jars
	        	if(!entry.isDirectory() && (startsWith(path,type,"jars") || startsWith(path,type,"jar") || startsWith(path,type,"lib") || startsWith(path,type,"libs")) && StringUtil.endsWithIgnoreCase(path, ".jar")) {
	        		log.info("extension","deploy jar "+fileName);
	        		ConfigWebAdmin.updateJar(config,zis,fileName,false);
	        	}
	        	
	        	// flds
	        	if(!entry.isDirectory() && startsWith(path,type,"flds") && StringUtil.endsWithIgnoreCase(path, ".fld")) {
	        		log.info("extension","deploy fld "+fileName);
	        		ConfigWebAdmin.updateFLD(config, zis, fileName,false);
	        	}
	        	
	        	// tlds
	        	if(!entry.isDirectory() && startsWith(path,type,"tlds") && StringUtil.endsWithIgnoreCase(path, ".tld")) {
	        		log.info("extension","deploy tld "+fileName);
	        		ConfigWebAdmin.updateTLD(config, zis, fileName,false);
	        	}
	        	
	        	// context
	        	String realpath;
	        	if(!entry.isDirectory() && startsWith(path,type,"context") && !StringUtil.startsWith(fileName(entry), '.')) {
	        		realpath=path.substring(8);
	        		//log.info("extension","deploy context "+realpath);
	        		log.info("extension","deploy context "+realpath);
	        		ConfigWebAdmin.updateContext((ConfigImpl)config, zis, realpath,false);
	        	}
	            zis.closeEntry() ;
	        }
        }
	    catch(Throwable t){
	    	log.error("extension",ExceptionUtil.getStacktrace(t, true));
			moveToFailedFolder(trgFile);
			return;
	    }
        finally {
        	IOUtil.closeEL(zis);
        }
	}

	private static Manifest toManifest(Config config,InputStream is, Manifest defaultValue) {
		try {
			String cs = config.getResourceCharset();
			String str = IOUtil.toString(is,cs);
			if(StringUtil.isEmpty(str,true)) return defaultValue;
			str=str.trim()+"\n";
			return new Manifest(new ByteArrayInputStream(str.getBytes(cs)));
		}
		catch (Throwable t) {
			return defaultValue;
		}
	}

	private static boolean startsWith(String path,String type, String name) {
		return StringUtil.startsWithIgnoreCase(path, name+"/") || StringUtil.startsWithIgnoreCase(path, type+"/"+name+"/");
	}

	private static String fileName(ZipEntry entry) {
		String name = entry.getName();
		int index=name.lastIndexOf('/');
		if(index==-1) return name;
		return name.substring(index+1);
	}

	private static void moveToFailedFolder(Resource archive) {
		Resource dir = archive.getParentResource().getRealResource("failed-to-deploy");
		Resource dst = dir.getRealResource(archive.getName());
		dir.mkdirs();
		
		try {
			if(dst.exists()) dst.remove(true);
			ResourceUtil.moveTo(archive, dst);
		}
		catch (Throwable t) {}
		
		// TODO Auto-generated method stub
		
	}

	private static String unwrap(String value) {
		if(value==null) return "";
		String res = unwrap(value,'"');
		if(res!=null) return res; // was double quote
		
		return unwrap(value,'\''); // try single quote unwrap, when there is no double quote.
	}
	
	private static String unwrap(String value, char del) {
		value=value.trim();
		if(StringUtil.startsWith(value, del) && StringUtil.endsWith(value, del)) {
			return value.substring(1, value.length()-1);
		}
		return value;
	}
	
}
