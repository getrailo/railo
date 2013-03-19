package railo.runtime.functions.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public class FileRead {

	public static Object call(PageContext pc, Object path) throws PageException {
		return _call(pc,Caster.toResource(pc,path,true),pc.getConfig().getResourceCharset());
	}
	
	public static Object call(PageContext pc, Object obj, Object charsetOrSize) throws PageException {
		if(charsetOrSize==null) return call(pc, obj);
		
		if(obj instanceof FileStreamWrapper) {
			return _call((FileStreamWrapper)obj,Caster.toIntValue(charsetOrSize));
		}
		Resource res = Caster.toResource(pc,obj,true);
		String charset=Caster.toString(charsetOrSize);
		if(Decision.isInteger(charset)){
			charset=pc.getConfig().getResourceCharset();
			return _call(pc,res,charset,Caster.toIntValue(charset));
		}
		
		return _call(pc,res,charset);
	}

	private static Object _call(FileStreamWrapper fs, int size) throws PageException {
		try {
			return fs.read(size);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	private static Object _call(PageContext pc,Resource res, String charset) throws PageException {
		pc.getConfig().getSecurityManager().checkFileLocation(res);
		try {
			return IOUtil.toString(res,charset);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
	
	private static Object _call(PageContext pc, Resource res, String charset,int size) throws PageException {
		pc.getConfig().getSecurityManager().checkFileLocation(res);
		
		InputStream is=null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is=res.getInputStream();
			IOUtil.copy(is, baos, 0, size);
			return new String(baos.toByteArray(),charset);
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
		finally {
			IOUtil.closeEL(is);
		}
		
		
		// TODO Auto-generated method stub
	}
	
}
