package railo.runtime.db;

import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;


/**
 * 
 */
public final class DataSourceImpl implements Cloneable, DataSource {

    private String dsn;
	private String username;
	private String password;
    private boolean readOnly;
    private Class clazz;
    private String host;
    private String database;
    private int port;
    private String dsnTranslated;
    private int connectionLimit;
    private int connectionTimeout;
    private boolean blob;
    private boolean clob;
    private int allow;
    private Struct custom;
    private String name;
    
	/**
	 * constructor of the class
	 * @param name 
	 * @param clazz 
	 * @param host 
	 * @param dsn
	 * @param database 
	 * @param port 
	 * @param username
	 * @param password
	 * @param connectionLimit 
	 * @param connectionTimeout 
	 * @param blob 
	 * @param clob 
	 * @param allow 
	 * @param custom 
	 * @param readOnly 
	 * @throws ClassException 
	 */
    public DataSourceImpl(String name,String className, String host, String dsn, String database, int port, String username, String password, 
            int connectionLimit, int connectionTimeout, boolean blob, boolean clob, int allow, Struct custom, boolean readOnly) throws ClassException {
        this(name, toClass(className), host, dsn, database, port, username, password, connectionLimit, connectionTimeout, blob, clob, allow, custom, readOnly);
    	
	}
    
    private static Class toClass(String className) throws ClassException {
    	try {
			return Class.forName(className);
		} 
		catch (ClassNotFoundException e) {
			Config config = ThreadLocalPageContext.getConfig();
			if(config!=null) return ClassUtil.loadClass(config.getClassLoader(),className);
			return ClassUtil.loadClass(className);
		}
	}

	private DataSourceImpl(String name,Class clazz, String host, String dsn, String database, int port, String username, String password, 
            int connectionLimit, int connectionTimeout, boolean blob, boolean clob, int allow, Struct custom, boolean readOnly) {
        if(allow<0) allow=ALLOW_ALL;
        this.name=name;
        this.clazz=clazz;
        this.host=host;
        this.database=database;
        this.dsn=dsn; 
        this.port=port;
        this.username=username;
	    this.password=password;

        this.connectionLimit=connectionLimit;
        this.connectionTimeout=connectionTimeout;
        this.blob=blob;
        this.clob=clob;
        this.allow=allow;
        this.readOnly=readOnly;
        this.custom=custom;
        
        this.dsnTranslated=dsn; 
        translateDsn();
        
        //	throw new DatabaseException("can't find class ["+classname+"] for jdbc driver, check if driver (jar file) is inside lib folder",e.getMessage(),null,null,null);
        
	}
    private void translateDsn() {
        dsnTranslated=replace(dsnTranslated,"host",host,false);
        dsnTranslated=replace(dsnTranslated,"database",database,false);
        dsnTranslated=replace(dsnTranslated,"port",Caster.toString(port),false);
        dsnTranslated=replace(dsnTranslated,"username",username,false);
        dsnTranslated=replace(dsnTranslated,"password",password,false);
        
        Collection.Key[] keys = custom==null?new Collection.Key[0]:custom.keys();
        for(int i=0;i<keys.length;i++) {
            dsnTranslated=replace(dsnTranslated,keys[i].getString(),Caster.toString(custom.get(keys[i],null),""),true);
        }
    }

    private String replace(String src, String name, String value,boolean doQueryString) {
        if(StringUtil.indexOfIgnoreCase(src,"{"+name+"}")!=-1) {
            return StringUtil.replace(dsnTranslated,"{"+name+"}",value,false);
        }
        if(!doQueryString) return src;
        if(clazz.getName().indexOf("microsoft")!=-1 || clazz.getName().indexOf("jtds")!=-1)
        return src+=';'+name+'='+value;
        return src+=((src.indexOf('?')!=-1)?'&':'?')+name+'='+value;
    }

    /**
     * @see railo.runtime.db.DataSource#getDsnOriginal()
     */
    public String getDsnOriginal() {
        return dsn;
    }
    
    /**
     * @see railo.runtime.db.DataSource#getDsnTranslated()
     */
    public String getDsnTranslated() {
        return dsnTranslated;
    }
    
    /**
     * @see railo.runtime.db.DataSource#getPassword()
     */
    public String getPassword() {
        return password;
    }
    /**
     * @see railo.runtime.db.DataSource#getUsername()
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * @see railo.runtime.db.DataSource#isReadOnly()
     */
    public boolean isReadOnly() {
        return readOnly;
    }
    
    /**
     * @see railo.runtime.db.DataSource#hasAllow(int)
     */
    public boolean hasAllow(int allow) {
        return (this.allow&allow)>0;
    }
    
    /**
     * @see railo.runtime.db.DataSource#getClazz()
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * @see railo.runtime.db.DataSource#getDatabase()
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @see railo.runtime.db.DataSource#getPort()
     */
    public int getPort() {
        return port;
    }

    /**
     * @see railo.runtime.db.DataSource#getHost()
     */
    public String getHost() {
        return host;
    }
    
    /**
     * @see railo.runtime.db.DataSource#clone()
     */
    public Object clone() {
        return new DataSourceImpl(name,clazz, host, dsn, database, port, username, password, connectionLimit, connectionTimeout, blob, clob, allow, custom, readOnly);
    }

    /**
     * @see railo.runtime.db.DataSource#cloneReadOnly()
     */
    public DataSource cloneReadOnly() {
        return new DataSourceImpl(name,clazz, host, dsn, database, port, username, password, connectionLimit, connectionTimeout, blob, clob, allow,custom, true);
    }

    /**
     * @see railo.runtime.db.DataSource#isBlob()
     */
    public boolean isBlob() {
        return blob;
    }

    /**
     * @see railo.runtime.db.DataSource#isClob()
     */
    public boolean isClob() {
        return clob;
    }

    /**
     * @see railo.runtime.db.DataSource#getConnectionLimit()
     */
    public int getConnectionLimit() {
        return connectionLimit;
    }

    /**
     * @see railo.runtime.db.DataSource#getConnectionTimeout()
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @see railo.runtime.db.DataSource#getCustomValue(java.lang.String)
     */
    public String getCustomValue(String key) {
        return Caster.toString(custom.get(key,null),"");
    }
    
    /**
     * @see railo.runtime.db.DataSource#getCustomNames()
     */
    public String[] getCustomNames() {
        return custom.keysAsString();
    }
    
    /**
     * @see railo.runtime.db.DataSource#getCustoms()
     */
    public Struct getCustoms() {
        return (Struct)custom.clone();
    }

    /**
     * @see railo.runtime.db.DataSource#hasSQLRestriction()
     */
    public boolean hasSQLRestriction() {
        return this.allow!=DataSource.ALLOW_ALL;
    }

    /**
     * @see railo.runtime.db.DataSource#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see railo.runtime.db.DataSource#setClazz(java.lang.Class)
     */
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

	/**
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.dsnTranslated;
	}

	/**
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(this==obj)return true;
		if(!(obj instanceof DataSourceImpl)) return false;
		DataSourceImpl ds = (DataSourceImpl)obj;
		return this.getDsnTranslated().equals(ds.getDsnTranslated());
	} 

	/* *
	 *
	 * @see railo.runtime.db.DataSource#getMaxConnection()
	 * /
	public int getMaxConnection() {
		return maxConnection;
	}*/

	/* *
	 * @param maxConnection the maxConnection to set
	 * /
	protected void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}*/

}