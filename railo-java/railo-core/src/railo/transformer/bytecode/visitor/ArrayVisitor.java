package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public final class ArrayVisitor {

	public void visitBegin(GeneratorAdapter adapter,Type type,int length) {
		adapter.push(length);
		adapter.newArray(type);
	}
	public void visitBeginItem(GeneratorAdapter adapter,int index) {
		adapter.dup();
        adapter.push(index);
	}
	/*public void visitEndItem(BytecodeContext bc) {
		bc.getAdapter().visitInsn(Opcodes.AASTORE);
	}*/
	
	public void visitEndItem(GeneratorAdapter adapter) {
		adapter.visitInsn(Opcodes.AASTORE);
	}

	public void visitEnd() {
	}
	
}
