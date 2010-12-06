package railo.runtime.functions.file;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;

public class FileOpen {

	public static Object call(PageContext pc, String path) throws PageException {
		return call(pc, path,"read",pc.getConfig().getResourceCharset(),false);
	}
	public static Object call(PageContext pc, String path,String mode) throws PageException {
		return call(pc, path,mode,pc.getConfig().getResourceCharset(),false);
	}

	public static Object call(PageContext pc, String path,String strMode, String charset) throws PageException {
		return call(pc, path,strMode,charset,false);
	}
	
	public static Object call(PageContext pc, String path,String strMode, String charset,boolean seekable) throws PageException {
		
		strMode=strMode.trim().toLowerCase();
		if(StringUtil.isEmpty(charset,true))
			charset=pc.getConfig().getResourceCharset();
		//try {

			if("read".equals(strMode)) {
				return new FileStreamWrapperRead(check(pc,ResourceUtil.toResourceExisting(pc, path)),charset,seekable);
			}
			if("readbinary".equals(strMode)) {
				return new FileStreamWrapperReadBinary(check(pc,ResourceUtil.toResourceExisting(pc, path)),seekable);
			}
			if("write".equals(strMode)) {
				return new FileStreamWrapperWrite(check(pc,ResourceUtil.toResourceNotExisting(pc, path)),charset,false,seekable);
			}
			if("append".equals(strMode)) {
				return new FileStreamWrapperWrite(check(pc,ResourceUtil.toResourceNotExisting(pc, path)),charset,true,seekable);
			}
			if("readwrite".equals(strMode)) {
				return new FileStreamWrapperReadWrite(check(pc,ResourceUtil.toResourceNotExisting(pc, path)),charset,seekable);
			}
			
			
			throw new FunctionException(pc,"FileOpen",2,"mode","invalid value ["+strMode+"], valid values for argument mode are [read,readBinary,append,write,readwrite]");
		
		
		
	}
	private static Resource check(PageContext pc, Resource res) throws PageException {
        pc.getConfig().getSecurityManager().checkFileLocation(res);
		return res;
	}
	
}
