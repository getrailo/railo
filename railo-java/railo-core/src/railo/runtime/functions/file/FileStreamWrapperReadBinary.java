package railo.runtime.functions.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class FileStreamWrapperReadBinary extends FileStreamWrapper {
	


	private BufferedInputStream bis;
	private boolean isEOF;
	private boolean seekable;
	private RandomAccessFile raf;

	/**
	 * Constructor of the class
	 * @param res
	 * @param charset
	 * @throws IOException
	 */
	public FileStreamWrapperReadBinary(Resource res,boolean seekable) {
		super(res);
		this.seekable=seekable;
	}
	



	@Override
	public Object read(int len) throws IOException {
		byte[] barr=new byte[len];
		len=seekable?getRAF().read(barr):_getBIS().read(barr);
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
	public void close() throws IOException {
		super.setStatus(FileStreamWrapper.STATE_CLOSE);
		if(bis!=null)bis.close();
		if(raf!=null)raf.close();
	}

	@Override
	public String getMode() {
		return "readBinary";
	}

	public boolean isEndOfFile() {
		return isEOF;
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
			_getBIS().skip(len);
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
	

	private BufferedInputStream _getBIS() throws IOException {
		if(bis==null)bis = IOUtil.toBufferedInputStream(res.getInputStream());
		return bis;
	}
}
