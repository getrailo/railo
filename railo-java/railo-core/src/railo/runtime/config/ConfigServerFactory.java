package railo.runtime.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
        Document doc=ConfigWebFactory.loadDocument(configFile);
       
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
        
        load(configServer,ConfigWebFactory.loadDocument(configFile));
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
        loadLabel(configServer,doc);
    }
    

	private static void loadLabel(ConfigServerImpl configServer, Document doc) {
		Element el= ConfigWebFactory.getChildByName(doc.getDocumentElement(),"labels");
        Element[] children=ConfigWebFactory.getChildren(el,"label");
        
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
	
	public static void createContextFiles(Resource configDir, ConfigServer config) throws IOException {
		//Resource tagDir = configDir.getRealResource("library/tag/");
		//if()
		//f=cDir.getRealResource("Cache.cfc");
        //if(!f.exists() || doNew)createFileFromResourceEL("/resource/context/admin/cdriver/Cache.cfc",f);
		
		// Security certificate
        Resource secDir = configDir.getRealResource("security");
        if(!secDir.exists())secDir.mkdirs();
        Resource f = secDir.getRealResource("cacerts");
        if(!f.exists())ConfigWebFactory.createFileFromResourceEL("/resource/security/cacerts",f);
        System.setProperty("javax.net.ssl.trustStore",f.toString());
	}
}