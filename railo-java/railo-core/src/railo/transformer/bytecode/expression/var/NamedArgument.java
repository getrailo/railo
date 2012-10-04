package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.type.FunctionValueImpl;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;

public final class NamedArgument extends Argument {
	


	private static final Type TYPE_FUNCTION_VALUE=Type.getType(FunctionValueImpl.class);
    // railo.runtime.type.FunctionValue newInstance (String,Object)
   /* 
    private final static Method  NEW_INSTANCE = new Method("newInstance",
			Types.FUNCTION_VALUE,
			new Type[]{Types.STRING,Types.OBJECT});
	
    private final static Method  NEW_INSTANCE_ARR = new Method("newInstance",
			Types.FUNCTION_VALUE,
			new Type[]{Types.STRING_ARRAY,Types.OBJECT});
*/
    private static final int VALUE=0;
    private static final int ARRAY=1;
    private static final int KEY=0;
    private static final int STRING=1;
    
    private final static Method[][]  NEW_INSTANCE = new Method[][]{
    	new Method[]{
    			new Method("newInstance",Types.FUNCTION_VALUE,new Type[]{Types.COLLECTION_KEY,Types.OBJECT}),
    			new Method("newInstance",Types.FUNCTION_VALUE,new Type[]{Types.COLLECTION_KEY_ARRAY,Types.OBJECT})
    	},
    	new Method[]{
    			new Method("newInstance",Types.FUNCTION_VALUE,new Type[]{Types.STRING,Types.OBJECT}),
    			new Method("newInstance",Types.FUNCTION_VALUE,new Type[]{Types.STRING_ARRAY,Types.OBJECT})
    	}
    };
    	
    
    private Expression name;

	public NamedArgument(Expression name, Expression value, String type) {
		super(value,type);
		this.name=name;
	}

	/**
	 *
	 * @see railo.transformer.bytecode.expression.var.Argument#_writeOut(org.objectweb.asm.commons.GeneratorAdapter, int)
	 */
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		
		int form=VALUE;
		int type=STRING;
		if(name instanceof Variable) {
			GeneratorAdapter adapter = bc.getAdapter();
			String[] arr = VariableString.variableToStringArray((Variable) name,true);
			if(arr.length>1){
				form=ARRAY;
				ArrayVisitor av=new ArrayVisitor();
	            av.visitBegin(adapter,Types.STRING,arr.length);
	            for(int y=0;y<arr.length;y++){
	    			av.visitBeginItem(adapter, y);
	    				adapter.push(arr[y]);
	    			av.visitEndItem(bc.getAdapter());
	            }
	            av.visitEnd();
			}
			else {
				//VariableString.toExprString(name).writeOut(bc, MODE_REF);
				name=LitString.toExprString(VariableString.variableToString((Variable) name,true));
				type=Variable.registerKey(bc, VariableString.toExprString(name))?KEY:STRING;
			}
		}
		else  {
			//CastString.toExprString(name).writeOut(bc, MODE_REF);
			type=Variable.registerKey(bc, CastString.toExprString(name))?KEY:STRING;
			
		}
		//name.writeOut(bc, MODE_REF);
		super._writeOut(bc, MODE_REF);
		//bc.getAdapter().push(variableString);
		bc.getAdapter().invokeStatic(TYPE_FUNCTION_VALUE,NEW_INSTANCE[type][form]);
		return Types.FUNCTION_VALUE;
	}

	
	/**
	 * @see railo.transformer.bytecode.expression.var.Argument#writeOutValue(railo.transformer.bytecode.BytecodeContext, int)
	 */
	public Type writeOutValue(BytecodeContext bc, int mode)
			throws BytecodeException {
		// TODO Auto-generated method stub
		return super.writeOutValue(bc, mode);
	}


    /**
	 * @return the name
	 */
	public Expression getName() {
		return name;
	}
}
