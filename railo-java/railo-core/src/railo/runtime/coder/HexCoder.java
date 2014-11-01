package railo.runtime.coder;

import org.apache.commons.codec.binary.Hex;

import railo.commons.io.CharsetUtil;


/**
 * 
 */
public final class HexCoder {
	
	/**
	 * encodes a byte array to a String
	 * @param bytes
	 * @return encoed String
	 */
	public static String encode(byte[] bytes) {
		return Hex.encodeHexString(bytes).toUpperCase();
	}

	/**
	 * decodes back a String to a byte array
	 * @param hexa
	 * @return decoded byte array
	 * @throws CoderException
	 */
	public static byte[] decode(String hexa) throws CoderException {
		if (hexa == null) {
			throw new CoderException("can't decode empty String");
		}
		if ((hexa.length() % 2) != 0) {
			throw new CoderException("invalid hexadicimal String");
		}
		int tamArray = hexa.length() / 2;
		byte[] retorno = new byte[tamArray];
		for (int i=0; i<tamArray; i++) {
			retorno[i] = hexToByte(hexa.substring(i*2,i*2+2));
		}
		return retorno;
	}

	private static byte hexToByte(String hexa) throws CoderException {
		if (hexa == null) {
			throw new CoderException("can't decode empty String");
		}
		if (hexa.length() != 2) {
			throw new CoderException("invalid hexadicimal String");
		}
		byte[] b = hexa.getBytes(CharsetUtil.UTF8);
		byte valor = (byte) (hexDigitValue((char)b[0]) * 16 +
								hexDigitValue((char)b[1]));
		return valor;
	}

	private static int hexDigitValue(char c) throws CoderException {
		int retorno = 0;
		if (c>='0' && c<='9') {
			retorno = (((byte)c) - 48);
		}
		else if (c>='A' && c<='F') {
			retorno = (((byte)c) - 55);
		}
		else if (c>='a' && c<='f') {
			retorno = (((byte)c) - 87);
		}
		else {
			throw new CoderException("invalid hexadicimal String");
		}
		return retorno;
	}
	
}