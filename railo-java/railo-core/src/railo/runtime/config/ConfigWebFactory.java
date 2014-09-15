package railo.runtime.config;

import static railo.runtime.db.DatasourceManagerImpl.QOQ_DATASOURCE_NAME;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.ServletConfig;

import org.apache.log4j.Level;
import org.jfree.chart.block.LabelBlockImpl;
import org.safehaus.uuid.UUIDGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import railo.aprint;
import railo.commons.collection.MapFactory;
import railo.commons.date.TimeZoneUtil;
import railo.commons.digest.Hash;
import railo.commons.digest.MD5;
import railo.commons.io.CharsetUtil;
import railo.commons.io.DevNullOutputStream;
import railo.commons.io.FileUtil;
import railo.commons.io.IOUtil;
import railo.commons.io.SystemUtil;
import railo.commons.io.log.LoggerAndSourceData;
import railo.commons.io.log.log4j.Log4jUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.ResourcesImpl;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.type.cfml.CFMLResourceProvider;
import railo.commons.io.res.type.s3.S3ResourceProvider;
import railo.commons.io.res.util.ResourceClassLoader;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ByteSizeParser;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassLoaderHelper;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.Md5;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.commons.net.URLDecoder;
import railo.loader.TP;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.CFMLFactoryImpl;
import railo.runtime.Component;
import railo.runtime.Info;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.cache.CacheConnection;
import railo.runtime.cache.CacheConnectionImpl;
import railo.runtime.cache.ServerCacheConnection;
import railo.runtime.cache.eh.EHCache;
import railo.runtime.cfx.customtag.CFXTagClass;
import railo.runtime.cfx.customtag.CPPCFXTagClass;
import railo.runtime.cfx.customtag.JavaCFXTagClass;
import railo.runtime.component.ImportDefintion;
import railo.runtime.config.ajax.AjaxFactory;
import railo.runtime.config.component.ComponentFactory;
import railo.runtime.crypt.BlowfishEasy;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceImpl;
import railo.runtime.dump.ClassicHTMLDumpWriter;
import railo.runtime.dump.DumpWriter;
import railo.runtime.dump.DumpWriterEntry;
import railo.runtime.dump.HTMLDumpWriter;
import railo.runtime.dump.SimpleHTMLDumpWriter;
import railo.runtime.dump.TextDumpWriter;
import railo.runtime.engine.CFMLEngineImpl;
import railo.runtime.engine.ConsoleExecutionLog;
import railo.runtime.engine.ExecutionLog;
import railo.runtime.engine.ExecutionLogFactory;
import railo.runtime.engine.ThreadLocalConfig;
import railo.runtime.engine.ThreadQueueImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.SecurityException;
import railo.runtime.extension.Extension;
import railo.runtime.extension.ExtensionImpl;
import railo.runtime.extension.ExtensionProvider;
import railo.runtime.extension.ExtensionProviderImpl;
import railo.runtime.gateway.GatewayEngineImpl;
import railo.runtime.gateway.GatewayEntry;
import railo.runtime.gateway.GatewayEntryImpl;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.listener.ApplicationContextSupport;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.listener.MixedAppListener;
import railo.runtime.listener.ModernAppListener;
import railo.runtime.monitor.ActionMonitorCollector;
import railo.runtime.monitor.ActionMonitorFatory;
import railo.runtime.monitor.AsyncRequestMonitor;
import railo.runtime.monitor.IntervallMonitor;
import railo.runtime.monitor.IntervallMonitorWrap;
import railo.runtime.monitor.RequestMonitor;
import railo.runtime.monitor.RequestMonitorWrap;
import railo.runtime.net.http.ReqRspUtil;
import railo.runtime.net.mail.Server;
import railo.runtime.net.mail.ServerImpl;
import railo.runtime.net.proxy.ProxyData;
import railo.runtime.net.proxy.ProxyDataImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.op.date.DateCaster;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMConfigurationImpl;
import railo.runtime.orm.ORMEngine;
import railo.runtime.reflection.Reflector;
import railo.runtime.reflection.pairs.ConstructorInstance;
import railo.runtime.search.SearchEngine;
import railo.runtime.security.SecurityManager;
import railo.runtime.security.SecurityManagerImpl;
import railo.runtime.spooler.SpoolerEngineImpl;
import railo.runtime.tag.TagUtil;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.dt.TimeSpan;
import railo.runtime.type.scope.Cluster;
import railo.runtime.type.scope.ClusterRemote;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.runtime.video.VideoExecuter;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.function.FunctionLibException;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibException;

/**
 * 
 */
public final class ConfigWebFactory extends ConfigFactory {
	/**
	 * creates a new ServletConfig Impl Object
	 * 
	 * @param configServer
	 * @param configDir
	 * @param servletConfig
	 * @return new Instance
	 * @throws SAXException
	 * @throws ClassNotFoundException
	 * @throws PageException
	 * @throws IOException
	 * @throws TagLibException
	 * @throws FunctionLibException
	 * @throws NoSuchAlgorithmException 
	 */

	public static ConfigWebImpl newInstance(CFMLFactoryImpl factory, ConfigServerImpl configServer, Resource configDir, boolean isConfigDirACustomSetting,
			ServletConfig servletConfig) throws SAXException, ClassException, PageException, IOException, TagLibException, FunctionLibException, NoSuchAlgorithmException {
		// DO NOT REMOVE!!!!
		try {
			new LabelBlockImpl("aa");
		}
		catch (Throwable t) {

		}

		String hash = SystemUtil.hash(servletConfig.getServletContext());
		Map<String, String> labels = configServer.getLabels();
		String label = null;
		if (labels != null) {
			label = labels.get(hash);
		}
		if (label == null)
			label = hash;

		SystemOut.print(SystemUtil.getPrintWriter(SystemUtil.OUT), "===================================================================\n" + "WEB CONTEXT (" + label + ")\n"
				+ "-------------------------------------------------------------------\n" + "- config:" + configDir + (isConfigDirACustomSetting ? " (custom setting)" : "") + "\n"
				+ "- webroot:" + ReqRspUtil.getRootPath(servletConfig.getServletContext()) + "\n" + "- hash:" + hash + "\n" + "- label:" + label + "\n"
				+ "===================================================================\n"

		);
		
		boolean doNew = doNew(configDir);

		Resource configFile = configDir.getRealResource("railo-web.xml.cfm");
		Resource configFileOld = configDir.getRealResource("railo-web.xml");

		String strPath = servletConfig.getServletContext().getRealPath("/WEB-INF");
		Resource path = ResourcesImpl.getFileResourceProvider().getResource(strPath);

		// get config file
		if (!configFile.exists()) {
			if (configFileOld.exists()) {
				// if(!configFileOld.renameTo(configFile))
				configFile = configFileOld;
			}
			else
				createConfigFile("web", configFile);
		}
		Document doc = null;

		Resource bugFile;
		int count = 1;
		// rename old bugfiles
		while ((bugFile = configDir.getRealResource("railo-web." + (count++) + ".buggy.cfm")).exists()) {
			bugFile.renameTo(configDir.getRealResource("railo-web." + (count) + ".buggy"));
		}

		try {
			doc = loadDocument(configFile);
		}
		catch (Exception e) {
			// rename buggy config files
			if (configFile.exists()) {
				SystemOut.printDate(SystemUtil.getPrintWriter(SystemUtil.OUT), "config file " + configFile + " was not valid and has been replaced");
				count = 1;
				while ((bugFile = configDir.getRealResource("railo-web." + (count++) + ".buggy")).exists()) {
				}
				IOUtil.copy(configFile, bugFile);
				configFile.delete();
			}
			createConfigFile("web", configFile);
			doc = loadDocument(configFile);
		}

		// htaccess
		if (path.exists())
			createHtAccess(path.getRealResource(".htaccess"));
		if (configDir.exists())
			createHtAccess(configDir.getRealResource(".htaccess"));

		createContextFiles(configDir, servletConfig, doNew);
		ConfigWebImpl configWeb = new ConfigWebImpl(factory, configServer, servletConfig, configDir, configFile);

		load(configServer, configWeb, doc, false, doNew);
		createContextFilesPost(configDir, configWeb, servletConfig, false, doNew);
		return configWeb;
	}

	public static void createHtAccess(Resource htAccess) {
		if (!htAccess.exists()) {
			htAccess.createNewFile();

			String content = "AuthName \"WebInf Folder\"\n" + "AuthType Basic\n" + "<Limit GET POST>\n" + "order deny,allow\n" + "deny from all\n" + "</Limit>";
			try {
				IOUtil.copy(new ByteArrayInputStream(content.getBytes()), htAccess, true);
			}
			catch (Throwable t) {
			}
		}
	}

	/**
	 * reloads the Config Object
	 * 
	 * @param cs
	 * @param force
	 * @throws SAXException
	 * @throws ClassNotFoundException
	 * @throws PageException
	 * @throws IOException
	 * @throws TagLibException
	 * @throws FunctionLibException
	 * @throws NoSuchAlgorithmException 
	 */
	public static void reloadInstance(ConfigServerImpl cs, ConfigWebImpl cw, boolean force) throws SAXException, ClassException, PageException, IOException, TagLibException,
			FunctionLibException {
		Resource configFile = cw.getConfigFile();
		Resource configDir = cw.getConfigDir();

		boolean doNew = doNew(configDir);

		if (configFile == null)
			return;

		if (second(cw.getLoadTime()) > second(configFile.lastModified()) && !force)
			return;

		Document doc = loadDocument(configFile);
		createContextFiles(configDir, null, doNew);
		cw.reset();

		load(cs, cw, doc, true, doNew);
		createContextFilesPost(configDir, cw, null, false, doNew);
	}

	private static long second(long ms) {
		return ms / 1000;
	}

	/**
	 * @param cs
	 * @param config
	 * @param doc
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws FunctionLibException
	 * @throws TagLibException
	 * @throws PageException
	 */
	public static void load(ConfigServerImpl cs, ConfigImpl config, Document doc, boolean isReload, boolean doNew) throws ClassException, PageException, IOException,
			TagLibException, FunctionLibException {
		ThreadLocalConfig.register(config);
		// fix
		boolean reload=false;
		if(ConfigWebAdmin.fixLFI(doc)) {
			String xml = XMLCaster.toString(doc);
			xml=StringUtil.replace(xml, "<railo-configuration", "<cfRailoConfiguration",false);
			xml=StringUtil.replace(xml, "</railo-configuration", "</cfRailoConfiguration",false);
			IOUtil.write(config.getConfigFile(), xml, CharsetUtil.UTF8, false);
			try {
				doc = ConfigWebFactory.loadDocument(config.getConfigFile());
			}
			catch (SAXException e) {
			}
		}
		if(ConfigWebAdmin.fixSalt(doc)) reload=true;
		if(ConfigWebAdmin.fixS3(doc)) reload=true;
		if(ConfigWebAdmin.fixPSQ(doc)) reload=true;
		if(ConfigWebAdmin.fixLogging(cs,config,doc)) reload=true;
		
		if (reload) {
			XMLCaster.writeTo(doc, config.getConfigFile());
			try {
				doc = ConfigWebFactory.loadDocument(config.getConfigFile());
			}
			catch (SAXException e) {
			}
		}
		config.setLastModified();
		if(config instanceof ConfigWeb)deployWebContext(cs,(ConfigWeb)config,false);
		loadRailoConfig(cs, config, doc);
		int mode = config.getMode();
		loadConstants(cs, config, doc);
		loadLoggers(cs, config, doc, isReload);
		loadTempDirectory(cs, config, doc, isReload);
		loadId(config);
		loadVersion(config, doc);
		loadSecurity(cs, config, doc);
		loadLib(cs, config);
		loadSystem(cs, config, doc);
		loadORM(cs, config, doc);
		loadResourceProvider(cs, config, doc);
		loadCharset(cs, config, doc);
		loadApplication(cs, config, doc, mode);
		loadMappings(cs, config, doc, mode); // it is important this runs after
												// loadApplication
		loadRest(cs, config, doc);
		loadExtensions(cs, config, doc);
		loadPagePool(cs, config, doc);
		loadDataSources(cs, config, doc);
		loadCache(cs, config, doc);
		loadCustomTagsMappings(cs, config, doc, mode);
		loadFilesystem(cs, config, doc, doNew); // load tlds
		loadTag(cs, config, doc); // load tlds
		loadRegional(cs, config, doc);
		loadCompiler(cs, config, doc, mode);
		loadScope(cs, config, doc, mode);
		loadMail(cs, config, doc);
		loadSearch(cs, config, doc);
		loadScheduler(cs, config, doc);
		loadDebug(cs, config, doc);
		loadError(cs, config, doc);
		loadCFX(cs, config, doc);
		loadComponent(cs, config, doc, mode);
		loadUpdate(cs, config, doc);
		loadJava(cs, config, doc); // define compile type
		loadSetting(cs, config, doc);
		loadProxy(cs, config, doc);
		loadRemoteClient(cs, config, doc);
		loadVideo(cs, config, doc);
		loadFlex(cs, config, doc);
		settings(config);
		loadListener(cs, config, doc);
		loadDumpWriter(cs, config, doc);
		loadGatewayEL(cs, config, doc);
		loadExeLog(cs, config, doc);
		loadThreadQueue(cs, config, doc);
		loadMonitors(cs, config, doc);
		loadLogin(cs, config, doc);
		config.setLoadTime(System.currentTimeMillis());

		if (config instanceof ConfigWebImpl)
			TagUtil.addTagMetaData((ConfigWebImpl) config);

		ThreadLocalConfig.release();
	}

	public static void deployWebContext(ConfigServer cs, ConfigWeb cw, boolean throwError) throws IOException  {
		Resource deploy = cs.getConfigDir().getRealResource("web-context-deployment"),trg;
        if(!deploy.isDirectory()) return;
			trg=cw.getConfigDir().getRealResource("context");
        	try {
				_deployWebContext(cw,deploy,trg);	
			}
			catch (IOException ioe) {
				if(throwError) throw ioe;
				SystemOut.printDate(cw.getErrWriter(), ExceptionUtil.getStacktrace(ioe, true));
			}
        
	}

	private static void _deployWebContext(ConfigWeb cw,Resource src, Resource trg) throws IOException {
		if(!src.isDirectory())return;
		if(trg.isFile()) trg.delete();
		if(!trg.exists()) trg.mkdirs();
		Resource _src,_trg;
		Resource[] children = src.listResources();
		if(ArrayUtil.isEmpty(children)) return;
		
		for(int i=0;i<children.length;i++){
			_src=children[i];
			_trg=trg.getRealResource(_src.getName());
			if(_src.isDirectory()) 
				_deployWebContext(cw,_src,_trg);
			if(_src.isFile()) {
				if(_src.length()!=_trg.length()) {
					_src.copyTo(_trg, false);
					SystemOut.printDate(cw.getOutWriter(), "write file:" + _trg);
					
				}
			}
		}
		
		
        //src.copyTo(trg, false);
		
		
	}

	private static void loadResourceProvider(ConfigServerImpl configServer, ConfigImpl config, Document doc) throws ClassException {
		boolean hasCS = configServer != null;
		config.clearResourceProviders();
		Element resources = getChildByName(doc.getDocumentElement(), "resources");
		Element[] providers = getChildren(resources, "resource-provider");
		Element[] defaultProviders = getChildren(resources, "default-resource-provider");

		// Default Resource Provider
		if (hasCS)
			config.setDefaultResourceProvider(configServer.getDefaultResourceProvider());
		if (defaultProviders != null && defaultProviders.length > 0) {
			Element defaultProvider = defaultProviders[defaultProviders.length - 1];
			String strDefaultProviderClass = defaultProvider.getAttribute("class");
			String strDefaultProviderComponent = defaultProvider.getAttribute("component");

			// class
			if (!StringUtil.isEmpty(strDefaultProviderClass)) {
				strDefaultProviderClass = strDefaultProviderClass.trim();
				config.setDefaultResourceProvider(strDefaultProviderClass, toArguments(defaultProvider.getAttribute("arguments"), true));
			}

			// component
			else if (!StringUtil.isEmpty(strDefaultProviderComponent)) {
				strDefaultProviderComponent = strDefaultProviderComponent.trim();
				Map<String, String> args = toArguments(defaultProvider.getAttribute("arguments"), true);
				args.put("component", strDefaultProviderComponent);
				config.setDefaultResourceProvider(CFMLResourceProvider.class.getName(), args);
			}
		}

		// Resource Provider
		if (hasCS)
			config.setResourceProviders(configServer.getResourceProviders());
		if (providers != null && providers.length > 0) {

			String strProviderClass;
			String strProviderCFC;
			String strProviderScheme;
			String httpClass = null;
			Map httpArgs = null;
			boolean hasHTTPs = false, hasS3 = false;
			String s3Class = "railo.commons.io.res.type.s3.S3ResourceProvider";
			for (int i = 0; i < providers.length; i++) {
				strProviderClass = providers[i].getAttribute("class");
				strProviderCFC = providers[i].getAttribute("component");

				// ignore S3 extension
				if ("railo.extension.io.resource.type.s3.S3ResourceProvider".equals(strProviderClass))
					strProviderClass = S3ResourceProvider.class.getName();

				strProviderScheme = providers[i].getAttribute("scheme");
				// class
				if (!StringUtil.isEmpty(strProviderClass) && !StringUtil.isEmpty(strProviderScheme)) {
					strProviderClass = strProviderClass.trim();
					strProviderScheme = strProviderScheme.trim().toLowerCase();
					config.addResourceProvider(strProviderScheme, strProviderClass, toArguments(providers[i].getAttribute("arguments"), true));

					// patch for user not having
					if (strProviderScheme.equalsIgnoreCase("http")) {
						httpClass = strProviderClass;
						httpArgs = toArguments(providers[i].getAttribute("arguments"), true);
					}
					else if (strProviderScheme.equalsIgnoreCase("https"))
						hasHTTPs = true;
					else if (strProviderScheme.equalsIgnoreCase("s3"))
						hasS3 = true;
				}

				// cfc
				else if (!StringUtil.isEmpty(strProviderCFC) && !StringUtil.isEmpty(strProviderScheme)) {
					strProviderCFC = strProviderCFC.trim();
					strProviderScheme = strProviderScheme.trim().toLowerCase();
					Map<String, String> args = toArguments(providers[i].getAttribute("arguments"), true);
					args.put("component", strProviderCFC);
					config.addResourceProvider(strProviderScheme, CFMLResourceProvider.class, args);
				}
			}

			// adding https when not exist
			if (!hasHTTPs && httpClass != null) {
				config.addResourceProvider("https", httpClass, httpArgs);
			}
			// adding s3 when not exist
			if (!hasS3 && config instanceof ConfigServer) {
				config.addResourceProvider("s3", s3Class, toArguments("lock-timeout:10000;", false));
			}
		}
	}

	private static void loadDumpWriter(ConfigServerImpl configServer, ConfigImpl config, Document doc) throws ClassException {
		boolean hasCS = configServer != null;

		Element coll = getChildByName(doc.getDocumentElement(), "dump-writers");
		Element[] writers = getChildren(coll, "dump-writer");

		Struct sct = new StructImpl();

		boolean hasPlain = false;
		boolean hasRich = false;
		if (hasCS) {
			DumpWriterEntry[] entries = configServer.getDumpWritersEntries();
			if (entries != null)
				for (int i = 0; i < entries.length; i++) {
					if (entries[i].getDefaultType() == HTMLDumpWriter.DEFAULT_PLAIN)
						hasPlain = true;
					if (entries[i].getDefaultType() == HTMLDumpWriter.DEFAULT_RICH)
						hasRich = true;
					sct.put(entries[i].getName(), entries[i]);
				}
		}

		if (writers != null && writers.length > 0) {
			String strClass;
			String strName;
			String strDefault;
			Class clazz;
			int def = HTMLDumpWriter.DEFAULT_NONE;
			for (int i = 0; i < writers.length; i++) {
				strClass = writers[i].getAttribute("class");
				strName = writers[i].getAttribute("name");
				strDefault = writers[i].getAttribute("default");
				clazz = ClassUtil.loadClass(strClass, null);
				if (clazz != null && !StringUtil.isEmpty(strName)) {
					if (StringUtil.isEmpty(strDefault))
						def = HTMLDumpWriter.DEFAULT_NONE;
					else if ("browser".equalsIgnoreCase(strDefault))
						def = HTMLDumpWriter.DEFAULT_RICH;
					else if ("console".equalsIgnoreCase(strDefault))
						def = HTMLDumpWriter.DEFAULT_PLAIN;
					sct.put(strName, new DumpWriterEntry(def, strName, (DumpWriter) ClassUtil.loadInstance(clazz)));
				}
			}
		}
		else {
			// print.err("yep");
			if (!hasRich)
				sct.setEL(KeyConstants._html, new DumpWriterEntry(HTMLDumpWriter.DEFAULT_RICH, "html", new HTMLDumpWriter()));
			if (!hasPlain)
				sct.setEL(KeyConstants._text, new DumpWriterEntry(HTMLDumpWriter.DEFAULT_PLAIN, "text", new TextDumpWriter()));

			sct.setEL(KeyConstants._classic, new DumpWriterEntry(HTMLDumpWriter.DEFAULT_NONE, "classic", new ClassicHTMLDumpWriter()));
			sct.setEL(KeyConstants._simple, new DumpWriterEntry(HTMLDumpWriter.DEFAULT_NONE, "simple", new SimpleHTMLDumpWriter()));

		}
		Iterator<Object> it = sct.valueIterator();
		java.util.List<DumpWriterEntry> entries = new ArrayList<DumpWriterEntry>();
		while (it.hasNext()) {
			entries.add((DumpWriterEntry) it.next());
		}
		config.setDumpWritersEntries(entries.toArray(new DumpWriterEntry[entries.size()]));
	}

	static Map<String, String> toArguments(String attributes, boolean decode) {
		return toArguments(attributes, decode, false);
		
	}
	static Map<String, String> toArguments(String attributes, boolean decode, boolean lowerKeys) {
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtil.isEmpty(attributes,true))
			return map;
		String[] arr = ListUtil.toStringArray(ListUtil.listToArray(attributes, ';'), null);

		int index;
		String str;
		for (int i = 0; i < arr.length; i++) {
			str = arr[i].trim();
			if (StringUtil.isEmpty(str))
				continue;
			index = str.indexOf(':');
			if (index == -1)
				map.put(lowerKeys?str.toLowerCase():str, "");
			else {
				String k=dec(str.substring(0, index).trim(), decode);
				if(lowerKeys)k=k.toLowerCase();
				map.put(k, dec(str.substring(index + 1).trim(), decode));
			}
		}
		return map;
	}

	private static String dec(String str, boolean decode) {
		if (!decode)
			return str;
		return URLDecoder.decode(str, false);
	}

	private static void loadListener(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		if (config instanceof ConfigServer) {
			ConfigServer cs = (ConfigServer) config;
			Element listener = getChildByName(doc.getDocumentElement(), "listener");
			String strClass = listener.getAttribute("class");
			String strArguments = listener.getAttribute("arguments");
			if (strArguments == null)
				strArguments = "";

			if (!StringUtil.isEmpty(strClass)) {
				try {

					Object obj = ClassUtil.loadInstance(strClass, new Object[] { strArguments }, null);
					if (obj instanceof ConfigListener) {
						ConfigListener cl = (ConfigListener) obj;
						cs.setConfigListener(cl);
					}
				}
				catch (Throwable t) {
					t.printStackTrace(config.getErrWriter());

				}

			}
		}
		else if (configServer != null) {
			ConfigListener listener = configServer.getConfigListener();
			if (listener != null)
				listener.onLoadWebContext(configServer, (ConfigWeb) config);
		}
	}

	private static void settings(ConfigImpl config) {
		if ((config instanceof ConfigWebImpl))
			doCheckChangesInLibraries((ConfigWebImpl) config);

	}

	private static void loadVersion(ConfigImpl config, Document doc) {
		Element railoConfiguration = doc.getDocumentElement();
		String strVersion = railoConfiguration.getAttribute("version");
		config.setVersion(Caster.toDoubleValue(strVersion, 1.0d));
	}

	private static void loadId(ConfigImpl config) {
		Resource res = config.getConfigDir().getRealResource("id");
		String securityKey = null;
		try {
			if (!res.exists()) {
				res.createNewFile();
				IOUtil.write(res, securityKey = UUIDGenerator.getInstance().generateRandomBasedUUID().toString(), SystemUtil.getCharset(), false);
			}
			else {
				securityKey = IOUtil.toString(res, SystemUtil.getCharset());
			}
		}
		catch (IOException ioe) {
		}

		if (StringUtil.isEmpty(securityKey))
			securityKey = UUIDGenerator.getInstance().generateRandomBasedUUID().toString();

		config.setSecurityKey(securityKey);
	}

	public static void reloadLib(ConfigImpl config) throws IOException {
		if (config instanceof ConfigWebImpl)
			loadLib(((ConfigWebImpl) config).getConfigServerImpl(), config);
		else
			loadLib(null, config);
	}

	private static void loadLib(ConfigServerImpl configServer, ConfigImpl config) throws IOException {
		// get lib and classes resources
		Resource lib = config.getConfigDir().getRealResource("lib");
		lib.mkdir();
		Resource classes = config.getConfigDir().getRealResource("classes");
		classes.mkdir();
		Resource[] libs = lib.listResources(ExtensionResourceFilter.EXTENSION_JAR_NO_DIR);

		// merge resources
		if (!ResourceUtil.isEmptyDirectory(classes, ExtensionResourceFilter.EXTENSION_CLASS_DIR)) {
			if(ArrayUtil.isEmpty(libs)) {
				libs=new Resource[]{classes};
			}
			else {
				Resource[] tmp = new Resource[libs.length + 1];
				for (int i = 0; i < libs.length; i++) {
					tmp[i] = libs[i];
				}
				tmp[libs.length] = classes;
				libs = tmp;
			}
		}

		// get resources from server config and merge
		if (configServer != null) {
			ResourceClassLoader rcl = configServer.getResourceClassLoader();
			libs = ResourceUtil.merge(libs, rcl.getResources());
		}

		// get parent classloader
		// ClassLoader parent;
		// if(configServer==null) parent=new
		// ClassLoaderHelper().getClass().getClassLoader();// this classloader
		// holds all loader and core classes
		// else parent = configServer.getResourceClassLoader(); // Resource
		// classloader from server config

		// set classloader
		ClassLoader parent = new ClassLoaderHelper().getClass().getClassLoader();// this
																					// classloader
																					// holds
																					// all
																					// loader
																					// and
																					// core
																					// classes
		config.setResourceClassLoader(new ResourceClassLoader(libs, parent));

	}

	private static boolean equal(Resource[] srcs, Resource[] trgs) {
		if (srcs.length != trgs.length)
			return false;
		Resource src;
		outer: for (int i = 0; i < srcs.length; i++) {
			src = srcs[i];
			for (int y = 0; y < trgs.length; y++) {
				if (src.equals(trgs[y]))
					continue outer;
			}
			return false;
		}
		return true;
	}

	private static Resource[] getNewResources(Resource[] srcs, Resource[] trgs) {
		Resource trg;
		java.util.List<Resource> list = new ArrayList<Resource>();
		outer: for (int i = 0; i < trgs.length; i++) {
			trg = trgs[i];
			for (int y = 0; y < srcs.length; y++) {
				if (trg.equals(srcs[y]))
					continue outer;
			}
			list.add(trg);
		}
		return list.toArray(new Resource[list.size()]);
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadSecurity(ConfigServerImpl configServer, ConfigImpl config, Document doc) {

		// Serial Number
		if (config instanceof ConfigServer) {
			Element railoConfiguration = doc.getDocumentElement();
			String serial = railoConfiguration.getAttribute("serial-number");
			if (!StringUtil.isEmpty(serial))
				config.setSerialNumber(serial);
		}
		else if (configServer != null) {
			config.setSerialNumber(configServer.getSerialNumber());
		}

		// Security Manger
		SecurityManager securityManager = null;
		if (config instanceof ConfigServerImpl) {
			ConfigServerImpl cs = (ConfigServerImpl) config;
			Element security = getChildByName(doc.getDocumentElement(), "security");

			// Default SecurityManager
			SecurityManagerImpl sm = _toSecurityManager(security);

			// addional file accesss directories
			Element[] elFileAccesses = getChildren(security, "file-access");
			sm.setCustomFileAccess(_loadFileAccess(config, elFileAccesses));

			cs.setDefaultSecurityManager(sm);

			// Web SecurityManager
			Element[] accessors = getChildren(security, "accessor");
			for (int i = 0; i < accessors.length; i++) {
				String id = accessors[i].getAttribute("id");
				if (id != null) {
					sm = _toSecurityManager(accessors[i]);
					elFileAccesses = getChildren(accessors[i], "file-access");
					sm.setCustomFileAccess(_loadFileAccess(config, elFileAccesses));
					cs.setSecurityManager(id, sm);
				}
			}

		}

		else if (configServer != null) {
			securityManager = configServer.getSecurityManager(config.getId());
		}
		if (config instanceof ConfigWebImpl) {
			if (securityManager == null)
				securityManager = SecurityManagerImpl.getOpenSecurityManager();
			((ConfigWebImpl) config).setSecurityManager(securityManager);
		}
	}

	private static Resource[] _loadFileAccess(Config config, Element[] fileAccesses) {
		if (ArrayUtil.isEmpty(fileAccesses))
			return new Resource[0];

		java.util.List<Resource> reses = new ArrayList<Resource>();
		String path;
		Resource res;
		for (int i = 0; i < fileAccesses.length; i++) {
			path = fileAccesses[i].getAttribute("path");
			if (!StringUtil.isEmpty(path)) {
				res = config.getResource(path);
				if (res.isDirectory())
					reses.add(res);
			}
		}
		return reses.toArray(new Resource[reses.size()]);
	}

	private static SecurityManagerImpl _toSecurityManager(Element el) {
		SecurityManagerImpl sm = new SecurityManagerImpl(_attr(el, "setting", SecurityManager.VALUE_YES), _attr(el, "file", SecurityManager.VALUE_ALL), _attr(el,
				"direct_java_access", SecurityManager.VALUE_YES), _attr(el, "mail", SecurityManager.VALUE_YES), _attr(el, "datasource", SecurityManager.VALUE_YES), _attr(el,
				"mapping", SecurityManager.VALUE_YES), _attr(el, "remote", SecurityManager.VALUE_YES), _attr(el, "custom_tag", SecurityManager.VALUE_YES), _attr(el, "cfx_setting",
				SecurityManager.VALUE_YES), _attr(el, "cfx_usage", SecurityManager.VALUE_YES), _attr(el, "debugging", SecurityManager.VALUE_YES), _attr(el, "search",
				SecurityManager.VALUE_YES), _attr(el, "scheduled_task", SecurityManager.VALUE_YES), _attr(el, "tag_execute", SecurityManager.VALUE_YES), _attr(el, "tag_import",
				SecurityManager.VALUE_YES), _attr(el, "tag_object", SecurityManager.VALUE_YES), _attr(el, "tag_registry", SecurityManager.VALUE_YES), _attr(el, "cache",
				SecurityManager.VALUE_YES), _attr(el, "gateway", SecurityManager.VALUE_YES), _attr(el, "orm", SecurityManager.VALUE_YES), _attr2(el, "access_read",
				SecurityManager.ACCESS_PROTECTED), _attr2(el, "access_write", SecurityManager.ACCESS_PROTECTED));
		return sm;
	}

	private static short _attr(Element el, String attr, short _default) {
		return SecurityManagerImpl.toShortAccessValue(el.getAttribute(attr), _default);
	}

	private static short _attr2(Element el, String attr, short _default) {
		String strAccess = el.getAttribute(attr);
		if (StringUtil.isEmpty(strAccess))
			return _default;
		strAccess = strAccess.trim().toLowerCase();
		if ("open".equals(strAccess))
			return SecurityManager.ACCESS_OPEN;
		if ("protected".equals(strAccess))
			return SecurityManager.ACCESS_PROTECTED;
		if ("close".equals(strAccess))
			return SecurityManager.ACCESS_CLOSE;
		return _default;
	}

	/**
	 * creates the Config File, if File not exist
	 * 
	 * @param xmlName
	 * @param configFile
	 * @throws IOException
	 */
	static void createConfigFile(String xmlName, Resource configFile) throws IOException {
		configFile.createFile(true);
		createFileFromResource("/resource/config/" + xmlName + ".xml", configFile.getAbsoluteResource());
	}



	static String createMD5FromResource(String resource) throws IOException {
		InputStream is = null;
		try {
			is = new Info().getClass().getResourceAsStream(resource);
			byte[] barr = IOUtil.toBytes(is);
			return MD5.getDigestAsString(barr);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}

	static String createContentFromResource(Resource resource) throws IOException {
		return IOUtil.toString(resource, (Charset)null);
	}

	static void createFileFromResourceCheckSizeDiffEL(String resource, Resource file) {
		try {
			createFileFromResourceCheckSizeDiff(resource, file);
		}
		catch (Throwable e) {
			aprint.err(resource);
			aprint.err(file);
			e.printStackTrace();
		}
	}

	/**
	 * creates a File and his content froma a resurce
	 * 
	 * @param resource
	 * @param file
	 * @throws IOException
	 */
	static void createFileFromResourceCheckSizeDiff(String resource, Resource file) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtil.copy(new Info().getClass().getResourceAsStream(resource), baos, true, false);
		byte[] barr = baos.toByteArray();

		if (file.exists()) {
			long trgSize = file.length();
			long srcSize = barr.length;
			if (srcSize == trgSize)
				return;

			SystemOut.printDate(SystemUtil.getPrintWriter(SystemUtil.OUT), "update file:" + file);
			SystemOut.printDate(SystemUtil.getPrintWriter(SystemUtil.OUT), " - source:" + srcSize);
			SystemOut.printDate(SystemUtil.getPrintWriter(SystemUtil.OUT), " - target:" + trgSize);

		}
		else
			file.createNewFile();

		// SystemOut.printDate("write file:"+file);
		IOUtil.copy(new ByteArrayInputStream(barr), file, true);
	}

	/**
	 * Creates all files for Railo Context
	 * 
	 * @param configDir
	 * @throws IOException
	 * @throws IOException
	 */
	public static void createContextFiles(Resource configDir, ServletConfig servletConfig, boolean doNew) throws IOException {
		// NICE dies muss dynamisch ersstelt werden, da hier der admin hinkommt
		// und dieser sehr viele files haben wird
		Resource contextDir = configDir.getRealResource("context");
		if (!contextDir.exists())
			contextDir.mkdirs();

		// custom locale files
		{
			Resource dir = configDir.getRealResource("locales");
			if (!dir.exists())
				dir.mkdirs();
			Resource file = dir.getRealResource("pt-PT-date.df");
			if (!file.exists())
				createFileFromResourceEL("/resource/locales/pt-PT-date.df", file);
		}


		// video
		Resource videoDir = configDir.getRealResource("video");
		if (!videoDir.exists())
			videoDir.mkdirs();

		Resource video = videoDir.getRealResource("video.xml");
		if (!video.exists())
			createFileFromResourceEL("/resource/video/video.xml", video);

		// bin
		Resource binDir = configDir.getRealResource("bin");
		if (!binDir.exists())
			binDir.mkdirs();

		Resource ctDir = configDir.getRealResource("customtags");
		if (!ctDir.exists())
			ctDir.mkdirs();

		// Jacob
		if (SystemUtil.isWindows()) {
			String name = (SystemUtil.getJREArch() == SystemUtil.ARCH_64) ? "jacob-x64.dll" : "jacob-x86.dll";
			Resource jacob = binDir.getRealResource(name);
			if (!jacob.exists()) {
				createFileFromResourceEL("/resource/bin/" + name, jacob);
			}
		}

		Resource storDir = configDir.getRealResource("storage");
		if (!storDir.exists())
			storDir.mkdirs();

		Resource cfcDir = configDir.getRealResource("components");
		if (!cfcDir.exists())
			cfcDir.mkdirs();

		// remove old cacerts files, they are now only in the server context
		Resource secDir = configDir.getRealResource("security");
		Resource f = null;
		if (secDir.exists()) {
			f = secDir.getRealResource("cacerts");
			if (f.exists())
				f.delete();
			if (ResourceUtil.isEmpty(secDir))
				secDir.delete();
		}

		f = contextDir.getRealResource("railo-context.ra");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/context/railo-context.ra", f);
		else
			createFileFromResourceCheckSizeDiffEL("/resource/context/railo-context.ra", f);

		f = contextDir.getRealResource("component-dump.cfm");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/component-dump.cfm", f);

		// Component.cfc
		String badContent = "<cfcomponent displayname=\"Component\" hint=\"This is the Base Component\">\n</cfcomponent>";
		String badVersion = "704b5bd8597be0743b0c99a644b65896";
		f = contextDir.getRealResource("Component.cfc");

		if (!f.exists())
			createFileFromResourceEL("/resource/context/Component.cfc", f);
		else if (doNew && badVersion.equals(ConfigWebUtil.createMD5FromResource(f))) {
			createFileFromResourceEL("/resource/context/Component.cfc", f);
		}
		else if (doNew && badContent.equals(createContentFromResource(f).trim())) {
			createFileFromResourceEL("/resource/context/Component.cfc", f);
		}

		f = contextDir.getRealResource(Constants.APP_CFM);
		if (!f.exists())
			createFileFromResourceEL("/resource/context/Application.cfm", f);

		f = contextDir.getRealResource("form.cfm");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/context/form.cfm", f);

		f = contextDir.getRealResource("graph.cfm");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/context/graph.cfm", f);

		f = contextDir.getRealResource("wddx.cfm");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/wddx.cfm", f);

		f = contextDir.getRealResource("railo-applet.cfm");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/railo-applet.cfm", f);

		f = contextDir.getRealResource("railo-applet.jar");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/context/railo-applet.jar", f);

		// f=new BinaryFile(contextDir,"railo_context.ra");
		// if(!f.exists())createFileFromResource("/resource/context/railo_context.ra",f);

		f = contextDir.getRealResource("admin.cfm");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin.cfm", f);

		// Video
		f = contextDir.getRealResource("swfobject.js");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/video/swfobject.js", f);
		f = contextDir.getRealResource("swfobject.js.cfm");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/video/swfobject.js.cfm", f);

		f = contextDir.getRealResource("mediaplayer.swf");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/video/mediaplayer.swf", f);
		f = contextDir.getRealResource("mediaplayer.swf.cfm");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/video/mediaplayer.swf.cfm", f);

		Resource adminDir = contextDir.getRealResource("admin");
		if (!adminDir.exists())
			adminDir.mkdirs();

		// Plugin
		Resource pluginDir = adminDir.getRealResource("plugin");
		if (!pluginDir.exists())
			pluginDir.mkdirs();

		f = pluginDir.getRealResource("Plugin.cfc");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin/plugin/Plugin.cfc", f);

		// Plugin DDNS
		/*
		 * File ddns = new File(pluginDir,"DDNS");
		 * if(!ddns.exists())ddns.mkdirs();
		 * 
		 * f=new File(ddns,"language.xml");
		 * if(!f.exists())createFileFromResource
		 * ("/resource/context/admin/plugin/DDNS/language.xml",f);
		 * 
		 * f=new File(ddns,"overview.cfm");
		 * if(!f.exists())createFileFromResource
		 * ("/resource/context/admin/plugin/DDNS/overview.cfm",f);
		 * 
		 * f=new File(ddns,"Action.cfc"); if(!f.exists())createFileFromResource(
		 * "/resource/context/admin/plugin/DDNS/Action.cfc",f);
		 */

		// Plugin Simon
		Resource simon = pluginDir.getRealResource("Simon");
		if (!simon.exists())
			simon.mkdirs();

		f = simon.getRealResource("language.xml");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin/plugin/Simon/language.xml", f);

		f = simon.getRealResource("overview.cfm");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin/plugin/Simon/overview.cfm", f);

		f = simon.getRealResource("simon.swf.cfm");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin/plugin/Simon/simon.swf.cfm", f);

		f = simon.getRealResource("Action.cfc");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin/plugin/Simon/Action.cfc", f);

		// Plugin Note
		Resource note = pluginDir.getRealResource("Note");
		if (!note.exists())
			note.mkdirs();

		f = note.getRealResource("language.xml");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin/plugin/Note/language.xml", f);

		f = note.getRealResource("overview.cfm");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin/plugin/Note/overview.cfm", f);

		f = note.getRealResource("Action.cfc");
		if (!f.exists())
			createFileFromResourceEL("/resource/context/admin/plugin/Note/Action.cfc", f);

		// gateway
		Resource componentsDir = configDir.getRealResource("components");
		if (!componentsDir.exists())
			componentsDir.mkdirs();

		Resource gwDir = componentsDir.getRealResource("railo/extension/gateway/");
		create("/resource/context/gateway/",new String[]{
		"TaskGateway.cfc","DummyGateway.cfc","DirectoryWatcher.cfc","DirectoryWatcherListener.cfc","MailWatcher.cfc","MailWatcherListener.cfc"
				},gwDir,doNew);

		// resources/language
		Resource langDir = adminDir.getRealResource("resources/language");
		create("/resource/context/admin/resources/language/",new String[]{
				"en.xml","de.xml"
						},langDir,doNew);

		

		// delete Debug
		Resource debug = adminDir.getRealResource("debug");
		delete(debug,new String[]{
				"Classic.cfc","Modern.cfc","Comment.cfc"
				});
		
		// add Debug
		create("/resource/context/admin/debug/",new String[]{
				"Debug.cfc","Field.cfc","Group.cfc"
				},debug,doNew);
		
		// delete Cache Driver
		Resource cDir = adminDir.getRealResource("cdriver");
		delete(cDir,new String[]{
		"RamCache.cfc","EHCacheLite.cfc","EHCache.cfc"
		});
		
		// add Cache Drivers
		create("/resource/context/admin/cdriver/",new String[]{
		"Cache.cfc","Field.cfc","Group.cfc"}
		,cDir,doNew);
		
		// delete DB Drivers
		Resource dbDir = adminDir.getRealResource("dbdriver");
		delete(dbDir,new String[]{
		"HSQLDB.cfc","H2.cfc","MSSQL.cfc","MSSQL2.cfc","DB2.cfc","Oracle.cfc","MySQL.cfc","ODBC.cfc","Sybase.cfc"
		,"PostgreSql.cfc","Other.cfc","Firebird.cfc","Driver.cfc"
		});
		
		// add DB Drivers types
		Resource typesDir = dbDir.getRealResource("types");
		create("/resource/context/admin/dbdriver/types/",new String[]{
		"IDriver.cfc","Driver.cfc","IDatasource.cfc","IDriverSelector.cfc","Field.cfc"
		},typesDir,doNew);
		
		// delete Gateway Drivers
		Resource gDir = adminDir.getRealResource("gdriver");
		delete(gDir,new String[]{
		"TaskGatewayDriver.cfc","DirectoryWatcher.cfc","MailWatcher.cfc"
		});
		
		// add Gateway Drivers
		create("/resource/context/admin/gdriver/",new String[]{
		"Gateway.cfc","Field.cfc","Group.cfc"}
		,gDir,doNew);
		

		// add Logging/appender
		Resource app = adminDir.getRealResource("logging/appender");
		create("/resource/context/admin/logging/appender/",new String[]{
		"Appender.cfc","Field.cfc","Group.cfc"}
		,app,doNew);
		
		// Logging/layout
		Resource lay = adminDir.getRealResource("logging/layout");
		create("/resource/context/admin/logging/layout/",new String[]{
		"Layout.cfc","Field.cfc","Group.cfc"}
		,lay,doNew);
		
		
		
		
		
		Resource templatesDir = contextDir.getRealResource("templates");
		if (!templatesDir.exists())
			templatesDir.mkdirs();

		Resource errorDir = templatesDir.getRealResource("error");
		if (!errorDir.exists())
			errorDir.mkdirs();

		f = errorDir.getRealResource("error.cfm");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/context/templates/error/error.cfm", f);

		f = errorDir.getRealResource("error-neo.cfm");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/context/templates/error/error-neo.cfm", f);

		f = errorDir.getRealResource("error-public.cfm");
		if (!f.exists() || doNew)
			createFileFromResourceEL("/resource/context/templates/error/error-public.cfm", f);

		Resource displayDir = templatesDir.getRealResource("display");
		if (!displayDir.exists())
			displayDir.mkdirs();

		//f = displayDir.getRealResource(Constants.APP_CFM);
		//if (!f.exists() || doNew)
		//	createFileFromResourceEL("/resource/context/templates/display/Application.cfm", f);

		//f = displayDir.getRealResource(Constants.APP_CFC);
		//if (!f.exists() || doNew)
		//	createFileFromResourceEL("/resource/context/templates/display/Application.cfc", f);

		Resource lib = ResourceUtil.toResource(CFMLEngineFactory.getClassLoaderRoot(TP.class.getClassLoader()));
		f = lib.getRealResource("jfreechart-patch.jar");
		if (!f.exists())
			createFileFromResourceEL("/resource/lib/jfreechart-patch.jar", f);

	}


	public static void createContextFilesPost(Resource configDir, ConfigImpl config, ServletConfig servletConfig, boolean isEventGatewayContext, boolean doNew) {
		Resource contextDir = configDir.getRealResource("context");
		if (!contextDir.exists())
			contextDir.mkdirs();

		Resource adminDir = contextDir.getRealResource("admin");
		if (!adminDir.exists())
			adminDir.mkdirs();

		// Plugin
		Resource pluginDir = adminDir.getRealResource("plugin");
		if (!pluginDir.exists())
			pluginDir.mkdirs();

		// deploy org.railo.cfml components
		if (config instanceof ConfigWeb) {
			ImportDefintion _import = config.getComponentDefaultImport();
			String path = _import.getPackageAsPath();
			Resource components = config.getConfigDir().getRealResource("components");
			Resource dir = components.getRealResource(path);
			dir.mkdirs();
			// print.o(dir);
			ComponentFactory.deploy(dir, doNew);
		}

		// flex
		if (!isEventGatewayContext && servletConfig != null && config.getAMFConfigType() == ConfigImpl.AMF_CONFIG_TYPE_XML) {
			String strPath = servletConfig.getServletContext().getRealPath("/WEB-INF");
			Resource webInf = ResourcesImpl.getFileResourceProvider().getResource(strPath);

			Resource flex = webInf.getRealResource("flex");
			if (!flex.exists())
				flex.mkdirs();

			Resource f = flex.getRealResource("messaging-config.xml");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/flex/messaging-config.xml", f);
			f = flex.getRealResource("proxy-config.xml");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/flex/proxy-config.xml", f);
			f = flex.getRealResource("remoting-config.xml");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/flex/remoting-config.xml", f);
			f = flex.getRealResource("services-config.xml");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/flex/services-config.xml", f);

		}

	}



	private static void doCheckChangesInLibraries(ConfigWebImpl config) {
		// create current hash from libs
		TagLib[] tlds = config.getTLDs();
		FunctionLib[] flds = config.getFLDs();

		// charset
		StringBuilder sb = new StringBuilder(config.getTemplateCharset());
		sb.append(';');

		// dot notation upper case
		_getDotNotationUpperCase(sb,config.getMappings());
		_getDotNotationUpperCase(sb,config.getMappings());
		_getDotNotationUpperCase(sb,config.getCustomTagMappings());
		_getDotNotationUpperCase(sb,config.getComponentMappings());
		_getDotNotationUpperCase(sb,config.getFunctionMapping());
		_getDotNotationUpperCase(sb,config.getServerFunctionMapping());
		_getDotNotationUpperCase(sb,config.getTagMapping());
		_getDotNotationUpperCase(sb,config.getServerTagMapping());
		
		// suppress ws before arg
		sb.append(config.getSuppressWSBeforeArg());
		sb.append(';');

		// full null support
		sb.append(config.getFullNullSupport());
		sb.append(';');

		// tld
		for (int i = 0; i < tlds.length; i++) {
			sb.append(tlds[i].getHash());
		}

		// fld
		for (int i = 0; i < flds.length; i++) {
			sb.append(flds[i].getHash());
		}

		boolean hasChanged = false;
		try {
			String hashValue = Md5.getDigestAsString(sb.toString());

			// check and compare lib version file
			Resource contextDir = config.getConfigDir();
			Resource libHash = contextDir.getRealResource("lib-hash");

			if (!libHash.exists()) {
				libHash.createNewFile();
				IOUtil.write(libHash, hashValue, SystemUtil.getCharset(), false);

				hasChanged = true;
			}
			else if (!IOUtil.toString(libHash, SystemUtil.getCharset()).equals(hashValue)) {
				IOUtil.write(libHash, hashValue, SystemUtil.getCharset(), false);
				hasChanged = true;
			}
		}
		catch (IOException e) {
		}
		// chnage Compile type
		if (hasChanged) {
			try {
				config.getDeployDirectory().remove(true);
			}
			catch (IOException e) {
				e.printStackTrace(config.getErrWriter());
			}
		}

	}

	private static void _getDotNotationUpperCase(StringBuilder sb, Mapping... mappings) {
		for(int i=0;i<mappings.length;i++){
			sb.append(((MappingImpl)mappings[i]).getDotNotationUpperCase()).append(';');
		}
	}

	/**
	 * load mapings from XML Document
	 * 
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws IOException
	 */
	private static void loadMappings(ConfigServerImpl configServer, ConfigImpl config, Document doc, int mode) throws IOException {
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_MAPPING);
		Element el = getChildByName(doc.getDocumentElement(), "mappings");
		Element[] _mappings = getChildren(el, "mapping");

		Map<String,Mapping> mappings = MapFactory.<String,Mapping>getConcurrentMap();
		Mapping tmp;

		if (configServer != null && config instanceof ConfigWeb) {
			Mapping[] sm = configServer.getMappings();
			for (int i = 0; i < sm.length; i++) {
				if (!sm[i].isHidden()) {
					if (sm[i] instanceof MappingImpl) {
						tmp = ((MappingImpl) sm[i]).cloneReadOnly(config);
						mappings.put(tmp.getVirtualLowerCase(), tmp);

					}
					else {
						tmp = sm[i];
						mappings.put(tmp.getVirtualLowerCase(), tmp);
					}
				}
			}
		}

		boolean finished = false;

		if (hasAccess) {
			boolean hasRailoServerContext=false;
			for (int i = 0; i < _mappings.length; i++) {
				el = _mappings[i];

				// File
				// physical=getDir(sc,el.getAttribute("physical"),null,configDir);
				// File
				// archive=getFile(sc,el.getAttribute("archive"),null,configDir);

				String physical = el.getAttribute("physical");
				String archive = el.getAttribute("archive");
				String virtual = el.getAttribute("virtual");
				String listType = el.getAttribute("listener-type");
				String listMode = el.getAttribute("listener-mode");
				boolean readonly = toBoolean(el.getAttribute("readonly"), false);
				boolean hidden = toBoolean(el.getAttribute("hidden"), false);
				boolean toplevel = toBoolean(el.getAttribute("toplevel"), true);
				int clMaxEl = toInt(el.getAttribute("classloader-max-elements"), 100);

				if(config instanceof ConfigServer && virtual.equalsIgnoreCase("/railo-server-context/")) {
					hasRailoServerContext=true;
				}

				// railo-context
				if (virtual.equalsIgnoreCase("/railo-context/")) {
					if (StringUtil.isEmpty(listType, true))
						listType = "modern";
					if (StringUtil.isEmpty(listMode, true))
						listMode = "curr2root";
					toplevel = true;
				}

				ApplicationListener listener = ConfigWebUtil.loadListener(listType, null);
				int listenerMode = ConfigWebUtil.toListenerMode(listMode, -1);
				if (listener != null || listenerMode != -1) {
					// type
					if (mode == ConfigImpl.MODE_STRICT)
						listener = new ModernAppListener();
					else if (listener == null)
						listener = ConfigWebUtil.loadListener(config.getApplicationListener().getType(), null);
					if (listener == null)// this should never be true
						listener = new ModernAppListener();

					// mode
					if (listenerMode == -1) {
						listenerMode = config.getApplicationListener().getMode();
					}
					listener.setMode(listenerMode);

				}

				// physical!=null &&
				if ((physical != null || archive != null)) {
					
					short insTemp=inspectTemplate(el);
					if("/railo-context/".equalsIgnoreCase(virtual) || "/railo-context".equalsIgnoreCase(virtual) ||
						"/railo-server-context/".equalsIgnoreCase(virtual) || "/railo-server-context".equalsIgnoreCase(virtual))
						insTemp=ConfigImpl.INSPECT_ONCE;
					//boolean trusted = toBoolean(el.getAttribute("trusted"), false);
					
					
					String primary = el.getAttribute("primary");
					boolean physicalFirst = primary == null || !primary.equalsIgnoreCase("archive");

					tmp = new MappingImpl(config, virtual, physical, archive, insTemp, physicalFirst, hidden, readonly, toplevel, false, false, listener, clMaxEl);
					mappings.put(tmp.getVirtualLowerCase(), tmp);
					if (virtual.equals("/")) {
						finished = true;
						// break;
					}
				}
			}
			
			// set default railo-server-context
			if(config instanceof ConfigServer && !hasRailoServerContext) {
				ApplicationListener listener = ConfigWebUtil.loadListener("modern", null);
				listener.setMode(ApplicationListener.MODE_CURRENT2ROOT);
				
				tmp = new MappingImpl(config, "/railo-server-context", "{railo-server}/context/", null, ConfigImpl.INSPECT_ONCE, true, false, true, true, false, false, listener, 100);
				mappings.put(tmp.getVirtualLowerCase(), tmp);
			}
		}


		if ( !finished ) {

			if ( (config instanceof ConfigWebImpl) && ResourceUtil.isUNCPath( config.getRootDirectory().getPath() ) ) {

				tmp = new MappingImpl( config, "/", config.getRootDirectory().getPath(), null, ConfigImpl.INSPECT_UNDEFINED, true, true, true, true, false, false, null );
			} else {

				tmp = new MappingImpl( config, "/", "/", null, ConfigImpl.INSPECT_UNDEFINED, true, true, true, true, false, false, null );
			}

			mappings.put( "/", tmp );
		}

		Mapping[] arrMapping = new Mapping[mappings.size()];
		int index = 0;
		Iterator it = mappings.keySet().iterator();
		while (it.hasNext()) {
			arrMapping[index++] = mappings.get(it.next());
		}
		config.setMappings(arrMapping);
		// config.setMappings((Mapping[]) mappings.toArray(new
		// Mapping[mappings.size()]));
	}

	private static short inspectTemplate(Element el) {
		String strInsTemp = el.getAttribute("inspect-template");
		if(StringUtil.isEmpty(strInsTemp)) strInsTemp = el.getAttribute("inspect");
		if(StringUtil.isEmpty(strInsTemp)) {
			Boolean trusted=Caster.toBoolean(el.getAttribute("trusted"),null);
			if(trusted!=null) {
				if(trusted.booleanValue()) return ConfigImpl.INSPECT_NEVER;
				return ConfigImpl.INSPECT_ALWAYS;
			}
			return ConfigImpl.INSPECT_UNDEFINED;
		}
		return ConfigWebUtil.inspectTemplate(strInsTemp,ConfigImpl.INSPECT_UNDEFINED);	
	}

	private static void loadRest(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		boolean hasAccess = true;// MUST
									// ConfigWebUtil.hasAccess(config,SecurityManager.TYPE_REST);
		boolean hasCS = configServer != null;
		Element el = getChildByName(doc.getDocumentElement(), "rest");

		// list
		Boolean list = Caster.toBoolean(el.getAttribute("list"), null);
		if (list != null) {
			config.setRestList(list.booleanValue());
		}
		else if (hasCS) {
			config.setRestList(configServer.getRestList());
		}

		Element[] _mappings = getChildren(el, "mapping");

		// first get mapping defined in server admin (read-only)
		Map<String, railo.runtime.rest.Mapping> mappings = new HashMap<String, railo.runtime.rest.Mapping>();
		railo.runtime.rest.Mapping tmp;
		if (configServer != null && config instanceof ConfigWeb) {
			railo.runtime.rest.Mapping[] sm = configServer.getRestMappings();
			for (int i = 0; i < sm.length; i++) {

				if (!sm[i].isHidden()) {
					tmp = sm[i].duplicate(config, Boolean.TRUE);
					mappings.put(tmp.getVirtual(), tmp);
				}
			}
		}

		// get current mappings
		if (hasAccess) {
			for (int i = 0; i < _mappings.length; i++) {
				el = _mappings[i];
				String physical = el.getAttribute("physical");
				String virtual = el.getAttribute("virtual");
				boolean readonly = toBoolean(el.getAttribute("readonly"), false);
				boolean hidden = toBoolean(el.getAttribute("hidden"), false);
				boolean _default = toBoolean(el.getAttribute("default"), false);
				if (physical != null) {
					tmp = new railo.runtime.rest.Mapping(config, virtual, physical, hidden, readonly, _default);
					mappings.put(tmp.getVirtual(), tmp);
				}
			}
		}

		config.setRestMappings(mappings.values().toArray(new railo.runtime.rest.Mapping[mappings.size()]));
	}

	private static void loadFlex(ConfigServerImpl configServer, ConfigImpl config, Document doc) {

		Element el = getChildByName(doc.getDocumentElement(), "flex");
		if (configServer != null)
			;

		// deploy
		String strConfig = el.getAttribute("configuration");
		if (!StringUtil.isEmpty(strConfig))
			config.setAMFConfigType(strConfig);
		else if (configServer != null)
			config.setAMFConfigType(configServer.getAMFConfigType());

		// caster
		String strCaster = el.getAttribute("caster");
		if (StringUtil.isEmpty(strCaster))
			strCaster = el.getAttribute("caster-class");

		// arguments
		String strArgs = el.getAttribute("caster-arguments");
		if (StringUtil.isEmpty(strArgs))
			strArgs = el.getAttribute("caster-class-arguments");
		toArguments(strArgs, false);

		if (!StringUtil.isEmpty(strCaster))
			config.setAMFCaster(strCaster, toArguments(strArgs, false));
		else if (configServer != null)
			config.setAMFCaster(config.getAMFCasterClass(), config.getAMFCasterArguments());

	}
	
	private static void loadLoggers(ConfigServerImpl configServer, ConfigImpl config, Document doc, boolean isReload) {
		
		Element parent = getChildByName(doc.getDocumentElement(), "logging");
		Element[] children = getChildren(parent, "logger");
		Element child;
		String name,appender,appenderArgs,layout,layoutArgs;
		Level level=Level.ERROR;
		boolean readOnly=false;
		for(int i=0;i<children.length;i++){
			child=children[i];
			name=StringUtil.trim(child.getAttribute("name"),"");
			appender=StringUtil.trim(child.getAttribute("appender"),"");
			appenderArgs=StringUtil.trim(child.getAttribute("appender-arguments"),"");
			layout=StringUtil.trim(child.getAttribute("layout"),"");
			layoutArgs=StringUtil.trim(child.getAttribute("layout-arguments"),"");
			level=Log4jUtil.toLevel(StringUtil.trim(child.getAttribute("level"),""),Level.ERROR);
			readOnly=Caster.toBooleanValue(child.getAttribute("read-only"),false);
			// ignore when no appender/name is defined
			if(!StringUtil.isEmpty(appender) && !StringUtil.isEmpty(name)) {
				Map<String, String> appArgs = toArguments(appenderArgs, true,true);
				if(!StringUtil.isEmpty(layout)) {
					Map<String, String> layArgs = toArguments(layoutArgs, true,true);
					config.addLogger(name,level,appender,appArgs,layout,layArgs,readOnly);
				}
				else
					config.addLogger(name,level,appender,appArgs,null,null,readOnly);
			}
		}
		
		if(configServer != null) {
			Iterator<Entry<String, LoggerAndSourceData>> it = configServer.getLoggers().entrySet().iterator();
			Entry<String, LoggerAndSourceData> e;
			LoggerAndSourceData data;
			while(it.hasNext()){
				e = it.next();
				
				// logger only exists in server context
				if(config.getLog(e.getKey(),false)==null) {
					data = e.getValue();
					config.addLogger(e.getKey(), data.getLevel(), 
							data.getAppenderName(), data.getAppenderArgs(), 
							data.getLayoutName(), data.getLayoutArgs(),true);
				}
			}
			
		}
		
	}

	private static void loadExeLog(ConfigServerImpl configServer, ConfigImpl config, Document doc) {

		boolean hasServer = configServer != null;

		Element el = getChildByName(doc.getDocumentElement(), "execution-log");

		// enabled
		Boolean bEnabled = Caster.toBoolean(el.getAttribute("enabled"), null);
		if (bEnabled == null) {
			if (hasServer)
				config.setExecutionLogEnabled(configServer.getExecutionLogEnabled());
		}
		else
			config.setExecutionLogEnabled(bEnabled.booleanValue());

		boolean hasChanged = false;
		String val = Caster.toString(config.getExecutionLogEnabled());
		try {
			Resource contextDir = config.getConfigDir();
			Resource exeLog = contextDir.getRealResource("exe-log");

			if (!exeLog.exists()) {
				exeLog.createNewFile();
				IOUtil.write(exeLog, val, SystemUtil.getCharset(), false);
				hasChanged = true;
			}
			else if (!IOUtil.toString(exeLog, SystemUtil.getCharset()).equals(val)) {
				IOUtil.write(exeLog, val, SystemUtil.getCharset(), false);
				hasChanged = true;
			}
		}
		catch (IOException e) {
			e.printStackTrace(config.getErrWriter());
		}

		if (hasChanged) {
			try {
				if (config.getDeployDirectory().exists())
					config.getDeployDirectory().remove(true);
			}
			catch (IOException e) {
				e.printStackTrace(config.getErrWriter());
			}
		}

		// class
		String strClass = el.getAttribute("class");
		Class clazz;
		if (!StringUtil.isEmpty(strClass)) {
			try {
				if ("console".equalsIgnoreCase(strClass))
					clazz = ConsoleExecutionLog.class;
				else {
					Class c = ClassUtil.loadClass(strClass);
					if ((c.newInstance() instanceof ExecutionLog)) {
						clazz = c;
					}
					else {
						clazz = ConsoleExecutionLog.class;
						SystemOut.printDate(config.getErrWriter(), "class [" + strClass + "] must implement the interface " + ExecutionLog.class.getName());
					}
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
				clazz = ConsoleExecutionLog.class;
			}
			if (clazz != null)
				SystemOut.printDate(config.getOutWriter(), "loaded ExecutionLog class " + clazz.getName());

			// arguments
			String strArgs = el.getAttribute("arguments");
			if (StringUtil.isEmpty(strArgs))
				strArgs = el.getAttribute("class-arguments");
			Map<String, String> args = toArguments(strArgs, true);

			config.setExecutionLogFactory(new ExecutionLogFactory(clazz, args));
		}
		else {
			if (hasServer)
				config.setExecutionLogFactory(configServer.getExecutionLogFactory());
			else
				config.setExecutionLogFactory(new ExecutionLogFactory(ConsoleExecutionLog.class, new HashMap<String, String>()));
		}
	}

	/**
	 * loads and sets the Page Pool
	 * 
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadPagePool(ConfigServer configServer, Config config, Document doc) {
		// TODO xml configuration f￼r das erstellen
		// config.setPagePool( new PagePool(10000,1000));
	}

	/**
	 * loads datasource settings from XMl DOM
	 * 
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws ClassNotFoundException
	 */
	private static void loadDataSources(ConfigServerImpl configServer, ConfigImpl config, Document doc) throws ClassException {

		// When set to true, makes JDBC use a representation for DATE data that
		// is compatible with the Oracle8i database.
		System.setProperty("oracle.jdbc.V8Compatible", "true");

		boolean hasCS = configServer != null;
		Map<String, DataSource> datasources = new HashMap<String, DataSource>();

		// Copy Parent datasources as readOnly
		if (hasCS) {
			Map<String, DataSource> ds = configServer.getDataSourcesAsMap();
			Iterator<Entry<String, DataSource>> it = ds.entrySet().iterator();
			Entry<String, DataSource> entry;
			while (it.hasNext()) {
				entry = it.next();
				if (!entry.getKey().equals(QOQ_DATASOURCE_NAME))
					datasources.put(entry.getKey(), entry.getValue().cloneReadOnly());
			}
		}

		// TODO support H2
		// Default query of query DB
		/*
		 * setDatasource(datasources, QOQ_DATASOURCE_NAME, "org.h2.Driver" ,"" ,""
		 * ,-1 ,"jdbc:h2:.;MODE=HSQLDB" ,"sa" ,"" ,-1 ,-1 ,true ,true
		 * ,DataSource.ALLOW_ALL, new StructImpl() );
		 */
		// Default query of query DB
		setDatasource(config, datasources, QOQ_DATASOURCE_NAME, "org.hsqldb.jdbcDriver", "", "", -1, "jdbc:hsqldb:.", "sa", "", -1, -1, 60000, true, true, DataSource.ALLOW_ALL,
				false, false, null, new StructImpl(), "");

		SecurityManager sm = config.getSecurityManager();
		short access = sm.getAccess(SecurityManager.TYPE_DATASOURCE);
		int accessCount = -1;
		if (access == SecurityManager.VALUE_YES)
			accessCount = -1;
		else if (access == SecurityManager.VALUE_NO)
			accessCount = 0;
		else if (access >= SecurityManager.VALUE_1 && access <= SecurityManager.VALUE_10) {
			accessCount = access - SecurityManager.NUMBER_OFFSET;
		}

		// Databases
		Element databases = getChildByName(doc.getDocumentElement(), "data-sources");
		// if(databases==null)databases=doc.createElement("data-sources");

		// PSQ
		String strPSQ = databases.getAttribute("psq");
		if (StringUtil.isEmpty(strPSQ)) {
			// prior version was buggy, was the opposite
			strPSQ = databases.getAttribute("preserve-single-quote");
			if (!StringUtil.isEmpty(strPSQ)) {
				Boolean b = Caster.toBoolean(strPSQ, null);
				if (b != null)
					strPSQ = b.booleanValue() ? "false" : "true";
			}
		}
		if (access != SecurityManager.VALUE_NO && !StringUtil.isEmpty(strPSQ)) {
			config.setPSQL(toBoolean(strPSQ, true));
		}
		else if (hasCS)
			config.setPSQL(configServer.getPSQL());

		// Data Sources
		Element[] dataSources = getChildren(databases, "data-source");
		if (accessCount == -1)
			accessCount = dataSources.length;
		if (dataSources.length < accessCount)
			accessCount = dataSources.length;

		// if(hasAccess) {
		for (int i = 0; i < accessCount; i++) {
			Element dataSource = dataSources[i];
			if (dataSource.hasAttribute("database")) {
				setDatasourceEL(config, datasources, dataSource.getAttribute("name"), dataSource.getAttribute("class"), dataSource.getAttribute("host"),
						dataSource.getAttribute("database"), toInt(dataSource.getAttribute("port"), -1), dataSource.getAttribute("dsn"), dataSource.getAttribute("username"),
						decrypt(dataSource.getAttribute("password")), toInt(dataSource.getAttribute("connectionLimit"), -1),
						toInt(dataSource.getAttribute("connectionTimeout"), -1), toLong(dataSource.getAttribute("metaCacheTimeout"), 60000),
						toBoolean(dataSource.getAttribute("blob"), true), toBoolean(dataSource.getAttribute("clob"), true),
						toInt(dataSource.getAttribute("allow"), DataSource.ALLOW_ALL), toBoolean(dataSource.getAttribute("validate"), false),
						toBoolean(dataSource.getAttribute("storage"), false), dataSource.getAttribute("timezone"), toStruct(dataSource.getAttribute("custom")), dataSource.getAttribute("dbdriver"));
			}
		}
		// }
		config.setDataSources(datasources);
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadCache(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		boolean hasCS = configServer != null;
		Map<String, CacheConnection> caches = new HashMap<String, CacheConnection>();

		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManagerImpl.TYPE_CACHE);
		// print.o("LOAD CACHE:"+hasAccess+":"+hasCS);

		Element eCache = getChildByName(doc.getDocumentElement(), "cache");

		// has changes

		String md5 = getMD5(eCache, hasCS ? configServer.getCacheMD5() : "");
		if (md5.equals(config.getCacheMD5()))
			return;
		config.setCacheMD5(md5);

		// default query
		String defaultResource = eCache.getAttribute("default-resource");
		if (hasAccess && !StringUtil.isEmpty(defaultResource)) {
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_RESOURCE, defaultResource);
		}
		else if (hasCS) {
			if (eCache.hasAttribute("default-resource"))
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_RESOURCE, "");
			else
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_RESOURCE, configServer.getCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_RESOURCE));
		}
		else
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_RESOURCE, "");

		// default function
		String defaultUDF = eCache.getAttribute("default-function");
		if (hasAccess && !StringUtil.isEmpty(defaultUDF)) {
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_FUNCTION, defaultUDF);
		}
		else if (hasCS) {
			if (eCache.hasAttribute("default-function"))
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_FUNCTION, "");
			else
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_FUNCTION, configServer.getCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_FUNCTION));
		}
		else
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_FUNCTION, "");

		// default include
		String defaultInclude = eCache.getAttribute("default-include");
		if (hasAccess && !StringUtil.isEmpty(defaultInclude)) {
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_INCLUDE, defaultInclude);
		}
		else if (hasCS) {
			if (eCache.hasAttribute("default-include"))
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_INCLUDE, "");
			else
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_INCLUDE, configServer.getCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_INCLUDE));
		}
		else
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_INCLUDE, "");

		// default query
		String defaultQuery = eCache.getAttribute("default-query");
		if (hasAccess && !StringUtil.isEmpty(defaultQuery)) {
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_QUERY, defaultQuery);
		}
		else if (hasCS) {
			if (eCache.hasAttribute("default-query"))
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_QUERY, "");
			else
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_QUERY, configServer.getCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_QUERY));
		}
		else
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_QUERY, "");

		// default template
		String defaultTemplate = eCache.getAttribute("default-template");
		if (hasAccess && !StringUtil.isEmpty(defaultTemplate)) {
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_TEMPLATE, defaultTemplate);
		}
		else if (hasCS) {
			if (eCache.hasAttribute("default-template"))
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_TEMPLATE, "");
			else
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_TEMPLATE, configServer.getCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_TEMPLATE));
		}
		else
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_TEMPLATE, "");

		// default object
		String defaultObject = eCache.getAttribute("default-object");
		if (hasAccess && !StringUtil.isEmpty(defaultObject)) {
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_OBJECT, defaultObject);
		}
		else if (hasCS) {
			if (eCache.hasAttribute("default-object"))
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_OBJECT, "");
			else
				config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_OBJECT, configServer.getCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_OBJECT));
		}
		else
			config.setCacheDefaultConnectionName(ConfigImpl.CACHE_DEFAULT_OBJECT, "");

		// cache connections
		Element[] eConnections = getChildren(eCache, "connection");

		// if(hasAccess) {
		String name, clazzName;
		CacheConnection cc;
		Class cacheClazz;
		// caches
		if (hasAccess)
			for (int i = 0; i < eConnections.length; i++) {
				Element eConnection = eConnections[i];
				name = eConnection.getAttribute("name");
				clazzName = eConnection.getAttribute("class");
				if (clazzName != null)
					clazzName = clazzName.trim();

				//
				try {
					Struct custom = toStruct(eConnection.getAttribute("custom"));
					
					// Workaround for old EHCache class defintions
					if ("railo.extension.io.cache.eh.EHCacheLite".equals(clazzName)
							|| "railo.runtime.cache.eh.EHCacheLite".equals(clazzName)) {
						cacheClazz = EHCache.class;
						if(!custom.containsKey("distributed")) 
							custom.setEL("distributed", "off");
						if(!custom.containsKey("asynchronousReplicationIntervalMillis")) 
							custom.setEL("asynchronousReplicationIntervalMillis", "1000");
						if(!custom.containsKey("maximumChunkSizeBytes")) 
							custom.setEL("maximumChunkSizeBytes", "5000000");
						
						
					}
					else if ("railo.extension.io.cache.eh.EHCache".equals(clazzName))
						cacheClazz = EHCache.class;
					else
						cacheClazz = ClassUtil.loadClass(config.getClassLoader(), clazzName);

					
					
					
					
					cc = new CacheConnectionImpl(config, name, cacheClazz, custom, Caster.toBooleanValue(
							eConnection.getAttribute("read-only"), false), Caster.toBooleanValue(eConnection.getAttribute("storage"), false));
					if (!StringUtil.isEmpty(name)) {
						caches.put(name.toLowerCase(), cc);
					}
					else
						SystemOut.print(config.getErrWriter(), "missing cache name");

				}
				catch (ClassException ce) {
					SystemOut.print(config.getErrWriter(), ExceptionUtil.getStacktrace(ce, true));
				}
				catch (IOException e) {
					SystemOut.print(config.getErrWriter(), ExceptionUtil.getStacktrace(e, true));
				}
			}
		// }

		// call static init once per driver
		{
			// group by classes
			final Map<Class<?>,List<CacheConnection>> _caches = new HashMap<Class<?>,List<CacheConnection>>();
			{
				Iterator<Entry<String, CacheConnection>> it = caches.entrySet().iterator();
				Entry<String, CacheConnection> entry;
				List<CacheConnection> list;
				while (it.hasNext()) {
					entry = it.next();
					cc = entry.getValue();
					list = _caches.get(cc.getClazz());
					if (list == null) {
						list = new ArrayList<CacheConnection>();
						_caches.put(cc.getClazz(), list);
					}
					list.add(cc);
				}
			}
			// call
			Iterator<Entry<Class<?>, List<CacheConnection>>> it = _caches.entrySet().iterator();
			Entry<Class<?>, List<CacheConnection>> entry;
			Class<?> clazz;
			List<CacheConnection> list;
			while (it.hasNext()) {
				entry = it.next();
				list = entry.getValue();
				clazz = entry.getKey();
				try {
					Method m = clazz.getMethod("init", new Class[] { Config.class, String[].class, Struct[].class });
					if(Modifier.isStatic(m.getModifiers()))
						m.invoke(null, new Object[] { config, _toCacheNames(list), _toArguments(list) });
					else
						SystemOut.print(config.getErrWriter(), "method [init(Config,String[],Struct[]):void] for class [" + clazz.getName() + "] is not static");
					
				}
				catch (InvocationTargetException e) {
					e.getTargetException().printStackTrace();
				}
				catch (RuntimeException e) {
					e.printStackTrace();
				}
				catch (NoSuchMethodException e) {
					SystemOut.print(config.getErrWriter(), "missing method [public static init(Config,String[],Struct[]):void] for class [" + clazz.getName() + "] ");
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		// Copy Parent caches as readOnly
		if (hasCS) {
			Map<String, CacheConnection> ds = configServer.getCacheConnections();
			Iterator<Entry<String, CacheConnection>> it = ds.entrySet().iterator();
			Entry<String, CacheConnection> entry;
			while (it.hasNext()) {
				entry = it.next();
				cc = entry.getValue();
				if (!caches.containsKey(entry.getKey()))
					caches.put(entry.getKey(), new ServerCacheConnection(configServer, cc));
			}
		}
		config.setCaches(caches);
	}

	private static String getMD5(Node node, String parentMD5) {
		try {
			return MD5.getDigestAsString(XMLCaster.toString(node, "") + ":" + parentMD5);
		}
		catch (IOException e) {
			return "";
		}
	}

	private static void loadGatewayEL(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		try {
			loadGateway(configServer, config, doc);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static void loadGateway(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		boolean hasCS = configServer != null;
		if (!hasCS)
			return;
		ConfigWebImpl cw = (ConfigWebImpl) config;

		Map<String, GatewayEntry> mapGateways = new HashMap<String, GatewayEntry>();

		Element eGateWay = getChildByName(doc.getDocumentElement(), "gateways");

		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManagerImpl.TYPE_GATEWAY);

		GatewayEntry ge;

		// cache connections
		Element[] gateways = getChildren(eGateWay, "gateway");

		// if(hasAccess) {
		String id;
		GatewayEngineImpl engine = cw.getGatewayEngine();
		// engine.reset();

		// caches
		if (hasAccess) {
			for (int i = 0; i < gateways.length; i++) {
				Element eConnection = gateways[i];
				id = eConnection.getAttribute("id").trim().toLowerCase();

				ge = new GatewayEntryImpl(engine, id, eConnection.getAttribute("class"), eConnection.getAttribute("cfc-path"), eConnection.getAttribute("listener-cfc-path"),
						eConnection.getAttribute("startup-mode"), toStruct(eConnection.getAttribute("custom")), Caster.toBooleanValue(eConnection.getAttribute("read-only"), false));

				if (!StringUtil.isEmpty(id)) {
					mapGateways.put(id.toLowerCase(), ge);
				}
				else
					SystemOut.print(config.getErrWriter(), "missing id");
			}
			cw.setGatewayEntries(mapGateways);
		}
		else {
			cw.getGatewayEngine().clear();
		}
	}

	private static Struct[] _toArguments(List<CacheConnection> list) {
		Iterator<CacheConnection> it = list.iterator();
		Struct[] args = new Struct[list.size()];
		int index = 0;
		while (it.hasNext()) {
			args[index++] = it.next().getCustom();
		}
		return args;
	}

	private static String[] _toCacheNames(List<CacheConnection> list) {
		Iterator<CacheConnection> it = list.iterator();
		String[] names = new String[list.size()];
		int index = 0;
		while (it.hasNext()) {
			names[index++] = it.next().getName();
		}
		return names;
	}

	public static String decrypt(String str) {
		if (StringUtil.isEmpty(str) || !StringUtil.startsWithIgnoreCase(str, "encrypted:"))
			return str;
		str = str.substring(10);
		return new BlowfishEasy("sdfsdfs").decryptString(str);
	}

	public static String encrypt(String str) {
		if (StringUtil.isEmpty(str))
			return "";
		if (StringUtil.startsWithIgnoreCase(str, "encrypted:"))
			return str;
		return "encrypted:" + new BlowfishEasy("sdfsdfs").encryptString(str);
	}

	private static Struct toStruct(String str) {

		Struct sct = new StructImpl();
		try {
			String[] arr = ListUtil.toStringArray(ListUtil.listToArrayRemoveEmpty(str, '&'));

			String[] item;
			for (int i = 0; i < arr.length; i++) {
				item = ListUtil.toStringArray(ListUtil.listToArrayRemoveEmpty(arr[i], '='));
				if (item.length == 2)
					sct.setEL(KeyImpl.init(URLDecoder.decode(item[0], true).trim()), URLDecoder.decode(item[1], true));
				else if (item.length == 1)
					sct.setEL(KeyImpl.init(URLDecoder.decode(item[0], true).trim()), "");
			}
		}
		catch (PageException ee) {
		}

		return sct;
	}

	private static void setDatasource(ConfigImpl config, Map<String, DataSource> datasources, String datasourceName, String className, String server, String databasename,
			int port, String dsn, String user, String pass, int connectionLimit, int connectionTimeout, long metaCacheTimeout, boolean blob, boolean clob, int allow,
			boolean validate, boolean storage, String timezone, Struct custom, String dbdriver) throws ClassException {

		datasources.put( datasourceName.toLowerCase(),
				new DataSourceImpl(datasourceName, className, server, dsn, databasename, port, user, pass, connectionLimit, connectionTimeout, metaCacheTimeout, blob, clob, allow,
						custom, false, validate, storage, StringUtil.isEmpty(timezone, true) ? null : TimeZoneUtil.toTimeZone(timezone, null), dbdriver) );

	}

	private static void setDatasourceEL(ConfigImpl config, Map<String, DataSource> datasources, String datasourceName, String className, String server, String databasename,
			int port, String dsn, String user, String pass, int connectionLimit, int connectionTimeout, long metaCacheTimeout, boolean blob, boolean clob, int allow,
			boolean validate, boolean storage, String timezone, Struct custom, String dbdriver) {
		try {
			setDatasource(config, datasources, datasourceName, className, server, databasename, port, dsn, user, pass, connectionLimit, connectionTimeout, metaCacheTimeout, blob,
					clob, allow, validate, storage, timezone, custom, dbdriver);
		}
		catch (Throwable t) {
		}
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws IOException
	 */
	private static void loadCustomTagsMappings(ConfigServerImpl configServer, ConfigImpl config, Document doc, int mode) {

		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_CUSTOM_TAG);
		boolean hasCS = configServer != null;

		Element customTag = getChildByName(doc.getDocumentElement(), "custom-tag");
		Element[] ctMappings = getChildren(customTag, "mapping");
		// String virtualx="/custom-tag/";

		// do patch cache
		String strDoPathcache = customTag.getAttribute("use-cache-path");
		if (hasAccess && !StringUtil.isEmpty(strDoPathcache, true)) {
			config.setUseCTPathCache(Caster.toBooleanValue(strDoPathcache.trim(), true));
		}
		else if (hasCS) {
			config.setUseCTPathCache(configServer.useCTPathCache());
		}

		// do custom tag local search
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setDoLocalCustomTag(false);
		}
		else {
			String strDoCTLocalSearch = customTag.getAttribute("custom-tag-local-search");
			if (hasAccess && !StringUtil.isEmpty(strDoCTLocalSearch)) {
				config.setDoLocalCustomTag(Caster.toBooleanValue(strDoCTLocalSearch.trim(), true));
			}
			else if (hasCS) {
				config.setDoLocalCustomTag(configServer.doLocalCustomTag());
			}
		}

		// do custom tag deep search
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setDoCustomTagDeepSearch(false);
		}
		else {
			String strDoCTDeepSearch = customTag.getAttribute("custom-tag-deep-search");
			if (hasAccess && !StringUtil.isEmpty(strDoCTDeepSearch)) {
				config.setDoCustomTagDeepSearch(Caster.toBooleanValue(strDoCTDeepSearch.trim(), false));
			}
			else if (hasCS) {
				config.setDoCustomTagDeepSearch(configServer.doCustomTagDeepSearch());
			}
		}

		// extensions
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setCustomTagExtensions(new String[] { "cfc" });
		}
		else {
			String strExtensions = customTag.getAttribute("extensions");
			if (hasAccess && !StringUtil.isEmpty(strExtensions)) {
				try {
					String[] arr = ListUtil.toStringArray(ListUtil.listToArrayRemoveEmpty(strExtensions, ","));
					config.setCustomTagExtensions(ListUtil.trimItems(arr));
				}
				catch (PageException e) {
				}
			}
			else if (hasCS) {
				config.setCustomTagExtensions(configServer.getCustomTagExtensions());
			}
		}

		// Web Mapping
		boolean hasSet = false;
		Mapping[] mappings = null;
		if (hasAccess && ctMappings.length > 0) {
			mappings = new Mapping[ctMappings.length];
			for (int i = 0; i < ctMappings.length; i++) {
				Element ctMapping = ctMappings[i];
				String physical = ctMapping.getAttribute("physical");
				String archive = ctMapping.getAttribute("archive");
				boolean readonly = toBoolean(ctMapping.getAttribute("readonly"), false);
				boolean hidden = toBoolean(ctMapping.getAttribute("hidden"), false);
				//boolean trusted = toBoolean(ctMapping.getAttribute("trusted"), false);
				short inspTemp=inspectTemplate(ctMapping);
				int clMaxEl = toInt(ctMapping.getAttribute("classloader-max-elements"), 100);

				String primary = ctMapping.getAttribute("primary");

				boolean physicalFirst = archive == null || !primary.equalsIgnoreCase("archive");
				hasSet = true;
				mappings[i] = new MappingImpl(config, ConfigWebAdmin.createVirtual(ctMapping), physical, archive, inspTemp, physicalFirst, hidden, readonly, true, false, true,
						null, clMaxEl);
				// print.out(mappings[i].isPhysicalFirst());
			}

			config.setCustomTagMappings(mappings);

		}

		// Server Mapping
		if (hasCS) {
			Mapping[] originals = configServer.getCustomTagMappings();
			Mapping[] clones = new Mapping[originals.length];
			LinkedHashMap map = new LinkedHashMap();
			Mapping m;
			for (int i = 0; i < clones.length; i++) {
				m = ((MappingImpl) originals[i]).cloneReadOnly(config);
				map.put(toKey(m), m);
				// clones[i]=((MappingImpl)m[i]).cloneReadOnly(config);
			}

			if (mappings != null) {
				for (int i = 0; i < mappings.length; i++) {
					m = mappings[i];
					map.put(toKey(m), m);
				}
			}
			if (originals.length > 0) {
				clones = new Mapping[map.size()];
				Iterator it = map.entrySet().iterator();
				Map.Entry entry;
				int index = 0;
				while (it.hasNext()) {
					entry = (Entry) it.next();
					clones[index++] = (Mapping) entry.getValue();
					// print.out("c:"+clones[index-1]);
				}
				hasSet = true;
				// print.err("set:"+clones.length);

				config.setCustomTagMappings(clones);
			}
		}

		if (!hasSet) {
			// MappingImpl m=new
			// MappingImpl(config,"/default-customtags/","{railo-web}/customtags/",null,false,true,false,false,true,false,true);
			// config.setCustomTagMappings(new
			// Mapping[]{m.cloneReadOnly(config)});
		}

	}

	private static Object toKey(Mapping m) {
		if (!StringUtil.isEmpty(m.getStrPhysical(), true))
			return m.getStrPhysical().toLowerCase().trim();
		return (m.getStrPhysical() + ":" + m.getStrArchive()).toLowerCase();
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	private static void loadRailoConfig(ConfigServerImpl configServer, ConfigImpl config, Document doc) throws IOException  {
		Element railoConfiguration = doc.getDocumentElement();
		
		// salt (every context need to have a salt)
		String salt=railoConfiguration.getAttribute("salt");
		if(StringUtil.isEmpty(salt,true)) throw new RuntimeException("context is invalid, there is no salt!");
		config.setSalt(salt=salt.trim());
		
		
		
		// password
		Password pw=Password.getInstance(railoConfiguration,salt,false);
		if(pw!=null) 
			config.setPassword(pw);
		else if (configServer != null) 
			config.setPassword(configServer.getDefaultPassword());
		
		
		if(config instanceof ConfigServerImpl) {
			ConfigServerImpl csi=(ConfigServerImpl)config;
			String keyList=railoConfiguration.getAttribute("auth-keys");
			if(!StringUtil.isEmpty(keyList)) {
				String[] keys = ListUtil.trimItems(ListUtil.toStringArray(ListUtil.toListRemoveEmpty(keyList, ',')));
				for(int i=0;i<keys.length;i++){
					keys[i]=URLDecoder.decode(keys[i], "UTF-8", true);
				}
				
				csi.setAuthenticationKeys(keys);
			}
		}
		
		// api key
		String apiKey = railoConfiguration.getAttribute("api-key");
		if(!StringUtil.isEmpty(apiKey))
			config.setApiKey(apiKey);
		else if(configServer != null)
			config.setApiKey(configServer.getApiKey());
		else 
			config.setApiKey(null);
		
		// default password
		if (config instanceof ConfigServerImpl) {
			pw=Password.getInstance(railoConfiguration,salt,true);
			if(pw!=null)
				((ConfigServerImpl) config).setDefaultPassword(pw);
		}

		// mode
		String mode = railoConfiguration.getAttribute("mode");
		if (!StringUtil.isEmpty(mode, true)) {
			mode = mode.trim();
			if ("custom".equalsIgnoreCase(mode))
				config.setMode(ConfigImpl.MODE_CUSTOM);
			if ("strict".equalsIgnoreCase(mode))
				config.setMode(ConfigImpl.MODE_STRICT);
		}
		else if (configServer != null) {
			config.setMode(configServer.getMode());
		}

		// check config file for changes
		String cFc = railoConfiguration.getAttribute("check-for-changes");
		if (!StringUtil.isEmpty(cFc, true)) {
			config.setCheckForChangesInConfigFile(Caster.toBooleanValue(cFc.trim(), false));
		}
		else if (configServer != null) {
			config.setCheckForChangesInConfigFile(configServer.checkForChangesInConfigFile());
		}
	}

	/*
	 * private static void loadLabel(ConfigServerImpl configServer, ConfigImpl
	 * config, Document doc) { // do only for web config if(configServer!=null
	 * && config instanceof ConfigWebImpl) { ConfigWebImpl cs=(ConfigWebImpl)
	 * config; String hash=SystemUtil.hash(cs.getServletContext());
	 * config.setLabel(hash);
	 * 
	 * Map<String, String> labels = configServer.getLabels(); if(labels!=null) {
	 * String label = labels.get(hash); if(!StringUtil.isEmpty(label)) {
	 * print.o("label:"+label); config.setLabel(label);
	 * config.getFactory().setLabel(label); } } } }
	 */

	private static void loadTag(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		Element parent = getChildByName(doc.getDocumentElement(), "tags");
		
		
		{
			Element[] tags = getChildren(parent, "tag");
			Element tag;
	
			String nss, ns, n, c;
			if (tags != null) {
				for (int i = 0; i < tags.length; i++) {
					tag = tags[i];
					ns = tag.getAttribute("namespace");
					nss = tag.getAttribute("namespace-seperator");
					n = tag.getAttribute("name");
					c = tag.getAttribute("class");
					config.addTag(ns, nss, n, c);
				}
			}
		}
		
		// set tag default values
		Element[] defaults = getChildren(parent, "default");
		if(!ArrayUtil.isEmpty(defaults)){
			Element def;
			String tagName,attrName,attrValue;
			Struct tags=new StructImpl(),tag;
			Map<Key, Map<Key, Object>> trg=new HashMap<Key, Map<Key,Object>>();
			for (int i = 0; i < defaults.length; i++) {
				def = defaults[i];
				tagName = def.getAttribute("tag");
				attrName = def.getAttribute("attribute-name");
				attrValue = def.getAttribute("attribute-value");
				if(StringUtil.isEmpty(tagName) || StringUtil.isEmpty(attrName) || StringUtil.isEmpty(attrValue)) continue;
				
				tag=(Struct) tags.get(tagName,null);
				if(tag==null) {
					tag=new StructImpl();
					tags.setEL(tagName, tag);
				}
				tag.setEL(attrName, attrValue);
				ApplicationContextSupport.initTagDefaultAttributeValues(config, trg, tags);
				config.setTagDefaultAttributeValues(trg);
			}
			
			// initTagDefaultAttributeValues
			
			
		}
		

	}

	private static void loadTempDirectory(ConfigServerImpl configServer, ConfigImpl config, Document doc, boolean isReload) throws ExpressionException {
		Resource configDir = config.getConfigDir();
		boolean hasCS = configServer != null;

		Element fileSystem = getChildByName(doc.getDocumentElement(), "file-system");
		if (fileSystem == null)
			fileSystem = getChildByName(doc.getDocumentElement(), "filesystem");

		String strTempDirectory = null;
		if (fileSystem != null)
			strTempDirectory = ConfigWebUtil.translateOldPath(fileSystem.getAttribute("temp-directory"));

		Resource cst = null;
		// Temp Dir
		if (!StringUtil.isEmpty(strTempDirectory))
			cst = ConfigWebUtil.getFile(configDir, strTempDirectory, null, configDir, FileUtil.TYPE_DIR, config);

		if (cst == null && hasCS)
			cst = configServer.getTempDirectory();

		if (cst == null)
			cst = ConfigWebUtil.getFile(configDir, "temp", null, configDir, FileUtil.TYPE_DIR, config);

		config.setTempDirectory(cst, !isReload);

	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws ExpressionException
	 * @throws TagLibException
	 * @throws FunctionLibException
	 */
	private static void loadFilesystem(ConfigServerImpl configServer, ConfigImpl config, Document doc, boolean doNew) throws ExpressionException, TagLibException,
			FunctionLibException {

		if (configServer != null) {
			Resource src = configServer.getConfigDir().getRealResource("distribution");
			Resource trg = config.getConfigDir().getRealResource("context/");
			copyContextFiles(src, trg);
		}

		Resource configDir = config.getConfigDir();

		boolean hasCS = configServer != null;

		Element fileSystem = getChildByName(doc.getDocumentElement(), "file-system");
		if (fileSystem == null)
			fileSystem = getChildByName(doc.getDocumentElement(), "filesystem");

		String strAllowRelPath = null;
		String strDeployDirectory = null;
		// String strTempDirectory=null;
		String strTLDDirectory = null;
		String strFLDDirectory = null;
		String strTagDirectory = null;
		String strFunctionDirectory = null;

		if (fileSystem != null) {
			strAllowRelPath = ConfigWebUtil.translateOldPath(fileSystem.getAttribute("allow-relpath"));
			strDeployDirectory = ConfigWebUtil.translateOldPath(fileSystem.getAttribute("deploy-directory"));
			// strTempDirectory=ConfigWebUtil.translateOldPath(fileSystem.getAttribute("temp-directory"));
			strTLDDirectory = ConfigWebUtil.translateOldPath(fileSystem.getAttribute("tld-directory"));
			strFLDDirectory = ConfigWebUtil.translateOldPath(fileSystem.getAttribute("fld-directory"));
			strTagDirectory = ConfigWebUtil.translateOldPath(fileSystem.getAttribute("tag-directory"));
			strFunctionDirectory = ConfigWebUtil.translateOldPath(fileSystem.getAttribute("function-directory"));
		}
		if (StringUtil.isEmpty(strFLDDirectory))
			strFLDDirectory = "{railo-config}/library/fld/";
		if (StringUtil.isEmpty(strTLDDirectory))
			strTLDDirectory = "{railo-config}/library/tld/";
		if (StringUtil.isEmpty(strFunctionDirectory))
			strFunctionDirectory = "{railo-config}/library/function/";
		if (StringUtil.isEmpty(strTagDirectory))
			strTagDirectory = "{railo-config}/library/tag/";

		// Deploy Dir
		// gateway must run in server env
		// if(!(config instanceof ConfigServer)) {
		Resource dd = ConfigWebUtil.getFile(configDir, strDeployDirectory, "cfclasses", configDir, FileUtil.TYPE_DIR, config);
		config.setDeployDirectory(dd);
		// }

		// Temp Dir
		/*
		 * if(!StringUtil.isEmpty(strTempDirectory)) {
		 * config.setTempDirectory(ConfigWebUtil
		 * .getFile(configDir,strTempDirectory, null, // create no default
		 * configDir,FileUtil.TYPE_DIR,config)); } else if(hasCS) {
		 * config.setTempDirectory(configServer.getTempDirectory()); }
		 * if(config.getTempDirectory()==null) {
		 * config.setTempDirectory(ConfigWebUtil.getFile(configDir,"temp", null,
		 * // create no default configDir,FileUtil.TYPE_DIR,config)); }
		 */

		// TLD Dir
		// if(hasCS) {
		// config.setTldFile(configServer.getTldFile());
		// }
		if (strTLDDirectory != null) {
			Resource tld = ConfigWebUtil.getFile(config, configDir, strTLDDirectory, FileUtil.TYPE_DIR);
			// print.err(tld);
			if (tld != null)
				config.setTldFile(tld);
		}

		// Tag Directory
		if (strTagDirectory != null) {
			Resource dir = ConfigWebUtil.getFile(config, configDir, strTagDirectory, FileUtil.TYPE_DIR);
			createTagFiles(config, configDir, dir, doNew);
			if (dir != null) {
				config.setTagDirectory(dir);
			}
		}

		// allow relpath
		if (hasCS) {
			config.setAllowRelPath(configServer.allowRelPath());
		}
		if (!StringUtil.isEmpty(strAllowRelPath, true)) {
			config.setAllowRelPath(Caster.toBooleanValue(strAllowRelPath, true));
		}

		// FLD Dir
		// if(hasCS) {
		// config.setFldFile(configServer.getFldFile());
		// }
		if (strFLDDirectory != null) {
			Resource fld = ConfigWebUtil.getFile(config, configDir, strFLDDirectory, FileUtil.TYPE_DIR);
			if (fld != null)
				config.setFldFile(fld);
		}

		// Function Directory
		if (strFunctionDirectory != null) {
			Resource dir = ConfigWebUtil.getFile(config, configDir, strFunctionDirectory, FileUtil.TYPE_DIR);
			createFunctionFiles(config, configDir, dir, doNew);
			if (dir != null)
				config.setFunctionDirectory(dir);
		}

		/*
		 * / Function Dir if(strFunctionDirectory!=null) { Resource
		 * func=ConfigWebUtil
		 * .getFile(config,configDir,strFunctionDirectory,FileUtil.TYPE_DIR);
		 * if(func!=null) config.setFldFile(fld); }
		 */

	}

	private static void createTagFiles(Config config, Resource configDir, Resource dir, boolean doNew) {
		if (config instanceof ConfigServer) {

			// Dump
			create("/resource/library/tag/",new String[]{
					"Dump.cfc"
					},dir,doNew);
			
			/*Resource sub = dir.getRealResource("railo/dump/skins/");
			create("/resource/library/tag/railo/dump/skins/",new String[]{
					"classic.json","large.json","pastel.cfm"
					},sub,doNew);
			create("/resource/library/tag/railo/dump/skins/",new String[]{
					"text.cfm","simple.cfm","modern.cfm","classic.cfm","pastel.cfm"
					},sub,doNew);*/

			// MediaPlayer
			Resource f = dir.getRealResource("MediaPlayer.cfc");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/tag/MediaPlayer.cfc", f);
			Resource build = dir.getRealResource("build");
			if (!build.exists())
				build.mkdirs();
			String[] names = new String[] { "_background.png", "_bigplay.png", "_controls.png", "_loading.gif", "_player.swf", "_player.xap", "background_png.cfm",
					"bigplay_png.cfm", "controls_png.cfm", "jquery.js.cfm", "loading_gif.cfm", "mediaelement-and-player.min.js.cfm", "mediaelementplayer.min.css.cfm",
					"player.swf.cfm", "player.xap.cfm" };
			for (int i = 0; i < names.length; i++) {
				f = build.getRealResource(names[i]);
				if (!f.exists() || doNew)
					createFileFromResourceEL("/resource/library/tag/build/" + names[i], f);

			}

			// AJAX
			AjaxFactory.deployTags(dir, doNew);

		}
	}

	private static void createFunctionFiles(Config config, Resource configDir, Resource dir, boolean doNew) {

		if (config instanceof ConfigServer) {
			Resource f = dir.getRealResource("writeDump.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/writeDump.cfm", f);

			f = dir.getRealResource("dump.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/dump.cfm", f);

			f = dir.getRealResource("location.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/location.cfm", f);

			f = dir.getRealResource("threadJoin.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/threadJoin.cfm", f);

			f = dir.getRealResource("threadTerminate.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/threadTerminate.cfm", f);

			f = dir.getRealResource("throw.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/throw.cfm", f);

			f = dir.getRealResource("trace.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/trace.cfm", f);

			f = dir.getRealResource("queryExecute.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/queryExecute.cfm", f);

			
			f = dir.getRealResource("transactionCommit.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/transactionCommit.cfm", f);

			f = dir.getRealResource("transactionRollback.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/transactionRollback.cfm", f);

			f = dir.getRealResource("transactionSetsavepoint.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/transactionSetsavepoint.cfm", f);

			f = dir.getRealResource("writeLog.cfm");
			if (!f.exists() || doNew)
				createFileFromResourceEL("/resource/library/function/writeLog.cfm", f);

			AjaxFactory.deployFunctions(dir, doNew);

		}
	}

	private static void copyContextFiles(Resource src, Resource trg) {
		// directory
		if (src.isDirectory()) {
			if (trg.exists())
				trg.mkdirs();
			Resource[] children = src.listResources();
			for (int i = 0; i < children.length; i++) {
				copyContextFiles(children[i], trg.getRealResource(children[i].getName()));
			}
		}
		// file
		else if (src.isFile()) {
			if (src.lastModified() > trg.lastModified()) {
				try {
					if (trg.exists())
						trg.remove(true);
					trg.createFile(true);
					src.copyTo(trg, false);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadUpdate(ConfigServer configServer, Config config, Document doc) {

		// Server
		if (config instanceof ConfigServer) {
			ConfigServer cs = (ConfigServer) config;
			Element update = getChildByName(doc.getDocumentElement(), "update");

			if (update != null) {
				cs.setUpdateType(update.getAttribute("type"));
				cs.setUpdateLocation(update.getAttribute("location"), null);
			}
		}
	}

	private static void loadVideo(ConfigServerImpl configServer, ConfigImpl config, Document doc) throws ApplicationException {

		Element video = config instanceof ConfigServerImpl ? getChildByName(doc.getDocumentElement(), "video") : null;
		boolean hasCS = configServer != null;
		String str = null;

		// video-executer
		if (video != null) {
			str = video.getAttribute("video-executer-class");
			if (StringUtil.isEmpty(str))
				str = video.getAttribute("video-executer");
		}
		if (!StringUtil.isEmpty(str)) {
			try {
				Class clazz = ClassUtil.loadClass(config.getClassLoader(), str);
				if (!Reflector.isInstaneOf(clazz, VideoExecuter.class))
					throw new ApplicationException("class [" + clazz.getName() + "] does not implement interface [" + VideoExecuter.class.getName() + "]");
				config.setVideoExecuterClass(clazz);

			}
			catch (ClassException e) {
				e.printStackTrace();
			}
		}
		else if (hasCS)
			config.setVideoExecuterClass(configServer.getVideoExecuterClass());

	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadSetting(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);

		Element setting = hasAccess ? getChildByName(doc.getDocumentElement(), "setting") : null;
		boolean hasCS = configServer != null;
		String str = null;

		// suppress whitespace
		str = null;
		if (setting != null) {
			str = setting.getAttribute("suppress-content");
		}
		if (!StringUtil.isEmpty(str) && hasAccess) {
			config.setSuppressContent(toBoolean(str, false));
		}
		else if (hasCS)
			config.setSuppressContent(configServer.isSuppressContent());

		// CFML Writer
		if (setting != null) {
			str = setting.getAttribute("cfml-writer");
		}
		if (!StringUtil.isEmpty(str) && hasAccess) {
			if ("white-space".equalsIgnoreCase(str))
				config.setCFMLWriterType(ConfigImpl.CFML_WRITER_WS);
			else if ("white-space-pref".equalsIgnoreCase(str))
				config.setCFMLWriterType(ConfigImpl.CFML_WRITER_WS_PREF);
			else if ("regular".equalsIgnoreCase(str))
				config.setCFMLWriterType(ConfigImpl.CFML_WRITER_REFULAR);
			// FUTURE add support for classes implementing CFMLWriter interface
		}
		else if (hasCS)
			config.setCFMLWriterType(configServer.getCFMLWriterType());

		// No longer supported, replaced with code above
		// suppress whitespace
		/*
		 * str=null; if(setting!=null){
		 * str=setting.getAttribute("suppress-whitespace");
		 * if(StringUtil.isEmpty
		 * (str))str=setting.getAttribute("suppresswhitespace"); }
		 * if(!StringUtil.isEmpty(str) && hasAccess) {
		 * config.setSuppressWhitespace(toBoolean(str,false)); } else
		 * if(hasCS)config
		 * .setSuppressWhitespace(configServer.isSuppressWhitespace());
		 */

		// show version
		str = null;
		if (setting != null) {
			str = setting.getAttribute("show-version");
			if (StringUtil.isEmpty(str))
				str = setting.getAttribute("showversion");
		}
		if (!StringUtil.isEmpty(str) && hasAccess) {
			config.setShowVersion(toBoolean(str, true));
		}
		else if (hasCS)
			config.setShowVersion(configServer.isShowVersion());

		// close connection
		str = null;
		if (setting != null) {
			str = setting.getAttribute("close-connection");
			if (StringUtil.isEmpty(str))
				str = setting.getAttribute("closeconnection");
		}
		if (!StringUtil.isEmpty(str) && hasAccess) {
			config.setCloseConnection(toBoolean(str, false));
		}
		else if (hasCS)
			config.setCloseConnection(configServer.closeConnection());

		// content-length
		str = null;
		if (setting != null) {
			str = setting.getAttribute("content-length");
			if (StringUtil.isEmpty(str))
				str = setting.getAttribute("contentlength");
		}
		if (!StringUtil.isEmpty(str) && hasAccess) {
			config.setContentLength(toBoolean(str, true));
		}
		else if (hasCS)
			config.setContentLength(configServer.contentLength());

		// buffer-output
		str = null;
		if (setting != null) {
			str = setting.getAttribute("buffer-output");
			if (StringUtil.isEmpty(str))
				str = setting.getAttribute("bufferoutput");
		}
		Boolean b = Caster.toBoolean(str, null);
		if (b != null && hasAccess) {
			config.setBufferOutput(b.booleanValue());
		}
		else if (hasCS)
			config.setBufferOutput(configServer.getBufferOutput());

		// allow-compression
		str = null;
		if (setting != null) {
			str = setting.getAttribute("allow-compression");
			if (StringUtil.isEmpty(str))
				str = setting.getAttribute("allowcompression");
		}
		if (!StringUtil.isEmpty(str) && hasAccess) {
			config.setAllowCompression(toBoolean(str, true));
		}
		else if (hasCS)
			config.setAllowCompression(configServer.allowCompression());

	}

	private static void loadRemoteClient(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManagerImpl.TYPE_REMOTE);

		// SNSN
		// RemoteClientUsage

		// boolean hasCS=configServer!=null;
		Element _clients = getChildByName(doc.getDocumentElement(), "remote-clients");

		// usage
		String strUsage = _clients.getAttribute("usage");
		Struct sct;
		if (!StringUtil.isEmpty(strUsage))
			sct = toStruct(strUsage);// config.setRemoteClientUsage(toStruct(strUsage));
		else
			sct = new StructImpl();
		// TODO make this generic
		if (configServer != null) {
			String sync = Caster.toString(configServer.getRemoteClientUsage().get("synchronisation", ""), "");
			if (!StringUtil.isEmpty(sync)) {
				sct.setEL("synchronisation", sync);
			}
		}
		config.setRemoteClientUsage(sct);

		// max-threads
		int maxThreads = Caster.toIntValue(_clients.getAttribute("max-threads"), -1);
		if(maxThreads<1 && configServer!=null) {
			SpoolerEngineImpl engine = (SpoolerEngineImpl) configServer.getSpoolerEngine();
			if(engine!=null) maxThreads=engine.getMaxThreads();
		}
		if(maxThreads<1)maxThreads=20;

		// directory
		Resource file = ConfigWebUtil.getFile(config.getRootDirectory(), _clients.getAttribute("directory"), "client-task", config.getConfigDir(), FileUtil.TYPE_DIR, config);
		config.setRemoteClientDirectory(file);

		Element[] clients;
		Element client;

		if (!hasAccess)
			clients = new Element[0];
		else
			clients = getChildren(_clients, "remote-client");
		java.util.List<RemoteClient> list = new ArrayList<RemoteClient>();
		for (int i = 0; i < clients.length; i++) {
			client = clients[i];
			// type
			String type = client.getAttribute("type");
			if (StringUtil.isEmpty(type))
				type = "web";
			// url
			String url = client.getAttribute("url");
			String label = client.getAttribute("label");
			if (StringUtil.isEmpty(label))
				label = url;
			String sUser = client.getAttribute("server-username");
			String sPass = ConfigWebFactory.decrypt(client.getAttribute("server-password"));
			String aPass = ConfigWebFactory.decrypt(client.getAttribute("admin-password"));
			String aCode = ConfigWebFactory.decrypt(client.getAttribute("security-key"));
			// if(aCode!=null && aCode.indexOf('-')!=-1)continue;
			String usage = client.getAttribute("usage");
			if (usage == null)
				usage = "";

			String pUrl = client.getAttribute("proxy-server");
			int pPort = Caster.toIntValue(client.getAttribute("proxy-port"), -1);
			String pUser = client.getAttribute("proxy-username");
			String pPass = ConfigWebFactory.decrypt(client.getAttribute("proxy-password"));

			ProxyData pd = null;
			if (!StringUtil.isEmpty(pUrl, true)) {
				pd = new ProxyDataImpl();
				pd.setServer(pUrl);
				if (!StringUtil.isEmpty(pUser)) {
					pd.setUsername(pUser);
					pd.setPassword(pPass);
				}
				if (pPort > 0)
					pd.setPort(pPort);
			}
			list.add(new RemoteClientImpl(label, type, url, sUser, sPass, aPass, pd, aCode, usage));
		}
		if (list.size() > 0)
			config.setRemoteClients(list.toArray(new RemoteClient[list.size()]));
		else
			config.setRemoteClients(new RemoteClient[0]);

		// init spooler engine
		Resource dir = config.getRemoteClientDirectory();
		if (dir != null && !dir.exists())
			dir.mkdirs();
		if (config.getSpoolerEngine() == null) {
			config.setSpoolerEngine(new SpoolerEngineImpl(config, dir, "Remote Client Spooler", config.getLog("remoteclient"), maxThreads));
		}
		else {
			SpoolerEngineImpl engine = (SpoolerEngineImpl) config.getSpoolerEngine();
			engine.setConfig(config);
			engine.setLog(config.getLog("remoteclient"));
			engine.setPersisDirectory(dir);
			engine.setMaxThreads(maxThreads);

		}
	}

	private static void loadSystem(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);
		Element sys = hasAccess ? getChildByName(doc.getDocumentElement(), "system") : null;

		boolean hasCS = configServer != null;

		String out = null, err = null;
		if (sys != null) {
			out = sys.getAttribute("out");
			err = sys.getAttribute("err");
		}
		if (!StringUtil.isEmpty(out) && hasAccess) {
			config.setOut(toPrintwriter(config, out, false));
		}
		else if (hasCS)
			config.setOut(configServer.getOutWriter());

		if (!StringUtil.isEmpty(err) && hasAccess) {
			config.setErr(toPrintwriter(config, err, true));
		}
		else if (hasCS)
			config.setErr(configServer.getErrWriter());

	}

	private static PrintWriter toPrintwriter(ConfigImpl config, String streamtype, boolean iserror) {
		if (!StringUtil.isEmpty(streamtype)) {
			streamtype = streamtype.trim();

			if (streamtype.equalsIgnoreCase("null"))
				return new PrintWriter(DevNullOutputStream.DEV_NULL_OUTPUT_STREAM);
			else if (StringUtil.startsWithIgnoreCase(streamtype, "class:")) {
				String classname = streamtype.substring(6);
				try {
					return (PrintWriter) ClassUtil.loadInstance(classname);
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
			else if (StringUtil.startsWithIgnoreCase(streamtype, "file:")) {
				String strRes = streamtype.substring(5);
				try {
					strRes = ConfigWebUtil.translateOldPath(strRes);
					Resource res = ConfigWebUtil.getFile(config, config.getConfigDir(), strRes, ResourceUtil.TYPE_FILE);
					if (res != null)
						return new PrintWriter(res.getOutputStream(), true);
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}

		}
		if (iserror)
			return SystemUtil.getPrintWriter(SystemUtil.ERR);
		return SystemUtil.getPrintWriter(SystemUtil.OUT);
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadCharset(ConfigServer configServer, ConfigImpl config, Document doc) {

		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);

		Element charset = hasAccess ? getChildByName(doc.getDocumentElement(), "charset") : null;
		Element regional = hasAccess ? getChildByName(doc.getDocumentElement(), "regional") : null;
		Element fileSystem = hasAccess ? getChildByName(doc.getDocumentElement(), "file-system") : null;

		boolean hasCS = configServer != null;

		// template
		String template = null, fsCharset = null, fsEncoding = null;
		if (charset != null)
			template = charset.getAttribute("template-charset");
		if (fileSystem != null)
			fsCharset = fileSystem.getAttribute("charset"); // deprecated but
															// still supported
		if (fileSystem != null)
			fsEncoding = fileSystem.getAttribute("encoding"); // deprecated but
																// still
																// supported

		if (!StringUtil.isEmpty(template))
			config.setTemplateCharset(template);
		else if (!StringUtil.isEmpty(fsCharset))
			config.setTemplateCharset(fsCharset);
		else if (!StringUtil.isEmpty(fsEncoding))
			config.setTemplateCharset(fsEncoding);
		else if (hasCS)
			config.setTemplateCharset(configServer.getTemplateCharset());

		// web
		String web = null, defaultEncoding = null;
		if (charset != null)
			web = charset.getAttribute("web-charset");
		if (regional != null)
			defaultEncoding = regional.getAttribute("default-encoding"); // deprecated
																			// but
																			// still
																			// supported
		if (!StringUtil.isEmpty(web))
			config.setWebCharset(web);
		else if (!StringUtil.isEmpty(defaultEncoding))
			config.setWebCharset(defaultEncoding);
		else if (hasCS)
			config.setWebCharset(configServer.getWebCharset());

		// resource
		String resource = null;
		if (charset != null)
			resource = charset.getAttribute("resource-charset");
		if (!StringUtil.isEmpty(resource))
			config.setResourceCharset(resource);
		else if (hasCS)
			config.setResourceCharset(configServer.getResourceCharset());

	}

	private static void loadThreadQueue(ConfigServer configServer, ConfigImpl config, Document doc) {
		Element queue = getChildByName(doc.getDocumentElement(), "queue");

		// Server
		if (config instanceof ConfigServerImpl) {
			int max = Caster.toIntValue(queue.getAttribute("max"), 100);
			int timeout = Caster.toIntValue(queue.getAttribute("timeout"), 0);
			((ConfigServerImpl) config).setThreadQueue(new ThreadQueueImpl(max, timeout));

		}
		// Web
		else {
		}
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadRegional(ConfigServer configServer, ConfigImpl config, Document doc) {
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);

		Element regional = hasAccess ? getChildByName(doc.getDocumentElement(), "regional") : null;
		boolean hasCS = configServer != null;

		// timeZone
		String strTimeZone = null;
		if (regional != null)
			strTimeZone = regional.getAttribute("timezone");

		if (!StringUtil.isEmpty(strTimeZone))
			config.setTimeZone(TimeZone.getTimeZone(strTimeZone));
		else if (hasCS)
			config.setTimeZone(configServer.getTimeZone());
		else
			config.setTimeZone(TimeZone.getDefault());

		// timeserver
		String strTimeServer = null;
		Boolean useTimeServer = null;
		if (regional != null) {
			strTimeServer = regional.getAttribute("timeserver");
			useTimeServer = Caster.toBoolean(regional.getAttribute("use-timeserver"), null);// 31
		}

		if (!StringUtil.isEmpty(strTimeServer))
			config.setTimeServer(strTimeServer);
		else if (hasCS)
			config.setTimeServer(configServer.getTimeServer());

		if (useTimeServer != null)
			config.setUseTimeServer(useTimeServer.booleanValue());
		else if (hasCS)
			config.setUseTimeServer(((ConfigImpl) configServer).getUseTimeServer());

		// locale
		String strLocale = null;
		if (regional != null)
			strLocale = regional.getAttribute("locale");

		if (!StringUtil.isEmpty(strLocale))
			config.setLocale(strLocale);
		else if (hasCS)
			config.setLocale(configServer.getLocale());
		else
			config.setLocale(Locale.US);

	}

	private static void loadORM(ConfigServer configServer, ConfigImpl config, Document doc) {
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManagerImpl.TYPE_ORM);

		Element orm = hasAccess ? getChildByName(doc.getDocumentElement(), "orm") : null;
		boolean hasCS = configServer != null;

		// engine
		String defaultEngineClass = "railo.runtime.orm.hibernate.HibernateORMEngine";

		// print.o("orm:"+defaulrEngineClass);
		String strEngine = null;
		if (orm != null)
			strEngine = orm.getAttribute("engine-class");
		if (StringUtil.isEmpty(strEngine, true))
			strEngine = defaultEngineClass;

		// load class
		Class<ORMEngine> clazz;
		try {
			clazz = ClassUtil.loadClass(strEngine);
			// TODO check interface as well
		}
		catch (ClassException ce) {
			ce.printStackTrace();
			clazz = ClassUtil.loadClass(defaultEngineClass, null);
		}
		config.setORMEngineClass(clazz);

		// config
		if (orm == null)
			orm = doc.createElement("orm"); // this is just a dummy
		ORMConfiguration def = hasCS ? ((ConfigServerImpl) configServer).getORMConfig() : null;
		ORMConfiguration ormConfig = ORMConfigurationImpl.load(config, null, orm, config.getRootDirectory(), def);
		config.setORMConfig(ormConfig);

	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws PageException
	 * @throws IOException
	 */
	private static void loadScope(ConfigServerImpl configServer, ConfigImpl config, Document doc, int mode) throws PageException {
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);

		Element scope = getChildByName(doc.getDocumentElement(), "scope");
		boolean hasCS = configServer != null;

		// Cluster Scope
		if (!hasCS) {
			String strClass = scope.getAttribute("cluster-class");
			if (hasAccess && !StringUtil.isEmpty(strClass)) {
				try {
					Class clazz = ClassUtil.loadClass(config.getClassLoader(), strClass);
					if (!Reflector.isInstaneOf(clazz, Cluster.class) && !Reflector.isInstaneOf(clazz, ClusterRemote.class))
						throw new ApplicationException("class [" + clazz.getName() + "] does not implement interface [" + Cluster.class.getName() + "] or ["
								+ ClusterRemote.class.getName() + "]");

					config.setClusterClass(clazz);

				}
				catch (ClassException e) {
					e.printStackTrace();
				}

			}
		}
		// else if(hasCS)
		// config.setClassClusterScope(configServer.getClassClusterScope());

		// Local Mode
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setLocalMode(Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS);
		}
		else {
			String strLocalMode = scope.getAttribute("local-mode");
			if (hasAccess && !StringUtil.isEmpty(strLocalMode)) {
				config.setLocalMode(strLocalMode);
			}
			else if (hasCS)
				config.setLocalMode(configServer.getLocalMode());
		}

		// Session-Type
		String strSessionType = scope.getAttribute("session-type");
		if (hasAccess && !StringUtil.isEmpty(strSessionType)) {
			config.setSessionType(strSessionType);
		}
		else if (hasCS)
			config.setSessionType(configServer.getSessionType());

		// Cascading
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setScopeCascadingType(Config.SCOPE_STRICT);
		}
		else {
			String strScopeCascadingType = scope.getAttribute("cascading");
			if (hasAccess && !StringUtil.isEmpty(strScopeCascadingType)) {
				config.setScopeCascadingType(ConfigWebUtil.toScopeCascading(strScopeCascadingType,Config.SCOPE_STANDARD));
			}
			else if (hasCS)
				config.setScopeCascadingType(configServer.getScopeCascadingType());
		}

		// cascade-to-resultset
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setAllowImplicidQueryCall(false);
		}
		else {
			String strAllowImplicidQueryCall = scope.getAttribute("cascade-to-resultset");
			if (hasAccess && !StringUtil.isEmpty(strAllowImplicidQueryCall)) {
				config.setAllowImplicidQueryCall(toBoolean(strAllowImplicidQueryCall, true));
			}
			else if (hasCS)
				config.setAllowImplicidQueryCall(configServer.allowImplicidQueryCall());
		}

		// Merge url and Form
		String strMergeFormAndURL = scope.getAttribute("merge-url-form");
		if (hasAccess && !StringUtil.isEmpty(strMergeFormAndURL)) {
			config.setMergeFormAndURL(toBoolean(strMergeFormAndURL, false));
		}
		else if (hasCS)
			config.setMergeFormAndURL(configServer.mergeFormAndURL());

		// Client-Storage
		{
			String clientStorage = scope.getAttribute("clientstorage");
			if (StringUtil.isEmpty(clientStorage, true)) 
				clientStorage = scope.getAttribute("client-storage");
			
			if (hasAccess && !StringUtil.isEmpty(clientStorage)) {
				config.setClientStorage(clientStorage);
			}
			else if (hasCS)
				config.setClientStorage(configServer.getClientStorage());
		}
		
		// Session-Storage
		{
			String sessionStorage = scope.getAttribute("sessionstorage");
			if (StringUtil.isEmpty(sessionStorage, true)) 
				sessionStorage = scope.getAttribute("session-storage");
			
			if (hasAccess && !StringUtil.isEmpty(sessionStorage)) {
				config.setSessionStorage(sessionStorage);
			}
			else if (hasCS)
				config.setSessionStorage(configServer.getSessionStorage());
		}
		
		// Client Timeout
		String clientTimeout = scope.getAttribute("clienttimeout");
		if(StringUtil.isEmpty(clientTimeout, true)) clientTimeout = scope.getAttribute("client-timeout");
		if (StringUtil.isEmpty(clientTimeout, true)) {
			// deprecated
			clientTimeout = scope.getAttribute("client-max-age");
			int days = Caster.toIntValue(clientTimeout, -1);
			if (days > 0)
				clientTimeout = days + ",0,0,0";
			else
				clientTimeout = "";
		}
		if (hasAccess && !StringUtil.isEmpty(clientTimeout)) {
			config.setClientTimeout(clientTimeout);
		}
		else if (hasCS)
			config.setClientTimeout(configServer.getClientTimeout());

		// Session Timeout
		String sessionTimeout = scope.getAttribute("sessiontimeout");
		if (hasAccess && !StringUtil.isEmpty(sessionTimeout)) {
			config.setSessionTimeout(sessionTimeout);
		}
		else if (hasCS)
			config.setSessionTimeout(configServer.getSessionTimeout());

		// App Timeout
		String appTimeout = scope.getAttribute("applicationtimeout");
		if (hasAccess && !StringUtil.isEmpty(appTimeout)) {
			config.setApplicationTimeout(appTimeout);
		}
		else if (hasCS)
			config.setApplicationTimeout(configServer.getApplicationTimeout());

		// Client Type
		String strClientType = scope.getAttribute("clienttype");
		if (hasAccess && !StringUtil.isEmpty(strClientType)) {
			config.setClientType(strClientType);
		}
		else if (hasCS)
			config.setClientType(configServer.getClientType());

		// Client
		Resource configDir = config.getConfigDir();
		String strClientDirectory = scope.getAttribute("client-directory");
		if (hasAccess && !StringUtil.isEmpty(strClientDirectory)) {
			strClientDirectory = ConfigWebUtil.translateOldPath(strClientDirectory);
			Resource res = ConfigWebUtil.getFile(configDir, strClientDirectory, "client-scope", configDir, FileUtil.TYPE_DIR, config);
			config.setClientScopeDir(res);
		}
		else {
			config.setClientScopeDir(configDir.getRealResource("client-scope"));
		}

		String strMax = scope.getAttribute("client-directory-max-size");
		if (hasAccess && !StringUtil.isEmpty(strMax)) {
			config.setClientScopeDirSize(ByteSizeParser.parseByteSizeDefinition(strMax, config.getClientScopeDirSize()));
		}
		else if (hasCS)
			config.setClientScopeDirSize(configServer.getClientScopeDirSize());

		// Session Management
		String strSessionManagement = scope.getAttribute("sessionmanagement");
		if (hasAccess && !StringUtil.isEmpty(strSessionManagement)) {
			config.setSessionManagement(toBoolean(strSessionManagement, true));
		}
		else if (hasCS)
			config.setSessionManagement(configServer.isSessionManagement());

		// Client Management
		String strClientManagement = scope.getAttribute("clientmanagement");
		if (hasAccess && !StringUtil.isEmpty(strClientManagement)) {
			config.setClientManagement(toBoolean(strClientManagement, false));
		}
		else if (hasCS)
			config.setClientManagement(configServer.isClientManagement());

		// Client Cookies
		String strClientCookies = scope.getAttribute("setclientcookies");
		if (hasAccess && !StringUtil.isEmpty(strClientCookies)) {
			config.setClientCookies(toBoolean(strClientCookies, true));
		}
		else if (hasCS)
			config.setClientCookies(configServer.isClientCookies());

		// Domain Cookies
		String strDomainCookies = scope.getAttribute("setdomaincookies");
		if (hasAccess && !StringUtil.isEmpty(strDomainCookies)) {
			config.setDomainCookies(toBoolean(strDomainCookies, false));
		}
		else if (hasCS)
			config.setDomainCookies(configServer.isDomainCookies());
	}

	private static void loadJava(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		boolean hasCS = configServer != null;
		Element java = getChildByName(doc.getDocumentElement(), "java");

		//
		String strInspectTemplate = java.getAttribute("inspect-template");
		if (!StringUtil.isEmpty(strInspectTemplate, true)) {
			config.setInspectTemplate(ConfigWebUtil.inspectTemplate(strInspectTemplate, ConfigImpl.INSPECT_ONCE));
		}
		else if (hasCS) {
			config.setInspectTemplate(configServer.getInspectTemplate());
		}

		//
		String strCompileType = java.getAttribute("compile-type");
		if (!StringUtil.isEmpty(strCompileType)) {
			strCompileType = strCompileType.trim().toLowerCase();
			if (strCompileType.equals("after-startup")) {
				config.setCompileType(Config.RECOMPILE_AFTER_STARTUP);
			}
			else if (strCompileType.equals("always")) {
				config.setCompileType(Config.RECOMPILE_ALWAYS);
			}
		}
		else if (hasCS) {
			config.setCompileType(configServer.getCompileType());
		}

	}

	private static void loadConstants(ConfigServerImpl configServer, ConfigImpl config, Document doc) {

		boolean hasCS = configServer != null;
		Element constant = getChildByName(doc.getDocumentElement(), "constants");

		// Constants
		Element[] elConstants = getChildren(constant, "constant");
		Struct sct = null;
		if (hasCS) {
			sct = configServer.getConstants();
			if (sct != null)
				sct = (Struct) sct.duplicate(false);
		}
		if (sct == null)
			sct = new StructImpl();
		String name;
		for (int i = 0; i < elConstants.length; i++) {
			name = elConstants[i].getAttribute("name");
			if (StringUtil.isEmpty(name))
				continue;
			sct.setEL(KeyImpl.getInstance(name.trim()), elConstants[i].getAttribute("value"));
		}
		config.setConstants(sct);
	}

	private static void loadLogin(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		// server context
		if (config instanceof ConfigServer) {
			Element login = getChildByName(doc.getDocumentElement(), "login");
			boolean captcha = Caster.toBooleanValue(login.getAttribute("captcha"), false);
			boolean rememberme = Caster.toBooleanValue(login.getAttribute("rememberme"), true);
			
			int delay = Caster.toIntValue(login.getAttribute("delay"), 1);
			ConfigServerImpl cs = (ConfigServerImpl) config;
			cs.setLoginDelay(delay);
			cs.setLoginCaptcha(captcha);
			cs.setRememberMe(rememberme);
		}
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws IOException
	 */
	private static void loadMail(ConfigServerImpl configServer, ConfigImpl config, Document doc) throws IOException {

		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_MAIL);

		boolean hasCS = configServer != null;
		Element mail = getChildByName(doc.getDocumentElement(), "mail");

		// Spool Interval
		String strSpoolInterval = mail.getAttribute("spool-interval");
		if (!StringUtil.isEmpty(strSpoolInterval) && hasAccess) {
			config.setMailSpoolInterval(toInt(strSpoolInterval, 30));
		}
		else if (hasCS)
			config.setMailSpoolInterval(configServer.getMailSpoolInterval());

		String strEncoding = mail.getAttribute("default-encoding");
		if (!StringUtil.isEmpty(strEncoding) && hasAccess)
			config.setMailDefaultEncoding(strEncoding);
		else if (hasCS)
			config.setMailDefaultEncoding(configServer.getMailDefaultEncoding());

		// Spool Enable
		String strSpoolEnable = mail.getAttribute("spool-enable");
		if (!StringUtil.isEmpty(strSpoolEnable) && hasAccess) {
			config.setMailSpoolEnable(toBoolean(strSpoolEnable, false));
		}
		else if (hasCS)
			config.setMailSpoolEnable(configServer.isMailSpoolEnable());

		// Timeout
		String strTimeout = mail.getAttribute("timeout");
		if (!StringUtil.isEmpty(strTimeout) && hasAccess) {
			config.setMailTimeout(toInt(strTimeout, 60));
		}
		else if (hasCS)
			config.setMailTimeout(configServer.getMailTimeout());

		// Servers
		int index = 0;
		Server[] servers = null;
		Element[] elServers = getChildren(mail, "server");
		if (hasCS) {
			Server[] readOnlyServers = configServer.getMailServers();
			servers = new Server[readOnlyServers.length + (hasAccess ? elServers.length : 0)];
			for (int i = 0; i < readOnlyServers.length; i++) {
				servers[i] = readOnlyServers[index++].cloneReadOnly();
			}
		}
		else {
			servers = new Server[elServers.length];
		}
		if (hasAccess) {
			for (int i = 0; i < elServers.length; i++) {
				Element el = elServers[i];
				if (el.getNodeName().equals("server"))
					servers[index++] = new ServerImpl(el.getAttribute("smtp"), toInt(el.getAttribute("port"), 25), el.getAttribute("username"),
							decrypt(el.getAttribute("password")), toBoolean(el.getAttribute("tls"), false), toBoolean(el.getAttribute("ssl"), false));

			}
		}
		config.setMailServers(servers);
	}

	private static void loadMonitors(ConfigServerImpl configServer, ConfigImpl config, Document doc) throws IOException {
		// only load in server context
		if (configServer != null) return;

		configServer = (ConfigServerImpl) config;

		Element parent = getChildByName(doc.getDocumentElement(), "monitoring");
		boolean enabled = Caster.toBooleanValue(parent.getAttribute("enabled"), false);
		configServer.setMonitoringEnabled(enabled);
		SystemOut.printDate(config.getOutWriter(), "monitoring is "+(enabled?"enabled":"disabled"));
		
		Element[] children = getChildren(parent, "monitor");
		
		java.util.List<IntervallMonitor> intervalls = new ArrayList<IntervallMonitor>();
		java.util.List<RequestMonitor> requests = new ArrayList<RequestMonitor>();
		java.util.List<MonitorTemp> actions = new ArrayList<MonitorTemp>();
		String className, strType, name;
		boolean log,async;
		short type;
		for (int i = 0; i < children.length; i++) {
			Element el = children[i];
			className = el.getAttribute("class");
			strType = el.getAttribute("type");
			name = el.getAttribute("name");
			async = Caster.toBooleanValue(el.getAttribute("async"),false);
			log = Caster.toBooleanValue(el.getAttribute("log"), true);
			
			if ("request".equalsIgnoreCase(strType))
				type = IntervallMonitor.TYPE_REQUEST;
			else if ("action".equalsIgnoreCase(strType))
				type = 4;// FUTURE Monitor.TYPE_ACTION;
			else
				type = IntervallMonitor.TYPE_INTERVALL;

			
			if (!StringUtil.isEmpty(className) && !StringUtil.isEmpty(name)) {
				name = name.trim();
				try {
					Class clazz = ClassUtil.loadClass(config.getClassLoader(), className);
					Object obj;
					ConstructorInstance constr = Reflector.getConstructorInstance(clazz, new Object[] { configServer }, null);
					if (constr != null)
						obj = constr.invoke();
					else
						obj = clazz.newInstance();
					SystemOut.printDate(config.getOutWriter(), "loaded "+(strType)+" monitor ["+clazz.getName()+"]");
					if (type == IntervallMonitor.TYPE_INTERVALL) {
						IntervallMonitor m = obj instanceof IntervallMonitor ? (IntervallMonitor) obj : new IntervallMonitorWrap(obj);
						m.init(configServer, name, log);
						intervalls.add(m);
					}
					else if (type == 4) {// FUTURE Monitor.TYPE_ACTION;
						actions.add(new MonitorTemp(obj, name, log));
					}
					else {
						RequestMonitor m = obj instanceof RequestMonitor ? (RequestMonitor) obj : new RequestMonitorWrap(obj);
						if(async) m=new AsyncRequestMonitor(m);
						m.init(configServer, name, log);
						SystemOut.printDate(config.getOutWriter(), "initialize "+(strType)+" monitor ["+clazz.getName()+"]");
						
						requests.add(m);
					}
				}
				catch (Throwable t) {
					SystemOut.printDate(config.getErrWriter(), ExceptionUtil.getStacktrace(t, true));
				}
			}

		}
		configServer.setRequestMonitors(requests.toArray(new RequestMonitor[requests.size()]));
		configServer.setIntervallMonitors(intervalls.toArray(new IntervallMonitor[intervalls.size()]));
		ActionMonitorCollector actionMonitorCollector = ActionMonitorFatory.getActionMonitorCollector(configServer, actions.toArray(new MonitorTemp[actions.size()]));
		configServer.setActionMonitorCollector(actionMonitorCollector);

		((CFMLEngineImpl) configServer.getCFMLEngine()).touchMonitor(configServer);
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws PageException
	 */
	private static void loadSearch(ConfigServer configServer, ConfigImpl config, Document doc) throws PageException {
		if (config instanceof ConfigServer)
			return;

		// ServletContext sc=config.getServletContext();
		Resource configDir = config.getConfigDir();

		Element search = getChildByName(doc.getDocumentElement(), "search");

		String strEngineClass = search.getAttribute("engine-class");
		SearchEngine se = null;
		Object o = ClassUtil.loadInstance(strEngineClass, (Object) null);
		if (o instanceof SearchEngine)
			se = (SearchEngine) o;

		if (se == null)
			se = new railo.runtime.search.lucene2.LuceneSearchEngine();

		try {
			// Init
			se.init(config, ConfigWebUtil.getFile(configDir, ConfigWebUtil.translateOldPath(search.getAttribute("directory")), "search", configDir, FileUtil.TYPE_DIR, config), null);
		}
		catch (Exception e) {
			throw Caster.toPageException(e);
		}

		config.setSearchEngine(se);
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @param isEventGatewayContext
	 * @throws IOException
	 * @throws PageException
	 */
	private static void loadScheduler(ConfigServer configServer, ConfigImpl config, Document doc) throws PageException, IOException {
		if (config instanceof ConfigServer)
			return;

		Resource configDir = config.getConfigDir();
		Element scheduler = getChildByName(doc.getDocumentElement(), "scheduler");

		// set scheduler
		Resource file = ConfigWebUtil.getFile(config.getRootDirectory(), scheduler.getAttribute("directory"), "scheduler", configDir, FileUtil.TYPE_DIR, config);
		config.setScheduler(configServer.getCFMLEngine(), file);
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadDebug(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		boolean hasCS = configServer != null;
		Element debugging = getChildByName(doc.getDocumentElement(), "debugging");
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_DEBUGGING);

		// Entries
		Element[] entries = getChildren(debugging, "debug-entry");
		Map<String, DebugEntry> list = new HashMap<String, DebugEntry>();
		if (hasCS) {
			DebugEntry[] _entries = ((ConfigImpl) configServer).getDebugEntries();
			for (int i = 0; i < _entries.length; i++) {
				list.put(_entries[i].getId(), _entries[i].duplicate(true));
			}
		}
		Element e;
		String id;
		for (int i = 0; i < entries.length; i++) {
			e = entries[i];
			id = e.getAttribute("id");
			try {
				list.put(id, new DebugEntry(id, e.getAttribute("type"), e.getAttribute("iprange"), e.getAttribute("label"), e.getAttribute("path"), e.getAttribute("fullname"),
						toStruct(e.getAttribute("custom"))));
			}
			catch (IOException ioe) {
			}
		}
		config.setDebugEntries(list.values().toArray(new DebugEntry[list.size()]));

		// debug
		String strDebug = debugging.getAttribute("debug");
		if (hasAccess && !StringUtil.isEmpty(strDebug)) {
			config.setDebug(toBoolean(strDebug, false) ? ConfigImpl.CLIENT_BOOLEAN_TRUE : ConfigImpl.CLIENT_BOOLEAN_FALSE);
		}
		else if (hasCS)
			config.setDebug(configServer.debug() ? ConfigImpl.SERVER_BOOLEAN_TRUE : ConfigImpl.SERVER_BOOLEAN_FALSE);

		// debug-log-output
		String strDLO = debugging.getAttribute("debug-log-output");
		if (hasAccess && !StringUtil.isEmpty(strDLO)) {
			config.setDebugLogOutput(toBoolean(strDLO, false) ? ConfigImpl.CLIENT_BOOLEAN_TRUE : ConfigImpl.CLIENT_BOOLEAN_FALSE);
		}
		else if (hasCS)
			config.setDebugLogOutput(configServer.debugLogOutput() ? ConfigImpl.SERVER_BOOLEAN_TRUE : ConfigImpl.SERVER_BOOLEAN_FALSE);

		// debug options
		int options = 0;
		String str = debugging.getAttribute("database");
		if (hasAccess && !StringUtil.isEmpty(str)) {
			if (toBoolean(str, false))
				options += ConfigImpl.DEBUG_DATABASE;
		}
		else if (hasCS && configServer.hasDebugOptions(ConfigImpl.DEBUG_DATABASE))
			options += ConfigImpl.DEBUG_DATABASE;

		str = debugging.getAttribute("exception");
		if (hasAccess && !StringUtil.isEmpty(str)) {
			if (toBoolean(str, false))
				options += ConfigImpl.DEBUG_EXCEPTION;
		}
		else if (hasCS && configServer.hasDebugOptions(ConfigImpl.DEBUG_EXCEPTION))
			options += ConfigImpl.DEBUG_EXCEPTION;

		
		str = debugging.getAttribute("dump");
		if (hasAccess && !StringUtil.isEmpty(str)) {
			if (toBoolean(str, false))
				options += ConfigImpl.DEBUG_DUMP;
		}
		else if (hasCS && configServer.hasDebugOptions(ConfigImpl.DEBUG_DUMP))
			options += ConfigImpl.DEBUG_DUMP;

		
		
		str = debugging.getAttribute("tracing");
		if (hasAccess && !StringUtil.isEmpty(str)) {
			if (toBoolean(str, false))
				options += ConfigImpl.DEBUG_TRACING;
		}
		else if (hasCS && configServer.hasDebugOptions(ConfigImpl.DEBUG_TRACING))
			options += ConfigImpl.DEBUG_TRACING;

		str = debugging.getAttribute("timer");
		if (hasAccess && !StringUtil.isEmpty(str)) {
			if (toBoolean(str, false))
				options += ConfigImpl.DEBUG_TIMER;
		}
		else if (hasCS && configServer.hasDebugOptions(ConfigImpl.DEBUG_TIMER))
			options += ConfigImpl.DEBUG_TIMER;

		str = debugging.getAttribute("implicit-access");
		if (hasAccess && !StringUtil.isEmpty(str)) {
			if (toBoolean(str, false))
				options += ConfigImpl.DEBUG_IMPLICIT_ACCESS;
		}
		else if (hasCS && configServer.hasDebugOptions(ConfigImpl.DEBUG_IMPLICIT_ACCESS))
			options += ConfigImpl.DEBUG_IMPLICIT_ACCESS;

		str = debugging.getAttribute("query-usage");
		if (StringUtil.isEmpty(str))
			str = debugging.getAttribute("show-query-usage");
		if (hasAccess && !StringUtil.isEmpty(str)) {
			if (toBoolean(str, false))
				options += ConfigImpl.DEBUG_QUERY_USAGE;
		}
		else if (hasCS && configServer.hasDebugOptions(ConfigImpl.DEBUG_QUERY_USAGE))
			options += ConfigImpl.DEBUG_QUERY_USAGE;

		// max records logged
		String strMax = debugging.getAttribute("max-records-logged");
		if (hasAccess && !StringUtil.isEmpty(strMax)) {
			config.setDebugMaxRecordsLogged(toInt(strMax, 10));
		}
		else if (hasCS)
			config.setDebugMaxRecordsLogged(configServer.getDebugMaxRecordsLogged());

		config.setDebugOptions(options);
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 */
	private static void loadCFX(ConfigServerImpl configServer, ConfigImpl config, Document doc) {

		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_CFX_SETTING);

		Map<String,CFXTagClass> map = MapFactory.<String,CFXTagClass>getConcurrentMap();
		if (configServer != null) {
			try {
				if (configServer.getCFXTagPool() != null) {
					Map<String,CFXTagClass> classes = configServer.getCFXTagPool().getClasses();
					Iterator<Entry<String, CFXTagClass>> it = classes.entrySet().iterator();
					Entry<String, CFXTagClass> e;
					while (it.hasNext()) {
						e = it.next();
						map.put(e.getKey(), e.getValue().cloneReadOnly());
					}
				}
			}
			catch (SecurityException e) {
			}
		}

		if (hasAccess) {
			if (configServer == null) {
				System.setProperty("cfx.bin.path", config.getConfigDir().getRealResource("bin").getAbsolutePath());
			}

			// Java CFX Tags
			Element cfxTagsParent = getChildByName(doc.getDocumentElement(), "ext-tags", false, true);
			if (cfxTagsParent == null)
				cfxTagsParent = getChildByName(doc.getDocumentElement(), "cfx-tags", false, true);
			if (cfxTagsParent == null)
				cfxTagsParent = getChildByName(doc.getDocumentElement(), "ext-tags");

			boolean oldStyle = cfxTagsParent.getNodeName().equals("cfx-tags");

			Element[] cfxTags = oldStyle ? getChildren(cfxTagsParent, "cfx-tag") : getChildren(cfxTagsParent, "ext-tag");
			for (int i = 0; i < cfxTags.length; i++) {
				String type = cfxTags[i].getAttribute("type");
				if (type != null) {
					// Java CFX Tags
					if (type.equalsIgnoreCase("java")) {
						String name = cfxTags[i].getAttribute("name");
						String clazz = cfxTags[i].getAttribute("class");
						if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(clazz)) {
							map.put(name.toLowerCase(), new JavaCFXTagClass(name, clazz));
						}
					}
					// C++ CFX Tags
					else if (type.equalsIgnoreCase("cpp")) {
						String name = cfxTags[i].getAttribute("name");
						String serverLibrary = cfxTags[i].getAttribute("server-library");
						String procedure = cfxTags[i].getAttribute("procedure");
						boolean keepAlive = Caster.toBooleanValue(cfxTags[i].getAttribute("keep-alive"), false);

						if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(serverLibrary) && !StringUtil.isEmpty(procedure)) {
							map.put(name.toLowerCase(), new CPPCFXTagClass(name, serverLibrary, procedure, keepAlive));
						}
					}
				}
			}

		}
		config.setCFXTagPool(map);
	}

	private static void loadExtensions(ConfigServerImpl configServer, ConfigImpl config, Document doc) {

		Element xmlExtParent = getChildByName(doc.getDocumentElement(), "extensions");

		String strEnabled = xmlExtParent.getAttribute("enabled");
		if (!StringUtil.isEmpty(strEnabled)) {
			config.setExtensionEnabled(Caster.toBooleanValue(strEnabled, false));
		}

		// providers
		Element[] xmlProviders = getChildren(xmlExtParent, "provider");
		String provider;
		Map list = new HashMap();

		for (int i = 0; i < ConfigImpl.RAILO_EXTENSION_PROVIDERS.length; i++) {
			list.put(ConfigImpl.RAILO_EXTENSION_PROVIDERS[i], "");
		}

		for (int i = 0; i < xmlProviders.length; i++) {
			provider = xmlProviders[i].getAttribute("url");
			if (!StringUtil.isEmpty(provider, true) && !"http://www.railo-technologies.com/ExtensionProvider.cfc".equals(provider)
					&& !"http://www.railo.ch/ExtensionProvider.cfc".equals(provider)) {
				list.put(new ExtensionProviderImpl(provider.trim(), false), "");
			}
		}
		config.setExtensionProviders((ExtensionProvider[]) list.keySet().toArray(new ExtensionProvider[list.size()]));

		// extensions
		Element[] xmlExtensions = getChildren(xmlExtParent, "extension");
		Extension[] extensions = new Extension[xmlExtensions.length];
		Element xmlExtension;
		for (int i = 0; i < xmlExtensions.length; i++) {
			xmlExtension = xmlExtensions[i];
			extensions[i] = new ExtensionImpl(xmlExtension.getAttribute("config"), xmlExtension.getAttribute("id"), xmlExtension.getAttribute("provider"),
					xmlExtension.getAttribute("version"),

					xmlExtension.getAttribute("name"), xmlExtension.getAttribute("label"), xmlExtension.getAttribute("description"), xmlExtension.getAttribute("category"),
					xmlExtension.getAttribute("image"), xmlExtension.getAttribute("author"), xmlExtension.getAttribute("codename"), xmlExtension.getAttribute("video"),
					xmlExtension.getAttribute("support"), xmlExtension.getAttribute("documentation"), xmlExtension.getAttribute("forum"), xmlExtension.getAttribute("mailinglist"),
					xmlExtension.getAttribute("network"), DateCaster.toDateAdvanced(xmlExtension.getAttribute("created"), null, null), xmlExtension.getAttribute("type"));
		}
		config.setExtensions(extensions);

	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws IOException
	 */
	private static void loadComponent(ConfigServer configServer, ConfigImpl config, Document doc, int mode) {
		Element component = getChildByName(doc.getDocumentElement(), "component");
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);
		boolean hasSet = false;
		boolean hasCS = configServer != null;

		// String virtual="/component/";

		if (component != null && hasAccess) {

			// component-default-import
			String strCDI = component.getAttribute("component-default-import");
			if (StringUtil.isEmpty(strCDI, true) && configServer != null) {
				strCDI = ((ConfigServerImpl) configServer).getComponentDefaultImport().toString();
			}
			if (!StringUtil.isEmpty(strCDI, true))
				config.setComponentDefaultImport(strCDI);

			// Base
			String strBase = component.getAttribute("base");
			if (StringUtil.isEmpty(strBase, true) && configServer != null) {
				strBase = configServer.getBaseComponentTemplate();
			}
			config.setBaseComponentTemplate(strBase);

			// deep search
			if (mode == ConfigImpl.MODE_STRICT) {
				config.setDoComponentDeepSearch(false);
			}
			else {
				String strDeepSearch = component.getAttribute("deep-search");
				if (!StringUtil.isEmpty(strDeepSearch)) {
					config.setDoComponentDeepSearch(Caster.toBooleanValue(strDeepSearch.trim(), false));
				}
				else if (hasCS) {
					config.setDoComponentDeepSearch(((ConfigServerImpl) configServer).doComponentDeepSearch());
				}
			}

			// Dump-Template
			String strDumpRemplate = component.getAttribute("dump-template");
			if ((strDumpRemplate == null || strDumpRemplate.trim().length() == 0) && configServer != null) {
				strDumpRemplate = configServer.getComponentDumpTemplate();
			}
			config.setComponentDumpTemplate(strDumpRemplate);

			// data-member-default-access
			if (mode == ConfigImpl.MODE_STRICT) {
				config.setComponentDataMemberDefaultAccess(Component.ACCESS_PRIVATE);
			}
			else {
				String strDmda = component.getAttribute("data-member-default-access");
				if (strDmda != null && strDmda.trim().length() > 0) {
					strDmda = strDmda.toLowerCase().trim();
					if (strDmda.equals("remote"))
						config.setComponentDataMemberDefaultAccess(Component.ACCESS_REMOTE);
					else if (strDmda.equals("public"))
						config.setComponentDataMemberDefaultAccess(Component.ACCESS_PUBLIC);
					else if (strDmda.equals("package"))
						config.setComponentDataMemberDefaultAccess(Component.ACCESS_PACKAGE);
					else if (strDmda.equals("private"))
						config.setComponentDataMemberDefaultAccess(Component.ACCESS_PRIVATE);
				}
				else if (configServer != null) {
					config.setComponentDataMemberDefaultAccess(configServer.getComponentDataMemberDefaultAccess());
				}
			}

			// trigger-properties
			if (mode == ConfigImpl.MODE_STRICT) {
				config.setTriggerComponentDataMember(true);
			}
			else {
				Boolean tp = Caster.toBoolean(component.getAttribute("trigger-data-member"), null);
				if (tp != null)
					config.setTriggerComponentDataMember(tp.booleanValue());
				else if (configServer != null) {
					config.setTriggerComponentDataMember(configServer.getTriggerComponentDataMember());
				}
			}

			// local search
			if (mode == ConfigImpl.MODE_STRICT) {
				config.setComponentLocalSearch(false);
			}
			else {
				Boolean ls = Caster.toBoolean(component.getAttribute("local-search"), null);
				if (ls != null)
					config.setComponentLocalSearch(ls.booleanValue());
				else if (configServer != null) {
					config.setComponentLocalSearch(((ConfigServerImpl) configServer).getComponentLocalSearch());
				}
			}

			// use cache path
			Boolean ucp = Caster.toBoolean(component.getAttribute("use-cache-path"), null);
			if (ucp != null)
				config.setUseComponentPathCache(ucp.booleanValue());
			else if (configServer != null) {
				config.setUseComponentPathCache(((ConfigServerImpl) configServer).useComponentPathCache());
			}

			// use component shadow
			if (mode == ConfigImpl.MODE_STRICT) {
				config.setUseComponentShadow(false);
			}
			else {
				Boolean ucs = Caster.toBoolean(component.getAttribute("use-shadow"), null);
				if (ucs != null)
					config.setUseComponentShadow(ucs.booleanValue());
				else if (configServer != null) {
					config.setUseComponentShadow(configServer.useComponentShadow());
				}
			}

		}
		else if (configServer != null) {
			config.setBaseComponentTemplate(configServer.getBaseComponentTemplate());
			config.setComponentDumpTemplate(configServer.getComponentDumpTemplate());
			if (mode == ConfigImpl.MODE_STRICT) {
				config.setComponentDataMemberDefaultAccess(Component.ACCESS_PRIVATE);
				config.setTriggerComponentDataMember(true);
			}
			else {
				config.setComponentDataMemberDefaultAccess(configServer.getComponentDataMemberDefaultAccess());
				config.setTriggerComponentDataMember(configServer.getTriggerComponentDataMember());
			}
		}

		if (mode == ConfigImpl.MODE_STRICT) {
			config.setDoComponentDeepSearch(false);
			config.setComponentDataMemberDefaultAccess(Component.ACCESS_PRIVATE);
			config.setTriggerComponentDataMember(true);
			config.setComponentLocalSearch(false);
			config.setUseComponentShadow(false);

		}

		// Web Mapping

		Element[] cMappings = getChildren(component, "mapping");
		hasSet = false;
		Mapping[] mappings = null;
		if (hasAccess && cMappings.length > 0) {
			mappings = new Mapping[cMappings.length];
			for (int i = 0; i < cMappings.length; i++) {
				Element cMapping = cMappings[i];
				String physical = cMapping.getAttribute("physical");
				String archive = cMapping.getAttribute("archive");
				boolean readonly = toBoolean(cMapping.getAttribute("readonly"), false);
				boolean hidden = toBoolean(cMapping.getAttribute("hidden"), false);
				//boolean trusted = toBoolean(cMapping.getAttribute("trusted"), false);
				short inspTemp = inspectTemplate(cMapping);
				String virtual = ConfigWebAdmin.createVirtual(cMapping);

				int clMaxEl = toInt(cMapping.getAttribute("classloader-max-elements"), 100);

				String primary = cMapping.getAttribute("primary");

				boolean physicalFirst = archive == null || !primary.equalsIgnoreCase("archive");
				hasSet = true;
				mappings[i] = new MappingImpl(config, virtual, physical, archive, inspTemp, physicalFirst, hidden, readonly, true, false, true, null, clMaxEl);
			}

			config.setComponentMappings(mappings);

		}

		// Server Mapping
		if (hasCS) {
			Mapping[] originals = ((ConfigServerImpl) configServer).getComponentMappings();
			Mapping[] clones = new Mapping[originals.length];
			LinkedHashMap map = new LinkedHashMap();
			Mapping m;
			for (int i = 0; i < clones.length; i++) {
				m = ((MappingImpl) originals[i]).cloneReadOnly(config);
				map.put(toKey(m), m);
				// clones[i]=((MappingImpl)m[i]).cloneReadOnly(config);
			}

			if (mappings != null) {
				for (int i = 0; i < mappings.length; i++) {
					m = mappings[i];
					map.put(toKey(m), m);
				}
			}
			if (originals.length > 0) {
				clones = new Mapping[map.size()];
				Iterator it = map.entrySet().iterator();
				Map.Entry entry;
				int index = 0;
				while (it.hasNext()) {
					entry = (Entry) it.next();
					clones[index++] = (Mapping) entry.getValue();
					// print.out("c:"+clones[index-1]);
				}
				hasSet = true;
				// print.err("set:"+clones.length);

				config.setComponentMappings(clones);
			}
		}

		if (!hasSet) {
			MappingImpl m = new MappingImpl(config, "/default", "{railo-web}/components/", null, ConfigImpl.INSPECT_UNDEFINED, true, false, false, true, false, true, null);
			config.setComponentMappings(new Mapping[] { m.cloneReadOnly(config) });
		}

	}

	private static void loadProxy(ConfigServerImpl configServer, ConfigImpl config, Document doc) {

		boolean hasCS = configServer != null;
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);
		Element proxy = getChildByName(doc.getDocumentElement(), "proxy");

		// server

		// proxy enabled
		// String enabled=proxy.getAttribute("enabled");
		// if(hasAccess && !StringUtil.isEmpty(enabled))
		// config.setProxyEnable(toBoolean(enabled, false));
		// else if(hasCS) config.setProxyEnable(configServer.isProxyEnable());

		// proxy server
		String server = proxy.getAttribute("server");
		String username = proxy.getAttribute("username");
		String password = proxy.getAttribute("password");
		int port = toInt(proxy.getAttribute("port"), -1);

		if (hasAccess && !StringUtil.isEmpty(server)) {
			config.setProxyData(ProxyDataImpl.getInstance(server, port, username, password));
		}
		else if (hasCS)
			config.setProxyData(configServer.getProxyData());
	}

	private static void loadError(ConfigServerImpl configServer, ConfigImpl config, Document doc) {
		Element error = getChildByName(doc.getDocumentElement(), "error");
		boolean hasCS = configServer != null;
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_DEBUGGING);

		// error template
		String template = error.getAttribute("template");

		// 500
		String template500 = error.getAttribute("template-500");
		if (StringUtil.isEmpty(template500))
			template500 = error.getAttribute("template500");
		if (StringUtil.isEmpty(template500))
			template500 = error.getAttribute("500");
		if (StringUtil.isEmpty(template500))
			template500 = template;
		if (hasAccess && !StringUtil.isEmpty(template500)) {
			config.setErrorTemplate(500, template500);
		}
		else if (hasCS)
			config.setErrorTemplate(500, configServer.getErrorTemplate(500));
		else
			config.setErrorTemplate(500, "/railo-context/templates/error/error.cfm");

		// 404
		String template404 = error.getAttribute("template-404");
		if (StringUtil.isEmpty(template404))
			template404 = error.getAttribute("template404");
		if (StringUtil.isEmpty(template404))
			template404 = error.getAttribute("404");
		if (StringUtil.isEmpty(template404))
			template404 = template;
		if (hasAccess && !StringUtil.isEmpty(template404)) {
			config.setErrorTemplate(404, template404);
		}
		else if (hasCS)
			config.setErrorTemplate(404, configServer.getErrorTemplate(404));
		else
			config.setErrorTemplate(404, "/railo-context/templates/error/error.cfm");

		// status code
		String strStausCode = error.getAttribute("status-code");
		if (StringUtil.isEmpty(strStausCode))
			strStausCode = error.getAttribute("statusCode");
		if (StringUtil.isEmpty(strStausCode))
			strStausCode = error.getAttribute("status");

		if (!StringUtil.isEmpty(strStausCode) && hasAccess) {
			config.setErrorStatusCode(toBoolean(strStausCode, true));
		}
		else if (hasCS)
			config.setErrorStatusCode(configServer.getErrorStatusCode());

	}

	private static void loadCompiler(ConfigServerImpl configServer, ConfigImpl config, Document doc, int mode) {
		boolean hasCS = configServer != null;

		Element compiler = getChildByName(doc.getDocumentElement(), "compiler");

		// suppress WS between cffunction and cfargument
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setSuppressWSBeforeArg(true);
		}
		else {
			String suppress = compiler.getAttribute("suppress-ws-before-arg");
			if(StringUtil.isEmpty(suppress, true)) 
				suppress = compiler.getAttribute("supress-ws-before-arg");
			if(!StringUtil.isEmpty(suppress, true)) {
				config.setSuppressWSBeforeArg(Caster.toBooleanValue(suppress, true));
			}
			else if (hasCS) {
				config.setSuppressWSBeforeArg(configServer.getSuppressWSBeforeArg());
			}
		}

		// do dot notation keys upper case
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setDotNotationUpperCase(false);
		}
		else {
			String _case = compiler.getAttribute("dot-notation-upper-case");
			if (!StringUtil.isEmpty(_case, true)) {
				config.setDotNotationUpperCase(Caster.toBooleanValue(_case, true));
			}
			else if (hasCS) {
				config.setDotNotationUpperCase(configServer.getDotNotationUpperCase());
			}
		}

		// full null support
		if (!hasCS) {
			boolean fns = false;
			if (mode == ConfigImpl.MODE_STRICT) {
				fns = true;
			}
			else {
				String str = compiler.getAttribute("full-null-support");

				if (!StringUtil.isEmpty(str, true)) {
					fns = Caster.toBooleanValue(str, false);
				}
			}
			NullSupportHelper.fullNullSupport = fns;
			((ConfigServerImpl) config).setFullNullSupport(fns);
		}
		
		// suppress WS between cffunction and cfargument
		
		String str = compiler.getAttribute("externalize-string-gte");
		if (Decision.isNumeric(str)) {
			config.setExternalizeStringGTE(Caster.toIntValue(str,-1));
		}
		else if (hasCS) {
			config.setExternalizeStringGTE(configServer.getExternalizeStringGTE());
		}
		
	}

	/**
	 * @param configServer
	 * @param config
	 * @param doc
	 * @throws IOException
	 * @throws PageException
	 */
	private static void loadApplication(ConfigServerImpl configServer, ConfigImpl config, Document doc, int mode) throws IOException, PageException {
		boolean hasCS = configServer != null;
		boolean hasAccess = ConfigWebUtil.hasAccess(config, SecurityManager.TYPE_SETTING);

		Element application = getChildByName(doc.getDocumentElement(), "application");
		Element scope = getChildByName(doc.getDocumentElement(), "scope");

		// Listener type
		ApplicationListener listener;
		if (mode == ConfigImpl.MODE_STRICT) {
			listener = new ModernAppListener();
		}
		else {

			listener = ConfigWebUtil.loadListener(application.getAttribute("listener-type"), null);

			if (listener == null) {
				if (hasCS && configServer.getApplicationListener() != null)
					listener = ConfigWebUtil.loadListener(configServer.getApplicationListener().getType(), null);
				if (listener == null)
					listener = new MixedAppListener();
			}
		}

		// Type Checking
		Boolean typeChecking = Caster.toBoolean(application.getAttribute("type-checking"),null);
		if (typeChecking !=null) config.setTypeChecking(typeChecking.booleanValue());
		else if(hasCS) config.setTypeChecking(configServer.getTypeChecking());
		
		
		// Listener Mode
		int listenerMode = ConfigWebUtil.toListenerMode(application.getAttribute("listener-mode"), -1);
		if (listenerMode == -1) {
			if (hasCS)
				listenerMode = configServer.getApplicationListener() == null ? ApplicationListener.MODE_CURRENT2ROOT : configServer.getApplicationListener().getMode();
			else
				listenerMode = ApplicationListener.MODE_CURRENT2ROOT;
		}

		listener.setMode(listenerMode);
		config.setApplicationListener(listener);

		// Req Timeout URL
		if (mode == ConfigImpl.MODE_STRICT) {
			config.setAllowURLRequestTimeout(false);
		}
		else {
			String allowURLReqTimeout = application.getAttribute("allow-url-requesttimeout");
			if (hasAccess && !StringUtil.isEmpty(allowURLReqTimeout)) {
				config.setAllowURLRequestTimeout(Caster.toBooleanValue(allowURLReqTimeout, false));
			}
			else if (hasCS)
				config.setAllowURLRequestTimeout(configServer.isAllowURLRequestTimeout());
		}

		// Req Timeout
		TimeSpan ts=null;
		if(hasAccess) {
			String reqTimeoutApplication = application.getAttribute("requesttimeout");
			String reqTimeoutScope = scope.getAttribute("requesttimeout"); // deprecated
			
			if (!StringUtil.isEmpty(reqTimeoutApplication)) ts=Caster.toTimespan(reqTimeoutApplication);
			if (ts==null && !StringUtil.isEmpty(reqTimeoutScope))  ts=Caster.toTimespan(reqTimeoutScope);
		}
		
		if (ts!=null && ts.getMillis()>0) config.setRequestTimeout(ts);
		else if (hasCS) config.setRequestTimeout(configServer.getRequestTimeout());

		// script-protect
		String strScriptProtect = application.getAttribute("script-protect");

		if (hasAccess && !StringUtil.isEmpty(strScriptProtect)) {
			// print.err("sp:"+strScriptProtect);
			config.setScriptProtect(AppListenerUtil.translateScriptProtect(strScriptProtect));
		}
		else if (hasCS)
			config.setScriptProtect(configServer.getScriptProtect());

		// classic-date-parsing
		if (config instanceof ConfigServer) {
			if (mode == ConfigImpl.MODE_STRICT) {
				DateCaster.classicStyle = true;
			}
			else {
				String strClassicDateParsing = application.getAttribute("classic-date-parsing");

				if (!StringUtil.isEmpty(strClassicDateParsing)) {
					DateCaster.classicStyle = Caster.toBooleanValue(strClassicDateParsing, false);
				}
			}
		}

		// Cache
		Resource configDir = config.getConfigDir();
		String strCacheDirectory = application.getAttribute("cache-directory");
		if (hasAccess && !StringUtil.isEmpty(strCacheDirectory)) {
			strCacheDirectory = ConfigWebUtil.translateOldPath(strCacheDirectory);
			Resource res = ConfigWebUtil.getFile(configDir, strCacheDirectory, "cache", configDir, FileUtil.TYPE_DIR, config);
			config.setCacheDir(res);
		}
		else {
			config.setCacheDir(configDir.getRealResource("cache"));
		}

		String strMax = application.getAttribute("cache-directory-max-size");
		if (hasAccess && !StringUtil.isEmpty(strMax)) {
			config.setCacheDirSize(ByteSizeParser.parseByteSizeDefinition(strMax, config.getCacheDirSize()));
		}
		else if (hasCS)
			config.setCacheDirSize(configServer.getCacheDirSize());

		// admin sync
		String strClass = application.getAttribute("admin-sync-class");
		if (StringUtil.isEmpty(strClass))
			strClass = scope.getAttribute("admin-sync");
		if (StringUtil.isEmpty(strClass))
			strClass = scope.getAttribute("admin-synchronisation-class");
		if (StringUtil.isEmpty(strClass))
			strClass = scope.getAttribute("admin-synchronisation");

		if (hasAccess && !StringUtil.isEmpty(strClass)) {
			try {
				Class clazz = ClassUtil.loadClass(config.getClassLoader(), strClass);
				if (!Reflector.isInstaneOf(clazz, AdminSync.class))
					throw new ApplicationException("class [" + clazz.getName() + "] does not implement interface [" + AdminSync.class.getName() + "]");
				config.setAdminSyncClass(clazz);

			}
			catch (ClassException e) {
				e.printStackTrace();
			}
		}
		else if (hasCS)
			config.setAdminSyncClass(configServer.getAdminSyncClass());
	}

	/**
	 * cast a string value to a int
	 * 
	 * @param value
	 *            String value represent a int value
	 * @param defaultValue
	 *            if can't cast to a int is value will be returned
	 * @return int value
	 */
	public static int toInt(String value, int defaultValue) {

		if (value == null || value.trim().length() == 0)
			return defaultValue;
		int intValue = Caster.toIntValue(value.trim(), Integer.MIN_VALUE);
		if (intValue == Integer.MIN_VALUE)
			return defaultValue;
		return intValue;
	}

	public static long toLong(String value, long defaultValue) {

		if (value == null || value.trim().length() == 0)
			return defaultValue;
		long longValue = Caster.toLongValue(value.trim(), Long.MIN_VALUE);
		if (longValue == Long.MIN_VALUE)
			return defaultValue;
		return longValue;
	}

	/**
	 * cast a string value to a boolean
	 * 
	 * @param value
	 *            String value represent a booolean ("yes", "no","true" aso.)
	 * @param defaultValue
	 *            if can't cast to a boolean is value will be returned
	 * @return boolean value
	 */
	private static boolean toBoolean(String value, boolean defaultValue) {

		if (value == null || value.trim().length() == 0)
			return defaultValue;

		try {
			return Caster.toBooleanValue(value.trim());
		}
		catch (PageException e) {
			return defaultValue;
		}
	}

	/* *
	 * return first direct child Elements of a Element with given Name and
	 * matching attribute
	 * 
	 * @param parent
	 * 
	 * @param nodeName
	 * 
	 * @param attributeName
	 * 
	 * @param attributeValue
	 * 
	 * @return matching children / private static Element getChildByName(Node
	 * parent, String nodeName, String attributeName, String attributeValue) {
	 * if(parent==null) return null; NodeList list=parent.getChildNodes(); int
	 * len=list.getLength();
	 * 
	 * for(int i=0;i<len;i++) { Node node=list.item(i);
	 * if(node.getNodeType()==Node.ELEMENT_NODE &&
	 * node.getNodeName().equalsIgnoreCase(nodeName)) { Element el=(Element)
	 * node; if(el.getAttribute(attributeName).equalsIgnoreCase(attributeValue))
	 * return el; } } return null; }
	 */



	public static class MonitorTemp {

		public final Object obj;
		public final String name;
		public final boolean log;

		public MonitorTemp(Object obj, String name, boolean log) {
			this.obj = obj;
			this.name = name;
			this.log = log;
		}

	}
}
