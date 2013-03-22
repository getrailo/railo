package railo.runtime.db.driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import railo.runtime.PageContext;

public interface PreparedStatementPro extends PreparedStatement,StatementPro {
	
	public boolean execute(PageContext pc) throws SQLException;
	public ResultSet executeQuery(PageContext pc) throws SQLException;
	public int executeUpdate(PageContext pc) throws SQLException;
}
