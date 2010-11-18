package railo.runtime.functions.system;

import java.util.Iterator;

import railo.runtime.Component;
import railo.runtime.ComponentWrap;
import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContextUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.util.ApplicationContextPro;

public class GetApplicationSettings {
	public static Struct call(PageContext pc) {
		return call(pc, false);
	}
	
	public static Struct call(PageContext pc, boolean suppressFunctions) {
		ApplicationContextPro ac = (ApplicationContextPro)pc.getApplicationContext();
		Component cfc = ac.getComponent();
		

		Struct sct=new StructImpl();
		sct.setEL("applicationtimeout", ac.getApplicationTimeout());
		sct.setEL("clientmanagement", Caster.toBoolean(ac.isSetClientManagement()));
		sct.setEL("clientstorage", ac.getClientstorage());
		sct.setEL("customtagpaths", toArray(ac.getCustomTagMappings()));
		sct.setEL("datasource", ac.getDefaultDataSource());
		sct.setEL("loginstorage", ApplicationContextUtil.translateLoginStorage(ac.getLoginStorage()));
		sct.setEL("mappings", toStruct(ac.getMappings()));
		sct.setEL("name", ac.getName());
		sct.setEL("scriptprotect", ApplicationContextUtil.translateScriptProtect(ac.getScriptProtect()));
		sct.setEL("securejson", Caster.toBoolean(ac.getSecureJson()));
		sct.setEL("securejsonprefix", ac.getSecureJsonPrefix());
		sct.setEL("sessionmanagement", Caster.toBoolean(ac.isSetSessionManagement()));
		sct.setEL("sessiontimeout", ac.getSessionTimeout());
		sct.setEL("setclientcookies", Caster.toBoolean(ac.isSetClientCookies()));
		sct.setEL("setdomaincookies", Caster.toBoolean(ac.isSetDomainCookies()));
		sct.setEL("name", ac.getName());
		sct.setEL("serverSideFormValidation", Boolean.FALSE); // TODO impl
		
		
		if(cfc!=null){
			try {
				ComponentWrap cw=new ComponentWrap(Component.ACCESS_PRIVATE, ComponentUtil.toComponentImpl(cfc));
				Iterator it=cw.keyIterator();
				Collection.Key key;
				Object value;
		        while(it.hasNext()) {
		            key=KeyImpl.toKey(it.next());
		            value=cw.get(key);
		            if(suppressFunctions && value instanceof UDF) continue;
		            sct.setEL(key, value);
				}
			} 
			catch (PageException e) {e.printStackTrace();}
		}
		return sct;
	}

	

	private static Array toArray(Mapping[] mappings) {
		Array arr=new ArrayImpl();
		if(mappings!=null)for(int i=0;i<mappings.length;i++){
			arr.appendEL(mappings[i].getStrPhysical());
		}
		return arr;
	}

	private static Struct toStruct(Mapping[] mappings) {
		Struct sct=new StructImpl();
		if(mappings!=null)for(int i=0;i<mappings.length;i++){
			sct.setEL(KeyImpl.init(mappings[i].getVirtual()), mappings[i].getStrPhysical());
		}
		return sct;
	}
}
