package railo.runtime.type.scope;

import railo.commons.io.SystemUtil;
import railo.runtime.Info;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.ReadOnlyStruct;
import railo.runtime.type.dt.DateTimeImpl;


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
    private static final Key APP_SERVER = KeyImpl.intern("appserver");
    private static final Key EXPIRATION = KeyImpl.intern("expiration");
    private static final Key INSTALL_KIT = KeyImpl.intern("installkit");
    private static final Key ROOT_DIR = KeyImpl.intern("rootdir");
    private static final Key SUPPORTED_LOCALES = KeyImpl.intern("supportedlocales");
    private static final Key  COLDFUSION= KeyImpl.intern("coldfusion");
    private static final Key  SERVLET= KeyImpl.intern("servlet");
    private static final Key  ARCH= KeyImpl.intern("arch");
    private static final Key  ARCH_MODEL= KeyImpl.intern("archModel");
    private static final Key  VERSION= KeyImpl.intern("version");
    private static final Key  ADDITIONAL_INFORMATION= KeyImpl.intern("additionalinformation");
    private static final Key BUILD_NUMBER = KeyImpl.intern("buildnumber");
    private static final Key OS = KeyImpl.intern("os");
    private static final Key STATE = KeyImpl.intern("state");
    private static final Key RELEASE_DATE = KeyImpl.intern("release-date");
    private static final Key RAILO = KeyImpl.intern("railo");
    private static final Key FILE = KeyImpl.intern("file");
    private static final Key SEPARATOR = KeyImpl.intern("separator");
    private static final Key VENDOR = KeyImpl.intern("vendor");
    private static final Key FREE_MEMORY = KeyImpl.intern("freeMemory");
    private static final Key MAX_MEMORY = KeyImpl.intern("maxMemory");
    private static final Key TOTAL_MEMORY = KeyImpl.intern("totalMemory");
    private static final Key JAVA = KeyImpl.intern("java");
	private static final Key VERSION_NAME = KeyImpl.intern("versionName");
	private static final Key VERSION_NAME_EXPLANATION = KeyImpl.intern("versionNameExplanation");

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
				rootdir=ThreadLocalPageContext.getConfig(pc).getRootDirectory().getAbsolutePath();
			}
			catch(Throwable t){}
			coldfusion.setEL(ROOT_DIR,rootdir);// 
			
			
			
			coldfusion.setEL(SUPPORTED_LOCALES,LocaleFactory.getLocaleList());// 
			
			
			coldfusion.setReadOnly(true);
		super.setEL (COLDFUSION,coldfusion);
		
		ReadOnlyStruct os=new ReadOnlyStruct();
			os.setEL(KeyImpl.NAME,System.getProperty("os.name") );
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
			separator.setEL(KeyImpl.PATH,System.getProperty("path.separator"));
			separator.setEL(FILE,System.getProperty("file.separator"));
			separator.setEL(KeyImpl.LINE,System.getProperty("line.separator"));
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
			servlet.setEL(KeyImpl.NAME,name);
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