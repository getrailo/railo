package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileWriteLine {

	public static String call(PageContext pc, Object obj, String text) throws PageException {
		FileStreamWrapper fsw=null;
		boolean close=false;
		try {
			try {
				if(obj instanceof FileStreamWrapper) {
					fsw=(FileStreamWrapper)obj;
				}
				else {
					close=true;
					Resource res = Caster.toResource(pc,obj,false);
					pc.getConfig().getSecurityManager().checkFileLocation(res);
					fsw=new FileStreamWrapperWrite(res,pc.getConfig().getResourceCharset(),false,false);
				}
				fsw.write(text+"\n");
			}
			finally {
				if(close && fsw!=null)fsw.close();
			}
			
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
		return null;
	}
}
