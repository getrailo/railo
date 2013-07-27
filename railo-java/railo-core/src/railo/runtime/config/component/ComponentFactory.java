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
		deploy(dir,path,doNew,"Administrator");
		
		// orm
		{
		Resource ormDir = dir.getRealResource("orm");
		String ormPath = path+"orm/";
		if(!ormDir.exists())ormDir.mkdirs();
		deploy(ormDir,ormPath,doNew,"IEventHandler");
		deploy(ormDir,ormPath,doNew,"INamingStrategy");
		}
		// test
		{
		Resource testDir = dir.getRealResource("test");
		String testPath = path+"test/";
		if(!testDir.exists())testDir.mkdirs();
		deploy(testDir,testPath,doNew,"AdministratorTest");
		deploy(testDir,testPath,doNew,"RailoTestSuite");
		deploy(testDir,testPath,doNew,"RailoTestSuiteRunner");
		deploy(testDir,testPath,doNew,"RailoTestCase");
		}
		
	}

	private static void deploy(Resource dir, String path,boolean doNew, String name) {
		Resource f = dir.getRealResource(name+".cfc");
        if(!f.exists() || doNew)ConfigWebFactory.createFileFromResourceEL(path+name+".cfc",f);
	}
}

