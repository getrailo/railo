/**
 * Implements the Cold Fusion Function getmetadata
 */
package railo.runtime.functions.other;

import java.util.HashMap;

import railo.runtime.Component;
import railo.runtime.InterfaceImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigImpl.ComponentMetaData;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ComponentUtil;

public final class GetComponentMetaData implements Function {
	
	
	public static Struct call(PageContext pc , Object obj) throws PageException {
		if(obj instanceof Component){
			Component cfc = (Component)obj;
			return getMetaData(pc,cfc);
		}
		
		try{
			Component cfc = CreateObject.doComponent(pc, Caster.toString(obj));
			return getMetaData(pc,cfc);
		}
		// TODO better solution
		catch(ApplicationException ae){
			InterfaceImpl inter = ComponentLoader.loadInterface(pc, Caster.toString(obj), new HashMap());
			return getMetaData(pc,inter);
		}
	}

	private static Struct getMetaData(PageContext pc, Component cfc) throws PageException {
		return getMetaData(pc, ComponentUtil.toComponent(cfc).getPageSource(), cfc);
	}
	
	private static Struct getMetaData(PageContext pc, InterfaceImpl inter) throws PageException {
		return getMetaData(pc, inter.getPageSource(), inter);
	}
	

	private static Struct getMetaData(PageContext pc, PageSource ps, Object obj) throws PageException {// FUTURE make a class instance of cfc instead of all this
		String key=ps.getDisplayPath();
		long lastMod=0;
		if(ps.physcalExists()) lastMod=ps.getFile().lastModified();
		
		ConfigImpl config=(ConfigWebImpl) pc.getConfig();
		ComponentMetaData data = config.getComponentMetadata(key);
		Struct meta;
		if(data==null || data.lastMod!=lastMod) {
			meta=getMetaData(pc,obj);
			config.putComponentMetadata(key, new ConfigImpl.ComponentMetaData(meta,lastMod));
		}
		else 
			meta=data.meta;
		/*else {
			Key[] keys = metadata.keys();
			for(int i=0;i<keys.length;i++){
				meta.setEL(keys[i],metadata.get(keys[i],null));
			}
		}*/
		return meta;
	}

	private static Struct getMetaData(PageContext pc, Object obj) throws PageException {
		if(obj instanceof Component) return ((Component)obj).getMetaData(pc);
		return ((InterfaceImpl)obj).getMetaData(pc);
	}
	
}