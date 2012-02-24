package railo.runtime.functions.xml;

import org.apache.axis.AxisFault;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.net.rpc.AxisUtil;
import railo.runtime.op.Caster;

public class AddSOAPResponseHeader {
	public static boolean call(PageContext pc, String nameSpace, String name, Object value) throws PageException {
		return call(pc, nameSpace, name, value,false);
	}
	public static boolean call(PageContext pc, String nameSpace, String name, Object value, boolean mustUnderstand) throws PageException {
		try {
			AxisUtil.addSOAPResponseHeader(nameSpace, name, value, mustUnderstand);
		} 
		catch (AxisFault e) {
			throw Caster.toPageException(e);
		}
		return true;
	}
}
