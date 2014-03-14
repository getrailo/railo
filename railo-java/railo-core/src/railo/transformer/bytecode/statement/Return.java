package railo.transformer.bytecode.statement;

import java.util.Stack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Statement;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.OnFinally;
import railo.transformer.expression.Expression;

/**
 * Return Statement
 */
public final class Return extends StatementBaseNoFinal {

	Expression expr;

	/**
	 * Constructor of the class
	 * @param line
	 */
	public Return(Factory f, Position start,Position end) {
		super(f,start,end);
		setHasFlowController(true);
		//expr=LitString.toExprString("", line);
	}
	
	/**
	 * Constructor of the class
	 * @param expr
	 * @param line
	 */
	public Return(Expression expr, Position start,Position end) {
		super(expr.getFactory(),start,end);
		this.expr=expr;
		setHasFlowController(true);
		//if(expr==null)expr=LitString.toExprString("", line);
	}

	public void _writeOut(BytecodeContext bc) throws TransformerException {
		GeneratorAdapter adapter = bc.getAdapter();
		
		Stack<OnFinally> finallies = bc.getOnFinallyStack();
		int len=finallies.size();
		OnFinally onFinally;
		
		if(expr==null)ASMConstants.NULL(adapter);
		else expr.writeOut(bc, Expression.MODE_REF);
		if(len>0) {
			int rtn = adapter.newLocal(Types.OBJECT);
			adapter.storeLocal(rtn, Types.OBJECT);
			for(int i=len-1;i>=0;i--) {
				
				onFinally=finallies.get(i);
				onFinally.writeOut(bc);
			}
			adapter.loadLocal(rtn, Types.OBJECT);
		}
		if(bc.getMethod().getReturnType().equals(Types.VOID)) {
			adapter.pop();
			adapter.visitInsn(Opcodes.RETURN);
		}
		else adapter.visitInsn(Opcodes.ARETURN);
	}
	

	/**
	 *
	 * @see railo.transformer.bytecode.statement.StatementBase#setParent(railo.transformer.bytecode.Statement)
	 */
	public void setParent(Statement parent) {
		super.setParent(parent);
		parent.setHasFlowController(true);
	}
}
