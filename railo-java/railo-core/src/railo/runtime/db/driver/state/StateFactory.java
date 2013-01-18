package railo.runtime.db.driver.state;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

import railo.runtime.db.driver.CallableStatementProxy;
import railo.runtime.db.driver.ConnectionProxy;
import railo.runtime.db.driver.Factory;
import railo.runtime.db.driver.PreparedStatementProxy;
import railo.runtime.db.driver.StatementProxy;

public class StateFactory implements Factory {

	@Override
	public StatementProxy createStatementProxy(ConnectionProxy conn, Statement stat) {
		return new StateStatement(conn,stat);
	}

	@Override
	public PreparedStatementProxy createPreparedStatementProxy(ConnectionProxy conn, PreparedStatement stat, String sql) {
		return new StatePreparedStatement(conn, stat,sql);
	}

	@Override
	public CallableStatementProxy createCallableStatementProxy(ConnectionProxy conn, CallableStatement stat, String sql) {
		return new StateCallableStatement(conn, stat,sql);
	}

}
