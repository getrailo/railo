package railo.transformer.bytecode.literal;

import org.objectweb.asm.Type;

import railo.runtime.type.scope.Scope;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.var.Variable;

public class Null extends ExpressionBase  {


	public Null(Factory f,Position start, Position end) {
		super(f,start, end);
	}

	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
		ASMConstants.NULL(bc.getAdapter());
		return Types.OBJECT;
	}

	public Variable toVariable() {
		Variable v = getFactory().createVariable(Scope.SCOPE_UNDEFINED,getStart(),getEnd());
		v.addMember(getFactory().createDataMember(getFactory().createLitString("null")));
		return v;
	}

}
