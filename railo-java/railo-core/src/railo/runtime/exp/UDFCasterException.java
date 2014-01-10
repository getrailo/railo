package railo.runtime.exp;

import railo.runtime.type.FunctionArgument;
import railo.runtime.type.UDF;
import railo.runtime.type.util.Type;

public class UDFCasterException extends CasterException {

	private static final long serialVersionUID = 4863042711433241644L;

	public UDFCasterException(UDF udf, FunctionArgument arg, Object value, int index) {
		super(createMessage(udf,arg,value,index));
	}

	public UDFCasterException(UDF udf, String returnType, Object value) {
		super(createMessage(udf,returnType,value));
	}

    private static String createMessage(UDF udf, String type, Object value) {
    	String detail;
    	if(value instanceof String) return "can't cast String ["+value+"] to a value of type ["+type+"]";
    	else if(value!=null) detail= "can't cast Object type ["+Type.getName(value)+"] to a value of type ["+type+"]";
		else detail= "can't cast null value to value of type ["+type+"]";
		return "the function "+udf.getFunctionName()+" has an invalid return value , "+detail;

    }   

	private static String createMessage(UDF udf, FunctionArgument arg, Object value, int index) {
		String detail;
		if(value instanceof String) detail= "can't cast String ["+value+"] to a value of type ["+arg.getTypeAsString()+"]";
		else if(value!=null) detail= "can't cast Object type ["+Type.getName(value)+"] to a value of type ["+arg.getTypeAsString()+"]";
		else detail= "can't cast Null value to value of type ["+arg.getTypeAsString()+"]";
		
		
		
		return "invalid call of the function "+udf.getFunctionName()+" ("+udf.getPageSource().getDisplayPath()+"), "+posToString(index)+" Argument ("+arg.getName()+") is of invalid type, "+detail;
	}
	
	private static String posToString(int index) {
		if(index==1) return "first";
		if(index==2) return "second";
		return index+"th";
	}
}
