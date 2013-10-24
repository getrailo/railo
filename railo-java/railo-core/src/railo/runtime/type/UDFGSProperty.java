package railo.runtime.type;

import railo.commons.lang.CFTypes;
import railo.commons.lang.StringUtil;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.PageSource;
import railo.runtime.component.MemberSupport;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.exp.UDFCasterException;
import railo.runtime.functions.decision.IsValid;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.UDFUtil;

public abstract class UDFGSProperty extends MemberSupport implements UDFPlus {

	private static final Collection.Key MIN_LENGTH = KeyImpl.intern("minLength");
	private static final Collection.Key MAX_LENGTH = KeyImpl.intern("maxLength");
	
	protected final FunctionArgument[] arguments;
	protected final String name;
	protected ComponentImpl component;
	private UDFPropertiesImpl properties;

	public UDFGSProperty(ComponentImpl component,String name,FunctionArgument[] arguments,short rtnType,String rtnFormat) {
		super(Component.ACCESS_PUBLIC);
		properties=UDFProperties(
				component.getPageSource(),
				arguments,
				-1,
				name,
				rtnType,
				rtnFormat,
				false,
				true,
				Component.ACCESS_PUBLIC,
				"",
				"",
				"",
				Boolean.FALSE,
				Boolean.FALSE,
				0L,
				null,
				new StructImpl()
				
		);
		
		this.name=name;
		this.arguments=arguments;
		this.component=component;
	}

	private static UDFPropertiesImpl UDFProperties(PageSource pageSource,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        short returnType, 
	        String strReturnFormat, 
	        boolean output, 
	        Boolean bufferOutput, 
	        int access, 
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        long cachedWithin,
	        Integer localMode,
	        StructImpl meta) {
			return new UDFPropertiesImpl( pageSource,
			        arguments,
					 index,
			         functionName, 
			         returnType, 
			         strReturnFormat, 
			         output,
			         access, 
			         bufferOutput,
			         displayName, 
			         description, 
			         hint, 
			         secureJson,
			         verifyClient,
			         cachedWithin,
			         localMode,
			         meta);
	}

	@Override
	public FunctionArgument[] getFunctionArguments() {
		return arguments;
	}

	@Override
	public String getFunctionName() {
		return name;
	}

	@Override
	public PageSource getPageSource() {
		return component.getPageSource();
	}

	@Override
	public int getIndex() {
		return -1;
	}

	@Override
	public Component getOwnerComponent() {
		return component;
	}

	public void setOwnerComponent(ComponentImpl component) {
		this.component = component;
	}
	
	public Page getPage() {
		throw new PageRuntimeException(new DeprecatedException("method getPage():Page is no longer suppoted, use instead getPageSource():PageSource"));
    }

	@Override
	public boolean getOutput() {
		return false;
	}
 
	public UDF duplicate(boolean deep) {
		return duplicate(); // deep has no influence here, because a UDF is not a collection
	}

	@Override
	public String getDisplayName() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getHint() {
		return "";
	}

	@Override
	public int getReturnFormat() {
		return UDF.RETURN_FORMAT_WDDX;
	}

	@Override
	public int getReturnFormat(int defaultValue) {
		return defaultValue;
	}

	@Override
	public int getReturnType() {
		return CFTypes.toShortStrict(getReturnTypeAsString(),CFTypes.TYPE_UNKNOW);
	}

	@Override
	public Object getValue() {
		return this;
	}
	
	@Override
	public Boolean getSecureJson() {
		return null;
	}

	@Override
	public Boolean getVerifyClient() {
		return null;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return UDFUtil.toDumpData(pageContext, maxlevel, properties, this,false);
	}
	
	@Override
	public Struct getMetaData(PageContext pc) throws PageException {
		return ComponentUtil.getMetaData(pc, properties);
	}
	
	

	final Object cast(FunctionArgument arg,Object value, int index) throws PageException {
		if(value==null || Decision.isCastableTo(arg.getType(),arg.getTypeAsString(),value)) 
			return value;
		throw new UDFCasterException(this,arg,value,index);
	}

	final static void validate(String validate, Struct validateParams, Object obj) throws PageException {
		if(StringUtil.isEmpty(validate,true)) return;
		validate=validate.trim().toLowerCase();
		
		if(!validate.equals("regex") && !Decision.isValid(validate, obj))
			throw new ExpressionException(createMessage(validate, obj));
		
		
		// range
		if(validateParams==null) return;

		if(validate.equals("integer") || validate.equals("numeric") || validate.equals("number")){
			double min=Caster.toDoubleValue(validateParams.get(KeyConstants._min,null),Double.NaN);
			double max=Caster.toDoubleValue(validateParams.get(KeyConstants._max,null),Double.NaN);
			double d=Caster.toDoubleValue(obj);
			if(!Double.isNaN(min) && d<min)
				throw new ExpressionException(validate+" ["+Caster.toString(d)+"] is out of range, value must be more than or equal to ["+min+"]");
			if(!Double.isNaN(max) && d>max)
				throw new ExpressionException(validate+" ["+Caster.toString(d)+"] is out of range, value must be less than or equal to ["+max+"]");
		}
		else if(validate.equals("string")){
			double min=Caster.toDoubleValue(validateParams.get(MIN_LENGTH,null),Double.NaN);
			double max=Caster.toDoubleValue(validateParams.get(MAX_LENGTH,null),Double.NaN);
			String str=Caster.toString(obj);
			int l=str.length();
			if(!Double.isNaN(min) && l<((int)min))
				throw new ExpressionException("string ["+str+"] is to short ["+l+"], the string must be at least ["+min+"] characters");
			if(!Double.isNaN(max) && l>((int)max))
				throw new ExpressionException("string ["+str+"] is to long ["+l+"], the string can have a maximum length of ["+max+"] characters");
		}
		else if(validate.equals("regex")){
			String pattern=Caster.toString(validateParams.get(KeyConstants._pattern,null),null);
			String value=Caster.toString(obj);
			if(!StringUtil.isEmpty(pattern,true) && !IsValid.regex(value, pattern))
				throw new ExpressionException("the string ["+value+"] does not match the regular expression pattern ["+pattern+"]");
		}
	}

	
	@Override
	public Object callWithNamedValues(PageContext pc, Key calledName, Struct values, boolean doIncludePath) throws PageException {
		PageContextImpl pci = ((PageContextImpl)pc);
		Key old =pci.getActiveUDFCalledName();
		pci.setActiveUDFCalledName(calledName);
		try{
			return callWithNamedValues(pci, values, doIncludePath);
		}
		finally{
			pci.setActiveUDFCalledName(old);
		}
	}

	@Override
	public Object call(PageContext pc, Key calledName, Object[] args, boolean doIncludePath) throws PageException {
		PageContextImpl pci = ((PageContextImpl)pc);
		Key old =pci.getActiveUDFCalledName();
		pci.setActiveUDFCalledName(calledName);
		try{
			return call(pci, args, doIncludePath);
		}
		finally{
			pci.setActiveUDFCalledName(old);
		}
	}

	private static String createMessage(String format, Object value) {
    	if(Decision.isSimpleValue(value)) return "the value ["+Caster.toString(value,null)+"] is not in  ["+format+"] format";
    	return "cannot convert object from type ["+Caster.toTypeName(value)+"] to a ["+format+"] format";
    }   
	
}
