package railo.runtime.orm.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

import railo.runtime.orm.ORMTransaction;

public class HibernateORMTransaction implements ORMTransaction {
	
	private Transaction trans;
	private Session session;
	private boolean doRollback;
	private boolean autoManage;

	public HibernateORMTransaction(Session session, boolean autoManage){
		this.session=session;
		this.autoManage=autoManage;
	}

	/**
	 * @see railo.runtime.orm.ORMTransaction#begin()
	 */
	public void begin() {
		if(autoManage)session.flush();
		trans=session.beginTransaction();
		
	}

	/**
	 * @see railo.runtime.orm.ORMTransaction#commit()
	 */
	public void commit() {
		// do nothing
	}

	/**
	 * @see railo.runtime.orm.ORMTransaction#rollback()
	 */
	public void rollback() {
		doRollback=true;
	}

	/**
	 * @see railo.runtime.orm.ORMTransaction#end()
	 */
	public void end() {
		if(doRollback){
			trans.rollback();
			if(autoManage)session.clear();
		}
		else{
			trans.commit();
			session.flush();
		}
	}
}
