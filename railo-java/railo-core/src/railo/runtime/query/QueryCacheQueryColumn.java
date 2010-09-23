package railo.runtime.query;

import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.Collection;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.Sizeable;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.QueryUtil;

public class QueryCacheQueryColumn implements QueryColumn,Sizeable {

	private QueryCacheQuery qcq;
	private QueryColumn column;
	private Collection.Key key;

	
	/**
	 * return a queryCacheQuery
	 * @param qcq
	 * @param key
	 * @return
	 * @throws DatabaseException
	 */
	public static QueryColumn getColumn(QueryCacheQuery qcq, Key key) throws DatabaseException {
		QueryColumn _column = qcq.getQuery().getColumn(key);
		return new QueryCacheQueryColumn(qcq,_column,key);
	}
	
	/**
	 * return a queryCacheQuery
	 * @param qcq
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static QueryColumn getColumn(QueryCacheQuery qcq, Key key, QueryColumn defaultValue) {
		QueryColumn _column = qcq.getQuery().getColumn(key,null);
		if(_column==null) return defaultValue;
		return new QueryCacheQueryColumn(qcq,_column,key);
	}

	/**
	 * Constructor of the class
	 * @param qcq
	 * @param column
	 * @param key
	 */
	private QueryCacheQueryColumn(QueryCacheQuery qcq, QueryColumn column, Key key) {
		this.qcq=qcq;
		this.column=column;
		this.key=key;
	}

	private void disconnectCache() {
		qcq.disconnectCache();
		try {
			column=qcq.getQuery().getColumn(key);
		} catch (DatabaseException e) {
			throw new PageRuntimeException(e);
		}
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#add(java.lang.Object)
	 */
	public void add(Object value) {
		disconnectCache();
		column.add(value);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#addRow(int)
	 */
	public void addRow(int count) {
		disconnectCache();
		column.addRow(count);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#cutRowsTo(int)
	 */
	public void cutRowsTo(int maxrows) {
		disconnectCache();
		column.cutRowsTo(maxrows);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#get(int)
	 */
	public Object get(int row) throws PageException {
		return column.get(row);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#get(int, java.lang.Object)
	 */
	public Object get(int row, Object defaultValue) {
		return column.get(row, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#getType()
	 */
	public int getType() {
		return column.getType();
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#getTypeAsString()
	 */
	public String getTypeAsString() {
		return column.getTypeAsString();
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#remove(int)
	 */
	public Object remove(int row) throws PageException {
		disconnectCache();
		return column.remove(row);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#removeEL(int)
	 */
	public Object removeEL(int row) {
		disconnectCache();
		return column.removeEL(row);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#removeRow(int)
	 */
	public Object removeRow(int row) throws PageException {
		disconnectCache();
		return column.removeRow(row);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#set(int, java.lang.Object)
	 */
	public Object set(int row, Object value) throws PageException {
		disconnectCache();
		return column.set(row, value);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryColumn#setEL(int, java.lang.Object)
	 */
	public Object setEL(int row, Object value) {
		disconnectCache();
		return column.setEL(row, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		disconnectCache();
		column.clear();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return column.containsKey(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return column.containsKey(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return column.duplicate(deepCopy);
	}
	

	/**
	 *
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
		return column.get(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return column.get(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		return column.get(key, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return column.get(key, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return column.keys();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return column.keysAsString();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		disconnectCache();
		return column.remove(key);
	}


	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		disconnectCache();
		return column.removeEL(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		disconnectCache();
		return column.set(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		disconnectCache();
		return column.set(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		disconnectCache();
		return column.setEL(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		disconnectCache();
		return column.setEL(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return column.size();
	}

	/**
	 *
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return column.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 *
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return column.iterator();
	}

	/**
	 *
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator keyIterator() {
		return column.keyIterator();
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return column.castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return column.castToBoolean(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return column.castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return column.castToDateTime(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return column.castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return column.castToDoubleValue(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return column.castToString();
	}

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return column.castToString(defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return column.compareTo(str);
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return column.compareTo(b);
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return column.compareTo(d);
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return column.compareTo(dt);
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext)
	 */
	public Object get(PageContext pc) throws PageException {
		return column.get(pc);
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext, java.lang.Object)
	 */
	public Object get(PageContext pc, Object defaultValue) {
		return column.get(pc, defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#getKey()
	 */
	public Key getKey() throws PageException {
		return column.getKey();
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#getKeyAsString()
	 */
	public String getKeyAsString() throws PageException {
		return column.getKeyAsString();
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#getParent()
	 */
	public Object getParent() {
		return qcq;
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#remove(railo.runtime.PageContext)
	 */
	public Object remove(PageContext pc) throws PageException {
		disconnectCache();
		return column.remove(pc);
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#removeEL(railo.runtime.PageContext)
	 */
	public Object removeEL(PageContext pc) {
		disconnectCache();
		return column.removeEL(pc);
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#set(railo.runtime.PageContext, java.lang.Object)
	 */
	public Object set(PageContext pc, Object value) throws PageException {
		disconnectCache();
		return column.set(pc, value);
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#setEL(railo.runtime.PageContext, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Object value) {
		disconnectCache();
		return column.setEL(pc, value);
	}

	/**
	 *
	 * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
	 */
	public Object touch(PageContext pc) throws PageException {
		disconnectCache();
		return column.touch(pc);
	}

	/**
	 * @see railo.runtime.type.ref.Reference#touchEL(railo.runtime.PageContext)
	 */
	public Object touchEL(PageContext pc) {
		disconnectCache();
		return column.touchEL(pc);
	}

	/**
	 *
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return column.clone();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return column.valueIterator();
	}
	


	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return QueryUtil.sizeOf(column);
	}
}
