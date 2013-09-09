package railo.runtime.orm.hibernate;

import java.io.Serializable;
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

import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefBoolean;
import railo.commons.lang.types.RefBooleanImpl;
import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQLItem;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMTransaction;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.ArgumentImpl;
import railo.runtime.type.util.CollectionUtil;

public class HibernateORMSession implements ORMSession{

	private final HibernateORMEngine engine;
	private Session _session;
	private DatasourceConnection dc;

	public HibernateORMSession(HibernateORMEngine engine, SessionFactory factory, DatasourceConnection dc){
		this.engine=engine;
		this.dc=dc;
		resetSession(factory);
		//this._session=session;
	}
	
	private Session session(){
		return _session;
	}

	private SessionFactory getSessionFactory(PageContext pc){
		// engine.getSessionFactory(pc);
		return _session.getSessionFactory();
	}
	SessionFactory getSessionFactory(){
		// engine.getSessionFactory(pc);
		return _session.getSessionFactory();
	}
	
	void resetSession(SessionFactory factory) {
		_session = factory.openSession(dc.getConnection());
		_session.setFlushMode(FlushMode.MANUAL);
	}

	@Override
	public ORMEngine getEngine() {
		return engine;
	}
	
	@Override
	public void flush(PageContext pc) throws PageException {
		try {
			session().flush();
		}
		catch(ConstraintViolationException cve){
			PageException pe = HibernateException.toPageException(engine, cve);
			if(pe instanceof PageExceptionImpl && !StringUtil.isEmpty(cve.getConstraintName())) {
				//print.o(cve.getConstraintName());
				((PageExceptionImpl)pe).setAdditional(KeyImpl.init("constraint name"), cve.getConstraintName() );
			}
			throw pe;
		}
		
	}

	@Override
	public void delete(PageContext pc, Object obj) throws PageException {
		if(Decision.isArray(obj)){
			Transaction trans = session().getTransaction();
			if(trans.isActive()) trans.begin();
			else trans=null;
			
			try{
				Iterator it = Caster.toArray(obj).valueIterator();
				while(it.hasNext()){
					_delete(pc,HibernateCaster.toComponent(it.next()));
				}
			}
			catch(Throwable t){
				if(trans!=null)trans.rollback();
				throw Caster.toPageException(t);
			}
			if(trans!=null)trans.commit();
		}
		else _delete(pc,HibernateCaster.toComponent(obj));
	}
	
	public void _delete(PageContext pc, Component cfc) throws PageException {
		engine.checkExistent(pc,cfc);
		//Session session = getSession(pc,cfc);
		
		try{
			session().delete(HibernateCaster.getEntityName(cfc), cfc);
		}
		catch(Throwable t){
			throw Caster.toPageException(t);
		}
	}
	
	
	
	@Override
	public void save(PageContext pc, Object obj,boolean forceInsert) throws PageException {
		Component cfc = HibernateCaster.toComponent(obj);
		//Session session = getSession(pc, cfc);
		String name = HibernateCaster.getEntityName(cfc);
		try {
			if(forceInsert)
				session().save(name, cfc);
			else
					session().saveOrUpdate(name, cfc);
		}
		catch(Throwable t){
			throw HibernateException.toPageException(getEngine(), t);
		}
	}
	
	@Override
	public void reload(PageContext pc,Object obj) throws PageException {
		Component cfc = HibernateCaster.toComponent(obj);
		engine.checkExistent(pc,cfc);
		//Session session = getSession(pc,cfc);
		session().refresh(cfc);
	}
	

	@Override
	public Component create(PageContext pc, String entityName)throws PageException {
		return engine.create(pc,this, entityName,true);
	}
	
	@Override
	public void clear(PageContext pc) throws PageException {
		session().clear();
	}
	
	@Override
	public void evictQueries(PageContext pc) throws PageException {
		evictQueries(pc, null);
	}

	@Override
	public void evictQueries(PageContext pc,String cacheName) throws PageException {
		SessionFactory f = getSessionFactory(pc);
		if(StringUtil.isEmpty(cacheName))f.evictQueries();
		else f.evictQueries(cacheName);
	}
	
	@Override
	public void evictEntity(PageContext pc, String entityName) throws PageException {
		evictEntity(pc, entityName, null);
	}

	@Override
	public void evictEntity(PageContext pc, String entityName, String id) throws PageException {
		SessionFactory f = getSessionFactory(pc);
		
		if(id==null) {
			f.evictEntity(entityName);
		}
		else {
			f.evictEntity(entityName,Caster.toSerializable(id));
		}
	}
	
	@Override
	public void evictCollection(PageContext pc, String entityName, String collectionName) throws PageException {
		evictCollection(pc, entityName, collectionName, null);
	}

	@Override
	public void evictCollection(PageContext pc, String entityName, String collectionName, String id) throws PageException {
		SessionFactory f = getSessionFactory(pc);
		String role=entityName+"."+collectionName;
		if(id==null) {
			f.evictCollection(role);
		}
		else {
			f.evictCollection(role,Caster.toSerializable(id));
		}
	}
	
	 
	
	
	
	
	

	@Override
	public Object executeQuery(PageContext pc,String hql, Array params, boolean unique,Struct queryOptions) throws PageException {
		return _executeQuery(pc, hql, params, unique, queryOptions);
	}

	@Override
	public Object executeQuery(PageContext pc,String hql, Struct params, boolean unique,Struct queryOptions) throws PageException {
		return _executeQuery(pc, hql, params, unique, queryOptions);
	}
	
	public Object _executeQuery(PageContext pc,String hql, Object params, boolean unique,Struct queryOptions) throws PageException {
		try{
			return __executeQuery(pc, hql, params, unique, queryOptions);
		}
		catch(QueryException qe) {
			// argument scope is array and struct at the same time, by default it is handled as struct, if this fails try it as array
			if(params instanceof Argument) {
				try{
					return __executeQuery(pc, hql, ArgumentImpl.toArray((Argument)params), unique, queryOptions);
				}
				catch(Throwable t){t.printStackTrace();}
			}
			throw qe;
		}
		
		
	}
	
	private Object __executeQuery(PageContext pc,String hql, Object params, boolean unique,Struct options) throws PageException {
		//Session session = getSession(pc,null);
		hql=hql.trim();
		org.hibernate.Query query = session().createQuery(hql); 
		// options
		if(options!=null){
			// maxresults
			Object obj=options.get("maxresults",null);
			if(obj!=null) {
				int max=Caster.toIntValue(obj,-1);
				if(max<0) throw new ORMException(engine,"option [maxresults] has an invalid value ["+obj+"], value should be a number bigger or equal to 0");
				query.setMaxResults(max);
			}
			// offset
			obj=options.get("offset",null);
			if(obj!=null) {
				int off=Caster.toIntValue(obj,-1);
				if(off<0) throw new ORMException(engine,"option [offset] has an invalid value ["+obj+"], value should be a number bigger or equal to 0");
				query.setFirstResult(off);
			}
			// readonly
			obj=options.get("readonly",null);
			if(obj!=null) {
				Boolean ro=Caster.toBoolean(obj,null);
				if(ro==null) throw new ORMException(engine,"option [readonly] has an invalid value ["+obj+"], value should be a boolean value");
				query.setReadOnly(ro.booleanValue());
			}
			// timeout
			obj=options.get("timeout",null);
			if(obj!=null) {
				int to=Caster.toIntValue(obj,-1);
				if(to<0) throw new ORMException(engine,"option [timeout] has an invalid value ["+obj+"], value should be a number bigger or equal to 0");
				query.setTimeout(to);
			}
        }
		
		
		// params
		if(params!=null){
			QueryPlanCache cache=engine.getQueryPlanCache(pc);
			HQLQueryPlan plan = cache.getHQLQueryPlan(hql, false, java.util.Collections.EMPTY_MAP);
			ParameterMetadata meta = plan.getParameterMetadata();
			Type type;
			Object obj;
			

			// struct
			if(Decision.isStruct(params)) {
				Struct sct=Caster.toStruct(params);
				Key[] keys = CollectionUtil.keys(sct);
				String name;
				// fix case-senstive
				Struct names=new StructImpl();
				if(meta!=null){
					Iterator<String> it = meta.getNamedParameterNames().iterator();
					while(it.hasNext()){
						name=it.next();
						names.setEL(name, name);
					}
				}
				
				RefBoolean isArray=new RefBooleanImpl();
				for(int i=0;i<keys.length;i++){
					obj=sct.get(keys[i],null);
					if(meta!=null){
						name=(String) names.get(keys[i],null);
						if(name==null) continue; // param not needed will be ignored
						type = meta.getNamedParameterExpectedType(name);
						obj=HibernateCaster.toSQL(engine, type, obj,isArray);
						if(isArray.toBooleanValue())
							query.setParameterList(name, (Object[])obj,type);
						else
							query.setParameter(name, obj,type);
						
						
					}
					else
						query.setParameter(keys[i].getString(), obj);
				}
			}
			
			// array
			else if(Decision.isArray(params)){
				Array arr=Caster.toArray(params);
				Iterator it = arr.valueIterator();
				int index=0;
				SQLItem item;
				RefBoolean isArray=null;//new RefBooleanImpl();
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
						obj=HibernateCaster.toSQL(engine, type, obj,isArray);
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
					throw new ORMException(engine,"parameter array is to small ["+arr.size()+"], need ["+meta.getOrdinalParameterCount()+"] elements");
			}
		}
		
		
		
		// select
		if(StringUtil.startsWithIgnoreCase(hql,"select") || StringUtil.startsWithIgnoreCase(hql,"from")){
			if(unique){
				return uniqueResult(query);
			}
			
			return query.list();
		}
	    // update
		return Caster.toDouble(query.executeUpdate());
	}
	
	
	
	private Object uniqueResult(org.hibernate.Query query) {
		try{
			return query.uniqueResult();
		}
		catch(NonUniqueResultException e){
			List list = query.list();
			if(list.size()>0) return list.iterator().next();
			throw e;
		}
	}

	@Override
	public railo.runtime.type.Query toQuery(PageContext pc, Object obj, String name) throws PageException {
		return HibernateCaster.toQuery(pc,this,obj,name);
	}
	
	@Override
	public void close(PageContext pc) throws PageException {
		session().close();
		((ConfigWebImpl)pc.getConfig()).getDatasourceConnectionPool().releaseDatasourceConnection(dc);
		dc=null;
	}
	
	@Override
	public Component merge(PageContext pc, Object obj) throws PageException {
		Component cfc = HibernateCaster.toComponent(obj);
		
		engine.checkExistent(pc,cfc);
		
		String name=HibernateCaster.getEntityName(cfc);
		
		//Session session = getSession(pc, cfc);
        try	{
            return Caster.toComponent(session().merge(name, cfc));
        }
        catch(HibernateException e) {
        	throw new ORMException(e);
        }
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
		Array arr=new ArrayImpl();
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
		return Caster.toArray(load(pc, name, filter, options, order, false));
	}
	
	@Override
	public Component load(PageContext pc, String cfcName, String id) throws PageException {
		//Component cfc = create(pc,cfcName);
		
		
		Component cfc=engine.create(pc, this,cfcName,false);
		
		String name = HibernateCaster.getEntityName(cfc);
		Object obj=null;
		try{
			ClassMetadata metaData = getSessionFactory(pc).getClassMetadata(name);
			if(metaData==null) throw new ORMException(engine,"could not load meta information for entity ["+name+"]");
			Serializable oId = Caster.toSerializable(
					Caster.castTo(pc, 
							metaData
								.getIdentifierType()
								.getReturnedClass(), 
							id));
			obj=session().get(name,oId);
		}
		catch(Throwable t){
			throw Caster.toPageException(t);
		}
		
		return (Component) obj;
	}
	
	@Override
	public Component loadByExample(PageContext pc, Object obj) throws PageException {
		return Caster.toComponent(loadByExample(pc,obj, true));
	}
	
	@Override
	public Array loadByExampleAsArray(PageContext pc, Object obj) throws PageException {
		return Caster.toArray(loadByExample(pc,obj, false));
	}
	
	private Object loadByExample(PageContext pc, Object obj,  boolean unique) throws PageException {
		 Component cfc=HibernateCaster.toComponent(obj);
		 ComponentScope scope = cfc.getComponentScope();
		 String name=HibernateCaster.getEntityName(cfc);
		 //Session session=getSession(pc, cfc);
		 
		 Object rtn=null;
		 
		 try{
			//trans.begin();
			
			ClassMetadata metaData = getSessionFactory(pc).getClassMetadata(name);
			String idName = metaData.getIdentifierPropertyName();
			Type idType = metaData.getIdentifierType();
		 
			Criteria criteria=session().createCriteria(name);
			if(!StringUtil.isEmpty(idName)){
				Object idValue = scope.get(KeyImpl.init(idName),null);
				if(idValue!=null){
					criteria.add(Restrictions.eq(idName, HibernateCaster.toSQL(engine, idType, idValue,null)));
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
			throw Caster.toPageException(t);
		 }
		 //trans.commit();

		 return rtn;
	}
	
	
	private Object load(PageContext pc, String cfcName, Struct filter, Struct options, String order, boolean unique) throws PageException {
		Component cfc=engine.create(pc, this,cfcName,false);
		
		String name = HibernateCaster.getEntityName(cfc);
		ClassMetadata metaData = null;
		
		Object rtn;
		try{
			//trans.begin();
			
			Criteria criteria = session().createCriteria(name);
			
			// filter
			if(filter!=null && !filter.isEmpty()){
				
				metaData = getSessionFactory(pc).getClassMetadata(name);
				
				
				
				Object value;
				Map.Entry entry;
				Iterator it = filter.entrySet().iterator();
				String colName;
				while(it.hasNext()){
					entry=(Entry) it.next();
					colName=HibernateUtil.validateColumnName(metaData, Caster.toString(entry.getKey()));
					Type type = HibernateUtil.getPropertyType(metaData,colName,null);
					value=HibernateCaster.toSQL(engine,type,entry.getValue(),null);
					if(value!=null)	criteria.add(Restrictions.eq(colName, value));
					else 			criteria.add(Restrictions.isNull(colName));
					
					
					
					
				}
			}
			
			// options
			boolean ignoreCase=false;
			if(options!=null && !options.isEmpty()){
				// ignorecase
				Boolean ignorecase=Caster.toBoolean(options.get("ignorecase",null),null);
		        if(ignorecase!=null)ignoreCase=ignorecase.booleanValue();
		        
				// offset
				int offset=Caster.toIntValue(options.get("offset",null),0);
				if(offset>0) criteria.setFirstResult(offset);
		        
				// maxResults
				int max=Caster.toIntValue(options.get("maxresults",null),-1);
				if(max>-1) criteria.setMaxResults(max);
		        
				// cacheable
				Boolean cacheable=Caster.toBoolean(options.get("cacheable",null),null);
		        if(cacheable!=null)criteria.setCacheable(cacheable.booleanValue());
		        
		        // MUST cacheName ?
		        
				// maxResults
				int timeout=Caster.toIntValue(options.get("timeout",null),-1);
				if(timeout>-1) criteria.setTimeout(timeout);
			}
			
			// order 
			if(!StringUtil.isEmpty(order)){
				if(metaData==null)metaData = getSessionFactory(pc).getClassMetadata(name);
				
				String[] arr = railo.runtime.type.util.ListUtil.listToStringArray(order, ',');
				railo.runtime.type.util.ListUtil.trimItems(arr);
		        String[] parts;
		        String col;
		        boolean isDesc;
		        Order _order;
		        //ColumnInfo ci;
		        for(int i=0;i<arr.length;i++) {
		        	parts=railo.runtime.type.util.ListUtil.toStringArray(railo.runtime.type.util.ListUtil.listToArray(arr[i],  " \t\n\b\r"));
		        	railo.runtime.type.util.ListUtil.trimItems(parts);
		            col=parts[0];
		            
		            col=HibernateUtil.validateColumnName(metaData, col);
					isDesc=false;
					if(parts.length>1){
						if(parts[1].equalsIgnoreCase("desc"))isDesc=true;
						else if(!parts[1].equalsIgnoreCase("asc")){
							throw new ORMException("invalid order direction defintion ["+parts[1]+"]","valid values are [asc, desc]");
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
			throw Caster.toPageException(t);
		}
		
		return rtn;
	}
	
	
	

	@Override
	public Session getRawSession() {
		return session();
	}

	@Override
	public boolean isValid() {
		return session()!=null && session().isOpen();
	}

	@Override
	public ORMTransaction getTransaction(boolean autoManage) {
		return new HibernateORMTransaction(session(),autoManage);
	}
}
