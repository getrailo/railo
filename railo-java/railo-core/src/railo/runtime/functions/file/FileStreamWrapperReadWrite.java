package railo.runtime.functions.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;

public class FileStreamWrapperReadWrite extends FileStreamWrapper {
	
	//private BufferedOutputStream bos;
	private String charset;
	private boolean seekable;
	private RandomAccessFile raf;
	private boolean isEOF;

	public FileStreamWrapperReadWrite(Resource res, String charset,boolean seekable) {
		super(res);
		
		this.charset=charset;
		this.seekable=seekable;
	}
	

	@Override
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
		
		if(bytes!=null){
			getRAF().write(bytes);
		}
		else if(is!=null){
			writeToRAF(is, getRAF());
		}
		else
			throw new IOException("can't write down object of type ["+Caster.toTypeName(obj)+"] to resource ["+res+"]");
		
		
		
		
	}

	public void close() throws IOException {
		if(raf!=null)raf.close();
	}

	@Override
	public String getMode() {
		return "readwrite";
	}
	
	@Override
	public Object read(int len) throws IOException {
		byte[] barr=new byte[len];
		len=getRAF().read(barr);
		if(len!=barr.length) {
			byte[] rtn=new byte[len];
			for(int i=0;i<len;i++) {
				rtn[i]=barr[i];
			}
			barr=rtn;
			isEOF=true;
		}
		return barr;
	}



	@Override
	public boolean isEndOfFile() {
		return isEOF;
	}

	@Override
	public long getSize() {
		return res.length();
	}
	
	@Override
	public void skip(int len) throws PageException {
		try {
			getRAF().skipBytes(len);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
	public void seek(long pos) throws PageException {
		try {
			getRAF().seek(pos);
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
	
	public static void writeToRAF(InputStream is, RandomAccessFile raf) throws IOException   {  
        
        byte[] buffer = new byte[2048];  
        int tmp=0;  
   
        while ((tmp = is.read(buffer)) != -1)   {  
          raf.write(buffer, 0, tmp);  
        }   
    } 
	
	private RandomAccessFile getRAF() throws IOException {
		if(raf==null){
			if(!(res instanceof File))
				throw new IOException("only resources for local filesytem support seekable");
			raf = new RandomAccessFile((File)res,"rw");
			
		}
		return raf;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
}
