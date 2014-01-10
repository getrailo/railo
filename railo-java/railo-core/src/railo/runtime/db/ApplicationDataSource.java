package railo.runtime.db;

import java.util.TimeZone;

import railo.commons.lang.ClassException;
import railo.runtime.config.ConfigWebFactory;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.Struct;

public class ApplicationDataSource extends DataSourceSupport {

	private String connStr;

	private ApplicationDataSource(String name, String className, String connStr, String username, String password,
			boolean blob, boolean clob, int connectionLimit, int connectionTimeout, long metaCacheTimeout, TimeZone timezone, int allow, boolean storage, boolean readOnly) throws ClassException {
		this(name, toClass(className), connStr, username, password,
				blob, clob, connectionLimit, connectionTimeout, metaCacheTimeout, timezone, allow, storage, readOnly);
	}

	private ApplicationDataSource(String name, Class clazz, String connStr, String username, String password,
			boolean blob, boolean clob, int connectionLimit, int connectionTimeout, long metaCacheTimeout, TimeZone timezone, int allow, boolean storage, boolean readOnly) {
		super(name, clazz,username,ConfigWebFactory.decrypt(password),
				blob,clob,connectionLimit, connectionTimeout, metaCacheTimeout, timezone, allow<0?ALLOW_ALL:allow, storage, readOnly);
		
		this.connStr = connStr;
	}
	

	public static DataSource getInstance(String name, String className, String connStr, String username, String password,
			boolean blob, boolean clob, int connectionLimit, int connectionTimeout, long metaCacheTimeout, TimeZone timezone, int allow, boolean storage, boolean readOnly) throws ClassException {
		
		return new ApplicationDataSource(name, className, connStr, username, password, blob, clob, connectionLimit, connectionTimeout, metaCacheTimeout, timezone, allow, storage, readOnly);
	}

	@Override
	public String getDsnOriginal() {
		throw exp();
	}

	@Override
	public String getConnectionString() {
		throw exp();
	}

	@Override
	public String getDsnTranslated() {
		return getConnectionStringTranslated();
	}

	@Override
	public String getConnectionStringTranslated() {
		return connStr;
	}

	@Override
	public String getDatabase() {
		throw new PageRuntimeException(new ApplicationException("Datasource defined in the Application.cfc has no name."));
	}

	@Override
	public int getPort() {
		throw exp();
	}

	@Override
	public String getHost() {
		throw exp();
	}

	@Override
	public DataSource cloneReadOnly() {
		return new ApplicationDataSource(getName(), getClazz(), connStr, getUsername(), getPassword(),
				isBlob(), isClob(), getConnectionLimit(), getConnectionTimeout(), getMetaCacheTimeout(), getTimeZone(), allow, isStorage(), isReadOnly());
	}

	@Override
	public String getCustomValue(String key) {
		throw exp();
	}

	@Override
	public String[] getCustomNames() {
		throw exp();
	}

	@Override
	public Struct getCustoms() {
		throw exp();
	}

	@Override
	public boolean validate() {
		throw exp();
	}


	private PageRuntimeException exp() {
		//return new MethodNotSupportedException();
		throw new PageRuntimeException(new ApplicationException("method not supported"));
	}
}
