package railo.runtime.db;

import railo.runtime.type.Struct;

/**
 * interface for a datasource
 */
public interface DataSource extends Cloneable {

    /**
     * Field <code>ALLOW_SELECT</code>
     */
    public static final int ALLOW_SELECT = 1;

    /**
     * Field <code>ALLOW_DELETE</code>
     */
    public static final int ALLOW_DELETE = 2;

    /**
     * Field <code>ALLOW_UPDATE</code>
     */
    public static final int ALLOW_UPDATE = 4;

    /**
     * Field <code>ALLOW_INSERT</code>
     */
    public static final int ALLOW_INSERT = 8;

    /**
     * Field <code>ALLOW_CREATE</code>
     */
    public static final int ALLOW_CREATE = 16;

    /**
     * Field <code>ALLOW_GRANT</code>
     */
    public static final int ALLOW_GRANT = 32;

    /**
     * Field <code>ALLOW_REVOKE</code>
     */
    public static final int ALLOW_REVOKE = 64;

    /**
     * Field <code>ALLOW_DROP</code>
     */
    public static final int ALLOW_DROP = 128;

    /**
     * Field <code>ALLOW_ALTER</code>
     */
    public static final int ALLOW_ALTER = 256;

    /**
     * Field <code>ALLOW_ALL</code>
     */
    public static final int ALLOW_ALL = ALLOW_SELECT + ALLOW_DELETE
            + ALLOW_UPDATE + ALLOW_INSERT + ALLOW_CREATE + ALLOW_GRANT
            + ALLOW_REVOKE + ALLOW_DROP + ALLOW_ALTER;

    /**
     * @return Returns the dsn.
     */
    public abstract String getDsnOriginal();

    /**
     * @return Returns the dsn.
     */
    public abstract String getDsnTranslated();

    /**
     * @return Returns the password.
     */
    public abstract String getPassword();

    /**
     * @return Returns the username.
     */
    public abstract String getUsername();

    /**
     * @return Returns the readOnly.
     */
    public abstract boolean isReadOnly();

    /**
     * @param allow 
     * @return returns if given allow exists
     */
    public abstract boolean hasAllow(int allow);

    /**
     * @return Returns the clazz.
     */
    public abstract Class getClazz();

    /**
     * @return Returns the database.
     */
    public abstract String getDatabase();

    /**
     * @return Returns the port.
     */
    public abstract int getPort();

    /**
     * @return Returns the host.
     */
    public abstract String getHost();

    /**
     * @return cloned Object
     */
    public abstract Object clone();

    /**
     * @return clone the DataSource as ReadOnly
     */
    public abstract DataSource cloneReadOnly();

    /**
     * @return Returns the blob.
     */
    public abstract boolean isBlob();

    /**
     * @return Returns the clob.
     */
    public abstract boolean isClob();

    /**
     * @return Returns the connectionLimit.
     */
    public abstract int getConnectionLimit();

    /**
     * @return Returns the connectionTimeout.
     */
    public abstract int getConnectionTimeout();

    /**
     * @param key 
     * @return Returns matching custom value or null if not exists.
     */
    public abstract String getCustomValue(String key);

    /**
     * @return returns all custom names
     */
    public abstract String[] getCustomNames();

    /**
     * @return returns custom
     */
    public abstract Struct getCustoms();

    /**
     * @return returns if database has a SQL restriction
     */
    public abstract boolean hasSQLRestriction();

    /**
     * @return Returns the name.
     */
    public abstract String getName();

    /**
     * @param clazz The clazz to set.
     */
    public abstract void setClazz(Class clazz);

	//public abstract int getMaxConnection();

}