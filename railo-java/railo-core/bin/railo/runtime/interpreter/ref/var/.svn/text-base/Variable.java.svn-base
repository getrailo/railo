package railo.runtime.interpreter.ref.var;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.Set;
import railo.runtime.interpreter.ref.literal.LString;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.StructImpl;

/**
 * 
 */
public final class Variable extends RefSupport implements Set {
	
	private String key;
	private Ref parent;
	private PageContext pc;
    private Ref refKey;

    /**
     * @param pc
     * @param parent
     * @param key
     */
    public Variable(PageContext pc, Ref parent,String key) {
        this.pc=pc;
        this.parent=parent;
        this.key=key;
    }
    
    /**
     * @param pc
     * @param parent
     * @param refKey
     */
    public Variable(PageContext pc, Ref parent,Ref refKey) {
        this.pc=pc;
        this.parent=parent;
        this.refKey=refKey;
    }

    /**
     * @see railo.runtime.interpreter.ref.Ref#getValue()
     */
    public Object getValue() throws PageException {
        return pc.get(parent.getCollection(),getKeyAsString());
    }
    
    /**
     * @see railo.runtime.interpreter.ref.Ref#touchValue()
     */
    public Object touchValue() throws PageException {
        Object p = parent.touchValue();
        if(p instanceof Query) {
            Object o= ((Query)p).getColumn(getKeyAsString(),null);
            if(o!=null) return o;
            return setValue(new StructImpl());
        }
        
        return pc.touch(p,getKeyAsString());
    }
    
    /**
     * @see railo.runtime.interpreter.ref.Ref#getCollection()
     */
    public Object getCollection() throws PageException {
        Object p = parent.getValue();
        if(p instanceof Query) {
            return ((Query)p).getColumn(getKeyAsString());
        }
        return pc.get(p,getKeyAsString());
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#setValue(java.lang.Object)
     */
    public Object setValue(Object obj) throws PageException {
        return pc.set(parent.touchValue(),getKeyAsString(),obj);
    }

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getTypeName()
	 */
	public String getTypeName() {
		return "variable";
	}

    /**
     * @see railo.runtime.interpreter.ref.Set#getKey()
     */
    public Ref getKey() throws PageException {
        if(key==null)return refKey;
        return new LString(key);
    }
    
    public String getKeyAsString() throws PageException {
        if(key==null)key=Caster.toString(refKey.getValue());
        return key;
    }

    /**
     * @see railo.runtime.interpreter.ref.Set#getParent()
     */
    public Ref getParent() throws PageException {
        return parent;
    }
}
