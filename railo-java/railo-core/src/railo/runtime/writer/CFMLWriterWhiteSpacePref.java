package railo.runtime.writer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JSP Writer that Remove WhiteSpace from given content
 */
public class CFMLWriterWhiteSpacePref extends CFMLWriterImpl implements WhiteSpaceWriter {
	

	public static final char CHAR_NL = '\n';
	public static final char CHAR_CR = '\r';
    public static final char CHAR_SP = ' ';
    public static final char CHAR_LT = '<';
    
    private char lastChar = 0;
    
    private int preDepth = 0;
    private int txtDepth = 0;
    
    private boolean isFirstChar = true;
    
    private boolean doChangeWsToSpace = true;       // can help fight XSS attacks with characters like Vertical Tab
    
    private StringBuilder sb = new StringBuilder();
	
	
	/**
	 * constructor of the class
	 * @param rsp
	 * @param bufferSize 
	 * @param autoFlush 
	 */
	public CFMLWriterWhiteSpacePref(HttpServletRequest req, HttpServletResponse rsp, int bufferSize, boolean autoFlush, boolean closeConn, 
			boolean showVersion, boolean contentLength, boolean allowCompression) {
		super(req,rsp, bufferSize, autoFlush,closeConn,showVersion,contentLength,allowCompression);
	}
	
    
    /**
     * prints the characters from the buffer and resets it
     * 
     * TODO: make sure that printBuffer() is called at the end of the stream in case we have some characters there! (flush() ?)
     */
    private synchronized void printBuffer() throws IOException {       // TODO: not sure if synchronized is needed here?
        
        int len = sb.length();
        
        if ( len > 0 ) {
        	
	        char[] chars = new char[ len ];
	        
	        sb.getChars( 0, len, chars, 0 );
	        
	        sb.setLength( 0 );
	        
	        super.write( chars, 0, chars.length );
        }
    }
    

	private void printBufferEL() {

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
            return false;                                   // buffer must starts with '<'
        
        
        sb.append( c );                                     // if we reached this point then we will return true

        if ( ++len > 5 ) {                                  // increment len as it was sampled before we appended c

            String substr = sb.substring( 1, 6 );           // we know that the 1st char is < so no need to test it

            if ( substr.equalsIgnoreCase( "/pre>" ) ) {

                if ( --preDepth < 0 )   preDepth = 0;       // decrement and ensure non-negative
            } else if ( substr.equalsIgnoreCase( "/text" ) ) {

                if ( --txtDepth < 0 )   txtDepth = 0;       // decrement and ensure non-negative
            }

            printBuffer();

	        lastChar = 0;									// needed to allow WS after buffer was printed
        } else if ( len == 5 ) {

            String substr = sb.substring( 1, 5 );

            if ( substr.equalsIgnoreCase( "pre>" ) ) {

                preDepth++;
            } else if ( substr.equalsIgnoreCase( "text" ) ) {

                txtDepth++;
            }
        }

        return true;
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
        	
        	if ( isFirstChar )						// ignore all WS before non-WS content
        		return;
        	
        	if ( c == CHAR_CR )						// ignore Carriage-Return chars
        		return;        	
        	
        	if ( sb.length() > 0 ) {
        		
        		printBuffer();                      // buffer should never contain WS so no need to call addToBuffer()

                super.print( c == CHAR_NL ? CHAR_NL : ( doChangeWsToSpace ? CHAR_SP : c ) );
        	}
        }
    	
    	isFirstChar = false;
        
        if ( isWS || !addToBuffer( c ) ) {
            
            if ( preDepth + txtDepth == 0 ) {       // we're not in PRE nor TEXTAREA; suppress whitespace
                
                if ( isWS ) {                       // this char is WS
                
                    if ( lastChar == CHAR_NL )      // lastChar was NL; discard this WS char
                        return;
                    
                    if ( c != CHAR_NL ) {           // this WS char is not NL
                        
                        if ( Character.isWhitespace( lastChar ) )
                            return;                 // lastChar was WS but Not NL; discard this WS char
                        
                        if ( doChangeWsToSpace )
                            c = CHAR_SP;            // this char is WS and not NL; change it to a regular space
                    }
                }                
            }
            
            lastChar = c;                           // remember c as lastChar and write it to output stream
            
            super.print( c );
        }
    }

    
    /* code below was copied from railo.runtime.writer.CFMLWriterWhiteSpace.java */
	

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clear()
	 */
	public void clear() throws IOException {
		printBuffer();
		super.clear();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#clearBuffer()
	 */
	public void clearBuffer() {
		printBufferEL();
		super.clearBuffer();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#close()
	 */
	public void close() throws IOException {
		printBuffer();
		super.close();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#flush()
	 */
	public void flush() throws IOException {
		printBuffer();
		super.flush();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#getRemaining()
	 */
	public int getRemaining() {
		printBufferEL();
		return super.getRemaining();
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#newLine()
	 */
	public void newLine() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(boolean)
	 */
	public void print(boolean b) throws IOException {
		printBuffer();
		super.print(b);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(char[])
	 */
	public void print(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(double)
	 */
	public void print(double d) throws IOException {
		printBuffer();
		super.print(d);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(float)
	 */
	public void print(float f) throws IOException {
		printBuffer();
		super.print(f);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(int)
	 */
	public void print(int i) throws IOException {
		printBuffer();
		super.print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(long)
	 */
	public void print(long l) throws IOException {
		printBuffer();
		super.print(l);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.Object)
	 */
	public void print(Object obj) throws IOException {
		print(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#print(java.lang.String)
	 */
	public void print(String str) throws IOException {
		write(str.toCharArray(),0,str.length());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println()
	 */
	public void println() throws IOException {
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(boolean)
	 */
	public void println(boolean b) throws IOException {
		printBuffer();
		super.print(b);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char)
	 */
	public void println(char c) throws IOException {
		print(c);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(char[])
	 */
	public void println(char[] chars) throws IOException {
		write(chars,0,chars.length);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(double)
	 */
	public void println(double d) throws IOException {
		printBuffer();
		super.print(d);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(float)
	 */
	public void println(float f) throws IOException {
		printBuffer();
		super.print(f);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(int)
	 */
	public void println(int i) throws IOException {
		printBuffer();
		super.print(i);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(long)
	 */
	public void println(long l) throws IOException {
		printBuffer();
		super.print(l);
		print(CHAR_NL);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.Object)
	 */
	public void println(Object obj) throws IOException {
		println(obj.toString());
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#println(java.lang.String)
	 */
	public void println(String str) throws IOException {
		print(str);
		print(CHAR_NL);
		
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[], int, int)
	 */
	public void write(char[] chars, int off, int len) throws IOException {
		for(int i=off;i<len;i++) {
			print(chars[i]);
		}
	}
	
	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String, int, int)
	 */
	public void write(String str, int off, int len) throws IOException {
		write(str.toCharArray(),off,len);
	}
	

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(char[])
	 */
	public void write(char[] chars) throws IOException {
		write(chars,0,chars.length);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(int)
	 */
	public void write(int i) throws IOException {
		print(i);
	}

	/**
	 * @see railo.runtime.writer.CFMLWriterImpl#write(java.lang.String)
	 */
	public void write(String str) throws IOException {
        write(str.toCharArray(),0,str.length());
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
	
}