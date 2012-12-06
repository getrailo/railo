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

	@Override
	public boolean addColumn(String columnName, Array content) throws DatabaseException {
		disconnectCache();
		return query.addColumn(columnName, content);
	}

	@Override
	public boolean addColumn(Collection.Key columnName, Array content) throws PageException {
		disconnectCache();
		return query.addColumn(columnName, content);
	}

	@Override
	public synchronized boolean addColumn(String columnName, Array content, int type) throws DatabaseException {
		disconnectCache();
		return query.addColumn(columnName, content, type);
	}

	@Override
	public boolean addColumn(Collection.Key columnName, Array content, int type) throws DatabaseException {
		disconnectCache();
		return query.addColumn(columnName, content, type);
	}

	@Override
	public synchronized boolean addRow(int count) {
		disconnectCache();
		return query.addRow(count);
	}

	@Override
	public int addRow() {
		disconnectCache();
		return query.addRow();
	}

	@Override
	public Object call(PageContext pc, Collection.Key methodName, Object[] arguments) throws PageException {
		return query.call(pc, methodName, arguments);
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Collection.Key methodName, Struct args) throws PageException {
		return query.callWithNamedValues(pc, methodName, args);
	}

	@Override
	public boolean castToBooleanValue() throws ExpressionException {
		return query.castToBooleanValue();
	}
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return query.castToBoolean(defaultValue);
    }

	@Override
	public DateTime castToDateTime() throws ExpressionException {
		return query.castToDateTime();
	}
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return query.castToDateTime(defaultValue);
    }

	@Override
	public double castToDoubleValue() throws ExpressionException {
		return query.castToDoubleValue();
	}
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return query.castToDoubleValue(defaultValue);
    }

	@Override
	public String castToString() throws ExpressionException {
		return query.castToString();
	}
	
	@Override
	public String castToString(String defaultValue) {
		return query.castToString(defaultValue);
	}

	@Override
	public void clear() {
		disconnectCache();
		query.clear();
	}

	@Override
	public Object clone() {
		return query.clone();
	}

	@Override
	public QueryImpl cloneQuery(boolean deepCopy) {
		return query.cloneQuery(deepCopy);
	}

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		return query.compareTo(b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return query.compareTo(dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return query.compareTo(d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return query.compareTo(str);
	}

	@Override
	public boolean containsKey(String key) {
		return query.containsKey(key);
	}

	@Override
	public boolean containsKey(Collection.Key key) {
		return query.containsKey(key);
	}

	@Override
	public synchronized boolean cutRowsTo(int maxrows) {
		disconnectCache();
		return query.cutRowsTo(maxrows);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return query.duplicate(deepCopy);
	}

	@Override
	public int executionTime() {
		return query.executionTime();
	}

	@Override
	public Object get(String key, Object defaultValue) {
		return query.get(key, defaultValue);
	}

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		return query.get(key, defaultValue);
	}

	@Override
	public Object get(String key) throws PageException {
		return query.get(key);
	}

	@Override
	public Object get(Collection.Key key) throws PageException {
		return query.get(key);
	}

	@Override
	public Object get(PageContext pc, Collection.Key key, Object defaultValue) {
		return query.get(pc, key, defaultValue);
	}

	@Override
	public Object get(PageContext pc, Collection.Key key) throws PageException {
		return query.get(pc, key);
	}

	@Override
	public Object getAt(String key, int row, Object defaultValue) {
		return query.getAt(key, row, defaultValue);
	}

	@Override
	public Object getAt(Collection.Key key, int row, Object defaultValue) {
		return query.getAt(key, row, defaultValue);
	}

	@Override
	public Object getAt(String key, int row) throws PageException {
		return query.getAt(key, row);
	}

	@Override
	public Object getAt(Collection.Key key, int row) throws PageException {
		return query.getAt(key, row);
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return query.getBoolean(columnIndex);
	}

	@Override
	public boolean getBoolean(String columnName) throws SQLException {
		return query.getBoolean(columnName);
	}

	@Override
	public QueryColumn getColumn(String key) throws DatabaseException {
		return getColumn(KeyImpl.getInstance(key));
	}

	@Override
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

	@Override
	public QueryColumn getColumn(String key, QueryColumn defaultValue) {
		return getColumn(KeyImpl.getInstance(key),defaultValue);
	}

	@Override
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

	@Override
	public int getColumnIndex(String coulmnName) {
		return query.getColumnIndex(coulmnName);
	}

	@Override
	public String getColumnlist() {
		return query.getColumnlist();
	}

	@Override
	public String[] getColumns() {
		return query.getColumns();
	}

	@Override
	public int getCurrentrow(int pid) {
		return query.getCurrentrow(pid);
	}

	@Override
	public String getData(int row, int col) throws IndexOutOfBoundsException {
		return query.getData(row, col);
	}
	
	/*public synchronized Struct _getMetaData() {
		return query._getMetaData();
	}*/

	@Override
	public synchronized Array getMetaDataSimple() {
		return query.getMetaDataSimple();
	}

	@Override
	public String getName() {
		return query.getName();
	}

	@Override
	public Object getObject(String columnName) throws SQLException {
		return query.getObject(columnName);
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return query.getObject(columnIndex);
	}

	@Override
	public int getRecordcount() {
		return query.getRecordcount();
	}

	@Override
	public int getRowCount() {
		return query.getRowCount();
	}

	@Override
	public SQL getSql() {
		return query.getSql();
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		return query.getString(columnIndex);
	}

	@Override
	public String getString(String columnName) throws SQLException {
		return query.getString(columnName);
	}

	@Override
	public synchronized int[] getTypes() {
		return query.getTypes();
	}

	@Override
	public synchronized Map getTypesAsMap() {
		return query.getTypesAsMap();
	}

	@Override
	public boolean go(int index) {
		return query.go(index);
	}

	@Override
	public boolean go(int index, int pid) {
		return query.go(index, pid);
	}

	@Override
	public boolean isCached() {
		return query.isCached();
	}

	@Override
	public boolean isEmpty() {
		return query.isEmpty();
	}

	@Override
	public boolean isInitalized() {
		return query.isInitalized();
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

	@Override
	public Collection.Key[] keys() {
		return query.keys();
	}

	@Override
	public boolean next() {
		return query.next();
	}

	@Override
	public boolean next(int pid) {
		return query.next(pid);
	}

	@Override
	public Object remove(Collection.Key key) throws PageException {
		disconnectCache();
		return query.remove(key);
	}

	@Override
	public QueryColumn removeColumn(String key) throws DatabaseException {
		disconnectCache();
		return query.removeColumn(key);
	}

	@Override
	public QueryColumn removeColumn(Collection.Key key) throws DatabaseException {
		disconnectCache();
		return query.removeColumn(key);
	}

	@Override
	public synchronized QueryColumn removeColumnEL(String key) {
		disconnectCache();
		return query.removeColumnEL(key);
	}

	@Override
	public QueryColumn removeColumnEL(Collection.Key key) {
		disconnectCache();
		return query.removeColumnEL(key);
	}

	@Override
	public Object removeEL(Collection.Key key) {
		disconnectCache();
		return query.removeEL(key);
	}

	@Override
	public synchronized int removeRow(int row) throws PageException {
		disconnectCache();
		return query.removeRow(row);
	}

	@Override
	public int removeRowEL(int row) {
		disconnectCache();
		return query.removeRowEL(row);
	}

	@Override
	public void reset() {
		query.reset();
	}

	@Override
	public void reset(int pid) {
		query.reset(pid);
	}

	@Override
	public Object set(String key, Object value) throws PageException {
		disconnectCache();
		return query.set(key, value);
	}

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		disconnectCache();
		return query.set(key, value);
	}

	@Override
	public Object set(PageContext pc, Collection.Key propertyName, Object value) throws PageException {
		disconnectCache();
		return query.set(pc, propertyName, value);
	}

	@Override
	public Object setAt(String key, int row, Object value) throws PageException {
		disconnectCache();
		return query.setAt(key, row, value);
	}

	@Override
	public Object setAt(Collection.Key key, int row, Object value) throws PageException {
		disconnectCache();
		return query.setAt(key, row, value);
	}

	@Override
	public Object setAtEL(String key, int row, Object value) {
		disconnectCache();
		return query.setAtEL(key, row, value);
	}

	@Override
	public Object setAtEL(Collection.Key key, int row, Object value) {
		disconnectCache();
		return query.setAtEL(key, row, value);
	}

	@Override
	public void setCached(boolean isCached) {
		query.setCached(isCached);
	}

	@Override
	public void setData(int row, int col, String value) throws IndexOutOfBoundsException {
		disconnectCache();
		query.setData(row, col, value);
	}

	@Override
	public Object setEL(String key, Object value) {
		disconnectCache();
		return query.setEL(key, value);
	}

	@Override
	public Object setEL(Collection.Key key, Object value) {
		disconnectCache();
		return query.setEL(key, value);
	}

	@Override
	public Object setEL(PageContext pc, Collection.Key propertyName, Object value) {
		disconnectCache();
		return query.setEL(pc, propertyName, value);
	}

	@Override
	public void setExecutionTime(long exeTime) {
		disconnectCache();
		query.setExecutionTime(exeTime);
	}


	@Override
	public void setSql(SQL sql) {
		disconnectCache();
		query.setSql(sql);
	}

	@Override
	public int size() {
		return query.size();
	}

	@Override
	public void sort(String column) throws PageException {
		disconnectCache();
		query.sort(column);
	}

	@Override
	public void sort(Collection.Key column) throws PageException {
		disconnectCache();
		query.sort(column);
	}

	@Override
	public synchronized void sort(String strColumn, int order) throws PageException {
		disconnectCache();
		query.sort(strColumn, order);
	}

	@Override
	public synchronized void sort(Collection.Key keyColumn, int order) throws PageException {
		disconnectCache();
		query.sort(keyColumn, order);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return query.toDumpData(pageContext, maxlevel,dp);
	}

	@Override
	public String toString() {
		return query.toString();
	}

	public QueryImpl getQuery() {
		return query;
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		disconnectCache();
		query.cancelRowUpdates();
	}

	@Override
	public void deleteRow() throws SQLException {
		disconnectCache();
		query.deleteRow();
	}

	@Override
	public void insertRow() throws SQLException {
		disconnectCache();
		query.insertRow();
	}

	@Override
	public synchronized void rename(Collection.Key columnName, Collection.Key newColumnName)
			throws ExpressionException {
		disconnectCache();
		query.rename(columnName, newColumnName);
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return query.rowDeleted();
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return query.rowInserted();
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return query.rowUpdated();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		disconnectCache();
		query.setFetchDirection(direction);
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		disconnectCache();
		query.setFetchSize(rows);
	}

	@Override
	public void updateArray(int columnIndex, java.sql.Array x)
			throws SQLException {
		disconnectCache();
		query.updateArray(columnIndex, x);
	}

	@Override
	public void updateArray(String columnName, java.sql.Array x)
			throws SQLException {
		disconnectCache();
		query.updateArray(columnName, x);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		disconnectCache();
		query.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateAsciiStream(String columnName, InputStream x, int length)
			throws SQLException {
		disconnectCache();
		query.updateAsciiStream(columnName, x, length);
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		disconnectCache();
		query.updateBigDecimal(columnIndex, x);
	}

	@Override
	public void updateBigDecimal(String columnName, BigDecimal x)
			throws SQLException {
		disconnectCache();
		query.updateBigDecimal(columnName, x);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		disconnectCache();
		query.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(String columnName, InputStream x, int length)
			throws SQLException {
		disconnectCache();
		query.updateBinaryStream(columnName, x, length);
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		disconnectCache();
		query.updateBlob(columnIndex, x);
	}

	@Override
	public void updateBlob(String columnName, Blob x) throws SQLException {
		disconnectCache();
		query.updateBlob(columnName, x);
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		disconnectCache();
		query.updateBoolean(columnIndex, x);
	}

	@Override
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		disconnectCache();
		query.updateBoolean(columnName, x);
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		disconnectCache();
		query.updateByte(columnIndex, x);
	}

	@Override
	public void updateByte(String columnName, byte x) throws SQLException {
		disconnectCache();
		query.updateByte(columnName, x);
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		disconnectCache();
		query.updateBytes(columnIndex, x);
	}

	@Override
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		disconnectCache();
		query.updateBytes(columnName, x);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader reader, int length)
			throws SQLException {
		disconnectCache();
		query.updateCharacterStream(columnIndex, reader, length);
	}

	@Override
	public void updateCharacterStream(String columnName, Reader reader,
			int length) throws SQLException {
		disconnectCache();
		query.updateCharacterStream(columnName, reader, length);
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		disconnectCache();
		query.updateClob(columnIndex, x);
	}

	@Override
	public void updateClob(String columnName, Clob x) throws SQLException {
		disconnectCache();
		query.updateClob(columnName, x);
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		disconnectCache();
		query.updateDate(columnIndex, x);
	}

	@Override
	public void updateDate(String columnName, Date x) throws SQLException {
		disconnectCache();
		query.updateDate(columnName, x);
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		disconnectCache();
		query.updateDouble(columnIndex, x);
	}

	@Override
	public void updateDouble(String columnName, double x) throws SQLException {
		disconnectCache();
		query.updateDouble(columnName, x);
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		disconnectCache();
		query.updateFloat(columnIndex, x);
	}

	@Override
	public void updateFloat(String columnName, float x) throws SQLException {
		disconnectCache();
		query.updateFloat(columnName, x);
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		disconnectCache();
		query.updateInt(columnIndex, x);
	}

	@Override
	public void updateInt(String columnName, int x) throws SQLException {
		disconnectCache();
		query.updateInt(columnName, x);
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		disconnectCache();
		query.updateLong(columnIndex, x);
	}

	@Override
	public void updateLong(String columnName, long x) throws SQLException {
		disconnectCache();
		query.updateLong(columnName, x);
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		disconnectCache();
		query.updateNull(columnIndex);
	}

	@Override
	public void updateNull(String columnName) throws SQLException {
		disconnectCache();
		query.updateNull(columnName);
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		disconnectCache();
		query.updateObject(columnIndex, x);
	}

	@Override
	public void updateObject(String columnName, Object x) throws SQLException {
		disconnectCache();
		query.updateObject(columnName, x);
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scale)
			throws SQLException {
		disconnectCache();
		query.updateObject(columnIndex, x, scale);
	}

	@Override
	public void updateObject(String columnName, Object x, int scale)
			throws SQLException {
		disconnectCache();
		query.updateObject(columnName, x, scale);
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		disconnectCache();
		query.updateRef(columnIndex, x);
	}

	@Override
	public void updateRef(String columnName, Ref x) throws SQLException {
		disconnectCache();
		query.updateRef(columnName, x);
	}

	@Override
	public void updateRow() throws SQLException {
		disconnectCache();
		query.updateRow();
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		disconnectCache();
		query.updateShort(columnIndex, x);
	}

	@Override
	public void updateShort(String columnName, short x) throws SQLException {
		disconnectCache();
		query.updateShort(columnName, x);
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		disconnectCache();
		query.updateString(columnIndex, x);
	}

	@Override
	public void updateString(String columnName, String x) throws SQLException {
		disconnectCache();
		query.updateString(columnName, x);
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		disconnectCache();
		query.updateTime(columnIndex, x);
	}

	@Override
	public void updateTime(String columnName, Time x) throws SQLException {
		disconnectCache();
		query.updateTime(columnName, x);
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		disconnectCache();
		query.updateTimestamp(columnIndex, x);
	}

	@Override
	public void updateTimestamp(String columnName, Timestamp x)
			throws SQLException {
		disconnectCache();
		query.updateTimestamp(columnName, x);
	}

	@Override
	public boolean wasNull() {
		return query.wasNull();
	}
	

	@Override
	public boolean absolute(int row) throws SQLException {
		return query.absolute(row);
	}

	@Override
	public void afterLast() throws SQLException {
		query.afterLast();
	}

	@Override
	public void beforeFirst() throws SQLException {
		query.beforeFirst();
	}

	@Override
	public void clearWarnings() throws SQLException {
		disconnectCache();
		query.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		disconnectCache();
		query.close();
	}

	@Override
	public int findColumn(String columnName) throws SQLException {
		return query.findColumn(columnName);
	}

	@Override
	public boolean first() throws SQLException {
		return query.first();
	}

	@Override
	public java.sql.Array getArray(int i) throws SQLException {
		return query.getArray(i);
	}

	@Override
	public java.sql.Array getArray(String colName) throws SQLException {
		return query.getArray(colName);
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return query.getAsciiStream(columnIndex);
	}

	@Override
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return query.getAsciiStream(columnName);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		return query.getBigDecimal(columnIndex, scale);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return query.getBigDecimal(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		return query.getBigDecimal(columnName, scale);
	}

	@Override
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return query.getBigDecimal(columnName);
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return query.getBinaryStream(columnIndex);
	}

	@Override
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return query.getBinaryStream(columnName);
	}

	@Override
	public Blob getBlob(int i) throws SQLException {
		return query.getBlob(i);
	}

	@Override
	public Blob getBlob(String colName) throws SQLException {
		return query.getBlob(colName);
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getByte(columnIndex);
	}

	@Override
	public byte getByte(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getByte(columnName);
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getBytes(columnIndex);
	}

	@Override
	public byte[] getBytes(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getBytes(columnName);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getCharacterStream(columnIndex);
	}

	@Override
	public Reader getCharacterStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getCharacterStream(columnName);
	}

	@Override
	public Clob getClob(int i) throws SQLException {
		// TODO Auto-generated method stub
		return query.getClob(i);
	}

	@Override
	public Clob getClob(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getClob(colName);
	}

	@Override
	public String getColumnlist(boolean upperCase) {
		// TODO Auto-generated method stub
		return query.getColumnlist(upperCase);
	}

	@Override
	public Collection.Key getColumnName(int columnIndex) {
		// TODO Auto-generated method stub
		return query.getColumnName(columnIndex);
	}

	@Override
	public Collection.Key[] getColumnNames() {
		// TODO Auto-generated method stub
		return query.getColumnNames();
	}

	@Override
	public String[] getColumnNamesAsString() {
		// TODO Auto-generated method stub
		return query.getColumnNamesAsString();
	}

	@Override
	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return query.getConcurrency();
	}

	@Override
	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return query.getCursorName();
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDate(columnIndex, cal);
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDate(columnIndex);
	}

	@Override
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDate(columnName, cal);
	}

	@Override
	public Date getDate(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDate(columnName);
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDouble(columnIndex);
	}

	@Override
	public double getDouble(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getDouble(columnName);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return query.getFetchDirection();
	}

	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return query.getFetchSize();
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getFloat(columnIndex);
	}

	@Override
	public float getFloat(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getFloat(columnName);
	}

	@Override
	public Query getGeneratedKeys() {
		// TODO Auto-generated method stub
		return query.getGeneratedKeys();
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getInt(columnIndex);
	}

	@Override
	public int getInt(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getInt(columnName);
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getLong(columnIndex);
	}

	@Override
	public long getLong(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getLong(columnName);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return query.getMetaData();
	}

	@Override
	public Object getObject(int i, Map map) throws SQLException {
		// TODO Auto-generated method stub
		return query.getObject(i, map);
	}

	@Override
	public Object getObject(String colName, Map map) throws SQLException {
		// TODO Auto-generated method stub
		return query.getObject(colName, map);
	}

	@Override
	public Ref getRef(int i) throws SQLException {
		// TODO Auto-generated method stub
		return query.getRef(i);
	}

	@Override
	public Ref getRef(String colName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getRef(colName);
	}

	@Override
	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return query.getRow();
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getShort(columnIndex);
	}

	@Override
	public short getShort(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getShort(columnName);
	}

	@Override
	public Statement getStatement() throws SQLException {
		// TODO Auto-generated method stub
		return query.getStatement();
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTime(columnIndex, cal);
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTime(columnIndex);
	}

	@Override
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTime(columnName, cal);
	}

	@Override
	public Time getTime(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTime(columnName);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		return query.getTimestamp(columnIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTimestamp(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		return query.getTimestamp(columnName, cal);
	}

	@Override
	public Timestamp getTimestamp(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getTimestamp(columnName);
	}

	@Override
	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return query.getType();
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getUnicodeStream(columnIndex);
	}

	@Override
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getUnicodeStream(columnName);
	}

	@Override
	public int getUpdateCount() {
		// TODO Auto-generated method stub
		return query.getUpdateCount();
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return query.getURL(columnIndex);
	}

	@Override
	public URL getURL(String columnName) throws SQLException {
		// TODO Auto-generated method stub
		return query.getURL(columnName);
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return query.getWarnings();
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		// TODO Auto-generated method stub
		return query.isAfterLast();
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		return query.isBeforeFirst();
	}

	@Override
	public boolean isFirst() throws SQLException {
		// TODO Auto-generated method stub
		return query.isFirst();
	}

	@Override
	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return query.isLast();
	}

	@Override
	public boolean last() throws SQLException {
		// TODO Auto-generated method stub
		return query.last();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub
		query.moveToCurrentRow();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub
		query.moveToInsertRow();
	}

	@Override
	public boolean previous() {
		// TODO Auto-generated method stub
		return query.previous();
	}

	@Override
	public boolean previous(int pid) {
		// TODO Auto-generated method stub
		return query.previous(pid);
	}

	@Override
	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub
		query.refreshRow();
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		// TODO Auto-generated method stub
		return query.relative(rows);
	}

	@Override
	public void setColumnNames(Collection.Key[] trg) throws PageException {
		// TODO Auto-generated method stub
		query.setColumnNames(trg);
	}

	@Override
	public Iterator<Object> valueIterator() {
		// TODO Auto-generated method stub
		return query.valueIterator();
	}

}
