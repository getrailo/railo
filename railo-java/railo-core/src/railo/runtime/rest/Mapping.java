package railo.runtime.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.print;
import railo.commons.io.FileUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.AndResourceFilter;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.ComponentLoader;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.config.Constants;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
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


	
	private Resource physical;
	private String virtual;
	
	private String strPhysical;
	private boolean hidden;
	private boolean readonly;
	private boolean _default;


	private Config config;


	private List<Source> sources;

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
			sources = new ArrayList<Source>(); 
			for(int i=0;i<children.length;i++){
				//ps=config.toPageSource(null, children[i],null);
				PageSource ps = pc.toPageSource(children[i],null);
				ComponentAccess cfc = ComponentLoader.loadComponent(pc, null, ps, children[i].getName(), true,true);
				Struct meta = cfc.getMetaData(pc);
				if(Caster.toBooleanValue(meta.get(RestUtil.REST,null),false)){
					String path = Caster.toString(meta.get(RestUtil.REST_PATH,null),null);
					sources.add(new Source(this, cfc.getPageSource(), path));
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


	public Result getResult(PageContext pc,String path,int format, Result defaultValue) throws PageException {
		init(pc);
		//if(!path.startsWith("/")) path="/"+path;
		Iterator<Source> it = sources.iterator();
		Source src;
		String[] arrPath,subPath;
		int index;
		while(it.hasNext()){
			src = it.next();
			Struct variables=new StructImpl();
			arrPath = RestUtil.splitPath(path);
			index=RestUtil.matchPath(variables,src.getPath(),arrPath);
			if(index!=-1){
            	subPath=new String[(arrPath.length-1)-index];
            	System.arraycopy(arrPath, index+1, subPath, 0, subPath.length);
				return new Result(src,variables,subPath, format);
			}	
		}
		
		return defaultValue;
	}


	public void setDefault(boolean _default) {
		this._default=_default;
	}
}
