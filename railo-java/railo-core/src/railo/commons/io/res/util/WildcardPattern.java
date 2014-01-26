package railo.commons.io.res.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * a WildcardPattern that accepts a comma- (or semi-colon-) separated value of patterns, e.g. "*.gif, *.jpg, *.jpeg, *.png"
 * and an optional isExclude boolean value which negates the results of the default implementation
 * 
 * also, lines 33 - 37 allow to set isExclude to true by passing a pattern whose first character is an exclamation point '!'
 * 
 * @author Igal
 */
public class WildcardPattern {

	private static final String specials="{}[]().+\\^$";
    private final String pattern;
    private final boolean isInclude;
    
    private final List<ParsedPattern> patterns;
    
    /**
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

        private final boolean isCaseSensitive;
        private Matcher matcher;


        public ParsedPattern( String pattern, boolean isCaseSensitive ) {
            StringBuffer sb = new StringBuffer(pattern.length());
            int len = pattern.length();

            this.isCaseSensitive = isCaseSensitive;
            
            if ( !isCaseSensitive )
                pattern = pattern.toLowerCase();

            for(int i=0;i<len;i++) {
                char c = pattern.charAt(i);
                if(c == '*')sb.append(".*");
                else if(c == '?')sb.append('.');
                else if(specials.indexOf(c)!=-1)sb.append('\\').append(c);
                else sb.append(c);
            }
            
            this.matcher = Pattern.compile(sb.toString()).matcher("");
        }


        /** calls this( pattern, false ); */
        public ParsedPattern( String pattern ) {

            this( pattern, false );
        }


        /** tests if the input string matches the pattern */
        public boolean isMatch( String input ) {

            if ( !isCaseSensitive )
                input = input.toLowerCase();

            return matcher.reset(input).matches();
        }


        @Override
        public String toString() {

        	return matcher.toString();
        }
    }
}
