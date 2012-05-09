package railo.runtime.type.scope.storage.db;

import java.util.HashSet;
import java.util.Set;

import railo.runtime.config.Config;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.KeyConstants;

public abstract class SQLExecutorSupport implements SQLExecutor {
	
	protected static final  Set<Collection.Key> ignoreSet=new HashSet<Collection.Key>();
	static {
		ignoreSet.add(KeyConstants._cfid);
		ignoreSet.add(KeyConstants._cftoken);
		ignoreSet.add(KeyConstants._urltoken);
	}

	
	protected static String now(Config config) {
		return Caster.toString(new DateTimeImpl(config).getTime());
	}

	protected static String createExpires(Config config,long timespan) {
		return Caster.toString(timespan+new DateTimeImpl(config).getTime());
	}

}
