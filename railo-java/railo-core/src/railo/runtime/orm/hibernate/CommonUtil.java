package railo.runtime.orm.hibernate;

import java.io.Serializable;
import java.sql.SQLException;

import org.hibernate.JDBCException;
import org.w3c.dom.Node;

import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLItem;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Struct;
import railo.runtime.util.Cast;
import railo.runtime.util.Decision;

public class CommonUtil {
	
	private static Cast caster;
	private static Decision decision;


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
	
	public static PageException toPageException(Throwable t) {
		if (!(t instanceof JDBCException))
			return caster().toPageException(t);
		
		
		JDBCException j = (JDBCException)t;
		String message = j.getMessage(); 
		Throwable cause = j.getCause();
		SQLException sqle;
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

}
