package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.tag.ThreadTag;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.Types;

public final class TagThread extends TagBase {

	
	
	public static final Type THREAD_TAG = Type.getType(ThreadTag.class);
	
	
	private static final Method REGISTER = new Method(
			"register",Types.VOID,new Type[]{Types.PAGE,Types.INT_VALUE});

	/**
	 * Constructor of the class
	 * @param line
	 */
	public TagThread(int line) {
		super(line);
	}
	public TagThread(int sl,int el) {
		super(sl,el);
	}
	
	
	
	

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(railo.transformer.bytecode.BytecodeContext)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		String action=ASMUtil.getAttributeString(this, "action","run");
		// no body
		if(!"run".equalsIgnoreCase(action)) {
			super._writeOut(bc);
			return;
		}
		/*if("run".equalsIgnoreCase(action)
				super.getBody()==null || 
				super.getBody().getStatements()==null || 
				super.getBody().getStatements().size()==0) {
			super._writeOut(bc);
			return;
		}*/
		

		GeneratorAdapter adapter = bc.getAdapter();
		Page page = ASMUtil.getAncestorPage(this);
		
		int index=page.addThread(this);
		super._writeOut(bc,false);
		
		adapter.loadLocal(bc.getCurrentTag());
		adapter.loadThis();
        adapter.push(index);
		adapter.invokeVirtual(THREAD_TAG, REGISTER);
		
	}





	/**
	 * @see railo.transformer.bytecode.statement.tag.TagBase#getBody()
	 */
	public Body getBody() {
		return new BodyBase();
	}
	
	public Body getRealBody() {
		return super.getBody();
	}
	
}
