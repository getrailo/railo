package railo.runtime.db.driver.state;

import java.sql.CallableStatement;

import railo.runtime.db.driver.CallableStatementProxy;
import railo.runtime.db.driver.ConnectionProxy;

public class StateCallableStatement extends CallableStatementProxy {

	public StateCallableStatement(ConnectionProxy conn,CallableStatement prepareCall, String sql) {
		super(conn, prepareCall,sql);
	}

}
