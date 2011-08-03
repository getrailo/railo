package railo.transformer.bytecode.statement.java;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;

public class Operation extends ExpressionBase {

	private Object left;
	private Object right;
	private DataBag db;
	private int op;

	public Operation(int line,Object left, Object right,String operation, DataBag db) {
		super(line);
		this.left=left;
		this.right=right;
		this.db = db;
		
		if("plus".equals(operation))this.op=GeneratorAdapter.ADD;
		else if("minus".equals(operation))this.op=GeneratorAdapter.SUB;
		else if("divide".equals(operation))this.op=GeneratorAdapter.DIV;
		else if("slash".equals(operation))this.op=GeneratorAdapter.DIV;
		else if("star".equals(operation))this.op=GeneratorAdapter.MUL;
		else if("times".equals(operation))this.op=GeneratorAdapter.MUL;
		else if("rem".equals(operation))this.op=GeneratorAdapter.REM;
		else if("remainder".equals(operation))this.op=GeneratorAdapter.REM;
		else if("binAnd".equals(operation))this.op=GeneratorAdapter.AND;
		else if("and".equals(operation))this.op=GeneratorAdapter.AND;
		else if("binOr".equals(operation))this.op=GeneratorAdapter.OR;
		else if("or".equals(operation))this.op=GeneratorAdapter.OR;
		else throw new RuntimeException("invalid operator ["+operation+"]");
		
	}

	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		Type l = Assign.writeOut(db,bc,null,mode,left,getLine(),false);
		Type r = Assign.writeOut(db,bc,null,mode,right,getLine(),false);
		Type t = result(l,r);
		
		bc.getAdapter().math(op,t);
		
		return t;
	}

	public static Type result(Type left, Type right) {
		if(left==Types.DOUBLE_VALUE || right==Types.DOUBLE_VALUE) return Types.DOUBLE_VALUE;
		if(left==Types.FLOAT_VALUE || right==Types.FLOAT_VALUE) return Types.FLOAT_VALUE;
		if(left==Types.LONG_VALUE || right==Types.LONG_VALUE) return Types.LONG_VALUE;
		if(left==Types.INT_VALUE || right==Types.INT_VALUE) return Types.INT_VALUE;
		if(left==Types.SHORT_VALUE || right==Types.SHORT_VALUE) return Types.SHORT_VALUE;
		
		return Types.CHAR;
	}

	public static void dup(BytecodeContext bc, Type t) {
		String cn=t.getClassName();
		if(cn.equals("long") || cn.equals("double")) bc.getAdapter().dup2();
		else bc.getAdapter().dup();
	}
	
}
