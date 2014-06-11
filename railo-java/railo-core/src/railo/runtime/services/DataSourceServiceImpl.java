package railo.runtime.services;

import java.io.IOException;
import java.sql.SQLException;

import railo.commons.io.res.Resource;
import railo.commons.io.res.filter.ExtensionResourceFilter;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.ExceptionUtil;
import railo.runtime.PageContext;
import railo.runtime.cache.tag.CacheHandlerFactory;
import railo.runtime.config.ConfigWebAdmin;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.db.DataSourceManager;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.exp.SecurityException;
import railo.runtime.functions.list.ListFirst;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;
import coldfusion.server.DataSourceService;
import coldfusion.server.ServiceException;
import coldfusion.sql.DataSource;

public class DataSourceServiceImpl extends ServiceSupport implements DataSourceService {

	private Number maxQueryCount=new Double(500);
	


	public Struct getDefaults() {
		Struct sct=new StructImpl();
		sct.setEL("alter",Boolean.TRUE);
		sct.setEL("blob_buffer",new Double(64000));
		sct.setEL("buffer",new Double(64000));
		sct.setEL("create",Boolean.TRUE);
		sct.setEL("delete",Boolean.TRUE);
		sct.setEL("disable",Boolean.FALSE);
		sct.setEL("disable_blob",Boolean.TRUE);
		sct.setEL("disable_clob",Boolean.TRUE);
		sct.setEL("drop",Boolean.TRUE);
		sct.setEL("grant",Boolean.TRUE);
		sct.setEL("insert",Boolean.TRUE);
		sct.setEL("pooling",Boolean.TRUE);
		sct.setEL("revoke",Boolean.TRUE);
		sct.setEL("select",Boolean.TRUE);
		sct.setEL("storedproc",Boolean.TRUE);
		sct.setEL("update",Boolean.TRUE);
		sct.setEL("",Boolean.TRUE);
		sct.setEL("",Boolean.TRUE);
		sct.setEL("",Boolean.TRUE);
		sct.setEL("interval",new Double(420));
		sct.setEL("login_timeout",new Double(30));
		sct.setEL("timeout",new Double(1200));

		return sct;
	}

	public Number getMaxQueryCount() {
		return maxQueryCount;
	}

	public void setMaxQueryCount(Number maxQueryCount) {
		this.maxQueryCount=maxQueryCount;
	}

	public String encryptPassword(String pass) {
		throw new PageRuntimeException(new ServiceException("method [encryptPassword] is not supported for datasource service"));
		//return pass;
	}

	@Override
	public String getDbdir() {
		Resource db = config().getConfigDir().getRealResource("db");
		if(!db.exists())db.createNewFile();
		return db.getPath();
	}

	@Override
	public Object getCachedQuery(String key) {
		throw new PageRuntimeException(new ServiceException("method [getQueryCache] is not supported for datasource service"));
	}

	@Override
	public void setCachedQuery(String arg0, Object arg1) {
		throw new PageRuntimeException(new ServiceException("method [setQueryCache] is not supported for datasource service"));
	}

	@Override
	public void purgeQueryCache() throws IOException {
		PageContext pc = pc();
		if(pc!=null)
			try {
				ConfigWebUtil.getCacheHandlerFactories(pc.getConfig()).query.clean(pc);
			}
			catch (PageException e) {
				throw ExceptionUtil.toIOException(e);
			}
		//if(pc!=null)pc.getQueryCache().clearUnused(pc);

	}

	@Override
	public boolean disableConnection(String name) {return false;}

	@Override
	public boolean isJadoZoomLoaded() {return false;}



	public Struct getDrivers() throws ServiceException, SecurityException {
		checkReadAccess();
		Struct rtn=new StructImpl();
		Struct driver;
		
		try {
			Resource railoContext = ResourceUtil.toResourceExisting(pc() ,"/railo-context/admin/dbdriver/");
			Resource[] children = railoContext.listResources(new ExtensionResourceFilter("cfc"));
	    	 
	    	String name;
	    	for(int i=0;i<children.length;i++) {
	    		driver=new StructImpl();
	    		name=ListFirst.call(pc(),children[i].getName(),".");
	    		driver.setEL(KeyConstants._name,name);
	    		driver.setEL("handler",children[i].getName());
	    		rtn.setEL(name,driver);
	    	}
		
		
		} catch (ExpressionException e) {
			throw new ServiceException(e.getMessage());
		}
    	
    	
		
		return rtn;
	}
	
	public Struct getDatasources() throws SecurityException {// MUST muss struct of struct zurŸckgeben!!!
		checkReadAccess();
		railo.runtime.db.DataSource[] sources = config().getDataSources();
		Struct rtn=new StructImpl();
		for(int i=0;i<sources.length;i++) {
			rtn.setEL(sources[i].getName(),new DataSourceImpl(sources[i]));
		}
		return rtn;
	}
	
	public Array getNames() throws SecurityException {
		checkReadAccess();
		railo.runtime.db.DataSource[] sources = config().getDataSources();
		Array names=new ArrayImpl();
		for(int i=0;i<sources.length;i++) {
			names.appendEL(sources[i].getName());
		}
		return names;
	}

	public void removeDatasource(String name) throws SQLException, SecurityException {
		checkWriteAccess();
		try {
			ConfigWebAdmin admin = ConfigWebAdmin.newInstance(config(),"");
			admin.removeDataSource(name);
		} catch (Exception e) {
			// ignoriert wenn die db nicht existiert
		}
	}



	public boolean verifyDatasource(String name) throws SQLException, SecurityException {
		checkReadAccess();
		railo.runtime.db.DataSource d = _getDatasource(name);
		PageContext pc = pc();
		DataSourceManager manager = pc.getDataSourceManager();
    	try {
			manager.releaseConnection(pc,manager.getConnection(pc,name, d.getUsername(), d.getPassword()));
			return true;
		} catch (PageException e) {
			return false;
		}
	}
	
	public DataSource getDatasource(String name) throws SQLException, SecurityException {
		return new DataSourceImpl(_getDatasource(name));
	}

	private railo.runtime.db.DataSource _getDatasource(String name) throws SQLException, SecurityException {
		checkReadAccess();
		name=name.trim();
		railo.runtime.db.DataSource[] sources = config().getDataSources();
		for(int i=0;i<sources.length;i++) {
			if(sources[i].getName().equalsIgnoreCase(name))return sources[i];
		}
		throw new SQLException("no datasource with name ["+name+"] found");
	}
}
