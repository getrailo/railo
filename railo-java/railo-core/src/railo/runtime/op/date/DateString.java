
package railo.runtime.op.date;


/**
 * helper class to convert a string to a Object
 */
public final class DateString {
    
    private static int[][] ints= { 
            {0,1,2,3,4,5,6,7,8,9}, 
            {0,10,20,30,40,50,60,70,80,90}, 
            {0,100,200,300,400,500,600,700,800,900}, 
            {0,1000,2000,3000,4000,5000,6000,7000,8000,9000}
    }; 

    private String str;
    private int pos;

    /**
     * constructor of the class
     * @param str Date String
     */
    public DateString(String str) {
        this.str=str;
    }

    /**
     * check if char a actuell position of the inner cursor is same value like given value
     * @param c char to compare
     * @return is same or not
     */
    public boolean isNext(char c) {
        return str.length()>pos+1 && str.charAt(pos+1)==c;
    }

    /**
     * check if char a actuell position of the inner cursor is same value like given value
     * @param c char to compare
     * @return is same or not
     */
    public boolean isCurrent(char c) {
        return str.length()>pos && str.charAt(pos)==c;
    }

    /**
     * check if last char has same value than given char
     * @param c char to check
     * @return is same or not
     */
    public boolean isLast(char c) {
        return str.charAt(str.length()-1)==c;
    }

    /**
     * set inner cursor one forward
     */
    public void next() {
        pos++;
    }

    /**
     * set inner cursor [count] forward
     * @param count forward count
     */
    public void next(int count) {
        pos+=count;
    }

    /**
     * @return the length of the inner String
     */
    public int length() {
       return str.length();
    }

    /**
     * forward inner cursor if value at actuell position is same as given.
     * @param c char to compare
     * @return has forwared or not
     */
    public boolean fwIfCurrent(char c) {
        if(isCurrent(c)) {
            pos++;
            return true;
        }
        return false;
    }

    /**
     * forward inner cursor if value at the next position is same as given.
     * @param c char to compare
     * @return has forwared or not
     */
    public boolean fwIfNext(char c) {
        if(isNext(c)) {
            pos++;
            return true;
        }
        return false;
    }

    /*
     * read in the next four digits from current position
     * @return value from the 4 digits
     *
    public int read4Digit() {
        // first
        char c=str.charAt(pos++);
        if(!isDigit(c)) return -1;
		int value=ints[3][c-48]; 
		
	    // second
        c=str.charAt(pos++);
        if(!isDigit(c)) return -1;
		value+=ints[2][c-48]; 
		
	    // third
        c=str.charAt(pos++);
        if(!isDigit(c)) return -1;
		value+=ints[1][c-48]; 
		
	    // fourt
        c=str.charAt(pos++);
        if(!isDigit(c)) return -1;
		value+=ints[0][c-48]; 
				
        return value;
    }*/

    /*
     * read in the next four digits from current position
     * @return value from the 4 digits
     *
    public int read2Digit() {
        // first
        char c=str.charAt(pos++);
        if(!isDigit(c)) return -1;
		int value=ints[1][c-48]; 
		
	    // second
        c=str.charAt(pos++);
        if(!isDigit(c)) return -1;
		value+=ints[0][c-48]; 
				
        return value;
    }*/

    /**
     * read in the next digits from current position
     * @return value from the digits
     */
    public int readDigits() {
        int value=0;
        char c;
        if(isValidIndex() && isDigit(c=str.charAt(pos))) {
            value=ints[0][c-48];
            pos++;
        }
        else return -1;
        while(isValidIndex() && isDigit(c=str.charAt(pos))) {
            value*=10;
            value+=ints[0][c-48];
            pos++;
        }
        return value;
    }
    
    /**
     * returns if c is a digit or not
     * @param c char to check
     * @return is digit
     */
    public boolean isDigit(char c) {
        return c>='0' && c<='9';
	}

    /**
     * returns if value at cursor position is a digit or not
     * @return is dgit
     */
    public boolean isDigit() {
        return isValidIndex() && isDigit(str.charAt(pos));
    }

    /**
     * returns if last char is a digit or not
     * @return is dgit
     */
    public boolean isLastDigit() {
        return isDigit(str.charAt(str.length()-1));
    }

    /**
     * return char at given position
     * @param pos postion to get value
     * @return character from given position
     */
    public char charAt(int pos) {
        return str.charAt(pos);
    }

    /**
     * returns if cursor is on the last position
     * @return is on last
     */
    public boolean isLast() {
        return pos+1==str.length();
    }

    /**
     * returns if cursor is after the last position
     * @return is after the last
     */
    public boolean isAfterLast() {
       return pos>=str.length();
    }

    /**
     * returns if cursor is on a valid position
     * @return is after the last
     */
    public boolean isValidIndex() {
       return pos<str.length();
    }
}