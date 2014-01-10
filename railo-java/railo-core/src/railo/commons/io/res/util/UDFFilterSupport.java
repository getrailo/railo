package railo.commons.io.res.util;

import railo.commons.lang.CFTypes;
import railo.runtime.exp.ExpressionException;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.UDF;

public abstract class UDFFilterSupport {

	protected UDF udf;
	protected Object[] args=new Object[1];
	
	public UDFFilterSupport(UDF udf) throws ExpressionException{
		this.udf=udf;
		
		// check UDF return type
		int type = udf.getReturnType();
		if(type!=CFTypes.TYPE_BOOLEAN && type!=CFTypes.TYPE_ANY)
			throw new ExpressionException("invalid return type ["+udf.getReturnTypeAsString()+"] for UDF Filter, valid return types are [boolean,any]");
		
		// check UDF arguments
		FunctionArgument[] args = udf.getFunctionArguments();
		if(args.length>1)
			throw new ExpressionException("UDF filter has to many arguments ["+args.length+"], should have at maximum 1 argument");
		
		if(args.length==1){
			type=args[0].getType();
			if(type!=CFTypes.TYPE_STRING && type!=CFTypes.TYPE_ANY)
				throw new ExpressionException("invalid type ["+args[0].getTypeAsString()+"] for first argument of UDF Filter, valid return types are [string,any]");
		}
		
		
	}
	
    @Override
	public String toString() {
		return "UDFFilter:"+udf;
	}
}
