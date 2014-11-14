/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.cfx;


import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
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
import java.util.Iterator;
import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;

import com.allaire.cfx.Query;

/**
 * Implementation of the Query Interface
 */
public class QueryWrap implements Query {
	

	private railo.runtime.type.Query rst;
	private String name;

	/**
	 * constructor of the class
	 * @param rst runtime Query
	 */
	public QueryWrap(railo.runtime.type.Query rst) {
		this.rst=rst;
		this.name=rst.getName();
	}
	/**
	 * constructor of the class
	 * @param rst runtime Query
	 * @param name name of the query (otherwise rst.getName())
	 */
	public QueryWrap(railo.runtime.type.Query rst, String name) {
		this.rst=rst;
		this.name=name;
	}

	/**
	 * @see com.allaire.cfx.Query#addRow()
	 */
	public int addRow() {
		return rst.addRow();
	}

	/**
	 * @see com.allaire.cfx.Query#getColumnIndex(java.lang.String)
	 */
	public int getColumnIndex(String coulmnName) {
		return rst.getColumnIndex(coulmnName);
	}

	/**
	 * @see com.allaire.cfx.Query#getColumns()
	 */
	public String[] getColumns() {
		return rst.getColumns();
	}
	
	public Collection.Key[] getColumnNames() {
    	return rst.getColumnNames();
    }
    

	public String[] getColumnNamesAsString() {
		return rst.getColumnNamesAsString();
	}

	/**
	 * @see com.allaire.cfx.Query#getData(int, int)
	 */
	public String getData(int row, int col) throws IndexOutOfBoundsException {
		return rst.getData(row,col);
	}

	/**
	 * @see com.allaire.cfx.Query#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see com.allaire.cfx.Query#getRowCount()
	 */
	public int getRowCount() {
		return rst.getRowCount();
	}

	/**
	 * @see com.allaire.cfx.Query#setData(int, int, java.lang.String)
	 */
	public void setData(int row, int col, String value) throws IndexOutOfBoundsException {
	    rst.setData(row,col,value);
	}
	
	
	/**
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int row) throws SQLException {	
		return rst.absolute(row);
	}
	/**
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException {
		rst.afterLast();
	}
	/**
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException {	
		rst.beforeFirst();
	}
	/**
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException {	
		rst.cancelRowUpdates();
	}
	/**
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException {
		rst.clearWarnings();
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {	
		return rst.clone();
	}
	/**
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException {	
		rst.close();
	}
	/**
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException {	
		rst.deleteRow();
	}
	/**
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) throws SQLException {
		return rst.findColumn(columnName);
	}
	/**
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException {	
		return rst.first();
	}
	/**
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(int i) throws SQLException {
		return rst.getArray(i);
	}
	/**
	 * @see java.sql.ResultSet#getArray(java.lang.String)
	 */
	public Array getArray(String colName) throws SQLException {
		return rst.getArray(colName);
	}
	/**
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return rst.getAsciiStream(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {	
		return rst.getAsciiStream(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale)throws SQLException {
		return rst.getBigDecimal(columnIndex, scale);
	}
	/**
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return rst.getBigDecimal(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
	 */
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		return rst.getBigDecimal(columnName, scale);
	}
	/**
	 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
	 */
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return rst.getBigDecimal(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return rst.getBinaryStream(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return rst.getBinaryStream(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(int i) throws SQLException {
		return rst.getBlob(i);
	}
	/**
	 * @see java.sql.ResultSet#getBlob(java.lang.String)
	 */
	public Blob getBlob(String colName) throws SQLException {
		return rst.getBlob(colName);
	}
	/**
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return rst.getBoolean(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return rst.getBoolean(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(int columnIndex) throws SQLException {
		return rst.getByte(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getByte(java.lang.String)
	 */
	public byte getByte(String columnName) throws SQLException {
		return rst.getByte(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return rst.getBytes(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getBytes(java.lang.String)
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return rst.getBytes(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {	
		return rst.getCharacterStream(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
	 */
	public Reader getCharacterStream(String columnName) throws SQLException {
		return rst.getCharacterStream(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(int i) throws SQLException {	
		return rst.getClob(i);
	}
	/**
	 * @see java.sql.ResultSet#getClob(java.lang.String)
	 */
	public Clob getClob(String colName) throws SQLException {
		return rst.getClob(colName);
	}
	/**
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException {	
		return rst.getConcurrency();
	}
	/**
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException {
		return rst.getCursorName();
	}
	/**
	 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return rst.getDate(columnIndex, cal);
	}
	/**
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public Date getDate(int columnIndex) throws SQLException {
		return rst.getDate(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		return rst.getDate(columnName, cal);
	}
	/**
	 * @see java.sql.ResultSet#getDate(java.lang.String)
	 */
	public Date getDate(String columnName) throws SQLException {
		return rst.getDate(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(int columnIndex) throws SQLException {
		return rst.getDouble(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getDouble(java.lang.String)
	 */
	public double getDouble(String columnName) throws SQLException {
		return rst.getDouble(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException {
		return rst.getFetchDirection();
	}
	/**
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException {
		return rst.getFetchSize();
	}
	/**
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(int columnIndex) throws SQLException {
		return rst.getFloat(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getFloat(java.lang.String)
	 */
	public float getFloat(String columnName) throws SQLException {
		return rst.getFloat(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(int columnIndex) throws SQLException {
		return rst.getInt(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getInt(java.lang.String)
	 */
	public int getInt(String columnName) throws SQLException {
		return rst.getInt(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(int columnIndex) throws SQLException {
		return rst.getLong(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getLong(java.lang.String)
	 */
	public long getLong(String columnName) throws SQLException {
		return rst.getLong(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException {	
		return rst.getMetaData();
	}
	/**
	 * @see java.sql.ResultSet#getObject(int, java.util.Map)
	 */
	public Object getObject(int i, Map map) throws SQLException {
		return rst.getObject(i, map);
	}
	/**
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(int columnIndex) throws SQLException {
		return rst.getObject(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
	 */
	public Object getObject(String colName, Map map) throws SQLException {
		return rst.getObject(colName, map);
	}
	/**
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	public Object getObject(String columnName) throws SQLException {
		return rst.getObject(columnName);
	}
	/**
	 * @return recordcount of the query
	 */
	public int getRecordcount() {
		return rst.getRecordcount();
	}
	/**
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int i) throws SQLException {
		return rst.getRef(i);
	}
	/**
	 * @see java.sql.ResultSet#getRef(java.lang.String)
	 */
	public Ref getRef(String colName) throws SQLException {
		return rst.getRef(colName);
	}
	/**
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException {
		return rst.getRow();
	}
	/**
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(int columnIndex) throws SQLException {
		return rst.getShort(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getShort(java.lang.String)
	 */
	public short getShort(String columnName) throws SQLException {	
		return rst.getShort(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getStatement()
	 */
	public Statement getStatement() throws SQLException {
		return rst.getStatement();
	}
	/**
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(int columnIndex) throws SQLException {
		return rst.getString(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	public String getString(String columnName) throws SQLException {
		return rst.getString(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return rst.getTime(columnIndex, cal);
	}
	/**
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(int columnIndex) throws SQLException {
		return rst.getTime(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return rst.getTime(columnName, cal);
	}
	/**
	 * @see java.sql.ResultSet#getTime(java.lang.String)
	 */
	public Time getTime(String columnName) throws SQLException {
		return rst.getTime(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal)throws SQLException {	
		return rst.getTimestamp(columnIndex, cal);
	}
	/**
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return rst.getTimestamp(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		return rst.getTimestamp(columnName, cal);
	}
	/**
	 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return rst.getTimestamp(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException {	
		return rst.getType();
	}
	/**
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return rst.getUnicodeStream(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
	 */
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		return rst.getUnicodeStream(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int columnIndex) throws SQLException {
		return rst.getURL(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#getURL(java.lang.String)
	 */
	public URL getURL(String columnName) throws SQLException {
		return rst.getURL(columnName);
	}
	/**
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException {
		return rst.getWarnings();
	}
	/**
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException {
		rst.insertRow();
	}
	/**
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException {
		
		return rst.isAfterLast();
	}
	/**
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException {
		return rst.isBeforeFirst();
	}
	/**
	 * @return is cached
	 */
	public boolean isCached() {
		return rst.isCached();
	}
	/**
	 * @return has records
	 */
	public boolean isEmpty() {
		return rst.isEmpty();
	}
	/**
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException {
		return rst.isFirst();
	}
	/**
	 * @see java.sql.ResultSet#isLast()
	 */
	public boolean isLast() throws SQLException {
		return rst.isLast();
	}
	
	/**
	 * @return iterator for he keys
	 */
	public Iterator<Collection.Key> keyIterator() {
		return rst.keyIterator();
	}
	
	/**
	 * @return all keys of the Query
	 */
	public Key[] keys() {
		return rst.keys();
	}
	/**
	 * @see java.sql.ResultSet#last()
	 */
	public boolean last() throws SQLException {
		
		return rst.last();
	}
	
	
	
	/**
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException {
		rst.moveToCurrentRow();
	}
	/**
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException {
		rst.moveToInsertRow();
	}
	/**
	 * @see java.sql.ResultSet#next()
	 */
	public boolean next() {
		return rst.next();
	}
	/**
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous()  throws SQLException{
		return rst.previous();
	}
	/**
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException {
		rst.refreshRow();
	}
	/**
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int rows) throws SQLException {
		return rst.relative(rows);
	}
	/**
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException {
		return rst.rowDeleted();
	}
	/**
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException {
		return rst.rowInserted();
	}
	/**
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException {
		return rst.rowUpdated();
	}
	
	/**
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(int direction) throws SQLException {
		rst.setFetchDirection(direction);
	}
	/**
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	public void setFetchSize(int rows) throws SQLException {
		rst.setFetchSize(rows);
	}
	
	/**
	 * @return the size of the query
	 */
	public int size() {	
		return rst.size();
	}
	/**
	 * @param keyColumn
	 * @param order
	 * @throws PageException
	 */
	public synchronized void sort(Key keyColumn, int order)
			throws PageException {
		
		rst.sort(keyColumn, order);
	}
	/**
	 * @param column
	 * @throws PageException
	 */
	public void sort(Key column) throws PageException {
		
		rst.sort(column);
	}
	/**
	 * @param strColumn
	 * @param order
	 * @throws PageException
	 */
	public synchronized void sort(String strColumn, int order)
			throws PageException {
		
		rst.sort(strColumn, order);
	}
	/**
	 * @param column
	 * @throws PageException
	 */
	public void sort(String column) throws PageException {
		
		rst.sort(column);
	}
	/**
	 * @param pageContext
	 * @param maxlevel
	 * @param dp
	 * @return generated DumpData
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties dp) {
		return rst.toDumpData(pageContext, maxlevel, dp);
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return rst.toString();
	}
	/**
	 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
	 */
	public void updateArray(int columnIndex, Array x) throws SQLException {
		rst.updateArray(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
	 */
	public void updateArray(String columnName, Array x) throws SQLException {
		rst.updateArray(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		rst.updateAsciiStream(columnIndex, x, length);
	}
	/**
	 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length)
			throws SQLException {
		
		rst.updateAsciiStream(columnName, x, length);
	}
	/**
	 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		
		rst.updateBigDecimal(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
	 */
	public void updateBigDecimal(String columnName, BigDecimal x)
			throws SQLException {
		
		rst.updateBigDecimal(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		
		rst.updateBinaryStream(columnIndex, x, length);
	}
	/**
	 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
	 */
	public void updateBinaryStream(String columnName, InputStream x, int length)
			throws SQLException {
		
		rst.updateBinaryStream(columnName, x, length);
	}
	/**
	 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
	 */
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		
		rst.updateBlob(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
	 */
	public void updateBlob(String columnName, Blob x) throws SQLException {
		
		rst.updateBlob(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		
		rst.updateBoolean(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
	 */
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		
		rst.updateBoolean(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		
		rst.updateByte(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		
		rst.updateByte(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		
		rst.updateBytes(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		rst.updateBytes(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
	 */
	public void updateCharacterStream(int columnIndex, Reader reader, int length)
			throws SQLException {
		
		rst.updateCharacterStream(columnIndex, reader, length);
	}
	/**
	 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
	 */
	public void updateCharacterStream(String columnName, Reader reader,
			int length) throws SQLException {
		rst.updateCharacterStream(columnName, reader, length);
	}
	/**
	 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
	 */
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		rst.updateClob(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
	 */
	public void updateClob(String columnName, Clob x) throws SQLException {
		rst.updateClob(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
	 */
	public void updateDate(int columnIndex, Date x) throws SQLException {	
		rst.updateDate(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
	 */
	public void updateDate(String columnName, Date x) throws SQLException {
		rst.updateDate(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		rst.updateDouble(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		rst.updateDouble(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		rst.updateFloat(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		rst.updateFloat(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		rst.updateInt(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		rst.updateInt(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		rst.updateLong(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		rst.updateLong(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int columnIndex) throws SQLException {
		rst.updateNull(columnIndex);
	}
	/**
	 * @see java.sql.ResultSet#updateNull(java.lang.String)
	 */
	public void updateNull(String columnName) throws SQLException {
		rst.updateNull(columnName);
	}
	/**
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
	 */
	public void updateObject(int columnIndex, Object x, int scale)
			throws SQLException {
		rst.updateObject(columnIndex, x, scale);
	}
	/**
	 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		rst.updateObject(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
	 */
	public void updateObject(String columnName, Object x, int scale)
			throws SQLException {
		rst.updateObject(columnName, x, scale);
	}
	/**
	 * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		rst.updateObject(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
	 */
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		rst.updateRef(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
	 */
	public void updateRef(String columnName, Ref x) throws SQLException {
		rst.updateRef(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException {
		rst.updateRow();
	}
	/**
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int columnIndex, short x) throws SQLException {
		rst.updateShort(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
	 */
	public void updateShort(String columnName, short x) throws SQLException {
		rst.updateShort(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		rst.updateString(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
	 */
	public void updateString(String columnName, String x) throws SQLException {
		rst.updateString(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		rst.updateTime(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		rst.updateTime(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
	 */
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		rst.updateTimestamp(columnIndex, x);
	}
	/**
	 * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
	 */
	public void updateTimestamp(String columnName, Timestamp x)
			throws SQLException {
		rst.updateTimestamp(columnName, x);
	}
	/**
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull()  throws SQLException{
		return rst.wasNull();
	}
	public railo.runtime.type.Query getQuery() {
		return rst;
	}
	public int getHoldability() throws SQLException {
		throw notSupported();
	}
	public Reader getNCharacterStream(int arg0) throws SQLException {
		throw notSupported();
	}
	public Reader getNCharacterStream(String arg0) throws SQLException {
		throw notSupported();
	}
	public String getNString(int arg0) throws SQLException {
		throw notSupported();
	}
	public String getNString(String arg0) throws SQLException {
		throw notSupported();
	}
	public boolean isClosed() throws SQLException {
		throw notSupported();
	}
	public void updateAsciiStream(int arg0, InputStream arg1)throws SQLException {
		throw notSupported();
	}
	public void updateAsciiStream(String arg0, InputStream arg1)throws SQLException {
		throw notSupported();
	}
	public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateAsciiStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateBinaryStream(int arg0, InputStream arg1)
			throws SQLException {
		throw notSupported();
	}
	public void updateBinaryStream(String arg0, InputStream arg1)
			throws SQLException {
		throw notSupported();
	}
	public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateBinaryStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateBlob(int arg0, InputStream arg1) throws SQLException {
		throw notSupported();
	}
	public void updateBlob(String arg0, InputStream arg1) throws SQLException {
		throw notSupported();
	}
	public void updateBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateBlob(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		throw notSupported();
	}
	public void updateCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		throw notSupported();
	}
	public void updateCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateClob(int arg0, Reader arg1) throws SQLException {
		throw notSupported();
	}
	public void updateClob(String arg0, Reader arg1) throws SQLException {
		throw notSupported();
	}
	public void updateClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateNCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		throw notSupported();
	}
	public void updateNCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		throw notSupported();
	}
	public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateNClob(int arg0, Reader arg1) throws SQLException {
		throw notSupported();
	}
	public void updateNClob(String arg0, Reader arg1) throws SQLException {
		throw notSupported();
	}
	public void updateNClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateNClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		throw notSupported();
	}
	public void updateNString(int arg0, String arg1) throws SQLException {
		throw notSupported();
	}
	public void updateNString(String arg0, String arg1) throws SQLException {
		throw notSupported();
	}
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		throw notSupported();
	}
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		throw notSupported();
	}

	//JDK6: uncomment this for compiling with JDK6 
	public NClob getNClob(int arg0) throws SQLException {
		return rst.getNClob(arg0);
	}
	public NClob getNClob(String arg0) throws SQLException {
		return rst.getNClob(arg0);
	}
	public RowId getRowId(int arg0) throws SQLException {
		return rst.getRowId(arg0);
	}
	public RowId getRowId(String arg0) throws SQLException {
		return rst.getRowId(arg0);
	}
	public SQLXML getSQLXML(int arg0) throws SQLException {
		return rst.getSQLXML(arg0);
	}
	public SQLXML getSQLXML(String arg0) throws SQLException {
		return rst.getSQLXML(arg0);
	}
	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		rst.updateNClob(arg0, arg1);
	}
	public void updateNClob(String arg0, NClob arg1) throws SQLException {
		rst.updateNClob(arg0, arg1);
	}
	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		rst.updateRowId(arg0, arg1);
	}
	public void updateRowId(String arg0, RowId arg1) throws SQLException {
		rst.updateRowId(arg0, arg1);
	}
	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		rst.updateSQLXML(arg0, arg1);
	}
	public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
		rst.updateSQLXML(arg0, arg1);
	}
	
	


	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    	try {
    		Method m = rst.getClass().getMethod("getObject", new Class[]{int.class,Class.class});
    		return (T) m.invoke(rst, new Object[]{columnIndex,type});
		} 
    	catch (Throwable t) {}
    	throw notSupported();
	}
	
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    	try {
    		Method m = rst.getClass().getMethod("getObject", new Class[]{String.class,Class.class});
    		return (T) m.invoke(rst, new Object[]{columnLabel,type});
		} 
    	catch (Throwable t) {}
    	throw notSupported();
	}

	private SQLException notSupported() {
		return new SQLException("this feature is not supported");
	}
	private RuntimeException notSupportedEL() {
		return new RuntimeException(new SQLException("this feature is not supported"));
	}
}