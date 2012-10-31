package railo.runtime.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import railo.commons.io.FileUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.AndResourceFilter;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.filter.ResourceFilter;
import railo.commons.lang.mimetype.MimeType;
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
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;

public class Mapping {

	private static final ResourceFilter FILTER = new AndResourceFilter(new ResourceFilter[]{
			new ExtensionResourceFilter(Constants.CFC_EXTENSION),
			new ResourceFilter() {
				
				public boolean accept(Resource res) {
					return !Constants.APP_CFC.equalsIgnoreCase(res.getName());
				}
			}
	});


	
	private String virtual;
	private Resource physical;
	private String strPhysical;
	private boolean hidden;
	private boolean readonly;
	private boolean _default;


	private List<Source> baseSources;
	private Map<Resource,List<Source>> customSources=new HashMap<Resource, List<Source>>();

	public Mapping(Config config, String virtual, String physical, boolean hidden, boolean readonly, boolean _default) {
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
		
	}


	private List<Source> init(PageContext pc, boolean reset) throws PageException {
		if(reset)release();
		
		Resource[] locations = pc.getApplicationContext().getRestCFCLocations();
		
		// base source
		if(ArrayUtil.isEmpty(locations)) {
			if(baseSources==null && this.physical!=null && this.physical.isDirectory()) {
				baseSources=_init(pc,this, this.physical);
			}
			return baseSources;
		}
		
		// custom sources
		List<Source> rtn = new ArrayList<Source>(); 
		List<Source> list;
		for(int i=0;i<locations.length;i++){
			list = customSources.get(locations[i]);
			if(list==null && locations[i].isDirectory()) {
				list=_init(pc,this, locations[i]);
				customSources.put(locations[i], list);
			}
			copy(list,rtn);
		}
		return rtn;
	}
	
	private void copy(List<Source> src, List<Source> trg) { 
		if(src==null) return;
		Iterator<Source> it = src.iterator();
		while(it.hasNext()){
			trg.add(it.next());
		}
	}


	private static ArrayList<Source> _init(PageContext pc, Mapping mapping, Resource dir) throws PageException{
		Resource[] children = dir.listResources(FILTER);
		
		RestSettings settings = pc.getApplicationContext().getRestSettings();
		ArrayList<Source> sources = new ArrayList<Source>(); 
	
		PageSource ps;
		ComponentAccess cfc;
		Struct meta;
		String path;
		for(int i=0;i<children.length;i++){
			try{
				ps = pc.toPageSource(children[i],null);
				cfc = ComponentLoader.loadComponent(pc, null, ps, children[i].getName(), true,true);
				meta = cfc.getMetaData(pc);
				if(Caster.toBooleanValue(meta.get(KeyConstants._rest,null),false)){
					path = Caster.toString(meta.get(KeyConstants._restPath,null),null);
					sources.add(new Source(mapping, cfc.getPageSource(), path));
				}
			}
			catch(Throwable t){
				if(!settings.getSkipCFCWithError()) throw Caster.toPageException(t);
			}
		}
		return sources;
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


	public Result getResult(PageContext pc,String path,Struct matrix,int format,boolean hasFormatExtension, List<MimeType> accept, MimeType contentType,Result defaultValue) throws PageException {
		List<Source> sources = init(pc,false);
		Iterator<Source> it = sources.iterator();
		Source src;
		String[] arrPath,subPath;
		int index;
		while(it.hasNext()) {
			src = it.next();
			Struct variables=new StructImpl();
			arrPath = RestUtil.splitPath(path);
			index=RestUtil.matchPath(variables,src.getPath(),arrPath);
			if(index!=-1){
            	subPath=new String[(arrPath.length-1)-index];
            	System.arraycopy(arrPath, index+1, subPath, 0, subPath.length);
				return new Result(src,variables,subPath,matrix,format,hasFormatExtension,accept,contentType);
			}	
		}
		
		return defaultValue;
	}


	public void setDefault(boolean _default) {
		this._default=_default;
	}


	public void reset(PageContext pc) throws PageException {
		init(pc, true);
	}


	public synchronized void release() {
		if(baseSources!=null) {
			baseSources.clear();
			baseSources = null; 
		}
		customSources.clear();
	}
}
