package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.TypeScope;
import railo.transformer.bytecode.util.Types;

public final class VariableRef extends ExpressionBase {

	
	private Variable variable;
	// Object touch (Object,String)
    private final static Method TOUCH =  new Method("touch",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING});
    // railo.runtime.type.ref.Reference getReference (Object,String)
    private final static Method GET_REFERENCE =  new Method("getReference",
			Types.REFERENCE,
			new Type[]{Types.OBJECT,Types.STRING});

	// Object touch (Object,Key)
    private final static Method TOUCH_KEY =  new Method("touch",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY});
    // railo.runtime.type.ref.Reference getReference (Object,Key)
    private final static Method GET_REFERENCE_KEY =  new Method("getReference",
			Types.REFERENCE,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY});

	public VariableRef(Variable variable) {
		super(variable.getStart(),variable.getEnd());
		this.variable=variable;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
	 */
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		int count=variable.countFM+variable.countDM;
		
		for(int i=0;i<=count;i++) {
    		adapter.loadArg(0);
		}
		TypeScope.invokeScope(adapter, variable.scope);
		
		boolean isLast;
		for(int i=0;i<count;i++) {
			isLast=(i+1)==count;
			if(Variable.registerKey(bc,((DataMember)variable.members.get(i)).getName()))
				adapter.invokeVirtual(Types.PAGE_CONTEXT,isLast?GET_REFERENCE_KEY:TOUCH_KEY);
			else
				adapter.invokeVirtual(Types.PAGE_CONTEXT,isLast?GET_REFERENCE:TOUCH);
			//((DataMember)variable.members.get(i)).getName().writeOut(bc, MODE_REF);
    		//adapter.invokeVirtual(Types.PAGE_CONTEXT,isLast?GET_REFERENCE:TOUCH);
		}
		return Types.REFERENCE;
	}

	/* *
	 *
	 * @see railo.transformer.bytecode.expression.Expression#getType()
	 * /
	public int getType() {
		return Types._OBJECT;
	}*/

}
