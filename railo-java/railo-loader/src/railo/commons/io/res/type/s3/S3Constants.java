package railo.commons.io.res.type.s3;

public interface S3Constants {

	public static final int ACL_PUBLIC_READ=0;			//"public-read";
	public static final int ACL_PRIVATE=1;				//private
	public static final int ACL_PUBLIC_READ_WRITE=2;	//public-read-write
	public static final int ACL_AUTH_READ=3;			//authenticated-read

	public static final int STORAGE_EU=0;//
	public static final int STORAGE_US=1;//
	public static final int STORAGE_US_WEST=2;//
	public static final int STORAGE_UNKNOW = -1;
	public static final String HOST = "s3.amazonaws.com";
}





