package railo.transformer.bytecode.statement;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;

public final class For extends StatementBase implements FlowControl,HasBody {

	private Expression init;
	private Expression contition;
	private Expression update;
	private Body body;
	
	//private static final int I=1;

	Label beforeUpdate = new Label();
	Label end = new Label();
	
	
	
	/**
	 * Constructor of the class
	 * @param init
	 * @param contition
	 * @param update
	 * @param body
	 * @param line
	 */
	public For(Expression init,Expression contition,Expression update,Body body,int startline,int endline) {
		super(startline,endline);
		this.init=init;
		this.contition=contition;
		this.update=update;
		this.body=body;
		body.setParent(this);
		
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(org.objectweb.asm.commons.GeneratorAdapter)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		Label beforeInit = new Label();
		Label afterInit = new Label();
		Label afterUpdate = new Label();

		ExpressionUtil.visitLine(bc, getStartLine());
		adapter.visitLabel(beforeInit);
		if(init!=null) {
			init.writeOut(bc, Expression.MODE_VALUE);
			adapter.pop();
		}
		adapter.visitJumpInsn(Opcodes.GOTO, afterUpdate);
		adapter.visitLabel(afterInit);
		
		body.writeOut(bc);
		
		adapter.visitLabel(beforeUpdate);
		//ExpressionUtil.visitLine(bc, getStartLine());
		if(update!=null) {
			update.writeOut(bc, Expression.MODE_VALUE);
			ASMUtil.pop(adapter,update, Expression.MODE_VALUE); 
		}
		//ExpressionUtil.visitLine(bc, getStartLine());
		adapter.visitLabel(afterUpdate);
		
		if(contition!=null)contition.writeOut(bc, Expression.MODE_VALUE);
		else LitBoolean.TRUE.writeOut(bc, Expression.MODE_VALUE);
		adapter.visitJumpInsn(Opcodes.IFNE, afterInit);
		//ExpressionUtil.visitLine(bc, getEndLine());
		adapter.visitLabel(end);
		
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getBreakLabel()
	 */
	public Label getBreakLabel() {
		return end;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.statement.FlowControl#getContinueLabel()
	 */
	public Label getContinueLabel() {
		return beforeUpdate;
	}

	/**
	 * @see railo.transformer.bytecode.statement.HasBody#getBody()
	 */
	public Body getBody() {
		return body;
	}

}
