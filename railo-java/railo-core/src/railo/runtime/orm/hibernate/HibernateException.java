package railo.runtime.orm.hibernate;

import railo.runtime.orm.ORMException;

public class HibernateException extends ORMException {


	public HibernateException(HibernateORMEngine engine, String message) {
		super(engine, message);
	}
	
	public HibernateException(HibernateORMEngine engine, String message, String detail) {
		super(engine, message, detail);
		
	}

	/*public HibernateException(ORMEngine engine, MappingException me) {
		super(engine, me.getMessage());
		setStackTrace(me.getStackTrace());
		setAdditional("Cause", me.getClass().getName());
	}*/

}
