package railo.transformer.bytecode.expression.type;

import org.objectweb.asm.Type;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.Types;

public class CollectionKey extends ExpressionBase {

	private String value;

	public CollectionKey(String value,int line) {
		super(line);
		this.value=value;
	}

	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		Variable.registerKey(bc, LitString.toExprString(value));
		return Types.COLLECTION_KEY;
	}

}
