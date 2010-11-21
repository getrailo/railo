package railo.transformer.bytecode.statement.java;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import railo.print;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;

public class Assign extends ExpressionBase {

	private String name;
	private Object value;
	private DataBag db;
	private String operator;

	public Assign(int line,String name,Object value, String operator, DataBag db) {
		super(line);
		this.name=name;
		this.value=value;
		this.operator=operator;
		this.db = db;
	}

	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		Integer local=db.locals.get(name);
		if(local==null){
			throw new BytecodeException("there is no variable declaration for ["+name+"]", getLine());
		}
		Type t = bc.getAdapter().getLocalType(local.intValue());
		if("assign".equals(operator))writeOut(db,bc,t,mode,value,getLine());
		else{
			new Operation(getLine(), name, value, operator, db).writeOut(bc, mode);
		}
		dup(bc,t);
		bc.getAdapter().storeLocal(local.intValue(),t);
		
		return t;
	}

	public static void dup(BytecodeContext bc, Type t) {
		String cn=t.getClassName();
		if(cn.equals("long") || cn.equals("double")) bc.getAdapter().dup2();
		else bc.getAdapter().dup();
	}

	public static Type writeOut(DataBag db,BytecodeContext bc, Type to, int mode,Object value, int line) throws BytecodeException {
		Type from;
		if(value instanceof Expression)
			from=((Expression)value).writeOut(bc, mode);
		else {
			Integer var=db.locals.get(value);
			if(var==null)
				throw new BytecodeException("there is no variable with name ["+value+"] in the enviroment", line);
			from=bc.getAdapter().getLocalType(var.intValue());
			bc.getAdapter().loadLocal(var.intValue(),from);
			
		}
		print.o(from+"->"+to);
		if(to!=null && !from.equals(to)){
			box/unbox
			bc.getAdapter().cast(from, to);
		}
		return from;
	}
	
}
