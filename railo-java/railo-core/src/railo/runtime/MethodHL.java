package railo.runtime;

import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFProperties;

public class MethodHL implements Method {

	public Object call(PageContext pageContext, Object[] args,
			boolean doIncludePath) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object callWithNamedValues(PageContext pageContext, Struct values,
			boolean doIncludePath) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public UDF duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getDefaultValue(PageContext pc, int index)
			throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	public FunctionArgument[] getFunctionArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFunctionName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHint() {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct getMetaData(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getOutput() {
		// TODO Auto-generated method stub
		return false;
	}

	public Component getOwnerComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	public Page getPage() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getReturnFormat() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getReturnType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getReturnTypeAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean getSecureJson() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean getVerifyClient() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object implementation(PageContext pageContext) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getAccess() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public UDFProperties getUDFProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
