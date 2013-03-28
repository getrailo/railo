package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileWrite {

	public static String call(PageContext pc, Object obj, Object data) throws PageException {
		return call(pc,obj,data,pc.getConfig().getResourceCharset());
	}
	
	public static String call(PageContext pc, Object obj, Object data,String charset) throws PageException {
		FileStreamWrapper fsw=null;
		boolean close=false;
		if(StringUtil.isEmpty(charset,true))
			charset=pc.getConfig().getResourceCharset();
		try {
			try {
				if(obj instanceof FileStreamWrapper) {
					fsw=(FileStreamWrapper)obj;
				}
				else {
					close=true;
					Resource res = Caster.toResource(pc,obj,false);
					pc.getConfig().getSecurityManager().checkFileLocation(res);
					fsw=new FileStreamWrapperWrite(res,charset,false,false);
				}
				fsw.write(data);
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
