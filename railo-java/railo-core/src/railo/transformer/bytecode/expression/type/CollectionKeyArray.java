package railo.transformer.bytecode.expression.type;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;

public class CollectionKeyArray extends ExpressionBase {

	private String[] arr;

	public CollectionKeyArray(String[] arr){
		super(null,null);
		this.arr=arr;
	}
	public CollectionKeyArray(String[] arr, Position start,Position end){
		super(start,end);
		this.arr=arr;
	}
	
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		ArrayVisitor av=new ArrayVisitor();
        av.visitBegin(adapter,Types.COLLECTION_KEY,arr.length);
        for(int y=0;y<arr.length;y++){
			av.visitBeginItem(adapter, y);
				new CollectionKey(arr[y])._writeOut(bc, mode);
				//adapter.push(arr[y]);
			av.visitEndItem(bc.getAdapter());
        }
        av.visitEnd();
        return Types.COLLECTION_KEY_ARRAY;
	}
}
