package railo.commons.lang;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.DirectoryResourceFilter;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.runtime.Mapping;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.transformer.bytecode.util.ASMUtil;

public class MappingUtil {
	//private static final ResourceFilter EXT=new ExtensionResourceFilter(".cfc");
	//private static final ResourceFilter DIR_OR_EXT=new OrResourceFilter(new ResourceFilter[]{DirectoryResourceFilter.FILTER,EXT});

	
	public static PageSource searchMappingRecursive(Mapping mapping, String name, boolean onlyCFC) {
		if(name.indexOf('/')==-1) { // TODO handle this as well?
			Config config = mapping.getConfig();
			ExtensionResourceFilter ext =null;
			if(onlyCFC) ext=new ExtensionResourceFilter(new String[]{config.getCFCExtension()},true,true);
			else {
				ext=new ExtensionResourceFilter(config.getCFMLExtensions(),true,true);
				ext.addExtension(config.getCFCExtension());
			}
			
			if(mapping.isPhysicalFirst()) {
				PageSource ps = searchPhysical(mapping,name,ext);
				if(ps!=null) return ps;
				ps=searchArchive(mapping,name,onlyCFC);
				if(ps!=null) return ps;
			}
			else {
				PageSource ps=searchArchive(mapping,name,onlyCFC);
				if(ps!=null) return ps;
				ps = searchPhysical(mapping,name,ext);
				if(ps!=null) return ps;
			}
		}
		return null;
	}

	private static PageSource searchArchive(Mapping mapping, String name, boolean onlyCFC) {
		Resource archive = mapping.getArchive();
		if(archive!=null && archive.isFile()) {
			ClassLoader cl = mapping.getClassLoaderForArchive();
			ZipInputStream zis = null;
			try{
				zis = new ZipInputStream(archive.getInputStream());
				ZipEntry entry;
				Class clazz;
				while((entry=zis.getNextEntry())!=null){
					if(entry.isDirectory() || !entry.getName().endsWith(".class")) continue;
					clazz=toClass(cl,entry.getName());
					
					if(clazz==null) continue;
					Pair<String, String> nameAndPath = ASMUtil.getSourceNameAndPath(mapping.getConfig(),clazz,onlyCFC);
					if(name.equalsIgnoreCase(nameAndPath.name)) {
						PageSource ps = mapping.getPageSource(nameAndPath.value);
						//Page page = ((PageSourceImpl)ps).loadPage(pc,(Page)null);
						return ps;
					}
				}
				
				
				
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
			finally {
				IOUtil.closeEL(zis);
			}
			
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	private static Class toClass(ClassLoader cl,String name) {
		name=name.replace('/', '.').substring(0,name.length()-6);
		try {
			return cl.loadClass(name);
		}
		catch (ClassNotFoundException e) {}
		return null;
	}

	
	
	
	

	private static PageSource searchPhysical(Mapping mapping, String name, ResourceFilter filter) {
		Resource physical = mapping.getPhysical();
		if(physical!=null) {
			String _path=searchPhysical(mapping.getPhysical(), null,name,filter,true);
			
			if(_path!=null) {
				PageSource ps = mapping.getPageSource(_path);
				//Page page = ((PageSourceImpl)ps).loadPage(pc,(Page)null);
				return ps;
			}
		}
		return null;
	}
	
	private static String searchPhysical(Resource res, String dir,String name, ResourceFilter filter, boolean top) {
		if(res.isFile()) {
			if(res.getName().equalsIgnoreCase(name)) {
				return dir+res.getName();
			}
		}
		else if(res.isDirectory()) {
			Resource[] _dir = res.listResources(top?DirectoryResourceFilter.FILTER:filter);
			if(_dir!=null){
				if(dir==null) dir="/";
				else dir=dir+res.getName()+"/";
				String path;
				for(int i=0;i<_dir.length;i++){
					path=searchPhysical(_dir[i],dir, name,filter,false);
					if(path!=null) return path;
				}
			}
		}
		
		return null;
	}
}
