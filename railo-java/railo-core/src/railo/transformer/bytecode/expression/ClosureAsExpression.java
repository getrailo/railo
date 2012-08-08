package railo.transformer.bytecode.expression;

import org.objectweb.asm.Type;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.statement.udf.Closure;
import railo.transformer.bytecode.util.Types;

public class ClosureAsExpression extends ExpressionBase {

	private Closure closure;


	public ClosureAsExpression(Closure closure) {
		super(closure.getStart(),closure.getEnd());
		this.closure=closure;
	}
	
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		closure._writeOut(bc);
		return Types.UDF_IMPL;
	}

	/**
	 * @return the closure
	 */
	public Closure getClosure() {
		return closure;
	}
}
