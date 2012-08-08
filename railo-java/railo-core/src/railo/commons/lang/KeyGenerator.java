package railo.commons.lang;

import java.io.IOException;


public class KeyGenerator {
	public static String createKey(String value) throws IOException{
		// create a crossfoot of the string and change result in constealltion of the position
		long sum=0;
		for(int i=value.length()-1;i>=0;i--){
			sum+=(value.charAt(i))*((i%3+1)/2f);
		}
		return Md5.getDigestAsString(value)+":"+sum;
	}
	public static String createVariable(String value) throws IOException{
		// create a crossfoot of the string and change result in constealltion of the position
		long sum=0;
		for(int i=value.length()-1;i>=0;i--){
			sum+=(value.charAt(i))*((i%3+1)/2f);
		}
		return "V"+Md5.getDigestAsString(value)+sum;
	}
}