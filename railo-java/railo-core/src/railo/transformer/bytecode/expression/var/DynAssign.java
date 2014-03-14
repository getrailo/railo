package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;

public final class DynAssign extends ExpressionBase {

	private ExprString name;
	private Expression value;
	
	// Object setVariable(String, Object)
    private final static Method METHOD_SET_VARIABLE = new Method("setVariable",
			Types.OBJECT,
			new Type[]{Types.STRING,Types.OBJECT}); 

	public DynAssign(Factory f,Position start,Position end) {
		super(f,start,end);
	}

	/**
	 * Constructor of the class
	 * @param name
	 * @param value
	 */
	public DynAssign(Expression name, Expression value) {
		super(name.getFactory(),name.getStart(),name.getEnd());
		this.name=name.getFactory().toExprString(name);
		this.value=value;
	}
	
	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
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
