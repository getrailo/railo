package railo.commons.io.res.util;

import railo.commons.io.SystemUtil;
import railo.commons.io.res.Resource;


public class WildcardPatternFilter implements ResourceAndResourceNameFilter {

	
	private final WildcardPattern matcher;
	
	
	public WildcardPatternFilter( String patt, boolean ignoreCase ) {
		
		matcher = new WildcardPattern( patt, !ignoreCase );
	}
	
	
	public WildcardPatternFilter( String pattern ) {
		
		this( pattern, SystemUtil.isWindows() );
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
