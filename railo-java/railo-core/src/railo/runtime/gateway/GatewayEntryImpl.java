package railo.runtime.gateway;

import org.opencfml.eventgateway.Gateway;
import org.opencfml.eventgateway.GatewayEngine;
import org.opencfml.eventgateway.GatewayException;

import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;

public class GatewayEntryImpl implements GatewayEntry {

	private String id;
	private Struct custom;
	private boolean readOnly;
	private String listenerCfcPath;
	private int startupMode;
	private Gateway gateway;
	private String cfcPath;
	private String className;
	private GatewayEngine engine;

	public GatewayEntryImpl(GatewayEngine engine,String id, String className, String cfcPath, String listenerCfcPath, String startupMode,Struct custom, boolean readOnly) {
		this.engine=engine;
		this.id=id;
		this.listenerCfcPath=listenerCfcPath;
		this.className=className;
		this.custom=custom;
		this.readOnly=readOnly;
		this.cfcPath=cfcPath;
		startupMode=startupMode.trim().toLowerCase();
		if("manual".equals(startupMode))this.startupMode=STARTUP_MODE_MANUAL;
		else if("disabled".equals(startupMode))this.startupMode=STARTUP_MODE_DISABLED;
		else this.startupMode=STARTUP_MODE_AUTOMATIC;
	}
	

	/**
	 * @return the gateway
	 * @throws ClassException 
	 * @throws PageException 
	 */
	public void createGateway(Config config) throws ClassException, PageException {
		if(gateway==null){
			if(!StringUtil.isEmpty(className)){
				Class clazz = ClassUtil.loadClass(config.getClassLoader(),className);
				gateway=(Gateway) ClassUtil.loadInstance(clazz);
			}
			else if(!StringUtil.isEmpty(cfcPath)){
				gateway=new CFCGateway(cfcPath);
			}
			else throw new ApplicationException("missing gateway source definitions");
			try{
				//new GatewayThread(engine,gateway,GatewayThread.START).run();
				gateway.init(engine,getId(), getListenerCfcPath(),getCustom());
				if(getStartupMode()==GatewayEntry.STARTUP_MODE_AUTOMATIC){
					new GatewayThread(engine,gateway,GatewayThread.START).start();
					/*try{
						//gateway.doStart();
					}
					catch(GatewayException ge){
						engine.log(gateway,GatewayEngine.LOGLEVEL_ERROR, ge.getMessage());
					}*/
				}
			}
			catch(GatewayException pe){
				throw Caster.toPageException(pe);
			}
		}
	}
	
	/**
	 * @see railo.runtime.gateway.GatewayEntry#getGateway()
	 */
	public Gateway getGateway() {
		return gateway;
	}
	
	/**
	 * @see railo.runtime.gateway.GatewayEntry#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see railo.runtime.gateway.GatewayEntry#getClazz()
	 */
	/*public Class getClazz() {
		return clazz;
	}*/

	/**
	 * @see railo.runtime.gateway.GatewayEntry#getCustom()
	 */
	public Struct getCustom() {
		return (Struct) custom.duplicate(true);
	}

	/**
	 * @see railo.runtime.gateway.GatewayEntry#isReadOnly()
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	

	/**
	 * @return the cfcPath
	 */
	public String getListenerCfcPath() {
		return listenerCfcPath;
	}
	
	/**
	 * @see railo.runtime.gateway.GatewayEntry#getCfcPath()
	 */
	public String getCfcPath() {
		return cfcPath;
	}


	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return the startupMode
	 */
	public int getStartupMode() {
		return startupMode;
	}

	public static String toStartup(int mode,String defautValue) {
		if(mode==STARTUP_MODE_MANUAL) return "manual";
		else if(mode==STARTUP_MODE_DISABLED) return "disabled";
		else if(mode==STARTUP_MODE_AUTOMATIC) return "automatic";
		return defautValue;
	}

	public static int toStartup(String strMode, int defaultValue) {
		strMode=strMode.trim().toLowerCase();
		if("manual".equals(strMode)) return STARTUP_MODE_MANUAL;
		else if("disabled".equals(strMode)) return STARTUP_MODE_DISABLED;
		else if("automatic".equals(strMode)) return STARTUP_MODE_AUTOMATIC;
		return defaultValue;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		if(obj==this) return true;
		if(!(obj instanceof GatewayEntryImpl))return false;
		
		GatewayEntryImpl other=(GatewayEntryImpl) obj;
		if(!other.getId().equals(id)) return false;
		if(!equal(other.className,className)) return false;
		if(!equal(other.cfcPath,cfcPath)) return false;
		if(!equal(other.listenerCfcPath,listenerCfcPath)) return false;
		if(other.getStartupMode()!=startupMode) return false;
		
		Struct otherCustom = other.getCustom();
		if(otherCustom.size()!=custom.size()) return false;
		
		Key[] keys = otherCustom.keys();
		Object ot,oc;
		for(int i=0;i<keys.length;i++){
			ot=custom.get(keys[i],null);
			oc=otherCustom.get(keys[i],null);
			if(ot==null) return false;
			if(!ot.equals(oc)) return false;
		}
		return true;
	}


	private static boolean equal(String left, String right) {
		if(left==null && right==null) return true;
		if(left!=null && right!=null) return left.equals(right);
		return false;
	}



}
