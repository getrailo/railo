package railo.transformer.bytecode.statement.udf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.exp.TemplateException;
import railo.runtime.listener.AppListenerUtil;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.FunctionArgumentImpl;
import railo.runtime.type.FunctionArgumentLight;
import railo.runtime.type.util.ComponentUtil;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.TransformerException;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.statement.Argument;
import railo.transformer.bytecode.statement.HasBody;
import railo.transformer.bytecode.statement.IFunction;
import railo.transformer.bytecode.statement.StatementBaseNoFinal;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.util.ASMConstants;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.ExpressionUtil;
import railo.transformer.bytecode.util.Types;
import railo.transformer.cfml.evaluator.EvaluatorException;
import railo.transformer.expression.ExprBoolean;
import railo.transformer.expression.ExprInt;
import railo.transformer.expression.ExprString;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitBoolean;
import railo.transformer.expression.literal.LitInteger;
import railo.transformer.expression.literal.LitString;
import railo.transformer.expression.literal.Literal;

public abstract class Function extends StatementBaseNoFinal implements Opcodes, IFunction,HasBody {

	

	// Scope variablesScope()
	static final Method VARIABLE_SCOPE = new Method(
			"variablesScope",
			Types.VARIABLES,
			new Type[]{}
    		);
	// Scope variablesScope()
	static final Method GET_PAGESOURCE = new Method(
			"getPageSource",
			Types.PAGE_SOURCE,
			new Type[]{}
    		);


	// Object set(String,Object)
	static final Method SET_STR = new Method(
			"set",
			Types.OBJECT,
			new Type[]{Types.STRING,Types.OBJECT}
    		);
	
	static final Method SET_KEY = new Method(
			"set",
			Types.OBJECT,
			new Type[]{Types.COLLECTION_KEY,Types.OBJECT}
    		);
	
	static final Method REG_UDF_STR = new Method(
			"registerUDF",
			Types.VOID,
			new Type[]{Types.STRING,Types.UDF_PROPERTIES}
    		);

	
	static final Method REG_UDF_KEY = new Method(
			"registerUDF",
			Types.VOID,
			new Type[]{Types.COLLECTION_KEY,Types.UDF_PROPERTIES}
    		);

	
	
	//private static final ExprString ANY = LitString.toExprString("any");
	
	// <init>(Page,FunctionArgument[],int String,String,boolean);
	private static final Type FUNCTION_ARGUMENT = Type.getType(FunctionArgument.class);
	private static final Type FUNCTION_ARGUMENT_IMPL = Type.getType(FunctionArgumentImpl.class);
	private static final Type FUNCTION_ARGUMENT_LIGHT = Type.getType(FunctionArgumentLight.class);
	private static final Type FUNCTION_ARGUMENT_ARRAY = Type.getType(FunctionArgument[].class);
	
	protected static final Method INIT_UDF_IMPL_PROP = new Method(
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
					Types.INT_VALUE,
					Types.BOOLEAN,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.BOOLEAN,
					Types.BOOLEAN,
					Types.OBJECT,
					Types.INTEGER,
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
					Types.INT_VALUE,
					Types.BOOLEAN,
					Types.STRING,
					Types.STRING,
					Types.STRING,
					Types.BOOLEAN,
					Types.BOOLEAN,
					Types.OBJECT,
					Types.INTEGER,
					Page.STRUCT_IMPL
				}
    		);
	private static final Method INIT_UDF_PROPERTIES_SHORTTYPE_LIGHT = new Method(
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
					Types.INT_VALUE
				}
    		);


	// FunctionArgumentImpl(String name,String type,boolean required,int defaultType,String dspName,String hint,StructImpl meta)
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
	
	
	ExprString name;
	ExprString returnType;
	ExprBoolean output;
	ExprBoolean bufferOutput;
	//ExprBoolean abstry=LitBoolean.FALSE;
	int access=Component.ACCESS_PUBLIC;
	ExprString displayName;
	ExprString hint;
	Body body;
	List<Argument> arguments=new ArrayList<Argument>();
	Map<String,Attribute> metadata;
	ExprString returnFormat;
	ExprString description;
	ExprBoolean secureJson;
	ExprBoolean verifyClient;
	ExprInt localMode;
	protected int valueIndex;
	protected int arrayIndex;
	private Literal cachedWithin;
	private boolean _abstract;
	private boolean _final;
	

	public Function(Page page,String name,int access,String returnType,Body body,Position start, Position end) {
		super(page.getFactory(),start,end);
		this.name=page.getFactory().createLitString(name);
		this.access=access;
		if(!StringUtil.isEmpty(returnType))
			this.returnType=page.getFactory().createLitString(returnType);
		else
			this.returnType=page.getFactory().createLitString("any");
		this.body=body;
		body.setParent(this);
		int[] indexes = page.addFunction(this);
		valueIndex=indexes[VALUE_INDEX];
		arrayIndex=indexes[ARRAY_INDEX];
		output=page.getFactory().TRUE();
		displayName=page.getFactory().EMPTY();
		hint=page.getFactory().EMPTY();
	}
	
	public Function(Page page,Expression name,Expression returnType,Expression returnFormat,Expression output,Expression bufferOutput,
			int access,Expression displayName,Expression description,Expression hint,Expression secureJson,
			Expression verifyClient,Expression localMode,Literal cachedWithin, boolean _abstract, boolean _final,Body body,Position start, Position end) {
		super(page.getFactory(),start,end);
		
		this.name=page.getFactory().toExprString(name);
		this.returnType=page.getFactory().toExprString(returnType);
		this.returnFormat=returnFormat!=null?page.getFactory().toExprString(returnFormat):null;
		this.output=page.getFactory().toExprBoolean(output);
		this.bufferOutput=bufferOutput==null?null:page.getFactory().toExprBoolean(bufferOutput);
		this.access=access;
		this.description=description!=null?page.getFactory().toExprString(description):null;
		this.displayName=page.getFactory().toExprString(displayName);
		this.hint=page.getFactory().toExprString(hint);
		this.secureJson=secureJson!=null?page.getFactory().toExprBoolean(secureJson):null;
		this.verifyClient=verifyClient!=null?page.getFactory().toExprBoolean(verifyClient):null;
		this.cachedWithin=cachedWithin;
		this._abstract=_abstract;
		this._final=_final;
		this.localMode=toLocalMode(localMode, null);
		
		this.body=body;
		body.setParent(this);
		int[] indexes=page.addFunction(this);
		valueIndex=indexes[VALUE_INDEX];
		arrayIndex=indexes[ARRAY_INDEX];
	}


	public static ExprInt toLocalMode(Expression expr, ExprInt defaultValue) {
		int mode=-1;
		if(expr instanceof Literal) {
			String str = ((Literal)expr).getString();
			str=str.trim().toLowerCase();
			mode = AppListenerUtil.toLocalMode(str,-1);
		}
		if(mode==-1) return defaultValue;
		return expr.getFactory().createLitInteger(mode);
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
	@Override
	public final void writeOut(BytecodeContext bc, int type) throws TransformerException {
    	ExpressionUtil.visitLine(bc, getStart());
        _writeOut(bc,type);
    	ExpressionUtil.visitLine(bc, getEnd());
	}
	
	/**
	 * @see railo.transformer.bytecode.statement.StatementBase#_writeOut(railo.transformer.bytecode.BytecodeContext)
	 */
	@Override
	public final void _writeOut(BytecodeContext bc) throws TransformerException {
		_writeOut(bc,PAGE_TYPE_REGULAR);
	}
	
	public abstract void _writeOut(BytecodeContext bc, int pageType) throws TransformerException;
	

	public final void loadUDFProperties(BytecodeContext bc, int valueIndex,int arrayIndex, boolean closure) throws TransformerException {
		BytecodeContext constr = bc.getConstructor();
		GeneratorAdapter cga = constr.getAdapter();
		GeneratorAdapter ga = bc.getAdapter();
		
		// store
		cga.visitVarInsn(ALOAD, 0);
		cga.visitFieldInsn(GETFIELD, bc.getClassName(), "udfs", Types.UDF_PROPERTIES_ARRAY.toString());
		cga.push(arrayIndex);
		createUDFProperties(constr,valueIndex,closure);
		//cga.visitInsn(DUP_X2);
		cga.visitInsn(AASTORE);
		
		// get
		ga.visitVarInsn(ALOAD, 0);
		ga.visitFieldInsn(GETFIELD, bc.getClassName(), "udfs", Types.UDF_PROPERTIES_ARRAY.toString());
		ga.push(arrayIndex);
		ga.visitInsn(AALOAD);
	}
	
	
	
	public final void createUDFProperties(BytecodeContext bc, int index, boolean closure) throws TransformerException {
		GeneratorAdapter adapter=bc.getAdapter();
		adapter.newInstance(Types.UDF_PROPERTIES_IMPL);
		adapter.dup();
		if(closure){
			adapter.loadThis();
			adapter.invokeVirtual(Types.PAGE, GET_PAGESOURCE);
		}
		else adapter.visitVarInsn(ALOAD, 1);
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
		short type=ExpressionUtil.toShortType(returnType,false,CFTypes.TYPE_UNKNOW);
		if(type==CFTypes.TYPE_UNKNOW) ExpressionUtil.writeOutSilent(returnType,bc, Expression.MODE_REF);
		else adapter.push(type);
		
		// return format
		if(returnFormat!=null)ExpressionUtil.writeOutSilent(returnFormat,bc, Expression.MODE_REF);
		else ASMConstants.NULL(adapter);
		
		// output
		ExpressionUtil.writeOutSilent(output,bc, Expression.MODE_VALUE);
		
		
		// access
		writeOutAccess(bc, access);
		
		boolean light=type!=-1;
		if(light && !bc.getFactory().EMPTY().equals(displayName))light=false;
		if(light && description!=null && !bc.getFactory().EMPTY().equals(description))light=false;
		if(light && !bc.getFactory().EMPTY().equals(hint))light=false;
		if(light && secureJson!=null)light=false;
		if(light && verifyClient!=null)light=false;
		if(light && cachedWithin!=null)light=false;
		if(light && bufferOutput!=null)light=false;
		if(light && localMode!=null)light=false;
		if(light && Page.hasMetaDataStruct(metadata, null))light=false;
		if(light){
			adapter.invokeConstructor(Types.UDF_PROPERTIES_IMPL, INIT_UDF_PROPERTIES_SHORTTYPE_LIGHT);
			return;
		}
		

		// buffer output
		if(bufferOutput!=null)ExpressionUtil.writeOutSilent(bufferOutput,bc, Expression.MODE_REF);
		else ASMConstants.NULL(adapter);
		
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
		
		// cachedwithin
		if(cachedWithin!=null) {
			cachedWithin.writeOut(bc, Expression.MODE_REF);
		}
		else ASMConstants.NULL(adapter);
		//adapter.push(cachedWithin<0?0:cachedWithin);
		
		// localMode
		if(localMode!=null)ExpressionUtil.writeOutSilent(localMode,bc, Expression.MODE_REF);
		else ASMConstants.NULL(adapter);
		
		// meta
		Page.createMetaDataStruct(bc,metadata,null);
		
		adapter.invokeConstructor(Types.UDF_PROPERTIES_IMPL, type==-1?INIT_UDF_PROPERTIES_STRTYPE:INIT_UDF_PROPERTIES_SHORTTYPE);
		
	}
	
	/*public final void loadUDF(BytecodeContext bc, int index) throws BytecodeException {
		// new UDF(...)
		GeneratorAdapter adapter=bc.getAdapter();
		adapter.newInstance(Types.UDF_IMPL);
		adapter.dup();
		
		loadUDFProperties(bc, index,false);
		
		adapter.invokeConstructor(Types.UDF_IMPL, INIT_UDF_IMPL_PROP);
	}*/
	
	public final void createUDF(BytecodeContext bc, int index, boolean closure) throws TransformerException {
		// new UDF(...)
		GeneratorAdapter adapter=bc.getAdapter();
		adapter.newInstance(closure?Types.CLOSURE:Types.UDF_IMPL);
		adapter.dup();
		
		createUDFProperties(bc, index,closure);
		//loadUDFProperties(bc, index,closure);
		
		adapter.invokeConstructor(closure?Types.CLOSURE:Types.UDF_IMPL, INIT_UDF_IMPL_PROP);
	}
	

	
	private final void createArguments(BytecodeContext bc) throws TransformerException {
		GeneratorAdapter ga = bc.getAdapter();
		ga.push(arguments.size());
		ga.newArray(FUNCTION_ARGUMENT);
		Argument arg;
        for (int i = 0; i < arguments.size(); i++) {
        	arg= arguments.get(i);
            
        	boolean canHaveKey = Factory.canRegisterKey(arg.getName());
    		
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
    		Expression _def = arg.getDefaultValueType(bc.getFactory());
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
    		bc.getFactory().registerKey(bc,arg.getName(),false);
    		
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
    		
    		if(functionIndex<INIT_FAI_KEY_LIGHT.length)
        		ga.invokeConstructor(FUNCTION_ARGUMENT_LIGHT, INIT_FAI_KEY[functionIndex]);
    		else 
    			ga.invokeConstructor(FUNCTION_ARGUMENT_IMPL, INIT_FAI_KEY[functionIndex]);

            ga.visitInsn(Opcodes.AASTORE);
        }
	}

	private final int toInt(Expression expr, int defaultValue) {
		if(expr instanceof LitInteger) {
			return ((LitInteger)expr).getInteger().intValue();
		}
		return defaultValue;
	}

	private final Boolean toBoolean(ExprBoolean expr, Boolean defaultValue) {
		if(expr instanceof LitBoolean) {
			return ((LitBoolean)expr).getBooleanValue()?Boolean.TRUE:Boolean.FALSE;
		}
		return defaultValue;
	}

	private final boolean isLiteralEmptyString(ExprString expr) {
		if(expr instanceof LitString) {
			return StringUtil.isEmpty(((LitString)expr).getString());
		}
		return false;
	}

	private final void writeOutAccess(BytecodeContext bc,ExprString expr) {
		
		// write short type
		if(expr instanceof LitString){
			int access=ComponentUtil.toIntAccess(((LitString)expr).getString(),Component.ACCESS_PUBLIC);
			bc.getAdapter().push(access);
		}
		else bc.getAdapter().push(Component.ACCESS_PUBLIC);
	}
	private final void writeOutAccess(BytecodeContext bc,int access) {
		bc.getAdapter().push(access);
	}

	public final void addArgument(Factory factory,String name, String type, boolean required, Expression defaultValue) {
		addArgument(
				factory.createLitString(name), 
				factory.createLitString(type), 
				factory.createLitBoolean(required),
				defaultValue, 
				factory.TRUE(),
				factory.EMPTY(),
				factory.EMPTY(),null);
	}

	public final void addArgument(Expression name, Expression type, Expression required, Expression defaultValue,ExprBoolean passByReference, 
			Expression displayName, Expression hint,Map meta) {
		arguments.add(new Argument(name,type,required,defaultValue,passByReference,displayName,hint,meta));
	}

	/**
	 * @return the arguments
	 */
	public final List<Argument> getArguments() {
		return arguments;
	}

	/**
	 * @return the body
	 */
	@Override
	public final Body getBody() {
		return body;
	}

	public final void setMetaData(Map<String,Attribute> metadata) {
		this.metadata=metadata;
	}
	
	public final void setHint(Factory factory,String hint){
		this.hint=factory.createLitString(hint);
	}

	public final void addAttribute(Attribute attr) throws TemplateException {
		String name=attr.getName().toLowerCase();
		// name
		if("name".equals(name))	{
			throw new TransformerException("name cannot be defined twice",getStart());
		}
		else if("returntype".equals(name))	{
			this.returnType=toLitString(name,attr.getValue());
		}
		else if("access".equals(name))	{
			
			LitString ls = toLitString(name,attr.getValue());
			String strAccess = ls.getString();
			int acc = ComponentUtil.toIntAccess(strAccess,-1);
			if(acc==-1)
				throw new TransformerException("invalid access type ["+strAccess+"], access types are remote, public, package, private",getStart());
			access=acc;
			
		}
		
		else if("output".equals(name))		this.output=toLitBoolean(name,attr.getValue());
		else if("bufferoutput".equals(name))this.bufferOutput=toLitBoolean(name,attr.getValue());
		else if("displayname".equals(name))	this.displayName=toLitString(name,attr.getValue());
		else if("hint".equals(name))		this.hint=toLitString(name,attr.getValue());
		else if("description".equals(name))	this.description=toLitString(name,attr.getValue());
		else if("returnformat".equals(name))this.returnFormat=toLitString(name,attr.getValue());
		else if("securejson".equals(name))	this.secureJson=toLitBoolean(name,attr.getValue());
		else if("verifyclient".equals(name))	this.verifyClient=toLitBoolean(name,attr.getValue());
		else if("localmode".equals(name))	{
			Expression v = attr.getValue();
			if(v!=null) {
				String str = ASMUtil.toString(v,null);
				if(!StringUtil.isEmpty(str)){
					int mode = AppListenerUtil.toLocalMode(str, -1);
					if(mode!=-1) this.localMode=v.getFactory().createLitInteger(mode);
					else throw new TransformerException("Attribute localMode of the Tag Function, must be a literal value (modern, classic, true or false)",getStart());
				}
			}
		}
		else if("cachedwithin".equals(name))	{
			try {
				this.cachedWithin=ASMUtil.cachedWithinValue(attr.getValue());//ASMUtil.timeSpanToLong(attr.getValue());
			} catch (EvaluatorException e) {
				throw new TemplateException(e.getMessage());
			}
		}
		else if("modifier".equals(name))	{
			Expression val = attr.getValue();
			if(val instanceof Literal) {
				Literal l=(Literal) val;
				String str = StringUtil.emptyIfNull(l.getString()).trim();
				if("abstract".equalsIgnoreCase(str))_abstract=true;
				else if("final".equalsIgnoreCase(str))_final=true;
			}
		}
		
		
		
		else {
			toLitString(name,attr.getValue());// needed for testing
			if(metadata==null)metadata=new HashMap<String,Attribute>();
			metadata.put(attr.getName(), attr);
		}
	}

	private final LitString toLitString(String name, Expression value) throws TransformerException {
		ExprString es = value.getFactory().toExprString(value);
		if(!(es instanceof LitString))
			throw new TransformerException("value of attribute ["+name+"] must have a literal/constant value",getStart());
		return (LitString) es;
	}
	
	private final LitBoolean toLitBoolean(String name, Expression value) throws TransformerException {
		 ExprBoolean eb = value.getFactory().toExprBoolean(value);
		if(!(eb instanceof LitBoolean))
			throw new TransformerException("value of attribute ["+name+"] must have a literal/constant value",getStart());
		return (LitBoolean) eb;
	}
	
	private final ExprInt toLitInt(String name, Expression value) throws TransformerException {
		ExprInt eb = value.getFactory().toExprInt(value);
		if(!(eb instanceof Literal))
			throw new TransformerException("value of attribute ["+name+"] must have a literal/constant value",getStart());
		return eb;
	}

}
