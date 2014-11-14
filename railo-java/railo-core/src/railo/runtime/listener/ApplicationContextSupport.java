/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.listener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.db.DataSource;
import railo.runtime.exp.ApplicationException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ArrayUtil;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.library.tag.TagLibTagAttr;

public abstract class ApplicationContextSupport implements ApplicationContextPro {

	private static final long serialVersionUID = 1384678713928757744L;
	
	protected int idletimeout=1800;
	protected String cookiedomain;
	protected String applicationtoken;
	
	private Map<Collection.Key,Map<Collection.Key,Object>> tagDefaultAttributeValues=null;

	protected ConfigWeb config;

	public ApplicationContextSupport(ConfigWeb config) {
		this.config=config;
		tagDefaultAttributeValues=((ConfigImpl)config).getTagDefaultAttributeValues();
	}
	

	protected void _duplicate(ApplicationContextSupport other) {
		idletimeout=other.idletimeout;
		cookiedomain=other.cookiedomain;
		applicationtoken=other.applicationtoken;
		if(other.tagDefaultAttributeValues!=null) {
			tagDefaultAttributeValues=new HashMap<Collection.Key, Map<Collection.Key,Object>>();
			Iterator<Entry<Collection.Key, Map<Collection.Key, Object>>> it = other.tagDefaultAttributeValues.entrySet().iterator();
			Entry<Collection.Key, Map<Collection.Key, Object>> e;
			Iterator<Entry<Collection.Key, Object>> iit;
			Entry<Collection.Key, Object> ee;
			Map<Collection.Key, Object> map;
			while(it.hasNext()){
				e = it.next();
				iit=e.getValue().entrySet().iterator();
				map=new HashMap<Collection.Key, Object>();
				while(iit.hasNext()){
					ee = iit.next();
					map.put(ee.getKey(), ee.getValue());
				}
				tagDefaultAttributeValues.put(e.getKey(), map);
			}
		}
	}

	@Override
	public void setSecuritySettings(String applicationtoken, String cookiedomain, int idletimeout) {
		this.applicationtoken=applicationtoken;
		this.cookiedomain=cookiedomain;
		this.idletimeout=idletimeout;
		
	}
	
	@Override
	public String getSecurityApplicationToken() {
		if(StringUtil.isEmpty(applicationtoken,true)) return getName();
		return applicationtoken;
	}
	
	@Override
	public String getSecurityCookieDomain() {
		if(StringUtil.isEmpty(applicationtoken,true)) return null;
		return cookiedomain;
	}
	
	@Override
	public int getSecurityIdleTimeout() {
		if(idletimeout<1) return 1800;
		return idletimeout;
	}
	

	
	@Override
	public DataSource getDataSource(String dataSourceName, DataSource defaultValue) {
		dataSourceName=dataSourceName.trim();
		DataSource[] sources = getDataSources();
		if(!ArrayUtil.isEmpty(sources)) {
			for(int i=0;i<sources.length;i++){
				if(sources[i].getName().equalsIgnoreCase(dataSourceName))
					return sources[i];
			}
		}
		return defaultValue;
	}
	
	@Override
	public DataSource getDataSource(String dataSourceName) throws ApplicationException {
		DataSource source = getDataSource(dataSourceName,null);
		if(source==null)
			throw new ApplicationException("there is no datasource with name ["+dataSourceName+"]");
		return source;
	}
	
	@Override
	public Map<Collection.Key, Map<Collection.Key, Object>> getTagAttributeDefaultValues() {
		return tagDefaultAttributeValues;
	}
	
	@Override
	public Map<Collection.Key, Object> getTagAttributeDefaultValues(String fullname) {
		if(tagDefaultAttributeValues==null) return null;
		return tagDefaultAttributeValues.get(KeyImpl.init(fullname));
	}

	
	@Override
	public void setTagAttributeDefaultValues(Struct sct) {
		if(tagDefaultAttributeValues==null) 
			tagDefaultAttributeValues=new HashMap<Collection.Key, Map<Collection.Key,Object>>();
		initTagDefaultAttributeValues(config, tagDefaultAttributeValues, sct);
	}
	

	public static void initTagDefaultAttributeValues(Config config,Map<Collection.Key, Map<Collection.Key, Object>> tagDefaultAttributeValues, Struct sct) {
		if(sct.size()==0) return;
		ConfigImpl ci = ((ConfigImpl)config);
		
		// first check the core lib without namesapce
		TagLib lib = ci.getCoreTagLib();
		_initTagDefaultAttributeValues(config, lib, tagDefaultAttributeValues, sct,false);
		if(sct.size()==0) return;
		
		// then all the other libs including the namespace
		TagLib[] tlds = ci.getTLDs();
		for(int i=0;i<tlds.length;i++){
			_initTagDefaultAttributeValues(config, tlds[i], tagDefaultAttributeValues, sct,true);
			if(sct.size()==0) return;
		}
	}
	
	private static void _initTagDefaultAttributeValues(Config config,TagLib lib,
			Map<Collection.Key, Map<Collection.Key, Object>> tagDefaultAttributeValues, Struct sct, boolean checkNameSpace) {
		if(sct==null) return;
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		// loop tags
		Struct attrs;
		TagLibTag tag;
		Iterator<Entry<Key, Object>> iit;
		Entry<Key, Object> e;
		Map<Collection.Key,Object> map;
		TagLibTagAttr attr;
		String name;
		while(it.hasNext()){
			e = it.next();
			attrs=Caster.toStruct(e.getValue(),null);
			if(attrs!=null){
				tag=null;
				if(checkNameSpace) {
					name=e.getKey().getLowerString();
					if(StringUtil.startsWithIgnoreCase(name, lib.getNameSpaceAndSeparator())) {
						name=name.substring(lib.getNameSpaceAndSeparator().length());
						tag = lib.getTag(name);
					}
				}
				else
					tag = lib.getTag(e.getKey().getLowerString());
				
				if(tag!=null) {
					sct.removeEL(e.getKey());
					map=new HashMap<Collection.Key, Object>();
					iit = attrs.entryIterator();
					while(iit.hasNext()){
						e = iit.next();
						map.put(KeyImpl.init(e.getKey().getLowerString()),e.getValue());
					}
					tagDefaultAttributeValues.put(KeyImpl.init(tag.getFullName()), map);
				}
			}	
		}
	}

}
