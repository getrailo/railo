package railo.commons.io.res.type.s3;



import railo.aprint;
import railo.commons.date.TimeZoneConstants;
import railo.commons.net.http.HTTPResponse;

public class TestS3 {
	public static void main(String[] args) throws Throwable {

		String accessKeyId = "1DHC5C5FVD7YEPR4DBG2"; 
		String secretAccessKey = "R/sOy3hgimrI8D9c0lFHchoivecnOZ8LyVmJpRFQ";
		HTTPResponse m;
		
		S3 s3=new S3(secretAccessKey, accessKeyId, TimeZoneConstants.CET);
		//raw = s3.listBucketsRaw();
		//print.o(StringUtil.replace(IOUtil.toString(raw, null),"<","\n<",false));
		
		//meta = s3.getMetadata("j878", "sub/text.txt");
		//print.o(meta);
		//meta = s3.getMetadata("j878", "sub/xxxx");
		//print.o(meta);
		//raw = s3.aclRaw("j878", null);
		//print.o(StringUtil.replace(IOUtil.toString(raw, null),"<","\n<",false));
		
		
		m = s3.head("j878", "sub/text.txt");
		aprint.o(m.getContentAsString());
		aprint.e(m.getStatusCode());
		//aprint.o(StringUtil.replace(m.getResponseBodyAsString(),"<","\n<",false));
		
		//m = s3.head("j878", null);
		//print.o(m.getResponseHeaders());
		//print.o(StringUtil.replace(m.getResponseBodyAsString(),"<","\n<",false));
		
	}
}
