package railo.runtime.db;

import java.io.Serializable;
import java.sql.Types;

import railo.commons.lang.SizeOf;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Sizeable;


/**
 * 
 */
public final class SQLItemImpl implements SQLItem,Serializable,Sizeable {

	/** Yes or No. Indicates whether the parameter is passed as a null. If Yes, the tag ignores the 
	** 	value attribute. The default is No. */
	private boolean nulls;

	/** Specifies the actual value that Railo passes to the right of the comparison operator in a 
	** 	where clause. */
	private Object value;
	private Object cfValue;


	/** Number of decimal places of the parameter. The default value is zero. */
	private int scale=0;

	/** The SQL type that the parameter (any type) will be bound to. */
	private int type=Types.CHAR;

    private boolean isValueSet;
    
    /**
     * constructor of the class
     */
    public SQLItemImpl() {}
    
    /**
     *  constructor of the class
     * @param value
     */
    public SQLItemImpl(Object value) {
        this.value=value;
    }
    
    /**
     *  constructor of the class
     * @param value
     */
    public SQLItemImpl(Object value, int type) {
        this.value=value;
        this.type=type;
    }

    /**
     * @see railo.runtime.db.SQLItem#isNulls()
     */
    public boolean isNulls() {
        return nulls;
    }
    /**
     * @see railo.runtime.db.SQLItem#setNulls(boolean)
     */
    public void setNulls(boolean nulls) {
        this.nulls = nulls;
    }
    /**
     * @see railo.runtime.db.SQLItem#getScale()
     */
    public int getScale() {
        return scale;
    }
    /**
     * @see railo.runtime.db.SQLItem#setScale(int)
     */
    public void setScale(int scale) {
        this.scale = scale;
    }
    /**
     * @see railo.runtime.db.SQLItem#getValue()
     */
    public Object getValue() {
        return value;
    }
    /**
     * @see railo.runtime.db.SQLItem#setValue(java.lang.Object)
     */
    public void setValue(Object value) {
        isValueSet=true;
        this.value = value;
    }
    
    /**
     * @see railo.runtime.db.SQLItem#getType()
     */
    public int getType() {
        return type;
    }
    /**
     * @see railo.runtime.db.SQLItem#setType(int)
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     * @see railo.runtime.db.SQLItem#clone(java.lang.Object)
     */
    public SQLItem clone(Object object) {
       
        SQLItemImpl item = new SQLItemImpl();
        item.nulls=nulls;
        item.scale=scale;
        item.type=type;
        item.value=object;
        return item;
    }
    
    /**
     * @see railo.runtime.db.SQLItem#getValueForCF()
     */
    public Object getValueForCF() throws PageException {
        if(cfValue==null) {
            cfValue=SQLCaster.toCFTypex(this);
        }
        return cfValue;
    }
    
    /**
     * @see railo.runtime.db.SQLItem#isValueSet()
     */
    public boolean isValueSet() {
        return isValueSet;
    }
    
    public String toString() {
    	try {
			return Caster.toString(getValueForCF(),"");
		} catch (PageException e) {
			return Caster.toString(getValue(),"");
		}
    }

	public long sizeOf() {
		return SizeOf.size(this.cfValue)+
		SizeOf.size(this.isValueSet)+
		SizeOf.size(this.nulls)+
		SizeOf.size(this.value);
	}
}