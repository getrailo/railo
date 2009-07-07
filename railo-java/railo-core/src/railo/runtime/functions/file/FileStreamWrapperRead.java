package railo.runtime.functions.file;

import java.io.BufferedReader;
import java.io.IOException;

import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;

public class FileStreamWrapperRead extends FileStreamWrapper {
	

	private BufferedReader br;
	private String charset;

	/**
	 * Constructor of the class
	 * @param res
	 * @param charset
	 * @throws IOException
	 */
	public FileStreamWrapperRead(Resource res, String charset) {
		super(res);
		this.charset=charset;
	}

	private BufferedReader getBR() throws IOException {
		if(br==null)br = IOUtil.toBufferedReader(IOUtil.getReader(res.getInputStream(),charset));
		return br;
	}
	
	/**
	 *
	 * @see railo.runtime.functions.file.FileStreamWrapper#read(int)
	 */
	public Object read(int len) throws IOException {
		char[] carr=new char[len];
		len = getBR().read(carr);
		if(len==-1) {
			throw new IOException(" End of file reached");
		}
		return new String(carr,0,len);
	}

	/**
	 *
	 * @see railo.runtime.functions.file.FileStreamWrapper#readLine()
	 */
	public String readLine() throws IOException {
		/*
byte[] buffer = new byte[blockSize];
        int len;
        while((len = in.read(buffer)) !=-1) {
          out.write(buffer, 0, len);
        }
		 */
		if(!getBR().ready())
			throw new IOException(" End of file reached");
		return getBR().readLine();
	}

	/**
	 * @see railo.runtime.functions.file.FileStreamWrapper#close()
	 */
	public void close() throws IOException {
		super.setStatus(FileStreamWrapper.STATE_CLOSE);
		if(br!=null)br.close();
	}

	/**
	 * @see railo.runtime.functions.file.FileStreamWrapper#getMode()
	 */
	public String getMode() {
		return "read";
	}

	public boolean isEndOfFile() {
		try {
			return !getBR().ready();
		} catch (IOException e) {
			return true;
		}
	}

	/**
	 * @see railo.runtime.functions.file.FileStreamWrapper#getSize()
	 */
	public long getSize() {
		return res.length();
	}

}
