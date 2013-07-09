package railo.runtime.text.csv;

import java.util.ArrayList;
import java.util.List;

import railo.commons.lang.StringUtil;


public class CSVString {

    private char[] buffer;
    private int pos;
    private char delim;

    public CSVString( String input, char delim ) {
    	this.buffer = input.toCharArray();
        this.delim = delim;
    }

    public List<List<String>> parse() {

        List<List<String>> result = new ArrayList();
        List<String> line = new ArrayList();

        if ( buffer.length == 0 )
            return result;

        StringBuilder sb = new StringBuilder();
        char c;

        do {

            c = buffer[ pos ];

            if ( c == '"' || c == '\'' ) {

                sb.append( fwdQuote( c ) );
            }
            else if ( c == '\n' ) {

                line.add( sb.toString().trim() );
                sb = new StringBuilder();
                if ( isValidLine( line ) )
                    result.add( line );
                line = new ArrayList();
            }
            else if ( c == delim ) {

                line.add( sb.toString().trim() );
                sb = new StringBuilder();
            }
            else
                sb.append( c );

            next();
        } while ( hasNext() );

        if(pos<buffer.length)
        	sb.append( buffer[ pos ] );
        line.add( sb.toString().trim() );

        if ( isValidLine( line ) )
            result.add( line );

        return result;
    }


    /** forward pos until the end of quote */
    StringBuilder fwdQuote( char q ) {

        StringBuilder sb = new StringBuilder();

        while ( hasNext() ) {

            next();
            sb.append( buffer[ pos ] );

            if ( isCurr( q ) ) {
                if ( isNext( q ) ) {            // consecutive quote sign
                    next();
                }
                else {
                    break;
                }
            }
        }

        if ( sb.length() > 0 )
            sb.setLength( sb.length() - 1 );    // remove closing quote sign

        return sb;
    }

    void next() {

        pos++;
    }

    boolean hasNext() {

        return pos < ( buffer.length - 1 );
    }

    boolean isNext( char c ) {

        if ( !hasNext() )   return false;

        return buffer[ pos + 1 ] == c;
    }


    boolean isCurr( char c ) {

        if ( !isValidPos() )
            return false;

        return buffer[ pos ] == c;
    }


    boolean isValidPos() {

        return pos >= 0 && pos < buffer.length - 1;
    }


    boolean isValidLine( List<String> line ) {

        for ( String s : line ) {

            if ( !StringUtil.isEmpty( s, true ) )
                return true;
        }

        return false;
    }

}
