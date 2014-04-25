package railo.transformer.bytecode.expression.var;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.ScopeFactory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.TypeScope;
import railo.transformer.bytecode.util.Types;
import railo.transformer.expression.Expression;
import railo.transformer.expression.var.DataMember;
import railo.transformer.expression.var.Member;
import railo.transformer.expression.var.Variable;

public class Assign extends ExpressionBase {

	//final static Method[] METHODS_SCOPE_SET = new Method[6];
	


//  java.lang.Object set(String,Object)
    private final static Method METHOD_SCOPE_SET = new Method("set",
			Types.OBJECT,
			new Type[]{Types.STRING,Types.OBJECT});
    
//  java.lang.Object set(String,Object)
    private final static Method METHOD_SCOPE_SET_KEY = new Method("set",
			Types.OBJECT,
			new Type[]{Types.COLLECTION_KEY,Types.OBJECT});
	
// .setArgument(obj)
    private final static Method SET_ARGUMENT = new Method("setArgument",
			Types.OBJECT,
			new Type[]{Types.OBJECT});
    
    
    // Object touch (Object,String)
    private final static Method TOUCH = new Method("touch",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING});

    // Object touch (Object,String)
    private final static Method TOUCH_KEY = new Method("touch",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY});
    

    //Object set (Object,String,Object)
    private final static Method SET_KEY = new Method("set",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY,Types.OBJECT});
    
    //Object set (Object,String,Object)
    private final static Method SET = new Method("set",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING,Types.OBJECT});

    // Object getFunction (Object,String,Object[])
    private final static Method GET_FUNCTION = new Method("getFunction",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING,Types.OBJECT_ARRAY});
    
    // Object getFunctionWithNamedValues (Object,String,Object[])
    private final static Method GET_FUNCTION_WITH_NAMED_ARGS = new Method("getFunctionWithNamedValues",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING,Types.OBJECT_ARRAY});
	

    // Object getFunction (Object,String,Object[])
    private final static Method GET_FUNCTION_KEY = new Method("getFunction",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY,Types.OBJECT_ARRAY});
    
    // Object getFunctionWithNamedValues (Object,String,Object[])
    private final static Method GET_FUNCTION_WITH_NAMED_ARGS_KEY = new Method("getFunctionWithNamedValues",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY,Types.OBJECT_ARRAY});
	
	
	private Variable variable;
	private Expression value;


	/**
	 * Constructor of the class
	 * @param variable
	 * @param value
	 */
	public Assign(Variable variable, Expression value, Position end) {
		super(variable.getFactory(),variable.getStart(),end);
		this.variable=variable;
		this.value=value;
		//this.returnOldValue=returnOldValue;
	}
	

	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
    	GeneratorAdapter adapter = bc.getAdapter();
		int count=variable.getCount();
        // count 0
        if(count==0){
        	if(variable.ignoredFirstMember() && variable.getScope()==Scope.SCOPE_VAR){
    			//print.dumpStack();
        		return Types.VOID;
    		}
        	return _writeOutEmpty(bc);
        }
        
        boolean doOnlyScope=variable.getScope()==Scope.SCOPE_LOCAL;
    	
    	Type rtn=Types.OBJECT;
    	//boolean last;
    	for(int i=doOnlyScope?0:1;i<count;i++) {
			adapter.loadArg(0);
    	}
		rtn=_writeOutFirst(bc, (variable.getMembers().get(0)),mode,count==1,doOnlyScope);
    	
		// pc.get(
		for(int i=doOnlyScope?0:1;i<count;i++) {
			Member member=(variable.getMembers().get(i));
			boolean last=(i+1)==count;
			
			
			// Data Member
			if(member instanceof DataMember)	{
				//((DataMember)member).getName().writeOut(bc, MODE_REF);
    			boolean isKey=getFactory().registerKey(bc, ((DataMember)member).getName(),false);
				
    			if(last)value.writeOut(bc, MODE_REF);
    			if(isKey)adapter.invokeVirtual(Types.PAGE_CONTEXT,last?SET_KEY:TOUCH_KEY);
    			else adapter.invokeVirtual(Types.PAGE_CONTEXT,last?SET:TOUCH);
        		rtn=Types.OBJECT;
			}
			
			// UDF
			else if(member instanceof UDF) {
				if(last)throw new TransformerException("can't asign value to a user defined function",getStart());
				UDF udf=(UDF) member;
				boolean isKey=getFactory().registerKey(bc, udf.getName(),false);
				//udf.getName().writeOut(bc, MODE_REF);
				ExpressionUtil.writeOutExpressionArray(bc, Types.OBJECT, udf.getArguments());
				
				if(isKey)adapter.invokeVirtual(Types.PAGE_CONTEXT,udf.hasNamedArgs()?GET_FUNCTION_WITH_NAMED_ARGS_KEY:GET_FUNCTION_KEY);
				else adapter.invokeVirtual(Types.PAGE_CONTEXT,udf.hasNamedArgs()?GET_FUNCTION_WITH_NAMED_ARGS:GET_FUNCTION);
				rtn=Types.OBJECT;
			}
    	}
    	return rtn;
	}

	private Type _writeOutFirst(BytecodeContext bc, Member member, int mode, boolean last, boolean doOnlyScope) throws TransformerException {
		
		if(member instanceof DataMember) {
			return _writeOutOneDataMember(bc,(DataMember)member,last,doOnlyScope);
			//return Variable._writeOutFirstDataMember(adapter,(DataMember)member,variable.scope, last);
		}
    	else if(member instanceof UDF) {
    		if(last)throw new TransformerException("can't assign value to a user defined function",getStart());
    		return VariableImpl._writeOutFirstUDF(bc,(UDF)member,variable.getScope(),doOnlyScope);
    	}
    	else {
    		if(last)throw new TransformerException("can't assign value to a built in function",getStart());
    		return VariableImpl._writeOutFirstBIF(bc,(BIF)member,mode,last,getStart());
    	}
	}



	private Type _writeOutOneDataMember(BytecodeContext bc, DataMember member,boolean last, boolean doOnlyScope) throws TransformerException {
    	GeneratorAdapter adapter = bc.getAdapter();
		
    	if(doOnlyScope){
    		adapter.loadArg(0);
    		if(variable.getScope()==Scope.SCOPE_LOCAL){
    			return TypeScope.invokeScope(adapter, TypeScope.METHOD_LOCAL_TOUCH,Types.PAGE_CONTEXT);
    		}
    		return TypeScope.invokeScope(adapter, variable.getScope());
    	}
    	
    	// pc.get
		adapter.loadArg(0);
		if(last) {
			TypeScope.invokeScope(adapter, variable.getScope());
			//adapter.invokeVirtual(Types.PAGE_CONTEXT,TypeScope.METHODS[variable.scope]);
			
			boolean isKey=getFactory().registerKey(bc, member.getName(),false);
			value.writeOut(bc, MODE_REF);
			
			if(isKey)adapter.invokeInterface(TypeScope.SCOPES[variable.getScope()],METHOD_SCOPE_SET_KEY);
			else adapter.invokeInterface(TypeScope.SCOPES[variable.getScope()],METHOD_SCOPE_SET);
			
		}
		else {
			adapter.loadArg(0);
			TypeScope.invokeScope(adapter, variable.getScope());
			if(getFactory().registerKey(bc, member.getName(),false))
    			adapter.invokeVirtual(Types.PAGE_CONTEXT,TOUCH_KEY);
    		else
    			adapter.invokeVirtual(Types.PAGE_CONTEXT,TOUCH);
		}
		return Types.OBJECT;
		
		
	}

	private Type _writeOutEmpty(BytecodeContext bc) throws TransformerException {
		GeneratorAdapter adapter = bc.getAdapter();

		if(variable.getScope()==Scope.SCOPE_ARGUMENTS) {
			adapter.loadArg(0);
			TypeScope.invokeScope(adapter, Scope.SCOPE_ARGUMENTS);
			value.writeOut(bc, MODE_REF);
			adapter.invokeInterface(TypeScope.SCOPE_ARGUMENT,SET_ARGUMENT);
		}
		else {
			adapter.loadArg(0);
			TypeScope.invokeScope(adapter, Scope.SCOPE_UNDEFINED);
			getFactory().registerKey(bc,bc.getFactory().createLitString(ScopeFactory.toStringScope(variable.getScope(),"undefined")),false);
			value.writeOut(bc, MODE_REF);
			adapter.invokeInterface(TypeScope.SCOPES[Scope.SCOPE_UNDEFINED],METHOD_SCOPE_SET_KEY);
		}
		
		
		return Types.OBJECT;
	}

	/**
	 * @return the value
	 */
	public Expression getValue() {
		return value;
	}


	/**
	 * @return the variable
	 */
	public Variable getVariable() {
		return variable;
	}
}
