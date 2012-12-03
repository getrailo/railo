package railo.runtime.type.trace;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.debug.Debugger;
import railo.runtime.exp.PageException;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;

public class TOUDF extends TOObjects implements UDF {

	private UDF udf;
	
	protected TOUDF(Debugger debugger,UDF udf, int type, String category, String text) {
		super(debugger,udf,type,category,text);
	}
	
	

	/**
	 * @see railo.runtime.component.Member#getAccess()
	 */
	public int getAccess() {
		log(null);
		return udf.getAccess();
	}


	/**
	 * @see railo.runtime.component.Member#getValue()
	 */
	public Object getValue() {
		log(null);
		return udf.getValue();
	}


	/**
	 * @see railo.runtime.type.UDF#implementation(railo.runtime.PageContext)
	 */
	public Object implementation(PageContext pageContext) throws Throwable {
		log(null);
		return udf.implementation(pageContext);
	}


	/**
	 * @see railo.runtime.type.UDF#getFunctionArguments()
	 */
	public FunctionArgument[] getFunctionArguments() {
		log(null);
		return udf.getFunctionArguments();
	}


	/**
	 * @see railo.runtime.type.UDF#getDefaultValue(railo.runtime.PageContext, int)
	 */
	public Object getDefaultValue(PageContext pc, int index)
			throws PageException {
		log(null);
		return udf.getDefaultValue(pc, index);
	}


	/**
	 * @see railo.runtime.type.UDF#getFunctionName()
	 */
	public String getFunctionName() {
		log(null);
		return udf.getFunctionName();
	}


	/**
	 * @see railo.runtime.type.UDF#getOutput()
	 */
	public boolean getOutput() {
		log(null);
		return udf.getOutput();
	}


	/**
	 * @see railo.runtime.type.UDF#getReturnType()
	 */
	public int getReturnType() {
		log(null);
		return udf.getReturnType();
	}


	/**
	 * @see railo.runtime.type.UDF#getReturnFormat()
	 */
	public int getReturnFormat() {
		log(null);
		return udf.getReturnFormat();
	}


	/**
	 * @see railo.runtime.type.UDF#getSecureJson()
	 */
	public Boolean getSecureJson() {
		log(null);
		return udf.getSecureJson();
	}


	/**
	 * @see railo.runtime.type.UDF#getVerifyClient()
	 */
	public Boolean getVerifyClient() {
		log(null);
		return udf.getVerifyClient();
	}


	/**
	 * @see railo.runtime.type.UDF#getReturnTypeAsString()
	 */
	public String getReturnTypeAsString() {
		log(null);
		return udf.getReturnTypeAsString();
	}


	/**
	 * @see railo.runtime.type.UDF#getDescription()
	 */
	public String getDescription() {
		log(null);
		return udf.getDescription();
	}


	/**
	 * @see railo.runtime.type.UDF#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Struct, boolean)
	 */
	public Object callWithNamedValues(PageContext pageContext, Struct values,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf.callWithNamedValues(pageContext, values, doIncludePath);
	}


	/**
	 * @see railo.runtime.type.UDF#call(railo.runtime.PageContext, java.lang.Object[], boolean)
	 */
	public Object call(PageContext pageContext, Object[] args,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf.call(pageContext, args, doIncludePath);
	}


	/**
	 * @see railo.runtime.type.UDF#getDisplayName()
	 */
	public String getDisplayName() {
		log(null);
		return udf.getDisplayName();
	}


	/**
	 * @see railo.runtime.type.UDF#getHint()
	 */
	public String getHint() {
		log(null);
		return udf.getHint();
	}

	/**
	 * @see railo.runtime.type.UDF#getPageSource()
	 */
	public PageSource getPageSource() {
		log(null);
		return udf.getPageSource();
	}


	/**
	 * @see railo.runtime.type.UDF#getMetaData(railo.runtime.PageContext)
	 */
	public Struct getMetaData(PageContext pc) throws PageException {
		log(null);
		return udf.getMetaData(pc);
	}

	/**
	 * @see railo.runtime.type.UDF#duplicate()
	 */
	public UDF duplicate() {
		log(null);
		return udf.duplicate();
	}


	/**
	 * @see railo.runtime.type.UDF#getOwnerComponent()
	 */
	public Component getOwnerComponent() {
		log(null);
		return udf.getOwnerComponent();
	}
	
	
}
