package railo.runtime.query;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
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

	@Override
	public void add(Object value) {
		disconnectCache();
		column.add(value);
	}

	@Override
	public void addRow(int count) {
		disconnectCache();
		column.addRow(count);
	}

	@Override
	public void cutRowsTo(int maxrows) {
		disconnectCache();
		column.cutRowsTo(maxrows);
	}

	@Override
	public Object get(int row) throws PageException {
		return column.get(row);
	}

	@Override
	public Object get(int row, Object defaultValue) {
		return column.get(row, defaultValue);
	}

	@Override
	public int getType() {
		return column.getType();
	}

	@Override
	public String getTypeAsString() {
		return column.getTypeAsString();
	}

	@Override
	public Object remove(int row) throws PageException {
		disconnectCache();
		return column.remove(row);
	}

	@Override
	public Object removeEL(int row) {
		disconnectCache();
		return column.removeEL(row);
	}

	@Override
	public Object removeRow(int row) throws PageException {
		disconnectCache();
		return column.removeRow(row);
	}

	@Override
	public Object set(int row, Object value) throws PageException {
		disconnectCache();
		return column.set(row, value);
	}

	@Override
	public Object setEL(int row, Object value) {
		disconnectCache();
		return column.setEL(row, value);
	}

	@Override
	public void clear() {
		disconnectCache();
		column.clear();
	}

	@Override
	public boolean containsKey(String key) {
		return column.containsKey(key);
	}

	@Override
	public boolean containsKey(Key key) {
		return column.containsKey(key);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return column.duplicate(deepCopy);
	}
	

	@Override
	public Object get(String key) throws PageException {
		return get(KeyImpl.init(key));
	}

	@Override
	public Object get(Key key) throws PageException {
		return column.get(key);
	}

	@Override
	public Object get(String key, Object defaultValue) {
		return column.get(key, defaultValue);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		return column.get(key, defaultValue);
	}

	@Override
	public Key[] keys() {
		return column.keys();
	}

	@Override
	public Object remove(Key key) throws PageException {
		disconnectCache();
		return column.remove(key);
	}


	@Override
	public Object removeEL(Key key) {
		disconnectCache();
		return column.removeEL(key);
	}

	@Override
	public Object set(String key, Object value) throws PageException {
		disconnectCache();
		return column.set(key, value);
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		disconnectCache();
		return column.set(key, value);
	}

	@Override
	public Object setEL(String key, Object value) {
		disconnectCache();
		return column.setEL(key, value);
	}

	@Override
	public Object setEL(Key key, Object value) {
		disconnectCache();
		return column.setEL(key, value);
	}

	@Override
	public int size() {
		return column.size();
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return column.toDumpData(pageContext, maxlevel,dp);
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return column.keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return column.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return column.entryIterator();
	}

	@Override
	public boolean castToBooleanValue() throws PageException {
		return column.castToBooleanValue();
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return column.castToBoolean(defaultValue);
    }

	@Override
	public DateTime castToDateTime() throws PageException {
		return column.castToDateTime();
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return column.castToDateTime(defaultValue);
    }

	@Override
	public double castToDoubleValue() throws PageException {
		return column.castToDoubleValue();
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return column.castToDoubleValue(defaultValue);
    }

	@Override
	public String castToString() throws PageException {
		return column.castToString();
	}

	@Override
	public String castToString(String defaultValue) {
		return column.castToString(defaultValue);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return column.compareTo(str);
	}

	@Override
	public int compareTo(boolean b) throws PageException {
		return column.compareTo(b);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return column.compareTo(d);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return column.compareTo(dt);
	}

	@Override
	public Object get(PageContext pc) throws PageException {
		return column.get(pc);
	}

	@Override
	public Object get(PageContext pc, Object defaultValue) {
		return column.get(pc, defaultValue);
	}

	@Override
	public Key getKey() throws PageException {
		return column.getKey();
	}

	@Override
	public String getKeyAsString() throws PageException {
		return column.getKeyAsString();
	}

	@Override
	public Object getParent() {
		return qcq;
	}

	@Override
	public Object remove(PageContext pc) throws PageException {
		disconnectCache();
		return column.remove(pc);
	}

	@Override
	public Object removeEL(PageContext pc) {
		disconnectCache();
		return column.removeEL(pc);
	}

	@Override
	public Object set(PageContext pc, Object value) throws PageException {
		disconnectCache();
		return column.set(pc, value);
	}

	@Override
	public Object setEL(PageContext pc, Object value) {
		disconnectCache();
		return column.setEL(pc, value);
	}

	@Override
	public Object touch(PageContext pc) throws PageException {
		disconnectCache();
		return column.touch(pc);
	}

	@Override
	public Object touchEL(PageContext pc) {
		disconnectCache();
		return column.touchEL(pc);
	}

	@Override
	public Object clone() {
		return column.clone();
	}

	@Override
	public Iterator<Object> valueIterator() {
		return column.valueIterator();
	}
	


	@Override
	public long sizeOf() {
		return QueryUtil.sizeOf(column);
	}
	
	@Override
	public java.util.Iterator<String> getIterator() {
    	return keysAsStringIterator();
    }
}
