package railo.transformer.bytecode.statement.java;

import org.objectweb.asm.Type;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;

public class CastOp extends ExpressionBase {

	private Object value;
	private Class type;
	private DataBag db;

	public CastOp(int line, Object value, Class type, DataBag db) {
		super(line);
		this.value=value;
		this.type=type;
		this.db=db;
	}

	/**
	 * @see railo.transformer.bytecode.expression.ExpressionBase#_writeOut(railo.transformer.bytecode.BytecodeContext, int)
	 */
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		Type to = Type.getType(type);
		return Assign.writeOut(db, bc, to, mode, value, getLine());
	}

}
