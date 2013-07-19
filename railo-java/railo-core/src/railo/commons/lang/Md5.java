package railo.commons.lang;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import railo.commons.digest.Hash;


/**
 * class to create a MD5 sum 
 */
public final class Md5 {
    
    /**
     * @deprecated use instead <code>Hash.md5(String)</code>
     * return md5 from string as string
     * @param str plain string to get md5 from
     * @return md5 from string
     * @throws IOException
     */
    public static String getDigestAsString(String str) throws IOException {
    	try {
			return Hash.md5(str);
		}
		catch (NoSuchAlgorithmException e) {
			throw ExceptionUtil.toIOException(e);
		}
    	//return new Md5 (str,"UTF-8").getDigest();
    }
}