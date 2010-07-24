package railo.commons.io.res.type.s3;

public interface S3Info {
	public long getSize();
	public long getLastModified();
	public boolean exists();
	public boolean isDirectory();
	public boolean isFile();
}
