package railo.runtime.config;

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
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.util.FileWrapper;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.Info;
import railo.runtime.config.Config;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public class DeployHandler {
	
	

	private static final ResourceFilter ALL_EXT = new ExtensionResourceFilter(new String[]{".re",".ra",".ras"});
	private static final ResourceFilter ARCHIVE_EXT = new ExtensionResourceFilter(new String[]{".ra",".ras"});


	public static void deploy(Config config){
		Resource dir = config.getConfigDir().getRealResource("deploy");
		int ma = Info.getMajorVersion();
		int mi = Info.getMinorVersion();
		if(!dir.exists()) {
			if(!dir.isDirectory()) {
				SystemOut.printDate(config.getErrWriter(),"["+dir+"] is not a accesible directory");
			}
			else if(ma>4 || ma==4 && mi>1) {// FUTURE remove the if contition
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
			else
				deployExtension(config, child);
			}
			catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void deployArchive(Config config,Resource archive) throws ZipException, IOException {
		ZipFile file=new ZipFile(FileWrapper.toFile(archive));
		ZipEntry entry = file.getEntry("META-INF/MANIFEST.MF");
		
		// no manifest
		if(entry==null) {
			SystemOut.printDate(config.getErrWriter(),"cannot deploy Railo Archive ["+archive+"], file is to old, the file does not have a MANIFEST.");
			moveToFailedFolder(archive);
			return;
		}
		String id=null,type=null,virtual=null,name=null;
		boolean readOnly,topLevel,trusted,hidden,physicalFirst;
		InputStream is = null;
		try{
			is = file.getInputStream(entry);
			Manifest manifest = new Manifest(is);
		    Attributes attr = manifest.getMainAttributes();
		    
		    id = unwrap(attr.getValue("mapping-id"));
		    type = unwrap(attr.getValue("mapping-type"));
		    virtual = unwrap(attr.getValue("mapping-virtual-path"));
		    name = ListUtil.trim(virtual, "/");
		    readOnly = Caster.toBooleanValue(unwrap(attr.getValue("mapping-readonly")),false);
		    topLevel = Caster.toBooleanValue(unwrap(attr.getValue("mapping-top-level")),false);
		    trusted = Caster.toBooleanValue(unwrap(attr.getValue("mapping-trusted")),false);
		    hidden = Caster.toBooleanValue(unwrap(attr.getValue("mapping-hidden")),false);
		    physicalFirst = Caster.toBooleanValue(unwrap(attr.getValue("mapping-physical-first")),false);
		    
		    //print.e("name:"+name);
		    //print.e("virtual:"+virtual);
		    //print.e("type:"+type);
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
			SystemOut.printDate(config.getOutWriter(),"add "+type+" mapping ["+virtual+"] with archive ["+trgFile.getAbsolutePath()+"]");
			if("regular".equalsIgnoreCase(type))
				ConfigWebAdmin.updateMapping((ConfigImpl)config,virtual, null, trgFile.getAbsolutePath(), "archive", trusted, topLevel);
			else if("cfc".equalsIgnoreCase(type))
				ConfigWebAdmin.updateComponentMapping((ConfigImpl)config,virtual, null, trgFile.getAbsolutePath(), "archive", trusted);
			else if("ct".equalsIgnoreCase(type))
				ConfigWebAdmin.updateCustomTagMapping((ConfigImpl)config,virtual, null, trgFile.getAbsolutePath(), "archive", trusted);
			
		    
		}
		catch (Throwable t) {
			moveToFailedFolder(archive);
			t.printStackTrace();
		}
		// component archive
		/*else if("cfcx".equalsIgnoreCase(type)){
			
			if(StringUtil.isEmpty(id)) {
				SystemOut.printDate(config.getErrWriter(),"cannot deploy Railo Component Archive ["+archive+"], file is to old, the file does not have a id defintion in the MANIFEST.");
				return;
			}
			
			
			Resource trgDir = config.getConfigDir().getRealResource("archives").getRealResource(id);
			Resource trgFile = trgDir.getRealResource(archive.getName());
			trgDir.mkdirs();
			// delete existing files
			ResourceUtil.deleteContent(trgDir, null);
			ResourceUtil.moveTo(archive, trgFile);
			
			try {
				SystemOut.printDate(config.getOutWriter(),"add component mapping ["+virtual+"] with archive ["+trgFile.getAbsolutePath()+"]");
				ConfigWebAdmin.updateComponentMapping((ConfigImpl)config,virtual, null, trgFile.getAbsolutePath(), "archive", trusted);
			    
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		}*/
		
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
	

	private static void deployExtension(Config config, Resource ext) throws ZipException, IOException {
		print.e(ext);
		//ZipFile file=new ZipFile(FileWrapper.toFile(ext));
		
		ZipInputStream zis=null;
        try {
	        zis = new ZipInputStream( IOUtil.toBufferedInputStream(ext.getInputStream()) ) ;     
	        ZipEntry entry;
	        String name;
	        while ( ( entry = zis.getNextEntry()) != null ) {
	        	name=entry.getName();
	        	
	        	// jars/, plugins/, applications/, tags/, functions/,
	        	
	        	// jars
	        	if(StringUtil.startsWithIgnoreCase(name, "jars/") && StringUtil.endsWithIgnoreCase(name, ".jar")) {
	        		print.e(name);
	        		
	        		ConfigWebAdmin.updateJar(config,ext);
	        	}
	        	
	        	
	        	/*Resource target=targetDir.getRealResource(entry.getName());
	            if(entry.isDirectory()) {
	                target.mkdirs();
	            }
	            else {
	            	Resource parent=target.getParentResource();
	                if(!parent.exists())parent.mkdirs();
	                IOUtil.copy(zis,target,false);
	            }
	            target.setLastModified(entry.getTime());
	            */
	            zis.closeEntry() ;
	        }
        }
        finally {
        	IOUtil.closeEL(zis);
        }
		
		//Compress c = Compress.getInstance(ext, Compress.FORMAT_ZIP,true);
		
	}
	
}
