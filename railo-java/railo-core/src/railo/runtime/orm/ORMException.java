package railo.runtime.orm;


import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.op.Caster;
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
		setAddional(engine,this);
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
		setAddional(engine, this);
	}

	public static PageException toPageException(ORMEngine engine,Throwable t) {
		Throwable c = t.getCause();
		if(c!=null)t=c;
		PageException pe = Caster.toPageException(t);
		setAddional(engine, pe);
		return pe;
	}
	
	public static void setAddional(ORMEngine engine,PageException pe) {
		String[] names = engine.getEntityNames();
		
		if(pe instanceof PageExceptionImpl){
			PageExceptionImpl pei = (PageExceptionImpl)pe;
			pei.setAdditional("Entities", List.arrayToList(names, ", "));
			pei.setAdditional("Datasource", engine.getDataSource().getName());
		}
	}

	public ORMException(Throwable t) {
		super(t.getMessage());
	}

}
