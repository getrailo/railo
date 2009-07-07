package railo.runtime.services;


import java.util.Map;

import railo.runtime.db.DataSource;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import coldfusion.sql.DataSourceDef;

public class DatSourceDefImpl implements DataSourceDef {

	private DataSource ds;

	public DatSourceDefImpl(DataSource ds) {
		this.ds=ds;
	}

	public Object get(Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct getAllowedSQL() {
		Struct allow=new StructImpl();
		allow.setEL("alter", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_ALTER)));
		allow.setEL("create", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_CREATE)));
		allow.setEL("delete", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_DELETE)));
		allow.setEL("drop", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_DROP)));
		allow.setEL("grant", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_GRANT)));
		allow.setEL("insert", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_INSERT)));
		allow.setEL("revoke", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_REVOKE)));
		allow.setEL("select", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_SELECT)));
		allow.setEL("storedproc", Caster.toBoolean(true));// TODO
		allow.setEL("update", Caster.toBoolean(ds.hasAllow(DataSource.ALLOW_UPDATE)));
		return allow;
	}

	/**
	 * @see coldfusion.sql.DataSourceDef#getClassName()
	 */
	public String getClassName() {
		return ds.getClazz().getName();
	}

	/**
	 * @see coldfusion.sql.DataSourceDef#getDatabase()
	 */
	public String getDatabase() {
		return ds.getDatabase();
	}

	/**
	 * @see coldfusion.sql.DataSourceDef#getDesc()
	 */
	public String getDesc() {
		return "";
	}

	public String getDriver() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDsn() {
		return ds.getDsnTranslated();
	}

	public Struct getExtraData() {
		Struct rtn=new StructImpl();
		Struct connprop=new StructImpl();
		String[] names = ds.getCustomNames();
		rtn.setEL("connectionprops", connprop);
		for(int i=0;i<names.length;i++) {
			connprop.setEL(names[i], ds.getCustomValue(names[i]));
		}
		rtn.setEL("maxpooledstatements",new Double(1000) );
		rtn.setEL("sid","");
		rtn.setEL("timestampasstring", Boolean.FALSE);
		rtn.setEL("useTrustedConnection", Boolean.FALSE);
		rtn.setEL("datasource",ds.getName() );
		rtn.setEL("_port",new Double(ds.getPort()) );
		rtn.setEL("port",new Double(ds.getPort()) );
		rtn.setEL("_logintimeout",new Double(30) );
		rtn.setEL("args", "");
		rtn.setEL("databaseFile", "");
		rtn.setEL("defaultpassword","" );
		rtn.setEL("defaultusername", "");
		rtn.setEL("host",ds.getHost() );
		rtn.setEL("maxBufferSize",new Double(0) );
		rtn.setEL("pagetimeout",new Double(0) );
		rtn.setEL("selectMethod","direct" );
		rtn.setEL("sendStringParamterAsUnicode",Boolean.TRUE );
		rtn.setEL("systemDatabaseFile", "");
		
		
		return rtn;
	}

	/**
	 *
	 * @see coldfusion.sql.DataSourceDef#getHost()
	 */
	public String getHost() {
		return ds.getHost();
	}

	public String getIfxSrv() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getJNDIName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see coldfusion.sql.DataSourceDef#getJndiName()
	 */
	public String getJndiName() {
		return getJNDIName();
	}

	public Struct getJndienv() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getLoginTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see coldfusion.sql.DataSourceDef#getLogintimeout()
	 */
	public int getLogintimeout() {
		return getLoginTimeout();
	}

	public int getMaxBlobSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxClobSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxConnections() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxPooledStatements() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxconnections() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see coldfusion.sql.DataSourceDef#getPort()
	 */
	public int getPort() {
		return ds.getPort();
	}

	public String getSelectMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSid() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getStrPrmUni() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see coldfusion.sql.DataSourceDef#getUrl()
	 */
	public String getUrl() {
		return ds.getDsnTranslated();
	}

	/**
	 *
	 * @see coldfusion.sql.DataSourceDef#getUsername()
	 */
	public String getUsername() {
		return ds.getUsername();
	}

	/**
	 *
	 * @see coldfusion.sql.DataSourceDef#getVendor()
	 */
	public String getVendor() {
		return ds.getPassword();
	}

	/**
	 *
	 * @see coldfusion.sql.DataSourceDef#isBlobEnabled()
	 */
	public boolean isBlobEnabled() {
		return ds.isBlob();
	}

	/**
	 * @see coldfusion.sql.DataSourceDef#isClobEnabled()
	 */
	public boolean isClobEnabled() {
		return ds.isClob();
	}

	public boolean isConnectionEnabled() {
		return true;
	}

	public boolean isDynamic() {
		return false;
	}

	public boolean isPooling() {
		return true;
	}

	public boolean isRemoveOnPageEnd() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSQLRestricted() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAllowedSQL(Struct arg1) {
		// TODO Auto-generated method stub

	}

	public void setBlobEnabled(boolean arg1) {
		// TODO Auto-generated method stub

	}

	public void setClassName(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setClobEnabled(boolean arg1) {
		// TODO Auto-generated method stub

	}

	public void setConnectionEnabled(boolean arg1) {
		// TODO Auto-generated method stub

	}

	public void setDatabase(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setDesc(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setDriver(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setDsn(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setDynamic(boolean arg1) {
		// TODO Auto-generated method stub

	}

	public void setExtraData(Struct arg1) {
		// TODO Auto-generated method stub

	}

	public void setHost(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setIfxSrv(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setInterval(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setJNDIName(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setJndiName(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setJndienv(Struct arg1) {
		// TODO Auto-generated method stub

	}

	public void setLoginTimeout(Object arg1) {
		// TODO Auto-generated method stub

	}

	public void setLogintimeout(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setMap(Map arg1) {
		// TODO Auto-generated method stub

	}

	public void setMaxBlobSize(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setMaxClobSize(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setMaxConnections(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setMaxConnections(Object arg1) {
		// TODO Auto-generated method stub

	}

	public void setMaxPooledStatements(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setPassword(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setPooling(boolean arg1) {
		// TODO Auto-generated method stub

	}

	public void setPort(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setPort(Object arg1) {
		// TODO Auto-generated method stub

	}

	public void setRemoveOnPageEnd(boolean arg1) {
		// TODO Auto-generated method stub

	}

	public void setSelectMethod(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setSid(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setStrPrmUni(boolean arg1) {
		// TODO Auto-generated method stub

	}

	public void setStrPrmUni(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setTimeout(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setType(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setType(int arg1) {
		// TODO Auto-generated method stub

	}

	public void setUrl(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setUsername(String arg1) {
		// TODO Auto-generated method stub

	}

	public void setVendor(String arg1) {
		// TODO Auto-generated method stub

	}

}
