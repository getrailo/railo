package railo.runtime.type.trace;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import railo.runtime.debug.Debugger;
import railo.runtime.exp.PageException;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;

public class TOQuery extends TOCollection implements Query,com.allaire.cfx.Query {


	private Query qry;

	protected TOQuery(Debugger debugger,Query qry, int type, String category, String text) {
		super(debugger,qry,type,category,text);
		this.qry=qry;
	}

	/**
	 * @see railo.runtime.type.QueryImpl#executionTime()
	 */
	
	public int executionTime() {
		
		return qry.executionTime();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUpdateCount()
	 */
	
	public int getUpdateCount() {
		
		return qry.getUpdateCount();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getGeneratedKeys()
	 */
	
	public Query getGeneratedKeys() {
		
		return qry.getGeneratedKeys();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(java.lang.String, int, java.lang.Object)
	 */
	
	public Object getAt(String key, int row, Object defaultValue) {
		
		return qry.getAt(key, row, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	
	public Object getAt(Key key, int row, Object defaultValue) {
		
		return qry.getAt(key, row, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(java.lang.String, int)
	 */
	
	public Object getAt(String key, int row) throws PageException {
		
		return qry.getAt(key, row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAt(railo.runtime.type.Collection.Key, int)
	 */
	
	public Object getAt(Key key, int row) throws PageException {
		
		return qry.getAt(key, row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeRow(int)
	 */
	
	public synchronized int removeRow(int row) throws PageException {
		
		return qry.removeRow(row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeRowEL(int)
	 */
	
	public int removeRowEL(int row) {
		
		return qry.removeRowEL(row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumn(java.lang.String)
	 */
	
	public QueryColumn removeColumn(String key) throws PageException {
		log(key);
		return qry.removeColumn(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumn(railo.runtime.type.Collection.Key)
	 */
	
	public QueryColumn removeColumn(Key key) throws PageException {
		log(key.getString());
		return qry.removeColumn(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumnEL(java.lang.String)
	 */
	
	public synchronized QueryColumn removeColumnEL(String key) {
		log(key);
		return qry.removeColumnEL(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#removeColumnEL(railo.runtime.type.Collection.Key)
	 */
	
	public QueryColumn removeColumnEL(Key key) {
		log(key.getString());
		return qry.removeColumnEL(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAt(java.lang.String, int, java.lang.Object)
	 */
	
	public Object setAt(String key, int row, Object value) throws PageException {
		log(key);
		return qry.setAt(key, row, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAt(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	
	public Object setAt(Key key, int row, Object value) throws PageException {
		log(key.getString());
		return qry.setAt(key, row, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAtEL(java.lang.String, int, java.lang.Object)
	 */
	
	public Object setAtEL(String key, int row, Object value) {
		log(key);
		return qry.setAtEL(key, row, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setAtEL(railo.runtime.type.Collection.Key, int, java.lang.Object)
	 */
	
	public Object setAtEL(Key key, int row, Object value) {
		log(key.getString());
		return qry.setAtEL(key, row, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#next()
	 */
	
	public boolean next() {
		log();
		return qry.next();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#next(int)
	 */
	
	public boolean next(int pid) throws PageException {
		log();
		return qry.next(pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#reset()
	 */
	
	public void reset() throws PageException {
		log();
		qry.reset();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#reset(int)
	 */
	
	public void reset(int pid) throws PageException {
		log();
		qry.reset(pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRecordcount()
	 */
	
	public int getRecordcount() {
		log();
		return qry.getRecordcount();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCurrentrow()
	 */
	
	public int getCurrentrow() {
		log();
		return qry.getCurrentrow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCurrentrow(int)
	 */
	
	public int getCurrentrow(int pid) {
		log();
		return qry.getCurrentrow(pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#go(int, int)
	 */
	
	public boolean go(int index, int pid) throws PageException {
		log();
		return qry.go(index, pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isEmpty()
	 */
	
	public boolean isEmpty() {
		log();
		return qry.isEmpty();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(java.lang.String)
	 */
	
	public void sort(String column) throws PageException {
		log(column);
		qry.sort(column);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(railo.runtime.type.Collection.Key)
	 */
	
	public void sort(Key column) throws PageException {
		log(column.getString());
		qry.sort(column);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(java.lang.String, int)
	 */
	
	public synchronized void sort(String strColumn, int order)
			throws PageException {
		log(strColumn);
		qry.sort(strColumn, order);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#sort(railo.runtime.type.Collection.Key, int)
	 */
	
	public synchronized void sort(Key keyColumn, int order)
			throws PageException {
		log(keyColumn.getString());
		qry.sort(keyColumn, order);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addRow(int)
	 */
	
	public synchronized boolean addRow(int count) {
		log(""+count);
		return qry.addRow(count);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(java.lang.String, railo.runtime.type.Array)
	 */
	
	public boolean addColumn(String columnName, Array content)
			throws PageException {
		log(columnName);
		return qry.addColumn(columnName, content);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(railo.runtime.type.Collection.Key, railo.runtime.type.Array)
	 */
	
	public boolean addColumn(Key columnName, Array content)
			throws PageException {
		log(columnName.getString());
		return qry.addColumn(columnName, content);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(java.lang.String, railo.runtime.type.Array, int)
	 */
	
	public synchronized boolean addColumn(String columnName, Array content,
			int type) throws PageException {
		log(columnName);
		return qry.addColumn(columnName, content, type);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addColumn(railo.runtime.type.Collection.Key, railo.runtime.type.Array, int)
	 */
	
	public boolean addColumn(Key columnName, Array content, int type)
			throws PageException {
		log();
		return qry.addColumn(columnName, content, type);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTypes()
	 */
	
	public synchronized int[] getTypes() {
		log();
		return qry.getTypes();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTypesAsMap()
	 */
	
	public synchronized Map getTypesAsMap() {
		log();
		return qry.getTypesAsMap();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(java.lang.String)
	 */
	
	public QueryColumn getColumn(String key) throws PageException {
		log(key);
		return qry.getColumn(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(railo.runtime.type.Collection.Key)
	 */
	
	public QueryColumn getColumn(Key key) throws PageException {
		log(key.getString());
		return qry.getColumn(key);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rename(railo.runtime.type.Collection.Key, railo.runtime.type.Collection.Key)
	 */
	
	public synchronized void rename(Key columnName, Key newColumnName)
			throws PageException {
		log(columnName+":"+newColumnName);
		qry.rename(columnName, newColumnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(java.lang.String, railo.runtime.type.QueryColumn)
	 */
	
	public QueryColumn getColumn(String key, QueryColumn defaultValue) {
		log(key);
		return qry.getColumn(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumn(railo.runtime.type.Collection.Key, railo.runtime.type.QueryColumn)
	 */
	
	public QueryColumn getColumn(Key key, QueryColumn defaultValue) {
		log(key.getString());
		return qry.getColumn(key, defaultValue);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setExecutionTime(long)
	 */
	
	public void setExecutionTime(long exeTime) {
		log();
		qry.setExecutionTime(exeTime);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setCached(boolean)
	 */
	
	public void setCached(boolean isCached) {
		log(""+isCached);
		qry.setCached(isCached);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isCached()
	 */
	
	public boolean isCached() {
		log();
		return qry.isCached();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#addRow()
	 */
	
	public int addRow() {
		log();
		return qry.addRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnIndex(java.lang.String)
	 */
	
	public int getColumnIndex(String coulmnName) {
		log(coulmnName);
		return qry.getColumnIndex(coulmnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumns()
	 */
	
	public String[] getColumns() {
		log();
		return qry.getColumns();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnNames()
	 */
	
	public Key[] getColumnNames() {
		log();
		return qry.getColumnNames();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getColumnNamesAsString()
	 */
	
	public String[] getColumnNamesAsString() {
		log();
		return qry.getColumnNamesAsString();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getData(int, int)
	 */
	
	public String getData(int row, int col) throws IndexOutOfBoundsException {
		log(row+":"+col);
		return qry.getData(row, col);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getName()
	 */
	
	public String getName() {
		log();
		return qry.getName();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRowCount()
	 */
	
	public int getRowCount() {
		log();
		return qry.getRowCount();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setData(int, int, java.lang.String)
	 */
	
	public void setData(int row, int col, String value)
			throws IndexOutOfBoundsException {
		log(""+row);
		qry.setData(row, col, value);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getMetaDataSimple()
	 */
	
	public synchronized Array getMetaDataSimple() {
		log();
		return qry.getMetaDataSimple();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(java.lang.String)
	 */
	
	public Object getObject(String columnName) throws SQLException {
		log(columnName);
		return qry.getObject(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getObject(int)
	 */
	
	public Object getObject(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getObject(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getString(int)
	 */
	
	public String getString(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getString(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getString(java.lang.String)
	 */
	
	public String getString(String columnName) throws SQLException {
		log(columnName);
		return qry.getString(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBoolean(int)
	 */
	
	public boolean getBoolean(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getBoolean(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBoolean(java.lang.String)
	 */
	
	public boolean getBoolean(String columnName) throws SQLException {
		log(columnName);
		return qry.getBoolean(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#wasNull()
	 */
	
	public boolean wasNull()  throws SQLException{
		log();
		return qry.wasNull();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#absolute(int)
	 */
	
	public boolean absolute(int row) throws SQLException {
		log();
		return qry.absolute(row);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#afterLast()
	 */
	
	public void afterLast() throws SQLException {
		log();
		qry.afterLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#beforeFirst()
	 */
	
	public void beforeFirst() throws SQLException {
		log();
		qry.beforeFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#cancelRowUpdates()
	 */
	
	public void cancelRowUpdates() throws SQLException {
		log();
		qry.cancelRowUpdates();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#clearWarnings()
	 */
	
	public void clearWarnings() throws SQLException {
		log();
		qry.clearWarnings();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#close()
	 */
	
	public void close() throws SQLException {
		log();
		qry.close();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#deleteRow()
	 */
	
	public void deleteRow() throws SQLException {
		log();
		qry.deleteRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#findColumn(java.lang.String)
	 */
	
	public int findColumn(String columnName) throws SQLException {
		log();
		return qry.findColumn(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#first()
	 */
	
	public boolean first() throws SQLException {
		log();
		return qry.first();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getArray(int)
	 */
	
	public java.sql.Array getArray(int i) throws SQLException {
		log(""+i);
		return qry.getArray(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getArray(java.lang.String)
	 */
	
	public java.sql.Array getArray(String colName) throws SQLException {
		log(colName);
		return qry.getArray(colName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAsciiStream(int)
	 */
	
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getAsciiStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getAsciiStream(java.lang.String)
	 */
	
	public InputStream getAsciiStream(String columnName) throws SQLException {
		log(columnName);
		return qry.getAsciiStream(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(int)
	 */
	
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getBigDecimal(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(java.lang.String)
	 */
	
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		log(columnName);
		return qry.getBigDecimal(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(int, int)
	 */
	
	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		log(""+columnIndex);
		return qry.getBigDecimal(columnIndex, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBigDecimal(java.lang.String, int)
	 */
	
	public BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		log(columnName);
		return qry.getBigDecimal(columnName, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBinaryStream(int)
	 */
	
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getBinaryStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBinaryStream(java.lang.String)
	 */
	
	public InputStream getBinaryStream(String columnName) throws SQLException {
		log(columnName);
		return qry.getBinaryStream(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBlob(int)
	 */
	
	public Blob getBlob(int i) throws SQLException {
		log(""+i);
		return qry.getBlob(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBlob(java.lang.String)
	 */
	
	public Blob getBlob(String colName) throws SQLException {
		log(colName);
		return qry.getBlob(colName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getByte(int)
	 */
	
	public byte getByte(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getByte(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getByte(java.lang.String)
	 */
	
	public byte getByte(String columnName) throws SQLException {
		log(""+columnName);
		return qry.getByte(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBytes(int)
	 */
	
	public byte[] getBytes(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getBytes(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getBytes(java.lang.String)
	 */
	
	public byte[] getBytes(String columnName) throws SQLException {
		log(columnName);
		return qry.getBytes(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCharacterStream(int)
	 */
	
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getCharacterStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCharacterStream(java.lang.String)
	 */
	
	public Reader getCharacterStream(String columnName) throws SQLException {
		log(columnName);
		return qry.getCharacterStream(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getClob(int)
	 */
	
	public Clob getClob(int i) throws SQLException {
		log(""+i);
		return qry.getClob(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getClob(java.lang.String)
	 */
	
	public Clob getClob(String colName) throws SQLException {
		log(colName);
		return qry.getClob(colName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getConcurrency()
	 */
	
	public int getConcurrency() throws SQLException {
		log();
		return qry.getConcurrency();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getCursorName()
	 */
	
	public String getCursorName() throws SQLException {
		log();
		return qry.getCursorName();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(int)
	 */
	
	public Date getDate(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getDate(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(java.lang.String)
	 */
	
	public Date getDate(String columnName) throws SQLException {
		log(columnName);
		return qry.getDate(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(int, java.util.Calendar)
	 */
	
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		log(columnIndex+"");
		return qry.getDate(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDate(java.lang.String, java.util.Calendar)
	 */
	
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		log(columnName);
		return qry.getDate(columnName, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDouble(int)
	 */
	
	public double getDouble(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getDouble(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getDouble(java.lang.String)
	 */
	
	public double getDouble(String columnName) throws SQLException {
		log(columnName);
		return qry.getDouble(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFetchDirection()
	 */
	
	public int getFetchDirection() throws SQLException {
		log();
		return qry.getFetchDirection();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFetchSize()
	 */
	
	public int getFetchSize() throws SQLException {
		log();
		return qry.getFetchSize();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFloat(int)
	 */
	
	public float getFloat(int columnIndex) throws SQLException {
		log(columnIndex+"");
		return qry.getFloat(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getFloat(java.lang.String)
	 */
	
	public float getFloat(String columnName) throws SQLException {
		log(columnName);
		return qry.getFloat(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getInt(int)
	 */
	
	public int getInt(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getInt(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getInt(java.lang.String)
	 */
	
	public int getInt(String columnName) throws SQLException {
		log(columnName);
		return qry.getInt(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getLong(int)
	 */
	
	public long getLong(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getLong(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getLong(java.lang.String)
	 */
	
	public long getLong(String columnName) throws SQLException {
		log(columnName);
		return qry.getLong(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRef(int)
	 */
	
	public Ref getRef(int i) throws SQLException {
		log(""+i);
		return qry.getRef(i);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRef(java.lang.String)
	 */
	
	public Ref getRef(String colName) throws SQLException {
		log(colName);
		return qry.getRef(colName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getRow()
	 */
	
	public int getRow() throws SQLException {
		log();
		return qry.getRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getShort(int)
	 */
	
	public short getShort(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getShort(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getShort(java.lang.String)
	 */
	
	public short getShort(String columnName) throws SQLException {
		log(columnName);
		return qry.getShort(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getStatement()
	 */
	
	public Statement getStatement() throws SQLException {
		log();
		return qry.getStatement();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(int)
	 */
	
	public Time getTime(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getTime(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(java.lang.String)
	 */
	
	public Time getTime(String columnName) throws SQLException {
		log(columnName);
		return qry.getTime(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(int, java.util.Calendar)
	 */
	
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		log(""+columnIndex);
		return qry.getTime(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTime(java.lang.String, java.util.Calendar)
	 */
	
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		log(columnName);
		return qry.getTime(columnName, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(int)
	 */
	
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getTimestamp(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(java.lang.String)
	 */
	
	public Timestamp getTimestamp(String columnName) throws SQLException {
		log(columnName);
		return qry.getTimestamp(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(int, java.util.Calendar)
	 */
	
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		log(""+columnIndex);
		return qry.getTimestamp(columnIndex, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	
	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		log(columnName);
		return qry.getTimestamp(columnName, cal);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getType()
	 */
	
	public int getType() throws SQLException {
		log();
		return qry.getType();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getURL(int)
	 */
	
	public URL getURL(int columnIndex) throws SQLException {
		log();
		return qry.getURL(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getURL(java.lang.String)
	 */
	
	public URL getURL(String columnName) throws SQLException {
		log();
		return qry.getURL(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUnicodeStream(int)
	 */
	
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		log();
		return qry.getUnicodeStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getUnicodeStream(java.lang.String)
	 */
	
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		log();
		return qry.getUnicodeStream(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getWarnings()
	 */
	
	public SQLWarning getWarnings() throws SQLException {
		log();
		return qry.getWarnings();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#insertRow()
	 */
	
	public void insertRow() throws SQLException {
		log();
		qry.insertRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isAfterLast()
	 */
	
	public boolean isAfterLast() throws SQLException {
		log();
		return qry.isAfterLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isBeforeFirst()
	 */
	
	public boolean isBeforeFirst() throws SQLException {
		log();
		return qry.isBeforeFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isFirst()
	 */
	
	public boolean isFirst() throws SQLException {
		log();
		return qry.isFirst();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isLast()
	 */
	
	public boolean isLast() throws SQLException {
		log();
		return qry.isLast();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#last()
	 */
	
	public boolean last() throws SQLException {
		log();
		return qry.last();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#moveToCurrentRow()
	 */
	
	public void moveToCurrentRow() throws SQLException {
		log();
		qry.moveToCurrentRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#moveToInsertRow()
	 */
	
	public void moveToInsertRow() throws SQLException {
		log();
		qry.moveToInsertRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#previous()
	 */
	
	public boolean previous() throws SQLException {
		log();
		return qry.previous();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#previous(int)
	 */
	
	public boolean previous(int pid) {
		log();
		return qry.previous(pid);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#refreshRow()
	 */
	
	public void refreshRow() throws SQLException {
		log();
		qry.refreshRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#relative(int)
	 */
	
	public boolean relative(int rows) throws SQLException {
		log();
		return qry.relative(rows);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowDeleted()
	 */
	
	public boolean rowDeleted() throws SQLException {
		log();
		return qry.rowDeleted();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowInserted()
	 */
	
	public boolean rowInserted() throws SQLException {
		log();
		return qry.rowInserted();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#rowUpdated()
	 */
	
	public boolean rowUpdated() throws SQLException {
		log();
		return qry.rowUpdated();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setFetchDirection(int)
	 */
	
	public void setFetchDirection(int direction) throws SQLException {
		log();
		qry.setFetchDirection(direction);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#setFetchSize(int)
	 */
	
	public void setFetchSize(int rows) throws SQLException {
		log(""+rows);
		qry.setFetchSize(rows);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateArray(int, java.sql.Array)
	 */
	
	public void updateArray(int columnIndex, java.sql.Array x)
			throws SQLException {
		log(columnIndex+"");
		qry.updateArray(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateArray(java.lang.String, java.sql.Array)
	 */
	
	public void updateArray(String columnName, java.sql.Array x)
			throws SQLException {
		log(columnName);
		qry.updateArray(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(int, java.io.InputStream, int)
	 */
	
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		log(""+columnIndex);
		qry.updateAsciiStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	
	public void updateAsciiStream(String columnName, InputStream x, int length)
			throws SQLException {
		log(columnName);
		qry.updateAsciiStream(columnName, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBigDecimal(int, java.math.BigDecimal)
	 */
	
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		log(""+columnIndex);
		qry.updateBigDecimal(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	
	public void updateBigDecimal(String columnName, BigDecimal x)
			throws SQLException {
		log(columnName);
		qry.updateBigDecimal(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(int, java.io.InputStream, int)
	 */
	
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		log(""+columnIndex);
		qry.updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	
	public void updateBinaryStream(String columnName, InputStream x, int length)
			throws SQLException {
		log(columnName);
		qry.updateBinaryStream(columnName, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(int, java.sql.Blob)
	 */
	
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		log(""+columnIndex);
		qry.updateBlob(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(java.lang.String, java.sql.Blob)
	 */
	
	public void updateBlob(String columnName, Blob x) throws SQLException {
		log(columnName);
		qry.updateBlob(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBoolean(int, boolean)
	 */
	
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		log(""+columnIndex);
		qry.updateBoolean(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBoolean(java.lang.String, boolean)
	 */
	
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		log(columnName);
		qry.updateBoolean(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateByte(int, byte)
	 */
	
	public void updateByte(int columnIndex, byte x) throws SQLException {
		log(""+columnIndex);
		qry.updateByte(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateByte(java.lang.String, byte)
	 */
	
	public void updateByte(String columnName, byte x) throws SQLException {
		log(columnName);
		qry.updateByte(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBytes(int, byte[])
	 */
	
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		log(""+columnIndex);
		qry.updateBytes(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBytes(java.lang.String, byte[])
	 */
	
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		log(columnName);
		qry.updateBytes(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(int, java.io.Reader, int)
	 */
	
	public void updateCharacterStream(int columnIndex, Reader reader, int length)
			throws SQLException {
		log(""+columnIndex);
		qry.updateCharacterStream(columnIndex, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	
	public void updateCharacterStream(String columnName, Reader reader,
			int length) throws SQLException {
		log(columnName);
		qry.updateCharacterStream(columnName, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(int, java.sql.Clob)
	 */
	
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		log(""+columnIndex);
		qry.updateClob(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(java.lang.String, java.sql.Clob)
	 */
	
	public void updateClob(String columnName, Clob x) throws SQLException {
		log(columnName);
		qry.updateClob(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDate(int, java.sql.Date)
	 */
	
	public void updateDate(int columnIndex, Date x) throws SQLException {
		log(""+columnIndex);
		qry.updateDate(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDate(java.lang.String, java.sql.Date)
	 */
	
	public void updateDate(String columnName, Date x) throws SQLException {
		log(columnName);
		qry.updateDate(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDouble(int, double)
	 */
	
	public void updateDouble(int columnIndex, double x) throws SQLException {
		log(""+columnIndex);
		qry.updateDouble(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateDouble(java.lang.String, double)
	 */
	
	public void updateDouble(String columnName, double x) throws SQLException {
		log(columnName);
		qry.updateDouble(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateFloat(int, float)
	 */
	
	public void updateFloat(int columnIndex, float x) throws SQLException {
		log(""+columnIndex);
		qry.updateFloat(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateFloat(java.lang.String, float)
	 */
	
	public void updateFloat(String columnName, float x) throws SQLException {
		log(columnName);
		qry.updateFloat(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateInt(int, int)
	 */
	
	public void updateInt(int columnIndex, int x) throws SQLException {
		log(""+columnIndex);
		qry.updateInt(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateInt(java.lang.String, int)
	 */
	
	public void updateInt(String columnName, int x) throws SQLException {
		log(columnName);
		qry.updateInt(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateLong(int, long)
	 */
	
	public void updateLong(int columnIndex, long x) throws SQLException {
		log(""+columnIndex);
		qry.updateLong(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateLong(java.lang.String, long)
	 */
	
	public void updateLong(String columnName, long x) throws SQLException {
		log(columnName);
		qry.updateLong(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNull(int)
	 */
	
	public void updateNull(int columnIndex) throws SQLException {
		log(""+columnIndex);
		qry.updateNull(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNull(java.lang.String)
	 */
	
	public void updateNull(String columnName) throws SQLException {
		log(columnName);
		qry.updateNull(columnName);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(int, java.lang.Object)
	 */
	
	public void updateObject(int columnIndex, Object x) throws SQLException {
		
		qry.updateObject(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(java.lang.String, java.lang.Object)
	 */
	
	public void updateObject(String columnName, Object x) throws SQLException {
		log(columnName);
		qry.updateObject(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(int, java.lang.Object, int)
	 */
	
	public void updateObject(int columnIndex, Object x, int scale)
			throws SQLException {
		log(""+columnIndex);
		qry.updateObject(columnIndex, x, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateObject(java.lang.String, java.lang.Object, int)
	 */
	
	public void updateObject(String columnName, Object x, int scale)
			throws SQLException {
		log(columnName);
		qry.updateObject(columnName, x, scale);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRef(int, java.sql.Ref)
	 */
	
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		log(""+columnIndex);
		qry.updateRef(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRef(java.lang.String, java.sql.Ref)
	 */
	
	public void updateRef(String columnName, Ref x) throws SQLException {
		log(columnName);
		qry.updateRef(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateRow()
	 */
	
	public void updateRow() throws SQLException {
		log();
		qry.updateRow();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateShort(int, short)
	 */
	
	public void updateShort(int columnIndex, short x) throws SQLException {
		log(""+columnIndex);
		qry.updateShort(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateShort(java.lang.String, short)
	 */
	
	public void updateShort(String columnName, short x) throws SQLException {
		log(columnName);
		qry.updateShort(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateString(int, java.lang.String)
	 */
	
	public void updateString(int columnIndex, String x) throws SQLException {
		log(""+columnIndex);
		qry.updateString(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateString(java.lang.String, java.lang.String)
	 */
	
	public void updateString(String columnName, String x) throws SQLException {
		log(columnName);
		qry.updateString(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTime(int, java.sql.Time)
	 */
	
	public void updateTime(int columnIndex, Time x) throws SQLException {
		log(""+columnIndex);
		qry.updateTime(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTime(java.lang.String, java.sql.Time)
	 */
	
	public void updateTime(String columnName, Time x) throws SQLException {
		log(columnName);
		qry.updateTime(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTimestamp(int, java.sql.Timestamp)
	 */
	
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		log(""+columnIndex);
		qry.updateTimestamp(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	
	public void updateTimestamp(String columnName, Timestamp x)
			throws SQLException {
		log(columnName);
		qry.updateTimestamp(columnName, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getMetaData()
	 */
	
	public ResultSetMetaData getMetaData() throws SQLException {
		log();
		return qry.getMetaData();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getHoldability()
	 */
	
	public int getHoldability() throws SQLException {
		log();
		return qry.getHoldability();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isClosed()
	 */
	
	public boolean isClosed() throws SQLException {
		log();
		return qry.isClosed();
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNString(int, java.lang.String)
	 */
	
	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		log(""+columnIndex);
		qry.updateNString(columnIndex, nString);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNString(java.lang.String, java.lang.String)
	 */
	
	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		log(columnLabel);
		qry.updateNString(columnLabel, nString);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNString(int)
	 */
	
	public String getNString(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getNString(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNString(java.lang.String)
	 */
	
	public String getNString(String columnLabel) throws SQLException {
		log(columnLabel);
		return qry.getNString(columnLabel);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNCharacterStream(int)
	 */
	
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		log(""+columnIndex);
		return qry.getNCharacterStream(columnIndex);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#getNCharacterStream(java.lang.String)
	 */
	
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		log(columnLabel);
		return qry.getNCharacterStream(columnLabel);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNCharacterStream(int, java.io.Reader, long)
	 */
	
	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		log(""+columnIndex);
		qry.updateNCharacterStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	
	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		log(columnLabel);
		qry.updateNCharacterStream(columnLabel, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(int, java.io.InputStream, long)
	 */
	
	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		log(""+columnIndex);
		qry.updateAsciiStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(int, java.io.InputStream, long)
	 */
	
	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		log(columnIndex+"");
		qry.updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(int, java.io.Reader, long)
	 */
	
	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		log(columnIndex+"");
		qry.updateCharacterStream(columnIndex, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(java.lang.String, java.io.InputStream, long)
	 */
	
	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		log(columnLabel);
		qry.updateAsciiStream(columnLabel, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(java.lang.String, java.io.InputStream, long)
	 */
	
	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		log(columnLabel);
		qry.updateBinaryStream(columnLabel, x, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(java.lang.String, java.io.Reader, long)
	 */
	
	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		log(columnLabel);
		qry.updateCharacterStream(columnLabel, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(int, java.io.InputStream, long)
	 */
	
	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		log(""+columnIndex);
		qry.updateBlob(columnIndex, inputStream, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(java.lang.String, java.io.InputStream, long)
	 */
	
	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		log(columnLabel);
		qry.updateBlob(columnLabel, inputStream, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(int, java.io.Reader, long)
	 */
	
	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		log(""+columnIndex);
		qry.updateClob(columnIndex, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(java.lang.String, java.io.Reader, long)
	 */
	
	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		log(columnLabel);
		qry.updateClob(columnLabel, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(int, java.io.Reader, long)
	 */
	
	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		log(""+columnIndex);
		qry.updateNClob(columnIndex, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(java.lang.String, java.io.Reader, long)
	 */
	
	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		log(columnLabel);
		qry.updateNClob(columnLabel, reader, length);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNCharacterStream(int, java.io.Reader)
	 */
	
	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		log(""+columnIndex);
		qry.updateNCharacterStream(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNCharacterStream(java.lang.String, java.io.Reader)
	 */
	
	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		log(columnLabel);
		qry.updateNCharacterStream(columnLabel, reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(int, java.io.InputStream)
	 */
	
	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		log(""+columnIndex);
		qry.updateAsciiStream(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(int, java.io.InputStream)
	 */
	
	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		log(""+columnIndex);
		qry.updateBinaryStream(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(int, java.io.Reader)
	 */
	
	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		log(""+columnIndex);
		qry.updateCharacterStream(columnIndex, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateAsciiStream(java.lang.String, java.io.InputStream)
	 */
	
	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		log(columnLabel);
		qry.updateAsciiStream(columnLabel, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBinaryStream(java.lang.String, java.io.InputStream)
	 */
	
	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		log(columnLabel);
		qry.updateBinaryStream(columnLabel, x);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateCharacterStream(java.lang.String, java.io.Reader)
	 */
	
	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		log(columnLabel);
		qry.updateCharacterStream(columnLabel, reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(int, java.io.InputStream)
	 */
	
	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		log(""+columnIndex);
		qry.updateBlob(columnIndex, inputStream);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateBlob(java.lang.String, java.io.InputStream)
	 */
	
	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		log(columnLabel);
		qry.updateBlob(columnLabel, inputStream);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(int, java.io.Reader)
	 */
	
	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		log(""+columnIndex);
		qry.updateClob(columnIndex, reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateClob(java.lang.String, java.io.Reader)
	 */
	
	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		log(columnLabel);
		qry.updateClob(columnLabel, reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(int, java.io.Reader)
	 */
	
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		log(""+columnIndex);
		qry.updateNClob(columnIndex, reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#updateNClob(java.lang.String, java.io.Reader)
	 */
	
	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		log(columnLabel);
		qry.updateNClob(columnLabel, reader);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#unwrap(java.lang.Class)
	 */
	
	public <T> T unwrap(Class<T> iface) throws SQLException {
		log();
		return qry.unwrap(iface);
	}

	/**
	 * @see railo.runtime.type.QueryImpl#isWrapperFor(java.lang.Class)
	 */
	
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		log();
		return qry.isWrapperFor(iface);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		log();
		return new TOQuery(debugger,(Query)qry.duplicate(deepCopy), type,category,text);
	}

	@Override
	public boolean go(int index) throws PageException {
		log(""+index);
		return qry.go(index);
	}

	@Override
	public NClob getNClob(int arg0) throws SQLException {
		log(""+arg0);
		return qry.getNClob(arg0);
	}

	@Override
	public NClob getNClob(String arg0) throws SQLException {
		log(arg0);
		return qry.getNClob(arg0);
	}

	@Override
	public Object getObject(int arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		log(""+arg0);
		return qry.getObject(arg0, arg1);
	}

	@Override
	public Object getObject(String arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		log(arg0);
		return qry.getObject(arg0, arg1);
	}

	@Override
	public RowId getRowId(int arg0) throws SQLException {
		log(""+arg0);
		return qry.getRowId(arg0);
	}

	@Override
	public RowId getRowId(String arg0) throws SQLException {
		log(arg0);
		return qry.getRowId(arg0);
	}

	@Override
	public SQLXML getSQLXML(int arg0) throws SQLException {
		log(""+arg0);
		return qry.getSQLXML(arg0);
	}

	@Override
	public SQLXML getSQLXML(String arg0) throws SQLException {
		log(arg0);
		return qry.getSQLXML(arg0);
	}

	@Override
	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		log(""+arg0);
		qry.updateNClob(arg0, arg1);
	}

	@Override
	public void updateNClob(String arg0, NClob arg1) throws SQLException {
		log(arg0);
		qry.updateNClob(arg0, arg1);
	}

	@Override
	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		log(""+arg0);
		qry.updateRowId(arg0, arg1);
	}

	@Override
	public void updateRowId(String arg0, RowId arg1) throws SQLException {
		log(arg0);
		qry.updateRowId(arg0, arg1);
	}

	@Override
	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		log(arg0+"");
		qry.updateSQLXML(arg0, arg1);
	}

	@Override
	public void updateSQLXML(String columnIndex, SQLXML x) throws SQLException {
		log(columnIndex);
		qry.updateSQLXML(columnIndex, x);
	}

}
