package railo.runtime.coder;

import java.io.UnsupportedEncodingException;

import railo.commons.digest.Base64Encoder;
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
    	byte[] dec = decode(Caster.toString(encoded,null));
    	return new String(dec,charset);
    }

    /**
     * encodes a String to Base64 String
     * @param plain String to encode
     * @return encoded String
     * @throws CoderException 
     * @throws UnsupportedEncodingException 
     */
    public static String encodeFromString(String plain,String charset) throws CoderException, UnsupportedEncodingException {
    	return encode(plain.getBytes(charset));
    }
    
    /**
     * encodes a byte array to Base64 String
     * @param barr byte array to encode
     * @return encoded String
     * @throws CoderException 
     */
    public static String encode(byte[] barr)  {
    	return Base64Encoder.encode(barr);
    } 
    
    /**
     * decodes a Base64 String to a Plain String
     * @param encoded
     * @return decoded binary data 
     * @throws CoderException 
     */
    public static byte[] decode(String encoded) throws CoderException {
        try {
        	return Base64Encoder.decode(encoded);
        }
        catch(Throwable t) {
        	throw new CoderException("can't decode input ["+encoded+"]");
        }
    }
}