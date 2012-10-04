package railo.runtime.converter;

import railo.runtime.op.Caster;

public abstract class ConverterSupport implements StringConverter {

	
	public static ConverterException toConverterException(Exception e){
		ConverterException ce=new ConverterException(Caster.toClassName(e)+":"+e.getMessage());
		ce.setStackTrace(e.getStackTrace());
		return ce;
	}

}
