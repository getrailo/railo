package railo.runtime.db.driver;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;

public interface Factory {

	public StatementProxy createStatementProxy(ConnectionProxy connectionProxy, Statement createStatement);
	public PreparedStatementProxy createPreparedStatementProxy(ConnectionProxy connectionProxy, PreparedStatement prepareStatement, String sql);
	public CallableStatementProxy createCallableStatementProxy(ConnectionProxy connectionProxy, CallableStatement prepareCall, String sql);

}
