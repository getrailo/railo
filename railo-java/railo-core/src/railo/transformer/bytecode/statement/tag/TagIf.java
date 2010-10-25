package railo.transformer.bytecode.statement.tag;

import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.util.ExpressionUtil;

public final class TagIf extends TagBase {

	
	/**
	 * Constructor of the class
	 * @param tag
	 */
	public TagIf(int sl) {
		this(sl,-1);
	}
	public TagIf(int sl,int el) {
		super(sl,el);
	}

	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		Label end = new Label();
		//Contition cont=new Contition(tag.getLine());
		//ExprBoolean ifContition = CastBoolean.toExprBoolean(tag.getAttribute("contition").getValue());
		Body ifBody=getBody();
		
		List stats = ifBody.getStatements();
		Iterator it = stats.iterator();
		Tag t;
		Label endIf=writeOutElseIfStart(bc, this);
		boolean hasElse=false;
		while(it.hasNext()) {
			Statement stat = ((Statement)it.next());
			if(!hasElse && stat instanceof Tag) {
				t=(Tag) stat;
				if(t.getTagLibTag().getTagClassName().equals("railo.runtime.tag.ElseIf")) {
					writeOutElseIfEnd(adapter, endIf, end);
					endIf=writeOutElseIfStart(bc,t);
					continue;
				}
				else if(t.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Else")) {
					ExpressionUtil.visitLine(bc, t.getLine());
			        hasElse=true;
					writeOutElseIfEnd(adapter, endIf, end);
					//endIf=writeOutElseIfStart(adapter,t);
					continue;
				}
			}
			stat.writeOut(bc);
		}
		if(!hasElse)writeOutElseIfEnd(adapter, endIf, end);
		
		adapter.visitLabel(end);
	}

	private static Label writeOutElseIfStart(BytecodeContext bc, Tag tag) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();

		ExprBoolean cont = CastBoolean.toExprBoolean(tag.getAttribute("contition").getValue());
		
		Label endIf = new Label();
        
		ExpressionUtil.visitLine(bc, tag.getLine());
        cont.writeOut(bc,Expression.MODE_VALUE);
        adapter.ifZCmp(Opcodes.IFEQ, endIf);
        return endIf;
	}
	private static void writeOutElseIfEnd(GeneratorAdapter adapter, Label endIf, Label end) {

        adapter.visitJumpInsn(Opcodes.GOTO, end);
        adapter.visitLabel(endIf);
	}


}
