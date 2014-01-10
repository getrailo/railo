package railo.runtime.orm.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.EntityMode;
import org.hibernate.SessionFactory;
import org.hibernate.engine.query.QueryPlanCache;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.tuple.entity.EntityTuplizerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import railo.commons.io.res.Resource;
import railo.loader.util.Util;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourcePro;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.listener.ApplicationContextPro;
import railo.runtime.op.Duplicator;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.orm.hibernate.event.AllEventListener;
import railo.runtime.orm.hibernate.event.EventListener;
import railo.runtime.orm.hibernate.event.InterceptorImpl;
import railo.runtime.orm.hibernate.event.PostDeleteEventListenerImpl;
import railo.runtime.orm.hibernate.event.PostInsertEventListenerImpl;
import railo.runtime.orm.hibernate.event.PostLoadEventListenerImpl;
import railo.runtime.orm.hibernate.event.PostUpdateEventListenerImpl;
import railo.runtime.orm.hibernate.event.PreDeleteEventListenerImpl;
import railo.runtime.orm.hibernate.event.PreInsertEventListenerImpl;
import railo.runtime.orm.hibernate.event.PreLoadEventListenerImpl;
import railo.runtime.orm.hibernate.event.PreUpdateEventListenerImpl;
import railo.runtime.orm.hibernate.tuplizer.AbstractEntityTuplizerImpl;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.Collection;
import railo.runtime.type.util.ArrayUtil;

public class HibernateORMEngine implements ORMEngine {
	
	private static final int INIT_NOTHING=1;
	private static final int INIT_CFCS=2;
	private static final int INIT_ALL=2;

	private Map<String,SessionFactoryData> factories=new ConcurrentHashMap<String, SessionFactoryData>();
	
	public HibernateORMEngine() {}

	@Override
	public void init(PageContext pc) throws PageException{
		getSessionFactory(pc,INIT_CFCS);
	}
		
	@Override
	public ORMSession createSession(PageContext pc) throws PageException {
		ApplicationContextPro appContext = (ApplicationContextPro) pc.getApplicationContext();
		Object o=appContext.getORMDataSource();
		
		DataSource ds=o instanceof DataSource?
				(DataSource)o:
				CommonUtil.getDataSource(pc,CommonUtil.toString(o));
		
				
		DatasourceConnection dc = CommonUtil.getDatasourceConnection(pc,ds);
			
		try{
			SessionFactoryData data = getSessionFactoryData(pc, INIT_NOTHING);
			return new HibernateORMSession(data,dc);
		}
		catch(PageException pe){
			//manager.releaseConnection(pc, dc);// connection is closed when session ends
			throw pe;
		}
	}
	

	QueryPlanCache getQueryPlanCache(PageContext pc) throws PageException {
		return getSessionFactoryData(pc,INIT_NOTHING).getQueryPlanCache();
	}

	@Override
	public SessionFactory getSessionFactory(PageContext pc) throws PageException{
		return getSessionFactory(pc,INIT_NOTHING);
	}
	
	public boolean reload(PageContext pc, boolean force) throws PageException {
		if(force) {
			getSessionFactoryData(pc, INIT_ALL);
		}
		else {
			if(factories.containsKey(hash(pc)))return false;
		}
		getSessionFactoryData(pc, INIT_CFCS);
		return true;
	}

	private SessionFactory getSessionFactory(PageContext pc,int initType) throws PageException {
		return getSessionFactoryData(pc, initType).getFactory();
	}

	private SessionFactoryData getSessionFactoryData(PageContext pc,int initType) throws PageException {
		ApplicationContextPro appContext = (ApplicationContextPro) pc.getApplicationContext();
		if(!appContext.isORMEnabled())
			throw ExceptionUtil.createException((ORMSession)null,null,"ORM is not enabled","");
		
		
		// datasource
		DataSource ds = ORMUtil.getDataSource(pc);
		ORMConfiguration ormConf=appContext.getORMConfiguration();
		String key = hash(ormConf,ds);
		SessionFactoryData data = factories.get(key);
		if(initType==INIT_ALL && data!=null) {
			data.reset();
			data=null;
		}
		if(data==null) {
			data=new SessionFactoryData(this,ormConf,ds);
			factories.put(key, data);
		}
		
		
		// config
		try{
			//arr=null;
			if(initType!=INIT_NOTHING){
				synchronized (data) {
					
					if(ormConf.autogenmap()){
						data.tmpList=HibernateSessionFactory.loadComponents(pc, this, ormConf);
						
						data.cfcs.clear();
					}
					else 
						throw ExceptionUtil.createException(data,null,"orm setting autogenmap=false is not supported yet",null);
				
					// load entities
					if(!ArrayUtil.isEmpty(data.tmpList)) {
						data.getNamingStrategy();// caled here to make sure, it is called in the right context the first one
						DatasourceConnection dc = CommonUtil.getDatasourceConnection(pc, ds);
						try {
							Iterator<Component> it = data.tmpList.iterator();
							while(it.hasNext()){
								createMapping(pc,it.next(),dc,ormConf,data);
							}
						}
						finally {
							CommonUtil.releaseDatasourceConnection(pc, dc);
						}
						if(data.tmpList.size()!=data.cfcs.size()){
							Component cfc;
							String name,lcName;
							Map<String,String> names=new HashMap<String,String>();
							Iterator<Component> it = data.tmpList.iterator();
							while(it.hasNext()){
								cfc=it.next();
								name=HibernateCaster.getEntityName(cfc);
								lcName=name.toLowerCase();
								if(names.containsKey(lcName))
									throw ExceptionUtil.createException(data,null,"Entity Name ["+name+"] is ambigous, ["+names.get(lcName)+"] and ["+cfc.getPageSource().getDisplayPath()+"] use the same entity name.",""); 
								names.put(lcName,cfc.getPageSource().getDisplayPath());
							}	
						}
					}
				}
			}
		}
		finally {
			data.tmpList=null;
		}
				
		// already initialized for this application context
		if(data.getConfiguration()!=null) return data;

		//MUST
		//cacheconfig
		//cacheprovider
		//...
		
		String mappings=HibernateSessionFactory.createMappings(ormConf,data);
		
		DatasourceConnection dc = CommonUtil.getDatasourceConnection(pc,ds);
		try{
			data.setConfiguration(mappings,dc);
		} 
		catch (Exception e) {
			throw CommonUtil.toPageException(e);
		}
		finally {
			CommonUtil.releaseDatasourceConnection(pc, dc);
		}
		
		addEventListeners(pc, data);
		
		EntityTuplizerFactory tuplizerFactory = data.getConfiguration().getEntityTuplizerFactory();
		//tuplizerFactory.registerDefaultTuplizerClass(EntityMode.MAP, CFCEntityTuplizer.class);
		//tuplizerFactory.registerDefaultTuplizerClass(EntityMode.MAP, MapEntityTuplizer.class);
		tuplizerFactory.registerDefaultTuplizerClass(EntityMode.MAP, AbstractEntityTuplizerImpl.class);
		tuplizerFactory.registerDefaultTuplizerClass(EntityMode.POJO, AbstractEntityTuplizerImpl.class);
		//tuplizerFactory.registerDefaultTuplizerClass(EntityMode.POJO, AbstractEntityTuplizerImpl.class);
		
		//configuration.setEntityResolver(new CFCEntityResolver());
		//configuration.setEntityNotFoundDelegate(new EntityNotFoundDelegate());
		
		
		
		data.buildSessionFactory();
		return data;
	}
	
	private static void addEventListeners(PageContext pc, SessionFactoryData data) throws PageException {
		if(!data.getORMConfiguration().eventHandling()) return;
		String eventHandler = data.getORMConfiguration().eventHandler();
		AllEventListener listener=null;
		if(!Util.isEmpty(eventHandler,true)){
			//try {
				Component c = pc.loadComponent(eventHandler.trim());
				
				listener = new AllEventListener(c);
		        //config.setInterceptor(listener);
			//}catch (PageException e) {e.printStackTrace();}
		}
		data.getConfiguration().setInterceptor(new InterceptorImpl(listener));
        EventListeners listeners = data.getConfiguration().getEventListeners();
        
        // post delete
		List<EventListener> 
		list=merge(listener,data.cfcs,CommonUtil.POST_DELETE);
		listeners.setPostDeleteEventListeners(list.toArray(new PostDeleteEventListener[list.size()]));
		
        // post insert
		list=merge(listener,data.cfcs,CommonUtil.POST_INSERT);
		listeners.setPostInsertEventListeners(list.toArray(new PostInsertEventListener[list.size()]));
		
		// post update
		list=merge(listener,data.cfcs,CommonUtil.POST_UPDATE);
		listeners.setPostUpdateEventListeners(list.toArray(new PostUpdateEventListener[list.size()]));
		
		// post load
		list=merge(listener,data.cfcs,CommonUtil.POST_LOAD);
		listeners.setPostLoadEventListeners(list.toArray(new PostLoadEventListener[list.size()]));
		
		// pre delete
		list=merge(listener,data.cfcs,CommonUtil.PRE_DELETE);
		listeners.setPreDeleteEventListeners(list.toArray(new PreDeleteEventListener[list.size()]));
		
		// pre insert
		//list=merge(listener,cfcs,CommonUtil.PRE_INSERT);
		//listeners.setPreInsertEventListeners(list.toArray(new PreInsertEventListener[list.size()]));
		
		// pre load
		list=merge(listener,data.cfcs,CommonUtil.PRE_LOAD);
		listeners.setPreLoadEventListeners(list.toArray(new PreLoadEventListener[list.size()]));
		
		// pre update
		//list=merge(listener,cfcs,CommonUtil.PRE_UPDATE);
		//listeners.setPreUpdateEventListeners(list.toArray(new PreUpdateEventListener[list.size()]));
	}

	private static List<EventListener> merge(EventListener listener, Map<String, CFCInfo> cfcs, Collection.Key eventType) {
		List<EventListener> list=new ArrayList<EventListener>();
			
		
		Iterator<Entry<String, CFCInfo>> it = cfcs.entrySet().iterator();
		Entry<String, CFCInfo> entry;
		Component cfc;
		while(it.hasNext()){
			entry = it.next();
			cfc = entry.getValue().getCFC();
			if(EventListener.hasEventType(cfc,eventType)) {
				if(CommonUtil.POST_DELETE.equals(eventType))
					list.add(new PostDeleteEventListenerImpl(cfc));
				if(CommonUtil.POST_INSERT.equals(eventType))
					list.add(new PostInsertEventListenerImpl(cfc));
				if(CommonUtil.POST_LOAD.equals(eventType))
					list.add(new PostLoadEventListenerImpl(cfc));
				if(CommonUtil.POST_UPDATE.equals(eventType))
					list.add(new PostUpdateEventListenerImpl(cfc));
				
				if(CommonUtil.PRE_DELETE.equals(eventType))
					list.add(new PreDeleteEventListenerImpl(cfc));
				if(CommonUtil.PRE_INSERT.equals(eventType))
					list.add(new PreInsertEventListenerImpl(cfc));
				if(CommonUtil.PRE_LOAD.equals(eventType))
					list.add(new PreLoadEventListenerImpl(cfc));
				if(CommonUtil.PRE_UPDATE.equals(eventType))
					list.add(new PreUpdateEventListenerImpl(cfc));
			}
		}
		
		// general listener
		if(listener!=null && EventListener.hasEventType(listener.getCFC(),eventType))
			list.add(listener);
		
		return list;
	}

	private static Object hash(PageContext pc) throws PageException {
		ApplicationContextPro appContext=(ApplicationContextPro) pc.getApplicationContext();
		Object o=appContext.getORMDataSource();
		DataSource ds;
		if(o instanceof DataSource) ds=(DataSource) o;
		else ds=CommonUtil.getDataSource(pc,CommonUtil.toString(o));
		return hash(appContext.getORMConfiguration(),ds);
	}
	
	private static String hash(ORMConfiguration ormConf,DataSource ds) {
		if(ds instanceof DataSourcePro)
			return ((DataSourcePro)ds).id()+":"+ormConf.hash();
		return ds.getClazz()+":"+ds.getDsnTranslated()+":"+ormConf.hash();
	}

	public void createMapping(PageContext pc,Component cfc, DatasourceConnection dc, ORMConfiguration ormConf,SessionFactoryData data) throws PageException {
		String id=HibernateUtil.id(HibernateCaster.getEntityName(cfc));
		CFCInfo info=data.cfcs.get(id);
		//Long modified=cfcs.get(id);
		String xml;
		long cfcCompTime = HibernateUtil.getCompileTime(pc,cfc.getPageSource());
		if(info==null || (ORMUtil.equals(info.getCFC(),cfc) ))	{//&& info.getModified()!=cfcCompTime
			StringBuilder sb=new StringBuilder();
			
			long xmlLastMod = loadMapping(sb,ormConf, cfc);
			Element root;
			// create maaping
			if(true || xmlLastMod< cfcCompTime) {//MUSTMUST
				data.reset();
				Document doc=null;
				try {
					doc=CommonUtil.newDocument();
				}catch(Throwable t){t.printStackTrace();}
				
				root=doc.createElement("hibernate-mapping");
				doc.appendChild(root);
				pc.addPageSource(cfc.getPageSource(), true);
				try{
					HBMCreator.createXMLMapping(pc,dc,cfc,root,data);
				}
				finally{
					pc.removeLastPageSource(true);
				}
				xml=XMLCaster.toString(root.getChildNodes(),true,true);
				saveMapping(ormConf,cfc,root);
			}
			// load
			else {
				xml=sb.toString();
				root=CommonUtil.toXML(xml).getOwnerDocument().getDocumentElement();
				/*print.o("1+++++++++++++++++++++++++++++++++++++++++");
				print.o(xml);
				print.o("2+++++++++++++++++++++++++++++++++++++++++");
				print.o(root);
				print.o("3+++++++++++++++++++++++++++++++++++++++++");*/
				
			}
			data.cfcs.put(id, new CFCInfo(HibernateUtil.getCompileTime(pc,cfc.getPageSource()),xml,cfc));
		}
		
	}

	private static void saveMapping(ORMConfiguration ormConf, Component cfc, Element hm) {
		if(ormConf.saveMapping()){
			Resource res=cfc.getPageSource().getResource();
			if(res!=null){
				res=res.getParentResource().getRealResource(res.getName()+".hbm.xml");
				try{
				CommonUtil.write(res, 
						XMLCaster.toString(hm,false,true,
								HibernateSessionFactory.HIBERNATE_3_PUBLIC_ID,
								HibernateSessionFactory.HIBERNATE_3_SYSTEM_ID,
								HibernateSessionFactory.HIBERNATE_3_CHARSET.name()), HibernateSessionFactory.HIBERNATE_3_CHARSET, false);
				}
				catch(Exception e){} 
			}
		}
	}
	
	private static long loadMapping(StringBuilder sb,ORMConfiguration ormConf, Component cfc) {
		
		Resource res=cfc.getPageSource().getResource();
		if(res!=null){
			res=res.getParentResource().getRealResource(res.getName()+".hbm.xml");
			try{
				sb.append(CommonUtil.toString(res, CommonUtil.UTF8));
				return res.lastModified();
			}
			catch(Exception e){} 
		}
		return 0;
	}

	@Override
	public int getMode() {
		//MUST impl
		return MODE_LAZY;
	}

	@Override
	public String getLabel() {
		return "Hibernate";
	}

	
	
	

	@Override
	public ORMConfiguration getConfiguration(PageContext pc) {
		ApplicationContext ac = pc.getApplicationContext();
		if(!ac.isORMEnabled())
			return null;
		return  ac.getORMConfiguration();
	}

	/**
	 * @param pc
	 * @param session
	 * @param entityName name of the entity to get
	 * @param unique create a unique version that can be manipulated
	 * @param init call the nit method of the cfc or not
	 * @return
	 * @throws PageException
	 */
	public Component create(PageContext pc, HibernateORMSession session,String entityName, boolean unique) throws PageException {
		SessionFactoryData data = session.getSessionFactoryData();
		// get existing entity
		Component cfc = _create(pc,entityName,unique,data);
		if(cfc!=null)return cfc;
		
		// reinit ORMEngine
		SessionFactory _old= getSessionFactory(pc,INIT_NOTHING);//_factory;
		SessionFactory _new = getSessionFactory(pc,INIT_CFCS);
		if(_old!=_new){
			session.resetSession(_new);
			cfc = _create(pc,entityName,unique,data);
			if(cfc!=null)return cfc;
		}
		
		
		
		ORMConfiguration ormConf = pc.getApplicationContext().getORMConfiguration();
		Resource[] locations = ormConf.getCfcLocations();
		
		throw ExceptionUtil.createException(data,null,
				"No entity (persitent component) with name ["+entityName+"] found, available entities are ["+CommonUtil.toList(data.getEntityNames(), ", ")+"] ",
				"component are searched in the following directories ["+toString(locations)+"]");
		
	}
	
	
	private String toString(Resource[] locations) {
		if(locations==null) return "";
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<locations.length;i++){
			if(i>0) sb.append(", ");
			sb.append(locations[i].getAbsolutePath());
		}
		return sb.toString();
	}

	private static Component _create(PageContext pc, String entityName, boolean unique, SessionFactoryData data) throws PageException {
		CFCInfo info = data.cfcs.get(HibernateUtil.id(entityName));
		if(info!=null) {
			Component cfc = info.getCFC();
			if(unique){
				cfc=(Component)Duplicator.duplicate(cfc,false);
				if(cfc.contains(pc,CommonUtil.INIT))cfc.call(pc, "init",new Object[]{});
			}
			return cfc;
		}
		return null;
	}
}
class CFCInfo {
	private String xml;
	private long modified;
	private Component cfc;
	
	public CFCInfo(long modified, String xml, Component cfc) {
		this.modified=modified;
		this.xml=xml;
		this.cfc=cfc;
	}
	/**
	 * @return the cfc
	 */
	public Component getCFC() {
		return cfc;
	}
	/**
	 * @return the xml
	 */
	public String getXML() {
		return xml;
	}
	/**
	 * @return the modified
	 */
	public long getModified() {
		return modified;
	}
	
}

