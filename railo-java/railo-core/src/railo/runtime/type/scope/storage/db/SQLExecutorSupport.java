package railo.runtime.type.scope.storage.db;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import railo.commons.io.log.Log;
import railo.runtime.config.Config;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.dt.DateTimeImpl;

public abstract class SQLExecutorSupport implements SQLExecutor {
	
	protected static final  Set<String> ignoreSet=new HashSet<String>();
	static {
		ignoreSet.add("cfid");
		ignoreSet.add("cftoken");
		ignoreSet.add("urltoken");
	}

	
	protected static String now(Config config) {
		return Caster.toString(new DateTimeImpl(config).getTime());
	}

	protected static String createExpires(Config config,long timespan) {
		return Caster.toString(timespan+new DateTimeImpl(config).getTime());
	}

}
