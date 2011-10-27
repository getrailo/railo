package railo.runtime.interpreter.ref.func;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.util.RefUtil;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.FunctionValueImpl;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

/**
 * a built In Function call
 *
 *
 */
public final class BIFCall extends RefSupport implements Ref {
		
	private Ref[] refArgs;
	private PageContext pc;
    private FunctionLibFunction flf;


	/**
	 * constructor of the class
	 * @param pc
	 * @param flf 
	 * @param refArgs 
	 */
	public BIFCall(PageContext pc, FunctionLibFunction flf,Ref[] refArgs) {
		this.pc=pc;
        this.flf=flf;
		this.refArgs=refArgs;
	}
	
	/**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() throws PageException {
        
        Object[] arguments = RefUtil.getValue(refArgs);
        
        
        if(isDynamic()){
        	if(flf.hasDefaultValues()){
        		List tmp=new ArrayList();
        		ArrayList args = flf.getArg();
        		Iterator it = args.iterator();
        		FunctionLibFunctionArg arg;
        		while(it.hasNext()){
        			arg=(FunctionLibFunctionArg) it.next();
        			if(arg.getDefaultValue()!=null)
        				tmp.add(new FunctionValueImpl(arg.getName(),arg.getDefaultValue()));
        		}
        		for(int i=0;i<arguments.length;i++){
        			tmp.add(arguments[i]);
        		}
        		arguments=tmp.toArray();
        	}
        	arguments=new Object[]{pc,arguments};
        }
        else {
            Object[] newAttr = new Object[arguments.length+1];
            newAttr[0]=pc;
            for(int i=0;i<arguments.length;i++) {
                newAttr[i+1]=arguments[i];
            }
            arguments=newAttr;
        }
        Class clazz=flf.getCazz();
        if(clazz==null)throw new ExpressionException("class "+clazz+" not found");
        return Caster.castTo(pc,flf.getReturnTypeAsString(),Reflector.callStaticMethod(clazz,"call",arguments),false);
	}
    
    private boolean isDynamic() {
        return flf.getArgType()==FunctionLibFunction.ARG_DYNAMIC;
    }
    
	/**
	 * @see railo.runtime.interpreter.ref.Ref#getTypeName()
	 */
	public String getTypeName() {
		return "built in function";
	}

}
