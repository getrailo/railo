package railo.intergral.fusiondebug.server.type;

import java.util.List;

import railo.intergral.fusiondebug.server.type.coll.FDUDF;
import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.type.UDF;

import com.intergral.fusiondebug.server.IFDStackFrame;

public abstract class FDNodeValueSupport extends FDValueSupport {
	
	private IFDStackFrame frame;

	public FDNodeValueSupport(IFDStackFrame frame){
		this.frame=frame;
	}
	
	public List getChildren() {
		return getChildren(frame,getName(),getRawValue());
	}
	

	/*public IFDValue getValue() {
		Object value = getRawValue();
		if(isSimpleValue(value))
			return getFDNodeVariableSupport();
		return FDCaster.toFDVariable(getName(), value).getValue();
	}*/

	@Override
	public String toString() {
		Object raw = getRawValue();
		if(raw instanceof UDF)return FDUDF.toString((UDF)raw);
		return FDCaster.serialize(raw);
	}
	
	@Override
	public boolean hasChildren() {
		return hasChildren(getRawValue());
	}

	protected abstract Object getRawValue();
	//protected abstract FDNodeValueSupport getFDNodeVariableSupport();
}
