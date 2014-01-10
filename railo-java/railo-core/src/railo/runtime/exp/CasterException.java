
package railo.runtime.exp;

import railo.runtime.op.Caster;
import railo.runtime.type.util.Type;


/**
 * 
 */
public class CasterException extends ExpressionException {

    /**
     * constructor of the Exception
     * @param o
     * @param type
     */
	public CasterException(Object o,String type) {
        super(createMessage(o, type),createDetail(o));
    }
	
	public CasterException(Object o,Class clazz) {
        super(createMessage(o, Caster.toTypeName(clazz)),createDetail(o));
    }

    /**
     * constructor of the Exception
     * @param message
     */
    public CasterException(String message) {
        super(message);
    }
    
    private static String createDetail(Object o) {
        if(o!=null) return "Java type of the object is "+Caster.toClassName(o);
        return "value is null";
    }   
    
    public static String createMessage(Object o, String type) {
        
    	if(o instanceof String) return "Can't cast String ["+o+"] to a value of type ["+type+"]";
    	if(o!=null) return "Can't cast Object type ["+Type.getName(o)+"] to a value of type ["+type+"]";
        return "Can't cast Null value to value of type ["+type+"]";
    }   
}