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
package railo.runtime.db.driver;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import railo.runtime.PageContext;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;

public class StatementProxy implements StatementPro {

	protected ConnectionProxy conn;
	protected Statement stat;

	public StatementProxy(ConnectionProxy conn,Statement stat){
		this.conn=conn;
		this.stat=stat;
	}
	
	@Override
	public boolean execute(PageContext pc,String sql) throws SQLException {
		return stat.execute(sql);
	}

	@Override
	public boolean execute(PageContext pc,String sql, int autoGeneratedKeys) throws SQLException {
		return stat.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(PageContext pc,String sql, int[] columnIndexes) throws SQLException {
		return stat.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(PageContext pc,String sql, String[] columnNames) throws SQLException {
		return stat.execute(sql, columnNames);
	}

	@Override
	public ResultSet executeQuery(PageContext pc,String sql) throws SQLException {
		return stat.executeQuery(sql);
	}

	@Override
	public int executeUpdate(PageContext pc,String sql) throws SQLException {
		return stat.executeUpdate(sql);
	}

	@Override
	public int executeUpdate(PageContext pc,String sql, int autoGeneratedKeys) throws SQLException {
		return stat.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(PageContext pc,String sql, int[] columnIndexes) throws SQLException {
		return stat.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(PageContext pc,String sql, String[] columnNames) throws SQLException {
		return stat.executeUpdate(sql, columnNames);
	}


	@Override
	public boolean execute(String sql) throws SQLException {
		return stat.execute(sql);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return stat.execute(sql, autoGeneratedKeys);
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return stat.execute(sql, columnIndexes);
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return stat.execute(sql, columnNames);
	}

	@Override
	public int[] executeBatch() throws SQLException {
		return stat.executeBatch();
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		return stat.executeQuery(sql);
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		return stat.executeUpdate(sql);
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return stat.executeUpdate(sql, autoGeneratedKeys);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return stat.executeUpdate(sql, columnIndexes);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return stat.executeUpdate(sql, columnNames);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return conn;
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return stat.getGeneratedKeys();
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return stat.getResultSet();
	}
	
	
	
	

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return stat.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return stat.unwrap(iface);
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		stat.addBatch(sql);
	}

	@Override
	public void cancel() throws SQLException {
		stat.cancel();
	}

	@Override
	public void clearBatch() throws SQLException {
		stat.clearBatch();
	}

	@Override
	public void clearWarnings() throws SQLException {
		stat.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		stat.close();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return stat.getFetchDirection();
	}

	@Override
	public int getFetchSize() throws SQLException {
		return stat.getFetchSize();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return stat.getMaxFieldSize();
	}

	@Override
	public int getMaxRows() throws SQLException {
		return stat.getMaxRows();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return stat.getMoreResults();
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return stat.getMoreResults(current);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return stat.getQueryTimeout();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return stat.getResultSetConcurrency();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return stat.getResultSetHoldability();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return stat.getResultSetType();
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return stat.getUpdateCount();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return stat.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return stat.isClosed();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return stat.isPoolable();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		stat.setCursorName(name);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		stat.setEscapeProcessing(enable);
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		stat.setFetchDirection(direction);
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		stat.setFetchSize(rows);
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		stat.setMaxFieldSize(max);
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		stat.setMaxRows(max);
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		stat.setPoolable(poolable);
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		stat.setQueryTimeout(seconds);
	}

	public void closeOnCompletion() throws SQLException {
		// used reflection to make sure this work with Java 5 and 6
		try {
			stat.getClass().getMethod("closeOnCompletion", new Class[0]).invoke(stat, new Object[0]);
		}
		catch (Throwable t) {
			if(t instanceof InvocationTargetException && ((InvocationTargetException)t).getTargetException() instanceof SQLException)
				throw (SQLException)((InvocationTargetException)t).getTargetException();
			throw new PageRuntimeException(Caster.toPageException(t));
		}
	}

	public boolean isCloseOnCompletion() throws SQLException {
		// used reflection to make sure this work with Java 5 and 6
		try {
			return Caster.toBooleanValue(stat.getClass().getMethod("isCloseOnCompletion", new Class[0]).invoke(stat, new Object[0]));
		}
		catch (Throwable t) {
			if(t instanceof InvocationTargetException && ((InvocationTargetException)t).getTargetException() instanceof SQLException)
				throw (SQLException)((InvocationTargetException)t).getTargetException();
			throw new PageRuntimeException(Caster.toPageException(t));
		}
	}
}
