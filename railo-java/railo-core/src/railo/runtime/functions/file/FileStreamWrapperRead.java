package railo.runtime.functions.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;

public class FileStreamWrapperRead extends FileStreamWrapper {
	

	private BufferedReader br;
	private String charset;
	private boolean seekable;
	private RandomAccessFile raf;

	/**
	 * Constructor of the class
	 * @param res
	 * @param charset
	 * @throws IOException
	 */
	public FileStreamWrapperRead(Resource res, String charset,boolean seekable) {
		super(res);
		this.charset=charset;
		this.seekable=seekable;
	}
	
	@Override
	public Object read(int len) throws IOException {
		if(seekable) {
			byte[] barr=new byte[len];
            len = getRAF().read(barr);
            if(len==-1) throw new IOException("End of file reached");
            return new String(barr, 0, len, charset);
		}
		
		char[] carr=new char[len];
		len = _getBR().read(carr);
		if(len==-1) throw new IOException("End of file reached");
		
		return new String(carr,0,len);
	}

	@Override
	public String readLine() throws IOException {
		if(seekable) {
			return getRAF().readLine();
		}
		
		if(!_getBR().ready())
			throw new IOException(" End of file reached");
		return _getBR().readLine();
	}

	@Override
	public void close() throws IOException {
		super.setStatus(FileStreamWrapper.STATE_CLOSE);
		if(br!=null)br.close();
		if(raf!=null)raf.close();
	}

	@Override
	public String getMode() {
		return "read";
	}

	public boolean isEndOfFile() {
		if(seekable){
			long pos=0;
			try {
				pos = getRAF().getFilePointer();
			} catch (IOException ioe) {
				throw new PageRuntimeException(Caster.toPageException(ioe));
			}
			try {
				if(raf.read()==-1) return true;
				raf.seek(pos);
			} 
			catch (IOException e) {
				return true;
			}
			return false;
		}
		
		try {
			return !_getBR().ready();
		} catch (IOException e) {
			return true;
		}
	}

	@Override
	public long getSize() {
		return res.length();
	}

	@Override
	public void skip(int len) throws PageException {
		if(seekable){
			try {
				getRAF().skipBytes(len);
			} catch (IOException e) {
				throw Caster.toPageException(e);
			}
			return;
		}
		
		try {
			_getBR().skip(len);
			return;
		} 
		catch (IOException e) {}
			
		throw Caster.toPageException(new IOException("skip is only supported when you have set argument seekable of function fileOpen to true"));
	}
	public void seek(long pos) throws PageException {
		if(seekable){
			try {
				getRAF().seek(pos);
			} catch (IOException e) {
				throw Caster.toPageException(e);
			}
		}
		else throw Caster.toPageException(new IOException("seek is only supported when you have set argument seekable of function fileOpen to true"));
	}

	private RandomAccessFile getRAF() throws IOException {
		if(raf==null){
			if(!(res instanceof File))
				throw new IOException("only resources for local filesytem support seekable");
			
			raf = new RandomAccessFile((File)res,"r");
		}
		return raf;
	}


	private BufferedReader _getBR() throws IOException {
		if(br==null){
			br = IOUtil.toBufferedReader(IOUtil.getReader(res.getInputStream(),charset));
		}
		return br;
	}
}
