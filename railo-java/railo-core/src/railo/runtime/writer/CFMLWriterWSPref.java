package railo.runtime.writer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/**
 * JSP Writer that Remove WhiteSpace from given content while preserving pre-formatted spaces 
 * in Tags like &lt;CODE&gt; &lt;PRE&gt; and &lt;TEXTAREA&gt;
 */
public final class CFMLWriterWSPref extends CFMLWriterImpl implements WhiteSpaceWriter {

	public static final char CHAR_EMPTY=0;
	public static final char CHAR_NL='\n';
	public static final char CHAR_SPACE=' ';
	public static final char CHAR_TAB='\t';
	public static final char CHAR_BS='\b'; // \x0B\
	public static final char CHAR_FW='\f';
	public static final char CHAR_RETURN='\r';

	private static final char CHAR_GT = '>';
	private static final char CHAR_LT = '<';
	private static final char CHAR_SL = '/';
	private static final String[] EXCLUDE_TAGS	= { "code", "pre", "textarea" };
	
	private static int minTagLen = 64;

	private static final boolean doChangeWsToSpace = true;				// can help fight XSS attacks with characters like Vertical Tab
	
	private int[] depths;
	private int depthSum = 0;
	private char lastChar = 0;
	private boolean isFirstChar = true;	
	private StringBuilder sb = new StringBuilder();


	static {
		
		// TODO: set EXCLUDE_TAGS (and perhaps doChangeWsToSpace) to values from WebConfigImpl
		
		for ( String s : EXCLUDE_TAGS )
			if ( s.length() < minTagLen )
				minTagLen = s.length();
		
		minTagLen++;															// add 1 for LessThan symbol
	}
	

	/**
	 * constructor of the class
	 * @param rsp
	 * @param bufferSize 
	 * @param autoFlush 
	 */
	public CFMLWriterWSPref(HttpServletRequest req, HttpServletResponse rsp, int bufferSize, boolean autoFlush, boolean closeConn, 
			boolean showVersion, boolean contentLength, boolean allowCompression) {
		super(req,rsp, bufferSize, autoFlush,closeConn,showVersion,contentLength,allowCompression);
		depths = new int[ EXCLUDE_TAGS.length ];
	}


	/**
	 * prints the characters from the buffer and resets it
	 * 
	 * TODO: make sure that printBuffer() is called at the end of the stream in case we have some characters there! (flush() ?)
	 */
	synchronized void printBuffer() throws IOException {				// TODO: is synchronized really needed here?
		int len = sb.length();
		if ( len > 0 ) {
			char[] chars = new char[ len ];
			sb.getChars( 0, len, chars, 0 );
			sb.setLength( 0 );
			super.write( chars, 0, chars.length );
		}
	}

	void printBufferEL() {
		if( sb.length() > 0 ) {
			try {
				printBuffer();
			} 
			catch (IOException e) {}
		}
	}

	/**
	 * checks if a character is part of an open html tag or close html tag, and if so adds it to the buffer, otherwise returns false.
	 * 
	 * @param c
	 * @return true if the char was added to the buffer, false otherwise
	 */
	boolean addToBuffer( char c ) throws IOException {
		int len = sb.length();
		if ( len == 0 && c != CHAR_LT )
			return false;														// buffer must starts with '<'

		sb.append( c );															// if we reached this point then we will return true
		if ( ++len >= minTagLen ) {												// increment len as it was sampled before we appended c
			boolean isClosingTag = ( len >= 2 && sb.charAt( 1 ) == CHAR_SL );
			String substr;
			if ( isClosingTag )
				substr = sb.substring( 2 );										// we know that the 1st two chars are "</"
			else
				substr = sb.substring( 1 );										// we know that the 1st char is "<"
			for ( int i=0; i<EXCLUDE_TAGS.length; i++ ) {								// loop thru list of WS-preserving tags
				if ( substr.equalsIgnoreCase( EXCLUDE_TAGS[ i ] ) ) {					// we have a match
					if ( isClosingTag ) {
						depthDec( i );											// decrement the depth at i and calc depthSum
						printBuffer();
						lastChar = 0;											// needed to allow WS after buffer was printed
					} else {
						depthInc( i );											// increment the depth at i and calc depthSum
					}
				}
			}	
		}
		return true;
	}

	/**
	 * decrement the depth at index and calc the new depthSum
	 * @param index
	 */
	private void depthDec( int index ) {
		if ( --depths[ index ] < 0 )
			depths[ index ] = 0;
		depthCalc();
	}

	/**
	 * increment the depth at index and calc the new depthSum
	 * @param index
	 */
	private void depthInc( int index ) {
		depths[ index ]++;
		depthCalc();
	}

	/**
	 * calc the new depthSum
	 */
	private void depthCalc() {
		int sum = 0;
		for ( int d : depths )
			sum += d;
		depthSum = sum;
	}


	/**
	 * sends a character to output stream if it is not a consecutive white-space unless we're inside a PRE or TEXTAREA tag.
	 * 
	 * @param c
	 * @throws IOException 
	 */
	@Override
	public void print( char c ) throws IOException {
		boolean isWS = Character.isWhitespace( c );
		if ( isWS ) {
			if ( isFirstChar )													// ignore all WS before non-WS content
				return;
			if ( c == CHAR_RETURN )													// ignore Carriage-Return chars
				return;
			if ( sb.length() > 0 ) {
				printBuffer();													// buffer should never contain WS so flush it
                lastChar = c == CHAR_NL ? CHAR_NL : ( doChangeWsToSpace ? CHAR_SPACE : c );
				super.print( lastChar );
                return;
			}
		}

		isFirstChar = false;
		if ( c == CHAR_GT && sb.length() > 0 )	
			printBuffer();														// buffer should never contain ">" so flush it

		if ( isWS || !addToBuffer( c ) ) {
			if ( depthSum == 0 ) {												// we're not in a WS-preserving tag; suppress whitespace
				if ( isWS ) {													// this char is WS
					if ( lastChar == CHAR_NL )									// lastChar was NL; discard this WS char
						return;
					if ( c != CHAR_NL ) {										// this WS char is not NL
						if ( Character.isWhitespace( lastChar ) )
							return;												// lastChar was WS but Not NL; discard this WS char
						if ( doChangeWsToSpace )
							c = CHAR_SPACE;										// this char is WS and not NL; change it to a regular space
					}
				}
			}
			lastChar = c;														// remember c as lastChar and write it to output stream
			super.print( c );
		}
	}
	

	/**
	 * @see railo.runtime.writer.CFMLWriter#writeRaw(java.lang.String)
	 */
	public void writeRaw(String str) throws IOException {
		printBuffer();
		super.write(str);
	}    
	
	/**
     * just a wrapper function for ACF
     * @throws IOException 
     */
    public void initHeaderBuffer() throws IOException{
    	resetHTMLHead();
    }
    
    
    
    
    
    
    
    
    
    
    

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clear()
	 */
	public final void clear() throws IOException {
		printBuffer();
		super.clear();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clearBuffer()
	 */
	public final void clearBuffer() {
		printBufferEL();
		super.clearBuffer();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#close()
	 */
	public final void close() throws IOException {
		printBuffer();
		super.close();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#flush()
	 */
	public final void flush() throws IOException {
		printBuffer();
		super.flush();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#getRemaining()
	 */
	public final int getRemaining() {
		printBufferEL();
		return super.getRemaining();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#newLine()
	 */
	public final void newLine() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(boolean)
	 */
	public final void print(boolean b) throws IOException {
		printBuffer();
		super.print(b);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(char[])
	 */
	public final void print(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(double)
	 */
	public final void print(double d) throws IOException {
		printBuffer();
		super.print(d);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(float)
	 */
	public final void print(float f) throws IOException {
		printBuffer();
		super.print(f);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(int)
	 */
	public final void print(int i) throws IOException {
		printBuffer();
		super.print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(long)
	 */
	public final void print(long l) throws IOException {
		printBuffer();
		super.print(l);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.Object)
	 */
	public final void print(Object obj) throws IOException {
		print(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.String)
	 */
	public final void print(String str) throws IOException {
		write(str.toCharArray(),0,str.length());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println()
	 */
	public final void println() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(boolean)
	 */
	public final void println(boolean b) throws IOException {
		printBuffer();
		super.print(b);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char)
	 */
	public final void println(char c) throws IOException {
		print(c);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char[])
	 */
	public final void println(char[] chars) throws IOException {
		write(chars,0,chars.length);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(double)
	 */
	public final void println(double d) throws IOException {
		printBuffer();
		super.print(d);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(float)
	 */
	public final void println(float f) throws IOException {
		printBuffer();
		super.print(f);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(int)
	 */
	public final void println(int i) throws IOException {
		printBuffer();
		super.print(i);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(long)
	 */
	public final void println(long l) throws IOException {
		printBuffer();
		super.print(l);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.Object)
	 */
	public final void println(Object obj) throws IOException {
		println(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.String)
	 */
	public final void println(String str) throws IOException {
		print(str);
		print(CHAR_NL);
		
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[], int, int)
	 */
	public final void write(char[] chars, int off, int len) throws IOException {
		for(int i=off;i<len;i++) {
			print(chars[i]);
		}
	}
	
	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String, int, int)
	 */
	public final void write(String str, int off, int len) throws IOException {
		write(str.toCharArray(),off,len);
	}
	

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[])
	 */
	public final void write(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(int)
	 */
	public final void write(int i) throws IOException {
		print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String)
	 */
	public final void write(String str) throws IOException {
        write(str.toCharArray(),0,str.length());
	}
}