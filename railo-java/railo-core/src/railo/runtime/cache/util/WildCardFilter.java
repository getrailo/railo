package railo.runtime.cache.util;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import railo.commons.io.cache.CacheKeyFilter;

/**
 * Wildcard Filter
 */
public class WildCardFilter implements CacheKeyFilter {
    
    private static final String specials="{}[]().+\\^$";
    
	private final Pattern pattern;
    private final PatternMatcher matcher=new Perl5Matcher();
	private final String wildcard;

	private boolean ignoreCase;


    /**
     * @param wildcard
     * @throws MalformedPatternException
     */
    public WildCardFilter(String wildcard,boolean ignoreCase) throws MalformedPatternException {
        this.wildcard=wildcard;
        this.ignoreCase=ignoreCase;
        StringBuffer sb = new StringBuffer(wildcard.length());
        int len=wildcard.length();
        
        for(int i=0;i<len;i++) {
            char c = wildcard.charAt(i);
            if(c == '*')sb.append(".*");
            else if(c == '?') sb.append('.');
            else if(specials.indexOf(c)!=-1)sb.append('\\').append(c);
            else sb.append(c);
        }
        pattern=new Perl5Compiler().compile(ignoreCase?sb.toString().toLowerCase():sb.toString());
    }

	@Override
	public boolean accept(String key) {
		return matcher.matches(ignoreCase?key.toLowerCase():key, pattern);
	}

    @Override
	public String toString() {
		return "Wildcardfilter:"+wildcard;
	}

	@Override
	public String toPattern() {
		return wildcard;
	}

}