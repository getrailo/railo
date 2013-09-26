package railo.runtime.orm.hibernate;

import railo.runtime.Component;
import railo.runtime.orm.ORMException;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public class HibernateORMException extends ORMException {

	private static final long serialVersionUID = -6156812531890987771L;

	public HibernateORMException(SessionFactoryData data, Component cfc, String msg, String detail) {
		super(null,cfc,msg,detail);
		if(data!=null)setAddional(data);
	}
	public HibernateORMException(SessionFactoryData data, Component cfc, Throwable t) {
		super(null,cfc,t);
		if(data!=null)setAddional(data);
	}

	private void setAddional(SessionFactoryData data) {
		String[] names = data.getEntityNames();
		setAdditional(KeyConstants._Entities, ListUtil.arrayToList(names, ", "));
		setAddional(data.getDataSource(),this);
	}
}
