package railo.runtime.listener;

import railo.commons.io.res.Resource;

public class JavaSettingsImpl implements JavaSettings {
	
	private final Resource[] resources;
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
