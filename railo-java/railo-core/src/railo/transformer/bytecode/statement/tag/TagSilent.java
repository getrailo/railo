package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.NotVisitor;
import railo.transformer.bytecode.visitor.TryFinallyVisitor;

public final class TagSilent extends TagBase {

	// boolean setSilent()
	private static final Method SET_SILENT = new Method(
			"setSilent",
			Types.BOOLEAN_VALUE,
			new Type[]{}
	);

	// boolean unsetSilent();
	private static final Method UNSET_SILENT = new Method(
			"unsetSilent",
			Types.BOOLEAN_VALUE,
			new Type[]{}
	);
	
	/**
	 * Constructor of the class
	 * @param line
	 */
	public TagSilent(int line) {
		super(line);
	}
	public TagSilent(int sl,int el) {
		super(sl,el);
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		
		int silentMode=adapter.newLocal(Types.BOOLEAN_VALUE);
		
		// boolean silentMode= pc.setSilent();
		adapter.loadArg(0);
		adapter.invokeVirtual(Types.PAGE_CONTEXT, SET_SILENT);
		adapter.storeLocal(silentMode);
		
		TryFinallyVisitor tfv=new TryFinallyVisitor();
		tfv.visitTryBegin(bc);
			getBody().writeOut(bc);
		tfv.visitTryEndFinallyBegin(bc);
			// if(!silentMode)pc.unsetSilent();
			Label _if=new Label();
			adapter.loadLocal(silentMode);
			NotVisitor.visitNot(bc);
			adapter.ifZCmp(Opcodes.IFEQ, _if);
				adapter.loadArg(0);
				adapter.invokeVirtual(Types.PAGE_CONTEXT, UNSET_SILENT);
				adapter.pop();
			
			adapter.visitLabel(_if);
		
		tfv.visitFinallyEnd(bc);

	}

}
