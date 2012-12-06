package railo.intergral.fusiondebug.server.type.simple;

import java.util.List;

import com.intergral.fusiondebug.server.IFDStackFrame;
import com.intergral.fusiondebug.server.IFDValue;
import com.intergral.fusiondebug.server.IFDVariable;


public class FDSimpleVariable implements IFDVariable {

	private String name;
	private IFDValue value;
	private IFDStackFrame frame;
	
	/**
	 * Constructor of the class
	 * @param frame 
	 * @param name
	 * @param value
	 * @param children
	 */
	public FDSimpleVariable(IFDStackFrame frame, String name, IFDValue value) {
		this.frame = frame;
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Constructor of the class
	 * @param name
	 * @param value
	 * @param children
	 */
	public FDSimpleVariable(IFDStackFrame frame,String name, String value,List children) {
		this(frame,name,new FDSimpleValue(children,value));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IFDValue getValue() {
		return value;
	}

	@Override
	public IFDStackFrame getStackFrame() {
		return frame;
	}

}
