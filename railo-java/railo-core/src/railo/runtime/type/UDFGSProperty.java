package railo.runtime.type;

import railo.commons.lang.CFTypes;
import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.Page;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.exp.UDFCasterException;
import railo.runtime.op.Decision;
import railo.runtime.op.Duplicator;
import railo.runtime.type.util.ComponentUtil;

public abstract class UDFGSProperty extends UDFImpl {
	
	protected final FunctionArgument[] arguments;
	protected final String name;
	protected final ComponentImpl component;

	public UDFGSProperty(ComponentImpl component,String name,FunctionArgument[] arguments,short rtnType,String rtnFormat) {
		super(UDFProperties(
				component.getPageSource(),
				arguments,
				-1,
				name,
				rtnType,
				rtnFormat,
				false,
				false,
				"public",
				"",
				"",
				"",
				Boolean.FALSE,
				Boolean.FALSE,
				new StructImpl()
				
		));
		
		this.name=name;
		this.arguments=arguments;
		this.component=component;
	}

	private static UDFProperties UDFProperties(PageSource pageSource,
	        FunctionArgument[] arguments,
			int index,
	        String functionName, 
	        short returnType, 
	        String strReturnFormat, 
	        boolean output, 
	        boolean async, 
	        String strAccess, 
	        String displayName, 
	        String description, 
	        String hint, 
	        Boolean secureJson,
	        Boolean verifyClient,
	        StructImpl meta) {
		try {
			return new UDFProperties( pageSource,
			        arguments,
					 index,
			         functionName, 
			         returnType, 
			         strReturnFormat, 
			         output, 
			         async, 
			         ComponentUtil.toIntAccess(strAccess), 
			         displayName, 
			         description, 
			         hint, 
			         secureJson,
			         verifyClient,
			         meta);
		} catch (ExpressionException e) {
			return new UDFProperties();
		}
	}

	/**
	 * @see railo.runtime.type.UDF#getFunctionArguments()
	 */
	public FunctionArgument[] getFunctionArguments() {
		return arguments;
	}

	/**
	 * @see railo.runtime.type.UDF#getFunctionName()
	 */
	public String getFunctionName() {
		return name;
	}

	/**
	 * @see railo.runtime.type.UDF#getOwnerComponent()
	 */
	public Component getOwnerComponent() {
		return component;
	}
	
	/**
	 * @see railo.runtime.type.UDF#getPage()
	 */
	public Page getPage() {
		throw new PageRuntimeException(new DeprecatedException("method getPage():Page is no longer suppoted, use instead getPageSource():PageSource"));
        //return component.getPage();
	}

	/**
	 * @see railo.runtime.type.UDF#getOutput()
	 */
	public boolean getOutput() {
		return false;
	}

	/**
	 * @see railo.runtime.component.Member#getAccess()
	 */
	public int getAccess() {
		return Component.ACCESS_PUBLIC;
	}

	/**
	 * @see railo.runtime.type.UDF#getDisplayName()
	 */
	public String getDisplayName() {
		return "";
	}

	/**
	 * @see railo.runtime.type.UDF#getDescription()
	 */
	public String getDescription() {
		return "";
	}

	/**
	 * @see railo.runtime.type.UDF#getHint()
	 */
	public String getHint() {
		return "";
	}

	/**
	 * @see railo.runtime.type.UDF#getReturnFormat()
	 */
	public int getReturnFormat() {
		return UDFImpl.RETURN_FORMAT_WDDX;
	}

	/**
	 * @see railo.runtime.type.UDF#getReturnType()
	 */
	public int getReturnType() {
		return CFTypes.toShortStrict(getReturnTypeAsString(),CFTypes.TYPE_UNKNOW);
	}

	/**
	 * @see railo.runtime.component.Member#getValue()
	 */
	public Object getValue() {
		return this;
	}
	
	/**
	 * @see railo.runtime.type.UDF#getSecureJson()
	 */
	public Boolean getSecureJson() {
		return null;
	}

	/**
	 * @see railo.runtime.type.UDF#getVerifyClient()
	 */
	public Boolean getVerifyClient() {
		return null;
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return UDFImpl.toDumpData(pageContext, maxlevel, properties, this);
	}
	
	/**
	 * @see railo.runtime.type.UDF#getMetaData(railo.runtime.PageContext)
	 */
	public Struct getMetaData(PageContext pc) throws PageException {
		return UDFImpl.getMetaData(pc, this);
	}
	
	

	final Object cast(FunctionArgument arg,Object value, int index) throws PageException {
		if(Decision.isCastableTo(arg.getType(),arg.getTypeAsString(),value)) 
			return value;
		throw new UDFCasterException(this,arg,value,index);
	}

}
