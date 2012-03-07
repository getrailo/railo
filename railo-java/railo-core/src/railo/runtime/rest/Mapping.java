package railo.runtime.rest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.print;
import railo.commons.io.FileUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.AndResourceFilter;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.NotResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.config.Constants;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.cfc.ComponentAccess;

public class Mapping {

	private static final ResourceFilter FILTER = new AndResourceFilter(new ResourceFilter[]{
			new ExtensionResourceFilter(Constants.CFC_EXTENSION),
			new ResourceFilter() {
				
				public boolean accept(Resource res) {
					return !Constants.APP_CFC.equalsIgnoreCase(res.getName());
				}
			}
	});


	private static final Collection.Key REST = KeyImpl.getInstance("rest");
	private static final Collection.Key RESTPATH = KeyImpl.getInstance("restpath");
	
	
	private Resource physical;
	private String virtual;
	
	private String strPhysical;
	private boolean hidden;
	private boolean readonly;
	private boolean _default;


	private Config config;


	private Map<String, PageSource> sources;

	public Mapping(Config config, String virtual, String physical, boolean hidden, boolean readonly, boolean _default) {
		this.config=config;
		if(!virtual.startsWith("/"))this.virtual="/"+virtual;
		if(virtual.endsWith("/"))this.virtual=virtual.substring(0,virtual.length()-1);
        else this.virtual=virtual;
        
		this.strPhysical=physical;
		this.hidden=hidden;
		this.readonly=readonly;
		this._default=_default;
		

        if(!(config instanceof ConfigWebImpl)) return;
        ConfigWebImpl cw=(ConfigWebImpl) config;
		
		this.physical=ConfigWebUtil.getExistingResource(cw.getServletContext(),physical,null,cw.getConfigDir(),FileUtil.TYPE_DIR,cw);
		
		//init((ConfigImpl) config);
		
	}


	private void init(PageContext pc) throws PageException {
		print.e("physical:"+physical);
		if(this.physical!=null && this.physical.isDirectory()) {
			Resource[] children = this.physical.listResources(FILTER);
			sources = new HashMap<String, PageSource>(); 
			for(int i=0;i<children.length;i++){
				//ps=config.toPageSource(null, children[i],null);
				PageSource ps = pc.toPageSource(children[i],null);
				ComponentAccess cfc = ComponentLoader.loadComponent(pc, null, ps, children[i].getName(), true,true);
				Struct meta = cfc.getMetaData(pc);
				if(Caster.toBooleanValue(meta.get(REST,null),false)){
					String path = Caster.toString(meta.get(RESTPATH,null),null);
					sources.put(path, cfc.getPageSource());
				}
			}
		}
	}


	public railo.runtime.rest.Mapping duplicate(Config config,Boolean readOnly) {
		return new Mapping(config, virtual, strPhysical, hidden, readOnly==null?this.readonly:readOnly.booleanValue(),_default); 
	}
	
	/**
	 * @return the physical
	 */
	public Resource getPhysical() {
		return physical;
	}


	/**
	 * @return the virtual
	 */
	public String getVirtual() {
		return virtual;
	}
	public String getVirtualWithSlash() {
		return virtual+"/";
	}


	/**
	 * @return the strPhysical
	 */
	public String getStrPhysical() {
		return strPhysical;
	}


	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}


	/**
	 * @return the readonly
	 */
	public boolean isReadonly() {
		return readonly;
	}

	public boolean isDefault() {
		return _default;
	}


	public Source getSource(PageContext pc,String path, Source defaultValue) throws PageException {
		init(pc);
		if(!path.startsWith("/")) path="/"+path;
		Iterator<Entry<String, PageSource>> it = sources.entrySet().iterator();
		Entry<String, PageSource> entry;
		String cfcPath;
		while(it.hasNext()){
			entry = it.next();
			cfcPath=entry.getKey();
			if(!cfcPath.startsWith("/")) cfcPath="/"+cfcPath;
			print.e(path+" -> "+cfcPath);
			
			RestUtil.matchPath(path,cfcPath);
            if(path.startsWith(cfcPath)){
				return new Source(this,entry.getValue(),path.substring(cfcPath.length()));
			}	
		}
		
		return defaultValue;
	}


	public void setDefault(boolean _default) {
		this._default=_default;
	}
}
