package railo.runtime.type.scope.storage.db;

import railo.runtime.db.DatasourceConnection;

public class SQLExecutionFactory {
	
	private static final SQLExecutor ANSI92 = new Ansi92(); 

	public static SQLExecutor getInstance(DatasourceConnection dc) {
		return ANSI92;
	}
	
}
