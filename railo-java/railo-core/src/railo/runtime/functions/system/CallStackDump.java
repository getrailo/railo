package railo.runtime.functions.system;

import java.io.IOException;
import java.util.Iterator;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Struct;
import railo.runtime.type.util.KeyConstants;

public class CallStackDump {

	public static String call(PageContext pc) throws PageException {
		return call(pc,null);
	}

	public static String call(PageContext pc, String output) throws PageException {
		Array arr = CallStackGet.call(pc);
		Struct sct=null;
		String func;
		
		// create stack
		StringBuilder sb=new StringBuilder();
		Iterator<Object> it = arr.valueIterator();
		while(it.hasNext()){
			sct=(Struct) it.next();
			func=(String) sct.get(KeyConstants._function);
			sb.append(sct.get(KeyConstants._template));
			if(func.length()>0) {
				sb.append(':');
				sb.append(func);
			}
			sb.append(':');
			sb.append(Caster.toString(sct.get(CallStackGet.LINE_NUMBER)));
			sb.append('\n');
		}
		
		// output
		try{
		if(StringUtil.isEmpty(output,true) || output.trim().equalsIgnoreCase("browser")) {
			pc.forceWrite("<pre>");
			pc.forceWrite(sb.toString());
			pc.forceWrite("</pre>");
		}
		else if(output.trim().equalsIgnoreCase("console")) {
			System.out.println(sb.toString());
		}
		else {
			Resource res = ResourceUtil.toResourceNotExisting(pc, output);
			IOUtil.write(
					res, 
					sb.toString()+"\n", 
					pc.getConfig().getResourceCharset(), true);
		}
		}
		catch(IOException ioe){
			throw Caster.toPageException(ioe);
		}
		
		return null;
	}
}
