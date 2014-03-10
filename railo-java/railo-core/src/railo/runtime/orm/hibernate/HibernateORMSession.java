package railo.runtime.orm.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.query.HQLQueryPlan;
import org.hibernate.engine.query.ParameterMetadata;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import railo.commons.lang.types.RefBoolean;
import railo.loader.util.Util;
import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQLItem;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMTransaction;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.Argument;

public class HibernateORMSession implements ORMSession {

	private SessionFactoryData data;
	//private DataSource[] sources;
	private DatasourceConnection[] connections;
	//private Map<DataSource,Session> _sessions=new HashMap<DataSource, Session>();
	
	//private Map<Key,DataSource> _sources=new HashMap<Key, DataSource>();
	private Map<Key,Session> _sessions=new HashMap<Key, Session>();

	public HibernateORMSession(PageContext pc,SessionFactoryData data) throws PageException{
		this.data=data;
		//this.dc=dc;
		DataSource[] sources = data.getDataSources();
		connections=new DatasourceConnection[sources.length];
		
		for(int i=0;i<sources.length;i++){
			connections[i]=CommonUtil.getDatasourceConnection(pc, sources[i]);
			resetSession(data.getFactory(KeyImpl.init(sources[i].getName())),connections[i]);
		}
	}
	
	/*private Session session(){
		return _session;
	}*/

	private Session getSession(Key datasSourceName) throws PageException{
		Session s = _sessions.get(datasSourceName);
		if(s!=null) return s; 
		
		railo.commons.lang.ExceptionUtil.similarKeyMessage(
				_sessions.keySet().toArray(new Key[_sessions.size()])
				,datasSourceName.getString(),"datasource","datasources",true);
		
		throw ExceptionUtil.createException(data, null, "there is no Session for the datasource ["+datasSourceName+"]",null);
	}
	
	public SessionFactoryData getSessionFactoryData(){
		return data;
	}
	SessionFactory getSessionFactory(Key datasSourceName) throws PageException{
		Session s = getSession(datasSourceName);
		return s.getSessionFactory();
	}

	
	void resetSession(PageContext pc,SessionFactory factory, Key dataSourceName, SessionFactoryData data) throws PageException { 
		for(int i=0;i<connections.length;i++){
			if(dataSourceName.equals(connections[i].getDatasource().getName())) {
				resetSession(factory, connections[i]);
				return;
			}
		}
		DataSource ds = data.getDataSource(dataSourceName);
		DatasourceConnection dc = CommonUtil.getDatasourceConnection(pc, ds);
		try{
			resetSession(factory, dc);
		}
		finally {
			CommonUtil.releaseDatasourceConnection(pc, dc);
		}
	}
	
	void resetSession(SessionFactory factory, DatasourceConnection dc) { 
		_sessions.clear();
		Session session;
		_sessions.put(KeyImpl.init(dc.getDatasource().getName()), session=factory.openSession(dc.getConnection()));
		session.setFlushMode(FlushMode.MANUAL);
	}

	@Override
	public ORMEngine getEngine() {
		return data.getEngine();
	}
	
	@Override
	public void flush(PageContext pc) throws PageException {
		try {
			Iterator<Session> it = _sessions.values().iterator();
			while(it.hasNext()){
				it.next().flush();
			}
		}
		catch(ConstraintViolationException cve){
			PageException pe = ExceptionUtil.createException(this,null,cve);
			if(!Util.isEmpty(cve.getConstraintName())) {
				ExceptionUtil.setAdditional(pe, CommonUtil.createKey("constraint name"), cve.getConstraintName());
			}
			throw pe;
		}
		catch(Throwable t) {
			throw CommonUtil.toPageException(t);
		}
		
	}

	@Override
	public void delete(PageContext pc, Object obj) throws PageException {
		if(CommonUtil.isArray(obj)){
			
			// convert to a usable structure
			Map<Key,List<Component>> cfcs=new HashMap<Key, List<Component>>();
			{
				Array arr = CommonUtil.toArray(obj);
				Iterator<?> it = arr.valueIterator();
				Component cfc;
				
				Key dsn;
				List<Component> list;
				while(it.hasNext()){
					cfc = HibernateCaster.toComponent(it.next());
					dsn = KeyImpl.init(ORMUtil.getDataSourceName(pc, cfc));
					list = cfcs.get(dsn);
					if(list==null)cfcs.put(dsn, list=new ArrayList<Component>());
					list.add(cfc);
				}
			}
			
			
			Iterator<Entry<Key, List<Component>>> it = cfcs.entrySet().iterator();
			while(it.hasNext()){
				Entry<Key, List<Component>> e = it.next();
				Transaction trans = getSession(e.getKey()).getTransaction();
				if(trans.isActive()) trans.begin();
				else trans=null;
				
				try{
					Iterator<Component> _it = e.getValue().iterator();
					while(_it.hasNext()){
						_delete(pc,_it.next(),e.getKey());
					}
				}
				catch(Throwable t){
					if(trans!=null)trans.rollback();
					throw CommonUtil.toPageException(t);
				}
				if(trans!=null)trans.commit();
			}
		}
		else _delete(pc,HibernateCaster.toComponent(obj),null);
	}
	
	public void _delete(PageContext pc, Component cfc, Key dsn) throws PageException {
		if(dsn==null)dsn = KeyImpl.init(ORMUtil.getDataSourceName(pc, cfc));
		data.checkExistent(pc,cfc);
		try{
			getSession(dsn).delete(HibernateCaster.getEntityName(cfc), cfc);
		}
		catch(Throwable t){
			throw CommonUtil.toPageException(t);
		}
	}
	
	
	
	@Override
	public void save(PageContext pc, Object obj,boolean forceInsert) throws PageException {
		Component cfc = HibernateCaster.toComponent(obj);
		String name = HibernateCaster.getEntityName(cfc);
		Key dsn = KeyImpl.init(ORMUtil.getDataSourceName(pc, cfc));
		
		try {
			Session session = getSession(dsn);
			if(forceInsert)
				session.save(name, cfc);
			else
				session.saveOrUpdate(name, cfc);
		}
		catch(Throwable t){
			throw ExceptionUtil.createException(this,null,t);
		}
	}
	
	@Override
	public void reload(PageContext pc,Object obj) throws PageException {
		Component cfc = HibernateCaster.toComponent(obj);
		Key dsn = KeyImpl.init(ORMUtil.getDataSourceName(pc, cfc));
		data.checkExistent(pc,cfc);
		getSession(dsn).refresh(cfc);
	}
	

	@Override
	public Component create(PageContext pc, String entityName)throws PageException {
		return data.getEngine().create(pc,this, entityName,true);
	}
	
	@Override
	public void clear(PageContext pc) throws PageException {
		Iterator<Session> it = _sessions.values().iterator();
		while(it.hasNext()){
			it.next().clear();
		}
	}
	public void clear(PageContext pc, Key dataSourceName) throws PageException {
		getSession(dataSourceName).clear();
	}
	
	@Override
	public void evictQueries(PageContext pc) throws PageException {
		evictQueries(pc, null);
	}

	@Override
	public void evictQueries(PageContext pc,String cacheName) throws PageException {
		Iterator<Session> it = _sessions.values().iterator();
		while(it.hasNext()){
			SessionFactory f = it.next().getSessionFactory();
			if(Util.isEmpty(cacheName))f.evictQueries();
			else f.evictQueries(cacheName);
		}
	}
	
	@Override
	public void evictEntity(PageContext pc, String entityName) throws PageException {
		evictEntity(pc, entityName, null);
	}

	@Override
	public void evictEntity(PageContext pc, String entityName, String id) throws PageException {
		Iterator<Session> it = _sessions.values().iterator();
		while(it.hasNext()){
			SessionFactory f = it.next().getSessionFactory();
			if(id==null) f.evictEntity(entityName);
			else f.evictEntity(entityName,CommonUtil.toSerializable(id));
		}
	}
	
	@Override
	public void evictCollection(PageContext pc, String entityName, String collectionName) throws PageException {
		evictCollection(pc, entityName, collectionName, null);
	}

	@Override
	public void evictCollection(PageContext pc, String entityName, String collectionName, String id) throws PageException {
		String role=entityName+"."+collectionName;
		
		Iterator<Session> it = _sessions.values().iterator();
		while(it.hasNext()){
			SessionFactory f = it.next().getSessionFactory();
			if(id==null) f.evictCollection(role);
			else f.evictCollection(role,CommonUtil.toSerializable(id));
		}
	}

	@Override
	public Object executeQuery(PageContext pc, String dataSourceName,String hql, Array params, boolean unique,Struct queryOptions) throws PageException {
		return _executeQuery(pc, dataSourceName,hql, params, unique, queryOptions);
	}

	@Override
	public Object executeQuery(PageContext pc,String dataSourceName, String hql, Struct params, boolean unique,Struct queryOptions) throws PageException {
		return _executeQuery(pc, dataSourceName,hql, params, unique, queryOptions);
	}
	
	private Object _executeQuery(PageContext pc, String dataSourceName,String hql, Object params, boolean unique,Struct queryOptions) throws PageException {
		Key dsn;
		if(dataSourceName==null)dsn=KeyImpl.init(ORMUtil.getDataSource(pc).getName());
		else dsn=KeyImpl.init(dataSourceName);
		
		Session s=getSession(dsn);
		try{
			return __executeQuery(pc,s,dsn, hql, params, unique, queryOptions);
		}
		catch(QueryException qe) {
			// argument scope is array and struct at the same time, by default it is handled as struct, if this fails try it as array
			if(params instanceof Argument) {
				try{
					return __executeQuery(pc, s, dsn, hql, CommonUtil.toArray((Argument)params), unique, queryOptions);
				}
				catch(Throwable t){t.printStackTrace();}
			}
			throw qe;
		}
		
	}
	
	private Object __executeQuery(PageContext pc, Session session,Key dsn,String hql, Object params, boolean unique,Struct options) throws PageException {
		//Session session = getSession(pc,null);
		hql=hql.trim();
		org.hibernate.Query query = session.createQuery(hql); 
		// options
		if(options!=null){
			// maxresults
			Object obj=options.get("maxresults",null);
			if(obj!=null) {
				int max=CommonUtil.toIntValue(obj,-1);
				if(max<0) throw ExceptionUtil.createException(this,null,"option [maxresults] has an invalid value ["+obj+"], value should be a number bigger or equal to 0",null);
				query.setMaxResults(max);
			}
			// offset
			obj=options.get("offset",null);
			if(obj!=null) {
				int off=CommonUtil.toIntValue(obj,-1);
				if(off<0) throw ExceptionUtil.createException(this,null,"option [offset] has an invalid value ["+obj+"], value should be a number bigger or equal to 0",null);
				query.setFirstResult(off);
			}
			// readonly
			obj=options.get("readonly",null);
			if(obj!=null) {
				Boolean ro=CommonUtil.toBoolean(obj,null);
				if(ro==null) throw ExceptionUtil.createException(this,null,"option [readonly] has an invalid value ["+obj+"], value should be a boolean value",null);
				query.setReadOnly(ro.booleanValue());
			}
			// timeout
			obj=options.get("timeout",null);
			if(obj!=null) {
				int to=CommonUtil.toIntValue(obj,-1);
				if(to<0) throw ExceptionUtil.createException(this,null,"option [timeout] has an invalid value ["+obj+"], value should be a number bigger or equal to 0",null);
				query.setTimeout(to);
			}
        }
		
		
		// params
		if(params!=null){
			QueryPlanCache cache=data.getQueryPlanCache(dsn);
			HQLQueryPlan plan = cache.getHQLQueryPlan(hql, false, java.util.Collections.EMPTY_MAP);
			ParameterMetadata meta = plan.getParameterMetadata();
			Type type;
			Object obj;
			

			// struct
			if(CommonUtil.isStruct(params)) {
				Struct sct=CommonUtil.toStruct(params);
				Key[] keys	 = CommonUtil.keys(sct);
				String name;
				// fix case-senstive
				Struct names=CommonUtil.createStruct();
				if(meta!=null){
					Iterator<String> it = meta.getNamedParameterNames().iterator();
					while(it.hasNext()){
						name=it.next();
						names.setEL(name, name);
					}
				}
				
				RefBoolean isArray=CommonUtil.createRefBoolean();
				for(int i=0;i<keys.length;i++){
					obj=sct.get(keys[i],null);
					if(meta!=null){
						name=(String) names.get(keys[i],null);
						if(name==null) continue; // param not needed will be ignored
						type = meta.getNamedParameterExpectedType(name);
						obj=HibernateCaster.toSQL(type, obj,isArray);
						if(isArray.toBooleanValue()) {
							if(obj instanceof Object[])
								query.setParameterList(name, (Object[])obj,type);
							else if(obj instanceof List)
								query.setParameterList(name, (List)obj,type);
							else
								query.setParameterList(name, Caster.toList(obj),type);
						}
						else
							query.setParameter(name, obj,type);
						
						
					}
					else
						query.setParameter(keys[i].getString(), obj);
				}
			}
			
			// array
			else if(CommonUtil.isArray(params)){
				Array arr=CommonUtil.toArray(params);
				Iterator it = arr.valueIterator();
				int index=0;
				SQLItem item;
				RefBoolean isArray=null;
				while(it.hasNext()){
					obj=it.next();
					if(obj instanceof SQLItem) {
						item=(SQLItem) obj;
						obj=item.getValue();
						//HibernateCaster.toHibernateType(item.getType(), null); MUST
						//query.setParameter(index, item.getValue(),type);
					}
					if(meta!=null){
						type = meta.getOrdinalParameterExpectedType(index+1);
						obj=HibernateCaster.toSQL(type, obj,isArray);
						// TOOD can the following be done somehow
						//if(isArray.toBooleanValue())
						//	query.setParameterList(index, (Object[])obj,type);
						//else
							query.setParameter(index, obj,type);
					}
					else
						query.setParameter(index, obj);
					index++;
				}
				if(meta.getOrdinalParameterCount()>index)
					throw ExceptionUtil.createException(this,null,"parameter array is to small ["+arr.size()+"], need ["+meta.getOrdinalParameterCount()+"] elements",null);
			}
		}
		
		
		
		// select
		String lcHQL = hql.toLowerCase();
		if(lcHQL.startsWith("select") || lcHQL.startsWith("from")){
			if(unique){
				return uniqueResult(query);
			}
			
			return query.list();
		}
	    // update
		return new Double(query.executeUpdate());
	}
	
	
	
	private Object uniqueResult(org.hibernate.Query query) throws PageException {
		try{
			return query.uniqueResult();
		}
		catch(NonUniqueResultException e){
			List list = query.list();
			if(list.size()>0) return list.iterator().next();
			throw CommonUtil.toPageException(e);
		}
		catch(Throwable t){
			throw CommonUtil.toPageException(t);
		}
	}

	@Override
	public railo.runtime.type.Query toQuery(PageContext pc, Object obj, String name) throws PageException {
		return HibernateCaster.toQuery(pc,this,obj,name);
	}
	
	@Override
	public void close(PageContext pc) throws PageException {
		Iterator<Session> it = _sessions.values().iterator();
		while(it.hasNext()){
			Session s = it.next();
			s.close();
		}
		for(int i=0;i<connections.length;i++){
			CommonUtil.releaseDatasourceConnection(pc, connections[i]);
		}
		connections=null;
	}
	
	@Override
	public Component merge(PageContext pc, Object obj) throws PageException {
		Component cfc = HibernateCaster.toComponent(obj);
		CFCInfo info = data.checkExistent(pc,cfc);
		
		String name=HibernateCaster.getEntityName(cfc);
		
		return CommonUtil.toComponent(getSession(KeyImpl.init(info.getDataSource().getName())).merge(name, cfc));
	}
	

	@Override
	public Component load(PageContext pc, String name, Struct filter) throws PageException {
		return (Component) load(pc, name, filter, null, null, true);
	}

	@Override
	public Array loadAsArray(PageContext pc, String name, Struct filter) throws PageException {
		return loadAsArray(pc, name, filter,null,null);
	}
	
	@Override
	public Array loadAsArray(PageContext pc, String name, String id, String order) throws PageException{
		return loadAsArray(pc, name, id);// order is ignored in this case ACF compatibility
	}
	
	@Override
	public Array loadAsArray(PageContext pc, String name, String id) throws PageException {
		Array arr=CommonUtil.createArray();
		Component c = load(pc, name, id);
		if(c!=null)arr.append(c);
		return arr;
	}
	
	@Override
	public Array loadAsArray(PageContext pc, String name, Struct filter, Struct options) throws PageException {
		return loadAsArray(pc, name, filter,options,null);
	}
	
	@Override
	public Array loadAsArray(PageContext pc, String name, Struct filter, Struct options, String order) throws PageException {
		return CommonUtil.toArray(load(pc, name, filter, options, order, false));
	}
	
	@Override
	public Component load(PageContext pc, String cfcName, String id) throws PageException {
		//Component cfc = create(pc,cfcName);
		
		
		Component cfc=data.getEngine().create(pc, this,cfcName,false);
		Key dsn = KeyImpl.init(ORMUtil.getDataSourceName(pc, cfc));
		Session sess = getSession(dsn);
		String name = HibernateCaster.getEntityName(cfc);
		Object obj=null;
		try{
			ClassMetadata metaData = sess.getSessionFactory().getClassMetadata(name);
			if(metaData==null) throw ExceptionUtil.createException(this,null,"could not load meta information for entity ["+name+"]",null);
			Serializable oId = CommonUtil.toSerializable(
					CommonUtil.castTo(pc, 
							metaData
								.getIdentifierType()
								.getReturnedClass(), 
							id));
			obj=sess.get(name,oId);
		}
		catch(Throwable t){
			throw CommonUtil.toPageException(t);
		}
		
		return (Component) obj;
	}
	
	@Override
	public Component loadByExample(PageContext pc, Object obj) throws PageException {
		return CommonUtil.toComponent(loadByExample(pc,obj, true));
	}
	
	@Override
	public Array loadByExampleAsArray(PageContext pc, Object obj) throws PageException {
		return CommonUtil.toArray(loadByExample(pc,obj, false));
	}
	
	private Object loadByExample(PageContext pc, Object obj,  boolean unique) throws PageException {
		Component cfc=HibernateCaster.toComponent(obj);
		Key dsn = KeyImpl.init(ORMUtil.getDataSourceName(pc, cfc));
		ComponentScope scope = cfc.getComponentScope();
		String name=HibernateCaster.getEntityName(cfc);
		Session sess = getSession(dsn);
		Object rtn=null;

		try{
			//trans.begin();
			
			ClassMetadata metaData = sess.getSessionFactory().getClassMetadata(name);
			String idName = metaData.getIdentifierPropertyName();
			Type idType = metaData.getIdentifierType();
		 
			Criteria criteria=sess.createCriteria(name);
			if(!Util.isEmpty(idName)){
				Object idValue = scope.get(CommonUtil.createKey(idName),null);
				if(idValue!=null){
					criteria.add(Restrictions.eq(idName, HibernateCaster.toSQL(idType, idValue,null)));
				}
			}
			criteria.add(Example.create(cfc));
	     
	     	// execute
			
			if(!unique){
				rtn = criteria.list();
			}
			else {
				//Map map=(Map) criteria.uniqueResult();
				rtn= criteria.uniqueResult();
			}
		 }
		 catch(Throwable t){
			// trans.rollback();
			throw CommonUtil.toPageException(t);
		 }
		 //trans.commit();

		 return rtn;
	}
	
	
	private Object load(PageContext pc, String cfcName, Struct filter, Struct options, String order, boolean unique) throws PageException {
		Component cfc=data.getEngine().create(pc, this,cfcName,false);
		Key dsn = KeyImpl.init(ORMUtil.getDataSourceName(pc, cfc));
		Session sess = getSession(dsn);

		String name = HibernateCaster.getEntityName(cfc);
		ClassMetadata metaData = null;
		
		Object rtn;
		try{
			//trans.begin();
			
			Criteria criteria = sess.createCriteria(name);
			
			// filter
			if(filter!=null && !filter.isEmpty()){
				metaData = sess.getSessionFactory().getClassMetadata(name);
				Object value;
				Entry<Key, Object> entry;
				Iterator<Entry<Key, Object>> it = filter.entryIterator();
				String colName;
				while(it.hasNext()){
					entry = it.next();
					colName=HibernateUtil.validateColumnName(metaData, CommonUtil.toString(entry.getKey()));
					Type type = HibernateUtil.getPropertyType(metaData,colName,null);
					value=entry.getValue();
					if(!(value instanceof Component)) 
						value=HibernateCaster.toSQL(type,value,null);
					
					if(value!=null)	criteria.add(Restrictions.eq(colName, value));
					else 			criteria.add(Restrictions.isNull(colName));
				}
			}
			
			// options
			boolean ignoreCase=false;
			if(options!=null && !options.isEmpty()){
				// ignorecase
				Boolean ignorecase=CommonUtil.toBoolean(options.get("ignorecase",null),null);
		        if(ignorecase!=null)ignoreCase=ignorecase.booleanValue();
		        
				// offset
				int offset=CommonUtil.toIntValue(options.get("offset",null),0);
				if(offset>0) criteria.setFirstResult(offset);
		        
				// maxResults
				int max=CommonUtil.toIntValue(options.get("maxresults",null),-1);
				if(max>-1) criteria.setMaxResults(max);
		        
				// cacheable
				Boolean cacheable=CommonUtil.toBoolean(options.get("cacheable",null),null);
		        if(cacheable!=null)criteria.setCacheable(cacheable.booleanValue());
		        
		        // MUST cacheName ?
		        
				// maxResults
				int timeout=CommonUtil.toIntValue(options.get("timeout",null),-1);
				if(timeout>-1) criteria.setTimeout(timeout);
			}
			
			// order 
			if(!Util.isEmpty(order)){
				if(metaData==null)metaData = sess.getSessionFactory().getClassMetadata(name);
				
				String[] arr = CommonUtil.toStringArray(order, ',');
				CommonUtil.trimItems(arr);
		        String[] parts;
		        String col;
		        boolean isDesc;
		        Order _order;
		        //ColumnInfo ci;
		        for(int i=0;i<arr.length;i++) {
		        	parts=CommonUtil.toStringArray(arr[i],  " \t\n\b\r");
		        	CommonUtil.trimItems(parts);
		            col=parts[0];
		            
		            col=HibernateUtil.validateColumnName(metaData, col);
					isDesc=false;
					if(parts.length>1){
						if(parts[1].equalsIgnoreCase("desc"))isDesc=true;
						else if(!parts[1].equalsIgnoreCase("asc")){
							throw ExceptionUtil.createException((ORMSession)null,null,"invalid order direction defintion ["+parts[1]+"]","valid values are [asc, desc]");
						}
						
					}
					_order=isDesc?Order.desc(col):Order.asc(col);
		            if(ignoreCase)_order.ignoreCase();
		            
		            criteria.addOrder(_order);
	            	
		        }
			}
			
			// execute
			if(!unique){
				rtn = HibernateCaster.toCFML(criteria.list());
			}
			else {
				rtn= HibernateCaster.toCFML(criteria.uniqueResult());
			}
			
			
		}
		catch(Throwable t){
			throw CommonUtil.toPageException(t);
		}
		
		return rtn;
	}
	
	
	

	@Override
	public Session getRawSession(String dsn) throws PageException {
		return getSession(KeyImpl.init(dsn));
	}
	
	@Override
	public SessionFactory getRawSessionFactory(String dsn) throws PageException {
		return getSession(KeyImpl.init(dsn)).getSessionFactory();
	}

	@Override
	public boolean isValid(DataSource ds) {
		Session sess = _sessions.get(ds);
		return sess!=null && sess.isOpen();
	}
	
	@Override
	public boolean isValid() {
		if(_sessions.size()==0) return false;
		Iterator<Session> it = _sessions.values().iterator();
		while(it.hasNext()){
			if(!it.next().isOpen()) return false;
		}
		return true;
	}

	@Override
	public ORMTransaction getTransaction(String dsn,boolean autoManage) throws PageException {
		return new HibernateORMTransaction(getSession(KeyImpl.init(dsn)),autoManage);
	}
	
	@Override
	public String[] getEntityNames() {
		List<String> names = data.getEntityNames();
		return names.toArray(new String[names.size()]);
	}

	@Override
	public DataSource[] getDataSources() {
		return data.getDataSources();
	} 
}
