package railo.runtime.orm.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;

import railo.runtime.orm.ORMTransaction;

public class HibernateORMTransaction implements ORMTransaction {
	
	private Transaction trans;
	private Session session;
	private boolean doRollback;

	public HibernateORMTransaction(Session session){
		this.session=session;
		//this.trans=session.getTransaction();
	}

	/**
	 * @see railo.runtime.orm.ORMTransaction#begin()
	 */
	public void begin() {
		session.flush();
		
		//print.err("begin:"+session.hashCode());
		trans=session.beginTransaction();
		
	}

	/**
	 * @see railo.runtime.orm.ORMTransaction#commit()
	 */
	public void commit() {
		/*if(!trans.isActive()) return;
		print.err("commit:"+session.hashCode());
		trans.commit();*/
	}

	/**
	 * @see railo.runtime.orm.ORMTransaction#rollback()
	 */
	public void rollback() {
		doRollback=true;
		/*if(!trans.isActive()) return;
		print.err("rollback:"+session.hashCode());
		trans.rollback();*/
	}

	/**
	 * @see railo.runtime.orm.ORMTransaction#end()
	 */
	public void end() {
		if(doRollback){
			trans.rollback();
			session.clear();
		}
		else{
			trans.commit();
			session.flush();
		}
		//print.err("end:"+doRollback);
		
	}


}
