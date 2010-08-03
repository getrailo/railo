package railo.runtime.coder;


/**
 * 
 */
public final class Coder {

	/**
	 * Field <code>ENCODING_UU</code>
	 */
	public static final short ENCODING_UU=0;
	/**
	 * Field <code>ENCODING_HEX</code>
	 */
	public static final short ENCODING_HEX=1;
	/**
	 * Field <code>ENCODING_BASE64</code>
	 */
	public static final short ENCODING_BASE64=2;
	

	/**
	 * @param type
	 * @param value
	 * @return
	 * @throws CoderException
	 */
	public static byte[] decode(String type, String value) throws CoderException {
		type=type.toLowerCase().trim();
		if(type.equals("hex"))		return decode(ENCODING_HEX,value);
		if(type.equals("uu"))		return decode(ENCODING_UU,value);
		if(type.equals("base64"))	return decode(ENCODING_BASE64,value);
		throw new CoderException("invalid encoding definition ["+type+"] valid encodingd are [hex, uu, base64]");
	}
	
	/**
	 * @param type
	 * @param value
	 * @return
	 * @throws CoderException
	 */
	public static byte[] decode(short type, String value) throws CoderException {
		if(type==ENCODING_UU)			return UUCoder.decode(value);
		else if(type==ENCODING_HEX)		return HexCoder.decode(value);
		else if(type==ENCODING_BASE64)	return Base64Coder.decode(value);
		throw new CoderException("invalid encoding definition");
	}

	/**
	 * @param type
	 * @param value
	 * @return
	 * @throws CoderException
	 */
	public static String encode(String type, byte[] value) throws CoderException {
		type=type.toLowerCase().trim();
		if(type.equals("hex"))		return encode(ENCODING_HEX,value);
		if(type.equals("uu"))		return encode(ENCODING_UU,value);
		if(type.equals("base64"))	return encode(ENCODING_BASE64,value);
		throw new CoderException("invalid encoding definition ["+type+"] valid encodingd are [hex, uu, base64]");
	}
	
	/**
	 * @param type
	 * @param value
	 * @return
	 * @throws CoderException
	 */
	public static String encode(short type, byte[] value) throws CoderException {
		if(type==ENCODING_UU)			return UUCoder.encode(value);
		else if(type==ENCODING_HEX)		return HexCoder.encode(value);
		else if(type==ENCODING_BASE64)	return Base64Coder.encode(value);
		throw new CoderException("invalid encoding definition");
	}
}