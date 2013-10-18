package railo.runtime.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface ORMEngine {
	
	// (CFML Compatibility Mode) is not so strict in input interpretation
	public static final int MODE_LAZY = 0;
	// more strict in input interpretation
	public static final int MODE_STRICT = 1;
	

	



	
	/**
	 * @return returns the label of the ORM Engine
	 */
	public String getLabel();


	
	public int getMode();
	
	
	public ORMSession createSession(PageContext pc) throws PageException;
	public Object getSessionFactory(PageContext pc) throws PageException;

	public void init(PageContext pc) throws PageException;

	public ORMConfiguration getConfiguration(PageContext pc);

	
	public boolean reload(PageContext pc, boolean force) throws PageException;


}
