package railo.runtime.type.ref;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;

/**
 * represent a reference to a Object
 */
public final class NativeReference implements Reference {
    
    private Object o;
    private String key;
    

    /**
     * Constructor of the class
     * @param o
     * @param key
     */
    private NativeReference(Object o, String key) {
        this.o=o;
        this.key=key;
    }


    /**
     * returns a Reference Instance
     * @param o
     * @param key
     * @return Reference Instance
     */
    public static Reference getInstance(Object o, String key) {
        if(o instanceof Reference) {
            return new ReferenceReference((Reference)o,key);
        }
        Collection coll = Caster.toCollection(o,null);
        if(coll!=null) return new VariableReference(coll,key);
        return new NativeReference(o,key);
    }

    @Override
    public Object getParent() {
        return o;
    }
    
    public Collection.Key getKey() {
        return KeyImpl.init(key);
    }
    
    @Override
    public String getKeyAsString() {
        return key;
    }

    @Override
    public Object get(PageContext pc) throws PageException {
        return pc.getCollection(o,key);
    }

    @Override
    public Object get(PageContext pc, Object defaultValue) {
        return pc.getCollection(o,key,null);
    }
    
    @Override
    public Object touch(PageContext pc) throws PageException {
        Object rtn=pc.getCollection(o,key,null);
        if(rtn!=null) return rtn;
        return pc.set(o,key,new StructImpl());
    }
    public Object touchEL(PageContext pc) {
        Object rtn=pc.getCollection(o,key,null);
        if(rtn!=null) return rtn;
        try {
			return pc.set(o,key,new StructImpl());
		} catch (PageException e) {
			return null;
		}
    }

    @Override
    public Object set(PageContext pc,Object value) throws PageException {
        return pc.set(o,key,value);
    }
    
    public Object setEL(PageContext pc,Object value) {
        try {
			return pc.set(o,key,value);
		} catch (PageException e) {
			return null;
		}
    }

    @Override
    public Object remove(PageContext pc) throws PageException {
        return pc.getVariableUtil().remove(o,key);
    }
    
    @Override
    public Object removeEL(PageContext pc) {
        return pc.getVariableUtil().removeEL(o,key);
    }


}