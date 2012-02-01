import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.objectweb.asm.commons.Method;

import railo.aprint;
import railo.print;
import railo.commons.io.CompressUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.Info;

public class WriteOSJar_2 implements ResourceFilter {

    
    private final static Method CONSTRUCTOR = Method.getMethod("void <init> ()");
	private static final Method GET_CODE = Method.getMethod("String getCode()");
	private static final Method NOPE = Method.getMethod("void nope()");
	private static String license;

    
    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        
    	ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
    	Resource pmf=frp.getResource(WriteOSJar_1.pathRailo+"Source2/"+WriteOSJar_1.projectNameMain);
       
    	Resource mainFolder=pmf.getRealResource("bin");
        main(mainFolder);
        
        //mainFolder=pmf.getRealResource("src");
        //main(mainFolder);
        
    }

    private static void main(Resource mainFolder) throws IOException {
    	ResourceProvider frp = ResourcesImpl.getFileResourceProvider();
    	String path=WriteOSJar_1.path;
    	Resource mainTargetJar=frp.getResource(path+"railo-"+Info.getVersionAsString()+".jar");
    	Resource coreFolder=mainFolder.getRealResource("core");
        
    	
    // save source
    	Resource srcCore = frp.getResource(WriteOSJar_1.pathRailo+"Source2/"+WriteOSJar_1.projectNameCore+"/src/");
    	Resource srcLoader = frp.getResource(WriteOSJar_1.pathRailo+"Source2/"+WriteOSJar_1.projectNameMain+"/src/");
    	Resource tmp=frp.getResource("/Users/mic/Temp/railo-lgpl-src/");
    	Resource tmpCore=tmp.getRealResource("core");
    	Resource tmpLoader=tmp.getRealResource("loader");
    	Resource licenseFile=frp.getResource(WriteOSJar_1.pathRailo+"Deploy-Source/licence/License-3-1.txt");
    	Resource src=frp.getResource(WriteOSJar_1.pathRailo+"Deploy/src/railo-"+Info.getVersionAsString()+"-src.zip");
    	//Resource src2=frp.getResource("/Volumes/EXPRESSCARD/Projects/Railo/Source/railo-"+Info.getVersionAsString()+"-src.zip");
    	license=IOUtil.toString(licenseFile,null);
    	
    	//Resource tmp=frp.getResource("/Users/mic/Temp/railo-lgpl-src/railo-"+Info.getVersionAsString()+".jar");
    	if(tmp.exists())
    		ResourceUtil.deleteContent(tmp, null);
    	else tmp.mkdirs();

    	licenseFile.copyTo(tmp.getRealResource("License.txt"), false);
    	copy(srcCore,tmpCore);
    	copy(srcLoader,tmpLoader);
    	
    	System.out.println("write source:"+src);
    	CompressUtil.compressZip(tmp.listResources(),src,new ChildFilter2());
    	
    	Resource src2=null;
    	// save to Expresscard
    	int count=0;
    	/*do {
    	src2=frp.getResource("/Volumes/EXPRESSCARD/Projects/Railo/Source2/railo-"+Info.getVersionAsString()+"-src"+(++count)+".zip");
    	}while(src2.exists());
    	src.copyTo(src2,false);
    	System.out.println("write source:"+src2);
    	
    	// delete older source
    	for(int i=0;i<count;i++) {
    		src2=frp.getResource("/Volumes/EXPRESSCARD/Projects/Railo/Source2/railo-"+Info.getVersionAsString()+"-src"+(i)+".zip");
    		src2.delete();
        }*/
    	
    	
    	// save to TimeCapsule
    	saveSource(frp,src,"/Volumes/Michael Streits Time Capsul/Railo/Source/");
    	// save to iDisk
    	saveSource(frp,src,"/Volumes/michael.streit/Sites/Railo/");
    	
    	// save to MyDrive
    	saveSource(frp,src,"/Volumes/webdav.mydrive.ch/Projects/Railo/Source/");
    	
    	// Dropbox
    	saveSource(frp,src,"/Users/mic/Dropbox/Projects/Railo/builds/");
    	
    	
    	

    // save admin source
    	Resource srcAdmin = frp.getResource(WriteOSJar_1.pathRailo+"webroot/context31/");
    	Resource trgAdmin=frp.getResource(WriteOSJar_1.pathRailo+"Deploy/src/railo-admin-"+Info.getVersionAsString()+"-src.zip");
    	CompressUtil.compressZip(srcAdmin.listResources(),trgAdmin,null);
    	System.out.println("write admin source:"+trgAdmin);
    	
    	
    	
    	
    	
        // Info
        Resource version=mainFolder.getRealResource("/railo/version");
        version.createNewFile();

        System.out.println("write info:"+version);
        System.out.println("write info:"+Info.getVersionAsInt()+":"+Info.getRealeaseTime());
        IOUtil.write(version,Info.getVersionAsInt()+":"+Info.getRealeaseTime(),"UTF-8",false);
        
        InputStream is;
        OutputStream os;
        
        System.out.println("copy to "+coreFolder.getRealResource("core.rc"));
        is=new FileInputStream(path+Info.getVersionAsString()+".rc");
        os=coreFolder.getRealResource("core.rc").getOutputStream();
        IOUtil.copy(is,os,true,true);
        
        
        // write main railo file
        System.out.println("write jar "+mainFolder);
        System.out.println("write jar "+mainTargetJar);
        CompressUtil.compressZip(mainFolder.listResources(new WriteOSJar_2()),mainTargetJar,new JavaFilter());
	}


	private static void saveSource(ResourceProvider frp, Resource src, String path) {
		try{
	    	int count=0;
	    	Resource src2;
			do {
	    	src2=frp.getResource(path+"railo-"+Info.getVersionAsString()+"-src"+(++count)+".zip");
	    	}while(src2.exists());
	    	src.copyTo(src2,false);
	    	System.out.println("write source:"+src2);
	    	
	    	// delete older source
	    	for(int i=0;i<count;i++) {
	    		src2=frp.getResource(path+"railo-"+Info.getVersionAsString()+"-src"+(i)+".zip");
	    		src2.delete();
	        }
    	}
    	catch(Throwable t){
    		aprint.e("cant write source to:"+path);
    	}
	}

	private static void copy(Resource src, Resource trg) throws IOException {
    	ResourceUtil.copyRecursive(src, trg);
    	Resource[] rootChildren = trg.listResources();
    	Resource child;
    	for(int i=0;i<rootChildren.length;i++){
    		child = rootChildren[i];
    		if(child.isFile()){
    			child.delete();
    		}
    		else if(
    				child.getName().equals("a") || 
    				child.getName().equals("aa") || 
    				child.getName().equals("railo-server")|| 
    				child.getName().equals("core")|| 
    				child.getName().equals("test")|| 
    				child.getName().equals("aservlet")
    				){
    			ResourceUtil.deleteContent(child, null);
    			child.delete();
    		}
    			
    	}
    	addLicence(trg);
    	
    	
	}

	private static void addLicence(Resource res) throws IOException {
		if(true) return;
		if(res.isDirectory()){
			Resource[] children = res.listResources();
			for(int i=0;i<children.length;i++){
				addLicence(children[i]);
			}
			
		}
		else if(res.isFile()){
			if(res.getName().endsWith(".java")){
				String content = IOUtil.toString(res,null);
				IOUtil.write(res, "/*\n"+license+"\n\n @author Michael Offner-Streit\n\n*/\n\n"+content, null, false);
			}
			else if(res.getName().endsWith(".class")){
				res.delete();
			}
			else if(res.getName().endsWith(".jar")){
				res.delete();
			}
		}
		
	}

	public boolean accept(Resource file) {
        String name=file.getName();
        
        return  !(name.equals("patches") ||
        		name.equals(".svn") ||
        		name.equals("railo-server") ||
                file.isFile());
    }
}



class ChildFilter2 implements ResourceFilter {

	public boolean accept(Resource res) {
		if(res.getName().equals(".svn")) {
			return false;
		}
		return true;
	}

}
