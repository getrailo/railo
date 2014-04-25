package railo.runtime.interpreter.ref.cast;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.var.Variable;
import railo.runtime.op.Caster;

/**
 * cast
 */
public final class Casting extends RefSupport implements Ref {
    
    private final short type;
    private final String strType;
    private Ref ref;
    private Object val;
    
    /**
     * constructor of the class
     * @param pc 
     * @param strType 
     * @param type
     * @param ref
     */
    public Casting(String strType,short type, Ref ref) {
    	this.type=type;
        this.strType=strType;
        this.ref=ref;
    }
    public Casting(String strType,short type, Object val) {
    	this.type=type;
        this.strType=strType;
        this.val=val;
    }
    
    @Override
    public Object getValue(PageContext pc) throws PageException {
    	// if ref == null, it is val based Casting
    	if(ref==null) return Caster.castTo(pc,type,strType,val);
    	if(ref instanceof Variable && "queryColumn".equalsIgnoreCase(strType)) {
    		Variable var=(Variable) ref;
    		return Caster.castTo(pc,type,strType,var.getCollection(pc));
    	}
    	return Caster.castTo(pc,type,strType,ref.getValue(pc));
    }
    
    public Ref getRef() {
        return ref;
    }
    
    public String getStringType() {
        return strType;
    }
    
    public short getType() {
        return type;
    }

    @Override
	public String getTypeName() {
        return "operation";
    }
    

    @Override
	public String toString() {
        return strType+":"+ref+":"+val;
    }
}
