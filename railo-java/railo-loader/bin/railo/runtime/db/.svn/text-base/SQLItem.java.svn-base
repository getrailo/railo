package railo.runtime.db;

import railo.runtime.exp.PageException;

/**
 * a Item of a SQL Statement
 */
public interface SQLItem {

    /**
     * @return Returns the nulls.
     */
    public abstract boolean isNulls();

    /**
     * @param nulls The nulls to set.
     */
    public abstract void setNulls(boolean nulls);

    /**
     * @return Returns the scale.
     */
    public abstract int getScale();

    /**
     * @param scale The scale to set.
     */
    public abstract void setScale(int scale);

    /**
     * @return Returns the value.
     */
    public abstract Object getValue();

    /**
     * @param value The value to set.
     */
    public abstract void setValue(Object value);

    /**
     * @return Returns the cfsqltype.
     */
    public abstract int getType();

    /**
     * @param type The cfsqltype to set.
     */
    public abstract void setType(int type);

    /**
     * @param object
     * @return cloned SQL Item
     */
    public abstract SQLItem clone(Object object);

    /**
     * @return CF combatible Type
     * @throws PageException
     */
    public abstract Object getValueForCF() throws PageException;

    /**
     * @return Returns the isValueSet.
     */
    public abstract boolean isValueSet();

}