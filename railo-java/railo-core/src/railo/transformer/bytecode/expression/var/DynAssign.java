package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;

public final class DynAssign extends ExpressionBase {

	private ExprString name;
	private Expression value;
	
	// Object setVariable(String, Object)
    private final static Method METHOD_SET_VARIABLE = new Method("setVariable",
			Types.OBJECT,
			new Type[]{Types.STRING,Types.OBJECT}); 

	public DynAssign(Position start,Position end) {
		super(start,end);
	}

	/**
	 * Constructor of the class
	 * @param name
	 * @param value
	 */
	public DynAssign(Expression name, Expression value) {
		super(name.getStart(),name.getEnd());
		this.name=CastString.toExprString(name);
		this.value=value;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
	 */
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		name.writeOut(bc, Expression.MODE_REF);
		value.writeOut(bc, Expression.MODE_REF);
		adapter.invokeVirtual(Types.PAGE_CONTEXT,METHOD_SET_VARIABLE);
		return Types.OBJECT;
	}

	/* *
	 *
	 * @see railo.transformer.bytecode.expression.Expression#getType()
	 * /
	public int getType() {
		return Types._OBJECT;
	}*/

	/**
	 * @return the name
	 */
	public ExprString getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public Expression getValue() {
		return value;
	}

}
