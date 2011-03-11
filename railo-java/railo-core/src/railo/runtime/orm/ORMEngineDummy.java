package railo.runtime.orm;

import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.exp.PageException;

public class ORMEngineDummy implements ORMEngine {

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ORMSession createSession(PageContext pc) throws PageException {
		return null;
	}

	public Object getSessionFactory(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(PageContext pc) throws PageException {
		// TODO Auto-generated method stub

	}

	public ORMConfiguration getConfiguration(PageContext pc) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataSource getDataSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getEntityNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean reload(PageContext pc, boolean force) throws PageException {
		// TODO Auto-generated method stub
		return false;
	}

}
