package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;

public class JsonStruct implements Function {

	private static final long serialVersionUID = 3030769464899375329L;

	public static Struct call(PageContext pc , Object[] objArr) throws PageException {
		return Struct_._call(objArr, "invalid argument for JSON struct, only named arguments are allowed like {name:\"value\",name2:\"value2\"}");
    	
	}
}
