package railo.transformer.bytecode.statement.tag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.Expression;

public final class TagIf extends TagBaseNoFinal {

	
	public TagIf(Factory f, Position start,Position end) {
		super(f,start,end);
	}

	public void _writeOut(BytecodeContext bc) throws TransformerException {
		GeneratorAdapter adapter = bc.getAdapter();
		
		Label end = new Label();
		List<Statement> tmp=new ArrayList<Statement>();
		Iterator<Statement> it = getBody().getStatements().iterator();
		Tag t;
		Label endIf=writeOutElseIfStart(bc, this);
		boolean hasElse=false;
		while(it.hasNext()) {
			Statement stat = it.next();
			if(!hasElse && stat instanceof Tag) {
				t=(Tag) stat;
				
				if(t.getTagLibTag().getTagClassName().equals("railo.runtime.tag.ElseIf")) {
					__writeOut(bc,tmp);
					writeOutElseIfEnd(adapter, endIf, end);
					endIf=writeOutElseIfStart(bc,t);
					continue;
				}
				else if(t.getTagLibTag().getTagClassName().equals("railo.runtime.tag.Else")) {
					__writeOut(bc,tmp);
					ExpressionUtil.visitLine(bc, t.getStart());
			        hasElse=true;
					writeOutElseIfEnd(adapter, endIf, end);
					continue;
				}
			}
			tmp.add(stat);
			//ExpressionUtil.writeOut(stat, bc);
		}
		__writeOut(bc,tmp);
		
		if(!hasElse)writeOutElseIfEnd(adapter, endIf, end);
		
		adapter.visitLabel(end);
	}

	private void __writeOut(BytecodeContext bc, List<Statement> statements) throws TransformerException {
		if(statements.size()>0) {
			BodyBase.writeOut(bc, statements);
			statements.clear();
		}
	}

	private static Label writeOutElseIfStart(BytecodeContext bc, Tag tag) throws TransformerException {
		GeneratorAdapter adapter = bc.getAdapter();

		ExprBoolean cont = bc.getFactory().toExprBoolean(tag.getAttribute("condition").getValue());
		
		Label endIf = new Label();
        
		ExpressionUtil.visitLine(bc, tag.getStart());
        cont.writeOut(bc,Expression.MODE_VALUE);
        adapter.ifZCmp(Opcodes.IFEQ, endIf);
        return endIf;
	}
	private static void writeOutElseIfEnd(GeneratorAdapter adapter, Label endIf, Label end) {
		adapter.visitJumpInsn(Opcodes.GOTO, end);
		adapter.visitLabel(endIf);
	}
}
