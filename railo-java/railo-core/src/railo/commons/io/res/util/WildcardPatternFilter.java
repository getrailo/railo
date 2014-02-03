package railo.commons.io.res.util;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;


public class WildcardPatternFilter implements ResourceAndResourceNameFilter {

	
	private final WildcardPattern matcher;
	
	
	public WildcardPatternFilter( String patt, boolean ignoreCase, String patternDelimiters ) {
		
		matcher = new WildcardPattern( patt, !ignoreCase, patternDelimiters );
	}
	
	
	public WildcardPatternFilter( String pattern, String patternDelimiters ) {
		
		this( pattern, SystemUtil.isWindows(), patternDelimiters );
	}
	
	
	@Override
	public boolean accept( Resource res ) {
		
		return matcher.isMatch( res.getName() );
	}

	
	@Override
	public boolean accept( Resource res, String name ) {

		return matcher.isMatch( name );
	}
	
	
	public boolean accept( String name ) {

		return matcher.isMatch( name );
	}

	
	@Override
	public String toString() {
		
		return matcher.toString();
	}
	
}
