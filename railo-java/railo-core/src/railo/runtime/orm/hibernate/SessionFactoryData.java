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
package railo.runtime.orm.hibernate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.query.QueryPlanCache;

import railo.commons.io.log.Log;
import railo.loader.util.Util;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.hibernate.naming.CFCNamingStrategy;
import railo.runtime.orm.hibernate.naming.DefaultNamingStrategy;
import railo.runtime.orm.hibernate.naming.SmartNamingStrategy;
import railo.runtime.orm.naming.NamingStrategy;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;

public class SessionFactoryData {

	public List<Component> tmpList;
	public Map<String, CFCInfo> cfcs=new HashMap<String, CFCInfo>();
	private final ORMConfiguration ormConf;
	private final DataSource datasource;
	private Configuration configuration=null;
	private SessionFactory factory;
	
	private QueryPlanCache queryPlanCache;
	private NamingStrategy namingStrategy;
	private final HibernateORMEngine engine;
	private Struct tableInfo=CommonUtil.createStruct();
	private String cfcNamingStrategy;

	
	
	public SessionFactoryData(HibernateORMEngine engine,ORMConfiguration ormConf, DataSource datasource) {
		this.engine=engine;
		this.ormConf=ormConf;
		this.datasource=datasource;
	}

	public Configuration getConfiguration(){
		return configuration;
	}
	public DataSource getDataSource(){
		return datasource;
	}
	
	public ORMConfiguration getORMConfiguration(){
		return ormConf;
	}
	
	public SessionFactory getFactory(){
		if(factory==null && configuration!=null) buildSessionFactory();// this should never be happen
		return factory;
	}
	public HibernateORMEngine getEngine(){
		return engine;
	}
	
	public QueryPlanCache getQueryPlanCache()  {
		if(queryPlanCache==null){
			queryPlanCache=new QueryPlanCache((SessionFactoryImplementor) factory);
		}
		return queryPlanCache;
	}

	public void reset() {
		configuration=null;
		if(factory!=null) {
			factory.close();
			factory=null;
		}
		//namingStrategy=null; because the ormconf not change, this has not to change as well
		tableInfo=CommonUtil.createStruct();
	}

	public void buildSessionFactory() {
		if(configuration==null) throw new RuntimeException("cannot build factory because there is no configuration"); // this should never happen
		factory=configuration.buildSessionFactory();
	}

	public void setConfiguration(Log log,String mappings, DatasourceConnection dc) throws PageException, SQLException, IOException {
		this.configuration=HibernateSessionFactory.createConfiguration(log,mappings,dc,this);
	}
	
	public NamingStrategy getNamingStrategy() throws PageException {
		if(namingStrategy==null) {
			String strNamingStrategy=ormConf.namingStrategy();
			if(Util.isEmpty(strNamingStrategy,true)) {
				namingStrategy=DefaultNamingStrategy.INSTANCE;
			}
			else {
				strNamingStrategy=strNamingStrategy.trim();
				if("default".equalsIgnoreCase(strNamingStrategy)) 
					namingStrategy=DefaultNamingStrategy.INSTANCE;
				else if("smart".equalsIgnoreCase(strNamingStrategy)) 
					namingStrategy=SmartNamingStrategy.INSTANCE;
				else {
					CFCNamingStrategy cfcNS = new CFCNamingStrategy(cfcNamingStrategy==null?strNamingStrategy:cfcNamingStrategy);
					cfcNamingStrategy=cfcNS.getComponent().getPageSource().getComponentName();
					namingStrategy=cfcNS;
					
				}
			}
		}
		if(namingStrategy==null) return DefaultNamingStrategy.INSTANCE;
		return namingStrategy;
	}
	
	public Struct getTableInfo(DatasourceConnection dc, String tableName) throws PageException {
		Collection.Key keyTableName=CommonUtil.createKey(tableName);
		Struct columnsInfo = (Struct) tableInfo.get(keyTableName,null);
		if(columnsInfo!=null) return columnsInfo;
		
		columnsInfo = HibernateUtil.checkTable(dc,tableName,this);
    	tableInfo.setEL(keyTableName,columnsInfo);
    	return columnsInfo;
	}
	
	public void checkExistent(PageContext pc,Component cfc) throws PageException {
		if(!cfcs.containsKey(HibernateUtil.id(HibernateCaster.getEntityName(cfc))))
            throw ExceptionUtil.createException(this,null,"there is no mapping definition for component ["+cfc.getAbsName()+"]","");
	}
	
	public String[] getEntityNames() {
		Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
		String[] names=new String[cfcs.size()];
		int index=0;
		while(it.hasNext()){
			names[index++]=HibernateCaster.getEntityName(it.next().getValue().getCFC());
		}
		return names;
	}

	public Component getEntityByEntityName(String entityName,boolean unique) throws PageException {
		Component cfc;
		
		CFCInfo info = cfcs!=null? cfcs.get(entityName.toLowerCase()):null;
		if(info!=null) {
			cfc=info.getCFC();
			return unique?(Component)cfc.duplicate(false):cfc;
		}
		if(tmpList!=null){
			Iterator<Component> it = tmpList.iterator();
			while(it.hasNext()){
				cfc=it.next();
				if(HibernateCaster.getEntityName(cfc).equalsIgnoreCase(entityName))
					return unique?(Component)cfc.duplicate(false):cfc;
			}
		}
		throw ExceptionUtil.createException((ORMSession)null,null,"entity ["+entityName+"] does not exist","");
	}
	

	public Component getEntityByCFCName(String cfcName,boolean unique) throws PageException {
		String name=cfcName;
		int pointIndex=cfcName.lastIndexOf('.');
		if(pointIndex!=-1) {
			name=cfcName.substring(pointIndex+1);
		}
		else 
			cfcName=null;
		
		
		
		Component cfc;
		String[] names=null;
		// search array (array exist when cfcs is in generation)
		
		List<Component> list = tmpList;
		if(list!=null){
			names=new String[list.size()];
			int index=0;
			Iterator<Component> it2 = list.iterator();
			while(it2.hasNext()){
				cfc=it2.next();
				names[index++]=cfc.getName();
				if(HibernateUtil.isEntity(ormConf,cfc,cfcName,name)) //if(cfc.equalTo(name))
					return unique?(Component)cfc.duplicate(false):cfc;
			}
		}
		else {
			// search cfcs
			Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
			Entry<String, CFCInfo> entry;
			while(it.hasNext()){
				entry=it.next();
				cfc=entry.getValue().getCFC();
				if(HibernateUtil.isEntity(ormConf,cfc,cfcName,name)) //if(cfc.instanceOf(name))
					return unique?(Component)cfc.duplicate(false):cfc;
				
				//if(name.equalsIgnoreCase(HibernateCaster.getEntityName(cfc)))
				//	return cfc;
			}
			names=cfcs.keySet().toArray(new String[cfcs.size()]);
		}
		
		// search by entityname //TODO is this ok?
		CFCInfo info = cfcs.get(name.toLowerCase());
		if(info!=null) {
			cfc=info.getCFC();
			return unique?(Component)cfc.duplicate(false):cfc;
		}
		
		throw ExceptionUtil.createException((ORMSession)null,null,"entity ["+name+"] "+(Util.isEmpty(cfcName)?"":"with cfc name ["+cfcName+"] ")+"does not exist, existing  entities are ["+CommonUtil.toList(names, ", ")+"]","");
		
	}
}
