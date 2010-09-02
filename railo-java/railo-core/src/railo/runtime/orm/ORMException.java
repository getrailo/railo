package railo.runtime.orm;


import railo.runtime.exp.ApplicationException;
import railo.runtime.type.List;

public class ORMException extends ApplicationException {

	/**
	 * Constructor of the class
	 * @param message
	 */
	public ORMException(String message) {
		super(message);
	}
	
	public ORMException(ORMEngine engine,String message) {
		super(message);
		setAddional(engine);
	}

	/**
	 * Constructor of the class
	 * @param message
	 * @param detail
	 */
	public ORMException(String message, String detail) {
		super(message, detail);
	}
	

	public ORMException(ORMEngine engine,String message, String detail) {
		super(message, detail);
		setAddional(engine);
	}


	private void setAddional(ORMEngine engine) {
		String[] names = engine.getEntityNames();
		setAdditional("Entities", List.arrayToList(names, ", "));
		setAdditional("Datasource", engine.getDataSource().getName());
		
	}

	public ORMException(Throwable t) {
		super(t.getMessage());
	}

}
