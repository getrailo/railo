package railo.runtime.type.scope;

import railo.commons.io.SystemUtil;
import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalConfig;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.ReadOnlyStruct;
import railo.runtime.type.scope.SharedScope;
import railo.runtime.type.dt.DateTimeImpl;


/**
 * Server Scope
 */
public final class ServerImpl extends ScopeSupport implements Server,SharedScope {



	private static final DateTimeImpl expired=new DateTimeImpl(2145913200000L,false);


	private static final Key PRODUCT_NAME = KeyImpl.getInstance("productname");
	private static final Key PRODUCT_LEVEL = KeyImpl.getInstance("productlevel");
    private static final Key PRODUCT_CONTEXT_COUNT = KeyImpl.getInstance("productcontextcount");
    private static final Key PRODUCT_VERSION = KeyImpl.getInstance("productversion");
    private static final Key SERIAL_NUMBER = KeyImpl.getInstance("serialnumber");
    private static final Key APP_SERVER = KeyImpl.getInstance("appserver");
    private static final Key EXPIRATION = KeyImpl.getInstance("expiration");
    private static final Key INSTALL_KIT = KeyImpl.getInstance("installkit");
    private static final Key ROOT_DIR = KeyImpl.getInstance("rootdir");
    private static final Key SUPPORTED_LOCALES = KeyImpl.getInstance("supportedlocales");
    private static final Key  COLDFUSION= KeyImpl.getInstance("coldfusion");
    private static final Key  NAME= KeyImpl.getInstance("name");
    private static final Key  SERVLET= KeyImpl.getInstance("servlet");
    private static final Key  ARCH= KeyImpl.getInstance("arch");
    private static final Key  ARCH_MODEL= KeyImpl.getInstance("archModel");
    private static final Key  VERSION= KeyImpl.getInstance("version");
    private static final Key  ADDITIONAL_INFORMATION= KeyImpl.getInstance("additionalinformation");
    private static final Key BUILD_NUMBER = KeyImpl.getInstance("buildnumber");
    private static final Key OS = KeyImpl.getInstance("os");
    private static final Key STATE = KeyImpl.getInstance("state");
    private static final Key RELEASE_DATE = KeyImpl.getInstance("release-date");
    private static final Key RAILO = KeyImpl.getInstance("railo");
    private static final Key PATH = KeyImpl.getInstance("path");
    private static final Key FILE = KeyImpl.getInstance("file");
    private static final Key LINE = KeyImpl.getInstance("line");
    private static final Key SEPARATOR = KeyImpl.getInstance("separator");
    private static final Key VENDOR = KeyImpl.getInstance("vendor");
    private static final Key FREE_MEMORY = KeyImpl.getInstance("freeMemory");
    private static final Key MAX_MEMORY = KeyImpl.getInstance("maxMemory");
    private static final Key TOTAL_MEMORY = KeyImpl.getInstance("totalMemory");
    private static final Key JAVA = KeyImpl.getInstance("java");
	private static final Key VERSION_NAME = KeyImpl.getInstance("versionName");
	private static final Key VERSION_NAME_EXPLANATION = KeyImpl.getInstance("versionNameExplanation");

	/*
    Supported CFML Application
    
    Blog
    - http://www.blogcfm.org
    
    
    
    */
	/**
	 * constructor of the server scope
	 * @param sn
	 */
	public ServerImpl(PageContext pc) {
		super(true,"server",SCOPE_SERVER);
		reload(pc);

	}
	
	/**
     * @see railo.runtime.type.scope.Server#reload(railo.runtime.security.SerialNumber)
     */
	public void reload() {	
		reload(ThreadLocalPageContext.get());
	}
	
	public void reload(PageContext pc) {		
	    
	    ReadOnlyStruct coldfusion=new ReadOnlyStruct();
			coldfusion.setEL(PRODUCT_LEVEL,Info.getLevel());
			//coldfusion.setEL(PRODUCT_CONTEXT_COUNT,"inf");
			coldfusion.setEL(PRODUCT_VERSION,"9,0,0,1");
			//coldfusion.setEL(PRODUCT_VERSION,"8,0,0,1");
			coldfusion.setEL(SERIAL_NUMBER,"0");
			coldfusion.setEL(PRODUCT_NAME,"Railo");
			
			// TODO scope server missing values
			coldfusion.setEL(APP_SERVER,"");// Jrun
			coldfusion.setEL(EXPIRATION,expired);// 
			coldfusion.setEL(INSTALL_KIT,"");// 
			
			String rootdir="";
			try{
				rootdir=ThreadLocalConfig.get().getRootDirectory().getAbsolutePath();
			}
			catch(Throwable t){}
			coldfusion.setEL(ROOT_DIR,rootdir);// 
			
			
			
			coldfusion.setEL(SUPPORTED_LOCALES,LocaleFactory.getLocaleList());// 
			
			
			coldfusion.setReadOnly(true);
		super.setEL (COLDFUSION,coldfusion);
		
		ReadOnlyStruct os=new ReadOnlyStruct();
			os.setEL(NAME,System.getProperty("os.name") );
			os.setEL(ARCH,System.getProperty("os.arch") );
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
			railo.setReadOnly(true);
		super.setEL (RAILO,railo);
		
		ReadOnlyStruct separator=new ReadOnlyStruct();
			separator.setEL(PATH,System.getProperty("path.separator"));
			separator.setEL(FILE,System.getProperty("file.separator"));
			separator.setEL(LINE,System.getProperty("line.separator"));
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
			java.setReadOnly(true);
			super.setEL (JAVA,java);
		

			ReadOnlyStruct servlet=new ReadOnlyStruct();
			String name="";
			try{
				name=pc.getServletContext().getServerInfo();
			}
			catch(Throwable t){}
			servlet.setEL(NAME,name);
			servlet.setReadOnly(true);
			
			
			super.setEL (SERVLET,servlet);
	    
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		if(isReadOnlyKey(key))
			throw new ExpressionException("you can't rewrite key ["+key+"] from server scope, key is readonly");
		return super.set (key, value);
	}


	/**
	 *
	 * @see railo.runtime.type.StructImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
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