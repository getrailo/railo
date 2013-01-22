package railo.commons.io.log;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.CFTypes;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * Datasource output logger
 * TODO impl
 */
public final class LogDataSource implements Log {
 
    private static final SQL SELECT = new SQLImpl("select for checking if table exists");//TODO
    private static final String INSERT = "insert into cflog(application,message,created) values(?,?,?)";//TODO
    private static final SQL CREATE = new SQLImpl("create table");//TODO
	//private ConfigImpl config;
    private DataSource datasource;
    private String username;
    private String password;
    //private String table;
    private int logLevel;
	private DatasourceConnectionPool pool;
	private LogConsole console;

    /**
     * Constructor of the class
     * @param config
     * @param datasource 
     * @param username 
     * @param password 
     * @param table 
     * @throws PageException 
     */
    public LogDataSource(PageContext pc,int logLevel,ConfigImpl config, DataSource datasource, String username, String password, String table) {
        this.logLevel=logLevel;
        //this.config=config;
        this.datasource=datasource;
        this.username=username;
        this.password=password;
        //this.table=table;
        pool = config.getDatasourceConnectionPool();
        console=LogConsole.getInstance(config,logLevel);
        
        DatasourceConnection dc=null;
    	try {
			dc = pool.getDatasourceConnection(pc,datasource, username, password);
			try {
				new QueryImpl(pc,dc,SELECT,-1,-1,-1,"query");
			}
			catch (PageException e) {
				new QueryImpl(pc,dc,CREATE,-1,-1,-1,"query");
			}
		} 
    	catch (PageException e) {
			config.getErrWriter();
		}
		finally{
			if(pool!=null)pool.releaseDatasourceConnection(dc);
		}
        
        
    }


    @Override
    public void log(int level, String application, String message) {
    	DatasourceConnection dc=null;
    	try {
			dc = pool.getDatasourceConnection(ThreadLocalPageContext.get(),datasource, username, password);
			SQLImpl sql = new SQLImpl(INSERT);
			sql.addItems(new SQLItemImpl(application,CFTypes.VARCHAR));
			sql.addItems(new SQLItemImpl(message,CFTypes.VARCHAR));
			sql.addItems(new SQLItemImpl(new DateTimeImpl(),CFTypes.DATE));
			new QueryImpl(ThreadLocalPageContext.get(),dc,sql,-1,-1,-1,"query");
		} 
    	catch (PageException e) {
			console.log(level, application, message);
		}
		finally{
			if(pool!=null)pool.releaseDatasourceConnection(dc);
		}
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setLogLevel(int level) {
        this.logLevel=level;
    }


    @Override
    public void debug(String application, String message) {
    	log(LEVEL_DEBUG, application, message);
    }

    @Override
    public void error(String application, String message) {
    	log(LEVEL_ERROR, application, message);
    }

    @Override
    public void fatal(String application, String message) {
    	log(LEVEL_FATAL, application, message);
    }

    @Override
    public void info(String application, String message) {
    	log(LEVEL_INFO, application, message);
    }

    @Override
    public void warn(String application, String message) {
    	log(LEVEL_WARN, application, message);
    }
}
