package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;

import railo.transformer.Factory;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.Types;

public class NullExpression extends ExpressionBase {

	//public static final NullExpression NULL_EXPRESSION=new NullExpression();
	
	public NullExpression(Factory f) {
		super(f,null,null);
	}
	

	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		ASMConstants.NULL(bc.getAdapter());
		return Types.OBJECT;
	}

}
