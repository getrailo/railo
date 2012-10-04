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
    private Ref refKey;

    /**
     * @param pc
     * @param parent
     * @param key
     */
    public Variable( Ref parent,String key) {
        this.parent=parent;
        this.key=key;
    }
    
    /**
     * @param pc
     * @param parent
     * @param refKey
     */
    public Variable(Ref parent,Ref refKey) {
        this.parent=parent;
        this.refKey=refKey;
    }
    
    @Override
    public Object getValue(PageContext pc) throws PageException {
        return pc.get(parent.getCollection(pc),getKeyAsString(pc));
    }
    
    @Override
    public Object touchValue(PageContext pc) throws PageException {
        Object p = parent.touchValue(pc);
        if(p instanceof Query) {
            Object o= ((Query)p).getColumn(getKeyAsString(pc),null);
            if(o!=null) return o;
            return setValue(pc,new StructImpl());
        }
        
        return pc.touch(p,getKeyAsString(pc));
    }
    
    @Override
    public Object getCollection(PageContext pc) throws PageException {
        Object p = parent.getValue(pc);
        if(p instanceof Query) {
            return ((Query)p).getColumn(getKeyAsString(pc));
        }
        return pc.get(p,getKeyAsString(pc));
    }

    @Override
    public Object setValue(PageContext pc,Object obj) throws PageException {
        return pc.set(parent.touchValue(pc),getKeyAsString(pc),obj);
    }

    @Override
    public String getTypeName() {
		return "variable";
	}

    @Override
    public Ref getKey(PageContext pc) throws PageException {
        if(key==null)return refKey;
        return new LString(key);
    }
    
    @Override
    public String getKeyAsString(PageContext pc) throws PageException {
        if(key==null)key=Caster.toString(refKey.getValue(pc));
        return key;
    }

    @Override
    public Ref getParent(PageContext pc) throws PageException {
        return parent;
    }
}
