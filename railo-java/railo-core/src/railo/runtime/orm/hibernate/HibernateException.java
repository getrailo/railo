package railo.runtime.orm.hibernate;

import railo.runtime.Component;
import railo.runtime.orm.ORMException;

public class HibernateException extends ORMException {


	public HibernateException(HibernateORMEngine engine, Component cfc,String message) {
		super(engine,cfc, message);
	}
	
	public HibernateException(HibernateORMEngine engine, Component cfc,String message, String detail) {
		super(engine,cfc, message, detail);
		
	}
	public HibernateException(HibernateORMEngine engine, String message) {
		super(engine, message);
	}
	
	public HibernateException(HibernateORMEngine engine, String message, String detail) {
		super(engine, message, detail);
		
	}
}
