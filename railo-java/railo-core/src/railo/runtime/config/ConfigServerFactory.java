package railo.runtime.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.jacob.com.LibraryLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.type.file.FileResource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassException;
import railo.commons.lang.SystemOut;
import railo.runtime.CFMLFactory;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.exp.PageException;
import railo.transformer.library.function.FunctionLibException;
import railo.transformer.library.tag.TagLibException;


/**
 * 
 */
public final class ConfigServerFactory extends ConfigFactory{
    
    /**
     * creates a new ServletConfig Impl Object
     * @param engine 
     * @param initContextes
     * @param contextes
     * @param configDir
     * @return new Instance
     * @throws SAXException
     * @throws ClassNotFoundException
     * @throws PageException
     * @throws IOException
     * @throws TagLibException
     * @throws FunctionLibException
     */
    public static ConfigServerImpl newInstance(CFMLEngineImpl engine,Map<String,CFMLFactory> initContextes, Map<String,CFMLFactory> contextes, Resource configDir) 
        throws SAXException, ClassException, PageException, IOException, TagLibException, FunctionLibException {
    	
    	boolean isCLI=SystemUtil.isCLICall();
    	if(isCLI){
    		Resource logs = configDir.getRealResource("logs");
    		logs.mkdirs();
    		Resource out = logs.getRealResource("out");
    		Resource err = logs.getRealResource("err");
    		ResourceUtil.touch(out);
    		ResourceUtil.touch(err);
    		if(logs instanceof FileResource) {
    			SystemUtil.setPrintWriter(SystemUtil.OUT, new PrintWriter((FileResource)out));
    			SystemUtil.setPrintWriter(SystemUtil.ERR, new PrintWriter((FileResource)err));
    		}
    		else{
    			SystemUtil.setPrintWriter(SystemUtil.OUT, new PrintWriter(IOUtil.getWriter(out,"UTF-8")));
    			SystemUtil.setPrintWriter(SystemUtil.ERR, new PrintWriter(IOUtil.getWriter(err,"UTF-8")));	
    		}
    	}
    	SystemOut.print(SystemUtil.getPrintWriter(SystemUtil.OUT),
    			"===================================================================\n"+
    			"SERVER CONTEXT\n" +
    			"-------------------------------------------------------------------\n"+
    			"- config:"+configDir+"\n"+
    			"- loader-version:"+SystemUtil.getLoaderVersion()+"\n"+
    			"===================================================================\n"
    			
    			);
          		
    	boolean doNew=doNew(configDir);
    	
    	Resource configFile=configDir.getRealResource("railo-server.xml");
        if(!configFile.exists()) {
		    configFile.createFile(true);
			//InputStream in = new TextFile("").getClass().getResourceAsStream("/resource/config/server.xml");
			createFileFromResource(
			     "/resource/config/server.xml",
			     configFile.getAbsoluteResource(),
			     "tpiasfap"
			);
		}
		//print.out(configFile);
        Document doc=loadDocument(configFile);
       
        ConfigServerImpl config=new ConfigServerImpl(engine,initContextes,contextes,configDir,configFile);
		load(config,doc,false,doNew);
	    
		createContextFiles(configDir,config,doNew);
	    return config;
    }
    /**
     * reloads the Config Object
     * @param configServer
     * @throws SAXException
     * @throws ClassNotFoundException
     * @throws PageException
     * @throws IOException
     * @throws TagLibException
     * @throws FunctionLibException
     */
    public static void reloadInstance(ConfigServerImpl configServer) 
    	throws SAXException, ClassException, PageException, IOException, TagLibException, FunctionLibException {
        Resource configFile=configServer.getConfigFile();
        
        if(configFile==null) return ;
        if(second(configServer.getLoadTime())>second(configFile.lastModified())) return ;
        boolean doNew=doNew(configServer.getConfigDir());
        load(configServer,loadDocument(configFile),true,doNew);
    }
    
    private static long second(long ms) {
		return ms/1000;
	}
    
    /**
     * @param configServer 
     * @param doc
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws FunctionLibException
     * @throws TagLibException
     * @throws PageException
     */
    static void load(ConfigServerImpl configServer, Document doc, boolean isReload, boolean doNew) throws ClassException, PageException, IOException, TagLibException, FunctionLibException {
        ConfigWebFactory.load(null,configServer,doc, isReload,doNew);
        loadLabel(configServer,doc);
    }
    

	private static void loadLabel(ConfigServerImpl configServer, Document doc) {
		Element el= getChildByName(doc.getDocumentElement(),"labels");
        Element[] children=getChildren(el,"label");
        
        Map<String, String> labels=new HashMap<String, String>();
        if(children!=null)for(int i=0;i<children.length;i++) {
           el=children[i];
           
           String id=el.getAttribute("id");
           String name=el.getAttribute("name");
           if(id!=null && name!=null) { 
               labels.put(id, name);
           }
        }
        configServer.setLabels(labels);
	}
	
	public static void createContextFiles(Resource configDir, ConfigServer config, boolean doNew) {
		
		Resource contextDir = configDir.getRealResource("context");
		Resource adminDir = contextDir.getRealResource("admin");
		
		

		// Debug
		Resource debug = adminDir.getRealResource("debug");
		create("/resource/context/admin/debug/",new String[]{
				"Debug.cfc","Field.cfc","Group.cfc","Classic.cfc","Modern.cfc","Comment.cfc"
				},debug,doNew);
		
		
		// DB Drivers types
		Resource dbDir = adminDir.getRealResource("dbdriver");
		Resource typesDir = dbDir.getRealResource("types");
		create("/resource/context/admin/dbdriver/types/",new String[]{
		"IDriver.cfc","Driver.cfc","IDatasource.cfc","IDriverSelector.cfc","Field.cfc"
		},typesDir,doNew);

		// DB Drivers
		create("/resource/context/admin/dbdriver/",new String[]{
		"H2.cfc","H2Selector.cfc","H2Server.cfc","HSQLDB.cfc","MSSQL.cfc","MSSQL2.cfc","MSSQLSelector.cfc","DB2.cfc","Oracle.cfc"
		,"MySQL.cfc","ODBC.cfc","Sybase.cfc","PostgreSql.cfc","Other.cfc","Firebird.cfc"}
		,dbDir,doNew);
		
		// Cache Drivers
		Resource cDir = adminDir.getRealResource("cdriver");
		create("/resource/context/admin/cdriver/",new String[]{
		"Cache.cfc","RamCache.cfc","EHCacheLite.cfc","Field.cfc","Group.cfc"}
		,cDir,doNew);
		
		// Gateway Drivers
		Resource gDir = adminDir.getRealResource("gdriver");
		create("/resource/context/admin/gdriver/",new String[]{
		"TaskGatewayDriver.cfc","DirectoryWatcher.cfc","MailWatcher.cfc","Gateway.cfc","Field.cfc","Group.cfc"}
		,gDir,doNew);
		
		// Security
		Resource secDir = configDir.getRealResource("security");
        if(!secDir.exists())secDir.mkdirs();
        Resource res = create("/resource/security/","cacerts",secDir,false);
		System.setProperty("javax.net.ssl.trustStore",res.toString());
		
        // ESAPI
        Resource propDir = configDir.getRealResource("properties");
        if(!propDir.exists())propDir.mkdirs();
        create("/resource/properties/","ESAPI.properties",propDir,doNew);
		System.setProperty("org.owasp.esapi.resources", propDir.toString());


		// Jacob
		if (SystemUtil.isWindows()) {

			Resource binDir = configDir.getRealResource("bin");
			if (binDir != null) {

				if (!binDir.exists())
					binDir.mkdirs();

				String name = (SystemUtil.getJREArch() == SystemUtil.ARCH_64) ? "jacob-x64.dll" : "jacob-x86.dll";

				Resource jacob = binDir.getRealResource(name);
				if (!jacob.exists()) {
					createFileFromResourceEL("/resource/bin/" + name, jacob);
				}
				// SystemOut.printDate(SystemUtil.PRINTWRITER_OUT,"set-property -> "+LibraryLoader.JACOB_DLL_PATH+":"+jacob.getAbsolutePath());
				System.setProperty(LibraryLoader.JACOB_DLL_PATH, jacob.getAbsolutePath());
				// SystemOut.printDate(SystemUtil.PRINTWRITER_OUT,"set-property -> "+LibraryLoader.JACOB_DLL_NAME+":"+name);
				System.setProperty(LibraryLoader.JACOB_DLL_NAME, name);
			}
		}
	}
	
}