package railo.transformer.bytecode.statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.exp.TemplateException;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.FunctionArgumentImpl;
import railo.runtime.type.FunctionArgumentLight;
import railo.runtime.type.util.ComponentUtil;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.cast.CastBoolean;
import railo.transformer.bytecode.cast.CastString;
import railo.transformer.bytecode.expression.ExprBoolean;
import railo.transformer.bytecode.expression.ExprString;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.expression.var.Variable;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitInteger;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;

public final class Function extends StatementBase implements Opcodes, IFunction,HasBody {

	
	
	// Scope variablesScope()
	private static final Method VARIABLE_SCOPE = new Method(
			"variablesScope",
			Types.VARIABLES,
			new Type[]{}
    		);


	// Object set(String,Object)
	private static final Method SET_STR = new Method(
			"set",
			Types.OBJECT,
			new Type[]{Types.STRING,Types.OBJECT}
    		);
	
	private static final Method SET_KEY = new Method(
			"set",
			Types.OBJECT,
			new Type[]{Types.COLLECTION_KEY,Types.OBJECT}
    		);
	
	private static final Method REG_UDF_STR = new Method(
			"registerUDF",
			Types.VOID,
			new Type[]{Types.STRING,Types.UDF_PROPERTIES}
    		);

	
	private static final Method REG_UDF_KEY = new Method(
			"registerUDF",
			Types.VOID,
			new Type[]{Types.COLLECTION_KEY,Types.UDF_PROPERTIES}
    		);

	
	
	private static final ExprString ANY = LitString.toExprString("any");
	//private static final ExprString PUBLIC = LitString.toExprString("public");
	//private static final ExprString EMPTY = LitString.toExprString("");


	private static final Type UDF_PROPERTIES = Type.getType(railo.runtime.type.UDFProperties.class);

	// <init>(Page,FunctionArgument[],int String,String,boolean);
	private static final Type FUNCTION_ARGUMENT = Type.getType(FunctionArgument.class);
	private static final Type FUNCTION_ARGUMENT_IMPL = Type.getType(FunctionArgumentImpl.class);
	private static final Type FUNCTION_ARGUMENT_LIGHT = Type.getType(FunctionArgumentLight.class);
	private static final Type FUNCTION_ARGUMENT_ARRAY = Type.getType(FunctionArgument[].class);
	
	private static final Method INIT_UDF_IMPL_PROP = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.UDF_PROPERTIES
				}
    		);
	
	
	private static final Method INIT_UDF_PROPERTIES_STRTYPE = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.PAGE_SOURCE,
					FUNCTION_ARGUMENT_ARRAY,
					Types.INT_VALUE,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.BOOLEAN_VALUE,
					Types.BOOLEAN_VALUE,
					Types.INT_VALUE,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.BOOLEAN,
					Types.BOOLEAN,
					Page.STRUCT_IMPL
				}
    		);
	private static final Method INIT_UDF_PROPERTIES_SHORTTYPE = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.PAGE_SOURCE,
					FUNCTION_ARGUMENT_ARRAY,
					Types.INT_VALUE,
					Types.STRING,
					Types.SHORT_VALUE,
					Types.STRING,
					Types.BOOLEAN_VALUE,
					Types.BOOLEAN_VALUE,
					Types.INT_VALUE,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.BOOLEAN,
					Types.BOOLEAN,
					Page.STRUCT_IMPL
				}
    		);


	// FunctionArgumentImpl(String name,String type,boolean required,int defaultType,String dspName,String hint,StructImpl meta)
	private static final Method INIT_FAI_STRING = new Method(
			"<init>",
			Types.VOID,
			new Type[]{Types.STRING,        Types.STRING,Types.SHORT_VALUE,Types.BOOLEAN_VALUE,Types.INT_VALUE,Types.BOOLEAN_VALUE,Types.STRING,Types.STRING,Page.STRUCT_IMPL}
    		);	
	private static final Method INIT_FAI_KEY1 = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COLLECTION_KEY}
    		);	
	private static final Method INIT_FAI_KEY3 = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COLLECTION_KEY,
					Types.STRING,
					Types.SHORT_VALUE}
    		);
	private static final Method INIT_FAI_KEY4 = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COLLECTION_KEY,
					Types.STRING,
					Types.SHORT_VALUE,
					Types.BOOLEAN_VALUE}
    		);	
	private static final Method INIT_FAI_KEY5 = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COLLECTION_KEY,
					Types.STRING,
					Types.SHORT_VALUE,
					Types.BOOLEAN_VALUE,
					Types.INT_VALUE}
    		);
	private static final Method INIT_FAI_KEY6 = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COLLECTION_KEY,
					Types.STRING,
					Types.SHORT_VALUE,
					Types.BOOLEAN_VALUE,
					Types.INT_VALUE,
					Types.BOOLEAN_VALUE}
    		);
	private static final Method INIT_FAI_KEY7 = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COLLECTION_KEY,
					Types.STRING,
					Types.SHORT_VALUE,
					Types.BOOLEAN_VALUE,
					Types.INT_VALUE,
					Types.BOOLEAN_VALUE,
					Types.STRING}
    		);
	private static final Method INIT_FAI_KEY8 = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COLLECTION_KEY,
					Types.STRING,
					Types.SHORT_VALUE,
					Types.BOOLEAN_VALUE,
					Types.INT_VALUE,
					Types.BOOLEAN_VALUE,
					Types.STRING,
					Types.STRING}
    		);
	private static final Method INIT_FAI_KEY9 = new Method(
			"<init>",
			Types.VOID,
			new Type[]{
					Types.COLLECTION_KEY,
					Types.STRING,
					Types.SHORT_VALUE,
					Types.BOOLEAN_VALUE,
					Types.INT_VALUE,
					Types.BOOLEAN_VALUE,
					Types.STRING,
					Types.STRING,
					Page.STRUCT_IMPL}
    		);	
	private static final Method[] INIT_FAI_KEY=new Method[]{
		INIT_FAI_KEY1,INIT_FAI_KEY3,INIT_FAI_KEY4,INIT_FAI_KEY5,INIT_FAI_KEY6,INIT_FAI_KEY7,INIT_FAI_KEY8,INIT_FAI_KEY9
	};
	private static final Method[] INIT_FAI_KEY_LIGHT=new Method[]{
		INIT_FAI_KEY1,INIT_FAI_KEY3
	};
	
	
	private ExprString name;
	private ExprString returnType=ANY;
	private ExprBoolean output=LitBoolean.TRUE;
	private ExprBoolean abstr=LitBoolean.FALSE;
	private int access=Component.ACCESS_PUBLIC;
	private ExprString displayName=LitString.EMPTY;
	private ExprString hint=LitString.EMPTY;
	private Body body;
	private List<Argument> arguments=new ArrayList<Argument>();
	private Map<String,Attribute> metadata;
	private ExprString returnFormat;
	private ExprString description;
	private ExprBoolean secureJson;
	private ExprBoolean verifyClient;

	public Function(String name,int access,String returnType,Body body,int startline,int endline) {
		super(startline,endline);
		this.name=LitString.toExprString(name, -1);
		this.access=access;
		if(!StringUtil.isEmpty(returnType))this.returnType=LitString.toExprString(returnType);
		
		this.body=body;
		body.setParent(this);
		
		
	}
	
	public Function(Expression name,Expression returnType,Expression returnFormat,Expression output,Expression abstr,
			int access,Expression displayName,Expression description,Expression hint,Expression secureJson,
			Expression verifyClient,Body body,int startline,int endline) {
		super(startline,endline);
		this.name=CastString.toExprString(name);
		this.returnType=CastString.toExprString(returnType);
		this.returnFormat=returnFormat!=null?CastString.toExprString(returnFormat):null;
		this.output=CastBoolean.toExprBoolean(output);
		this.abstr=CastBoolean.toExprBoolean(abstr);
		this.access=access;
		this.description=description!=null?CastString.toExprString(description):null;
		this.displayName=CastString.toExprString(displayName);
		this.hint=CastString.toExprString(hint);
		this.secureJson=secureJson!=null?CastBoolean.toExprBoolean(secureJson):null;
		this.verifyClient=verifyClient!=null?CastBoolean.toExprBoolean(verifyClient):null;
		//checkNameConflict(this.name);

		
		this.body=body;
		body.setParent(this);
		
	}


	/*private static void checkNameConflict(ExprString expr) {
		if(expr instanceof LitString){
			String name=((LitString)expr).getString();
			if()
		}
	}*/

	/**
	 * @see railo.transformer.bytecode.statement.IFunction#writeOut(railo.transformer.bytecode.BytecodeContext, int)
	 */
	public void writeOut(BytecodeContext bc, int type) throws BytecodeException {
    	ExpressionUtil.visitLine(bc, getStartLine());
        _writeOut(bc,type);
    	ExpressionUtil.visitLine(bc, getEndLine());
	}
	
	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(railo.transformer.bytecode.BytecodeContext)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		_writeOut(bc,PAGE_TYPE_REGULAR);
	}
	
	public void _writeOut(BytecodeContext bc, int pageType) throws BytecodeException {
		
		GeneratorAdapter adapter = bc.getAdapter();
		Page page = ASMUtil.getAncestorPage(this);
		int index=page.addFunction(this);

		// c.set(<name>,udf);
		if(pageType==PAGE_TYPE_INTERFACE) {
			adapter.loadArg(0);
		}
		else if(pageType==PAGE_TYPE_COMPONENT) {
			adapter.loadArg(1);
		}
		// pc.variablesScope().set(<name>,udf);
		else {
			adapter.loadArg(0);
			adapter.invokeVirtual(Types.PAGE_CONTEXT, VARIABLE_SCOPE);
		}
		boolean hasKey = Variable.registerKey(bc,name,true);
		//name.writeOut(bc, Expression.MODE_REF);
		
		// CHANGES
		//loadUDF(bc,index);
		//loadUDFProperties(bc,index);
		//createUDF(bc,index);
		
		if(pageType==PAGE_TYPE_COMPONENT) {
			loadUDF(bc,index,true);
			adapter.invokeVirtual(Types.COMPONENT_IMPL, hasKey?REG_UDF_KEY:REG_UDF_STR);
		}
		else if(pageType==PAGE_TYPE_INTERFACE) {
			loadUDF(bc,index,true);
			adapter.invokeVirtual(Types.INTERFACE_IMPL, hasKey?REG_UDF_KEY:REG_UDF_STR);
		}
		else {
			loadUDF(bc, index);
			adapter.invokeInterface(Types.VARIABLES, hasKey?SET_KEY:SET_STR);
			adapter.pop();
		}
	}

	public void loadUDF(BytecodeContext bc, int index, boolean onlyProps) throws BytecodeException {
		BytecodeContext constr = bc.getConstructor();
		GeneratorAdapter cga = constr.getAdapter();
		GeneratorAdapter ga = bc.getAdapter();
		
		// store
		cga.visitVarInsn(ALOAD, 0);
		cga.visitFieldInsn(GETFIELD, bc.getClassName(), "udfs", onlyProps?Types.UDF_PROPERTIES_ARRAY.toString():Types.UDF_IMPL_ARRAY.toString());
		cga.push(index);
		createUDFProperties(constr,index,onlyProps);
		//cga.visitInsn(DUP_X2);
		cga.visitInsn(AASTORE);
		
		// get
		ga.visitVarInsn(ALOAD, 0);
		ga.visitFieldInsn(GETFIELD, bc.getClassName(), "udfs", onlyProps?Types.UDF_PROPERTIES_ARRAY.toString():Types.UDF_IMPL_ARRAY.toString());
		ga.push(index);
		ga.visitInsn(AALOAD);
	}
	
	
	
	public void createUDFProperties(BytecodeContext bc, int index, boolean onlyProps) throws BytecodeException {
		GeneratorAdapter adapter=bc.getAdapter();
		if(!onlyProps) {
			adapter.newInstance(Types.UDF_IMPL);
			adapter.dup();
		}
		adapter.newInstance(UDF_PROPERTIES);
		adapter.dup();
		adapter.visitVarInsn(ALOAD, 1);
		// page
		//adapter.loadLocal(0);
		//adapter.loadThis();
		
		// arguments
		createArguments(bc);
		// index
		adapter.push(index);
		// name
		ExpressionUtil.writeOutSilent(name,bc, Expression.MODE_REF);
		// return type
		short type=ExpressionUtil.toShortType(returnType,CFTypes.TYPE_UNKNOW);
		if(type==CFTypes.TYPE_UNKNOW) ExpressionUtil.writeOutSilent(returnType,bc, Expression.MODE_REF);
		else adapter.push(type);
		// return format
		if(returnFormat!=null)ExpressionUtil.writeOutSilent(returnFormat,bc, Expression.MODE_REF);
		else ASMConstants.NULL(adapter);
		// output
		ExpressionUtil.writeOutSilent(output,bc, Expression.MODE_VALUE);
		// abstract
		ExpressionUtil.writeOutSilent(abstr,bc, Expression.MODE_VALUE);
		// access
		writeOutAccess(bc, access);
		// displayName
		ExpressionUtil.writeOutSilent(displayName,bc, Expression.MODE_REF);// displayName;
		// description
		if(description!=null)ExpressionUtil.writeOutSilent(description,bc, Expression.MODE_REF);// displayName;
		else adapter.push("");
		// hint
		ExpressionUtil.writeOutSilent(hint,bc, Expression.MODE_REF);// hint;
		// secureJson
		if(secureJson!=null)ExpressionUtil.writeOutSilent(secureJson,bc, Expression.MODE_REF);
		else ASMConstants.NULL(adapter);
		// verify client
		if(verifyClient!=null)ExpressionUtil.writeOutSilent(verifyClient,bc, Expression.MODE_REF);
		else ASMConstants.NULL(adapter);
		// meta
		Page.createMetaDataStruct(bc,metadata,null);
		
		adapter.invokeConstructor(UDF_PROPERTIES, type==-1?INIT_UDF_PROPERTIES_STRTYPE:INIT_UDF_PROPERTIES_SHORTTYPE);
		if(!onlyProps)adapter.invokeConstructor(Types.UDF_IMPL, INIT_UDF_IMPL_PROP);
		
	}
	
	public void loadUDF(BytecodeContext bc, int index) throws BytecodeException {
		// new UDF(...)
		GeneratorAdapter adapter=bc.getAdapter();
		adapter.newInstance(Types.UDF_IMPL);
		adapter.dup();
		
		loadUDF(bc, index,true);
		
		adapter.invokeConstructor(Types.UDF_IMPL, INIT_UDF_IMPL_PROP);
	}
	
	

	/*private void loadArguments(BytecodeContext bc, int index) throws BytecodeException {
		BytecodeContext constr = bc.getStaticConstructor();
		String type = Type.getType(FunctionArgument[].class).toString();
		String fa="fa_"+index;
		FieldVisitor fv = constr.getClassWriter().visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, 
				fa, type, null, null);
		fv.visitEnd();
		createArguments(constr);
		constr.getAdapter().visitFieldInsn(Opcodes.PUTSTATIC, constr.getClassName(), fa, type);
		
		bc.getAdapter().visitFieldInsn(Opcodes.GETSTATIC, constr.getClassName(), fa, type);
		//createArguments(bc, adapter);
		
		
	}*/
	
	private void createArguments(BytecodeContext bc) throws BytecodeException {
		GeneratorAdapter ga = bc.getAdapter();
		ga.push(arguments.size());
		ga.newArray(FUNCTION_ARGUMENT);
		Argument arg;
        for (int i = 0; i < arguments.size(); i++) {
        	arg= arguments.get(i);
            
        	boolean canHaveKey = Variable.canRegisterKey(arg.getName());
    		
    	// CHECK if default values
    		// type
    		ExprString _strType = arg.getType();
    		short _type=CFTypes.TYPE_UNKNOW;
    		if(_strType instanceof LitString){
    			_type=CFTypes.toShortStrict(((LitString)_strType).getString(),CFTypes.TYPE_UNKNOW);
    		}
    		boolean useType=!canHaveKey || _type!=CFTypes.TYPE_ANY;
    		//boolean useStrType=useType && (_type==CFTypes.TYPE_UNDEFINED || _type==CFTypes.TYPE_UNKNOW || CFTypes.toString(_type, null)==null);
    		
    		// required
    		ExprBoolean _req = arg.getRequired();
    		boolean useReq=!canHaveKey || toBoolean(_req,null)!=Boolean.FALSE;
    		
    		// default-type
    		Expression _def = arg.getDefaultValueType();
    		boolean useDef=!canHaveKey || toInt(_def,-1)!=FunctionArgument.DEFAULT_TYPE_NULL;
    		
    		// pass by reference
    		ExprBoolean _pass = arg.isPassByReference();
    		boolean usePass=!canHaveKey || toBoolean(_pass,null)!=Boolean.TRUE;
    		
    		// display-hint
    		ExprString _dsp = arg.getDisplayName();
    		boolean useDsp=!canHaveKey || !isLiteralEmptyString(_dsp);
    		
    		// hint
    		ExprString _hint = arg.getHint();
    		boolean useHint=!canHaveKey || !isLiteralEmptyString(_hint);
    		
    		// meta
    		Map _meta = arg.getMetaData();
    		boolean useMeta=!canHaveKey || (_meta!=null && !_meta.isEmpty());
    		int functionIndex=7;
    		if(!useMeta) {
    			functionIndex--;
    			if(!useHint) {
    				functionIndex--;
    				if(!useDsp){
        				functionIndex--;
        				if(!usePass) {
        					functionIndex--;
        					if(!useDef) {
        						functionIndex--;
        						if(!useReq) {
        							functionIndex--;
        							if(!useType){
        								functionIndex--;
        							}
        						}
        					}
        				}
    				}
    			}
    		}
    	// write out arguments	
    		ga.dup();
            ga.push(i);
            	
            // new FunctionArgument(...)
            ga.newInstance(canHaveKey && functionIndex<INIT_FAI_KEY_LIGHT.length?FUNCTION_ARGUMENT_LIGHT:FUNCTION_ARGUMENT_IMPL);
    		ga.dup();
    		boolean hasKey = Variable.registerKey(bc,arg.getName(),false);
    		
    		// type
    		if(functionIndex>=INIT_FAI_KEY.length-7) {
    			_strType.writeOut(bc, Expression.MODE_REF);
    			bc.getAdapter().push(_type);
    		}
    		// required
    		if(functionIndex>=INIT_FAI_KEY.length-6)_req.writeOut(bc, Expression.MODE_VALUE);
    		// default value
    		if(functionIndex>=INIT_FAI_KEY.length-5)_def.writeOut(bc, Expression.MODE_VALUE);
    		// pass by reference
    		if(functionIndex>=INIT_FAI_KEY.length-4)_pass.writeOut(bc, Expression.MODE_VALUE);
    		// display-name
    		if(functionIndex>=INIT_FAI_KEY.length-3)_dsp.writeOut(bc, Expression.MODE_REF);
    		// hint
    		if(functionIndex>=INIT_FAI_KEY.length-2)_hint.writeOut(bc, Expression.MODE_REF);
    		//meta
    		if(functionIndex==INIT_FAI_KEY.length-1)Page.createMetaDataStruct(bc,_meta,null);
    		
    		if(hasKey && functionIndex<INIT_FAI_KEY_LIGHT.length)
        		ga.invokeConstructor(FUNCTION_ARGUMENT_LIGHT, INIT_FAI_KEY[functionIndex]);
    		else 
    			ga.invokeConstructor(FUNCTION_ARGUMENT_IMPL, hasKey?INIT_FAI_KEY[functionIndex]:INIT_FAI_STRING);

            ga.visitInsn(Opcodes.AASTORE);
        }
	}

	private int toInt(Expression expr, int defaultValue) {
		if(expr instanceof LitInteger) {
			return ((LitInteger)expr).getInteger().intValue();
		}
		return defaultValue;
	}

	private Boolean toBoolean(ExprBoolean expr, Boolean defaultValue) {
		if(expr instanceof LitBoolean) {
			return ((LitBoolean)expr).getBooleanValue()?Boolean.TRUE:Boolean.FALSE;
		}
		return defaultValue;
	}

	private boolean isLiteralEmptyString(ExprString expr) {
		if(expr instanceof LitString) {
			return StringUtil.isEmpty(((LitString)expr).getString());
		}
		return false;
	}

	private void writeOutAccess(BytecodeContext bc,ExprString expr) {
		
		// write short type
		if(expr instanceof LitString){
			int access=ComponentUtil.toIntAccess(((LitString)expr).getString(),Component.ACCESS_PUBLIC);
			bc.getAdapter().push(access);
		}
		else bc.getAdapter().push(Component.ACCESS_PUBLIC);
	}
	private void writeOutAccess(BytecodeContext bc,int access) {
		bc.getAdapter().push(access);
	}

	public void addArgument(String name, String type, boolean required, Expression defaultValue) {
		addArgument(
				LitString.toExprString(name), 
				LitString.toExprString(type), 
				LitBoolean.toExprBoolean(required),
				defaultValue, 
				LitBoolean.TRUE,
				LitString.EMPTY,
				LitString.EMPTY,null);
	}

	public void addArgument(Expression name, Expression type, Expression required, Expression defaultValue,ExprBoolean passByReference, 
			Expression displayName, Expression hint,Map meta) {
		arguments.add(new Argument(name,type,required,defaultValue,passByReference,displayName,hint,meta));
	}

	/**
	 * @return the arguments
	 */
	public List getArguments() {
		return arguments;
	}

	/**
	 * @return the body
	 */
	public Body getBody() {
		return body;
	}

	public void setMetaData(Map<String,Attribute> metadata) {
		this.metadata=metadata;
	}
	
	public void setHint(String hint){
		this.hint=LitString.toExprString(hint);
	}

	public void addAttribute(Attribute attr) throws TemplateException {
		String name=attr.getName().toLowerCase();
		// name
		if("name".equals(name))	{
			throw new BytecodeException("name cannot be defined twice",getLine());
			//this.name=CastString.toExprString(attr.getValue());
		}
		else if("returntype".equals(name))	{
			this.returnType=toLitString(name,attr.getValue());
		}
		else if("access".equals(name))	{
			
			LitString ls = toLitString(name,attr.getValue());
			String strAccess = ls.getString();
			int acc = ComponentUtil.toIntAccess(strAccess,-1);
			if(acc==-1)
				throw new BytecodeException("invalid access type ["+strAccess+"], access types are remote, public, package, private",getLine());
			access=acc;
			
		}
		
		else if("output".equals(name))		this.output=toLitBoolean(name,attr.getValue());
		else if("abstract".equals(name))	this.abstr=toLitBoolean(name,attr.getValue());
		else if("displayname".equals(name))	this.displayName=toLitString(name,attr.getValue());
		else if("hint".equals(name))		this.hint=toLitString(name,attr.getValue());
		else if("description".equals(name))	this.description=toLitString(name,attr.getValue());
		else if("returnformat".equals(name))this.returnFormat=toLitString(name,attr.getValue());
		else if("securejson".equals(name))	this.secureJson=toLitBoolean(name,attr.getValue());
		else if("verifyclient".equals(name))	this.verifyClient=toLitBoolean(name,attr.getValue());
		else {
			toLitString(name,attr.getValue());// needed for testing
			if(metadata==null)metadata=new HashMap();
			metadata.put(attr.getName(), attr);
		}
	}

	private LitString toLitString(String name, Expression value) throws BytecodeException {
		ExprString es = CastString.toExprString(value);
		if(!(es instanceof LitString))
			throw new BytecodeException("value of attribute ["+name+"] must have a literal/constant value",getLine());
		return (LitString) es;
	}
	
	private LitBoolean toLitBoolean(String name, Expression value) throws BytecodeException {
		 ExprBoolean eb = CastBoolean.toExprBoolean(value);
		if(!(eb instanceof LitBoolean))
			throw new BytecodeException("value of attribute ["+name+"] must have a literal/constant value",getLine());
		return (LitBoolean) eb;
	}

}
