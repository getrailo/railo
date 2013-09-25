package railo.runtime.orm;


import railo.runtime.Component;
import railo.runtime.db.DataSource;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageExceptionImpl;
import railo.runtime.op.Caster;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public class ORMException extends ApplicationException {

	private static final long serialVersionUID = 9198067367309355433L;

	public ORMException(Throwable t) {
		super(t.getMessage());
	}
	

	public ORMException(ORMSession session,Component cfc,String message,String detail) {
		super(message);
		if(session!=null)setAddional(session,this);
		if(cfc!=null)setContext(cfc);
	}

	public ORMException(DataSource ds,String message) {
		super(message);
		setAddional(ds,this);
	}
	

	private void setContext(Component cfc) {
		if(cfc!=null && getPageDeep()==0)addContext(cfc.getPageSource(), 1, 1, null);
	}

	public static PageException toPageException(ORMSession session,Throwable t) {
		Throwable c = t.getCause();
		if(c!=null)t=c;
		PageException pe = Caster.toPageException(t);
		setAddional(session, pe);
		return pe;
	}
	public static PageException toPageException(DataSource ds,Throwable t) {
		Throwable c = t.getCause();
		if(c!=null)t=c;
		PageException pe = Caster.toPageException(t);
		setAddional(ds, pe);
		return pe;
	}
	
	public static void setAddional(ORMSession session,PageException pe) {
		String[] names = session.getEntityNames();
		
		if(pe instanceof PageExceptionImpl){
			PageExceptionImpl pei = (PageExceptionImpl)pe;
			pei.setAdditional(KeyConstants._Entities, ListUtil.arrayToList(names, ", "));
			setAddional(session.getDataSource(),pe);
		}
	}
	
	public static void setAddional(DataSource ds,PageException pe) {
		
		if(ds!=null && pe instanceof PageExceptionImpl){
			PageExceptionImpl pei = (PageExceptionImpl)pe;
			String dsn=null;
			if(ds!=null) dsn=ds.getName();
			if(dsn!=null)pei.setAdditional(KeyConstants._Datasource, dsn);
		}
	}

}
