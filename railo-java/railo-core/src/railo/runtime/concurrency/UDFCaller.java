package railo.runtime.concurrency;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;

public class UDFCaller extends Caller {

	private UDF udf;
	private boolean doIncludePath;
	private Object[] arguments;
	private Struct namedArguments;

	public UDFCaller(PageContext parent, UDF udf, Object[] arguments, boolean doIncludePath) {
		super(parent);
		this.udf=udf;
		this.arguments=arguments;
		this.doIncludePath=doIncludePath;
	}
	public UDFCaller(PageContext parent, UDF udf,Struct namedArguments, boolean doIncludePath) {
		super(parent);
		this.udf=udf;
		this.namedArguments=namedArguments;
		this.doIncludePath=doIncludePath;
	}

	@Override
	public void _call(PageContext parent,PageContext pc) throws PageException {
		if(namedArguments!=null) udf.callWithNamedValues(pc, namedArguments, doIncludePath);
		else udf.call(pc, arguments, doIncludePath);
	}

}
