package railo.runtime.coder;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import railo.runtime.exp.ExpressionException;
import railo.runtime.op.Caster;

/**
 * Util class to handle Base 64 Encoded Strings
 */
public final class Base64Coder {

    /**
     * decodes a Base64 String to a Plain String
     * @param encoded
     * @return
     * @throws ExpressionException
     */
    public static String decodeToString(String encoded,String charset) throws CoderException, UnsupportedEncodingException {
    	byte[] dec = decode(Caster.toString(encoded,null),charset);
    	return new String(dec,charset);
    }
    
    /**
     * decodes a Base64 String to a Plain String
     * @param encoded
     * @return decoded binary data 
     * @throws CoderException 
     */
    public static byte[] decode(String encoded, String charset) throws CoderException {
        try {
	    	return Base64.decodeBase64(encoded.getBytes(charset));
        }
        catch(Throwable t) {
        	throw new CoderException("can't decode input");
        }
    } 
    /**
     * encodes a String to Base64 String
     * @param plain String to encode
     * @return encoded String
     * @throws CoderException 
     * @throws UnsupportedEncodingException 
     */
    public static String encodeFromString(String plain,String charset) throws CoderException, UnsupportedEncodingException {
    	return encode(plain.getBytes(charset),charset);
    }
    
    /**
     * encodes a byte array to Base64 String
     * @param barr byte array to encode
     * @return encoded String
     * @throws CoderException 
     */
    public static String encode(byte[] barr, String charset) throws CoderException {
		try {
			return new String(Base64.encodeBase64(barr),charset);
		}
        catch(Throwable t) {
        	throw new CoderException("can't encode input");
        }
    } 
}