package railo.runtime.type.scope;

import java.io.File;

import railo.commons.io.SystemUtil;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.instrumentation.InstrumentationUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.ReadOnlyStruct;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.KeyConstants;


/**
 * Server Scope
 */
public final class ServerImpl extends ScopeSupport implements Server,SharedScope {

	private static final DateTimeImpl expired=new DateTimeImpl(2145913200000L,false);

	private static final Key PRODUCT_NAME = KeyImpl.intern("productname");
	private static final Key PRODUCT_LEVEL = KeyImpl.intern("productlevel");
    private static final Key PRODUCT_CONTEXT_COUNT = KeyImpl.intern("productcontextcount");
    private static final Key PRODUCT_VERSION = KeyImpl.intern("productversion");
    private static final Key SERIAL_NUMBER = KeyImpl.intern("serialnumber");
    private static final Key APP_SERVER =KeyConstants._appserver;
    private static final Key EXPIRATION = KeyImpl.intern("expiration");
    private static final Key INSTALL_KIT = KeyImpl.intern("installkit");
    private static final Key ROOT_DIR = KeyImpl.intern("rootdir");
    private static final Key SUPPORTED_LOCALES = KeyImpl.intern("supportedlocales");
    private static final Key  COLDFUSION= KeyConstants._coldfusion;
    private static final Key  SERVLET= KeyConstants._servlet;
    private static final Key  ARCH= KeyImpl.intern("arch");
    private static final Key  MAC_ADDRESS= KeyImpl.intern("macAddress");
    private static final Key  ARCH_MODEL= KeyImpl.intern("archModel");
//  private static final Key  JAVA_AGENT_PATH = KeyImpl.intern("javaAgentPath");
    private static final Key  JAVA_EXECUTION_PATH = KeyImpl.intern("executionPath");
    private static final Key  JAVA_AGENT_SUPPORTED = KeyImpl.intern("javaAgentSupported");
    private static final Key  LOADER_VERSION= KeyImpl.intern("loaderVersion");
    private static final Key  LOADER_PATH = KeyImpl.intern("loaderPath");
    private static final Key  VERSION= KeyConstants._version;
    private static final Key  ADDITIONAL_INFORMATION= KeyImpl.intern("additionalinformation");
    private static final Key BUILD_NUMBER = KeyImpl.intern("buildnumber");
    private static final Key OS = KeyConstants._os;
    private static final Key STATE = KeyConstants._state;
    private static final Key RELEASE_DATE = KeyImpl.intern("release-date");
    private static final Key RAILO = KeyConstants._railo;
    private static final Key FILE = KeyConstants._file;
    private static final Key SEPARATOR = KeyConstants._separator;
    private static final Key VENDOR = KeyImpl.intern("vendor");
    private static final Key FREE_MEMORY = KeyImpl.intern("freeMemory");
    private static final Key MAX_MEMORY = KeyImpl.intern("maxMemory");
    private static final Key TOTAL_MEMORY = KeyImpl.intern("totalMemory");
    private static final Key JAVA = KeyConstants._java;
	private static final Key VERSION_NAME = KeyImpl.intern("versionName");
	private static final Key VERSION_NAME_EXPLANATION = KeyImpl.intern("versionNameExplanation");

	private static String jap;

	private static String jep;

	/*
    Supported CFML Application
    
    Blog
    - http://www.blogcfm.org
    
    
    
    */
	/**
	 * constructor of the server scope
	 * @param pc
	 */
	public ServerImpl(PageContext pc) {
		super(true,"server",SCOPE_SERVER);
		reload(pc);

	}
	
	@Override
	public void reload() {	
		reload(ThreadLocalPageContext.get());
	}
	
	public void reload(PageContext pc) {		
	    
	    ReadOnlyStruct coldfusion=new ReadOnlyStruct();
			coldfusion.setEL(PRODUCT_LEVEL,Info.getLevel());
			//coldfusion.setEL(PRODUCT_CONTEXT_COUNT,"inf");
			coldfusion.setEL(PRODUCT_VERSION,"10,0,0,0");
			//coldfusion.setEL(PRODUCT_VERSION,"8,0,0,1");
			coldfusion.setEL(SERIAL_NUMBER,"0");
			coldfusion.setEL(PRODUCT_NAME,"Railo");
			
			// TODO scope server missing values
			coldfusion.setEL(APP_SERVER,"");// Jrun
			coldfusion.setEL(EXPIRATION,expired);// 
			coldfusion.setEL(INSTALL_KIT,"");// 
			
			String rootdir="";
			try{
				rootdir=ThreadLocalPageContext.getConfig(pc).getRootDirectory().getAbsolutePath();
			}
			catch(Throwable t){}
			coldfusion.setEL(ROOT_DIR,rootdir);// 

			
			coldfusion.setEL(SUPPORTED_LOCALES,LocaleFactory.getLocaleList());// 
			
			
			coldfusion.setReadOnly(true);
		super.setEL (COLDFUSION,coldfusion);
		
		ReadOnlyStruct os=new ReadOnlyStruct();
			os.setEL(KeyConstants._name,System.getProperty("os.name") );
			os.setEL(ARCH,System.getProperty("os.arch") );
			os.setEL(MAC_ADDRESS,SystemUtil.getMacAddress());
			int arch=SystemUtil.getOSArch();
			if(arch!=SystemUtil.ARCH_UNKNOW)os.setEL(ARCH_MODEL,new Double(arch) );
			os.setEL(VERSION,System.getProperty("os.version") );
			os.setEL(ADDITIONAL_INFORMATION,"");
			os.setEL(BUILD_NUMBER,"");

			
			os.setReadOnly(true);
		super.setEL (OS,os);
		
		ReadOnlyStruct railo=new ReadOnlyStruct();
			railo.setEL(VERSION,Info.getVersionAsString());
			railo.setEL(VERSION_NAME,Info.getVersionName());
			railo.setEL(VERSION_NAME_EXPLANATION,Info.getVersionNameExplanation());
			railo.setEL(STATE,Info.getStateAsString());
			railo.setEL(RELEASE_DATE,Info.getRealeaseDate());
			railo.setEL(LOADER_VERSION,Caster.toDouble(SystemUtil.getLoaderVersion()));
			railo.setEL(LOADER_PATH, ClassUtil.getSourcePathForClass("railo.loader.servlet.CFMLServlet", ""));

			railo.setReadOnly(true);
		super.setEL (RAILO,railo);

		ReadOnlyStruct separator=new ReadOnlyStruct();
			separator.setEL(KeyConstants._path,System.getProperty("path.separator"));
			separator.setEL(FILE,System.getProperty("file.separator"));
			separator.setEL(KeyConstants._line,System.getProperty("line.separator"));
			separator.setReadOnly(true);
		super.setEL (SEPARATOR,separator);
			
		ReadOnlyStruct java=new ReadOnlyStruct();
			java.setEL(VERSION,System.getProperty("java.version"));
			java.setEL(VENDOR,System.getProperty("java.vendor"));
			arch=SystemUtil.getJREArch();
			if(arch!=SystemUtil.ARCH_UNKNOW)java.setEL(ARCH_MODEL,new Double(arch) );
			Runtime rt = Runtime.getRuntime();
			java.setEL(FREE_MEMORY,new Double(rt.freeMemory()));
			java.setEL(TOTAL_MEMORY,new Double(rt.totalMemory()));
			java.setEL(MAX_MEMORY,new Double(rt.maxMemory()));
			java.setEL(JAVA_AGENT_SUPPORTED,Caster.toBoolean(InstrumentationUtil.isSupported()));
			
			//if(jap==null) jap=JavaUtil.getSourcePathForClass("railo.runtime.instrumentation.Agent");
			//java.setEL(JAVA_AGENT_PATH, jap);
			
			if(jep==null) {
				String temp = System.getProperty( "user.dir", "" );
				if ( !StringUtil.isEmpty(temp) && !temp.endsWith( File.separator ) )
					temp = temp + File.separator;
				jep=temp;
			}
			java.setEL( JAVA_EXECUTION_PATH, jep );

			java.setReadOnly(true);
			super.setEL (JAVA,java);
		

			ReadOnlyStruct servlet=new ReadOnlyStruct();
			String name="";
			try{
				name=pc.getServletContext().getServerInfo();
			}
			catch(Throwable t){}
			servlet.setEL(KeyConstants._name,name);
			servlet.setReadOnly(true);
			
			
			super.setEL (SERVLET,servlet);
	    
	}

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		if(isReadOnlyKey(key))
			throw new ExpressionException("you can't rewrite key ["+key+"] from server scope, key is readonly");
		return super.set (key, value);
	}


	@Override
	public Object setEL(Collection.Key key, Object value) {
		if(!isReadOnlyKey(key))return super.setEL (key, value);
		return value;
	}
	
	/**
	 * returns if the key is a readonly key
	 * @param key key to check
	 * @return is readonly 
	 */
	private boolean isReadOnlyKey(Collection.Key key) {
		
		return (key.equals(JAVA) || 
				key.equals(SEPARATOR) || 
				key.equals(OS) || 
				key.equals(COLDFUSION) || 
				key.equals(RAILO));
	}

	public void touchBeforeRequest(PageContext pc) {
		// do nothing
	}

	public void touchAfterRequest(PageContext pc) {
		// do nothing
	}
}