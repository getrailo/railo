package railo.commons.io.res.util;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;

/**
 * Wildcard Filter
 */
public class WildCardFilter implements ResourceAndResourceNameFilter {
    
    private static final String specials="{}[]().+\\^$";
    private static final boolean IS_WIN=SystemUtil.isWindows();
    
	private final Pattern pattern;
    private final PatternMatcher matcher=new Perl5Matcher();
	private final String wildcard;
	private boolean ignoreCase;

    public WildCardFilter(String wildcard) throws MalformedPatternException {
    	this(wildcard,IS_WIN);
    }
	
    /**
     * @param wildcard
     * @throws MalformedPatternException
     */
    public WildCardFilter(String wildcard,boolean ignoreCase) throws MalformedPatternException {
        this.wildcard=wildcard;
        StringBuilder sb = new StringBuilder(wildcard.length());
        int len=wildcard.length();
        
        for(int i=0;i<len;i++) {
            char c = wildcard.charAt(i);
            if(c == '*')sb.append(".*");
            else if(c == '?') sb.append('.');
            else if(specials.indexOf(c)!=-1)sb.append('\\').append(c);
            else sb.append(c);
        }
        
        this.ignoreCase=ignoreCase;
        pattern=new Perl5Compiler().compile(ignoreCase?StringUtil.toLowerCase(sb.toString()):sb.toString());
    }

    @Override
    public boolean accept(Resource file) {
        return matcher.matches(ignoreCase?StringUtil.toLowerCase(file.getName()):file.getName(), pattern);
    }

	public boolean accept(Resource parent, String name) {
		//print.out("accept:"+name);
        return matcher.matches(ignoreCase?StringUtil.toLowerCase(name):name, pattern);
	}
	public boolean accept(String name) {
		return matcher.matches(ignoreCase?StringUtil.toLowerCase(name):name, pattern);
	}

    @Override
	public String toString() {
		return "Wildcardfilter:"+wildcard;
	}
	
}