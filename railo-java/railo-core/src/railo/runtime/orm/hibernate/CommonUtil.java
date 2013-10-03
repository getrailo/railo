package railo.runtime.orm.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.JDBCException;
import org.w3c.dom.Node;

import railo.commons.lang.types.RefBoolean;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLItem;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.Struct;
import railo.runtime.util.Cast;
import railo.runtime.util.Creation;
import railo.runtime.util.Decision;

public class CommonUtil {
	
	public static final Key ENTITY_NAME = CommonUtil.createKey("entityname");
	public static final Key FIELDTYPE = CommonUtil.createKey("fieldtype");
	public static final Key POST_INSERT=CommonUtil.createKey("postInsert");
	public static final Key POST_UPDATE=CommonUtil.createKey("postUpdate");
	public static final Key PRE_DELETE=CommonUtil.createKey("preDelete");
	public static final Key POST_DELETE=CommonUtil.createKey("postDelete");
	public static final Key PRE_LOAD=CommonUtil.createKey("preLoad");
	public static final Key POST_LOAD=CommonUtil.createKey("postLoad");
	public static final Key PRE_UPDATE=CommonUtil.createKey("preUpdate");
	public static final Key PRE_INSERT=CommonUtil.createKey("preInsert");
	public static final Key INIT=CommonUtil.createKey("init");
	
	

	
	private static Cast caster;
	private static Decision decision;
	private static Creation creator;


	public static Object castTo(PageContext pc, Class trgClass, Object obj) throws PageException {
		return Caster.castTo(pc, trgClass, obj);
	}
	
	public static Array toArray(Object obj) throws PageException {
		return caster().toArray(obj);
	}
	public static Array toArray(Object obj, Array defaultValue) {
		return caster().toArray(obj,defaultValue);
	}

	public static Boolean toBoolean(String str) throws PageException {
		return caster().toBoolean(str);
	}
	public static Boolean toBoolean(String str, Boolean defaultValue) {
		return caster().toBoolean(str,defaultValue);
	}
	public static Boolean toBoolean(Object obj) throws PageException {
		return caster().toBoolean(obj);
	}
	public static Boolean toBoolean(Object obj, Boolean defaultValue) {
		return caster().toBoolean(obj,defaultValue);
	}

	public static Boolean toBooleanValue(String str) throws PageException {
		return caster().toBooleanValue(str);
	}
	public static Boolean toBooleanValue(String str, Boolean defaultValue) {
		return caster().toBooleanValue(str,defaultValue);
	}
	public static boolean toBooleanValue(Object obj) throws PageException {
		return caster().toBooleanValue(obj);
	}
	public static boolean toBooleanValue(Object obj, boolean defaultValue) {
		return caster().toBooleanValue(obj,defaultValue);
	}

	public static Component toComponent(Object obj) throws PageException {
		return Caster.toComponent(obj);
	}
	public static Component toComponent(Object obj, Component defaultValue) {
		return Caster.toComponent(obj,defaultValue);
	}
	
	public static String toString(Object obj, String defaultValue) {
		return caster().toString(obj,defaultValue);
	}
	public static String toString(Object obj) throws PageException {
		return caster().toString(obj);
	}
	public static String toString(boolean b) {
		return caster().toString(b);
	}
	public static String toString(double d) {
		return caster().toString(d);
	}
	public static String toString(int i) {
		return caster().toString(i);
	}
	public static String toString(long l) {
		return caster().toString(l);
	}
	
	public static Integer toInteger(Object obj) throws PageException {
		return caster().toInteger(obj);
	}
	public static Integer toInteger(Object obj, Integer defaultValue) {
		return caster().toInteger(obj, defaultValue);
	}
	public static int toIntValue(Object obj) throws PageException {
		return caster().toIntValue(obj);
	}
	public static int toIntValue(Object obj, int defaultValue) {
		return caster().toIntValue(obj,defaultValue);
	}
	
	public static Array toArray(Argument arg) {
		Array trg=createArray();
		int[] keys = arg.intKeys();
		for(int i=0;i<keys.length;i++){
			trg.setEL(keys[i],
					arg.get(keys[i],null));
		}
		return trg;
	}
	
	public static PageException toPageException(Throwable t) {
		if (!(t instanceof JDBCException))
			return caster().toPageException(t);
		
		
		JDBCException j = (JDBCException)t;
		String message = j.getMessage(); 
		Throwable cause = j.getCause();
		if(cause != null) {
			message += " [" + cause.getMessage() + "]";
		}
		return CFMLEngineFactory.getInstance().getExceptionUtil().createDatabaseException(message, new SQLImpl(j.getSQL()));
		
	}
	public static Serializable toSerializable(Object obj) throws PageException {
		return caster().toSerializable(obj);
	}
	public static Serializable toSerializable(Object obj,Serializable defaultValue) {
		return caster().toSerializable(obj,defaultValue);
	}

	public static Struct toStruct(Object obj) throws PageException {
		return caster().toStruct(obj);
	}
	public static Struct toStruct(Object obj, Struct defaultValue) {
		return caster().toStruct(obj,defaultValue);
	}
	
	public static Object[] toNativeArray(Object obj) throws PageException {
		return Caster.toNativeArray(obj);
	}
	
	public static String toTypeName(Object obj) {
		return caster().toTypeName(obj);
	}
	public static Node toXML(Object obj) throws PageException {
		return caster().toXML(obj);
	}
	public static Node toXML(Object obj, Node defaultValue) {
		return caster().toXML(obj,defaultValue);
	}

	public static boolean isArray(Object obj) {
		return decision().isArray(obj);
	}
	
	public static boolean isStruct(Object obj) {
		return decision().isStruct(obj);
	}
	
	public static Array createArray(){
		return creator().createArray();
	}

	public static DateTime createDateTime(long time) {
		return creator().createDateTime(time);
	}
	public static Struct createStruct(){
		return creator().createStruct();
	}
	public static Collection.Key createKey(String key){
		return creator().createKey(key);
	}
	public static Query createQuery(Collection.Key[] columns, int rows, String name) throws PageException{
		return creator().createQuery(columns, rows, name);
	}
	public static Query createQuery(Array names, Array types, int rows, String name) throws PageException{ 
		return new QueryImpl(names,types,rows,name);
	}

	public static RefBoolean createRefBoolean() {
		return new RefBooleanImpl();
	}
	
	public static Key[] keys(Collection coll) { 
		if(coll==null) return new Key[0];
		Iterator<Key> it = coll.keyIterator();
		List<Key> rtn=new ArrayList<Key>();
		if(it!=null)while(it.hasNext()){
			rtn.add(it.next());
		}
		return rtn.toArray(new Key[rtn.size()]);
	}
	
	
	
	private static Creation creator() {
		if(creator==null)
			creator=CFMLEngineFactory.getInstance().getCreationUtil();
		return creator;
	}

	private static Decision decision() {
		if(decision==null)
			decision=CFMLEngineFactory.getInstance().getDecisionUtil();
		return decision;
	}
	private static Cast caster() {
		if(caster==null)
			caster=CFMLEngineFactory.getInstance().getCastUtil();
		return caster;
	}
	

	/**
	 * represents a SQL Statement with his defined arguments for a prepared statement
	 */
	static class SQLImpl implements SQL {
	    
	    private String strSQL;
	    
	    /**
	     * Constructor only with SQL String
	     * @param strSQL SQL String
	     */
	    public SQLImpl(String strSQL) {
	        this.strSQL=strSQL;
	    }
	    
	    
	    public void addItems(SQLItem item) {
	    	
	    }
	    
	    @Override
	    public SQLItem[] getItems() {
	        return new SQLItem[0];
	    }

	    @Override
	    public int getPosition() {
	        return 0;
	    }
	    
	    @Override
	    public void setPosition(int position) {
	    }    
	    

	    @Override
	    public String getSQLString() {
	        return strSQL;
	    }
	    
	    @Override
	    public void setSQLString(String strSQL) {
	        this.strSQL= strSQL;
	    }

	    @Override
	    public String toString() {
	        return strSQL;
	    }    
	    
	    @Override
	    public String toHashString() {
	        return strSQL;
	    }
	}
	
	/**
	 * Integer Type that can be modified
	 */
	public static final class RefBooleanImpl implements RefBoolean {//MUST add interface Castable

	    private boolean value;


	    public RefBooleanImpl() {}
	    
	    /**
	     * @param value
	     */
	    public RefBooleanImpl(boolean value) {
	        this.value=value;
	    }
	    
	    /**
	     * @param value
	     */
	    public void setValue(boolean value) {
	        this.value = value;
	    }
	    
	    /**
	     * @return returns value as Boolean Object
	     */
	    public Boolean toBoolean() {
	        return value?Boolean.TRUE:Boolean.FALSE;
	    }
	    
	    /**
	     * @return returns value as boolean value
	     */
	    public boolean toBooleanValue() {
	        return value;
	    }
	    
	    @Override
	    public String toString() {
	        return value?"true":"false";
	    }
	}

	public static DataSource getDataSource(PageContext pc, String name) throws PageException {
		return ((PageContextImpl)pc).getDataSource(name); // TODO use reflection
	}

	public static DatasourceConnection getDatasourceConnection(PageContext pc, DataSource ds) throws PageException {
		return ((ConfigWebImpl)pc.getConfig()).getDatasourceConnectionPool().getDatasourceConnection(pc,ds,null,null); // TODO use reflection
	}
	
	public static void releaseDatasourceConnection(PageContext pc, DatasourceConnection dc) {
		((ConfigWebImpl)pc.getConfig()).getDatasourceConnectionPool().releaseDatasourceConnection(dc); // TODO use reflection
	}
	
	

}
