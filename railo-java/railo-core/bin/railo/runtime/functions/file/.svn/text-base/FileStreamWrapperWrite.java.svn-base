package railo.runtime.functions.file;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public class FileStreamWrapperWrite extends FileStreamWrapper {
	
	private BufferedOutputStream bos;
	private boolean append;
	private String charset;

	public FileStreamWrapperWrite(Resource res, String charset,boolean append) {
		super(res);
		
		this.charset=charset;
		this.append=append;
	}
	
	private BufferedOutputStream getOS() throws IOException{
		if(bos==null)
			bos = IOUtil.toBufferedOutputStream(res.getOutputStream(append));
		return bos;
	}

	/**
	 * @see railo.runtime.functions.file.FileStreamWrapper#write(java.lang.Object)
	 */
	public void write(Object obj) throws IOException {
		byte[] bytes = null;
		InputStream is=null;
		if(Decision.isBinary(obj)){
			bytes=Caster.toBinary(obj,null);
		}
		else if(obj instanceof FileStreamWrapper) {
			is=((FileStreamWrapper)obj).getResource().getInputStream();
		}
		else if(obj instanceof Resource) {
			is=((Resource)obj).getInputStream();
		}
		else if(Decision.isSimpleValue(obj)){
			String str = Caster.toString(obj,null);
			if(str!=null) bytes=str.getBytes(charset);
		}
		
		if(bytes!=null)
			getOS().write(bytes);
		else if(is!=null)
			IOUtil.copy(is, getOS(),true,false);
		else
			throw new IOException("can't write down object of type ["+Caster.toTypeName(obj)+"] to resource ["+res+"]");
		
		
		
		
	}

	public void close() throws IOException {
		if(bos!=null)bos.close();
	}

	/**
	 *
	 * @see railo.runtime.functions.file.FileStreamWrapper#getMode()
	 */
	public String getMode() {
		return append?"append":"write";
	}
}
