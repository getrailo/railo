package railo.commons.lang;


public class CharSequenceImpl implements CharSequence {
	
	private final char[] chars;
	private final String str;
	private final String lcStr;

	/**
	 * Constructor of the class
	 * @param chars
	 */
	public CharSequenceImpl(char[] chars) {
		this.str=new String(chars);
		this.chars=chars;
		
		char c;
		for(int i=0;i<chars.length;i++) {
            c=chars[i];
            if(!((c>='a' && c<='z') || (c>='0' && c<='9'))) {
            	lcStr=str.toLowerCase();
                return ;
            }
        }
		lcStr=str;
	}
	
	/**
	 * Constructor of the class
	 * @param str
	 */
	public CharSequenceImpl(String str) {
		this(str.toCharArray());
	}

	@Override
	public char charAt(int index) {
		return chars[index];
	}

	@Override
	public int length() {
		return chars.length;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		char[] dest = new char[end-start];
		System.arraycopy(chars, start, dest, 0, end-start);
		return new CharSequenceImpl(dest);
	}

	@Override
	public String toString() {
		return str;
	} 
	
	public String toLowerCaseString() {
		return lcStr;
	} 
}
