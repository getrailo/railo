package railo.transformer.bytecode.statement;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.util.Types;

public final class Abort extends StatementBaseNoFinal {

	private static final Type ABORT = Type.getType(railo.runtime.exp.Abort.class);
	
	// ExpressionException newInstance(int)
	private static final Method NEW_INSTANCE =  new Method(
			"newInstance",
			ABORT,
			new Type[]{Types.INT_VALUE});

	
	public Abort(Position start, Position end) {
		super(start,end);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(railo.transformer.bytecode.BytecodeContext)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.push(railo.runtime.exp.Abort.SCOPE_PAGE);
		adapter.invokeStatic(ABORT, NEW_INSTANCE);
		adapter.throwException();
		
	}
}
