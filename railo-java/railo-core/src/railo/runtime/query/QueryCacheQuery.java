package railo.runtime.query;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.db.SQL;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;

public class QueryCacheQuery extends QueryImpl {

	private QueryImpl query;
	private boolean isCloned=false;
	private Map _columns=new HashMap();

	public QueryCacheQuery(QueryImpl query) throws DatabaseException {
		super(query.keys(),0,query.getName());
		this.query=query;
	}

	protected void disconnectCache() {
		if(isCloned) return;
		this.query=query.cloneQuery(true);
		isCloned=true;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(java.lang.String, railo.runtime.type.Array)
	 */
	public boolean addColumn(String columnName, Array content) throws DatabaseException {
		disconnectCache();
		return query.addColumn(columnName, content);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(railo.runtime.type.Collection.Key, railo.runtime.type.Array)
	 */
	public boolean addColumn(Collection.Key columnName, Array content) throws PageException {
		disconnectCache();
		return query.addColumn(columnName, content);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(java.lang.String, railo.runtime.type.Array, int)
	 */
	public synchronized boolean addColumn(String columnName, Array content, int type) throws DatabaseException {
		disconnectCache();
		return query.addColumn(columnName, content, type);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(railo.runtime.type.Collection.Key, railo.runtime.type.Array, int)
	 */
	public boolean addColumn(Collection.Key columnName, Array content, int type) throws DatabaseException {
		disconnectCache();
		return query.addColumn(columnName, content, type);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addRow(int)
	 */
	public synchronized boolean addRow(int count) {
		disconnectCache();
		return query.addRow(count);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addRow()
	 */
	public int addRow() {
		disconnectCache();
		return query.addRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String methodName, Object[] arguments) throws PageException {
		return query.call(pc, methodName, arguments);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Collection.Key methodName, Object[] arguments) throws PageException {
		return query.call(pc, methodName, arguments);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String methodName, Struct args) throws PageException {
		return query.callWithNamedValues(pc, methodName, args);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Collection.Key methodName, Struct args) throws PageException {
		return query.callWithNamedValues(pc, methodName, args);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws ExpressionException {
		return query.castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return query.castToBoolean(defaultValue);
    }

	/**
	 * @see railo.runtime.type.QueryImpl#castToDateTime()
	 */
	public DateTime castToDateTime() throws ExpressionException {
		return query.castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return query.castToDateTime(defaultValue);
    }

	/**
	 * @see railo.runtime.type.QueryImpl#castToDoubleValue()
	 */
	public double castToDoubleValue() throws ExpressionException {
		return query.castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return query.castToDoubleValue(defaultValue);
    }

	/**
	 * @see railo.runtime.type.QueryImpl#castToString()
	 */
	public String castToString() throws ExpressionException {
		return query.castToString();
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return query.castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#clear()
	 */
	public void clear() {
		disconnectCache();
		query.clear();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#clone()
	 */
	public Object clone() {
		return query.clone();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#cloneQuery(boolean)
	 */
	public QueryImpl cloneQuery(boolean deepCopy) {
		return query.cloneQuery(deepCopy);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws ExpressionException {
		return query.compareTo(b);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return query.compareTo(dt);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return query.compareTo(d);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return query.compareTo(str);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return query.containsKey(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
		return query.containsKey(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#cutRowsTo(int)
	 */
	public synchronized boolean cutRowsTo(int maxrows) {
		disconnectCache();
		return query.cutRowsTo(maxrows);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return query.duplicate(deepCopy);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#executionTime()
	 */
	public int executionTime() {
		return query.executionTime();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		return query.get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return query.get(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
		return query.get(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		return query.get(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object get(PageContext pc, String key, Object defaultValue) {
		return query.get(pc, key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return query.get(pc, key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.PageContext, java.lang.String)
	 */
	public Object get(PageContext pc, String key) throws PageException {
		return query.get(pc, key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return query.get(pc, key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(java.lang.String, int, java.lang.Object)
	 */
	public Object getAt(String key, int row, Object defaultValue) {
		return query.getAt(key, row, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	public Object getAt(Collection.Key key, int row, Object defaultValue) {
		return query.getAt(key, row, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(java.lang.String, int)
	 */
	public Object getAt(String key, int row) throws PageException {
		return query.getAt(key, row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(railo.runtime.type.Collection.Key, int)
	 */
	public Object getAt(Collection.Key key, int row) throws PageException {
		return query.getAt(key, row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBoolean(int)
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return query.getBoolean(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return query.getBoolean(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(java.lang.String)
	 */
	public QueryColumn getColumn(String key) throws DatabaseException {
		return getColumn(KeyImpl.getInstance(key));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(railo.runtime.type.Collection.Key)
	 */
	public QueryColumn getColumn(Collection.Key key) throws DatabaseException {
		if(!isCloned) {
			QueryColumn column = (QueryColumn) _columns.get(key);
			if(column==null) {
				column=QueryCacheQueryColumn.getColumn(this,key);
				_columns.put(key, column);
			}
			return column;
		}
		//disconnectCache();
		return query.getColumn(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(java.lang.String, railo.runtime.type.QueryColumn)
	 */
	public QueryColumn getColumn(String key, QueryColumn defaultValue) {
		return getColumn(KeyImpl.getInstance(key),defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(railo.runtime.type.Collection.Key, railo.runtime.type.QueryColumn)
	 */
	public QueryColumn getColumn(Collection.Key key, QueryColumn defaultValue) {
		if(!isCloned) {
			QueryColumn column = (QueryColumn) _columns.get(key);
			if(column==null) {
				column=QueryCacheQueryColumn.getColumn(this,key,defaultValue);
				if(column!=defaultValue)_columns.put(key, column);
			}
			return column;
		}
		//disconnectCache();
		return query.getColumn(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnIndex(java.lang.String)
	 */
	public int getColumnIndex(String coulmnName) {
		return query.getColumnIndex(coulmnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnlist()
	 */
	public String getColumnlist() {
		return query.getColumnlist();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumns()
	 */
	public String[] getColumns() {
		return query.getColumns();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCurrentrow()
	 */
	public int getCurrentrow() {
		return query.getCurrentrow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCurrentrow(int)
	 */
	public int getCurrentrow(int pid) {
		return query.getCurrentrow(pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getData(int, int)
	 */
	public String getData(int row, int col) throws IndexOutOfBoundsException {
		return query.getData(row, col);
	}
	
	/**
	 * @see railo.runtime.type.QueryImpl#_getMetaData()
	 */
	public synchronized Struct _getMetaData() {
		return query._getMetaData();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getMetaDataSimple()
	 */
	public synchronized Array getMetaDataSimple() {
		return query.getMetaDataSimple();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getName()
	 */
	public String getName() {
		return query.getName();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(java.lang.String)
	 */
	public Object getObject(String columnName) throws SQLException {
		return query.getObject(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(int)
	 */
	public Object getObject(int columnIndex) throws SQLException {
		return query.getObject(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRecordcount()
	 */
	public int getRecordcount() {
		return query.getRecordcount();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRowCount()
	 */
	public int getRowCount() {
		return query.getRowCount();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getSql()
	 */
	public SQL getSql() {
		return query.getSql();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getString(int)
	 */
	public String getString(int columnIndex) throws SQLException {
		return query.getString(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getString(java.lang.String)
	 */
	public String getString(String columnName) throws SQLException {
		return query.getString(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTypes()
	 */
	public synchronized int[] getTypes() {
		return query.getTypes();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTypesAsMap()
	 */
	public synchronized Map getTypesAsMap() {
		return query.getTypesAsMap();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#go(int)
	 */
	public boolean go(int index) {
		return query.go(index);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#go(int, int)
	 */
	public boolean go(int index, int pid) {
		return query.go(index, pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isCached()
	 */
	public boolean isCached() {
		return query.isCached();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isEmpty()
	 */
	public boolean isEmpty() {
		return query.isEmpty();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isInitalized()
	 */
	public boolean isInitalized() {
		return query.isInitalized();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#iterator()
	 */
	public Iterator iterator() {
		return query.iterator();
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return query.keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return query.keysAsStringIterator();
    }

	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return query.entryIterator();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#keys()
	 */
	public Collection.Key[] keys() {
		return query.keys();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#next()
	 */
	public boolean next() {
		return query.next();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#next(int)
	 */
	public boolean next(int pid) {
		return query.next(pid);
	}

	/* *
	 * @see railo.runtime.type.QueryImpl#remove(java.lang.String)
	 * /
	public synchronized Object remove (String key) throws PageException {
		disconnectCache();
		return query.remove(key);
	}*/

	/**
	 * @see railo.runtime.type.QueryImpl#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		disconnectCache();
		return query.remove(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumn(java.lang.String)
	 */
	public QueryColumn removeColumn(String key) throws DatabaseException {
		disconnectCache();
		return query.removeColumn(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumn(railo.runtime.type.Collection.Key)
	 */
	public QueryColumn removeColumn(Collection.Key key) throws DatabaseException {
		disconnectCache();
		return query.removeColumn(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumnEL(java.lang.String)
	 */
	public synchronized QueryColumn removeColumnEL(String key) {
		disconnectCache();
		return query.removeColumnEL(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumnEL(railo.runtime.type.Collection.Key)
	 */
	public QueryColumn removeColumnEL(Collection.Key key) {
		disconnectCache();
		return query.removeColumnEL(key);
	}

	/* *
	 * @see railo.runtime.type.QueryImpl#removeEL(java.lang.String)
	 * /
	public synchronized Object removeEL (String key) {
		disconnectCache();
		return query.removeEL(key);
	}*/

	/**
	 * @see railo.runtime.type.QueryImpl#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		disconnectCache();
		return query.removeEL(key);
	}

	/**
	 *
	 * @see railo.runtime.type.QueryImpl#removeRow(int)
	 */
	public synchronized int removeRow(int row) throws PageException {
		disconnectCache();
		return query.removeRow(row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeRowEL(int)
	 */
	public int removeRowEL(int row) {
		disconnectCache();
		return query.removeRowEL(row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#reset()
	 */
	public void reset() {
		query.reset();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#reset(int)
	 */
	public void reset(int pid) {
		query.reset(pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		disconnectCache();
		return query.set(key, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		disconnectCache();
		return query.set(key, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value) throws PageException {
		disconnectCache();
		return query.set(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		disconnectCache();
		return query.set(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAt(java.lang.String, int, java.lang.Object)
	 */
	public Object setAt(String key, int row, Object value) throws PageException {
		disconnectCache();
		return query.setAt(key, row, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAt(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	public Object setAt(Collection.Key key, int row, Object value) throws PageException {
		disconnectCache();
		return query.setAt(key, row, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAtEL(java.lang.String, int, java.lang.Object)
	 */
	public Object setAtEL(String key, int row, Object value) {
		disconnectCache();
		return query.setAtEL(key, row, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAtEL(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	public Object setAtEL(Collection.Key key, int row, Object value) {
		disconnectCache();
		return query.setAtEL(key, row, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setCached(boolean)
	 */
	public void setCached(boolean isCached) {
		query.setCached(isCached);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setData(int, int, java.lang.String)
	 */
	public void setData(int row, int col, String value) throws IndexOutOfBoundsException {
		disconnectCache();
		query.setData(row, col, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		disconnectCache();
		return query.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		disconnectCache();
		return query.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		disconnectCache();
		return query.setEL(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		disconnectCache();
		return query.setEL(pc, propertyName, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setExecutionTime(long)
	 */
	public void setExecutionTime(long exeTime) {
		disconnectCache();
		query.setExecutionTime(exeTime);
	}


	/**
	 * @see railo.runtime.type.QueryImpl#setSql(railo.runtime.db.SQL)
	 */
	public void setSql(SQL sql) {
		disconnectCache();
		query.setSql(sql);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#size()
	 */
	public int size() {
		return query.size();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(java.lang.String)
	 */
	public void sort(String column) throws PageException {
		disconnectCache();
		query.sort(column);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(railo.runtime.type.Collection.Key)
	 */
	public void sort(Collection.Key column) throws PageException {
		disconnectCache();
		query.sort(column);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(java.lang.String, int)
	 */
	public synchronized void sort(String strColumn, int order) throws PageException {
		disconnectCache();
		query.sort(strColumn, order);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(railo.runtime.type.Collection.Key, int)
	 */
	public synchronized void sort(Collection.Key keyColumn, int order) throws PageException {
		disconnectCache();
		query.sort(keyColumn, order);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return query.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#toString()
	 */
	public String toString() {
		return query.toString();
	}

	public QueryImpl getQuery() {
		return query;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {
		disconnectCache();
		query.cancelRowUpdates();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#deleteRow()
	 */
	public void deleteRow() throws SQLException {
		disconnectCache();
		query.deleteRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#insertRow()
	 */
	public void insertRow() throws SQLException {
		disconnectCache();
		query.insertRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rename(railo.runtime.type.Collection.Key, railo.runtime.type.Collection.Key)
	 */
	public synchronized void rename(Collection.Key columnName, Collection.Key newColumnName)
			throws ExpressionException {
		disconnectCache();
		query.rename(columnName, newColumnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		return query.rowDeleted();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		return query.rowInserted();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		return query.rowUpdated();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		disconnectCache();
		query.setFetchDirection(direction);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		disconnectCache();
		query.setFetchSize(rows);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int columnIndex, java.sql.Array x)
			throws SQLException {
		disconnectCache();
		query.updateArray(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String columnName, java.sql.Array x)
			throws SQLException {
		disconnectCache();
		query.updateArray(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		disconnectCache();
		query.updateAsciiStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length)
			throws SQLException {
		disconnectCache();
		query.updateAsciiStream(columnName, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		disconnectCache();
		query.updateBigDecimal(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void updateBigDecimal(String columnName, BigDecimal x)
			throws SQLException {
		disconnectCache();
		query.updateBigDecimal(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		disconnectCache();
		query.updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateBinaryStream(String columnName, InputStream x, int length)
			throws SQLException {
		disconnectCache();
		query.updateBinaryStream(columnName, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		disconnectCache();
		query.updateBlob(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String columnName, Blob x) throws SQLException {
		disconnectCache();
		query.updateBlob(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		disconnectCache();
		query.updateBoolean(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		disconnectCache();
		query.updateBoolean(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateByte(int, byte)
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		disconnectCache();
		query.updateByte(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		disconnectCache();
		query.updateByte(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBytes(int, byte[])
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		disconnectCache();
		query.updateBytes(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		disconnectCache();
		query.updateBytes(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int columnIndex, Reader reader, int length)
			throws SQLException {
		disconnectCache();
		query.updateCharacterStream(columnIndex, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void updateCharacterStream(String columnName, Reader reader,
			int length) throws SQLException {
		disconnectCache();
		query.updateCharacterStream(columnName, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		disconnectCache();
		query.updateClob(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String columnName, Clob x) throws SQLException {
		disconnectCache();
		query.updateClob(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int columnIndex, Date x) throws SQLException {
		disconnectCache();
		query.updateDate(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String columnName, Date x) throws SQLException {
		disconnectCache();
		query.updateDate(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDouble(int, double)
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		disconnectCache();
		query.updateDouble(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		disconnectCache();
		query.updateDouble(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateFloat(int, float)
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		disconnectCache();
		query.updateFloat(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		disconnectCache();
		query.updateFloat(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateInt(int, int)
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		disconnectCache();
		query.updateInt(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateInt(java.lang.String, int)
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		disconnectCache();
		query.updateInt(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateLong(int, long)
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		disconnectCache();
		query.updateLong(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateLong(java.lang.String, long)
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		disconnectCache();
		query.updateLong(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNull(int)
	 */
	public void updateNull(int columnIndex) throws SQLException {
		disconnectCache();
		query.updateNull(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNull(java.lang.String)
	 */
	public void updateNull(String columnName) throws SQLException {
		disconnectCache();
		query.updateNull(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		disconnectCache();
		query.updateObject(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		disconnectCache();
		query.updateObject(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int columnIndex, Object x, int scale)
			throws SQLException {
		disconnectCache();
		query.updateObject(columnIndex, x, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(java.lang.String, java.lang.Object, int)
	 */
	public void updateObject(String columnName, Object x, int scale)
			throws SQLException {
		disconnectCache();
		query.updateObject(columnName, x, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		disconnectCache();
		query.updateRef(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String columnName, Ref x) throws SQLException {
		disconnectCache();
		query.updateRef(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRow()
	 */
	public void updateRow() throws SQLException {
		disconnectCache();
		query.updateRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateShort(int, short)
	 */
	public void updateShort(int columnIndex, short x) throws SQLException {
		disconnectCache();
		query.updateShort(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateShort(java.lang.String, short)
	 */
	public void updateShort(String columnName, short x) throws SQLException {
		disconnectCache();
		query.updateShort(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateString(int, java.lang.String)
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		disconnectCache();
		query.updateString(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String columnName, String x) throws SQLException {
		disconnectCache();
		query.updateString(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		disconnectCache();
		query.updateTime(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		disconnectCache();
		query.updateTime(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		disconnectCache();
		query.updateTimestamp(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void updateTimestamp(String columnName, Timestamp x)
			throws SQLException {
		disconnectCache();
		query.updateTimestamp(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#wasNull()
	 */
	public boolean wasNull() {
		return query.wasNull();
	}
	

	/**
	 * @see railo.runtime.type.QueryImpl#absolute(int)
	 */
	public boolean absolute(int row) throws SQLException {
		return query.absolute(row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#afterLast()
	 */
	public void afterLast() throws SQLException {
		query.afterLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#beforeFirst()
	 */
	public void beforeFirst() throws SQLException {
		query.beforeFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		disconnectCache();
		query.clearWarnings();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#close()
	 */
	public void close() throws SQLException {
		disconnectCache();
		query.close();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws SQLException {
		return query.findColumn(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#first()
	 */
	public boolean first() throws SQLException {
		return query.first();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getArray(int)
	 */
	public java.sql.Array getArray(int i) throws SQLException {
		return query.getArray(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getArray(java.lang.String)
	 */
	public java.sql.Array getArray(String colName) throws SQLException {
		return query.getArray(colName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return query.getAsciiStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return query.getAsciiStream(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		return query.getBigDecimal(columnIndex, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return query.getBigDecimal(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(java.lang.String, int)
	 */
	public BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		return query.getBigDecimal(columnName, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return query.getBigDecimal(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return query.getBinaryStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBinaryStream(java.lang.String)
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return query.getBinaryStream(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		return query.getBlob(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBlob(java.lang.String)
	 */
	public Blob getBlob(String colName) throws SQLException {
		return query.getBlob(colName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getByte(int)
	 */
	public byte getByte(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getByte(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getByte(java.lang.String)
	 */
	public byte getByte(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getByte(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBytes(int)
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getBytes(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getBytes(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getCharacterStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getCharacterStream(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {
		// TODO Auto-generated method stub
		return query.getClob(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getClob(java.lang.String)
	 */
	public Clob getClob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getClob(colName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnlist(boolean)
	 */
	public String getColumnlist(boolean upperCase) {
		// TODO Auto-generated method stub
		return query.getColumnlist(upperCase);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnName(int)
	 */
	public Collection.Key getColumnName(int columnIndex) {
		// TODO Auto-generated method stub
		return query.getColumnName(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnNames()
	 */
	public Collection.Key[] getColumnNames() {
		// TODO Auto-generated method stub
		return query.getColumnNames();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnNamesAsString()
	 */
	public String[] getColumnNamesAsString() {
		// TODO Auto-generated method stub
		return query.getColumnNamesAsString();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return query.getConcurrency();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return query.getCursorName();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDate(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(int)
	 */
	public Date getDate(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDate(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDate(columnName, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(java.lang.String)
	 */
	public Date getDate(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDate(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDouble(int)
	 */
	public double getDouble(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDouble(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDouble(java.lang.String)
	 */
	public double getDouble(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDouble(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return query.getFetchDirection();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return query.getFetchSize();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFloat(int)
	 */
	public float getFloat(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getFloat(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFloat(java.lang.String)
	 */
	public float getFloat(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getFloat(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getGeneratedKeys()
	 */
	public Query getGeneratedKeys() {
		// TODO Auto-generated method stub
		return query.getGeneratedKeys();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getInt(int)
	 */
	public int getInt(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getInt(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getInt(java.lang.String)
	 */
	public int getInt(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getInt(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getLong(int)
	 */
	public long getLong(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getLong(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getLong(java.lang.String)
	 */
	public long getLong(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getLong(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return query.getMetaData();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(int, java.util.Map)
	 */
	public Object getObject(int i, Map map) throws SQLException {
		// TODO Auto-generated method stub
		return query.getObject(i, map);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String colName, Map map) throws SQLException {
		// TODO Auto-generated method stub
		return query.getObject(colName, map);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		// TODO Auto-generated method stub
		return query.getRef(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRef(java.lang.String)
	 */
	public Ref getRef(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getRef(colName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRow()
	 */
	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return query.getRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getShort(int)
	 */
	public short getShort(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getShort(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getShort(java.lang.String)
	 */
	public short getShort(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getShort(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getStatement()
	 */
	public Statement getStatement() throws SQLException {
		// TODO Auto-generated method stub
		return query.getStatement();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTime(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(int)
	 */
	public Time getTime(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTime(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTime(columnName, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(java.lang.String)
	 */
	public Time getTime(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTime(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		return query.getTimestamp(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTimestamp(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		return query.getTimestamp(columnName, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTimestamp(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getType()
	 */
	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return query.getType();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getUnicodeStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUnicodeStream(java.lang.String)
	 */
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getUnicodeStream(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUpdateCount()
	 */
	public int getUpdateCount() {
		// TODO Auto-generated method stub
		return query.getUpdateCount();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getURL(int)
	 */
	public URL getURL(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getURL(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getURL(java.lang.String)
	 */
	public URL getURL(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getURL(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return query.getWarnings();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException {
		// TODO Auto-generated method stub
		return query.isAfterLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		return query.isBeforeFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isFirst()
	 */
	public boolean isFirst() throws SQLException {
		// TODO Auto-generated method stub
		return query.isFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isLast()
	 */
	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return query.isLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#last()
	 */
	public boolean last() throws SQLException {
		// TODO Auto-generated method stub
		return query.last();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub
		query.moveToCurrentRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub
		query.moveToInsertRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#previous()
	 */
	public boolean previous() {
		// TODO Auto-generated method stub
		return query.previous();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#previous(int)
	 */
	public boolean previous(int pid) {
		// TODO Auto-generated method stub
		return query.previous(pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#refreshRow()
	 */
	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub
		query.refreshRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#relative(int)
	 */
	public boolean relative(int rows) throws SQLException {
		// TODO Auto-generated method stub
		return query.relative(rows);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setColumnNames(railo.runtime.type.Collection.Key[])
	 */
	public void setColumnNames(Collection.Key[] trg) throws PageException {
		// TODO Auto-generated method stub
		query.setColumnNames(trg);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#valueIterator()
	 */
	public Iterator valueIterator() {
		// TODO Auto-generated method stub
		return query.valueIterator();
	}

}
