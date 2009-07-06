package railo.transformer.bytecode.statement;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Literal;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.Types;

public final class ExpressionStatement extends StatementBase {

	private Expression expr;

	/**
	 * Constructor of the class
	 * @param expr
	 */
	public ExpressionStatement(Expression expr) {
		super(expr.getLine());
		this.expr=expr;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		if(!(expr instanceof Literal)) {
			Type type = expr.writeOut(bc, Expression.MODE_VALUE);
			if(type.equals(Types.DOUBLE_VALUE))adapter.pop2();
			else adapter.pop();
		}
	}
}
