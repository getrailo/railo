package railo.intergral.fusiondebug.server.type;

import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

import com.intergral.fusiondebug.server.IFDStackFrame;
import com.intergral.fusiondebug.server.IFDValue;
import com.intergral.fusiondebug.server.IFDVariable;

public class FDVariable implements IFDVariable {

	private Collection.Key name;
	private IFDValue value;
	private IFDStackFrame frame;

	public FDVariable(IFDStackFrame frame,String name,IFDValue value){
		this(frame,KeyImpl.getInstance(name), value);
	}
	
	/**
	 * Constructor of the class
	 * @param name
	 * @param value
	 * @param frame
	 */
	public FDVariable(IFDStackFrame frame,Collection.Key name,IFDValue value){
		this.name=name;
		this.value=value;
		this.frame=frame;
	}
	
	@Override
	public String getName() {
		return name.getString();
	}

	@Override
	public IFDStackFrame getStackFrame() {
		return frame;
	}

	@Override
	public IFDValue getValue() {
		return value;
	}
}
