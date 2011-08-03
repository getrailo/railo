package railo.transformer.bytecode.statement.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class DataBag {
	//public final BytecodeContext bc;
	public Stack<Object> rtn=new Stack<Object>();
	//public final Label start;
	//public final Label end;
	public Map<String, Integer> locals;
	
	public DataBag() {
		//this.start = start;
		//this.end = end;
		this.locals=new HashMap<String, Integer>();
	}
	
}
