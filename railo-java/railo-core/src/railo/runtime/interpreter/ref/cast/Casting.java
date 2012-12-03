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
    
    private short type;
    private Ref ref;
    private Object val;
    private String strType;
    
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
    	if(val!=null) return Caster.castTo(pc,type,strType,val);
    	// patch for valueList ..., the complete interpreter code should be removed soon anyway
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

    public String getTypeName() {
        return "operation";
    }
}
