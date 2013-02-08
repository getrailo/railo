package railo.runtime.functions.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.List;
import railo.runtime.type.util.ArrayUtil;

public class ComponentListPackage implements Function {
	
	private static final long serialVersionUID = 6502632300879457687L;
	
	private static final ExtensionResourceFilter FILTER_CFC = new ExtensionResourceFilter(".cfc");
	private static final ExtensionResourceFilter FILTER_CLASS = new ExtensionResourceFilter(".class");
	private static final String[] EMPTY = new String[0];
	

	public static Array call(PageContext pc , String packageName) throws PageException {
		String[] names;
		try {
			names = _call(pc, packageName);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
		
		Array arr=new ArrayImpl();
		String name;
		for(int i=0;i<names.length;i++){
			name=names[i];
			if(StringUtil.endsWithIgnoreCase(name, ".cfc")) {
				name=name.substring(0,name.length()-4);
			}
			arr.appendEL(name);
		}
		return arr;
	}
	
	private static String[] _call(PageContext pc , String packageName) throws IOException, ApplicationException {
		PageContextImpl pci=(PageContextImpl) pc;
		ConfigWebImpl config = (ConfigWebImpl) pc.getConfig();
		//var SEP=server.separator.file;
		
		// get enviroment configuration
		boolean searchLocal = config.getComponentLocalSearch();
		boolean searchRoot=config.getComponentRootSearch();
		
		String path=StringUtil.replace(packageName, ".", File.pathSeparator, false);
	    	
		// search local 
		if(searchLocal) {
			//Resource dir=pc.getCurrentTemplatePageSource().getResourceTranslated(pc).getParentResource();
			//dir=dir.getRealResource(path);
			PageSource ps= pci.getRelativePageSourceExisting(path);
			if(ps!=null){
				Mapping mapping = ps.getMapping();
				String _path=ps.getRealpath();
				_path=List.trim(_path,"\\/");
				String[] list = _listMapping(pc,mapping,_path);
				if(!ArrayUtil.isEmpty(list)) return list;
			}
		}
		
		// check mappings (this includes the webroot)
		if(searchRoot) {	
			String virtual="/"+StringUtil.replace(packageName, ".", "/", false);
			Mapping[] mappings = config.getMappings();
			Mapping mapping;
			String _path;
			String[] list;
			for(int i=0;i<mappings.length;i++){
				mapping=mappings[i];
				if(StringUtil.startsWithIgnoreCase(virtual, mapping.getVirtual()))  {
					_path=List.trim(virtual.substring(mapping.getVirtual().length()),"\\/").trim(); 
					_path=StringUtil.replace(_path, "/", File.pathSeparator, false);
					list = _listMapping(pc,mapping,_path);
					if(!ArrayUtil.isEmpty(list)) return list;
				}
			}
		}
		
		// check component mappings
		Mapping[] mappings = config.getComponentMappings();
		Mapping mapping;
		String[] list;
		for(int i=0;i<mappings.length;i++){
			mapping=mappings[i];
			list=_listMapping(pc,mapping,path);
			if(!ArrayUtil.isEmpty(list)) return list;
		}
		
		throw new ApplicationException("no package with name ["+packageName+"] found");
		
	}

	
	private static String[] _listMapping(PageContext pc,Mapping mapping, String path) throws IOException{
		if(mapping.isPhysicalFirst()) {
			// check physical
			String[] list = _listPhysical(path,mapping);
			if(!ArrayUtil.isEmpty(list)) return list;
			
			// check archive
			list=_listArchive(pc,path,mapping);
			if(!ArrayUtil.isEmpty(list)) return list;
		}
		else {
			// check archive
			String[] list = _listArchive(pc,path,mapping);
			if(!ArrayUtil.isEmpty(list)) return list;
			// check physical
			list=_listPhysical(path,mapping);
			if(!ArrayUtil.isEmpty(list)) return list;
		}
		return null;
	}

	private static String[] _listPhysical(String path, Mapping mapping){
		Resource physical = mapping.getPhysical();
		if(physical!=null) {
			Resource dir = physical.getRealResource(path);
			if(dir.isDirectory()) {
				return dir.list(FILTER_CFC);
			}
		}
		return EMPTY;
	}
	
	private static String[]  _listArchive(PageContext pc,String path, Mapping mapping) throws IOException {
		String packageName=StringUtil.replace(path, File.pathSeparator, ".", false);
		Resource archive = mapping.getArchive();
		if(archive!=null) {
			// TODO nor working with pathes with none ascci characters, eith none ascci characters, the java class path is renamed, so make sure you rename the path as well
			String strDir="zip://"+archive+"!"+File.pathSeparator+path;
			Resource dir = ResourceUtil.toResourceNotExisting(pc, strDir,true);
			
			if(dir.isDirectory()) {
				java.util.List<String> list=new ArrayList<String>();
				// we use the class files here to get the info, the source files are optional and perhaps not present.
				Resource[] children = dir.listResources(FILTER_CLASS);
				String className,c;
				for(int i=0;i<children.length;i++){
					className=children[i].getName();
					className=className.substring(0,className.length()-6);
					className=packageName+"."+className;
					//mapping.getClassLoaderForArchive().loadClass(className);
					
					// TODO do the following 4 lines with help of ASM this way is ugly but working for the moment
					c=IOUtil.toString(children[i],null);
					c=c.substring(0,c.indexOf("<clinit>"));
					c = List.last(c, "/\\",true).trim();
					if(StringUtil.endsWithIgnoreCase(c, ".cfc")) list.add(c);
				}
				if(list.size()>0) return list.toArray(new String[list.size()]);
			} 
		}
		return null;
	}
}
