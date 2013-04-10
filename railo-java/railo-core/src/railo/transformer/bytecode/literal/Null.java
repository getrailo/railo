package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;

import railo.runtime.type.scope.Scope;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.expression.var.DataMember;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.Types;

public class Null extends ExpressionBase  {


	public Null(Position start, Position end) {
		super(start, end);
	}

	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		ASMConstants.NULL(bc.getAdapter());
		return Types.OBJECT;
	}

	public Variable toVariable() {
		Variable v = new Variable(Scope.SCOPE_UNDEFINED,getStart(),getEnd());
		v.addMember(new DataMember(LitString.toExprString("null")));
		return v;
	}

}
