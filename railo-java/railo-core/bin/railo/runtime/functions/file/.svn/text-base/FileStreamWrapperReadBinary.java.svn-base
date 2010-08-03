package railo.runtime.functions.file;

import java.io.BufferedInputStream;
import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

public class FileStreamWrapperReadBinary extends FileStreamWrapper {
	


	private BufferedInputStream bis;
	private boolean isEOF;

	/**
	 * Constructor of the class
	 * @param res
	 * @param charset
	 * @throws IOException
	 */
	public FileStreamWrapperReadBinary(Resource res) {
		super(res);
		
	}
	


	private BufferedInputStream getBIS() throws IOException {
		if(bis==null)bis = IOUtil.toBufferedInputStream(res.getInputStream());
		return bis;
	}

	/**
	 *
	 * @see railo.runtime.functions.file.FileStreamWrapper#read(int)
	 */
	public Object read(int len) throws IOException {
		byte[] barr=new byte[len];
		len=getBIS().read(barr);
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

	/**
	 * @see railo.runtime.functions.file.FileStreamWrapper#close()
	 */
	public void close() throws IOException {
		super.setStatus(FileStreamWrapper.STATE_CLOSE);
		if(bis!=null)bis.close();
	}

	/**
	 * @see railo.runtime.functions.file.FileStreamWrapper#getMode()
	 */
	public String getMode() {
		return "readBinary";
	}

	public boolean isEndOfFile() {
		return isEOF;
	}

	/**
	 * @see railo.runtime.functions.file.FileStreamWrapper#getSize()
	 */
	public long getSize() {
		return res.length();
	}
}
