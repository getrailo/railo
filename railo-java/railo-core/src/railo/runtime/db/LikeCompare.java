package railo.runtime.db;

import railo.runtime.exp.PageException;

public class LikeCompare {
	public static boolean like(SQL sql, String haystack, String needle) throws PageException {
    	return LikeCompareJRE.like(sql, haystack, needle, null);
    }
	
	public static boolean like(SQL sql, String haystack, String needle,String escape) throws PageException {	
    	return LikeCompareJRE.like(sql, haystack, needle, escape);
    }
}
