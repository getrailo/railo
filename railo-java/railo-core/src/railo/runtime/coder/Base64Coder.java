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
        	return _decode(encoded);
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
        	return _encode(barr);
        }
    } 
  
    private static byte[] _decode(String encoded) throws CoderException {
        try {
    	char[] chars;
		chars = encoded.toCharArray();
		
		byte[] bytes=new byte[chars.length];
		for(int i=0;i<chars.length;i++) {
			bytes[i]=(byte)chars[i];
		}
		return Base64.decodeBase64(bytes);
        }
        catch(Throwable t) {
        	throw new CoderException("can't decode input ["+encoded+"]");
        }
    } 
    
    private static String _encode(byte[] barr) {
		byte[] bytes=Base64.encodeBase64(barr);
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<bytes.length;i++) {
			sb.append((char)bytes[i]);
		}
		return sb.toString();
    } 
}