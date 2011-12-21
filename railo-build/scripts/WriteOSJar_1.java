
import java.io.IOException;
import java.io.Serializable;

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

public class WriteOSJar_1 implements ResourceFilter {
	


	public static final String pathRailo="/Users/mic/Projects/Railo/";
	public static final String path=pathRailo+"Deploy-Source/railo-jar-open/";
	public static final String pathScript=pathRailo+"Deploy-Source/script/";
	
	public static final String projectNameMain="railo/railo-java/railo-loader";
	public static final String projectNameCore="railo/railo-java/railo-core";
	public static final String projectNameCoreEx="Railo-Core-EXE";
	//public static final String projectNameMain="Railo-3-1-Main";
	//public static final String projectNameCore="Railo-3-1-Core";
	private static boolean useExe=false;
	
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	
    	ResourceProvider frp = ResourcesImpl.getFileResourceProvider();

    	
    	// create files from context to resource
    	Resource contextFolder = frp.getResource(pathRailo+"webroot/context30/");
    	Resource binFolder = frp.getResource(pathRailo+"Source2/"+projectNameCore+"/bin/resource/context");
    	Resource srcFolder = frp.getResource(pathRailo+"Source2/"+projectNameCore+"/src/resource/context");
    	copy(contextFolder.getRealResource("templates"),srcFolder.getRealResource("templates"));
    	copy(contextFolder.getRealResource("templates"),binFolder.getRealResource("templates"));
    	copy(contextFolder.getRealResource("admin/plugin"),srcFolder.getRealResource("admin/plugin"));
    	copy(contextFolder.getRealResource("admin/plugin"),binFolder.getRealResource("admin/plugin"));
    	copy(contextFolder.getRealResource("admin/dbdriver"),srcFolder.getRealResource("admin/dbdriver"));
    	copy(contextFolder.getRealResource("admin/dbdriver"),binFolder.getRealResource("admin/dbdriver"));
    	
    	// create temp directory
    	Resource temp = frp.getResource("/Users/mic/temp/railosource");
    	System.out.println("create temp directory:"+temp);
    	if(temp.exists()) temp.remove(true);
    	else temp.mkdirs();
    	
    	// copy data to temp directory
    	Resource projectFolder = frp.getResource(pathRailo+"Source2/"+projectNameCore+"/bin/");
    	ResourceUtil.copyRecursive(projectFolder, temp,new SVNFilter());
    	
    	// copy exe data as well
    	if(useExe){
	    	projectFolder = frp.getResource(pathRailo+"Source2/"+projectNameCoreEx+"/bin/");
	    	ResourceUtil.copyRecursive(projectFolder, temp,new SVNFilter());
    	}
    	
    	// create zip file 
    	Resource targetJar=frp.getResource(path+Info.getVersionAsString()+".rc");
    	System.out.println("create zip file:"+targetJar);
        CompressUtil.compressZip(
        		temp.listResources(new WriteOSJar_1())
        		,
        		targetJar,new ChildFilter());
        
        // clear
    	Resource rs = frp.getResource("/Users/mic/Projects/Railo/Source2/"+WriteOSJar_1.projectNameMain+"/src/railo-server/");
    	aprint.out("clear:"+rs);
    	ResourceUtil.removeChildren(rs);
    	rs = frp.getResource("/Users/mic/Projects/Railo/Source2/"+WriteOSJar_1.projectNameMain+"/bin/railo-server/");
    	aprint.out("clear:"+rs);
    	ResourceUtil.removeChildren(rs);
    	
        
    }
    
	public boolean accept(Resource file) {
    	return _accept(file);
    }
    
    public boolean _accept(Resource file) {
        String name=file.getName();
        return  !(name.equals(".settings") ||
        		name.equals("context") ||
        		name.equals(".DS_Store") ||
                //name.equals("org") ||
                name.equals("servlet")||
                name.equals("aservlet")||
                name.equals("railo-server") ||
                name.equals("a") ||
                name.equals("aa") ||
                name.equals(".svn") ||
                file.isFile());
    }    
    

    public static void copy(Resource src,Resource trg) throws IOException {
        if(!src.exists()) return ;
        if(src.isDirectory()) {
        	if(!trg.exists())trg.createDirectory(true);
        	Resource[] files=src.listResources();
            for(int i=0;i<files.length;i++) {
            	//print.ln(files[i]);
            	copy(files[i],trg.getRealResource(files[i].getName()));
            }
        }
        else if(src.isFile()) {
        	if(src.lastModified()>trg.lastModified()){
        		ResourceUtil.touch(trg);
        		IOUtil.copy(src,trg);
        	}
        }
    }
    
}


class SVNFilter implements ResourceFilter {

	public boolean accept(Resource res) {
		if(res.getName().startsWith(".")) {
			return false;
		}
		return true;
	}
}


class ChildFilter extends JavaFilter {

	public boolean accept(Resource res) {
		String name=res.getName();
		//if(!res.isDirectory() && !name.endsWith(".class"))print.out(name+":"+res.length());
		if(name.startsWith(".")) {
			return false;
		}
		return super.accept(res);
	}
}