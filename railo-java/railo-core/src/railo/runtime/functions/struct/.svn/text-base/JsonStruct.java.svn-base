package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;

public class JsonStruct implements Function {
	public static Struct call(PageContext pc , Object[] objArr) throws PageException {
		return Struct_._call(objArr, "invalid argument for JSON struct, only named arguments are allowed like {name:\"value\",name2:\"value2\"}");
    	
	}
}
