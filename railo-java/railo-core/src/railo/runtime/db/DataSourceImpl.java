package railo.runtime.db;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.commons.collections.map.ReferenceMap;

import railo.print;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.CollectionUtil;


/**
 * 
 */
public final class DataSourceImpl  extends DataSourceSupport {

    private String dsn;
    private String host;
    private String database;
    private int port;
    private String dsnTranslated;
    private Struct custom;
	private boolean validate;
    
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
            int connectionLimit, int connectionTimeout,long metaCacheTimeout, boolean blob, boolean clob, int allow, Struct custom, boolean readOnly, 
            boolean validate,boolean storage, TimeZone timezone) throws ClassException {
        this(name, toClass(className), host, dsn, database, port, username, password, connectionLimit, connectionTimeout,metaCacheTimeout, blob, clob, allow, custom, readOnly,validate,storage,timezone);
    	
	}
    
    public static Class toClass(String className) throws ClassException {
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
            int connectionLimit, int connectionTimeout,long metaCacheTimeout, boolean blob, boolean clob, int allow, Struct custom, boolean readOnly, 
            boolean validate,boolean storage,TimeZone timezone) {
		super(name, clazz,username,password,blob,clob,connectionLimit, connectionTimeout, metaCacheTimeout, timezone, allow<0?ALLOW_ALL:allow, storage, readOnly);
			
        this.host=host;
        this.database=database;
        this.dsn=dsn; 
        this.port=port;

        this.custom=custom;
        this.validate=validate;
        
        this.dsnTranslated=dsn; 
        translateDsn();
        
        //	throw new DatabaseException("can't find class ["+classname+"] for jdbc driver, check if driver (jar file) is inside lib folder",e.getMessage(),null,null,null);
        
	}
    private void translateDsn() {
        dsnTranslated=replace(dsnTranslated,"host",host,false);
        dsnTranslated=replace(dsnTranslated,"database",database,false);
        dsnTranslated=replace(dsnTranslated,"port",Caster.toString(port),false);
        dsnTranslated=replace(dsnTranslated,"username",getUsername(),false);
        dsnTranslated=replace(dsnTranslated,"password",getPassword(),false);
        
        //Collection.Key[] keys = custom==null?new Collection.Key[0]:custom.keys();
        if(custom!=null) {
        	Iterator<Entry<Key, Object>> it = custom.entryIterator();
        	Entry<Key, Object> e;
            while(it.hasNext()) {
	        	e = it.next();
	            dsnTranslated=replace(dsnTranslated,e.getKey().getString(),Caster.toString(e.getValue(),""),true);
	        }
        }
    }

    private String replace(String src, String name, String value,boolean doQueryString) {
        if(StringUtil.indexOfIgnoreCase(src,"{"+name+"}")!=-1) {
            return StringUtil.replace(dsnTranslated,"{"+name+"}",value,false);
        }
        if(!doQueryString) return src;
        if(getClazz().getName().indexOf("microsoft")!=-1 || getClazz().getName().indexOf("jtds")!=-1)
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
        return new DataSourceImpl(getName(),getClazz(), host, dsn, database, port, getUsername(), getPassword(), getConnectionLimit(), getConnectionTimeout(),getMetaCacheTimeout(), isBlob(), isClob(), allow, custom, isReadOnly(),validate,isStorage(),getTimeZone());
    }

    /**
     * @see railo.runtime.db.DataSource#cloneReadOnly()
     */
    public DataSource cloneReadOnly() {
        return new DataSourceImpl(getName(),getClazz(), host, dsn, database, port, getUsername(), getPassword(), getConnectionLimit(), getConnectionTimeout(),getMetaCacheTimeout(), isBlob(), isClob(), allow,custom, true,validate,isStorage(),getTimeZone());
    }

    /**
     * @see railo.runtime.db.DataSource#getCustomValue(java.lang.String)
     */
    public String getCustomValue(String key) {
        return Caster.toString(custom.get(KeyImpl.init(key),null),"");
    }
    
    /**
     * @see railo.runtime.db.DataSource#getCustomNames()
     */
    public String[] getCustomNames() {
        return CollectionUtil.keysAsString(custom);
    }
    
    /**
     * @see railo.runtime.db.DataSource#getCustoms()
     */
    public Struct getCustoms() {
        return (Struct)custom.clone();
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
	public boolean equals(Object obj) {print.ds();
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

	public boolean validate() {
		return validate;
	}

	/* *
	 * @param maxConnection the maxConnection to set
	 * /
	protected void setMaxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
	}*/

}