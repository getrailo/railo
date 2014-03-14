package railo.transformer.bytecode.expression.type;

import org.objectweb.asm.Type;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;

public class CollectionKey extends ExpressionBase {

	private String value;

	public CollectionKey(Factory factory,String value) {
		super(factory,null,null);
		this.value=value;
	}

	public CollectionKey(Factory factory,String value,Position start,Position end) {
		super(factory,start,end);
		this.value=value;
	}

	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
		getFactory().registerKey(bc, bc.getFactory().createLitString(value),false);
		return Types.COLLECTION_KEY;
	}

}
