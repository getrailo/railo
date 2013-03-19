package railo.runtime.functions.file;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileAppend {

	public static String call(PageContext pc, String path, Object data) throws PageException {
		return call(pc,path,data,pc.getConfig().getResourceCharset());
	}
	
	public static String call(PageContext pc, String path, Object data,String charset) throws PageException {
		FileStreamWrapper fsw=null;
		if(StringUtil.isEmpty(charset,true))
			charset=pc.getConfig().getResourceCharset();
		
		try {
			Resource res = Caster.toResource(pc,path,false);
			pc.getConfig().getSecurityManager().checkFileLocation(res);
			fsw=new FileStreamWrapperWrite(res,charset,true,false);
			fsw.write(data);	
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
		finally {
			closeEL(fsw);
		}
		return null;
	}

	private static void closeEL(FileStreamWrapper fsw) {
		if(fsw==null)return;
		try {
			fsw.close();
		} catch (Throwable t) {}
	}
}