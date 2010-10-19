package railo.runtime.config.component;

import railo.commons.io.res.Resource;
import railo.runtime.config.ConfigWebFactory;

public class ComponentFactory {

	
	/**
	* this method deploy all components for org.railo.cfml
	* @param dir components directory
	* @param doNew redeploy even the file exist, this is set to true when a new version is started
	*/
	public static void deploy(Resource dir, boolean doNew) {
		String path="/resource/component/org/railo/cfml/";
		
		deploy(dir,path,doNew,"Base");
		deploy(dir,path,doNew,"Feed");
		deploy(dir,path,doNew,"Ftp");
		deploy(dir,path,doNew,"Http");
		deploy(dir,path,doNew,"Mail");
		deploy(dir,path,doNew,"Query");
		deploy(dir,path,doNew,"Result");
		
		// orm
		dir = dir.getRealResource("orm");
		path+="orm/";
		if(!dir.exists())dir.mkdirs();
		deploy(dir,path,doNew,"IEventHandler");
		deploy(dir,path,doNew,"INamingStrategy");
	}

	private static void deploy(Resource dir, String path,boolean doNew, String name) {
		Resource f = dir.getRealResource(name+".cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL(path+name+".cfc",f);
	}
}

