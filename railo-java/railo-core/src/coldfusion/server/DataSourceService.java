package coldfusion.server;

import java.io.IOException;
import java.sql.SQLException;

import railo.runtime.exp.SecurityException;
import railo.runtime.type.Array;
import railo.runtime.type.Struct;
import coldfusion.sql.DataSource;


public interface DataSourceService extends Service {
	
	/*
	TODO impl
	public abstract Query executeQuery(Connection arg0, String arg1,
			ParameterList arg2, Integer arg3, Integer arg4, Integer arg5,
			int[] arg6, int arg7, int arg8, boolean arg9, boolean arg10)
			throws SQLException;

	public abstract Query executeQuery(Connection arg0, String arg1,
			ParameterList arg2, Integer arg3, Integer arg4, Integer arg5,
			int[] arg6, String arg7) throws SQLException;

	public abstract Query executeQuery(Connection arg0, String arg1,
			ParameterList arg2, Integer arg3, Integer arg4, Integer arg5,
			int[] arg6, DataSourceDef arg7) throws SQLException;

	public abstract Query executeQuery(Connection arg0, String arg1,
			ParameterList arg2, Integer arg3, Integer arg4, Integer arg5,
			int[] arg6, Object arg7) throws SQLException;

	public abstract Query executeCall(Connection arg0, String arg1,
			ParameterList arg2, int[] arg3, Integer arg4, Integer arg5,
			int[] arg6, int arg7, int arg8, boolean arg9, boolean arg10)
			throws SQLException;

	public abstract Query executeCall(Connection arg0, String arg1,
			ParameterList arg2, int[] arg3, Integer arg4, Integer arg5,
			int[] arg6, String arg7) throws SQLException;

	public abstract Query executeCall(Connection arg0, String arg1,
			ParameterList arg2, int[] arg3, Integer arg4, Integer arg5,
			int[] arg6, DataSourceDef arg7) throws SQLException;

	public abstract Query executeCall(Connection arg0, String arg1,
			ParameterList arg2, int[] arg3, Integer arg4, Integer arg5,
			int[] arg6, Object arg7) throws SQLException;
*/
	public abstract Struct getDatasources() throws SecurityException;

	public abstract Struct getDrivers() throws ServiceException, SecurityException;

	public abstract Array getNames() throws SecurityException;

	public abstract Struct getDefaults();

	public abstract Number getMaxQueryCount();

	public abstract void setMaxQueryCount(Number arg0);

	public abstract String encryptPassword(String arg0);

	public abstract boolean verifyDatasource(String arg0) throws SQLException, SecurityException;

	public abstract DataSource getDatasource(String arg0) throws SQLException, SecurityException;

	public abstract String getDbdir();

	public abstract Object getCachedQuery(String arg0);

	public abstract void setCachedQuery(String arg0, Object arg1);

	public abstract void purgeQueryCache() throws IOException;

	public abstract boolean disableConnection(String arg0);

	public abstract boolean isJadoZoomLoaded();

	public abstract void removeDatasource(String arg0) throws SQLException, SecurityException;

}