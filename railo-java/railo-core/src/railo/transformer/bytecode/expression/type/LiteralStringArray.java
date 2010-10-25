package railo.transformer.bytecode.expression.type;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;

public class LiteralStringArray extends ExpressionBase {

	private String[] arr;

	public LiteralStringArray(String[] arr, int line){
		super(line);
		this.arr=arr;
	}
	
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		ArrayVisitor av=new ArrayVisitor();
        av.visitBegin(adapter,Types.STRING,arr.length);
        for(int y=0;y<arr.length;y++){
			av.visitBeginItem(adapter, y);
				adapter.push(arr[y]);
			av.visitEndItem(bc);
        }
        av.visitEnd();
        return Types.STRING_ARRAY;
	}
}
