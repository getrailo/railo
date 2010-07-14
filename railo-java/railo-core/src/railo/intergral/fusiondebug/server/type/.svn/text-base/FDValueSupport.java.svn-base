package railo.intergral.fusiondebug.server.type;

import java.util.List;

import railo.intergral.fusiondebug.server.util.FDCaster;
import railo.runtime.op.Decision;

import com.intergral.fusiondebug.server.IFDStackFrame;
import com.intergral.fusiondebug.server.IFDValue;

public abstract class FDValueSupport implements IFDValue {
	
	protected boolean isSimpleValue(Object value) {
		return Decision.isSimpleValue(value);
	}
	
	public boolean hasChildren(Object value) {
		return !isSimpleValue(value);
	}

	public List getChildren(IFDStackFrame frame,String name,Object value) {
		if(isSimpleValue(value))return null;
		return FDCaster.toFDValue(frame,name, value).getChildren();
	}

	public abstract String getName();
}