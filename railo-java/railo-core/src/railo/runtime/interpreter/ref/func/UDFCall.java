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
    private Ref parent;
    private Ref refName;

    /**
     * @param pc
     * @param parent
     * @param name
     * @param arguments
     */
    public UDFCall(Ref parent, String name, Ref[] arguments) {
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
    public UDFCall(Ref parent, Ref refName, Ref[] arguments) {
        this.parent=parent;
        this.refName=refName;
        this.arguments=arguments;
    }

    @Override
	public Object getValue(PageContext pc) throws PageException {
        return pc.getVariableUtil().callFunction(
                pc,
                parent.getValue(pc),
                getName(pc),
                RefUtil.getValue(pc,arguments)
        );
	}

	private String getName(PageContext pc) throws PageException {
        if(name!=null)return name;
        return Caster.toString(refName.getValue(pc));
    }

	@Override
    public String getTypeName() {
		return "user defined function";
	}
}
