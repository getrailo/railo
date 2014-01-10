package railo.runtime.type.trace;

import railo.runtime.Component;
import railo.runtime.ComponentImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.component.Member;
import railo.runtime.debug.Debugger;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.UDFUtil;

public class TOUDF extends TOObjects implements UDFPlus,Member {

	private UDFPlus udf;
	
	protected TOUDF(Debugger debugger,UDFPlus udf, int type, String category, String text) {
		super(debugger,udf,type,category,text);
		this.udf=udf;
	}
	
	

	@Override
	public int getAccess() {
		log(null);
		return udf.getAccess();
	}
	
	public void setAccess(int access) {
		log(ComponentUtil.toStringAccess(access,null));
		udf.setAccess(access);
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
		return UDFUtil.getDefaultValue(pc, udf, index, defaultValue);
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
	public int getReturnFormat(int defaultValue) {
		log(null);
		return udf.getReturnFormat(defaultValue);
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
	public Object callWithNamedValues(PageContext pageContext, Collection.Key calledName, Struct values,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf.callWithNamedValues(pageContext, calledName, values, doIncludePath);
	}


	@Override
	public Object call(PageContext pageContext, Object[] args,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf.call(pageContext, args, doIncludePath);
	}


	@Override
	public Object call(PageContext pageContext, Collection.Key calledName, Object[] args,
			boolean doIncludePath) throws PageException {
		log(null);
		return udf.call(pageContext,calledName, args, doIncludePath);
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
		return udf.getIndex();
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
	public void setOwnerComponent(ComponentImpl cfc) {
		log(null);
		udf.setOwnerComponent(cfc);
	}
	
	
}
