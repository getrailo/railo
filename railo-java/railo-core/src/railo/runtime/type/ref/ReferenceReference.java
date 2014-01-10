package railo.runtime.type.ref;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;

/**
 * Handle a Reference
 */
public final class ReferenceReference implements Reference {

    private Reference reference;
    private String key;

    /**
     * @param reference
     * @param key
     */
    public ReferenceReference(Reference reference, String key) {
        this.reference=reference;
        this.key=key;
    }

    @Override
    public Collection.Key getKey() {
        return KeyImpl.init(key);
    }

    @Override
    public String getKeyAsString() {
        return key;
    }

    @Override
    public Object get(PageContext pc) throws PageException {
        return pc.getCollection(reference.get(pc),key);       
    }

    @Override
    public Object get(PageContext pc, Object defaultValue) {
        return pc.getCollection(reference.get(pc,null),key,defaultValue);       
    }

    @Override
    public Object set(PageContext pc, Object value) throws PageException {
        return pc.set(reference.touch(pc),key,value);
    }

    @Override
    public Object setEL(PageContext pc, Object value) {
        try {
			return set(pc,value);
		} catch (PageException e) {
			return null;
		}
    }

    @Override
    public Object touch(PageContext pc) throws PageException {
        Object parent=reference.touch(pc);
        Object o= pc.getCollection(parent,key,null);
        if(o!=null) return o;
        return pc.set(parent,key,new StructImpl());
    }
    
    public Object touchEL(PageContext pc) {
        Object parent=reference.touchEL(pc);
        Object o= pc.getCollection(parent,key,null);
        if(o!=null) return o;
        try {
			return pc.set(parent,key,new StructImpl());
		} catch (PageException e) {
			return null;
		}
    }

    @Override
    public Object remove(PageContext pc) throws PageException {
        return pc.getVariableUtil().remove(reference.get(pc),key);
    }

    @Override
    public Object removeEL(PageContext pc) {
        return pc.getVariableUtil().removeEL(reference.get(pc,null),key);
    }

    @Override
    public Object getParent() {
        return reference;
    }
    @Override
    public String toString() {
        return "java.util.ReferenceReference(reference:"+reference+";key:"+key+")";
    }
}