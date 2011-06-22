package railo.runtime.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.xerces.parsers.DOMParser;
import org.opencfml.eventgateway.GatewayException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import railo.commons.io.FileUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.cache.Cache;
import railo.commons.io.log.LogUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourceProvider;
import railo.commons.io.res.filter.ResourceNameFilter;
import railo.commons.io.res.type.s3.S3ResourceProvider;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.commons.net.HTTPUtil;
import railo.commons.net.URLEncoder;
import railo.loader.TP;
import railo.loader.engine.CFMLEngineFactory;
import railo.loader.util.ExtensionFilter;
import railo.runtime.Info;
import railo.runtime.cache.CacheConnection;
import railo.runtime.cfx.CFXTagException;
import railo.runtime.cfx.CFXTagPool;
import railo.runtime.converter.ConverterException;
import railo.runtime.converter.WDDXConverter;
import railo.runtime.crypt.BlowfishEasy;
import railo.runtime.db.DataSource;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.HTTPException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.extension.Extension;
import railo.runtime.functions.cache.Util;
import railo.runtime.functions.other.CreateObject;
import railo.runtime.functions.string.Hash;
import railo.runtime.gateway.GatewayEntry;
import railo.runtime.gateway.GatewayEntryImpl;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.net.ntp.NtpClient;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.reflection.Reflector;
import railo.runtime.security.SecurityManager;
import railo.runtime.security.SecurityManagerImpl;
import railo.runtime.security.SerialNumber;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.Cluster;
import railo.runtime.type.scope.ClusterNotSupported;
import railo.runtime.type.scope.ClusterRemote;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.video.VideoExecuter;
import railo.runtime.video.VideoExecuterNotSupported;
import railo.transformer.library.function.FunctionLibException;
import railo.transformer.library.tag.TagLibException;

import com.allaire.cfx.CustomTag;

/**
 * 
 */
public final class ConfigWebAdmin {
    
    private static final Object NULL = new Object();
	private ConfigImpl config;
    private Document doc;
	private String password;
    //private SecurityManager accessorx;


    /**
     * 
     * @param config
     * @param password
     * @return returns a new instance of the class
     * @throws SAXException
     * @throws IOException
     */
    public static ConfigWebAdmin newInstance(ConfigImpl config, String password) throws SAXException, IOException {
        return new ConfigWebAdmin(config, password);
    }
    

    private void checkWriteAccess() throws SecurityException {
    	ConfigWebUtil.checkGeneralWriteAccess(config,password);
	}
    private void checkReadAccess() throws SecurityException {
    	ConfigWebUtil.checkGeneralReadAccess(config,password);
	}
    
    
    /**
     * 
     * @param config
     * @param isServer 
     * @param passwordOld
     * @param passwordNew
     * @throws SAXException
     * @throws IOException
     * @throws FunctionLibException
     * @throws TagLibException
     * @throws ClassNotFoundException
     * @throws PageException
     */
    public static void setPassword(ConfigImpl config, boolean isServer, String passwordOld, String passwordNew) 
		throws PageException, SAXException, ClassException, IOException, TagLibException, FunctionLibException {
    	//if(config.hasPassword())ConfigWebUtil.checkGeneralWriteAccess(config,passwordOld);
    	if(isServer)config=config.getConfigServerImpl();
	        
	        
	    if(!config.hasPassword()) { 
	        config.setPassword(passwordNew);
	        
	        ConfigWebAdmin admin = newInstance(config,passwordNew);
	        admin.setPassword(passwordNew);
	        admin.store();
	    }
	    else {
	    	ConfigWebUtil.checkGeneralWriteAccess(config,passwordOld);
	        ConfigWebAdmin admin = newInstance(config,passwordOld);
	        admin.setPassword(passwordNew);
	        admin.store();
	    }
	}
    
    
    
    /**
     * @param password
     * @throws ExpressionException 
     */
    public void setPassword(String password) throws SecurityException {
    	checkWriteAccess();
        Element root=doc.getDocumentElement();
        if(password==null || password.length()==0) {
            if(root.getAttribute("password")!=null)
                root.removeAttribute("password");
        }
        else {
            root.setAttribute("password",new BlowfishEasy("tpwisgh").encryptString(password));
        }
    }

    public void setVersion(double version) {
    	
    	Element root=doc.getDocumentElement();
    	String str = Caster.toString(version);
    	if(str.length()>3)str=str.substring(0,3);
        root.setAttribute("version",str); 
        
    }
    /*public void setId(String id) {
    	
    	Element root=doc.getDocumentElement();
    	if(!StringUtil.isEmpty(root.getAttribute("id"))) return;
    	root.setAttribute("id",id); 
    	try {
			store(config);
		} 
    	catch (Exception e) {}
    }*/
    
    /**
     * @param contextPath
     * @param password
     * @throws FunctionLibException
     * @throws TagLibException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SAXException
     * @throws PageException
     */
    public void setPassword(String contextPath,String password) throws PageException, SAXException, ClassException, IOException, TagLibException, FunctionLibException {
    	checkWriteAccess();
    	if(contextPath==null || contextPath.length()==0 || !(config instanceof ConfigServerImpl)) {
            setPassword(password);
        }
        else { 
            ConfigServerImpl cs=(ConfigServerImpl)config;
            ConfigWebImpl cw=cs.getConfigWebImpl(contextPath);
            if(cw!=null)setPassword(cw,false,cw.getPassword(),password);
        }
    }
    
    private ConfigWebAdmin(ConfigImpl config, String password) throws SAXException, IOException {
    	this.config=config;
    	this.password=password;
        doc=loadDocument(config.getConfigFile());
        if(config.getVersion()<2.0D){
        	try {
    			updateTo2(config);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
        //setId(config.getId());
    }    
    // FUTURE resources for old version of railo remove in never
    private void updateTo2(ConfigImpl config) throws PageException, ClassException, SAXException, IOException, TagLibException, FunctionLibException {
//    	 Server
    	if(config instanceof ConfigServer) {
    		addResourceProvider(
    				"ftp",
    				"railo.commons.io.res.type.ftp.FTPResourceProvider",
    				"lock-timeout:20000;socket-timeout:-1;client-timeout:60000");
    		// zip
    		addResourceProvider(
    				"zip",
    				"railo.commons.io.res.type.zip.ZipResourceProvider",
    				"lock-timeout:1000;case-sensitive:true;");
    		// tar
    		addResourceProvider(
    				"tar",
    				"railo.commons.io.res.type.tar.TarResourceProvider",
    				"lock-timeout:1000;case-sensitive:true;");
    		// tgz
    		addResourceProvider(
    				"tgz",
    				"railo.commons.io.res.type.tgz.TGZResourceProvider",
    				"lock-timeout:1000;case-sensitive:true;");
    		
    		
    		
    	}
    	else {
    		// ram
    		addResourceProvider(
    				"ram",
    				"railo.commons.io.res.type.ram.RamResourceProvider",
    				"case-sensitive:true;lock-timeout:1000;");
    		
    	}
    	setVersion(Caster.toDoubleValue(Info.getVersionAsString().substring(0,3),1.0D));
    	store(config);
    }

    private void addResourceProvider(String scheme,String clazz,String arguments) throws SecurityException {
    	checkWriteAccess();
    	
        Element resources=_getRootElement("resources");
        Element[] rpElements = ConfigWebFactory.getChildren(resources,"resource-provider");
      	String s;
        // update
        if(rpElements!=null) {
        	for(int i=0;i<rpElements.length;i++) {
        		s=rpElements[i].getAttribute("scheme");
        		if(!StringUtil.isEmpty(s) && s.equalsIgnoreCase(scheme))	{
        			rpElements[i].setAttribute("class", clazz);
        			rpElements[i].setAttribute("scheme", scheme);
        			rpElements[i].setAttribute("arguments", arguments);
        			return;
        		}
        	}
        }
        // Insert
    	Element el=doc.createElement("resource-provider");
    	resources.appendChild(XMLCaster.toRawNode(el));
    	el.setAttribute("class", clazz);
    	el.setAttribute("scheme", scheme);
    	el.setAttribute("arguments", arguments);
	}


	/**
     * load XML Document from XML File
     * @param config
     * @param xmlFile XML File to read
     * @return returns the Document
     * @throws SAXException
     * @throws IOException
     */
    private static Document loadDocument(Resource xmlFile) throws SAXException, IOException {
        DOMParser parser = new DOMParser();
        InputStream is=null;
        try {
        	is = IOUtil.toBufferedInputStream(xmlFile.getInputStream());
    	    InputSource source = new InputSource(is);
    	    parser.parse(source);
        }
        finally {
        	IOUtil.closeEL(is);
        }
	    return parser.getDocument();
    }
    
    /**
     * store changes back to railo xml
     * @throws PageException
     * @throws SAXException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws TagLibException
     * @throws FunctionLibException
     */
    public void store() throws PageException, SAXException, ClassException, IOException, TagLibException, FunctionLibException  {
    	store(config);
    }
    
    private synchronized void store(ConfigImpl config) throws PageException, SAXException, ClassException, IOException, TagLibException, FunctionLibException  {
    	renameOldstyleCFX();
    	
    	checkWriteAccess();
        createAbort();
        if(config instanceof ConfigServerImpl) {
        	XMLCaster.writeTo(doc,config.getConfigFile());
            
            ConfigServerImpl cs=(ConfigServerImpl) config;
            ConfigServerFactory.reloadInstance(cs);
            ConfigWeb[] webs=cs.getConfigWebs();
            for(int i=0;i<webs.length;i++) {
                ConfigWebFactory.reloadInstance((ConfigImpl)webs[i],true);
            }
        }
        else {
            XMLCaster.writeTo(doc,config.getConfigFile());
            //SystemUtil.sleep(10);
            
            ConfigWebFactory.reloadInstance(config,false);
        }
    }
    
    

    private void createAbort() {
    	try {
	    	ConfigWebFactory.getChildByName(doc.getDocumentElement(),"cfabort",true);
    	}
    	catch(Throwable t) {}
    }

	/**
     * sets Mail Logger to Config
     * @param logFile
     * @param level 
     * @throws PageException 
     */
    public void setMailLog(String logFile, String level) throws PageException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_MAIL);
        
        if(!hasAccess)
            throw new SecurityException("no access to update mail server settings");
        ConfigWebUtil.getFile(config,config.getRootDirectory(),logFile,FileUtil.TYPE_FILE);
        
        Element mail=_getRootElement("mail");
        mail.setAttribute("log",logFile);
        mail.setAttribute("log-level",level);
        //config.setMailLogger(logFile.getCanonicalPath());
    }

    /**
     * sets if spool is enable or not
     * @param spoolEnable
     * @throws SecurityException
     */
    public void setMailSpoolEnable(Boolean spoolEnable) throws SecurityException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_MAIL);
        
      	if(!hasAccess)
            throw new SecurityException("no access to update mail server settings");
        Element mail=_getRootElement("mail");
        mail.setAttribute("spool-enable",Caster.toString(spoolEnable,""));
        //config.setMailSpoolEnable(spoolEnable);
    }

    /**
     * sets if er interval is enable or not
     * @param interval
     * @throws SecurityException
     */
    public void setMailSpoolInterval(Integer interval) throws SecurityException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_MAIL);
        if(!hasAccess)
            throw new SecurityException("no access to update mail server settings");
        Element mail=_getRootElement("mail");
        mail.setAttribute("spool-interval",Caster.toString(interval,""));
        //config.setMailSpoolInterval(interval);
    }

    /**
     * sets the timeout for the spooler for one job
     * @param timeout
     * @throws SecurityException
     */
    public void setMailTimeout(Integer timeout) throws SecurityException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_MAIL);
        if(!hasAccess)
            throw new SecurityException("no access to update mail server settings");
        Element mail=_getRootElement("mail");
        mail.setAttribute("timeout",Caster.toString(timeout,""));
        //config.setMailTimeout(timeout);
    }
    
    /**
     * sets the charset for the mail
     * @param timeout
     * @throws SecurityException
     */
    public void setMailDefaultCharset(String charset) throws PageException {
    	checkWriteAccess();
    	boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_MAIL);
    	if(!hasAccess) throw new SecurityException("no access to update mail server settings");
        
    	if(!StringUtil.isEmpty(charset)){
			try {
				IOUtil.checkEncoding(charset);
			} catch (IOException e) {
				throw Caster.toPageException(e);
			}
    	}
    	
		Element mail=_getRootElement("mail");
        mail.setAttribute("default-encoding",charset);
        //config.setMailDefaultEncoding(charset);		
	}

	/**
     * insert or update a mailserver on system
     * @param hostName
     * @param username
     * @param password
     * @param port
	 * @param ssl 
	 * @param tls 
     * @throws PageException 
     */
    public void updateMailServer(String hostName,String username,String password, int port, boolean tls, boolean ssl) throws PageException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_MAIL);
        if(!hasAccess)
            throw new SecurityException("no access to update mail server settings");
        
        /*try {
            SMTPVerifier.verify(hostName,username,password,port);
        } catch (SMTPException e) {
            throw Caster.toPageException(e);
        }*/
        
        Element mail=_getRootElement("mail");
        if(port<1) port=21;

        if(hostName==null || hostName.trim().length()==0)
            throw new ExpressionException("Host (SMTP) can be a empty value");
        hostName=hostName.trim();
        
        
        Element[] children = ConfigWebFactory.getChildren(mail,"server");
      	
        // Update
        for(int i=0;i<children.length;i++) {
      	    Element el=children[i];
      	    String smtp=el.getAttribute("smtp");
  			if(smtp!=null && smtp.equalsIgnoreCase(hostName)) {
	      		el.setAttribute("username",username);
	      		el.setAttribute("password",ConfigWebFactory.encrypt(password));
	      		el.setAttribute("port",Caster.toString(port));
	      		el.setAttribute("tls",Caster.toString(tls));
	      		el.setAttribute("ssl",Caster.toString(ssl));
	      		return ;
  			}
      	}
        
        // Insert
      	Element server = doc.createElement("server");
      	server.setAttribute("smtp",hostName);
      	server.setAttribute("username",username);
      	server.setAttribute("password",ConfigWebFactory.encrypt(password));
      	server.setAttribute("port",Caster.toString(port));
      	server.setAttribute("tls",Caster.toString(tls));
      	server.setAttribute("ssl",Caster.toString(ssl));
      	mail.appendChild(XMLCaster.toRawNode(server));
      	
    }

    /**
     *removes a mailserver from system
     * @param hostName
     * @throws SecurityException 
     */
    public void removeMailServer(String hostName) throws SecurityException {
    	checkWriteAccess();
    	
        Element mail=_getRootElement("mail");
        Element[] children = ConfigWebFactory.getChildren(mail,"server");
        if(children.length>0) {
	      	for(int i=0;i<children.length;i++) {
	      	    Element el=children[i];
	      	    String smtp=el.getAttribute("smtp");
	  			if(smtp!=null && smtp.equalsIgnoreCase(hostName)) {
		      		mail.removeChild(children[i]);
	  			}
	      	}
        }
    }

    /**
     * insert or update a mapping on system
     * @param virtual
     * @param physical
     * @param archive
     * @param primary
     * @param trusted
     * @param toplevel 
     * @throws ExpressionException
     * @throws SecurityException
     */
    public void updateMapping(String virtual, String physical,String archive,String primary, boolean trusted, boolean toplevel) throws ExpressionException, SecurityException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_MAPPING);
        
        virtual=virtual.trim(); 
        physical=physical.trim();
        archive=archive.trim();
        primary=primary.trim();
        if(!hasAccess)
            throw new SecurityException("no access to update mappings");
        
        // check virtual
            if(virtual==null || virtual.length()==0)
                throw new ExpressionException("virtual path can be a empty value");
            virtual=virtual.replace('\\','/');
            
            if(!virtual.equals("/") && virtual.endsWith("/"))
    	            virtual=virtual.substring(0,virtual.length()-1);
            
            if(virtual.charAt(0)!='/')
                throw new ExpressionException("virtual path must start with [/]");
        boolean isArchive=primary.equalsIgnoreCase("archive");
        
        if((physical.length()+archive.length())==0)
            throw new ExpressionException("physical or archive must gave a value");
        
        if(isArchive && archive.length()==0 ) isArchive=false;
        //print.ln("isArchive:"+isArchive);
        
        if(!isArchive && archive.length()>0 && physical.length()==0 ) isArchive=true;
        //print.ln("isArchive:"+isArchive);
        
        
        
        Element mappings=_getRootElement("mappings");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(mappings,"mapping");
      	for(int i=0;i<children.length;i++) {
      	    String v=children[i].getAttribute("virtual");
      	    if(v!=null) {
      	        if(!v.equals("/") && v.endsWith("/"))
      	            v=v.substring(0,v.length()-1);
              
	      	    if(v.equals(virtual)) {
		      		Element el=children[i];
		      		if(physical.length()>0) {
                        el.setAttribute("physical",physical);
                    }
                    else if(el.hasAttribute("physical")) {
                        el.removeAttribute("physical");
                    }
		      		if(archive.length()>0) {
                        el.setAttribute("archive",archive);
                    }
                    else if(el.hasAttribute("archive")) {
                        el.removeAttribute("archive");
                    }
		      		el.setAttribute("primary",isArchive?"archive":"physical");
		      		el.setAttribute("trusted",Caster.toString(trusted));
		      		el.setAttribute("toplevel",Caster.toString(toplevel));
		      		return ;
	  			}
      	    }
      	}
      	
      	// Insert
      	Element el=doc.createElement("mapping");
      	mappings.appendChild(el);
      	el.setAttribute("virtual",virtual);
      	if(physical.length()>0)el.setAttribute("physical",physical);
  		if(archive.length()>0)el.setAttribute("archive",archive);
  		el.setAttribute("primary",isArchive?"archive":"physical");
  		el.setAttribute("trusted",Caster.toString(trusted));
  		el.setAttribute("toplevel",Caster.toString(toplevel));
  		
  		// set / to the end
  		children = ConfigWebFactory.getChildren(mappings,"mapping");
      	for(int i=0;i<children.length;i++) {
      	    String v=children[i].getAttribute("virtual");
      	    
  	        if(v!=null && v.equals("/")) {
	      		el=children[i];
	      		mappings.removeChild(el);
	      		mappings.appendChild(el);
	      		return ;
  			}

      	}
  		
    }


    /**
     * delete a mapping on system
     * @param virtual
     * @throws ExpressionException
     * @throws SecurityException 
     */
    public void removeMapping(String virtual) throws ExpressionException, SecurityException {
    	checkWriteAccess();
    	// check parameters
        if(virtual==null || virtual.length()==0)
            throw new ExpressionException("virtual path can be a empty value");
        virtual=virtual.replace('\\','/');
        
        if(!virtual.equals("/") && virtual.endsWith("/"))
	            virtual=virtual.substring(0,virtual.length()-1);
        if(virtual.charAt(0)!='/')
            throw new ExpressionException("virtual path must start with [/]");
        
        
        Element mappings=_getRootElement("mappings");

        Element[] children = ConfigWebFactory.getChildren(mappings,"mapping");
      	for(int i=0;i<children.length;i++) {
      	    String v=children[i].getAttribute("virtual");
      	    if(v!=null) {
      	        if(!v.equals("/") && v.endsWith("/"))
      	            v=v.substring(0,v.length()-1);
	  	    	if(v!=null && v.equals(virtual)) {
		      		Element el=children[i];
		      		mappings.removeChild(el);
	  			}
      	    }
      	}
    }

    /**
     * delete a customtagmapping on system
     * @param virtual
     * @throws SecurityException 
     */
    public void removeCustomTag(String virtual) throws SecurityException {
    	checkWriteAccess();
    	
        Element mappings=_getRootElement("custom-tag");
        Element[] children = ConfigWebFactory.getChildren(mappings,"mapping");
      	for(int i=0;i<children.length;i++) {
      	    if(virtual.equals("/"+i))mappings.removeChild(children[i]);
      	}
    }
    
    public void removeComponentMapping(String virtual) throws SecurityException {
    	checkWriteAccess();
    	
        Element mappings=_getRootElement("component");
        Element[] children = ConfigWebFactory.getChildren(mappings,"mapping");
      	for(int i=0;i<children.length;i++) {
      	    if(virtual.equals("/"+i))mappings.removeChild(children[i]);
      	}
    }
    
    


    /**
     * insert or update a mapping for Custom Tag
     * @param virtual
     * @param physical
     * @param archive
     * @param primary
     * @param trusted
     * @throws ExpressionException
     * @throws SecurityException
     */
    public void updateCustomTag(String virtual,String physical,String archive,String primary, boolean trusted) throws ExpressionException, SecurityException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_CUSTOM_TAG);
        if(!hasAccess)
            throw new SecurityException("no access to change custom tag settings");
        
        //virtual="/custom-tag";
        
        boolean isArchive=primary.equalsIgnoreCase("archive");
        if(isArchive && archive.length()==0 ) {
            throw new ExpressionException("archive must have a value when primary has value archive");
        }
        if(!isArchive && physical.length()==0 ) {
            throw new ExpressionException("physical must have a value when primary has value physical");
        }
        
        Element mappings=_getRootElement("custom-tag");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(mappings,"mapping");
        for(int i=0;i<children.length;i++) {
      	    if(("/"+i).equals(virtual)) {
	      		Element el=children[i];
	      		el.setAttribute("physical",physical);
	      		el.setAttribute("archive",archive);
	      		el.setAttribute("primary",primary.equalsIgnoreCase("archive")?"archive":"physical");
	      		el.setAttribute("trusted",Caster.toString(trusted));
	      		return ;
  			}
      	}
      	
      	// Insert
      	Element el=doc.createElement("mapping");
      	mappings.appendChild(el);
      	if(physical.length()>0)el.setAttribute("physical",physical);
  		if(archive.length()>0)el.setAttribute("archive",archive);
  		el.setAttribute("primary",primary.equalsIgnoreCase("archive")?"archive":"physical");
  		el.setAttribute("trusted",Caster.toString(trusted));
    }
    

    public void updateComponentMapping(String virtual,String physical,String archive,String primary, boolean trusted) throws ExpressionException, SecurityException {
    	checkWriteAccess();
    	boolean hasAccess=true;// TODO ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_CUSTOM_TAG);
        //if(!hasAccess)
        //    throw new SecurityException("no access to change custom tag settings");
        
        //virtual="/custom-tag";
        
        boolean isArchive=primary.equalsIgnoreCase("archive");
        if(isArchive && archive.length()==0 ) {
            throw new ExpressionException("archive must have a value when primary has value archive");
        }
        if(!isArchive && physical.length()==0 ) {
            throw new ExpressionException("physical must have a value when primary has value physical");
        }
        
        Element mappings=_getRootElement("component");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(mappings,"mapping");
        for(int i=0;i<children.length;i++) {
      	    if(("/"+i).equals(virtual)) {
	      		Element el=children[i];
	      		el.setAttribute("physical",physical);
	      		el.setAttribute("archive",archive);
	      		el.setAttribute("primary",primary.equalsIgnoreCase("archive")?"archive":"physical");
	      		el.setAttribute("trusted",Caster.toString(trusted));
	      		return ;
  			}
      	}
      	
      	// Insert
      	Element el=doc.createElement("mapping");
      	mappings.appendChild(el);
      	if(physical.length()>0)el.setAttribute("physical",physical);
  		if(archive.length()>0)el.setAttribute("archive",archive);
  		el.setAttribute("primary",primary.equalsIgnoreCase("archive")?"archive":"physical");
  		el.setAttribute("trusted",Caster.toString(trusted));
    }

    


    /**
     * insert or update a Java CFX Tag
     * @param name
     * @param strClass
     * @throws PageException 
     */
    public void updateJavaCFX(String name,String strClass) throws PageException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_CFX_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to change cfx settings");
        
        
        try {
        	Class clazz = ClassUtil.loadClass(config.getClassLoader(),strClass);
			if(!Reflector.isInstaneOf(clazz, CustomTag.class))
				throw new ExpressionException("class ["+strClass+"] must implement interface ["+CustomTag.class.getName()+"]");
           
        } 
        catch (ClassException e) {
        	
        	throw Caster.toPageException(e);
		}
        
        
        
        
        if(name==null || name.length()==0)
            throw new ExpressionException("class name can't be a empty value");
        
        renameOldstyleCFX();
        
        
        Element tags=_getRootElement("ext-tags");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(tags,"ext-tag");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("name");
      	    
      	    if(n!=null && n.equalsIgnoreCase(name)) {
	      		Element el=children[i];
	      		if(!"java".equalsIgnoreCase(el.getAttribute("type"))) throw new ExpressionException("there is already a c++ cfx tag with this name");
      	    	el.setAttribute("class",strClass);
	      		el.setAttribute("type","java");
	      		return ;
  			}
      	    
      	}
      	
      	// Insert
      	Element el=doc.createElement("ext-tag");
      	tags.appendChild(el);
      	el.setAttribute("class",strClass);
      	el.setAttribute("name",name);
  		el.setAttribute("type","java");  		
    }
    
    public void updateCPPCFX(String name, String procedure, String strServerLibrary, boolean keepAlive) throws PageException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_CFX_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to change cfx settings");
        
        // name
        if(StringUtil.isEmpty(name))
            throw new ExpressionException("name cannot be a empty value");
        
        // serverLibrary
        if(StringUtil.isEmpty(strServerLibrary)) throw new ExpressionException("serverLibrary cannot be a empty value");
        Resource serverLibrary = ResourceUtil.toResourceExisting(config, strServerLibrary);
        
        // procedure
        if(StringUtil.isEmpty(procedure)) throw new ExpressionException("procedure cannot be a empty value");
        
        renameOldstyleCFX();
        
        
        Element tags=_getRootElement("ext-tags");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(tags,"ext-tag");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("name");
      	    
      	    if(n!=null && n.equalsIgnoreCase(name)) {
	      		Element el=children[i];
	      		if(!"cpp".equalsIgnoreCase(el.getAttribute("type"))) throw new ExpressionException("there is already a java cfx tag with this name");
      	    	el.setAttribute("server-library",serverLibrary.getAbsolutePath());
      	    	el.setAttribute("procedure",procedure);
      	    	el.setAttribute("keep-alive",Caster.toString(keepAlive));
      	    	el.setAttribute("type","cpp");
	      		return ;
  			}
      	    
      	}
      	
      	// Insert
      	Element el=doc.createElement("ext-tag");
      	tags.appendChild(el);
      	el.setAttribute("server-library",serverLibrary.getAbsolutePath());
    	el.setAttribute("procedure",procedure);
    	el.setAttribute("keep-alive",Caster.toString(keepAlive));
    	el.setAttribute("name",name);
  		el.setAttribute("type","cpp"); 
	}
    
    private void renameOldstyleCFX() {
    	
        Element tags=_getRootElement("ext-tags",false,true);
        if(tags!=null) return;
        tags=_getRootElement("cfx-tags",false,true);
        if(tags==null) return;
        
        
        
    	Element newTags = _getRootElement("ext-tags");
    	Element[] children = ConfigWebFactory.getChildren(tags,"cfx-tag");
    	String type;
      	// copy
    	for(int i=0;i<children.length;i++) {
      	    Element el=doc.createElement("ext-tag");
      	    newTags.appendChild(el);
      	    type=children[i].getAttribute("type");
      	    // java
      	    if(type.equalsIgnoreCase("java")){
      	    	el.setAttribute("class",children[i].getAttribute("class"));
      	    }
      	    // c++
      	    else {
      	    	el.setAttribute("server-library",children[i].getAttribute("server-library"));
      	    	el.setAttribute("procedure",children[i].getAttribute("procedure"));
      	    	el.setAttribute("keep-alive",children[i].getAttribute("keep-alive"));
      	    	
      	    }
          	el.setAttribute("name",children[i].getAttribute("name"));
      		el.setAttribute("type",children[i].getAttribute("type"));  
      	}
    	
    	// remove old
    	for(int i=0;i<children.length;i++) {
    		tags.removeChild(children[i]);
    	}
    	tags.getParentNode().removeChild(tags);
	}
    
    
    public static boolean fixPSQ(Document doc) throws SecurityException {
    	
    	Element datasources=ConfigWebFactory.getChildByName(doc.getDocumentElement(),"data-sources",false,true);
        if(datasources!=null && datasources.hasAttribute("preserve-single-quote")){
        	Boolean b=Caster.toBoolean(datasources.getAttribute("preserve-single-quote"),null);
        	if(b!=null)datasources.setAttribute("psq",Caster.toString(!b.booleanValue()));
        	datasources.removeAttribute("preserve-single-quote");
        	return true;
        }
    	return false;
    }

    public static boolean fixS3(Document doc) {
    	Element resources=ConfigWebFactory.getChildByName(doc.getDocumentElement(),"resources",false,true);
        
        Element[] providers = ConfigWebFactory.getChildren(resources,"resource-provider");
        
        // replace extension class with core class
        for(int i=0;i<providers.length;i++) {
        	if("s3".equalsIgnoreCase(providers[i].getAttribute("scheme"))) {
        		if("railo.extension.io.resource.type.s3.S3ResourceProvider".equalsIgnoreCase(providers[i].getAttribute("class"))){
        			providers[i].setAttribute("class", S3ResourceProvider.class.getName());
        			return true;
        		}
        		return false;
        	}
        }
        
        
        // FUTURE remove this part for version 4.0
        // add s3 when not
        Element el=doc.createElement("resource-provider");
        el.setAttribute("scheme", "s3");
        el.setAttribute("class", S3ResourceProvider.class.getName());
        el.setAttribute("arguments", "lock-timeout:10000;");
        resources.appendChild(el);
        
        return true;
	}

    
    
  
    public void verifyCFX(String name) throws PageException {
    	CFXTagPool pool=config.getCFXTagPool();
		CustomTag ct=null;
		try {
			ct = pool.getCustomTag(name);
		} 
        catch (CFXTagException e) {
			throw Caster.toPageException(e);
		}
        finally {
        	if(ct!=null)pool.releaseCustomTag(ct);
        }
		
	}
    


	public void verifyJavaCFX(String name,String strClass) throws PageException {
    	try {
    		Class clazz = ClassUtil.loadClass(config.getClassLoader(),strClass);
			if(!Reflector.isInstaneOf(clazz, CustomTag.class))
				throw new ExpressionException("class ["+strClass+"] must implement interface ["+CustomTag.class.getName()+"]");
        } 
        catch (ClassException e) {
        	throw Caster.toPageException(e);
		}
        
        if(StringUtil.startsWithIgnoreCase(name,"cfx_"))name=name.substring(4);
        if(StringUtil.isEmpty(name))
            throw new ExpressionException("class name can't be a empty value");
    }
    

    /**
     * remove a CFX Tag
     * @param name
     * @throws ExpressionException
     * @throws SecurityException 
     */
    public void removeCFX(String name) throws ExpressionException, SecurityException {
    	checkWriteAccess();
    	// check parameters
        if(name==null || name.length()==0)
            throw new ExpressionException("name for CFX Tag can be a empty value");
        
        renameOldstyleCFX();
        
        Element mappings=_getRootElement("ext-tags");

        Element[] children = ConfigWebFactory.getChildren(mappings,"ext-tag");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("name"); 
  	    	if(n!=null && n.equalsIgnoreCase(name)) {
	      		mappings.removeChild(children[i]);
  			}
  	    }
    }

    /**
     * update or insert new database connection
     * @param name
     * @param clazzName
     * @param dsn
     * @param username
     * @param password
     * @param host 
     * @param database 
     * @param port 
     * @param connectionLimit 
     * @param connectionTimeout 
     * @param blob 
     * @param clob 
     * @param allow 
     * @param storage 
     * @param custom 
     * @throws ExpressionException
     * @throws SecurityException
     */
    public void updateDataSource(String name, String newName, String clazzName, String dsn, String username,String password,
            String host,String database,int port,int connectionLimit, int connectionTimeout,long metaCacheTimeout,
            boolean blob,boolean clob,int allow,boolean validate,boolean storage, Struct custom) throws ExpressionException, SecurityException {

    	checkWriteAccess();
    	SecurityManager sm = config.getSecurityManager();
    	short access = sm.getAccess(SecurityManager.TYPE_DATASOURCE);
    	boolean hasAccess=true;
    	boolean hasInsertAccess=true;
    	int maxLength=0;
    	
    	if(access==SecurityManager.VALUE_YES) hasAccess=true;
    	else if(access==SecurityManager.VALUE_NO) hasAccess=false;
    	else if(access>=SecurityManager.VALUE_1 && access<=SecurityManager.VALUE_10){
    		int existingLength=getDatasourceLength(config);
    		maxLength=access-SecurityManager.NUMBER_OFFSET;
    		hasInsertAccess=maxLength>existingLength;
        	//print.ln("maxLength:"+maxLength);
        	//print.ln("existingLength:"+existingLength);
    	}
    	//print.ln("hasAccess:"+hasAccess);
    	//print.ln("hasInsertAccess:"+hasInsertAccess);
        
        //boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_DATASOURCE);
        if(!hasAccess)
            throw new SecurityException("no access to update datsource connections");
        
        // check parameters
        if(name==null || name.length()==0)
            throw new ExpressionException("name can't be a empty value");
        
        try {
			ClassUtil.loadInstance(clazzName);
		}
        catch (ClassException e) {
            throw new ExpressionException(e.getMessage());
		}
        
        Element datasources=_getRootElement("data-sources");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(datasources,"data-source");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("name");
      	    
      	    if(n.equalsIgnoreCase(name)) {
	      		Element el=children[i];
	      		if(password.equalsIgnoreCase("****************"))
	            	password=el.getAttribute("password");
	            
	      		if(!StringUtil.isEmpty(newName) && !newName.equals(name))
	      			el.setAttribute("name",newName);
	      		el.setAttribute("class",clazzName);
	      		el.setAttribute("dsn",dsn);
	      		el.setAttribute("username",username);
	      		el.setAttribute("password",ConfigWebFactory.encrypt(password));

                el.setAttribute("host",host);
                el.setAttribute("database",database);
                el.setAttribute("port",Caster.toString(port));
                el.setAttribute("connectionLimit",Caster.toString(connectionLimit));
                el.setAttribute("connectionTimeout",Caster.toString(connectionTimeout));
                el.setAttribute("metaCacheTimeout",Caster.toString(metaCacheTimeout));
                el.setAttribute("blob",Caster.toString(blob));
                el.setAttribute("clob",Caster.toString(clob));
                el.setAttribute("allow",Caster.toString(allow));
                el.setAttribute("validate",Caster.toString(validate));
                el.setAttribute("storage",Caster.toString(storage));
                el.setAttribute("custom",toStringURLStyle(custom));
                
	      		return ;
  			}
      	}
      	
      	if(!hasInsertAccess)
            throw new SecurityException("no access to add datsource connections, the maximum count of ["+maxLength+"] datasources is reached");
      	
      	// Insert
      	Element el=doc.createElement("data-source");
      	datasources.appendChild(el);
      	if(!StringUtil.isEmpty(newName))
      		el.setAttribute("name",newName);
      	else 
      		el.setAttribute("name",name);
  		el.setAttribute("class",clazzName);
  		el.setAttribute("dsn",dsn);
  		if(username.length()>0)el.setAttribute("username",username);
  		if(password.length()>0)el.setAttribute("password",ConfigWebFactory.encrypt(password));
        
        el.setAttribute("host",host);
        el.setAttribute("database",database);
        if(port>-1)el.setAttribute("port",Caster.toString(port));
        if(connectionLimit>-1)el.setAttribute("connectionLimit",Caster.toString(connectionLimit));
        if(connectionTimeout>-1)el.setAttribute("connectionTimeout",Caster.toString(connectionTimeout));
        if(metaCacheTimeout>-1)el.setAttribute("metaCacheTimeout",Caster.toString(metaCacheTimeout));
        
        
        
        el.setAttribute("blob",Caster.toString(blob));
        el.setAttribute("clob",Caster.toString(clob));
        el.setAttribute("validate",Caster.toString(validate));
        el.setAttribute("storage",Caster.toString(storage));
        if(allow>-1)el.setAttribute("allow",Caster.toString(allow));
        el.setAttribute("custom",toStringURLStyle(custom));
        /*
  		    String host,String database,int port,String connectionLimit, String connectionTimeout,
            boolean blob,boolean clob,int allow,Struct custom
        );
        */
    }
    

	public void updateGatewayEntry(String id,String className, String cfcPath, String listenerCfcPath,int startupMode,Struct custom, boolean readOnly) throws PageException {
		
		checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_GATEWAY);
        
    	if(!hasAccess)
            throw new SecurityException("no access to update gateway entry");
        
    	
        // check parameters
        id=id.trim();
    	if(StringUtil.isEmpty(id))
            throw new ExpressionException("id can't be a empty value");
    	
    	if(StringUtil.isEmpty(className) && StringUtil.isEmpty(cfcPath))
    		throw new ExpressionException("you must define className or cfcPath");
    	
        try {
        	if(!StringUtil.isEmpty(className)){
	        	Class clazz = ClassUtil.loadClass(className);
        	}
		}
        catch (ClassException e) {
            throw new ExpressionException(e.getMessage());
		}
        
        Element parent=_getRootElement("gateways");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(parent,"gateway");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("id");
      	    Element el=children[i];
      	    if(n.equalsIgnoreCase(id)) {
      	    	el.setAttribute("class",className);
      	    	el.setAttribute("cfc-path",cfcPath);
      	    	el.setAttribute("listener-cfc-path",listenerCfcPath);
      	    	el.setAttribute("startup-mode",GatewayEntryImpl.toStartup(startupMode, "automatic"));
      	    	el.setAttribute("custom",toStringURLStyle(custom));
	      		el.setAttribute("read-only",Caster.toString(readOnly));
	      		return;
  			}
      	  
      	}
      	// Insert
      	Element el=doc.createElement("gateway");
      	parent.appendChild(el);
      	el.setAttribute("id",id);
      	el.setAttribute("cfc-path",cfcPath);
      	el.setAttribute("listener-cfc-path",listenerCfcPath);
	    el.setAttribute("startup-mode",GatewayEntryImpl.toStartup(startupMode, "automatic"));
	    el.setAttribute("class",className);
  		el.setAttribute("custom",toStringURLStyle(custom));
  		el.setAttribute("read-only",Caster.toString(readOnly));
        
    }

	public void updateCacheConnection(String name, String classname,int _default, Struct custom,boolean readOnly,boolean storage) throws PageException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_CACHE);
		if(!hasAccess)
            throw new SecurityException("no access to update cache connection");
        
    	
        // check parameters
        name=name.trim();
    	if(StringUtil.isEmpty(name))
            throw new ExpressionException("name can't be a empty value");
    	//else if(name.equals("template") || name.equals("object"))
    		//throw new ExpressionException("name ["+name+"] is not allowed for a cache connection, the following names are reserved words [object,template]");	
        
        try {
        	Class clazz = ClassUtil.loadClass(config.getClassLoader(),classname);
			if(!Reflector.isInstaneOf(clazz, Cache.class))
				throw new ExpressionException("class ["+clazz.getName()+"] is not of type ["+Cache.class.getName()+"]");
		}
        catch (ClassException e) {
            throw new ExpressionException(e.getMessage());
		}
        
        Element parent=_getRootElement("cache");

        if(name.equalsIgnoreCase(parent.getAttribute("default-template")))
    		parent.removeAttribute("default-template");
        if(name.equalsIgnoreCase(parent.getAttribute("default-object")))
    		parent.removeAttribute("default-object");
        if(name.equalsIgnoreCase(parent.getAttribute("default-query")))
    		parent.removeAttribute("default-query");
        if(name.equalsIgnoreCase(parent.getAttribute("default-resource")))
    		parent.removeAttribute("default-resource");
        
        
        if(_default==ConfigImpl.CACHE_DEFAULT_OBJECT){
        	parent.setAttribute("default-object",name);
        }
        else if(_default==ConfigImpl.CACHE_DEFAULT_TEMPLATE){
        	parent.setAttribute("default-template",name);
        }
        else if(_default==ConfigImpl.CACHE_DEFAULT_QUERY){
        	parent.setAttribute("default-query",name);
        }
        else if(_default==ConfigImpl.CACHE_DEFAULT_RESOURCE){
        	parent.setAttribute("default-resource",name);
        }
        
        // Update
        //boolean isUpdate=false;
        Element[] children = ConfigWebFactory.getChildren(parent,"connection");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("name");
      	    Element el=children[i];
      	    if(n.equalsIgnoreCase(name)) {
      	    	el.setAttribute("class",classname);
	      		//el.setAttribute("default",Caster.toString(_default));
	      		el.setAttribute("custom",toStringURLStyle(custom));
	      		el.setAttribute("read-only",Caster.toString(readOnly));
	      		el.setAttribute("storage",Caster.toString(storage));
	      		return;
  			}
      	  
      	}
      	
      	// Insert
      	Element el=doc.createElement("connection");
      	parent.appendChild(el);
      	el.setAttribute("name",name);
  		el.setAttribute("class",classname);
  		//el.setAttribute("default",Caster.toString(_default));
  		el.setAttribute("custom",toStringURLStyle(custom));
  		el.setAttribute("read-only",Caster.toString(readOnly));
  		el.setAttribute("storage",Caster.toString(storage));
        
    }
	
	public void removeCacheDefaultConnection(int type) throws PageException {
    	checkWriteAccess();
    	
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_CACHE);
        if(!hasAccess)
            throw new SecurityException("no access to update cache connections");
        
        Element parent=_getRootElement("cache");
        if(type==ConfigImpl.CACHE_DEFAULT_OBJECT){
        	parent.removeAttribute("default-object");
        }
        else if(type==ConfigImpl.CACHE_DEFAULT_TEMPLATE){
        	parent.removeAttribute("default-template");
        }
        else if(type==ConfigImpl.CACHE_DEFAULT_QUERY){
        	parent.removeAttribute("default-query");
        }
        else if(type==ConfigImpl.CACHE_DEFAULT_RESOURCE){
        	parent.removeAttribute("default-resource");
        }
    }
	
	public void updateCacheDefaultConnection(int type,String name) throws PageException {
    	checkWriteAccess();
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_CACHE);
        
    	if(!hasAccess)
            throw new SecurityException("no access to update cache default connections");
        
        Element parent=_getRootElement("cache");
        if(type==ConfigImpl.CACHE_DEFAULT_OBJECT){
        	parent.setAttribute("default-object", name);
        }
        else if(type==ConfigImpl.CACHE_DEFAULT_TEMPLATE){
        	parent.setAttribute("default-template", name);
        }
        else if(type==ConfigImpl.CACHE_DEFAULT_QUERY){
        	parent.setAttribute("default-query", name);
        }
        else if(type==ConfigImpl.CACHE_DEFAULT_RESOURCE){
        	parent.setAttribute("default-resource", name);
        }
    }
	
    public void removeResourceProvider(Class clazz) throws PageException {
    	checkWriteAccess();
    	SecurityManager sm = config.getSecurityManager();
    	short access = sm.getAccess(SecurityManager.TYPE_FILE);
    	boolean hasAccess=access==SecurityManager.VALUE_YES;
    	
    	String className=clazz.getName();
    	
    	if(!hasAccess)
            throw new SecurityException("no access to remove resources");
        
        Element parent=_getRootElement("resources");
        
        // remove
        Element[] children = ConfigWebFactory.getChildren(parent,"resource-provider");
      	for(int i=0;i<children.length;i++) {
      	    String cn=children[i].getAttribute("class");
      	    if(cn.equalsIgnoreCase(className)) {
      	    	parent.removeChild(children[i]);                
	      		break;
  			}
      	}
	}	
    
    public void updateResourceProvider(String scheme, Class clazz,Struct arguments) throws PageException {
    	updateResourceProvider(scheme, clazz, toStringCSSStyle(arguments));
    }

	public void updateResourceProvider(String scheme, Class clazz,String arguments) throws PageException {
    	checkWriteAccess();
    	SecurityManager sm = config.getSecurityManager();
    	short access = sm.getAccess(SecurityManager.TYPE_FILE);
    	boolean hasAccess=access==SecurityManager.VALUE_YES;
    	
    	String className=clazz.getName();
    	
    	if(!hasAccess)
            throw new SecurityException("no access to update resources");
        
        // check parameters
    	if(StringUtil.isEmpty(scheme))throw new ExpressionException("scheme can't be a empty value");
    	
        Element parent=_getRootElement("resources");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(parent,"resource-provider");
      	for(int i=0;i<children.length;i++) {
      	    String cn=children[i].getAttribute("class");
      	    if(cn.equalsIgnoreCase(className)) {
	      		Element el=children[i];
	      		el.setAttribute("scheme",scheme);
	      		el.setAttribute("arguments",arguments);                
	      		return ;
  			}
      	}
      	
      	// Insert
      	Element el=doc.createElement("resource-provider");
      	parent.appendChild(el);
  		el.setAttribute("scheme",scheme);
  		el.setAttribute("arguments",arguments);  
  		el.setAttribute("class",className);
	}	
	
	public void updateDefaultResourceProvider(Class clazz, String arguments) throws PageException {
    	checkWriteAccess();
    	SecurityManager sm = config.getSecurityManager();
    	short access = sm.getAccess(SecurityManager.TYPE_FILE);
    	boolean hasAccess=access==SecurityManager.VALUE_YES;
    	
    	String className=clazz.getName();
    	
    	if(!hasAccess)
            throw new SecurityException("no access to update resources");
        
        Element parent=_getRootElement("resources");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(parent,"default-resource-provider");
      	for(int i=0;i<children.length;i++) {
      	    Element el=children[i];
      		el.setAttribute("arguments",arguments);                
      		return;
      	}
      	
      	// Insert
      	Element el=doc.createElement("default-resource-provider");
      	parent.appendChild(el);
  		el.setAttribute("arguments",arguments);  
  		el.setAttribute("class",className);
	}

    private int getDatasourceLength(ConfigImpl config) {
    	Map ds = config.getDataSourcesAsMap();
    	Iterator it = ds.keySet().iterator();
    	int len=0;
    	
    	while(it.hasNext()) {
    		if(!((DataSource)ds.get(it.next())).isReadOnly())len++;
    	}
		return len;
	}


	private static String toStringURLStyle(Struct sct) {
        String[] keys = sct.keysAsString();
        StringBuffer rtn=new StringBuffer();
        
        for(int i=0;i<keys.length;i++) {
            
	            if(rtn.length()>0)rtn.append('&');
	            rtn.append(URLEncoder.encode(keys[i]));
	            rtn.append('=');
	            rtn.append(URLEncoder.encode(Caster.toString(sct.get(keys[i],null),"")));
        }
        return rtn.toString();
    }

	private static String toStringCSSStyle(Struct sct) {
        String[] keys = sct.keysAsString();
        StringBuffer rtn=new StringBuffer();
        
        for(int i=0;i<keys.length;i++) {
            
	            if(rtn.length()>0)rtn.append(';');
	            rtn.append(URLEncoder.encode(keys[i]));
	            rtn.append(':');
	            rtn.append(URLEncoder.encode(Caster.toString(sct.get(keys[i],null),"")));
        }
        return rtn.toString();
    }

    public Query getResourceProviders() throws PageException {
    	checkReadAccess();
        // check parameters
        Element parent=_getRootElement("resources");
        Element[] elProviders = ConfigWebFactory.getChildren(parent,"resource-provider");
        Element[] elDefaultProviders = ConfigWebFactory.getChildren(parent,"default-resource-provider");
        ResourceProvider[] providers = config.getResourceProviders();
        ResourceProvider defaultProvider = config.getDefaultResourceProvider();
		
        Query qry=new QueryImpl(new String[]{"support","scheme","caseSensitive","default","class","arguments"},elProviders.length+elDefaultProviders.length,"resourceproviders");
        int row=1;
        for(int i=0;i<elDefaultProviders.length;i++) {
      		getResourceProviders(new ResourceProvider[]{defaultProvider},qry,elDefaultProviders[i],row++,Boolean.TRUE);
  	    }
        for(int i=0;i<elProviders.length;i++) {
      		getResourceProviders(providers,qry,elProviders[i],row++,Boolean.FALSE);
  	    }
      	return qry;
    }
    
    private void getResourceProviders(ResourceProvider[] providers,Query qry,Element p, int row,Boolean def) throws PageException {
    	Array support=new ArrayImpl();
  	    String clazz=p.getAttribute("class");
  		qry.setAt("scheme",row,p.getAttribute("scheme"));
  		qry.setAt("arguments",row,p.getAttribute("arguments"));
  		qry.setAt("class",row,clazz);
  		for(int i=0;i<providers.length;i++) {
  			if(providers[i].getClass().getName().equals(clazz)){
  				if(providers[i].isAttributesSupported())support.append("attributes");
  	            if(providers[i].isModeSupported())support.append("mode");
  	            qry.setAt("support",row,List.arrayToList(support, ","));
  	            qry.setAt("scheme",row,providers[i].getScheme());
  	            qry.setAt("caseSensitive",row,Caster.toBoolean(providers[i].isCaseSensitive()));
  	            qry.setAt("default",row,def);
  				break;
  			}
  		}
	}


	/**
     * remove a DataSource Connection
     * @param name
     * @throws ExpressionException
     * @throws SecurityException 
     */
    public void removeDataSource(String name) throws ExpressionException, SecurityException {
    	checkWriteAccess();
        // check parameters
        if(name==null || name.length()==0)
            throw new ExpressionException("name for Datasource Connection can be a empty value");
        

        Element datasources=_getRootElement("data-sources");

        Element[] children = ConfigWebFactory.getChildren(datasources,"data-source");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("name"); 
  	    	if(n!=null && n.equalsIgnoreCase(name)) {
  	    	  datasources.removeChild(children[i]);
  			}
  	    }
    }

	public void removeCacheConnection(String name) throws ExpressionException, SecurityException {
		checkWriteAccess();
		
		boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_CACHE);
		if(!hasAccess)
            throw new SecurityException("no access to remove cache connection");
        
		
        // check parameters
        if(StringUtil.isEmpty(name))
            throw new ExpressionException("name for Cache Connection can be a empty value");
        
        Element parent=_getRootElement("cache");
        
        // remove default flag
        if(name.equalsIgnoreCase(parent.getAttribute("default-object")))
        	parent.removeAttribute("default-object");
        if(name.equalsIgnoreCase(parent.getAttribute("default-template")))
        	parent.removeAttribute("default-template");
        if(name.equalsIgnoreCase(parent.getAttribute("default-query")))
        	parent.removeAttribute("default-query");
        if(name.equalsIgnoreCase(parent.getAttribute("default-resource")))
        	parent.removeAttribute("default-resource");
        
        // remove element
        Element[] children = ConfigWebFactory.getChildren(parent,"connection");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("name"); 
  	    	if(n!=null && n.equalsIgnoreCase(name)) {
  	    		Map conns = config.getCacheConnections();
  	    		CacheConnection cc=(CacheConnection) conns.get(n);
  	    		if(cc!=null)Util.removeEL(config instanceof ConfigWeb?(ConfigWeb)config:null,cc);
  	    	  parent.removeChild(children[i]);
  			}
  	    }
      	
	}
	

	public void removeCacheGatewayEntry(String name) throws PageException {
		checkWriteAccess();
        
		boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_GATEWAY);
        if(!hasAccess)
            throw new SecurityException("no access to remove gateway entry");
        
        if(StringUtil.isEmpty(name))
            throw new ExpressionException("name for Gateway Id can be a empty value");
        
        Element parent=_getRootElement("gateways");
        
        // remove element
        Element[] children = ConfigWebFactory.getChildren(parent,"gateway");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("id"); 
  	    	if(n!=null && n.equalsIgnoreCase(name)) {
  	    		Map conns = ((ConfigWebImpl)config).getGatewayEngine().getEntries();
  	    		GatewayEntry ge=(GatewayEntry) conns.get(n);
  	    		if(ge!=null){
  	    			try {
  	    				((ConfigWebImpl)config).getGatewayEngine().remove(ge);
					} catch (GatewayException e) {
						throw Caster.toPageException(e);
					}
  	    		}
  	    		parent.removeChild(children[i]);
  			}
  	    }
	}

	
	
    public void removeRemoteClient(String url) throws ExpressionException, SecurityException {
    	checkWriteAccess();
    	
    	// SNSN
    	
    	
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_REMOTE);
        if(!hasAccess)
            throw new SecurityException("no access to remove remote client settings");
        
    	
    	
    	// check parameters
        if(StringUtil.isEmpty(url))
            throw new ExpressionException("url for Remote Client can be a empty value");
        

        Element clients=_getRootElement("remote-clients");

        Element[] children = ConfigWebFactory.getChildren(clients,"remote-client");
      	for(int i=0;i<children.length;i++) {
      	    String n=children[i].getAttribute("url"); 
  	    	if(n!=null && n.equalsIgnoreCase(url)) {
  	    	  clients.removeChild(children[i]);
  			}
  	    }
    }
    
    /**
     * 	update PSQ State
     * @param psq Preserver Single Quote
     * @throws SecurityException
     */
    public void updatePSQ(Boolean psq) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_DATASOURCE);
        
        if(!hasAccess) throw new SecurityException("no access to update datsource connections");
        
        Element datasources=_getRootElement("data-sources");
        datasources.setAttribute("psq",Caster.toString(psq,""));
        if(datasources.hasAttribute("preserve-single-quote"))
        	datasources.removeAttribute("preserve-single-quote");
    }


	public void updateInspectTemplate(String str) throws SecurityException {
		checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update");
        
        Element datasources=_getRootElement("java");
        datasources.setAttribute("inspect-template",str);

	}
    
    
    /**
     * sets the scope cascading type
     * @param type (ServletConfigImpl.SCOPE_XYZ)
     * @throws SecurityException
     */ 
    public void updateScopeCascadingType(String type) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        
        Element scope=_getRootElement("scope");
        if(type.equalsIgnoreCase("strict"))        scope.setAttribute("cascading","strict");
        else if(type.equalsIgnoreCase("small"))    scope.setAttribute("cascading","small");
        else if(type.equalsIgnoreCase("standard")) scope.setAttribute("cascading","standard");
        else                                       scope.setAttribute("cascading","standard");
        
    }
    
    /**
     * sets the scope cascading type
     * @param type (ServletConfigImpl.SCOPE_XYZ)
     * @throws SecurityException
     */ 
    public void updateScopeCascadingType(short type) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update scope setting");
        
        //railo.print.ln("********........type:"+type);
        Element scope=_getRootElement("scope");
        if(type==ConfigWeb.SCOPE_STRICT) scope.setAttribute("cascading","strict");
        else if(type==ConfigWeb.SCOPE_SMALL) scope.setAttribute("cascading","small");
        else if(type==ConfigWeb.SCOPE_STANDARD) scope.setAttribute("cascading","standard");
        
    }
    
    /**
     * sets if allowed implicid query call
     * @param allow
     * @throws SecurityException
     */
    public void updateAllowImplicidQueryCall(Boolean allow) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        scope.setAttribute("cascade-to-resultset",Caster.toString(allow,""));
        
    }
    
    public void updateMergeFormAndUrl(Boolean merge) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        scope.setAttribute("merge-url-form",Caster.toString(merge,""));
        
    }
    
    /**
     * updates request timeout value
     * @param span
     * @throws SecurityException
     */
    public void updateRequestTimeout(TimeSpan span) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        
        Element application=_getRootElement("application");
        if(span!=null){
        application.setAttribute("requesttimeout",span.getDay()+","+span.getHour()+","+span.getMinute()+","+span.getSecond());
        }
        else application.removeAttribute("requesttimeout");
        
        // remove deprecated attribute
        if(scope.hasAttribute("requesttimeout"))
        	scope.removeAttribute("requesttimeout");
    }
    
    /**
     * updates session timeout value
     * @param span
     * @throws SecurityException
     */
    public void updateSessionTimeout(TimeSpan span) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        if(span!=null)scope.setAttribute("sessiontimeout",span.getDay()+","+span.getHour()+","+span.getMinute()+","+span.getSecond());
        else scope.removeAttribute("sessiontimeout");
    }
    
    /**
     * updates session timeout value
     * @param span
     * @throws SecurityException
     */
    public void updateClientTimeout(TimeSpan span) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        if(span!=null)scope.setAttribute("clienttimeout",span.getDay()+","+span.getHour()+","+span.getMinute()+","+span.getSecond());
        else scope.removeAttribute("clienttimeout");
        
        // deprecated
        if(scope.hasAttribute("client-max-age"))scope.removeAttribute("client-max-age");
       
        
    }
    
    

    public void updateSuppressWhitespace(Boolean value) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("setting");
        scope.setAttribute("suppress-whitespace",Caster.toString(value,""));
    }
    public void updateSuppressContent(Boolean value) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("setting");
        scope.setAttribute("suppress-content",Caster.toString(value,""));
    }
    
    public void updateShowVersion(Boolean value) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("setting");
        scope.setAttribute("show-version",Caster.toString(value,""));
    }
    
    public void updateAllowCompression(Boolean value) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("setting");
        scope.setAttribute("allow-compression",Caster.toString(value,""));
    }
    
    public void updateContentLength(Boolean value) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("setting");
        scope.setAttribute("content-length",Caster.toString(value,""));
    }
    
    /**
     * updates request timeout value
     * @param span
     * @throws SecurityException
     */
    public void updateApplicationTimeout(TimeSpan span) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        if(span!=null)scope.setAttribute("applicationtimeout",span.getDay()+","+span.getHour()+","+span.getMinute()+","+span.getSecond());
        else scope.removeAttribute("applicationtimeout");
    }

    public void updateApplicationListener(String type,String mode) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update listener type");
        
        Element scope=_getRootElement("application");
        scope.setAttribute("listener-type",type.toLowerCase().trim());
        scope.setAttribute("listener-mode",mode.toLowerCase().trim());
    }
    
    public void updateProxy(boolean enabled,String server, int port, String username, String password) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess) throw new SecurityException("no access to update listener type");
        
        Element proxy=_getRootElement("proxy");
        proxy.setAttribute("enabled",Caster.toString(enabled));
        if(!StringUtil.isEmpty(server))		proxy.setAttribute("server",server);
        if(port>0)							proxy.setAttribute("port",Caster.toString(port));
        if(!StringUtil.isEmpty(username))	proxy.setAttribute("username",username);
        if(!StringUtil.isEmpty(password))	proxy.setAttribute("password",password);
    }
    
    /*public void removeProxy() throws SecurityException {
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess) throw new SecurityException("no access to remove proxy settings");
        
        Element proxy=_getRootElement("proxy");
        proxy.removeAttribute("server");
        proxy.removeAttribute("port");
        proxy.removeAttribute("username");
        proxy.removeAttribute("password");
    }*/
    
    /**
     * enable or desable session management
     * @param sessionManagement
     * @throws SecurityException
     */
    public void updateSessionManagement(Boolean sessionManagement) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess)
            throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        scope.setAttribute("sessionmanagement",Caster.toString(sessionManagement,""));
    }
    
    /**
     * enable or desable client management
     * @param clientManagement
     * @throws SecurityException
     */
    public void updateClientManagement(Boolean clientManagement) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        
        if(!hasAccess)
            throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        scope.setAttribute("clientmanagement",Caster.toString(clientManagement,""));
    }
    
    /**
     * set if client cookies are enabled or not
     * @param clientCookies
     * @throws SecurityException
     */
    public void updateClientCookies(Boolean clientCookies) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        scope.setAttribute("setclientcookies",Caster.toString(clientCookies,""));
    }
    
    /**
     * set if domain cookies are enabled or not
     * @param domainCookies
     * @throws SecurityException
     */
    public void updateDomaincookies(Boolean domainCookies) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update scope setting");
        
        Element scope=_getRootElement("scope");
        scope.setAttribute("setdomaincookies",Caster.toString(domainCookies,""));
    }
    
    /**
     * update the locale
     * @param locale
     * @throws SecurityException
     */
    public void updateLocale(String locale) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update regional setting");
        
        Element scope=_getRootElement("regional");
        scope.setAttribute("locale",locale.trim());
    }
    
    public void updateScriptProtect(String strScriptProtect) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update script protect");
        
        Element scope=_getRootElement("application");
        scope.setAttribute("script-protect",strScriptProtect.trim());
    }
    
    public void updateAllowURLRequestTimeout(Boolean allowURLRequestTimeout) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update AllowURLRequestTimeout");
        
        Element scope=_getRootElement("application");
        scope.setAttribute("allow-url-requesttimeout",Caster.toString(allowURLRequestTimeout,""));
    }
    
    
    public void updateScriptProtect(int scriptProtect) throws SecurityException { 
    	updateScriptProtect(AppListenerUtil.translateScriptProtect(scriptProtect));
    }
    
    /**
     * update the timeZone
     * @param timeZone
     * @throws SecurityException
     */
    public void updateTimeZone(String timeZone) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update regional setting");
        
        Element regional=_getRootElement("regional");
        regional.setAttribute("timezone",timeZone.trim());
        
    }

    /**
     * update the timeServer
     * @param timeServer
     * @param useTimeServer 
     * @throws PageException 
     */
    public void updateTimeServer(String timeServer, Boolean useTimeServer) throws PageException {
    	checkWriteAccess();
       if(useTimeServer!=null && useTimeServer.booleanValue() && !StringUtil.isEmpty(timeServer,true)) {
            try {
                new NtpClient(timeServer).getOffset();
            } catch (IOException e) {
                throw new ExpressionException("invalid timeserver (NTP) ["+timeServer+"] ");
            }
       }
        
       boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
       if(!hasAccess)
            throw new SecurityException("no access to update regional setting");
        
        Element scope=_getRootElement("regional");
        scope.setAttribute("timeserver",timeServer.trim());
        if(useTimeServer!=null)scope.setAttribute("use-timeserver",Caster.toString(useTimeServer));
        else scope.removeAttribute("use-timeserver");
    }
    
    /**
     * update the baseComponent
     * @param baseComponent
     * @throws SecurityException
     */
    public void updateBaseComponent(String baseComponent) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update component setting");
        //config.resetBaseComponentPage();
        Element scope=_getRootElement("component");
        //if(baseComponent.trim().length()>0)
        	scope.setAttribute("base",baseComponent);
    }
    

    public void updateComponentDefaultImport(String componentDefaultImport) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update component setting");
        //config.resetBaseComponentPage();
        Element scope=_getRootElement("component");
        //if(baseComponent.trim().length()>0)
        	scope.setAttribute("component-default-import",componentDefaultImport);
    }
    
    
    
    
    /**
     * update the Component Data Member default access type
     * @param access
     * @throws SecurityException
     * @throws ExpressionException 
     */
    public void updateComponentDataMemberDefaultAccess(String strAccess) throws SecurityException, ExpressionException {
    	checkWriteAccess(); 
        
    	
    	boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update component setting");
        
        Element scope=_getRootElement("component");

        if(StringUtil.isEmpty(strAccess)){
        	scope.setAttribute("data-member-default-access","");
        }
        else{
	        scope.setAttribute("data-member-default-access",ComponentUtil.toStringAccess(ComponentUtil.toIntAccess(strAccess)));
        }
    } 
    
    /**
     * update the Component Data Member default access type
     * @param accessType
     * @throws SecurityException
     */
    public void updateTriggerDataMember(Boolean triggerDataMember) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update trigger-data-member");
        
        Element scope=_getRootElement("component");
        scope.setAttribute("trigger-data-member",Caster.toString(triggerDataMember,""));
    }

	public void updateComponentUseShadow(Boolean useShadow) throws SecurityException {
    	checkWriteAccess();
		boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
		if(!hasAccess)
            throw new SecurityException("no access to update use-shadow");
        
        Element scope=_getRootElement("component");
        scope.setAttribute("use-shadow",Caster.toString(useShadow,""));
	}

	public void updateComponentLocalSearch(Boolean componentLocalSearch) throws SecurityException {
    	checkWriteAccess();
		boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
		if(!hasAccess)
            throw new SecurityException("no access to update component Local Search");
        
        Element scope=_getRootElement("component");
        scope.setAttribute("local-search",Caster.toString(componentLocalSearch,""));
	}
	
	public void updateComponentPathCache(Boolean componentPathCache) throws SecurityException {
		checkWriteAccess();
		boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
		if(!hasAccess)
            throw new SecurityException("no access to update component Cache Path");
        
        Element scope=_getRootElement("component");
        if(!Caster.toBooleanValue(componentPathCache,false))
        	config.clearComponentCache();
        scope.setAttribute("use-cache-path",Caster.toString(componentPathCache,""));
	}
	public void updateCTPathCache(Boolean ctPathCache) throws SecurityException {
		checkWriteAccess();
		if(!ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_CUSTOM_TAG)) 
			throw new SecurityException("no access to update custom tag setting");
		
		 if(!Caster.toBooleanValue(ctPathCache,false))
	        	config.clearCTCache();
	        Element scope=_getRootElement("custom-tag");
        scope.setAttribute("use-cache-path",Caster.toString(ctPathCache,""));
	}

	
	
	
    /**
     * updates if debugging or not
     * @param debug if value is null server setting is used
     * @throws SecurityException
     */
    public void updateDebug(Boolean debug) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_DEBUGGING);
        if(!hasAccess)
            throw new SecurityException("no access to change debugging settings");
        Element debugging=_getRootElement("debugging");
        if(debug!=null)debugging.setAttribute("debug",Caster.toString(debug.booleanValue()));
        else debugging.removeAttribute("debug");
    }

    /**
     * updates the DebugTemplate
     * @param template
     * @throws SecurityException
     */
    public void updateDebugTemplate(String template) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_DEBUGGING);
        if(!hasAccess)
            throw new SecurityException("no access to change debugging settings");

        Element debugging=_getRootElement("debugging");
        //if(template.trim().length()>0)
        	debugging.setAttribute("template",template);
    }
    /**
     * updates the ErrorTemplate
     * @param template
     * @throws SecurityException
     */
    public void updateErrorTemplate(int statusCode,String template) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_DEBUGGING);
        if(!hasAccess)
            throw new SecurityException("no access to change error settings");

        Element error=_getRootElement("error");
        //if(template.trim().length()>0)
        	error.setAttribute("template-"+statusCode,template);
    }
    

    public void updateErrorStatusCode(Boolean doStatusCode) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_DEBUGGING);
        if(!hasAccess)
            throw new SecurityException("no access to change error settings");

        Element error=_getRootElement("error");
        error.setAttribute("status-code",Caster.toString(doStatusCode,""));
    }

    /**
     * updates the DebugTemplate
     * @param template
     * @throws SecurityException
     */
    public void updateComponentDumpTemplate(String template) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess)
            throw new SecurityException("no access to update component setting");
        
        Element component=_getRootElement("component");
        //if(template.trim().length()>0)
        	component.setAttribute("dump-template",template);
    }
    
    /* *
     * updates the if memory usage will be logged or not
     * @param logMemoryUsage
     * @throws SecurityException
     * /
    public void updateLogMemoryUsage(boolean logMemoryUsage) throws SecurityException {
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_DEBUGGING);
        if(!hasAccess)
            throw new SecurityException("no access to change debugging settings");
        
        Element debugging=_getRootElement("debugging");
        debugging.setAttribute("log-memory-usage",Caster.toString(logMemoryUsage));
    }*/
    
    /* *
     * updates the Memory Logger
     * @param memoryLogger
     * @throws SecurityException
     * /
    public void updateMemoryLogger(String memoryLogger) throws SecurityException {
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_DEBUGGING);
        if(!hasAccess)
            throw new SecurityException("no access to change debugging settings");
        
        Element debugging=_getRootElement("debugging");
        if(memoryLogger.trim().length()>0)debugging.setAttribute("memory-log",memoryLogger);
    }*/

    private Element _getRootElement(String name) {
        Element el=ConfigWebFactory.getChildByName(doc.getDocumentElement(),name);
        if(el==null) {
            el=doc.createElement(name);
            doc.getDocumentElement().appendChild(el);
        }
        return el;
    }

    private Element _getRootElement(String name, boolean insertBefore,boolean doNotCreate) {
        return ConfigWebFactory.getChildByName(doc.getDocumentElement(),name,insertBefore,doNotCreate);
    }
    
    /**
     * @param setting
     * @param file
     * @param directJavaAccess
     * @param mail
     * @param datasource
     * @param mapping
     * @param customTag
     * @param cfxSetting
     * @param cfxUsage
     * @param debugging
     * @param search 
     * @param scheduledTasks 
     * @param tagExecute
     * @param tagImport
     * @param tagObject
     * @param tagRegistry
     * @throws SecurityException
     */
    public void updateDefaultSecurity(short setting, short file,Resource[] fileAccess,short directJavaAccess,
 	       short mail, short datasource, short mapping, short remote, short customTag,
 	      short cfxSetting, short cfxUsage, short debugging,
         short search, short scheduledTasks,
         short tagExecute,short tagImport, short tagObject, short tagRegistry,
         short cache, short gateway,short orm,
         short accessRead, short accessWrite) throws SecurityException {
    	checkWriteAccess();
        if(!(config instanceof ConfigServer))
            throw new SecurityException("can't change security settings from this context");
        
        Element security=_getRootElement("security");
        updateSecurityFileAccess(security,fileAccess,file);
        security.setAttribute("setting",            SecurityManagerImpl.toStringAccessValue(setting));
        security.setAttribute("file",               SecurityManagerImpl.toStringAccessValue(file));
        security.setAttribute("direct_java_access", SecurityManagerImpl.toStringAccessValue(directJavaAccess));
        security.setAttribute("mail",               SecurityManagerImpl.toStringAccessValue(mail));
        security.setAttribute("datasource",         SecurityManagerImpl.toStringAccessValue(datasource));
        security.setAttribute("mapping",            SecurityManagerImpl.toStringAccessValue(mapping));
        security.setAttribute("remote",            	SecurityManagerImpl.toStringAccessValue(remote));
        security.setAttribute("custom_tag",         SecurityManagerImpl.toStringAccessValue(customTag));
        security.setAttribute("cfx_setting",        SecurityManagerImpl.toStringAccessValue(cfxSetting));
        security.setAttribute("cfx_usage",          SecurityManagerImpl.toStringAccessValue(cfxUsage));
        security.setAttribute("debugging",          SecurityManagerImpl.toStringAccessValue(debugging));
        security.setAttribute("search",             SecurityManagerImpl.toStringAccessValue(search));
        security.setAttribute("scheduled_task",     SecurityManagerImpl.toStringAccessValue(scheduledTasks));

        security.setAttribute("tag_execute",        SecurityManagerImpl.toStringAccessValue(tagExecute));
        security.setAttribute("tag_import",         SecurityManagerImpl.toStringAccessValue(tagImport));
        security.setAttribute("tag_object",         SecurityManagerImpl.toStringAccessValue(tagObject));
        security.setAttribute("tag_registry",         SecurityManagerImpl.toStringAccessValue(tagRegistry));
        security.setAttribute("cache",       SecurityManagerImpl.toStringAccessValue(cache));
        security.setAttribute("gateway",       SecurityManagerImpl.toStringAccessValue(gateway));
        security.setAttribute("orm",       SecurityManagerImpl.toStringAccessValue(orm));

        security.setAttribute("access_read",       SecurityManagerImpl.toStringAccessRWValue(accessRead));
        security.setAttribute("access_write",      SecurityManagerImpl.toStringAccessRWValue(accessWrite));
    
    
    
    }
    
    private void removeSecurityFileAccess(Element parent) {
    	Element[] children = ConfigWebFactory.getChildren(parent,"file-access");
      	
    	// remove existing
    	if(!ArrayUtil.isEmpty(children)){
    		for(int i=children.length-1;i>=0;i--){
	    		parent.removeChild(children[i]);
	    	}
    	}
    }
    
    private void updateSecurityFileAccess(Element parent,Resource[] fileAccess, short file) {
    	removeSecurityFileAccess(parent);
    	
    	// insert
    	if(!ArrayUtil.isEmpty(fileAccess) && file!=SecurityManager.VALUE_ALL){
    		Element fa;
	    	for(int i=0;i<fileAccess.length;i++){
	    		fa=doc.createElement("file-access");
	    		fa.setAttribute("path", fileAccess[i].getAbsolutePath());
	    		parent.appendChild(fa);
	    	}
    	}
    	
	}


	/**
     * update a security manager that match the given id
     * @param id
     * @param setting
     * @param file
     * @param file_access 
     * @param directJavaAccess
     * @param mail
     * @param datasource
     * @param mapping
     * @param customTag
     * @param cfxSetting
     * @param cfxUsage
     * @param debugging
     * @param search 
     * @param scheduledTasks 
     * @param tagExecute
     * @param tagImport
     * @param tagObject
     * @param tagRegistry
     * @throws SecurityException
     * @throws ApplicationException
     */
    public void updateSecurity(String id, short setting, short file,Resource[] fileAccess, short directJavaAccess,
           short mail, short datasource, short mapping, short remote, short customTag,
          short cfxSetting, short cfxUsage, short debugging,
          short search, short scheduledTasks,
          short tagExecute,short tagImport, short tagObject, short tagRegistry, 
          short cache,short gateway,short orm,
          short accessRead, short accessWrite) throws SecurityException, ApplicationException {
    	checkWriteAccess();
        if(!(config instanceof ConfigServer))
            throw new SecurityException("can't change security settings from this context");
        
        Element security=_getRootElement("security");
        Element[] children = ConfigWebFactory.getChildren(security,"accessor");
        Element accessor=null;
        for(int i=0;i<children.length;i++) {
            if(id.equals(children[i].getAttribute("id"))) {
                accessor=children[i];
            }
        }
        if(accessor==null) throw new ApplicationException("there is noc Security Manager for id ["+id+"]");
        updateSecurityFileAccess(accessor,fileAccess,file);
        
        accessor.setAttribute("setting",            SecurityManagerImpl.toStringAccessValue(setting));
        accessor.setAttribute("file",               SecurityManagerImpl.toStringAccessValue(file));
        accessor.setAttribute("direct_java_access", SecurityManagerImpl.toStringAccessValue(directJavaAccess));
        accessor.setAttribute("mail",               SecurityManagerImpl.toStringAccessValue(mail));
        accessor.setAttribute("datasource",         SecurityManagerImpl.toStringAccessValue(datasource));
        accessor.setAttribute("mapping",            SecurityManagerImpl.toStringAccessValue(mapping));
        accessor.setAttribute("remote",            	SecurityManagerImpl.toStringAccessValue(remote));
        accessor.setAttribute("custom_tag",         SecurityManagerImpl.toStringAccessValue(customTag));
        accessor.setAttribute("cfx_setting",        SecurityManagerImpl.toStringAccessValue(cfxSetting));
        accessor.setAttribute("cfx_usage",          SecurityManagerImpl.toStringAccessValue(cfxUsage));
        accessor.setAttribute("debugging",          SecurityManagerImpl.toStringAccessValue(debugging));
        accessor.setAttribute("search",             SecurityManagerImpl.toStringAccessValue(search));
        accessor.setAttribute("scheduled_task",     SecurityManagerImpl.toStringAccessValue(scheduledTasks));
        accessor.setAttribute("cache",     SecurityManagerImpl.toStringAccessValue(cache));
        accessor.setAttribute("gateway",     SecurityManagerImpl.toStringAccessValue(gateway));
        accessor.setAttribute("orm",     SecurityManagerImpl.toStringAccessValue(orm));
        
        accessor.setAttribute("tag_execute",        SecurityManagerImpl.toStringAccessValue(tagExecute));
        accessor.setAttribute("tag_import",         SecurityManagerImpl.toStringAccessValue(tagImport));
        accessor.setAttribute("tag_object",         SecurityManagerImpl.toStringAccessValue(tagObject));
        accessor.setAttribute("tag_registry",       SecurityManagerImpl.toStringAccessValue(tagRegistry));

        accessor.setAttribute("access_read",       SecurityManagerImpl.toStringAccessRWValue(accessRead));
        accessor.setAttribute("access_write",      SecurityManagerImpl.toStringAccessRWValue(accessWrite));
    }
    
    
    
    
    /**
     * @return returns the default password
     * @throws SecurityException
     */
    public String getDefaultPassword() throws SecurityException {
    	checkReadAccess();
        if(config instanceof ConfigServerImpl) {
            return ((ConfigServerImpl)config).getDefaultPassword();
        }
        throw new SecurityException("can't access default password within this context");
    }
    /**
     * @param password
     * @throws SecurityException 
     */
    public void updateDefaultPassword(String password) throws SecurityException {
    	checkWriteAccess();
        Element root=doc.getDocumentElement();
        root.setAttribute("default-password",new BlowfishEasy("tpwisgh").encryptString(password));
        ((ConfigServerImpl)config).setDefaultPassword(password);
    }

	public void removeDefaultPassword() throws SecurityException {
		checkWriteAccess();
        Element root=doc.getDocumentElement();
        root.removeAttribute("default-password");
        ((ConfigServerImpl)config).setDefaultPassword(null);
	}
    
    /**
     * session type update
     * @param type
     * @throws SecurityException
     */
    public void updateSessionType(String type) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        type=type.toLowerCase().trim();
        
        Element scope=_getRootElement("scope");
        scope.setAttribute("session-type",type);
    }
    
    public void updateLocalMode(String mode) throws SecurityException {
    	checkWriteAccess();
        boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_SETTING);
        if(!hasAccess) throw new SecurityException("no access to update scope setting");
        
        mode=mode.toLowerCase().trim();
        Element scope=_getRootElement("scope");
        scope.setAttribute("local-mode",mode);
    }


    /**
     * updates update settingd for railo
     * @param type
     * @param location
     * @throws SecurityException
     */
    public void updateUpdate(String type, String location) throws SecurityException {
    	checkWriteAccess();
        
        if(!(config instanceof ConfigServer)) {
            throw new SecurityException("can't change update setting from this context, access is denied");
        }
        Element update=_getRootElement("update");
        update.setAttribute("type",type);
        try {
			location=HTTPUtil.toURL(location).toString();
		} 
        catch (Throwable e) {}
        update.setAttribute("location",location);
    }
    
    /**
     * creates a individual security manager based on the default security manager
     * @param id
     * @throws DOMException 
     * @throws SecurityException 
     */
    public void createSecurityManager(String id) throws SecurityException, DOMException {
    	checkWriteAccess();
        SecurityManagerImpl dsm = (SecurityManagerImpl) config.getConfigServerImpl().getDefaultSecurityManager().cloneSecurityManager();
        config.getConfigServerImpl().setSecurityManager(id,dsm);
        
        Element security=_getRootElement("security");
        Element accessor=null;
        
        Element[] children = ConfigWebFactory.getChildren(security,"accessor");
        for(int i=0;i<children.length;i++) {
            if(id.equals(children[i].getAttribute("id"))) {
                accessor=children[i];             
            }
        }
        if(accessor==null) {
            accessor=doc.createElement("accessor");
            security.appendChild(accessor);
        }
        
        updateSecurityFileAccess(accessor,dsm.getCustomFileAccess(),dsm.getAccess(SecurityManager.TYPE_FILE));
        
        
        accessor.setAttribute("id",id);
        accessor.setAttribute("setting",            SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_SETTING)));
        accessor.setAttribute("file",               SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_FILE)));
        accessor.setAttribute("direct_java_access", SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_DIRECT_JAVA_ACCESS)));
        accessor.setAttribute("mail",               SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_MAIL)));
        accessor.setAttribute("datasource",         SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_DATASOURCE)));
        accessor.setAttribute("mapping",            SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_MAPPING)));
        accessor.setAttribute("custom_tag",         SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_CUSTOM_TAG)));
        accessor.setAttribute("cfx_setting",        SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_CFX_SETTING)));
        accessor.setAttribute("cfx_usage",          SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_CFX_USAGE)));
        accessor.setAttribute("debugging",          SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_DEBUGGING)));
        accessor.setAttribute("cache",          	SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManagerImpl.TYPE_CACHE)));
        accessor.setAttribute("gateway",          	SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManagerImpl.TYPE_GATEWAY)));
        accessor.setAttribute("orm",          		SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManagerImpl.TYPE_ORM)));

        accessor.setAttribute("tag_execute",        SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_TAG_EXECUTE)));
        accessor.setAttribute("tag_import",         SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_TAG_IMPORT)));
        accessor.setAttribute("tag_object",         SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_TAG_OBJECT)));
        accessor.setAttribute("tag_registry",       SecurityManagerImpl.toStringAccessValue(dsm.getAccess(SecurityManager.TYPE_TAG_REGISTRY)));
        
    }


    /**
     * remove security manager matching given id
     * @param id
     * @throws SecurityException 
     */
    public void removeSecurityManager(String id) throws SecurityException {
    	checkWriteAccess();
        config.getConfigServerImpl().removeSecurityManager(id);
        
        Element security=_getRootElement("security");
       
        
        Element[] children = ConfigWebFactory.getChildren(security,"accessor");
        for(int i=0;i<children.length;i++) {
            if(id.equals(children[i].getAttribute("id"))) {
                security.removeChild(children[i]);
            }
        }
        
    }

    /**
     * run update from cfml engine
     * @throws PageException
     */
    public void runUpdate() throws PageException {
    	checkWriteAccess();
        ConfigServerImpl cs = config.getConfigServerImpl();
        CFMLEngineFactory factory = cs.getCFMLEngine().getCFMLEngineFactory();
        synchronized(factory){
	        try {
	            factory.update(cs.getPassword());
	        } 
	        catch (Exception e) {
	            throw Caster.toPageException(e);
	        }
        }
        
    }
    
    /**
     * run update from cfml engine
     * @throws PageException
     */
    public void removeLatestUpdate() throws PageException {
    	_removeUpdate(true);
    }
    
    public void removeUpdate() throws PageException {
    	_removeUpdate(false);
    }
    
    private void _removeUpdate(boolean onlyLatest) throws PageException {
    	checkWriteAccess();
    	ConfigServerImpl cs = config.getConfigServerImpl();
        try {
        	CFMLEngineFactory factory = cs.getCFMLEngine().getCFMLEngineFactory();
        	
        	if(onlyLatest){
        		// FUTURE make direct call (see below)
        		//factory.removeLatestUpdate(cs.getPassword());
        		try{
        			Method removeLatestUpdate = factory.getClass().getMethod("removeLatestUpdate", new Class[]{String.class});
        			removeLatestUpdate.invoke(factory, new Object[]{cs.getPassword()});
        		}
        		catch(NoSuchMethodException e)	{
        			removeLatestUpdate(factory,cs.getPassword());
        			//throw new SecurityException("this feature is not supported by your version, you have to update your railo.jar first");
        		}
        		catch(Throwable t){
        			throw Caster.toPageException(t);
        		}
        	}
        	else factory.removeUpdate(cs.getPassword());
        	
        	
        } 
        catch (Exception e) {
            throw Caster.toPageException(e);
        }
    }
    
    // FUTURE remove this
	private String getCoreExtension() throws ServletException {
    	URL res = new TP().getClass().getResource("/core/core.rcs");
        if(res!=null) return "rcs";
        
        res = new TP().getClass().getResource("/core/core.rc");
        if(res!=null) return "rc";
        
        throw new ServletException("missing core file");
	}
	
	// FUTURE remove this
	private boolean isNewerThan(int left, int right) {
        return left>right;
    }
	
    // FUTURE remove this
    private boolean removeLatestUpdate(CFMLEngineFactory factory, String password) throws IOException, ServletException {
    	File patchDir = new File(factory.getResourceRoot(),"patches");
        if(!patchDir.exists())patchDir.mkdirs();
        
    	File[] patches=patchDir.listFiles(new ExtensionFilter(new String[]{"."+getCoreExtension()}));
        File patch=null;
        for(int i=0;i<patches.length;i++) {
        	 if(patch==null || isNewerThan(railo.loader.util.Util.toInVersion(patches[i].getName()),railo.loader.util.Util.toInVersion(patch.getName()))) {
                 patch=patches[i];
             }
        }
    	if(patch!=null && !patch.delete())patch.deleteOnExit();
        factory.restart(password);
        return true;
    }
    
    
    /*private Resource getPatchDirectory(CFMLEngine engine) throws IOException {
    	//File f=engine.getCFMLEngineFactory().getResourceRoot();
    	Resource res = ResourcesImpl.getFileResourceProvider().getResource(engine.getCFMLEngineFactory().getResourceRoot().getAbsolutePath());
    	Resource pd = res.getRealResource("patches");
        if(!pd.exists())pd.mkdirs();
        return pd;
    }*/
    
    
    
    
    /**
     * run update from cfml engine
     * @throws PageException
     */
    public void restart() throws PageException {
    	checkWriteAccess();
        ConfigServerImpl cs = config.getConfigServerImpl();
        CFMLEngineFactory factory = cs.getCFMLEngine().getCFMLEngineFactory();
        synchronized(factory){
	        try {
	            factory.restart(cs.getPassword());
	        } 
	        catch (Exception e) {
	            throw Caster.toPageException(e);
	        }
        }
    }

	public void updateWebCharset(String charset) throws PageException {
    	checkWriteAccess();
		
    	if(StringUtil.isEmpty(charset)){
			if(config instanceof ConfigWeb)charset=config.getConfigServerImpl().getWebCharset();
			else charset="UTF-8";
		}
    	
    	charset=checkCharset(charset);
		// update charset
		Element element = _getRootElement("charset");
		element.setAttribute("web-charset", charset.trim());
		
		element = _getRootElement("regional");
		element.removeAttribute("default-encoding");// remove deprecated attribute
		
	}

	public void updateResourceCharset(String charset) throws PageException {
    	checkWriteAccess();
    	if(StringUtil.isEmpty(charset)){
			if(config instanceof ConfigWeb)charset=config.getConfigServerImpl().getResourceCharset();
			else charset=SystemUtil.getCharset();
		}
    	charset=checkCharset(charset);
		// update charset
		Element element = _getRootElement("charset");
		element.setAttribute("resource-charset", charset.trim());
		
	}

	public void updateTemplateCharset(String charset) throws PageException {
    	checkWriteAccess();
    	if(StringUtil.isEmpty(charset)){
			if(config instanceof ConfigWeb)charset=config.getConfigServerImpl().getTemplateCharset();
			else charset=SystemUtil.getCharset();
		}
    	charset=checkCharset(charset);
    	
    	
    	
		// update charset
		Element element = _getRootElement("charset");
		element.setAttribute("template-charset", charset.trim());
		
	}
	
	private String checkCharset(String charset)  throws PageException{
		if("system".equalsIgnoreCase(charset))
			charset=SystemUtil.getCharset();
		else if("jre".equalsIgnoreCase(charset))
			charset=SystemUtil.getCharset();
		else if("os".equalsIgnoreCase(charset))
			charset=SystemUtil.getCharset();
		
		// check access
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);
		if(!hasAccess) {
			throw new SecurityException("no access to update regional setting");
		}
		
		// check encoding
		try {
			IOUtil.checkEncoding(charset);
		}
		catch (IOException e) {throw Caster.toPageException(e);}
		return charset;
	}


	private Resource getStoragDir(Config config) {
    	Resource storageDir = config.getConfigDir().getRealResource("storage");
		if(!storageDir.exists())storageDir.mkdirs();
		return storageDir;
	}
	
	public void storageSet(Config config,String key, Object value) throws ConverterException, IOException, SecurityException {
    	checkWriteAccess();
		Resource storageDir = getStoragDir(config);
		Resource storage=storageDir.getRealResource(key+".wddx");
		
		WDDXConverter converter =new WDDXConverter(config.getTimeZone(),true,true);
		String wddx=converter.serialize(value);
		IOUtil.write(storage, wddx, "UTF-8", false);
	}


	public Object storageGet(Config config, String key) throws ConverterException, IOException, SecurityException {
    	checkReadAccess();
		Resource storageDir = getStoragDir(config);
		Resource storage=storageDir.getRealResource(key+".wddx");
		if(!storage.exists()) throw new IOException("there is no storage with name "+key);
		WDDXConverter converter =new WDDXConverter(config.getTimeZone(),true,true);
		return converter.deserialize(IOUtil.toString(storage,"UTF-8"), true);
	}


	public void updateCustomTagDeepSearch(boolean customTagDeepSearch) throws SecurityException {
		checkWriteAccess();
		if(!ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_CUSTOM_TAG)) 
			throw new SecurityException("no access to update custom tag setting");
		
		Element element = _getRootElement("custom-tag");
		element.setAttribute("custom-tag-deep-search",Caster.toString(customTagDeepSearch));
	}

	public void resetId() throws PageException {
		checkWriteAccess();
		Resource res=config.getConfigDir().getRealResource("id");
		try {
			if(res.exists())res.remove(false);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
    	
	}
	
	public void updateCustomTagLocalSearch(boolean customTagLocalSearch) throws SecurityException {
		checkWriteAccess();
		if(!ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_CUSTOM_TAG)) 
			throw new SecurityException("no access to update custom tag setting");
		
		Element element = _getRootElement("custom-tag");
		element.setAttribute("custom-tag-local-search",Caster.toString(customTagLocalSearch));
	}
	


	public void updateCustomTagExtensions(String extensions) throws PageException {
		checkWriteAccess();
		if(!ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_CUSTOM_TAG)) 
			throw new SecurityException("no access to update custom tag setting");
		
		// check
		Array arr = List.listToArrayRemoveEmpty(extensions, ',');
		List.trimItems(arr);
		//throw new ApplicationException("you must define at least one extension");
		
		
		// update charset
		Element element = _getRootElement("custom-tag");
		element.setAttribute("extensions",List.arrayToList(arr, ","));
	}


	public void updateRemoteClient(String label,String url, String type,
			String securityKey, String usage, String adminPassword, String serverUsername, String serverPassword,
			String proxyServer, String proxyUsername, String proxyPassword, String proxyPort) throws PageException {
		checkWriteAccess();
		
		// SNSN
		
		boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_REMOTE);
        if(!hasAccess)
            throw new SecurityException("no access to update remote client settings");
        
        
        Element clients=_getRootElement("remote-clients");
        
        if(StringUtil.isEmpty(url)) throw new ExpressionException("url can be a empty value");
        if(StringUtil.isEmpty(securityKey)) throw new ExpressionException("securityKey can be a empty value");
        if(StringUtil.isEmpty(adminPassword)) throw new ExpressionException("adminPassword can be a empty value");
        url=url.trim();
        securityKey=securityKey.trim();
        adminPassword=adminPassword.trim();
        
        Element[] children = ConfigWebFactory.getChildren(clients,"remote-client");
      	
        // Update
        for(int i=0;i<children.length;i++) {
      	    Element el=children[i];
      	    String _url=el.getAttribute("url");
  			if(_url!=null && _url.equalsIgnoreCase(url)) {
  				el.setAttribute("label",label);
  				el.setAttribute("type",type);
	      		el.setAttribute("usage",usage);
	      		el.setAttribute("server-username",serverUsername);
	      		el.setAttribute("proxy-server",proxyServer);
	      		el.setAttribute("proxy-username",proxyUsername);
	      		el.setAttribute("proxy-port",proxyPort);
	      		el.setAttribute("security-key",ConfigWebFactory.encrypt(securityKey));
	      		el.setAttribute("admin-password",ConfigWebFactory.encrypt(adminPassword));
	      		el.setAttribute("server-password",ConfigWebFactory.encrypt(serverPassword));
	      		el.setAttribute("proxy-password",ConfigWebFactory.encrypt(proxyPassword));
	      		return ;
  			}
      	}
        
        // Insert
      	Element el = doc.createElement("remote-client");

      	el.setAttribute("label",label);
		el.setAttribute("url",url);
  		el.setAttribute("type",type);
  		el.setAttribute("usage",usage);
  		el.setAttribute("server-username",serverUsername);
  		el.setAttribute("proxy-server",proxyServer);
  		el.setAttribute("proxy-username",proxyUsername);
  		el.setAttribute("proxy-port",proxyPort);
  		el.setAttribute("security-key",ConfigWebFactory.encrypt(securityKey));
  		el.setAttribute("admin-password",ConfigWebFactory.encrypt(adminPassword));
  		el.setAttribute("server-password",ConfigWebFactory.encrypt(serverPassword));
  		el.setAttribute("proxy-password",ConfigWebFactory.encrypt(proxyPassword));
      	
      	
      	clients.appendChild(el);
	}



	public void updateExtensionInfo(boolean enabled) {
		Element extensions=_getRootElement("extensions");
		extensions.setAttribute("enabled", Caster.toString(enabled));
	}
	

	public void updateExtensionProvider(String strUrl) {
		Element extensions=_getRootElement("extensions");
		Element[] children = ConfigWebFactory.getChildren(extensions,"provider");
      	strUrl=strUrl.trim();
        
      	// Update
		Element el;
		String url;
        for(int i=0;i<children.length;i++) {
      	    el=children[i];
      	    url=el.getAttribute("url");
      	    if(	url!=null && url.trim().equalsIgnoreCase(strUrl)) {
  				//el.setAttribute("cache-timeout",Caster.toString(cacheTimeout));
  				return ;
  			}
      	}
        
     // Insert
      	el = doc.createElement("provider");

      	el.setAttribute("url",strUrl);
      	//el.setAttribute("cache-timeout",Caster.toString(cacheTimeout));
  		
  		XMLUtil.prependChild(extensions,el);
	}
	

	public void removeExtensionProvider(String strUrl) {
		Element parent=_getRootElement("extensions");
		Element[] children = ConfigWebFactory.getChildren(parent,"provider");
		strUrl=strUrl.trim();
		Element child;
		String url;
        for(int i=0;i<children.length;i++) {
      	    child=children[i];
      	    url=child.getAttribute("url");
			if(	url!=null && url.trim().equalsIgnoreCase(strUrl)) {
  				parent.removeChild(child);
  				return ;
  			}
      	}
	}
	

	public void updateExtension(Extension extension) throws PageException {
		checkWriteAccess();
		
		String uid = createUid(extension.getProvider(),extension.getId());
		
		Element extensions=_getRootElement("extensions");
		Element[] children = ConfigWebFactory.getChildren(extensions,"extension");
      	
        // Update
		Element el;
		String provider,id;
        for(int i=0;i<children.length;i++) {
      	    el=children[i];
      	    provider=el.getAttribute("provider");
      	    id=el.getAttribute("id");
  			if(uid.equalsIgnoreCase(createUid(provider, id))) {
  				setExtensionAttrs(el,extension);
  				return ;
  			}
      	}
        
     // Insert
      	el = doc.createElement("extension");

  		el.setAttribute("provider",extension.getProvider());
  		el.setAttribute("id",extension.getId());
  		setExtensionAttrs(el,extension);
      	extensions.appendChild(el);
	}


	private String createUid(String provider, String id) throws PageException {
		if(Decision.isUUId(id)) {
			return Hash.invoke(config,id,null,null);
		}
		else {
			return Hash.invoke(config,provider+id,null,null);
		}
	}


	private void setExtensionAttrs(Element el, Extension extension) {
      	el.setAttribute("version",extension.getVersion());
      	
      	el.setAttribute("config",extension.getStrConfig());
      	//el.setAttribute("config",new ScriptConverter().serialize(extension.getConfig()));
  		
  		el.setAttribute("category",extension.getCategory());
  		el.setAttribute("description",extension.getDescription());
  		el.setAttribute("image",extension.getImage());
  		el.setAttribute("label",extension.getLabel());
  		el.setAttribute("name",extension.getName());

  		el.setAttribute("author",extension.getAuthor());
  		el.setAttribute("type",extension.getType());
  		el.setAttribute("codename",extension.getCodename());
  		el.setAttribute("video",extension.getVideo());
  		el.setAttribute("support",extension.getSupport());
  		el.setAttribute("documentation",extension.getDocumentation());
  		el.setAttribute("forum",extension.getForum());
  		el.setAttribute("mailinglist",extension.getMailinglist());
  		el.setAttribute("network",extension.getNetwork());
  		el.setAttribute("created",Caster.toString(extension.getCreated(),null));


	}
	

	public void resetORMSetting() throws SecurityException {
		boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_ORM);
        
    	if(!hasAccess)
            throw new SecurityException("no access to update ORM Settings");
        
		
		
		Element orm=_getRootElement("orm");
		orm.getParentNode().removeChild(orm);
	}
	

	public void updateORMSetting(ORMConfiguration oc) throws SecurityException {
		boolean hasAccess=ConfigWebUtil.hasAccess(config,SecurityManagerImpl.TYPE_ORM);
        
		if(!hasAccess)
            throw new SecurityException("no access to update ORM Settings");
        
		
		
		Element orm=_getRootElement("orm");
		orm.setAttribute("autogenmap",Caster.toString(oc.autogenmap(),"true"));
		orm.setAttribute("event-handler",Caster.toString(oc.eventHandler(),""));
		orm.setAttribute("event-handling",Caster.toString(oc.eventHandling(),"false"));
		orm.setAttribute("naming-strategy",Caster.toString(oc.namingStrategy(),""));
		orm.setAttribute("flush-at-request-end",Caster.toString(oc.flushAtRequestEnd(),"true"));
		orm.setAttribute("cache-provider",Caster.toString(oc.getCacheProvider(),""));
		orm.setAttribute("cache-config",Caster.toString(oc.getCacheConfig(),"true"));
		orm.setAttribute("catalog",Caster.toString(oc.getCatalog(),""));
		orm.setAttribute("db-create",ORMConfiguration.dbCreateAsString(oc.getDbCreate()));
		orm.setAttribute("dialect",Caster.toString(oc.getDialect(),""));
		orm.setAttribute("schema",Caster.toString(oc.getSchema(),""));
		orm.setAttribute("log-sql",Caster.toString(oc.logSQL(),"false"));
		orm.setAttribute("save-mapping",Caster.toString(oc.saveMapping(),"false"));
		orm.setAttribute("secondary-cache-enable",Caster.toString(oc.secondaryCacheEnabled(),"false"));
		orm.setAttribute("use-db-for-mapping",Caster.toString(oc.useDBForMapping(),"true"));
		orm.setAttribute("orm-config",Caster.toString(oc.getOrmConfig(),""));
		orm.setAttribute("sql-script",Caster.toString(oc.getSqlScript(),"true"));
		
		if(oc.isDefaultCfcLocation()) {
			orm.removeAttribute("cfc-location");
		}
		else {
			Resource[] locations = oc.getCfcLocations();
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<locations.length;i++) {
				if(i!=0)sb.append(",");
				sb.append(locations[i].getAbsolutePath());
			}
			orm.setAttribute("cfc-location",sb.toString());
		}
		
		
		orm.setAttribute("sql-script",Caster.toString(oc.getSqlScript(),"true"));
		
	}


	public void removeExtension(String provider, String id) throws SecurityException {
		checkWriteAccess();
    	
		Element extensions=_getRootElement("extensions");
		Element[] children = ConfigWebFactory.getChildren(extensions,"extension");
		Element child;
		String _provider, _id;
      	for(int i=0;i<children.length;i++) {
      	    child=children[i];
      		_provider=child.getAttribute("provider");
      	    _id=child.getAttribute("id"); 
  	    	if(_provider!=null && _provider.equalsIgnoreCase(provider) && _id!=null && _id.equalsIgnoreCase(id)) {
	      		extensions.removeChild(child);
  			}
  	    }
	}
	
	public void verifyExtensionProvider(String strUrl) throws PageException {
		HttpMethod method=null;
		try {
			URL url = HTTPUtil.toURL(strUrl+"?wsdl");
			method = HTTPUtil.invoke(url, null, null, 2000, null, null, null, -1, null, null, null);
		} 
		catch (MalformedURLException e) {
			throw new ApplicationException("url defintion ["+strUrl+"] is invalid");
		} 
		catch (IOException e) {
			throw new ApplicationException("can't invoke ["+strUrl+"]",e.getMessage());
		}
		
		if(method.getStatusCode()!=200){
			throw new HTTPException(method);
		}
		//Object o = 
			CreateObject.doWebService(null, strUrl+"?wsdl");
			
	}


	public void updateTLD(Resource resTld) throws IOException {
		updateLD(config.getTldFile(),resTld);
	}
	public void updateFLD(Resource resFld) throws IOException {
		updateLD(config.getFldFile(),resFld);
	}
	
	public void updateLD(Resource dir,Resource res) throws IOException {
		if(!dir.exists())dir.createDirectory(true);
    	
    	Resource file = dir.getRealResource(res.getName());
    	if(file.length()!=res.length()){
			ResourceUtil.copy(res, file);
		}
	}

	public void removeTLD(String name) throws IOException {
		removeLD(config.getTldFile(),name);
	}

	public void removeFLD(String name) throws IOException {
		removeLD(config.getFldFile(),name);
	}
	private void removeLD(Resource dir,String name) throws IOException {
		if(dir.isDirectory()){
			Resource[] children = dir.listResources(new MyResourceNameFilter(name));
			for(int i=0;i<children.length;i++){
				children[i].remove(false);
			}
		}
	}

	public void updateJar(Resource resJar) throws IOException {
		Resource lib = config.getConfigDir().getRealResource("lib");
		if(!lib.exists())lib.mkdir();
    	
		Resource fileLib = lib.getRealResource(resJar.getName());
		
		if(fileLib.length()!=resJar.length()){
			IOUtil.closeEL(config.getClassLoader());
			ResourceUtil.copy(resJar, fileLib);
			ConfigWebFactory.reloadLib(this.config);
		}
	}


	public void updateLogSettings(String name, int level,String virtualpath, int maxfile, int maxfilesize) throws ApplicationException {
		name=name.toLowerCase().trim();
		
		
		
		
		if("application".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "application","application") ;
		}
		else if("exception".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "application","exception") ;
		}
		else if("trace".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "application","trace") ;
		}
		else if("thread".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "application","thread") ;
		}
		else if("orm".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "orm") ;
		}
		else if("gateway".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "gateways") ;
		}
		else if("mail".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "mail") ;
		}  
		else if("mapping".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "mappings") ;
		}  
		else if("remote-client".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "remote-clients") ;
		}  
		else if("request-timeout".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "application","requesttimeout") ;
		}  
		else if("request-timeout".equals(name)) {
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "application","requesttimeout") ;
		}  
		else if("schedule-task".equals(name)) {
			if(config instanceof ConfigServer)
				throw new ApplicationException("scheduled task logger is not supported for server context");
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "scheduler") ;
		}  
		else if("search".equals(name)) {
			if(config instanceof ConfigServer)
				throw new ApplicationException("search logger is not supported for server context");
			updateLogSettings(name, level,virtualpath, maxfile, maxfilesize, "search") ;
		}  
		else {
			throw new ApplicationException("invalid logger name ["+name+"], supported names are [application,exception,trace,thread,orm,gateway,mail,mapping,remote-client,request-timeout,request-timeout,schedule-task,search]");
		}
	}

	private void updateLogSettings(String name, int level, String virtualpath,int maxfile, int maxfilesize, String elName){
		updateLogSettings(name, level, virtualpath,maxfile,maxfilesize,elName, null);
	}

	private void updateLogSettings(String name, int level, String virtualpath,int maxfile, int maxfilesize, String elName, String prefix) {
		if(StringUtil.isEmpty(prefix)) prefix="";
		else prefix+="-";
		
		Element el = _getRootElement(elName);
		el.setAttribute(prefix+"log", virtualpath);
		el.setAttribute(prefix+"log-level", LogUtil.toStringType(level, ""));
		el.setAttribute(prefix+"log-max-file", Caster.toString(maxfile));
		el.setAttribute(prefix+"log-max-file-size", Caster.toString(maxfilesize));
	}


	public void updateRemoteClientUsage(String code, String displayname) {
		Struct usage = config.getRemoteClientUsage();
		usage.setEL(code, displayname);
		
		Element extensions=_getRootElement("remote-clients");
		extensions.setAttribute("usage", toStringURLStyle(usage));		
		
	}

	public void updateClusterClass(String classname) throws PageException {
		if(StringUtil.isEmpty(classname,true))
			classname=ClusterNotSupported.class.getName();
		
		
    	Class clazz=null;
		try {
			clazz = ClassUtil.loadClass(config.getClassLoader(),classname);
		} catch (ClassException e) {
			throw Caster.toPageException(e);
		}
		if(!Reflector.isInstaneOf(clazz,Cluster.class) && !Reflector.isInstaneOf(clazz,ClusterRemote.class))
    		throw new ApplicationException("class ["+clazz.getName()+"] does not implement interface ["+Cluster.class.getName()+"] or ["+ClusterRemote.class.getName()+"]");   
		

		Element scope=_getRootElement("scope");
		scope.setAttribute("cluster-class", clazz.getName());	
		ScopeContext.clearClusterScope();
	}
	

	
	public void updateVideoExecuterClass(String classname) throws PageException {
		classname=classname.trim();
		
		if(StringUtil.isEmpty(classname,true))
			classname=VideoExecuterNotSupported.class.getName();
		
		Class clazz=null;
		try {
			clazz = ClassUtil.loadClass(config.getClassLoader(),classname);
		} catch (ClassException e) {
			throw Caster.toPageException(e);
		}
		if(!Reflector.isInstaneOf(clazz,VideoExecuter.class))
    		throw new ApplicationException("class ["+clazz.getName()+"] does not implement interface ["+VideoExecuter.class.getName()+"]");   
		

		Element app=_getRootElement("video");
		app.setAttribute("video-executer-class", clazz.getName());	
	}
	public void updateAdminSyncClass(String classname) throws PageException {
		classname=classname.trim();
		
		if(StringUtil.isEmpty(classname,true))
			classname=AdminSyncNotSupported.class.getName();
		
		Class clazz=null;
		try {
			clazz = ClassUtil.loadClass(config.getClassLoader(),classname);
		} catch (ClassException e) {
			throw Caster.toPageException(e);
		}
		if(!Reflector.isInstaneOf(clazz,AdminSync.class))
    		throw new ApplicationException("class ["+clazz.getName()+"] does not implement interface ["+AdminSync.class.getName()+"]");   
		

		Element app=_getRootElement("application");
		app.setAttribute("admin-sync-class", clazz.getName());	
	}
	
	
	
	public void removeRemoteClientUsage(String code) {
		Struct usage = config.getRemoteClientUsage();
		usage.removeEL(KeyImpl.getInstance(code));
		
		Element extensions=_getRootElement("remote-clients");
		extensions.setAttribute("usage", toStringURLStyle(usage));		
		
	}

	public void removeJar(String strNames) throws IOException {
		
		
		Resource lib = config.getConfigDir().getRealResource("lib");
		boolean changed=false;
		if(lib.isDirectory()){
			String[] names = List.listToStringArray(strNames, ',');
			for(int n=0;n<names.length;n++){
				Resource[] children = lib.listResources(new MyResourceNameFilter(names[n].trim()));
				for(int i=0;i<children.length;i++){
					try {
						changed=true;
						IOUtil.closeEL(config.getClassLoader());
						children[i].remove(false);
					} 
					catch (IOException ioe) {
						if(children[i] instanceof File)
							((File)children[i]).deleteOnExit();
						else{
							ConfigWebFactory.reloadLib(this.config);
							throw ioe;
						}
					}
				}
			}
		}
		if(changed){
			ConfigWebFactory.reloadLib(this.config);
			//new ResourceClassLoader(lib.listResources(),config.getClass().getClassLoader());
		}
	}

	class MyResourceNameFilter implements ResourceNameFilter {
		private String name;
		public MyResourceNameFilter(String name){
			this.name=name;
		}
		
		/**
		 * @see railo.commons.io.res.filter.ResourceNameFilter#accept(railo.commons.io.res.Resource, java.lang.String)
		 */
		public boolean accept(Resource parent, String name) {
			return name.equals(this.name);
		}
	}

	 public void updateSerial(String serial) throws PageException {
		 	
	    	checkWriteAccess();
	        if(!(config instanceof ConfigServer)) {
	            throw new SecurityException("can't change serial number from this context, access is denied");
	        }
	        
	        Element root=doc.getDocumentElement();
	        if(!StringUtil.isEmpty(serial)){
	        	serial=serial.trim();
	        	if(!new SerialNumber(serial).isValid(serial))
	        		throw new SecurityException("serial number is invalid");
	        	root.setAttribute("serial-number",serial);
	        }
	        else{
	        	try{
	        		root.removeAttribute("serial-number");
	        	}
	        	catch(Throwable t){}
	        }
        	try{
        		root.removeAttribute("serial");
        	}
        	catch(Throwable t){}
	    }


	public boolean updateLabel(String hash, String label) {
		// check
        if(StringUtil.isEmpty(hash,true))  return false;
        if(StringUtil.isEmpty(label,true)) return false;
        
		hash=hash.trim(); 
		label=label.trim();
        
        Element labels=_getRootElement("labels");
        
        // Update
        Element[] children = ConfigWebFactory.getChildren(labels,"label");
      	for(int i=0;i<children.length;i++) {
      	    String h=children[i].getAttribute("id");
      	    if(h!=null) {
      	        if(h.equals(hash)) {
		      		Element el=children[i];
		      		if(label.equals(el.getAttribute("name"))) return false;
		      		el.setAttribute("name",label);
		      		return true;
	  			}
      	    }
      	}
      	
      	// Insert
      	Element el=doc.createElement("label");
      	labels.appendChild(el);
      	el.setAttribute("id",hash);
      	el.setAttribute("name",label);
  		
      	return true;
	}
}