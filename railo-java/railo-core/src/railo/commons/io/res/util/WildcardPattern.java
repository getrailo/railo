package railo.commons.io.res.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * a WildcardPattern that accepts a comma- (or semi-colon-) separated value of patterns, e.g. "*.gif, *.jpg, *.jpeg, *.png"
 * and an optional isExclude boolean value which negates the results of the default implementation
 * 
 * also, lines 31 - 35 allow to set isExclude to true by passing a pattern whose first character is an exclamation point '!'
 * 
 * @author Igal
 */
public class WildcardPattern {

    private final String pattern;
    private final boolean isInclude;
    
    private final List<ParsedPattern> patterns;
    
    /**
     * 
     * @param pattern - the wildcard pattern, or a comma/semi-colon separated value of wildcard patterns
     * @param isCaseSensitive - if true, does a case-sensitive matching
     * @param isExclude - if true, the filter becomes an Exclude filter so that only items that do not match the pattern are accepted
     */
    public WildcardPattern( String pattern, boolean isCaseSensitive, boolean isExclude ) {

    	if ( pattern.charAt( 0 ) == '!' ) {		// set isExclude to true if the first char of pattern is an exclamation point '!'
    		
    		pattern = pattern.substring( 1 );
    		isExclude = true;
    	}
    	
        this.pattern = pattern;
        this.isInclude = !isExclude;
        
        StringTokenizer tokenizer = new StringTokenizer( pattern, ",;|" );
        
        patterns = new ArrayList<ParsedPattern>();
        
        while ( tokenizer.hasMoreTokens() ) {
            
            String token = tokenizer.nextToken().trim();
            
            if ( !token.isEmpty() )
                patterns.add( new ParsedPattern( token, isCaseSensitive ) );
        }
    }
    
    
    /** calls this( pattern, isCaseSensitive, false ); */
    public WildcardPattern( String pattern, boolean isCaseSensitive ) {
    
        this( pattern, isCaseSensitive, false );
    }
    
    
    public boolean isMatch( String input ) {
        
        for ( ParsedPattern pp : this.patterns ) {
            
            boolean match = pp.isMatch( input );
            
            if ( match )
                return isInclude;
        }
        
        return !isInclude;
    }
    
    
    public String toString() {
        
        return "WildcardPattern: " + pattern;
    }

    public static class ParsedPattern {

        public final static String MATCH_ANY = "*";
        public final static String MATCH_ONE = "?";

        private List<String> parts;
        private final boolean isCaseSensitive;

        private final boolean isLastPartMatchAny;
        

        public ParsedPattern( String pattern, boolean isCaseSensitive ) {

            this.isCaseSensitive = isCaseSensitive;

            if ( !isCaseSensitive )
                pattern = pattern.toLowerCase();

            parts = new ArrayList<String>();

            int len = pattern.length();

            int subStart = 0;

            for ( int i=subStart; i<len; i++ ) {

                char c = pattern.charAt( i );

                if ( c == '*' || c == '?' ) {

                    if ( i > subStart )
                        parts.add( pattern.substring( subStart, i ) );

                    parts.add( c == '*' ? MATCH_ANY : MATCH_ONE );

                    subStart = i + 1;
                }
            }

            if ( len > subStart ) {

                parts.add( pattern.substring( subStart ) );
            }

            isLastPartMatchAny = ( parts.get( parts.size() - 1 ) == MATCH_ANY );
        }


        /** calls this( pattern, false, false ); */
        public ParsedPattern( String pattern ) {

            this( pattern, false );
        }


        /** tests if the input string matches the pattern */
        public boolean isMatch( String input ) {

            if ( !isCaseSensitive )
                input = input.toLowerCase();

            int pos = 0;
            int len = input.length();

            boolean doMatchAny = false;

            for ( String part : parts ) {

                if ( part == MATCH_ANY ) {

                    doMatchAny = true;
                    continue;
                }

                if ( part == MATCH_ONE ) {

                    doMatchAny = false;
                    pos++;
                    continue;
                }

                int ix = input.indexOf( part, pos );

                if ( ix == -1 )
                    return false;

                if ( !doMatchAny && ix != pos )
                    return false;

                pos = ix + part.length();
                doMatchAny = false;
            }

            if ( !isLastPartMatchAny && ( len != pos ) )        // if pattern doesn't end with * then we shouldn't have any more characters in input
                return false;

            return true;
        }


        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();

            for ( String s : parts ) {

                sb.append( s );
                sb.append( ':' );
            }

            if ( sb.length() > 0 )
                sb.setLength( sb.length() - 1 );

            return "[" + sb.toString() + "]";
        }
    }
}

