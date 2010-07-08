package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.OutputStream;

public final class S3ResourceOutputStream extends OutputStream {
	
	private final S3 s3;
	
	private final String contentType="application";
	private final String bucketName;
	private final String objectName;
	private final int acl;

	private TemporaryStream ts;
	
	public S3ResourceOutputStream(S3 s3,String bucketName,String objectName,int acl) {
		this.s3=s3;
		this.bucketName=bucketName;
		this.objectName=objectName;
		this.acl=acl;
		
		ts = new TemporaryStream();
	}
	
	/**
	 *
	 * @see java.io.OutputStream#close()
	 */
	public void close() throws IOException {
		ts.close();
		
		//InputStream is = ts.getInputStream();
		try {
			s3.put(bucketName, objectName, acl, new TemporaryStreamRequestEntity(ts,contentType));
		} 
		catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 *
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		ts.flush();
	}

	/**
	 *
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		ts.write(b, off, len);
	}

	/**
	 *
	 * @see java.io.OutputStream#write(byte[])
	 */
	public void write(byte[] b) throws IOException {
		ts.write(b);
	}

	/**
	 *
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		ts.write(b);
	}
}
