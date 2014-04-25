package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.RandomUtil;
import railo.runtime.tag.ThreadTag;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.Types;

public final class TagThread extends TagBaseNoFinal {

	
	
	public static final Type THREAD_TAG = Type.getType(ThreadTag.class);
	
	
	private static final Method REGISTER = new Method(
			"register",Types.VOID,new Type[]{Types.PAGE,Types.INT_VALUE});


	public TagThread(Factory f, Position start,Position end) {
		super(f,start,end);
	}

	@Override
	public void _writeOut(BytecodeContext bc) throws TransformerException {
		String action=ASMUtil.getAttributeString(this, "action","run");
		// no body
		if(!"run".equalsIgnoreCase(action)) {
			super._writeOut(bc);
			return;
		}
		
		Attribute name = getAttribute("name");
		if(name==null){
			addAttribute(new Attribute(false, "name",bc.getFactory().createLitString("thread"+RandomUtil.createRandomStringLC(20)), "string"));
		}

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
	@Override
	public Body getBody() {
		return new BodyBase(getFactory());
	}
	
	public Body getRealBody() {
		return super.getBody();
	}
	
}
