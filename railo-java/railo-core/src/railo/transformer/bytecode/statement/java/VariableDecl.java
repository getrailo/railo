package railo.transformer.bytecode.statement.java;

import org.objectweb.asm.Type;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;

public class VariableDecl extends ExpressionBase {

	private Class type;
	private String name;
	private Object value;
	private DataBag db;

	public VariableDecl(int line,Class type,String name,Object value, DataBag db) {
		super(line);
		this.type=type;
		this.name=name;
		this.value=value;
		this.db = db;
		
	}

	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		Integer oLocal=db.locals.get(name);
		int local;
		Type t = null;
		if(oLocal==null){
			
				t = Type.getType(type);
				local =bc.getAdapter().newLocal(t);
				db.locals.put(name, Integer.valueOf(local));
			
		}
		else 
			throw new BytecodeException("there is already a variable declared with name ["+name+"]", getLine());
		
		
		//bc.getAdapter().visitLocalVariable(name, strType, null, db.start,db.end,x);
		
		if(value!=null){
			Type rtn = Assign.writeOut(db, bc, t,mode, value, getLine(),false);
			
			bc.getAdapter().storeLocal(local,t);
			return rtn;
		}
		
		return Types.VOID;
	}

	
	
}
