package railo.commons.lang;

import java.security.SecureRandom;

public class RandomUtil {
	public static final char[]  CHARS=new char[]{
		'0','1','2','3','4','5','6','7','8','9',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	
	public static final char[]  CHARS_LC=new char[]{
		'0','1','2','3','4','5','6','7','8','9',
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	

	public static String createRandomString(int length){
		if(length<1) return "";
		SecureRandom sr = new SecureRandom();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<length;i++){
			int rnd=(int) (sr.nextDouble()*(CHARS.length-1));
			sb.append(CHARS[rnd]);
		}
		return sb.toString();
	}
	
	public static String createRandomStringLC(int length){
		if(length<1) return "";
		SecureRandom sr = new SecureRandom();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<length;i++){
			int rnd=(int) (sr.nextDouble()*(CHARS_LC.length-1));
			sb.append(CHARS_LC[rnd]);
		}
		return sb.toString();
	}
}
