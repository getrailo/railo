package railo.transformer.bytecode.visitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.PrintOut;

public final class SwitchVisitor implements Opcodes{
	
	
	public void visitBegin(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();

		adapter.push(3);
		Label l1 = new Label();
		Label l2 = new Label();
		Label l3 = new Label();
		Label l4 = new Label();
		adapter.visitLookupSwitchInsn(l4, new int[] { 3, 50, 70 }, new Label[] { l1, l2, l3 });
		adapter.visitLabel(l1);
		new PrintOut(LitString.toExprString("3", -1),-1).writeOut(bc);//3
		adapter.visitLabel(l2);
		new PrintOut(LitString.toExprString("50", -1),-1).writeOut(bc);// 50
		adapter.visitLabel(l3);
		new PrintOut(LitString.toExprString("70", -1),-1).writeOut(bc);// 70
		adapter.visitLabel(l4);
		new PrintOut(LitString.toExprString("default", -1),-1).writeOut(bc);// default
		Label l5 = new Label();
		adapter.visitLabel(l5);

	}
}
