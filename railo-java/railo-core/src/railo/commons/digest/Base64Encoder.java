package railo.commons.digest;

import java.io.ByteArrayOutputStream;

import railo.print;
import railo.commons.io.CharsetUtil;

public class Base64Encoder {
	private static int min=Integer.MAX_VALUE;
	private static int max=Integer.MIN_VALUE;
	
	private static final char[] carr = {
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
			'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
			'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
			'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/' };
	
	private static final  int[] iarr = {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54,
			55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2,
			3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
			20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30,
			31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
			48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	
	static {
		int tmp;
		for(int i=0;i<carr.length;i++){
			tmp=carr[i];
			if(min>tmp)min=tmp;
			if(max<tmp)max=tmp;
			
		}
		print.e(min);
		print.e(max);
	}
	
	public static String encodeFromString(String data) {
		return encode(data.getBytes(CharsetUtil.UTF8));
	}
	
	public static String encode(byte[] data) {
		
		StringBuilder buffer = new StringBuilder();
		int pad = 0;
		for (int i = 0; i < data.length; i += 3) {

			int b = ((data[i] & 0xFF) << 16) & 0xFFFFFF;
			if (i + 1 < data.length) {
				b |= (data[i+1] & 0xFF) << 8;
			} else {
				pad++;
			}
			if (i + 2 < data.length) {
				b |= (data[i+2] & 0xFF);
			} else {
				pad++;
			}

			for (int j = 0; j < 4 - pad; j++) {
				int c = (b & 0xFC0000) >> 18;
				buffer.append(carr[c]);
				b <<= 6;
			}
		}
		for (int j = 0; j < pad; j++) {
			buffer.append("=");
		}

		return buffer.toString();
	}

	public static String decodeAsString(String data) {
		return new String(decode(data),CharsetUtil.UTF8);
	}
	
	
	
	public static byte[] decode(String data) {
		
		// VALIDATE
		// check characters are in range
		int val;
		for(int i=data.length()-1;i>=0;i--){
			val=data.charAt(i);
			if(val<min || val>max) throw new IllegalArgumentException("input character ["+((char)val)+"] of string ["+data+"] is not a valid Base64 String character.");
		}
		// check if the length of the string is valid
		if(data.length() % 4 != 0)
			throw new IllegalArgumentException("input ["+data+"] is not a valid Base64 String, invalid length of the string");
		
		byte[] bytes = data.getBytes(CharsetUtil.UTF8);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (int i = 0; i < bytes.length; ) {
			int b = 0;
			if (iarr[bytes[i]] != -1) {
				b = (iarr[bytes[i]] & 0xFF) << 18;
			}
			// skip unknown characters
			else {
				i++;
				continue;
			}
			if (i + 1 < bytes.length && iarr[bytes[i+1]] != -1) {
				b = b | ((iarr[bytes[i+1]] & 0xFF) << 12);
			}
			if (i + 2 < bytes.length && iarr[bytes[i+2]] != -1) {
				b = b | ((iarr[bytes[i+2]] & 0xFF) << 6);
			}
			if (i + 3 < bytes.length && iarr[bytes[i+3]] != -1) {
				b = b | (iarr[bytes[i+3]] & 0xFF);
			}
			while ((b & 0xFFFFFF) != 0) {
				int c = (b & 0xFF0000) >> 16;
				buffer.write((char)c);
				b <<= 8;
			}
			i += 4;
		}
		return buffer.toByteArray();
	}
}