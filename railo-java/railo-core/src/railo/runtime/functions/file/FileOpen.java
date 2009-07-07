package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;

public class FileOpen {

	public static Object call(PageContext pc, String path) throws PageException {
		return call(pc, path,"read",pc.getConfig().getResourceCharset());
	}
	public static Object call(PageContext pc, String path,String mode) throws PageException {
		return call(pc, path,mode,pc.getConfig().getResourceCharset());
	}
	
	public static Object call(PageContext pc, String path,String strMode, String charset) throws PageException {
		strMode=strMode.trim().toLowerCase();
		
		//try {
			if("read".equals(strMode)) {
				return new FileStreamWrapperRead(check(pc,ResourceUtil.toResourceExisting(pc, path)),charset);
			}
			if("readbinary".equals(strMode)) {
				return new FileStreamWrapperReadBinary(check(pc,ResourceUtil.toResourceExisting(pc, path)));
			}
			if("write".equals(strMode)) {
				return new FileStreamWrapperWrite(check(pc,ResourceUtil.toResourceNotExisting(pc, path)),charset,false);
			}
			if("append".equals(strMode)) {
				return new FileStreamWrapperWrite(check(pc,ResourceUtil.toResourceNotExisting(pc, path)),charset,true);
			}
			
			
			throw new FunctionException(pc,"FileOpen",2,"mode","invalid value ["+strMode+"], valid values for argument mode are [read,readBinary,append,write]");
		
		
		
	}
	private static Resource check(PageContext pc, Resource res) throws PageException {
        pc.getConfig().getSecurityManager().checkFileLocation(res);
		return res;
	}
	
}
