package railo.runtime;

import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;
import railo.runtime.type.UDFImpl;

public class MethodSL implements Method {
	private ComponentImpl component;
	private UDF udf;

	public MethodSL(ComponentImpl component,UDF udf){
		// TODO when MethodHL translate to UDFImpl
		if(udf instanceof UDFImpl){
			this.udf=udf;
		}
		else if(udf instanceof MethodSL){
			this.udf=((MethodSL)udf).udf;
		}
		// TODO are there really only this 3?
		else {
			this.udf=new UDFImpl(((MethodHL)udf).getUDFProperties());
		}
		this.component=component;
		
	}
	
	/**
	 * @see railo.runtime.type.UDF#call(railo.runtime.PageContext, java.lang.Object[], boolean)
	 */
	public Object call(PageContext pc, Object[] args,boolean doIncludePath) throws PageException {
		return component._call(pc, udf, null, args);
	}

	/**
	 * @see railo.runtime.type.UDF#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Struct, boolean)
	 */
	public Object callWithNamedValues(PageContext pc, Struct values,boolean doIncludePath) throws PageException {
		return component._call(pc, udf, values, null);
	}

	/**
	 * @see railo.runtime.type.UDF#duplicate()
	 */
	public UDF duplicate() {
		return new MethodSL(component,udf.duplicate());
	}

	/**
	 * @see railo.runtime.type.UDF#getDefaultValue(railo.runtime.PageContext, int)
	 */
	public Object getDefaultValue(PageContext pc, int index) throws PageException {
		return udf.getDefaultValue(pc, index);
	}

	/**
	 * @see railo.runtime.type.UDF#getDescription()
	 */
	public String getDescription() {
		return udf.getDescription();
	}

	/**
	 * @see railo.runtime.type.UDF#getDisplayName()
	 */
	public String getDisplayName() {
		return udf.getDisplayName();
	}

	/**
	 * @see railo.runtime.type.UDF#getFunctionArguments()
	 */
	public FunctionArgument[] getFunctionArguments() {
		return udf.getFunctionArguments();
	}

	/**
	 * @see railo.runtime.type.UDF#getFunctionName()
	 */
	public String getFunctionName() {
		return udf.getFunctionName();
	}

	/**
	 * @see railo.runtime.type.UDF#getHint()
	 */
	public String getHint() {
		return udf.getHint();
	}

	/**
	 * @see railo.runtime.type.UDF#getMetaData(railo.runtime.PageContext)
	 */
	public Struct getMetaData(PageContext pc) throws PageException {
		return udf.getMetaData(pc);
	}

	/**
	 * @see railo.runtime.type.UDF#getOutput()
	 */
	public boolean getOutput() {
		return udf.getOutput();
	}

	/**
	 * @see railo.runtime.type.UDF#getOwnerComponent()
	 */
	public Component getOwnerComponent() {
		return udf.getOwnerComponent();
	}

	public Page getPage() {
		return udf.getPage();
	}

	/**
	 * @see railo.runtime.type.UDF#getReturnFormat()
	 */
	public int getReturnFormat() {
		return udf.getReturnFormat();
	}

	/**
	 * @see railo.runtime.type.UDF#getReturnType()
	 */
	public int getReturnType() {
		return udf.getReturnType();
	}

	/**
	 * @see railo.runtime.type.UDF#getReturnTypeAsString()
	 */
	public String getReturnTypeAsString() {
		return udf.getReturnTypeAsString();
	}

	/**
	 * @see railo.runtime.type.UDF#getSecureJson()
	 */
	public Boolean getSecureJson() {
		return udf.getSecureJson();
	}

	/**
	 * @see railo.runtime.type.UDF#getVerifyClient()
	 */
	public Boolean getVerifyClient() {
		return udf.getVerifyClient();
	}

	/**
	 * @see railo.runtime.type.UDF#implementation(railo.runtime.PageContext)
	 */
	public Object implementation(PageContext pageContext) throws Throwable {
		return udf.implementation(pageContext);
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return udf.toDumpData(pageContext, maxlevel, properties);
	}

	/**
	 * @see railo.runtime.component.Member#getAccess()
	 */
	public int getAccess() {
		return udf.getAccess();
	}

	/**
	 * @see railo.runtime.component.Member#getValue()
	 */
	public Object getValue() {
		return udf.getValue();
	}

}
