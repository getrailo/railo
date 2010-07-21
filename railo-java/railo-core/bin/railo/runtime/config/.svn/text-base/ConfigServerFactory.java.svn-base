package railo.runtime.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.ClassException;
import railo.commons.lang.SystemOut;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.exp.PageException;
import railo.transformer.library.function.FunctionLibException;
import railo.transformer.library.tag.TagLibException;


/**
 * 
 */
public final class ConfigServerFactory {
    
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
    public static ConfigServerImpl newInstance(CFMLEngineImpl engine,Map initContextes, Map contextes, Resource configDir) 
        throws SAXException, ClassException, PageException, IOException, TagLibException, FunctionLibException {
    	SystemOut.print(SystemUtil.PRINTWRITER_OUT,
    			"===================================================================\n"+
    			"SERVER CONTEXT\n" +
    			"-------------------------------------------------------------------\n"+
    			"- config:"+configDir+"\n"+
    			"===================================================================\n"
    			
    			);
    	
    	
    	Resource configFile=configDir.getRealResource("railo-server.xml");
        if(!configFile.exists()) {
		    configFile.createFile(true);
			//InputStream in = new TextFile("").getClass().getResourceAsStream("/resource/config/server.xml");
			ConfigWebFactory.createFileFromResource(
			     "/resource/config/server.xml",
			     configFile.getAbsoluteResource(),
			     "tpiasfap"
			);
		}
		//print.out(configFile);
        InputStream is = null;
        Document doc=null;
        try {
        	is = IOUtil.toBufferedInputStream(configFile.getInputStream());
        	doc=ConfigWebFactory.loadDocument(is);
        }
        finally {
        	IOUtil.closeEL(is);
        }
		//ConfigWebFactory.createContextFiles(configDir);
		
        ConfigServerImpl config=new ConfigServerImpl(engine,initContextes,contextes,configDir,configFile);
		load(config,doc);
	    
		createContextFiles(configDir,config);
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
        
        InputStream is = null;
        Document doc = null;
        try {
        	is = IOUtil.toBufferedInputStream(configFile.getInputStream());
        	doc=ConfigWebFactory.loadDocument(is);
        }
        finally {
        	IOUtil.closeEL(is);
        }
		// createContextFiles(configDir);
		load(configServer,doc);
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
    static void load(ConfigServerImpl configServer, Document doc) throws ClassException, PageException, IOException, TagLibException, FunctionLibException {
        ConfigWebFactory.load(null,configServer,doc);
    }
    

	public static void createContextFiles(Resource configDir, ConfigServer config) throws IOException {
		//Resource tagDir = configDir.getRealResource("library/tag/");
		//if()
		//f=cDir.getRealResource("Cache.cfc");
        //if(!f.exists() || doNew)createFileFromResourceEL("/resource/context/admin/cdriver/Cache.cfc",f);

	}
}