package railo.transformer.bytecode.statement.java;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;

public class UnaryOp extends ExpressionBase {

	private DataBag db;
	private Object operant;
	private String operation;

	public UnaryOp(int line,Object operant, String operation, DataBag db) {
		super(line);
		this.operant=operant;
		this.operation=operation;
		this.db = db;
	}

	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		
		Integer var=db.locals.get(operant);
		if(var==null)
			throw new BytecodeException("there is no variable with name ["+operation+"] in the enviroment", getLine());
		
		GeneratorAdapter a = bc.getAdapter();
		
		if(operation.startsWith("pos")) a.loadLocal(var.intValue());
		if("preDecrement".equals(operation))a.iinc(var.intValue(), -1);
		else if("posDecrement".equals(operation))a.iinc(var.intValue(), -1);
		else if("preIncrement".equals(operation))a.iinc(var.intValue(), 1);
		else if("posIncrement".equals(operation))a.iinc(var.intValue(), 1);
		if(operation.startsWith("pre")) a.loadLocal(var.intValue());
		
		return a.getLocalType(var.intValue());
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
