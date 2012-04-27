package railo.runtime.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import railo.commons.db.DBUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefInteger;
import railo.commons.lang.types.RefIntegerImpl;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ArrayUtil;

public class DatasourceConnectionPool {

	private Map<String,DCStack> dcs=new HashMap<String,DCStack>();
	private Map<String,RefInteger> counter=new HashMap<String,RefInteger>();
	
	public DatasourceConnection getDatasourceConnection(PageContext pc,DataSource datasource, String user, String pass) throws PageException {
		pc=ThreadLocalPageContext.get(pc);
		if(StringUtil.isEmpty(user)) {
            user=datasource.getUsername();
            pass=datasource.getPassword();
        }
        if(pass==null)pass="";
		
		
		// get stack
		DCStack stack=getDCStack(datasource,user,pass);
		
		
		// max connection
		int max=datasource.getConnectionLimit();
		synchronized (stack) {
			while(max!=-1 && max<=_size(datasource)) {
				try {
					//stack.inc();
					stack.wait(10000L);
					
				} 
				catch (InterruptedException e) {
					throw Caster.toPageException(e);
				}
				
			}
			if(pc!=null){
				while(!stack.isEmpty()) {
					DatasourceConnectionImpl dc=(DatasourceConnectionImpl) stack.get(pc);
						if(dc!=null && isValid(dc,Boolean.TRUE)){
							_inc(datasource);
							return dc.using();
						}
					
				}
			}
			_inc(datasource);
			return loadDatasourceConnection(datasource, user, pass).using();
		}
	}

	private DatasourceConnectionImpl loadDatasourceConnection(DataSource ds, String user, String pass) throws DatabaseException  {
        Connection conn=null;
        String dsn = ds.getDsnTranslated();
        try {
        	conn = DBUtil.getConnection(dsn, user, pass);
        	conn.setAutoCommit(true);
        } 
        catch (SQLException e) {
        	throw new DatabaseException("can't connect to datasource ["+ds.getName()+"]",e,null,null);
        }
		//print.err("create connection");
        return new DatasourceConnectionImpl(conn,ds,user,pass);
    }
	
	public void releaseDatasourceConnection(DatasourceConnection dc) {
		if(dc==null) return;
		
		DCStack stack=getDCStack(dc.getDatasource(), dc.getUsername(), dc.getPassword());
		synchronized (stack) {
			stack.add((DatasourceConnectionPro)dc);
			int max = dc.getDatasource().getConnectionLimit();

			if(max!=-1) {
				_dec(dc.getDatasource());
				stack.notify();
				 
			}
			else _dec(dc.getDatasource());
		}
	}

	public void clear() {
		int size=0;
		
		// remove all timed out conns
		try{
			Object[] arr = dcs.entrySet().toArray();
			if(ArrayUtil.isEmpty(arr)) return;
			for(int i=0;i<arr.length;i++) {
				DCStack conns=(DCStack) ((Map.Entry) arr[i]).getValue();
				if(conns!=null)conns.clear();
				size+=conns.size();
			}
		}
		catch(Throwable t){}
	}

	public void remove(String datasource) {
		Object[] arr = dcs.keySet().toArray();
		String key;
        for(int i=0;i<arr.length;i++) {
        	key=(String) arr[i];
        	if(key.startsWith(datasource.toLowerCase())) {
				DCStack conns=(DCStack) dcs.get(key);
				conns.clear();
        	}
		}
        
        String did = createId(datasource);
		RefInteger ri=(RefInteger) counter.get(did);
		if(ri!=null)ri.setValue(0);
		else counter.put(did,new RefIntegerImpl(0));
        
	}
	

	
	public static boolean isValid(DatasourceConnection dc,Boolean autoCommit) {
		try {
			if(dc.getConnection().isClosed())return false;
		} 
		catch (Throwable t) {return false;}

		try {
			if(((DataSourceImpl)dc.getDatasource()).validate() && !DataSourceUtil.isValid(dc,1000))return false;
		} 
		catch (Throwable t) {} // not all driver support this, because of that we ignore a error here, also protect from java 5
		
		
		try {
			if(autoCommit!=null) dc.getConnection().setAutoCommit(autoCommit.booleanValue());
		} 
		catch (Throwable t) {return false;}
		
		
		return true;
	}


	private DCStack getDCStack(DataSource datasource, String user, String pass) {
		String id = createId(datasource,user,pass);
		
		DCStack stack=(DCStack)dcs.get(id);
		
		if(stack==null){
			dcs.put(id, stack=new DCStack());
		}
		return stack;
	}

	private void _inc(DataSource datasource) {
		_getCounter(datasource.getName()).plus(1);
	}
	private void _dec(DataSource datasource) {
		_getCounter(datasource.getName()).minus(1);
	}
	private int _size(DataSource datasource) {
		return _getCounter(datasource.getName()).toInt();
	}

	private RefInteger _getCounter(String datasource) {
		String did = createId(datasource);
		RefInteger ri=(RefInteger) counter.get(did);
		if(ri==null) {
			counter.put(did,ri=new RefIntegerImpl(0));
		}
		return ri;
	}

	public static String createId(DataSource datasource, String user, String pass) {
		return datasource.getName().toLowerCase()+user+pass;
	}
	private static String createId(String datasource) {
		return datasource.toLowerCase();
	}
}
