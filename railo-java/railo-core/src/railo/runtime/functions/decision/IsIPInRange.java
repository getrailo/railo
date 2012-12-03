package railo.runtime.functions.decision;

import java.io.IOException;

import railo.commons.net.IPRange;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;

public class IsIPInRange {
	public static boolean call(PageContext pc, Object ips,String ip) throws PageException {
		try {
			if(ips instanceof String)
				return IPRange.getInstance((String)ips).inRange(ip);
			
			Array arr = Caster.toArray(ips,null);
			if(arr==null) throw new FunctionException(pc, "IsIpRange", 1, "ips", "ips must be a string list or a string array");
			
			String[] _ips=new String[arr.size()];
			for(int i=0;i<_ips.length;i++){
				_ips[i]=Caster.toString(arr.getE(i+1),null);
				if(_ips[i]==null)
					throw new FunctionException(pc, "IsIpRange", 1, "ips", "element number "+(i+1)+" in ips array is not a string");
			}
			return IPRange.getInstance(_ips).inRange(ip);
			
		}
		catch(IOException e){
			throw Caster.toPageException(e);
		}
		
		
	}

}
