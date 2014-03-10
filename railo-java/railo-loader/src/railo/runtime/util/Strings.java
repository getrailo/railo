package railo.runtime.util;

public interface Strings {

    /**
     * performs a replace operation on a string
     *  
     * @param input - the string input to work on 
     * @param find - the substring to find
     * @param repl - the substring to replace the matches with
     * @param firstOnly - if true then only the first occurrence of {@code find} will be replaced
     * @param ignoreCase - if true then matches will not be case sensitive
     * @return
     */
	public String replace(String input, String find, String repl, boolean firstOnly, boolean ignoreCase);

	public String toVariableName(String str, boolean addIdentityNumber, boolean allowDot);
	
	/**
	 * return first element of the list
	 * @param list
	 * @param delimiter
	 * @param ignoreEmpty
	 * @return returns the first element of the list
	 */
	public String first(String list, String delimiter, boolean ignoreEmpty);
	
	/**
	 * return last element of the list
	 * @param list
	 * @param delimiter
	 * @param ignoreEmpty
	 * @return returns the last Element of a list
	 */
	public String last(String list, String delimiter, boolean ignoreEmpty);
	
	/**
	 * removes quotes(",') that wraps the string
	 * @param string
	 * @return
	 */
	public String removeQuotes(String string,boolean trim);
	
	public long create64BitHash(CharSequence cs);
}
