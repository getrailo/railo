package railo.intergral.fusiondebug.server.util;

import railo.intergral.fusiondebug.server.type.coll.FDCollection;
import railo.intergral.fusiondebug.server.type.coll.FDUDF;
import railo.intergral.fusiondebug.server.type.nat.FDNative;
import railo.intergral.fusiondebug.server.type.qry.FDQuery;
import railo.intergral.fusiondebug.server.type.simple.FDSimpleValue;
import railo.runtime.converter.ScriptConverter;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.UDF;

import com.intergral.fusiondebug.server.IFDStackFrame;
import com.intergral.fusiondebug.server.IFDValue;

public class FDCaster {
	

	public static IFDValue toFDValue(IFDStackFrame frame,String name,Object value) {
		if(value instanceof UDF)
			return new FDUDF(frame,name, (UDF)value);
		if(value instanceof Query)
			return new FDQuery(frame, (Query)value);
		//if(value instanceof Array)
		//	return new FDArray(frame,name,(Array)value);
		if(value instanceof Collection)
			return new FDCollection(frame,name,(Collection)value);
		if(Decision.isCastableToString(value))
			return new FDSimpleValue(null,Caster.toString(value,null));
		return new FDNative(frame,name,value);
	}
	
	public static IFDValue toFDValue(IFDStackFrame frame,Object value) {
		return toFDValue(frame,"", value);
	}

	/**
	 * translate a object to its string representation
	 * @param object
	 * @return
	 */
	public static String serialize(Object object) {
		if(object==null) return "[null]";
		try {
			return new ScriptConverter().serialize(object);
		} 
		catch (Throwable t) {
			return object.toString();
		}
	}

	public static Object unserialize(String value) {
		// TODO
		return value;
	}
}
