package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import railo.runtime.type.FunctionValueImpl;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.Types;

public final class NamedArgument extends Argument {
	

    private static final Type TYPE_FUNCTION_VALUE=Type.getType(FunctionValueImpl.class);
    // railo.runtime.type.FunctionValue newInstance (String,Object)
    private final static Method  NEW_INSTANCE = new Method("newInstance",
			Types.FUNCTION_VALUE,
			new Type[]{Types.STRING,Types.OBJECT});
	
	private ExprString name;

	public NamedArgument(Expression name, Expression value, String type) {
		super(value,type);
		
		if(name instanceof Variable) {
			this.name=VariableString.toExprString(name);
		}
		else if(name instanceof LitString) {
			this.name=CastString.toExprString(name);
		}
		else this.name=CastString.toExprString(name);
	}

	/**
	 * @return the name
	 */
	public ExprString getName() {
		return name;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.expression.var.Argument#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
	 */
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		
		name.writeOut(bc, MODE_REF);
		super._writeOut(bc, MODE_REF);
		bc.getAdapter().invokeStatic(TYPE_FUNCTION_VALUE, NEW_INSTANCE);
		return Types.FUNCTION_VALUE;
	}

	
	/**
	 * @see railo.transformer.bytecode.expression.var.Argument#writeOutValue(railo.transformer.bytecode.BytecodeContext, int)
	 */
	public Type writeOutValue(BytecodeContext bc, int mode)
			throws BytecodeException {
		// TODO Auto-generated method stub
		return super.writeOutValue(bc, mode);
	}
	
}
