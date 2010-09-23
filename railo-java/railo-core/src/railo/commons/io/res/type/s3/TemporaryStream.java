package railo.commons.io.res.type.s3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.CastImpl;

public final class TemporaryStream extends OutputStream {

	private static final int MAX_MEMORY = 1024*1024;
	private static int index=1;
	private static Resource tempFile;
	
	private Resource persis;
	private long count=0;
	private OutputStream os;
	public boolean memoryMode=true;
	public boolean available=false;
	
	/**
	 * Constructor of the class
	 */
	public TemporaryStream() {
		do {
		this.persis=getTempDirectory().getRealResource("temporary-stream-"+(index++));
		}
		while(persis.exists());
		os=new java.io.ByteArrayOutputStream();
	}
	
	/**
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		count++;
		check();
		os.write(b);
	}
	
	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		count+=len;
		check();
		os.write(b, off, len);
	}

	/**
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] b) throws IOException {
		count+=b.length;
		check();
		os.write(b);
	}

	private void check() throws IOException {
		if(memoryMode && count>=MAX_MEMORY && os instanceof java.io.ByteArrayOutputStream) {
			memoryMode=false;
			OutputStream nos = persis.getOutputStream();
			nos.write(((java.io.ByteArrayOutputStream)os).toByteArray());
			os=nos;
		}
	}


	/**
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		os.close();
		available=true;
	}

	/**
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		os.flush();
	}

	public InputStream getInputStream() throws IOException {
		return new InpuStreamWrap(this);
	}
	
	class InpuStreamWrap extends InputStream {

		private TemporaryStream ts;
		private InputStream is;

		public InpuStreamWrap(TemporaryStream ts) throws IOException {
			this.ts=ts;
			if(ts.os instanceof java.io.ByteArrayOutputStream) {
				is=new ByteArrayInputStream(((java.io.ByteArrayOutputStream)ts.os).toByteArray());
			}
			else if(ts.available) {
				ts.available=false;
				try {
					is=ts.persis.getInputStream();
				} catch (IOException e) {
					ts.persis.delete();
					throw e;
				}
			}
			else 
				throw new IOException("InputStream no longer available");
		}
		
		/**
		 * @see java.io.InputStream#read()
		 */
		public int read() throws IOException {
			return is.read();
		}

		/**
		 * @see java.io.InputStream#available()
		 */
		public int available() throws IOException {
			return is.available();
		}

		/**
		 * @see java.io.InputStream#close()
		 */
		public void close() throws IOException {
			ts.persis.delete();
			is.close();
		}

		/**
		 *
		 * @see java.io.InputStream#mark(int)
		 */
		public synchronized void mark(int readlimit) {
			is.mark(readlimit);
		}

		/**
		 *
		 * @see java.io.InputStream#markSupported()
		 */
		public boolean markSupported() {
			return is.markSupported();
		}

		/**
		 *
		 * @see java.io.InputStream#read(byte[], int, int)
		 */
		public int read(byte[] b, int off, int len) throws IOException {
			return is.read(b, off, len);
		}

		/**
		 *
		 * @see java.io.InputStream#read(byte[])
		 */
		public int read(byte[] b) throws IOException {
			return is.read(b);
		}

		/**
		 *
		 * @see java.io.InputStream#reset()
		 */
		public synchronized void reset() throws IOException {
			is.reset();
		}

		/**
		 *
		 * @see java.io.InputStream#skip(long)
		 */
		public long skip(long n) throws IOException {
			return is.skip(n);
		}
	}

	public long length() {
		return count;
	}
	
	public static Resource getTempDirectory() {
        if(tempFile!=null) return tempFile;
        String tmpStr = System.getProperty("java.io.tmpdir");
        if(tmpStr!=null) {
        	
        	tempFile=ResourceUtil.toResourceNotExisting(ThreadLocalPageContext.get(), tmpStr);
        	//tempFile=CFMLEngineFactory.getInstance().getCastUtil().toResource(tmpStr,null);
            
            if(tempFile!=null && tempFile.exists()) {
                tempFile=getCanonicalResourceEL(tempFile);
                return tempFile;
            }
        }
        File tmp =null;
        try {
        	tmp = File.createTempFile("a","a");
        	tempFile=ResourceUtil.toResourceNotExisting(ThreadLocalPageContext.get(), tmp.getParent());
        	//tempFile=CFMLEngineFactory.getInstance().getCastUtil().toResource(tmp.getParent(),null);
            tempFile=getCanonicalResourceEL(tempFile);   
        }
        catch(IOException ioe) {}
        finally {
        	if(tmp!=null)tmp.delete();
        }
        return tempFile;
    }
	
	private static Resource getCanonicalResourceEL(Resource res) {
		try {
			return res.getCanonicalResource();
		} catch (IOException e) {
			return res.getAbsoluteResource();
		}
	}
}