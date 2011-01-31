package railo.runtime.listener;

import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.runtime.Mapping;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.Undefined;

public final class AppListenerUtil {
	
	public static PageSource getApplicationPageSource(PageContext pc,PageSource requestedPage, String filename, int mode) {
		if(mode==ApplicationListener.MODE_CURRENT)return getApplicationPageSourceCurrent(requestedPage, filename);
		if(mode==ApplicationListener.MODE_ROOT)return getApplicationPageSourceRoot(pc, filename);
		return getApplicationPageSourceCurr2Root(pc, requestedPage, filename);
	}
	
	public static PageSource getApplicationPageSourceCurrent(PageSource requestedPage, String filename) {
		PageSource res=requestedPage.getRealPage(filename);
	    if(res.exists()) return res;
		return null;
	}
	
	public static PageSource getApplicationPageSourceRoot(PageContext pc, String filename) {
		PageSource res = pc.getPageSource("/".concat(filename));
		if(res.exists()) return res;
		return null;
	}
	
	public static PageSource getApplicationPageSourceCurr2Root(PageContext pc,PageSource requestedPage, String filename) {
		PageSource res=requestedPage.getRealPage(filename);
	    if(res.exists()) { 
			return res;
		}
	    Array arr=railo.runtime.type.List.listToArrayRemoveEmpty(requestedPage.getFullRealpath(),"/");
	    //Config config = pc.getConfig();
		for(int i=arr.size()-1;i>0;i--) {
		    StringBuffer sb=new StringBuffer("/");
			for(int y=1;y<i;y++) {
			    sb.append((String)arr.get(y,""));
			    sb.append('/');
			}
			sb.append(filename);
			res = pc.getPageSource(sb.toString());
			if(res.exists()) {
				return res;
			}
		}
		return null;
	}
	
	

	public static PageSource getApplicationPageSource(PageContext pc,PageSource requestedPage, int mode, RefBoolean isCFC) {
		if(mode==ApplicationListener.MODE_CURRENT)return getApplicationPageSourceCurrent(requestedPage, isCFC);
		if(mode==ApplicationListener.MODE_ROOT)return getApplicationPageSourceRoot(pc, isCFC);
		return getApplicationPageSourceCurr2Root(pc, requestedPage, isCFC);
	}
	
	public static PageSource getApplicationPageSourceCurrent(PageSource requestedPage, RefBoolean isCFC) {
		PageSource res=requestedPage.getRealPage("Application.cfc");
	    if(res.exists()) {
	    	isCFC.setValue(true);
	    	return res;
	    }
	    res=requestedPage.getRealPage("Application.cfm");
	    if(res.exists()) return res;
		return null;
	}
	
	public static PageSource getApplicationPageSourceRoot(PageContext pc, RefBoolean isCFC) {
		PageSource res = pc.getPageSource("/Application.cfc");
		if(res.exists()) {
			isCFC.setValue(true);
	    	return res;
		}
		res = pc.getPageSource("/Application.cfm");
		if(res.exists()) return res;
		return null;
	}
	

	public static PageSource getApplicationPageSourceCurr2Root(PageContext pc,PageSource requestedPage, RefBoolean isCFC) {
	    PageSource res=requestedPage.getRealPage("Application.cfc");
	    if(res.exists()) {
	    	isCFC.setValue(true);
	    	return res;
	    }
	    res=requestedPage.getRealPage("Application.cfm");
	    if(res.exists()) return res;
		
	    
	    
	    Array arr=railo.runtime.type.List.listToArrayRemoveEmpty(requestedPage.getFullRealpath(),"/");
		//Config config = pc.getConfig();
		String path;
		for(int i=arr.size()-1;i>0;i--) {
		    StringBuffer sb=new StringBuffer("/");
			for(int y=1;y<i;y++) {
			    sb.append((String)arr.get(y,""));
			    sb.append('/');
			}
			path=sb.toString();
			res = pc.getPageSource(path.concat("Application.cfc"));
			if(res.exists()) {
				isCFC.setValue(true);
				return res;
			}

			res = pc.getPageSource(path.concat("Application.cfm"));
			if(res.exists()) return res;
		}
		return null;
	}
	
	public static String toStringMode(int mode) {
		if(mode==ApplicationListener.MODE_CURRENT)	return "curr";
		if(mode==ApplicationListener.MODE_ROOT)		return "root";
		return "curr2root";
	}

	public static String toStringType(ApplicationListener listener) {
		if(listener instanceof NoneAppListener)			return "none";
		else if(listener instanceof MixedAppListener)	return "mixed";
		else if(listener instanceof ClassicAppListener)	return "classic";
		else if(listener instanceof ModernAppListener)	return "modern";
		return "";
	}

	public static Mapping[] toMappings(PageContext pc,Object o) throws PageException {
		Struct sct = Caster.toStruct(o);
		Key[] keys = sct.keys();
		Mapping[] mappings=new Mapping[keys.length];
		ConfigWebImpl config=(ConfigWebImpl) pc.getConfig();
		String virtual,physical;
		for(int i=0;i<keys.length;i++) {
			virtual=translateMappingVirtual(keys[i].getString());
			physical=Caster.toString(sct.get(keys[i]));
			mappings[i]=config.getApplicationMapping(virtual,physical);
			
		}
		return mappings;
	}
	

	private static String translateMappingVirtual(String virtual) {
		virtual=virtual.replace('\\', '/');
		if(!StringUtil.startsWith(virtual,'/'))virtual="/".concat(virtual);
		return virtual;
	}

	public static MappingImpl[] toCustomTagMappings(PageContext pc, Object o) throws PageException {
		Array array;
		if(o instanceof String){
			array=List.listToArrayRemoveEmpty(Caster.toString(o),',');
		}
		else if(o instanceof Struct){
			array=new ArrayImpl();
			Struct sct=(Struct) o;
			Key[] keys = sct.keys();
			for(int i=0;i<keys.length;i++) {
				array.append(sct.get(keys[i]));
			}
		}
		else {
			array=Caster.toArray(o);
		}
		MappingImpl[] mappings=new MappingImpl[array.size()];
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		for(int i=0;i<mappings.length;i++) {
			
			mappings[i]=(MappingImpl) config.createCustomTagAppMappings("/"+i,Caster.toString(array.getE(i+1)).trim());
			/*mappings[i]=new MappingImpl(
					config,"/"+i,
					Caster.toString(array.getE(i+1)).trim(),
					null,false,true,false,false,false
					);*/
		}
		return mappings;
	}

	public static int toLocalMode(Object oMode, int defaultValue) {
		if(oMode==null) return defaultValue;
		
		if(Decision.isBoolean(oMode)) {
			if(Caster.toBooleanValue(oMode, false))
				return Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
			return Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		}
		String strMode=Caster.toString(oMode,null);
		if("always".equalsIgnoreCase(strMode)) return Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
		if("update".equalsIgnoreCase(strMode)) return Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		return defaultValue;
	}
	
	public static int toLocalMode(String strMode) throws ApplicationException {
		int lm = toLocalMode(strMode, -1);
		if(lm!=-1) return lm;
		throw new ApplicationException("invalid localMode definition ["+strMode+"] for tag application/application.cfc, valid values are [always,update]");
	}

	public static short toSessionType(String str, Config config) {
		if(!StringUtil.isEmpty(str,true)){
			str=str.trim().toLowerCase();
			if("cfml".equals(str)) return Config.SESSION_TYPE_CFML;
			if("j2ee".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("cfm".equals(str)) return Config.SESSION_TYPE_CFML;
			if("jee".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("j".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("c".equals(str)) return Config.SESSION_TYPE_J2EE;
		}
		return config.getSessionType();
	}

	public static short toSessionType(String str) throws ApplicationException {
		if(!StringUtil.isEmpty(str,true)){
			str=str.trim().toLowerCase();
			if("cfml".equals(str)) return Config.SESSION_TYPE_CFML;
			if("j2ee".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("cfm".equals(str)) return Config.SESSION_TYPE_CFML;
			if("jee".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("j".equals(str)) return Config.SESSION_TYPE_J2EE;
			if("c".equals(str)) return Config.SESSION_TYPE_J2EE;
		}
		throw new ApplicationException("invalid sessionType definition ["+str+"] for tag application/application.cfc, valid values are [cfml,j2ee]");
	}
}


