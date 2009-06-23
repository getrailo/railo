

package railo.runtime.interpreter.ref.func;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.util.RefUtil;
import railo.runtime.op.Caster;

/**
 * call of a User defined Function
 */
public final class UDFCall extends RefSupport implements Ref {

	
	private Ref[] arguments;
    private String name;
    private PageContext pc;
    private Ref parent;
    private Ref refName;

    /**
     * @param pc
     * @param parent
     * @param name
     * @param arguments
     */
    public UDFCall(PageContext pc, Ref parent, String name, Ref[] arguments) {
        this.pc=pc;
        this.parent=parent;
        this.name=name;
        this.arguments=arguments;
    }
    
    /**
     * @param pc
     * @param parent
     * @param refName
     * @param arguments
     */
    public UDFCall(PageContext pc, Ref parent, Ref refName, Ref[] arguments) {
        this.pc=pc;
        this.parent=parent;
        this.refName=refName;
        this.arguments=arguments;
    }

    /**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() throws PageException {
        return pc.getVariableUtil().callFunction(
                pc,
                parent.getValue(),
                getName(),
                RefUtil.getValue(arguments)
        );
	}

	private String getName() throws PageException {
        if(name!=null)return name;
        return Caster.toString(refName.getValue());
    }

    /**
	 * @see railo.runtime.interpreter.ref.Ref#getTypeName()
	 */
	public String getTypeName() {
		return "user defined function";
	}
}
