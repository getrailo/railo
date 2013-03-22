package railo.runtime.listener;

import java.util.ArrayList;
import java.util.List;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.type.util.ArrayUtil;

public class JavaSettingsImpl implements JavaSettings {

	private final Resource[] resources;
	private Resource[] resourcesTranslated;
	private final boolean loadCFMLClassPath;
	private final boolean reloadOnChange;
	private final int watchInterval;
	private final String[] watchedExtensions;

	public JavaSettingsImpl(){
		this.resources=new Resource[0];
		this.loadCFMLClassPath=false;
		this.reloadOnChange=false;
		this.watchInterval=60;
		this.watchedExtensions=new String[]{"jar","class"};
	}

	public JavaSettingsImpl(Resource[] resources, Boolean loadCFMLClassPath,boolean reloadOnChange, int watchInterval, String[] watchedExtensions) {

		this.resources=resources;
		this.loadCFMLClassPath=loadCFMLClassPath;
		this.reloadOnChange=reloadOnChange;
		this.watchInterval=watchInterval;
		this.watchedExtensions=watchedExtensions;
	}

	@Override
	public Resource[] getResources() {
		return resources;
	}
	
	// FUTURE add to interface
	public Resource[] getResourcesTranslated() {
		if(resourcesTranslated==null) {
			List<Resource> list=new ArrayList<Resource>();
			_getResourcesTranslated(list,resources, true);
			resourcesTranslated=list.toArray(new Resource[list.size()]);
		}
		return resourcesTranslated;
	}
	
	public static void _getResourcesTranslated(List<Resource> list, Resource[] resources, boolean deep) {
		if(ArrayUtil.isEmpty(resources)) return;
		for(int i=0;i<resources.length;i++){
			if(resources[i].isFile()) {
				if(ResourceUtil.getExtension(resources[i], "").equalsIgnoreCase("jar"))
					list.add(resources[i]);
			}
			else if(deep && resources[i].isDirectory()){
				list.add(resources[i]); // add as possible classes dir
				_getResourcesTranslated(list,resources[i].listResources(),false);
				
			}
		}
	}

	@Override
	public boolean loadCFMLClassPath() {
		return loadCFMLClassPath;
	}

	@Override
	public boolean reloadOnChange() {
		return reloadOnChange;
	}

	@Override
	public int watchInterval() {
		return watchInterval;
	}

	@Override
	public String[] watchedExtensions() {
		return watchedExtensions;
	}

}
