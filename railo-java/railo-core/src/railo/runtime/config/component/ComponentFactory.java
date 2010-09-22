package railo.runtime.config.component;

import railo.commons.io.res.Resource;
import railo.runtime.config.ConfigWebFactory;

public class ComponentFactory {

	private static String path="/resource/component/org/railo/cfml/";

	/**
	* this method deploy all components for org.railo.cfml
	* @param dir components directory
	* @param doNew redeploy even the file exist, this is set to true when a new version is started
	*/
	public static void deploy(Resource dir, boolean doNew) {
		deploy(dir,doNew,"Base");
		deploy(dir,doNew,"Feed");
		deploy(dir,doNew,"Ftp");
		deploy(dir,doNew,"Http");
		deploy(dir,doNew,"Mail");
		deploy(dir,doNew,"Query");
		deploy(dir,doNew,"Result");
	}

	private static void deploy(Resource dir, boolean doNew, String name) {
		Resource f = dir.getRealResource(name+".cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL(path+name+".cfc",f);
	}
}

