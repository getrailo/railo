package railo.commons.io.res.type.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.collections.map.ReferenceMap;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.xml.sax.SAXException;

import railo.commons.net.URLEncoder;
import railo.loader.util.Util;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public final class S3 implements S3Constants {

	private static final String DEFAULT_URL="s3.amazonaws.com";
	
	private String secretAccessKey;
	private String accessKeyId;
	private TimeZone timezone;
	private String host;


	private final Map infos=new ReferenceMap();



	public S3(String secretAccessKey, String accessKeyId,TimeZone tz) {
		host=DEFAULT_URL;
		this.secretAccessKey = secretAccessKey;
		this.accessKeyId = accessKeyId;
		this.timezone = tz;
		//testFinal();
	}
	
	

	public S3() {
		
		//testFinal();
	}
	
	/**
	 * @return the secretAccessKey
	 */
	String getSecretAccessKey() {
		return secretAccessKey;
	}

	/**
	 * @return the accessKeyId
	 */
	String getAccessKeyId() {
		return accessKeyId;
	}

	/**
	 * @return the tz
	 */
	TimeZone getTimeZone() {
		if(timezone==null)timezone=ThreadLocalPageContext.getTimeZone();
		return timezone;
	}
	
	private static byte[] HMAC_SHA1(String key, String message,String charset) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
		
			SecretKeySpec sks = new SecretKeySpec(key.getBytes(charset),"HmacSHA1");
			Mac mac = Mac.getInstance(sks.getAlgorithm());
			mac.init(sks);
			mac.update(message.getBytes(charset));
			return mac.doFinal();
		
	}

	private static String createSignature(String str, String secretAccessKey,String charset) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
		//str=StringUtil.replace(str, "\\n", String.valueOf((char)10), false);
		byte[] digest = HMAC_SHA1(secretAccessKey,str,charset);
		try {
			return Caster.toBase64(digest);
		} catch (PageException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	public InputStream listBucketsRaw() throws MalformedURLException, IOException, InvalidKeyException, NoSuchAlgorithmException {
		String dateTimeString = Util.toHTTPTimeString();
		String signature = createSignature("GET\n\n\n"+dateTimeString+"\n/", secretAccessKey, "iso-8859-1");
		
		HttpMethod method = railo.commons.net.HTTPUtil.invoke(new URL("http://"+host), null, null, -1, null, "Railo", null, -1, null, null,
				new Header[]{
					new Header("Date",dateTimeString),
					new Header("Authorization","AWS "+accessKeyId+":"+signature)
				}
		);
		
		return method.getResponseBodyAsStream();
		
	}
	
	
	public InputStream listContentsRaw(String bucketName,String prefix,String marker,int maxKeys) throws MalformedURLException, IOException, InvalidKeyException, NoSuchAlgorithmException {
		bucketName=checkBucket(bucketName);
		String dateTimeString = Util.toHTTPTimeString();
		String signature = createSignature("GET\n\n\n"+dateTimeString+"\n/"+bucketName+"/", secretAccessKey, "iso-8859-1");
		
		
		List headers=new ArrayList();
		headers.add(new Header("Date",dateTimeString));
		headers.add(new Header("Authorization","AWS "+accessKeyId+":"+signature));
		headers.add(new Header("Host",bucketName+"."+host));
		
		///if(!StringUtil.isEmpty(prefix)) headers.add(new Header("prefix",prefix));
		//if(!StringUtil.isEmpty(marker)) headers.add(new Header("marker",marker));
		//if(maxKeys>=0) headers.add(new Header("max-keys",Caster.toString(maxKeys)));
		
		String strUrl="http://"+bucketName+"."+host+"/";
		if(Util.hasUpperCase(bucketName))strUrl="http://"+host+"/"+bucketName+"/";
		
		
		char amp='?';
		if(!Util.isEmpty(prefix)){
			strUrl+=amp+"prefix="+encodeEL(prefix);
			amp='&';
		}
		if(!Util.isEmpty(marker)) {
			strUrl+=amp+"marker="+encodeEL(marker);
			amp='&';
		}
		if(maxKeys!=-1) {
			strUrl+=amp+"max-keys="+maxKeys;
			amp='&';
		}
		
		HttpMethod method = railo.commons.net.HTTPUtil.invoke(new URL(strUrl), null, null, -1, null, "Railo", null, -1, null, null,(Header[])headers.toArray(new Header[headers.size()]));
		return method.getResponseBodyAsStream();
		
	}
	

	public Content[] listContents(String bucketName,String prefix) throws InvalidKeyException, MalformedURLException, NoSuchAlgorithmException, IOException, SAXException {
		String marker=null,last=null;
		ContentFactory factory;
		Content[] contents;
		List list = new ArrayList();
		int size=0;
		while(true) {
			factory = new ContentFactory(listContentsRaw(bucketName, prefix, marker, -1),this);
			contents = factory.getContents();
			list.add(contents);
			size+=contents.length;
			if(factory.isTruncated() && contents.length>0) {
				last=marker;
				marker=contents[contents.length-1].getKey();
				if(marker.equals(last))break;
			}
			else break;
		}
		
		if(list.size()==1) return (Content[]) list.get(0);
		if(list.size()==0) return new Content[0];
		
		Content[] rtn=new Content[size];
		Iterator it = list.iterator();
		int index=0;
		while(it.hasNext()) {
			contents=(Content[]) it.next();
			for(int i=0;i<contents.length;i++) {
				rtn[index++]=contents[i];
			}
		}
		
		return rtn;
	}

	public Content[] listContents(String bucketName,String prefix,String marker,int maxKeys) throws InvalidKeyException, MalformedURLException, NoSuchAlgorithmException, IOException, SAXException {
		InputStream raw = listContentsRaw(bucketName, prefix, marker, maxKeys);
		ContentFactory factory = new ContentFactory(raw,this);
		return factory.getContents();
	}
	
	public Bucket[] listBuckets() throws InvalidKeyException, MalformedURLException, NoSuchAlgorithmException, IOException, SAXException {
		InputStream raw = listBucketsRaw();
		//print.o(IOUtil.toString(raw, null));
		BucketFactory factory = new BucketFactory(raw,this);
		return factory.getBuckets();
	}
	
	public void putBuckets(String bucketName,int acl, int storage) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SAXException {
		String strXML = "";
		if(storage==STORAGE_EU) {
			strXML="<CreateBucketConfiguration><LocationConstraint>EU</LocationConstraint></CreateBucketConfiguration>";
		}
		
		byte[] barr = strXML.getBytes("iso-8859-1");
		put(bucketName, null, acl,new ByteArrayRequestEntity(barr,"text/html"));	
	}
	
	/*public void putObject(String bucketName,String objectName,int acl,Resource res) throws IOException, InvalidKeyException, NoSuchAlgorithmException, PageException, SAXException, EncoderException {
		String contentType = IOUtil.getMymeType(res, "application");
		InputStream is = null;
		try {
			is = res.getInputStream();
			put(bucketName, objectName, acl, is, contentType);
		}
		finally {
			IOUtil.closeEL(is);
		}
	}*/
	/*
	public void put(String bucketName,String objectName,int acl, InputStream is,long length, String contentType) throws IOException, InvalidKeyException, NoSuchAlgorithmException, PageException, SAXException, EncoderException {
		put(bucketName, objectName, acl, HTTPUtil.toRequestEntity(is),length, contentType);
	}*/
		
	public void put(String bucketName,String objectName,int acl, RequestEntity re) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SAXException {
		bucketName=checkBucket(bucketName);
		objectName=checkObjectName(objectName);
		
		String dateTimeString = Util.toHTTPTimeString();
		// Create a canonical string to send based on operation requested 
		String cs = "PUT\n\n"+re.getContentType()+"\n"+dateTimeString+"\nx-amz-acl:"+toStringACL(acl)+"\n/"+bucketName+"/"+objectName;
		String signature = createSignature(cs, secretAccessKey, "iso-8859-1");
		Header[] headers = new Header[]{
				new Header("Content-Type",re.getContentType()),
				new Header("Content-Length",Long.toString(re.getContentLength())),
				new Header("Date",dateTimeString),
				new Header("x-amz-acl",toStringACL(acl)),
				new Header("Authorization","AWS "+accessKeyId+":"+signature),
		};
		
		String strUrl="http://"+bucketName+"."+host+"/"+objectName;
		if(Util.hasUpperCase(bucketName))strUrl="http://"+host+"/"+bucketName+"/"+objectName;
		
		
		
		HttpMethod method = railo.commons.net.HTTPUtil.put(new URL(strUrl), null, null, -1, null, 
				"Railo", null, -1, null, null,headers,re);
		if(method.getStatusCode()!=200){
			new ErrorFactory(method.getResponseBodyAsStream());
		}
		
		
	}
		
	public HttpURLConnection preput(String bucketName,String objectName,int acl, String contentType) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
		bucketName=checkBucket(bucketName);
		objectName=checkObjectName(objectName);
		
		String dateTimeString = Util.toHTTPTimeString();
		// Create a canonical string to send based on operation requested 
		String cs = "PUT\n\n"+contentType+"\n"+dateTimeString+"\nx-amz-acl:"+toStringACL(acl)+"\n/"+bucketName+"/"+objectName;
		String signature = createSignature(cs, secretAccessKey, "iso-8859-1");
		
		String strUrl="http://"+bucketName+"."+host+"/"+objectName;
		if(Util.hasUpperCase(bucketName))strUrl="http://"+host+"/"+bucketName+"/"+objectName;
		
		URL url = new URL(strUrl);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("PUT");
		
		conn.setFixedLengthStreamingMode(227422142);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestProperty("CONTENT-TYPE", contentType);
		conn.setRequestProperty("USER-AGENT", "S3 Resource");        
		//conn.setRequestProperty("Transfer-Encoding", "chunked" );
		conn.setRequestProperty("Date", dateTimeString);
		conn.setRequestProperty("x-amz-acl", toStringACL(acl));
		conn.setRequestProperty("Authorization", "AWS "+accessKeyId+":"+signature);
		return conn;
	}

	public String getObjectLink(String bucketName,String objectName,int secondsValid) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
		bucketName=checkBucket(bucketName);
		objectName=checkObjectName(objectName);
		
		//String dateTimeString = GetHttpTimeString.invoke();
		long epoch = (System.currentTimeMillis()/1000)+(secondsValid);
		String cs = "GET\n\n\n"+epoch+"\n/"+bucketName+"/"+objectName;
		String signature = createSignature(cs, secretAccessKey, "iso-8859-1");
		
		String strUrl="http://"+bucketName+"."+host+"/"+objectName;
		if(Util.hasUpperCase(bucketName))strUrl="http://"+host+"/"+bucketName+"/"+objectName;
		
		
		return strUrl+"?AWSAccessKeyId="+accessKeyId+"&Expires="+epoch+"&Signature="+signature;
	}

	public InputStream getInputStream(String bucketName,String objectName) throws InvalidKeyException, NoSuchAlgorithmException, IOException, SAXException  {
		bucketName=checkBucket(bucketName);
		objectName=checkObjectName(objectName);
		
		String dateTimeString = Util.toHTTPTimeString();
		//long epoch = (System.currentTimeMillis()/1000)+6000;
		String cs = "GET\n\n\n"+dateTimeString+"\n/"+bucketName+"/"+objectName;
		    
		
		String signature = createSignature(cs, secretAccessKey, "iso-8859-1");
		
		String strUrl="http://"+bucketName+"."+host+"/"+objectName;
		if(Util.hasUpperCase(bucketName))strUrl="http://"+host+"/"+bucketName+"/"+objectName;
		URL url = new URL(strUrl);
		
		
		HttpMethod method = railo.commons.net.HTTPUtil.invoke(url, null, null, -1, null, "Railo", null, -1, null, null,
				new Header[]{
					new Header("Date",dateTimeString),
					new Header("Host",bucketName+"."+host),
					new Header("Authorization","AWS "+accessKeyId+":"+signature)
				}
		);
		if(method.getStatusCode()!=200)
			new ErrorFactory(method.getResponseBodyAsStream());
		
		return method.getResponseBodyAsStream();
		
		
		
		
		//URL url = new URL(getObjectLink(bucketName, objectName, 6000));
		//HttpMethod method = HTTPUtil.invoke(url, null, null, -1, null, null, null, -1, null, null, null);
		//if(method.getStatusCode()!=200)new ErrorFactory(method.getResponseBodyAsStream());
		
		//return method.getResponseBodyAsStream();
	}
	
	
	
	/*
<cffunction name="getObject" access="public" output="false" returntype="string" description="Returns a link to an object.">
		<cfargument name="minutesValid" type="string" required="false" default="60">

		<cfreturn timedAmazonLink>
	</cffunction>


	 */
	

	/*public void deleteBucket(String bucketName) throws InvalidKeyException, NoSuchAlgorithmException, IOException, SAXException {
		_delete(bucketName, "");
	}

	public void deleteObject(String bucketName,String objectName) throws InvalidKeyException, NoSuchAlgorithmException, IOException, SAXException {
		_delete(bucketName, checkObjectName(objectName));
	}*/
	

	public void delete(String bucketName, String objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SAXException {
		bucketName=checkBucket(bucketName);
		objectName = checkObjectName(objectName);

		String dateTimeString = Util.toHTTPTimeString();
		// Create a canonical string to send based on operation requested 
		String cs ="DELETE\n\n\n"+dateTimeString+"\n/"+bucketName+"/"+objectName;
		//print.out(cs);
		String signature = createSignature(cs, secretAccessKey, "iso-8859-1");
		
		Header[] headers = new Header[]{
				new Header("Date",dateTimeString),
				new Header("Authorization","AWS "+accessKeyId+":"+signature),
		};
		
		String strUrl="http://"+bucketName+"."+host+"/"+objectName;
		if(Util.hasUpperCase(bucketName))strUrl="http://"+host+"/"+bucketName+"/"+objectName;
		
		
		
		HttpMethod method = railo.commons.net.HTTPUtil.delete(new URL(strUrl), null, null, -1, null, "Railo", null, -1, null, null,headers);
		
		if(method.getStatusCode()!=200)
			new ErrorFactory(method.getResponseBodyAsStream());
	}

	
	
	
	
	
	// --------------------------------
	public static String toStringACL(int acl) throws S3Exception {
		switch(acl) {
			case ACL_AUTH_READ:return "authenticated-read";
			case ACL_PUBLIC_READ:return "public-read";
			case ACL_PRIVATE:return "private";
			case ACL_PUBLIC_READ_WRITE:return "public-read-write";
		}
		throw new S3Exception("invalid acl definition");
	}

	public static String toStringStorage(int storage) throws S3Exception {
		String s = toStringStorage(storage, null);
		if(s==null)
			throw new S3Exception("invalid storage definition");
		return s;
	}
	public static String toStringStorage(int storage, String defaultValue) {
		switch(storage) {
			case STORAGE_EU:return "eu";
			case STORAGE_US:return "us";
		}
		return defaultValue;
	}
	
	public static int toIntACL(String acl) throws S3Exception {
		acl=acl.toLowerCase().trim();
		if("public-read".equals(acl)) return ACL_PUBLIC_READ;
		if("private".equals(acl)) return ACL_PRIVATE;
		if("public-read-write".equals(acl)) return ACL_PUBLIC_READ_WRITE;
		if("authenticated-read".equals(acl)) return ACL_AUTH_READ;
		
		if("public_read".equals(acl)) return ACL_PUBLIC_READ;
		if("public_read_write".equals(acl)) return ACL_PUBLIC_READ_WRITE;
		if("authenticated_read".equals(acl)) return ACL_AUTH_READ;
		
		if("publicread".equals(acl)) return ACL_PUBLIC_READ;
		if("publicreadwrite".equals(acl)) return ACL_PUBLIC_READ_WRITE;
		if("authenticatedread".equals(acl)) return ACL_AUTH_READ;
		
		throw new S3Exception("invalid acl value, valid values are [public-read, private, public-read-write, authenticated-read]");
	}

	public static int toIntStorage(String storage) throws S3Exception {
		int s=toIntStorage(storage,-1);
		if(s==-1)
			throw new S3Exception("invalid storage value, valid values are [eu,us]");
		return s;
	}
	public static int toIntStorage(String storage, int defaultValue) {
		storage=storage.toLowerCase().trim();
		if("us".equals(storage)) return STORAGE_US;
		if("usa".equals(storage)) return STORAGE_US;
		if("eu".equals(storage)) return STORAGE_EU;
		
		if("u.s.".equals(storage)) return STORAGE_US;
		if("u.s.a.".equals(storage)) return STORAGE_US;
		if("europe.".equals(storage)) return STORAGE_EU;
		if("euro.".equals(storage)) return STORAGE_EU;
		if("e.u.".equals(storage)) return STORAGE_EU;
		if("united states of america".equals(storage)) return STORAGE_US;
		return defaultValue;
	}
	

	private String checkObjectName(String objectName) throws UnsupportedEncodingException {
		if(Util.isEmpty(objectName)) return "";
		if(objectName.startsWith("/"))objectName=objectName.substring(1);
		return encode(objectName);
	}

	private String checkBucket(String name) {
		/*if(!Decision.isVariableName(name)) 
			throw new S3Exception("invalid bucket name definition ["+name+"], name should only contain letters, digits, dashes and underscores");
		
		if(name.length()<3 || name.length()>255) 
			throw new S3Exception("invalid bucket name definition ["+name+"], the length of a bucket name must be between 3 and 255");
		*/
		
		return encodeEL(name);
	}

	private String encodeEL(String name) {
		try {
			return encode(name);
		
		} catch (UnsupportedEncodingException e) {
			return name;
		}
	}
	private String encode(String name) throws UnsupportedEncodingException {
		return URLEncoder.encode(name,"UTF-8");
	}

	/**
	 * @param secretAccessKey the secretAccessKey to set
	 */
	void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	/**
	 * @param accessKeyId the accessKeyId to set
	 */
	void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	/**
	 * @param url the url to set
	 */
	void setHost(String host) {
		this.host=host;
	}

	public String getHost() {
		return host;
	}

	public S3Info getInfo(String path) {
		return (S3Info) infos.get(path.toLowerCase());
	}

	public void setInfo(String path,S3Info info) {
		infos.put(path.toLowerCase(),info);
	}

	public void releaseInfo(String path) {
		infos.remove(path.toLowerCase());
	}



	public static DateTime toDate(String strDate, TimeZone tz) throws PageException {
		if(strDate.endsWith("Z"))
			strDate=strDate.substring(0,strDate.length()-1);
		return Caster.toDate(strDate, tz);
	}
}





