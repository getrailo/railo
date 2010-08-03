package railo.runtime.coder;

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
    public static String decodeBase64(Object encoded) throws ExpressionException {

        StringBuffer sb=new StringBuffer();
        byte[] bytes = Caster.toBinary(encoded);
		for(int i=0;i<bytes.length;i++) {
			sb.append((char)bytes[i]);
		}
        return sb.toString();
    } 
    
    /**
     * decodes a Base64 String to a Plain String
     * @param encoded
     * @return decoded binary data 
     * @throws CoderException 
     */
    public static byte[] decode(String encoded) throws CoderException {
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
        	throw new CoderException("can't decode input");
        }
    } 
    /**
     * encodes a String to Base64 String
     * @param plain String to encode
     * @return encoded String
     */
    public static String encodeBase64(String plain) {
        byte[] b=plain.getBytes();
	    
		byte[] bytes=Base64.encodeBase64(b);
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<bytes.length;i++) {
			sb.append((char)bytes[i]);
		}
		return sb.toString();
    } 
    
    /**
     * encodes a byte array to Base64 String
     * @param barr byte array to encode
     * @return encoded String
     */
    public static String encode(byte[] barr) {
		byte[] bytes=Base64.encodeBase64(barr);
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<bytes.length;i++) {
			sb.append((char)bytes[i]);
		}
		return sb.toString();
    } 
}