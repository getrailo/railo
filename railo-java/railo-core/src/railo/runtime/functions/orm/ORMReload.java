package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMConfiguration;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class ORMReload {
	public static String call(PageContext pc) throws PageException {
		
		// flush and close session
		ORMSession session = ORMUtil.getSession(pc,false);
		if(session!=null) {// MUST do the same with all sesson using the same engine
			ORMConfiguration config = session.getEngine().getConfiguration(pc);
			if(config.autoManageSession()) {
				session.flush(pc);
				session.close(pc);
			}
		}
		pc.getApplicationContext().reinitORM(pc);
		ORMUtil.resetEngine(pc,true);
		return null;
	}
}
