package railo.runtime.orm.hibernate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.JDBCException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.types.RefBoolean;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.Component;
import railo.runtime.ComponentPro;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.component.Property;
import railo.runtime.component.PropertyImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLItem;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.util.ListUtil;
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
	private static final short INSPECT_UNDEFINED = (short)4; /*ConfigImpl.INSPECT_UNDEFINED*/
	
	public static final Charset UTF8;
	
	static {
		UTF8=Charset.forName("utf-8");
	}

	
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

	public static Object toList(String[] arr, String delimiter) { 
		return ListUtil.arrayToList(arr, delimiter);
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
	public static String toString(Resource file, Charset charset) throws IOException {
		return IOUtil.toString(file, charset);
	}

	public static String[] toStringArray(String list, char delimiter) { 
		return ListUtil.listToStringArray(list, delimiter);
	}
	public static String[] toStringArray(String list, String delimiter) { 
		return ListUtil.toStringArray(ListUtil.listToArray(list,delimiter),""); //TODO better
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

	public static SQLItem toSQLItem(Object value, int type) {
		return new SQLItemImpl(value,type);
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
	

	public static Document toDocument(Resource res) throws IOException, SAXException {
		return XMLUtil.parse(XMLUtil.toInputSource(res), null, false);
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

	public static Property createProperty(String name, String type) {
		PropertyImpl pi = new PropertyImpl();
		pi.setName(name);
		pi.setType(type);
		return pi;
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

	public static MappingImpl createMapping(Config config, String virtual, String physical) {
		return new MappingImpl(config,
				virtual,
				physical,
				null,INSPECT_UNDEFINED,true,false,false,false,true,true,null
				);
	}
	
	public static String last(String list, char delimiter) {
		return ListUtil.last(list, delimiter);
	}
	
	public static String last(String list, String delimiter) {
		return ListUtil.last(list, delimiter,true);
	}
	
	public static int listFindNoCaseIgnoreEmpty(String list, String value, char delimiter) {
		return ListUtil.listFindNoCaseIgnoreEmpty(list,value,delimiter);
	}
	
	public static String[] trimItems(String[] arr) {
		for(int i=0;i<arr.length;i++) {
			arr[i]=arr[i].trim();
		}
		return arr;
	}
	
	public static Document getDocument(Node node) {
		return XMLUtil.getDocument(node);
	}
	public static Document newDocument() throws ParserConfigurationException, FactoryConfigurationError {
		return XMLUtil.newDocument();
	}
	public static void setFirst(Node parent, Node node) {
		XMLUtil.setFirst(parent, node);
	}

	public static Property[] getProperties(Component c,boolean onlyPeristent, boolean includeBaseProperties, boolean preferBaseProperties, boolean inheritedMappedSuperClassOnly) {
		if(c instanceof ComponentPro)
			return ((ComponentPro)c).getProperties(onlyPeristent, includeBaseProperties,preferBaseProperties,preferBaseProperties);
		return c.getProperties(onlyPeristent);
	}
	
	public static void write(Resource res, String string, Charset charset, boolean append) throws IOException {
		IOUtil.write(res, string, charset, append);
	}

	public static BufferedReader toBufferedReader(Resource res, Charset charset) throws IOException {
		return IOUtil.toBufferedReader(IOUtil.getReader(res,(Charset)null));
	}
	
	public static boolean equalsComplexEL(Object left, Object right) {
		return Operator.equalsComplexEL(left, right, false,true);
	}

	public static void setEntity(Component c, boolean entity) { 
		((ComponentAccess)c).setEntity(entity);
	}

	public static PageContext pc() {
		//return CFMLEngineFactory.getInstance().getThreadPageContext();
		return ThreadLocalPageContext.get();
	}

	public static Config config() { 
		//return CFMLEngineFactory.getInstance().getThreadPageContext().getConfig();
		return ThreadLocalPageContext.getConfig();
	}
}
