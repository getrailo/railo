package railo.runtime.db;

/**
 * represent a SQL Statment
 */
public interface SQL {

    /**
     * @return Returns the items.
     */
    public abstract SQLItem[] getItems();

    /**
     * @return Returns the position.
     */
    public abstract int getPosition();

    /**
     * @param position The position to set.
     */
    public abstract void setPosition(int position);

    /**
     * @return returns the pure SQL String
     */
    public abstract String getSQLString();

    /**
     * @param strSQL sets the SQL String
     */
    public abstract void setSQLString(String strSQL);

    /**
     * @return returns Unique String for Hash
     */
    public abstract String toHashString();

}