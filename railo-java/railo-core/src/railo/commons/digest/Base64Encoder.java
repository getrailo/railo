/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.commons.digest;

import railo.commons.io.CharsetUtil;
import railo.runtime.coder.CoderException;

public class Base64Encoder {

	private static final char[] ALPHABET = {
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
			'0','1','2','3','4','5','6','7','8','9','+','/' };
    private static int[]  toInt= new int[128];
    static {
        for(int i=0; i< toInt.length; i++){
            toInt[i]= -1;
        }
        for(int i=0; i< ALPHABET.length; i++){
            toInt[ALPHABET[i]]= i;
        }
    }

	public static String encodeFromString(String data) {
		return encode(data.getBytes(CharsetUtil.UTF8));
	}
	

	
	/**
     * Translates the specified byte array into Base64 string.
     *
     * @param data the byte array (not null)
     * @return the translated Base64 string (not null)
     */
    public static String encode(byte[] data){
        int size = data.length;
        char[] ar = new char[((size + 2) / 3) * 4];
        int a = 0;
        int i=0;
        while(i < size){
            byte b0 = data[i++];
            byte b1 = (i < size) ? data[i++] : 0;
            byte b2 = (i < size) ? data[i++] : 0;

            int mask = 0x3F;
            ar[a++] = ALPHABET[(b0 >> 2) & mask];
            ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
            ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
            ar[a++] = ALPHABET[b2 & mask];
        }
        switch(size % 3){
            case 1: ar[--a]  = '=';
            case 2: ar[--a]  = '=';
        }
        return new String(ar);
    }

	public static String decodeAsString(String data) throws CoderException {
		return new String(decode(data),CharsetUtil.UTF8);
	}
	
	
	/**
     * Translates the specified Base64 string into a byte array.
     *
     * @param s the Base64 string (not null)
     * @return the byte array (not null)
     * @throws CoderException 
     */
    public static byte[] decode(String str) throws CoderException {
    	final String s=str.trim();
    	
    	final int len=s.length();
    	
    // valid length
    	if(len % 4 != 0 || len < 4)
    		throw new CoderException("can't decode the input string"+printString(s)+", because the input string has an invalid length");
		
    	int delta = s.endsWith( "==" ) ? 2 : s.endsWith( "=" ) ? 1 : 0;
        byte[] buffer = new byte[s.length()*3/4 - delta];
        int mask = 0xFF;
        int index = 0;
        for(int i=0; i< s.length(); i+=4){
        	int c0 = toInt[s.charAt( i )];
            int c1 = toInt[s.charAt( i + 1)];
            int c2 = toInt[s.charAt( i + 2)];
            int c3 = toInt[s.charAt( i + 3 )];
            
            // invalid char can be a "=" in the end
            if(c0==-1) {
    			throw new CoderException("can't decode the input string"+printString(s)+", because the input is not a valid Base64 String, the String has an invalid character ["+s.charAt(i+0)+"] at position ["+(i+1)+"]");
    		}
            else if(c1==-1) {
    			// 3 fillbytes in the end
    			if(i+4==s.length() && s.charAt(i+1)=='=' && s.charAt(i+2)=='=' && s.charAt(i+3)=='=') continue;
        		throw new CoderException("can't decode the input string"+printString(s)+", because the input is not a valid Base64 String, the String has an invalid character ["+s.charAt(i+1)+"] at position ["+(i+2)+"]");
    		}
            else if(c2==-1) {
    			// 2 fillbytes in the end
    			if(i+4==s.length() && s.charAt(i+2)=='=' && s.charAt(i+3)=='=') continue;
        		throw new CoderException("can't decode the input string"+printString(s)+", because the input is not a valid Base64 String, the String has an invalid character ["+s.charAt(i+2)+"] at position ["+(i+3)+"]");
    		}
            else if(c3==-1) {
    			// 1 fillbytes in the end
    			if(i+4==s.length() && s.charAt(i+3)=='=') continue;
        		throw new CoderException("can't decode the input string"+printString(s)+", because the input is not a valid Base64 String, the String has an invalid character ["+s.charAt(i+3)+"] at position ["+(i+4)+"]");
    		}

            buffer[index++]= (byte)(((c0 << 2) | (c1 >> 4)) & mask);
            if(index >= buffer.length){
                return buffer;
            }
            buffer[index++]= (byte)(((c1 << 4) | (c2 >> 2)) & mask);
            if(index >= buffer.length){
                return buffer;
            }
            buffer[index++]= (byte)(((c2 << 6) | c3) & mask);
        }
        return buffer;
    }

	private static String printString(String s) {
		if(s.length()>50) return " ["+s.substring(0,50)+" ... truncated]";
		return  " ["+s+"]";
	} 
}