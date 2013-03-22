package railo.commons.io.res.type.datasource;

import java.io.InputStream;
import java.sql.SQLException;

import railo.commons.io.res.type.datasource.core.Core;
import railo.runtime.db.DatasourceConnection;

public class DataWriter extends Thread {

	private Core core;
	private DatasourceConnection dc;
	private String prefix;
	private Attr attr;
	private InputStream is;
	private SQLException e;
	private boolean append;

	private DatasourceResourceProvider drp;

	public DataWriter(Core core, DatasourceConnection dc, String prefix, Attr attr, InputStream is, DatasourceResourceProvider drp, boolean append) {
		this.core=core;
		this.dc=dc;
		this.prefix=prefix;
		this.attr=attr;
		this.is=is;
		this.drp=drp;
		this.append=append;
	}

	public void run(){
		try {
			core.write(dc, prefix, attr, is,append);
			drp.release(dc);
	    	//manager.releaseConnection(connId,dc);
		} 
		catch (SQLException e) {
			this.e=e;
		}
	}
	


	public SQLException getException() {
		return e;
	}
}
