package railo.runtime.query;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;

// FUTURE in der bytecode generierung wird QueryImpl verwendet, darum muss diese klasse QueryImpl erben anstelle von Query

public class QueryCacheQuery extends QueryImpl {

	private QueryImpl query;
	private boolean isCloned=false;
	private Map _columns=new HashMap();

	public QueryCacheQuery(QueryImpl query) {
		super(query.keys(),0,query.getName()); // FUTURE kann entfernt werden wenn interface query
		this.query=query;
	}

	protected void disconnectCache() {
		if(isCloned) return;
		//print.out("cloned");
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
	public boolean addColumn(Key columnName, Array content) throws PageException {
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
	public boolean addColumn(Key columnName, Array content, int type) throws DatabaseException {
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
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
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
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
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
	public boolean containsKey(Key key) {
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
	public Object get(Key key, Object defaultValue) {
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
	public Object get(Key key) throws PageException {
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
	public Object get(PageContext pc, Key key, Object defaultValue) {
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
	public Object get(PageContext pc, Key key) throws PageException {
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
	public Object getAt(Key key, int row, Object defaultValue) {
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
	public Object getAt(Key key, int row) throws PageException {
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
		return getColumn(KeyImpl.init(key));
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(railo.runtime.type.Collection.Key)
	 */
	public QueryColumn getColumn(Key key) throws DatabaseException {
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
	public QueryColumn getColumn(Key key, QueryColumn defaultValue) {
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

	/**
	 * @see railo.runtime.type.QueryImpl#keyIterator()
	 */
	public Iterator keyIterator() {
		return query.keyIterator();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#keys()
	 */
	public Key[] keys() {
		return query.keys();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#keysAsString()
	 */
	public String[] keysAsString() {
		return query.keysAsString();
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

	/**
	 * @see railo.runtime.type.QueryImpl#remove(java.lang.String)
	 */
	public synchronized Object remove(String key) throws PageException {
		disconnectCache();
		return query.remove(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
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
	public QueryColumn removeColumn(Key key) throws PageException {
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
	public QueryColumn removeColumnEL(Key key) {
		disconnectCache();
		return query.removeColumnEL(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeEL(java.lang.String)
	 */
	public synchronized Object removeEL(String key) {
		disconnectCache();
		return query.removeEL(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
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
	public Object set(Key key, Object value) throws PageException {
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
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
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
	public Object setAt(Key key, int row, Object value) throws PageException {
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
	public Object setAtEL(Key key, int row, Object value) {
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
	public Object setEL(Key key, Object value) {
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
	public Object setEL(PageContext pc, Key propertyName, Object value) {
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
	public void sort(Key column) throws PageException {
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
	public synchronized void sort(Key keyColumn, int order) throws PageException {
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
	public synchronized void rename(Key columnName, Key newColumnName)
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
}
