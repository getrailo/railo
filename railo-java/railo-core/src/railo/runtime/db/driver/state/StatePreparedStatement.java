package railo.runtime.db.driver.state;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import railo.runtime.PageContextImpl;
import railo.runtime.db.debug.DebugQuery;
import railo.runtime.db.driver.ConnectionProxy;
import railo.runtime.db.driver.PreparedStatementProxy;
import railo.runtime.engine.ThreadLocalPageContext;

public class StatePreparedStatement extends PreparedStatementProxy {

	public StatePreparedStatement(ConnectionProxy conn, PreparedStatement stat, String sql) {
		super(conn, stat,sql);
	}
	
	@Override
	public boolean execute() throws SQLException {
		PageContextImpl pc = (PageContextImpl) ThreadLocalPageContext.get();
		if(pc==null) return stat.execute();
		try {
			setActiveStatement(pc,stat,sql);
			 return stat.execute();
		}
		finally {
			pc.releaseActiveQuery();
		}
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		PageContextImpl pc = (PageContextImpl) ThreadLocalPageContext.get();
		if(pc==null) return stat.executeQuery();
		try {
			setActiveStatement(pc,stat,sql);
			 return stat.executeQuery();
		}
		finally {
			pc.releaseActiveQuery();
		}
	}

	@Override
	public int executeUpdate() throws SQLException {
		PageContextImpl pc = (PageContextImpl) ThreadLocalPageContext.get();
		if(pc==null) return stat.executeUpdate();
		try {
			setActiveStatement(pc,stat,sql);
			 return stat.executeUpdate();
		}
		finally {
			pc.releaseActiveQuery();
		}
	}

	protected void setActiveStatement(PageContextImpl pc,Statement stat, String sql) {
		pc.setActiveQuery(new DebugQuery(sql,System.currentTimeMillis()));
	}
}
