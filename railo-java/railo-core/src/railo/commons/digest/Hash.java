package railo.commons.digest;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import railo.print;

public class Hash {
	
	public static final char[] ENCODING_HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	//public static final char[] ENCODING_ASCII = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
	private static final byte[] DEL=new byte[]{58};
	
	public static final Charset CHARSET_UTF_8=Charset.forName("UTF-8");
	
	public static final String ALGORITHM_MD5="MD5";
	public static final String ALGORITHM_SHA_256="SHA-256";
	public static final String ALGORITHM_SHA_384="SHA-384";
	public static final String ALGORITHM_SHA_512="SHA-512";
	public static final String ALGORITHM_SHA="SHA";

	public static void main(String[] args) throws Throwable {
		hash("key","nonce",ALGORITHM_SHA_256,ENCODING_HEX);
	}
	
	public static String md5(String str) throws NoSuchAlgorithmException {
		return hash(str, ALGORITHM_MD5,ENCODING_HEX);
	}
	
	public static String sha(String str) throws NoSuchAlgorithmException {
		return hash(str, ALGORITHM_SHA,ENCODING_HEX);
	}
	
	public static String sha256(String str) throws NoSuchAlgorithmException {
		return hash(str, ALGORITHM_SHA_256,ENCODING_HEX);
	}
	
	public static String sha384(String str) throws NoSuchAlgorithmException {
		return hash(str, ALGORITHM_SHA_384,ENCODING_HEX);
	}
	
	public static String sha512(String str) throws NoSuchAlgorithmException {
		return hash(str, ALGORITHM_SHA_512,ENCODING_HEX);
	}
	
	
	public static String hash(String str, String nonce, String algorithm,char[] encoding) throws NoSuchAlgorithmException {
		MessageDigest md=MessageDigest.getInstance(algorithm);
	    md.reset();
	    md.update(toBytes(str, CHARSET_UTF_8));
	    md.update(DEL);
	    md.update(toBytes(nonce, CHARSET_UTF_8));
	    return new String( enc(md.digest(),encoding)); // no charset needed because all characters are below us-ascii (hex)
	}
	

	
	public static String hash(String str, String algorithm, int numIterations,char[] encoding) throws NoSuchAlgorithmException {
		try {
			MessageDigest md=MessageDigest.getInstance(algorithm),mdc;
			for(int i=0;i<numIterations;i++){
				mdc=(MessageDigest) md.clone();
				mdc.reset();
			    mdc.update(toBytes(str, CHARSET_UTF_8));
			    str=new String(enc(mdc.digest(),encoding));
			}
			return str;
		}
		catch (CloneNotSupportedException e) {}
		
		// if not possible to clone the MessageDigest create always a new instance
		for(int i=0;i<numIterations;i++){
			str=hash(str, algorithm,encoding);
		}
		return str;
	}
	
	public static String hash(String str, String algorithm,char[] encoding) throws NoSuchAlgorithmException {
		MessageDigest md=MessageDigest.getInstance(algorithm);
	    md.reset();
	    md.update(toBytes(str, CHARSET_UTF_8));
	    return new String( enc(md.digest(),encoding)); // no charset needed because all characters are below us-ascii (hex)
	}
	
	private static byte[] toBytes(String str, Charset charset) {
		if (str==null) return null;
		return str.getBytes(charset);
	}
	
	private static char[] enc(byte[] data,char[] enc) {
		
		int len = data.length;
		char[] out = new char[len << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < len; i++) {
			out[j++] = enc[(0xF0 & data[i]) >>> 4];
			out[j++] = enc[0x0F & data[i]];
		}
		return out;
	}
}
