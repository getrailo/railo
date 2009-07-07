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

    /**
     *
     * @see railo.runtime.type.ref.Reference#getKey()
     */
    public Collection.Key getKey() {
        return KeyImpl.init(key);
    }

    /**
     *
     * @see railo.runtime.type.ref.Reference#getKeyAsString()
     */
    public String getKeyAsString() {
        return key;
    }

    /**
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext)
     */
    public Object get(PageContext pc) throws PageException {
        return pc.getCollection(reference.get(pc),key);       
    }

    /**
     *
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext, java.lang.Object)
     */
    public Object get(PageContext pc, Object defaultValue) {
        return pc.getCollection(reference.get(pc,null),key,defaultValue);       
    }

    /**
     * @see railo.runtime.type.ref.Reference#set(railo.runtime.PageContext, java.lang.Object)
     */
    public Object set(PageContext pc, Object value) throws PageException {
        return pc.set(reference.touch(pc),key,value);
    }

    /**
     * @see railo.runtime.type.ref.Reference#setEL(railo.runtime.PageContext, java.lang.Object)
     */
    public Object setEL(PageContext pc, Object value) {
        try {
			return set(pc,value);
		} catch (PageException e) {
			return null;
		}
    }

    /**
     * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
     */
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

    /**
     * @see railo.runtime.type.ref.Reference#remove(railo.runtime.PageContext)
     */
    public Object remove(PageContext pc) throws PageException {
        return pc.getVariableUtil().remove(reference.get(pc),key);
    }

    /**
     * @see railo.runtime.type.ref.Reference#removeEL(railo.runtime.PageContext)
     */
    public Object removeEL(PageContext pc) {
        return pc.getVariableUtil().removeEL(reference.get(pc,null),key);
    }

    /**
     * @see railo.runtime.type.ref.Reference#getParent()
     */
    public Object getParent() {
        return reference;
    }
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "java.util.ReferenceReference(reference:"+reference+";key:"+key+")";
    }
}