package railo.runtime.type.trace;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.debug.Debugger;
import railo.runtime.exp.PageException;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.util.UDFUtil;

public class TOUDF extends TOObjects implements UDFPlus {

	private UDF udf;
	
	protected TOUDF(Debugger debugger,UDF udf, int type, String category, String text) {
		super(debugger,udf,type,category,text);
	}
	
	

	@Override
	public int getAccess() {
		log(null);
		return udf.getAccess();
	}


	@Override
	public Object getValue() {
		log(null);
		return udf.getValue();
	}


	@Override
	public Object implementation(PageContext pageContext) throws Throwable {
		log(null);
		return udf.implementation(pageContext);
	}


	@Override
	public FunctionArgument[] getFunctionArguments() {
		log(null);
		return udf.getFunctionArguments();
	}


	@Override
	public Object getDefaultValue(PageContext pc, int index)
			throws PageException {
		log(null);
		return udf.getDefaultValue(pc, index);
	}
	@Override
	public Object getDefaultValue(PageContext pc, int index, Object defaultValue) throws PageException {
		log(null);
		return UDFUtil.getDefaultValue(pc, (UDFPlus)udf, index, defaultValue);
	}


	@Override
	public String getFunctionName() {
		log(null);
		return udf.getFunctionName();
	}


	@Override
	public boolean getOutput() {
		log(null);
		return udf.getOutput();
	}


	@Override
	public int getReturnType() {
		log(null);
		return udf.getReturnType();
	}


	@Override
	public int getReturnFormat() {
		log(null);
		return udf.getReturnFormat();
	}


	@Override
	public Boolean getSecureJson() {
		log(null);
		return udf.getSecureJson();
	}


	@Override
	public Boolean getVerifyClient() {
		log(null);
		return udf.getVerifyClient();
	}


	@Override
	public String getReturnTypeAsString() {
		log(null);
		return udf.getReturnTypeAsString();
	}


	@Override
	public String getDescription() {
		log(null);
		return udf.getDescription();
	}


	@Override
	public Object callWithNamedValues(PageContext pageContext, Struct values,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf.callWithNamedValues(pageContext, values, doIncludePath);
	}


	@Override
	public Object call(PageContext pageContext, Object[] args,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf.call(pageContext, args, doIncludePath);
	}


	@Override
	public String getDisplayName() {
		log(null);
		return udf.getDisplayName();
	}


	@Override
	public String getHint() {
		log(null);
		return udf.getHint();
	}

	@Override
	public PageSource getPageSource() {
		log(null);
		return udf.getPageSource();
	}
	
	public int getIndex(){
		log(null);
		return ((UDFPlus)udf).getIndex();
	}


	@Override
	public Struct getMetaData(PageContext pc) throws PageException {
		log(null);
		return udf.getMetaData(pc);
	}

	@Override
	public UDF duplicate() {
		log(null);
		return udf.duplicate();
	}


	@Override
	public Component getOwnerComponent() {
		log(null);
		return udf.getOwnerComponent();
	}
	
	
}
