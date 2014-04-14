package railo.transformer.bytecode.expression.var;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.StringUtil;
import railo.commons.lang.types.RefInteger;
import railo.commons.lang.types.RefIntegerImpl;
import railo.runtime.exp.TemplateException;
import railo.runtime.op.Constants;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.ScopeSupport;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.UDFUtil;
import railo.runtime.util.CallerUtil;
import railo.runtime.util.VariableUtilImpl;
import railo.transformer.Context;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.cast.CastOther;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.TypeScope;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitString;
import railo.transformer.expression.var.DataMember;
import railo.transformer.expression.var.Member;
import railo.transformer.expression.var.Variable;
import railo.transformer.library.function.FunctionLibFunction;
import railo.transformer.library.function.FunctionLibFunctionArg;

public class VariableImpl extends ExpressionBase implements Variable {
	 

	private static final Type CALLER_UTIL = Type.getType(CallerUtil.class);

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

	final static Method INIT= new Method("init",
			Types.COLLECTION_KEY,
			new Type[]{Types.STRING});
	final static Method TO_KEY= new Method("toKey",
			Types.COLLECTION_KEY,
			new Type[]{Types.OBJECT});

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

    //public Object get(PageContext pc,Object coll, Key[] keys, Object defaultValue) {
    private final static Method CALLER_UTIL_GET = new Method("get",
			Types.OBJECT,
			new Type[]{Types.PAGE_CONTEXT,Types.OBJECT,Types.COLLECTION_KEY_ARRAY,Types.OBJECT});

    	
    // Object getCollection (Object,String)
    private final static Method GET_COLLECTION_KEY = new Method("getCollection",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY});
    // Object get (Object,String)
    private final static Method GET_KEY = new Method("get",
			Types.OBJECT,
			new Type[]{Types.OBJECT,Types.COLLECTION_KEY});
    

    
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

	private static final Method THIS_GET = new Method("thisGet",
			Types.OBJECT,
			new Type[]{});
	private static final Method THIS_TOUCH = new Method("thisTouch",
			Types.OBJECT,
			new Type[]{});

	private static final Method THIS_GET_EL = new Method("thisGet",
			Types.OBJECT,
			new Type[]{Types.OBJECT});
	private static final Method THIS_TOUCH_EL = new Method("thisTouch",
			Types.OBJECT,
			new Type[]{Types.OBJECT});
	
	private static final Type CONSTANTS = Type.getType(Constants.class);
    
    
	private int scope=Scope.SCOPE_UNDEFINED;
	List<Member> members=new ArrayList<Member>();
	int countDM=0;
	int countFM=0;
	private boolean ignoredFirstMember;

	private boolean fromHash=false;
	private Expression defaultValue;
	private Boolean asCollection;

	public VariableImpl(Factory factory,Position start,Position end) {
		super(factory,start,end);
	}
	
	public VariableImpl(Factory factory,int scope,Position start,Position end) {
		super(factory,start,end);
		this.scope=scope;
	}
	

	
	public Expression getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Expression defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Boolean getAsCollection() {
		return asCollection;
	}

	public void setAsCollection(Boolean asCollection) {
		this.asCollection = asCollection;
	}

	@Override
	public int getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
	
	@Override
	public void addMember(Member member) {
		if(member instanceof DataMember)countDM++;
		else countFM++;
		members.add(member);
	}
	
	@Override
	public final Type writeOutCollection(Context c, int mode) throws TransformerException {
		BytecodeContext bc=(BytecodeContext) c;
        ExpressionUtil.visitLine(bc, getStart());
    	Type type = _writeOut(bc,mode, Boolean.TRUE);
        ExpressionUtil.visitLine(bc, getEnd());
        return type;
    }

	@Override
	public Type _writeOut(BytecodeContext bc, int mode) throws TransformerException {
		if(defaultValue!=null && countFM==0 && countDM!=0)
			return _writeOutCallerUtil(bc, mode);
		return _writeOut(bc, mode, asCollection);
	}
	private Type _writeOut(BytecodeContext bc, int mode,Boolean asCollection) throws TransformerException {
		
		
		GeneratorAdapter adapter = bc.getAdapter();
		final int count=countFM+countDM;
		
		// count 0
        if(count==0) return _writeOutEmpty(bc);
       
    	boolean doOnlyScope=scope==Scope.SCOPE_LOCAL;
    	
    	
    	//boolean last;
    	for(int i=doOnlyScope?0:1;i<count;i++) {
			adapter.loadArg(0);
    	}
    	
    	Type rtn=_writeOutFirst(bc, (members.get(0)),mode,count==1,doOnlyScope,null,null);
		
		// pc.get(
		for(int i=doOnlyScope?0:1;i<count;i++) {
			Member member=(members.get(i));
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
						
						if(getFactory().registerKey(bc,name,false))adapter.invokeVirtual(Types.PAGE_CONTEXT,asCollection(asCollection, last)?GET_COLLECTION_KEY:GET_KEY);
						else adapter.invokeVirtual(Types.PAGE_CONTEXT,asCollection(asCollection, last)?GET_COLLECTION:GET);
					}
				}
				else{
					if(getFactory().registerKey(bc,name,false))adapter.invokeVirtual(Types.PAGE_CONTEXT,asCollection(asCollection, last)?GET_COLLECTION_KEY:GET_KEY);
					else adapter.invokeVirtual(Types.PAGE_CONTEXT,asCollection(asCollection, last)?GET_COLLECTION:GET);
				}
				rtn=Types.OBJECT;
			}

			// UDF
			else if(member instanceof UDF) {
				rtn= _writeOutUDF(bc,(UDF) member);
			}
    	}
    	return rtn;
	}
	
	private Type _writeOutCallerUtil(BytecodeContext bc, int mode) throws TransformerException {
		
		
		GeneratorAdapter adapter = bc.getAdapter();
		final int count=countFM+countDM;
		
		// count 0
        if(count==0) return _writeOutEmpty(bc);
       
    	
    	//boolean last;
    	/*for(int i=doOnlyScope?0:1;i<count;i++) {
			adapter.loadArg(0);
    	}*/
    	
        // pc
        //adapter.loadArg(0);
        adapter.loadArg(0);
        
        // collection
        RefInteger startIndex=new RefIntegerImpl();
    	_writeOutFirst(bc, (members.get(0)),mode,count==1,true,defaultValue,startIndex);
		
    	// keys
    	Iterator<Member> it = members.iterator();
    	ArrayVisitor av=new ArrayVisitor();
    	av.visitBegin(adapter,Types.COLLECTION_KEY,countDM-startIndex.toInt());
    	int index=0, i=0;
        while(it.hasNext()) {
        	DataMember member=(DataMember) it.next();
        	if(i++<startIndex.toInt()) continue;
			av.visitBeginItem(adapter, index++);
				getFactory().registerKey(bc,member.getName(),false);
			av.visitEndItem(bc.getAdapter());

    	}
        av.visitEnd();
        
        // defaultValue
        defaultValue.writeOut(bc, MODE_REF);
        
        bc.getAdapter().invokeStatic(CALLER_UTIL, CALLER_UTIL_GET);
        
    	return Types.OBJECT;
	}
	
	private boolean asCollection(Boolean asCollection, boolean last) {
		if(!last) return true;
		return asCollection!=null && asCollection.booleanValue();
	}

	
	/**
	 * outputs a empty Variable, only scope 
	 * Example: pc.formScope();
	 * @param adapter
	 * @throws TemplateException
	 */
	private Type _writeOutEmpty(BytecodeContext bc) throws TransformerException {
		if(ignoredFirstMember && (scope==Scope.SCOPE_LOCAL || scope==ScopeSupport.SCOPE_VAR)) 
			return Types.VOID;
		
		
		GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		Method m;
		Type t=Types.PAGE_CONTEXT;
		if(scope==Scope.SCOPE_ARGUMENTS) {
			getFactory().TRUE().writeOut(bc, MODE_VALUE);
			 m = TypeScope.METHOD_ARGUMENT_BIND;
		}
		else if(scope==Scope.SCOPE_LOCAL) {
			t=Types.PAGE_CONTEXT;
			getFactory().TRUE().writeOut(bc, MODE_VALUE);
			 m = TypeScope.METHOD_LOCAL_BIND;
		}
		else if(scope==ScopeSupport.SCOPE_VAR) {
			t=Types.PAGE_CONTEXT;
			getFactory().TRUE().writeOut(bc, MODE_VALUE);
			 m = TypeScope.METHOD_VAR_BIND;
		}
		else m = TypeScope.METHODS[scope]; 
		
		TypeScope.invokeScope(adapter,m,t);
		
		
		return m.getReturnType();
	}
	
	

	private Type _writeOutFirst(BytecodeContext bc, Member member, int mode, boolean last, boolean doOnlyScope, Expression defaultValue, RefInteger startIndex) throws TransformerException {
		
		if(member instanceof DataMember)
    		return _writeOutFirstDataMember(bc,(DataMember)member, scope,last , doOnlyScope,defaultValue,startIndex);
    	else if(member instanceof UDF)
    		return _writeOutFirstUDF(bc,(UDF)member,scope,doOnlyScope);
    	else
    		return _writeOutFirstBIF(bc,(BIF)member,mode,last,getStart());
	}
	
	static Type _writeOutFirstBIF(BytecodeContext bc, BIF bif, int mode,boolean last,Position line) throws TransformerException {
    	GeneratorAdapter adapter = bc.getAdapter();
		adapter.loadArg(0);
		// class
		Class<?> bifClass = bif.getClazz();
		Type bifType = Type.getType(bifClass);//Types.toType(bif.getClassName());
		Type rtnType=Types.toType(bif.getReturnType());
		if(rtnType==Types.VOID)rtnType=Types.STRING;
		
		// arguments
		Argument[] args = bif.getArguments();
		Type[] argTypes;
		// Arg Type FIX
		if(bif.getArgType()==FunctionLibFunction.ARG_FIX)	{
			
			if(isNamed(bif.getName(),args)) {
				NamedArgument[] nargs=toNamedArguments(args);
				
				String[] names=new String[nargs.length];
				// get all names
				for(int i=0;i<nargs.length;i++){
					names[i] = getName(nargs[i].getName());
				}
				
				
				ArrayList<FunctionLibFunctionArg> list = bif.getFlf().getArg();
				Iterator<FunctionLibFunctionArg> it = list.iterator();
				
				argTypes=new Type[list.size()+1];
				argTypes[0]=Types.PAGE_CONTEXT;
				
				FunctionLibFunctionArg flfa;
				int index=0;
				VT vt;
				while(it.hasNext()) {
					flfa =it.next();
					vt = getMatchingValueAndType(bc.getFactory(),flfa,nargs,names,line);
					if(vt.index!=-1) 
						names[vt.index]=null;
					argTypes[++index]=Types.toType(vt.type);
					if(vt.value==null)ASMConstants.NULL(bc.getAdapter());
					else vt.value.writeOut(bc, Types.isPrimitiveType(argTypes[index])?MODE_VALUE:MODE_REF);
				}
				
				for(int y=0;y<names.length;y++){
					if(names[y]!=null) {
						TransformerException bce = new TransformerException("argument ["+names[y]+"] is not allowed for function ["+bif.getFlf().getName()+"]", args[y].getStart());
						UDFUtil.addFunctionDoc(bce, bif.getFlf());
						throw bce;
					}
				}
				
			}
			else{
				argTypes=new Type[args.length+1];
				argTypes[0]=Types.PAGE_CONTEXT;
				
				
				for(int y=0;y<args.length;y++) {
					argTypes[y+1]=Types.toType(args[y].getStringType());
					args[y].writeOutValue(bc, Types.isPrimitiveType(argTypes[y+1])?MODE_VALUE:MODE_REF);
				}
				// if no method exists for the exact match of arguments, call the method with all arguments (when exists)
				if(methodExists(bifClass,"call",argTypes,rtnType)==Boolean.FALSE) {
					ArrayList<FunctionLibFunctionArg> _args = bif.getFlf().getArg();
					
					Type[] tmp = new Type[_args.size()+1];
					
					// fill the existing
					for(int i=0;i<argTypes.length;i++){
						tmp[i]=argTypes[i];
					}
					
					// get the rest with default values
					FunctionLibFunctionArg flfa;
					for(int i=argTypes.length;i<tmp.length;i++){
						flfa = _args.get(i-1);
						tmp[i]=Types.toType(flfa.getTypeAsString());
						getDefaultValue(bc.getFactory(),flfa).value.writeOut(
								bc, 
								Types.isPrimitiveType(tmp[i])?MODE_VALUE:MODE_REF);
					}
					argTypes=tmp;
				}
 				
			}
			
		}
		// Arg Type DYN
		else	{
			
			argTypes=new Type[2];
			argTypes[0]=Types.PAGE_CONTEXT;
			argTypes[1]=Types.OBJECT_ARRAY;
			ExpressionUtil.writeOutExpressionArray(bc, Types.OBJECT, args);	
		}
		adapter.invokeStatic(bifType,new Method("call",rtnType,argTypes));
		if(mode==MODE_REF || !last) {
			if(Types.isPrimitiveType(rtnType)) {
				adapter.invokeStatic(Types.CASTER,new Method("toRef",Types.toRefType(rtnType),new Type[]{rtnType}));
				rtnType=Types.toRefType(rtnType);
			}
		}
		return rtnType;
	}
		
	

	

	
	/**
	 * checks if a method exists
	 * @param clazz
	 * @param methodName
	 * @param args
	 * @param returnType
	 * @return returns null when checking fi
	 */

	private static Boolean methodExists(Class clazz, String methodName, Type[] args, Type returnType)  {
		try {
			//Class _clazz=Types.toClass(clazz);
			Class<?>[] _args=new Class[args.length];
			for(int i=0;i<_args.length;i++){
				_args[i]=Types.toClass(args[i]);
			}
			Class<?> rtn = Types.toClass(returnType);
		
			try {
				java.lang.reflect.Method m = clazz.getMethod(methodName, _args);
				return m.getReturnType()==rtn;
			}
			catch (Exception e) {
				return false;
			}
			
		}
		catch (Exception e) {e.printStackTrace();
			return null;
		}
	}

	static Type _writeOutFirstUDF(BytecodeContext bc, UDF udf, int scope, boolean doOnlyScope) throws TransformerException {

    	GeneratorAdapter adapter = bc.getAdapter();
		// pc.getFunction (Object,String,Object[])
	    // pc.getFunctionWithNamedValues (Object,String,Object[])
		adapter.loadArg(0);
		
		if(!doOnlyScope)adapter.loadArg(0);
		Type rtn = TypeScope.invokeScope(adapter, scope);
		if(doOnlyScope) return rtn;
		
		
		return _writeOutUDF(bc,udf);
	}

	private static Type _writeOutUDF(BytecodeContext bc, UDF udf) throws TransformerException {
		bc.getFactory().registerKey(bc,udf.getName(),false);
		Argument[] args = udf.getArguments();
		
		// no arguments
		if(args.length==0) {
			bc.getAdapter().getStatic(CONSTANTS, "EMPTY_OBJECT_ARRAY", Types.OBJECT_ARRAY);
		}
		else ExpressionUtil.writeOutExpressionArray(bc, Types.OBJECT, args);
		bc.getAdapter().invokeVirtual(Types.PAGE_CONTEXT,udf.hasNamedArgs()?GET_FUNCTION_WITH_NAMED_ARGS_KEY:GET_FUNCTION_KEY);
		return Types.OBJECT;
	}

	Type _writeOutFirstDataMember(BytecodeContext bc, DataMember member, int scope, boolean last, boolean doOnlyScope, Expression defaultValue, RefInteger startIndex) throws TransformerException {
    	GeneratorAdapter adapter = bc.getAdapter();
    	if(startIndex!=null)startIndex.setValue(doOnlyScope?0:1);
		
    	// this
    	if(scope==Scope.SCOPE_UNDEFINED) {
    		ExprString name = member.getName();
    		if(ASMUtil.isDotKey(name)){
    			LitString ls = (LitString)name;
				if(ls.getString().equalsIgnoreCase("THIS")){
					if(startIndex!=null)startIndex.setValue(1);
					adapter.loadArg(0);
					adapter.checkCast(Types.PAGE_CONTEXT_IMPL);
					if(defaultValue!=null) {
						defaultValue.writeOut(bc, MODE_REF);
						adapter.invokeVirtual(Types.PAGE_CONTEXT_IMPL,(countFM+countDM)==1?THIS_GET_EL:THIS_TOUCH_EL);
					}
					else adapter.invokeVirtual(Types.PAGE_CONTEXT_IMPL,(countFM+countDM)==1?THIS_GET:THIS_TOUCH);
					return Types.OBJECT;
				}
    		}
    	}
    	// local
    	Type rtn;
    	if(scope==Scope.SCOPE_LOCAL && defaultValue!=null) {
    		adapter.loadArg(0);
    		adapter.checkCast(Types.PAGE_CONTEXT_IMPL);
    		getFactory().FALSE().writeOut(bc, MODE_VALUE);
    		defaultValue.writeOut(bc, MODE_VALUE);
    		adapter.invokeVirtual(Types.PAGE_CONTEXT_IMPL, TypeScope.METHOD_LOCAL_EL);
    		rtn= Types.OBJECT;
    	}
    	else {
    		adapter.loadArg(0);
    		rtn = TypeScope.invokeScope(adapter, scope);
    	}
		if(doOnlyScope) return rtn;
		
		if(getFactory().registerKey(bc,member.getName(),false))
    		adapter.invokeInterface(TypeScope.SCOPES[scope],!last && scope==Scope.SCOPE_UNDEFINED?METHOD_SCOPE_GET_COLLECTION_KEY:METHOD_SCOPE_GET_KEY);
		else
			adapter.invokeInterface(TypeScope.SCOPES[scope],!last && scope==Scope.SCOPE_UNDEFINED?METHOD_SCOPE_GET_COLLECTION:METHOD_SCOPE_GET);
    	return Types.OBJECT;
	}

	@Override
	public List<Member> getMembers() {
		return members;
	}

	@Override
	public Member getFirstMember() {
		if(members.isEmpty()) return null;
		return members.get(0);
	}

	@Override
	public Member getLastMember() {
		if(members.isEmpty()) return null;
		return members.get(members.size()-1);
	}

	@Override
	public void ignoredFirstMember(boolean b) {
		this.ignoredFirstMember=b;
	}
	
	@Override
	public boolean ignoredFirstMember() {
		return ignoredFirstMember;
	}
	
	
	

	private static VT getMatchingValueAndType(Factory factory,FunctionLibFunctionArg flfa, NamedArgument[] nargs,String[] names, Position line) throws TransformerException {
		String flfan=flfa.getName();
		
		// first search if a argument match
		for(int i=0;i<nargs.length;i++){
			if(names[i]!=null && names[i].equalsIgnoreCase(flfan)) {
				nargs[i].setValue(nargs[i].getRawValue(),flfa.getTypeAsString());
				return new VT(nargs[i].getValue(),flfa.getTypeAsString(),i);
			}
		}
		
		// then check if a alias match
		String alias=flfa.getAlias();
		if(!StringUtil.isEmpty(alias)) {
			//String[] arrAlias = railo.runtime.type.List.toStringArray(railo.runtime.type.List.trimItems(railo.runtime.type.List.listToArrayRemoveEmpty(alias, ',')));
			for(int i=0;i<nargs.length;i++){
				if(names[i]!=null && railo.runtime.type.util.ListUtil.listFindNoCase(alias, names[i])!=-1){
					nargs[i].setValue(nargs[i].getRawValue(),flfa.getTypeAsString());
					return new VT(nargs[i].getValue(),flfa.getTypeAsString(),i);
				}
			}
		}
		
		// if not required return the default value
		if(!flfa.getRequired()) {
			return getDefaultValue(factory,flfa);
		}
		TransformerException be = new TransformerException("missing required argument ["+flfan+"] for function ["+flfa.getFunction().getName()+"]",line);
		UDFUtil.addFunctionDoc(be, flfa.getFunction());
		throw be;
	}
	
	private static VT getDefaultValue(Factory factory,FunctionLibFunctionArg flfa) {
		String defaultValue = flfa.getDefaultValue();
		String type = flfa.getTypeAsString();
		if(defaultValue==null) {
			if(type.equals("boolean") || type.equals("bool")) 
				return new VT(factory.FALSE(),type,-1);
			if(type.equals("number") || type.equals("numeric") || type.equals("double")) 
				return new VT(factory.DOUBLE_ZERO(),type,-1);
			return new VT(null,type,-1);
		}
		return new VT(CastOther.toExpression(factory.createLitString(defaultValue), type),type,-1);
	}

	private static String getName(Expression expr) throws TransformerException {
		String name = ASMUtil.toString(expr);
		if(name==null) throw new TransformerException("cannot extract a string from a object of type ["+expr.getClass().getName()+"]",null);
		return name;
	}

	/**
	 * translate a array of arguments to a araay of NamedArguments, attention no check if the elements are really  named arguments
	 * @param args
	 * @return
	 */
	private static NamedArgument[] toNamedArguments(Argument[] args) {
		NamedArgument[] nargs=new NamedArgument[args.length];
		for(int i=0;i<args.length;i++){
			nargs[i]=(NamedArgument) args[i];
		}
		
		return nargs;
	}

	

	/**
	 * check if the arguments are named arguments or regular arguments, throws a exception when mixed
	 * @param funcName
	 * @param args
	 * @param line
	 * @return
	 * @throws TransformerException
	 */
	private static  boolean isNamed(Object funcName,Argument[] args) throws TransformerException {
		if(ArrayUtil.isEmpty(args)) return false;
		boolean named=false;
		for(int i=0;i<args.length;i++){
			if(args[i] instanceof NamedArgument)named=true;
			else if(named)
				throw new TransformerException("invalid argument for function "+funcName+", you can not mix named and unnamed arguments", args[i].getStart());
		}
		
		
		return named;
	}

	@Override
	public void fromHash(boolean fromHash) {
		this.fromHash=fromHash;
	}

	@Override
	public boolean fromHash() {
		return fromHash;
	}
	
	@Override
	public int getCount() {
		return countDM+countFM;
	}
	
}

class VT{
	Expression value;
	String type;
	int index;

	public VT(Expression value, String type, int index) {
		this.value=value;
		this.type=type;
		this.index=index;
	}
}
