package railo.transformer.bytecode.expression.var;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.exp.TemplateException;
import railo.runtime.type.Scope;
import railo.runtime.type.scope.ScopeSupport;
import railo.runtime.util.VariableUtilImpl;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.expression.Invoker;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.TypeScope;
import railo.transformer.bytecode.util.Types;
import railo.transformer.library.function.FunctionLibFunction;

public class Variable extends ExpressionBase implements Invoker {
	 


	// java.lang.Object get(java.lang.String)
	final static Method METHOD_SCOPE_GET_KEY = new Method("get",
			Types.OBJECT,
			new Type[]{Types.COLLECTION_KEY});
	// Object getCollection(java.lang.String)
	final static Method METHOD_SCOPE_GET_COLLECTION_KEY= new Method("getCollection",
			Types.OBJECT,
			new Type[]{Types.COLLECTION_KEY});

	// java.lang.Object get(java.lang.String)
	final static Method METHOD_SCOPE_GET = new Method("get",
			Types.OBJECT,
			new Type[]{Types.STRING});
	// Object getCollection(java.lang.String)
	final static Method METHOD_SCOPE_GET_COLLECTION= new Method("getCollection",
			Types.OBJECT,
			new Type[]{Types.STRING});

    final static Method[] METHODS_SCOPE_GET = new Method[6];
    static {
	    METHODS_SCOPE_GET[0] = METHOD_SCOPE_GET;
	    METHODS_SCOPE_GET[1] = new Method("get",Types.OBJECT,new Type[]{Types.SCOPE,Types.STRING,Types.STRING}); 
	    METHODS_SCOPE_GET[2] = new Method("get",Types.OBJECT,new Type[]{Types.SCOPE,Types.STRING,Types.STRING,Types.STRING});
	    METHODS_SCOPE_GET[3] = new Method("get",Types.OBJECT,new Type[]{Types.SCOPE,Types.STRING,Types.STRING,Types.STRING,Types.STRING});
	    METHODS_SCOPE_GET[4] = new Method("get",Types.OBJECT,new Type[]{Types.SCOPE,Types.STRING,Types.STRING,Types.STRING,Types.STRING,Types.STRING});
	    METHODS_SCOPE_GET[5] = new Method("get",Types.OBJECT,new Type[]{Types.SCOPE,Types.STRING,Types.STRING,Types.STRING,Types.STRING,Types.STRING,Types.STRING});
    }
    
    // Object getCollection (Object,String)
    private final static Method GET_COLLECTION = new Method("getCollection",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING});
    // Object get (Object,String)
    private final static Method GET = new Method("get",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.STRING});

    
    // Object getCollection (Object,String)
    private final static Method GET_COLLECTION_KEY = new Method("getCollection",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY});
    // Object get (Object,String)
    private final static Method GET_KEY = new Method("get",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY});
    

    
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
	
	private static final Type VARIABLE_UTIL_IMPL = Type.getType(VariableUtilImpl.class);
	
    private static final Method RECORDCOUNT = new Method("recordcount",
			Types.OBJECT,
			new Type[]{Types.PAGE_CONTEXT,Types.OBJECT});
	private static final Method CURRENTROW = new Method("currentrow",
			Types.OBJECT,
			new Type[]{Types.PAGE_CONTEXT,Types.OBJECT});
	private static final Method COLUMNLIST = new Method("columnlist",
			Types.OBJECT,
			new Type[]{Types.PAGE_CONTEXT,Types.OBJECT});
    
    
	int scope=Scope.SCOPE_UNDEFINED;
	List members=new ArrayList();
	int countDM=0;
	int countFM=0;
	private boolean ignoredFirstMember;

	public Variable(int line) {
		super(line);
	}
	
	public Variable(int scope,int line) {
		super(line);
		this.scope=scope;
	}
	
	/**
	 * @return the scope
	 */
	public int getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
	
	public void addMember(Member member) {
		if(member instanceof DataMember)countDM++;
		else countFM++;
		members.add(member);
	}

	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		
		
		GeneratorAdapter adapter = bc.getAdapter();
		int count=countFM+countDM;
		
		// count 0
        if(count==0) 						return _writeOutEmpty(bc);
       
    	boolean doOnlyScope=scope==Scope.SCOPE_LOCAL;
    	
    	Type rtn=Types.OBJECT;
    	//boolean last;
    	for(int i=doOnlyScope?0:1;i<count;i++) {
			adapter.loadArg(0);
    	}
    	
		rtn=_writeOutFirst(bc, ((Member)members.get(0)),mode,count==1,doOnlyScope);
		
		// pc.get(
		for(int i=doOnlyScope?0:1;i<count;i++) {
			Member member=((Member)members.get(i));
			boolean last=(i+1)==count;
			
			// Data Member
			if(member instanceof DataMember)	{
				ExprString name = ((DataMember)member).getName();
				
				if(last && ASMUtil.isDotKey(name)){
					LitString ls = (LitString)name;
					if(ls.getString().equalsIgnoreCase("RECORDCOUNT")){
						adapter.invokeStatic(VARIABLE_UTIL_IMPL, RECORDCOUNT);
					}
					else if(ls.getString().equalsIgnoreCase("CURRENTROW")){
						adapter.invokeStatic(VARIABLE_UTIL_IMPL, CURRENTROW);
					}
					else if(ls.getString().equalsIgnoreCase("COLUMNLIST")){
						adapter.invokeStatic(VARIABLE_UTIL_IMPL, COLUMNLIST);
					}
					else {
						if(registerKey(bc,name))adapter.invokeVirtual(Types.PAGE_CONTEXT,last?GET_KEY:GET_COLLECTION_KEY);
						else adapter.invokeVirtual(Types.PAGE_CONTEXT,last?GET:GET_COLLECTION);
					}
				}
				else{
					if(registerKey(bc,name))adapter.invokeVirtual(Types.PAGE_CONTEXT,last?GET_KEY:GET_COLLECTION_KEY);
					else adapter.invokeVirtual(Types.PAGE_CONTEXT,last?GET:GET_COLLECTION);
				}
				rtn=Types.OBJECT;
			}

			// UDF
			else if(member instanceof UDF) {
				UDF udf=(UDF) member;
				boolean isKey=registerKey(bc,udf.getName());
				//udf.getName().writeOut(bc, MODE_REF);
				ExpressionUtil.writeOutExpressionArray(bc, Types.OBJECT, udf.getArguments());
				
				if(isKey) adapter.invokeVirtual(Types.PAGE_CONTEXT,udf.hasNamedArgs()?GET_FUNCTION_WITH_NAMED_ARGS_KEY:GET_FUNCTION_KEY);
				else adapter.invokeVirtual(Types.PAGE_CONTEXT,udf.hasNamedArgs()?GET_FUNCTION_WITH_NAMED_ARGS:GET_FUNCTION);
				rtn=Types.OBJECT;
			}
    	}
    	return rtn;
	}
	
	public static boolean registerKey(BytecodeContext bc,Expression name) throws BytecodeException {
		return registerKey(bc, name, false);
	}
	
	public static boolean registerKey(BytecodeContext bc,Expression name,boolean doUpperCase) throws BytecodeException {
		
		if(name instanceof LitString) {
			LitString lit = (LitString)name;
			if(doUpperCase){
				lit=lit.duplicate();
				lit.upperCase();
			}
			String key=bc.registerKey(lit);
			bc.getAdapter().visitFieldInsn(Opcodes.GETSTATIC, bc.getClassName(), key, "Lrailo/runtime/type/Collection$Key;");
			return true;
		}
		name.writeOut(bc, MODE_REF);
		return false;
	}

	
	/**
	 * outputs a empty Variable, only scope 
	 * Example: pc.formScope();
	 * @param adapter
	 * @throws TemplateException
	 */
	private Type _writeOutEmpty(BytecodeContext bc) throws BytecodeException {
		if(ignoredFirstMember && (scope==Scope.SCOPE_LOCAL || scope==ScopeSupport.SCOPE_VAR)) 
			return Types.VOID;
		
		
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		Method m;
		Type t=Types.PAGE_CONTEXT;
		if(scope==Scope.SCOPE_ARGUMENTS) {
			LitBoolean.TRUE.writeOut(bc, MODE_VALUE);
			 m = TypeScope.METHOD_ARGUMENT_BIND;
		}
		else if(scope==Scope.SCOPE_LOCAL) {
			adapter.checkCast(Types.PAGE_CONTEXT_IMPL);// FUTURE remove when function localScope(boolean) is part of class PageContext
			t=Types.PAGE_CONTEXT_IMPL;
			LitBoolean.TRUE.writeOut(bc, MODE_VALUE);
			 m = TypeScope.METHOD_LOCAL_BIND;
		}
		else if(scope==ScopeSupport.SCOPE_VAR) {
			adapter.checkCast(Types.PAGE_CONTEXT_IMPL);// FUTURE remove when function localScope(boolean) is part of class PageContext
			t=Types.PAGE_CONTEXT_IMPL;
			LitBoolean.TRUE.writeOut(bc, MODE_VALUE);
			 m = TypeScope.METHOD_VAR_BIND;
		}
		else m = TypeScope.METHODS[scope]; 
		
		TypeScope.invokeScope(adapter,m,t);
		
		
		return m.getReturnType();
	}
	
	

	private Type _writeOutFirst(BytecodeContext bc, Member member, int mode, boolean last, boolean doOnlyScope) throws BytecodeException {
    	if(member instanceof DataMember)
    		return _writeOutFirstDataMember(bc,(DataMember)member, scope,last , doOnlyScope);
    	else if(member instanceof UDF)
    		return _writeOutFirstUDF(bc,(UDF)member,scope,doOnlyScope);
    	else
    		return _writeOutFirstBIF(bc,(BIF)member,mode,last);
	}
	
	static Type _writeOutFirstBIF(BytecodeContext bc, BIF bif, int mode,boolean last) throws BytecodeException {
    	GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		// class
		Type bifClass = Types.toType(bif.getClassName());
		
		// arguments
		Argument[] args = bif.getArguments();
		Type[] argTypes;
		// Arg Type FIX
		if(bif.getArgType()==FunctionLibFunction.ARG_FIX)	{
			argTypes=new Type[args.length+1];
			argTypes[0]=Types.PAGE_CONTEXT;
			for(int y=0;y<args.length;y++) {
				argTypes[y+1]=Types.toType(args[y].getStringType());
				args[y].writeOutValue(bc, Types.isPrimitiveType(argTypes[y+1])?MODE_VALUE:MODE_REF);
				//print.err(argTypes[y+1]);
				//print.err("->"+args[y].getStringType());
			}
		}
		// Arg Type DYN
		else	{
			
			argTypes=new Type[2];
			argTypes[0]=Types.PAGE_CONTEXT;
			argTypes[1]=Types.OBJECT_ARRAY;
			ExpressionUtil.writeOutExpressionArray(bc, Types.OBJECT, args);	
		}
		
		// return type
		Type rtnType=Types.toType(bif.getReturnType());
		if(rtnType==Types.VOID)rtnType=Types.STRING;
		adapter.	invokeStatic(bifClass,new Method("call",rtnType,argTypes));
		
		
		if(mode==MODE_REF || !last) {
			if(Types.isPrimitiveType(rtnType)) {
				adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtnType),new Type[]{rtnType}));
				rtnType=Types.toRefType(rtnType);
			}
		}
		return rtnType;
	}
		
	

	static Type _writeOutFirstUDF(BytecodeContext bc, UDF udf, int scope, boolean doOnlyScope) throws BytecodeException {

    	GeneratorAdapter adapter = bc.getAdapter();
		// pc.getFunction (Object,String,Object[])
	    // pc.getFunctionWithNamedValues (Object,String,Object[])
		adapter.loadArg(0);
		if(!doOnlyScope)adapter.loadArg(0);
		Type rtn = TypeScope.invokeScope(adapter, scope);
		if(doOnlyScope) return rtn;
		
		boolean isKey=registerKey(bc,udf.getName());
		ExpressionUtil.writeOutExpressionArray(bc, Types.OBJECT, udf.getArguments());
		if(isKey) adapter.invokeVirtual(Types.PAGE_CONTEXT,udf.hasNamedArgs()?GET_FUNCTION_WITH_NAMED_ARGS_KEY:GET_FUNCTION_KEY);
		else adapter.invokeVirtual(Types.PAGE_CONTEXT,udf.hasNamedArgs()?GET_FUNCTION_WITH_NAMED_ARGS:GET_FUNCTION);
		return Types.OBJECT;
		
	}

	static Type _writeOutFirstDataMember(BytecodeContext bc, DataMember member, int scope, boolean last, boolean doOnlyScope) throws BytecodeException {
    	
		
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		Type rtn = TypeScope.invokeScope(adapter, scope);
		if(doOnlyScope) return rtn;
		
		if(registerKey(bc,member.getName()))
    		adapter.invokeInterface(TypeScope.SCOPES[scope],!last && scope==Scope.SCOPE_UNDEFINED?METHOD_SCOPE_GET_COLLECTION_KEY:METHOD_SCOPE_GET_KEY);
		else
			adapter.invokeInterface(TypeScope.SCOPES[scope],!last && scope==Scope.SCOPE_UNDEFINED?METHOD_SCOPE_GET_COLLECTION:METHOD_SCOPE_GET);
    	return Types.OBJECT;
	}
	
	

	/**
	 * @return the members
	 */
	public List getMembers() {
		return members;
	}

	/**
	 * @return the first member or null if there no member
	 */
	public Member getFirstMember() {
		if(members.isEmpty()) return null;
		return (Member) members.get(0);
	}

	/**
	 * @return the first member or null if there no member
	 */
	public Member getLastMember() {
		if(members.isEmpty()) return null;
		return (Member) members.get(members.size()-1);
	}

	public void ignoredFirstMember(boolean b) {
		this.ignoredFirstMember=b;
	}
	public boolean ignoredFirstMember() {
		return ignoredFirstMember;
	}
	
}
