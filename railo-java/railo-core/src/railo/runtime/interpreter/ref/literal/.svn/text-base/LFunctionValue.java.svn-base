package railo.runtime.interpreter.ref.literal;

import java.util.ArrayList;
import java.util.List;

import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.Set;
import railo.runtime.interpreter.ref.var.Variable;
import railo.runtime.type.FunctionValueImpl;

/**
 * ref for a functionValue
 */
public final class LFunctionValue extends RefSupport implements Ref {


    private Ref name;
    private Ref value;

    /**
     * constructor of the class
     * @param name
     * @param value
     */
    public LFunctionValue(Ref name, Ref value) {
    	this.name=name;
        this.value=value;
    }

    public Object getValue() throws PageException {
        
        if(name instanceof Variable){
        	return new FunctionValueImpl(toStringArray((Set)name),value.getValue());
        }
        if(name instanceof Literal) {
        	return new FunctionValueImpl(((Literal)name).getString(),value.getValue());
        }
        
        // TODO no idea if this is ever used
        if(name instanceof Set){
        	return new FunctionValueImpl(railo.runtime.type.List.arrayToList(toStringArray((Set)name),"."),value.getValue());
        }
        throw new ExpressionException("invalid syntax in named argument");
        //return new FunctionValueImpl(key,value.getValue());
    }

    private String[] toStringArray(Set set) throws PageException {
    	Ref ref=set;
    	String str;
    	List<String> arr=new ArrayList<String>();
    	do {
            set=(Set) ref;
            str=set.getKeyAsString();
            if(str!=null)arr.add(0, str);
            else break;
            ref=set.getParent();
        }while(ref instanceof Set);
        return arr.toArray(new String[arr.size()]);
	}

	/**
     * @see railo.runtime.interpreter.ref.Ref#getTypeName()
     */
    public String getTypeName() {
        return "function value";
    }

}
