package railo.commons.io.res.util;


import railo.commons.collection.LinkedHashMapMaxSize;
import railo.commons.io.res.Resource;
import railo.runtime.PageSource;
import java.util.Map;

public class ResourceSnippetsMap {

	/* methods that access these Map objects should take care of synchronization */
	private final Map<String, String> sources;
	private final Map<String, ResourceSnippet> snippets;

	public ResourceSnippetsMap( int maxSnippets, int maxSources ) {

		sources  = new LinkedHashMapMaxSize<String, String>( maxSources );
		snippets = new LinkedHashMapMaxSize<String, ResourceSnippet>( maxSnippets );
	}

	/**
	 * this method accesses the underlying Map(s) and is therefore synchronized
	 *
	 * @param ps
	 * @param startPos
	 * @param endPos
	 * @param charset
	 * @return
	 */
	public synchronized ResourceSnippet getSnippet( PageSource ps, int startPos, int endPos, String charset ) {

		String keySnp = calcKey( ps, startPos, endPos );

		ResourceSnippet snippet = snippets.get( keySnp );

		if ( snippet == null ) {

			Resource res = ps.getResource();
			String keyRes = calcKey( res );
			String src = sources.get( keyRes );

			if ( src == null ) {
				src = ResourceSnippet.getContents( res, charset );
				sources.put( keyRes, src );
			}

			snippet = ResourceSnippet.createResourceSnippet( src, startPos, endPos );
			snippets.put( keySnp, snippet );
		}

		return snippet;
	}

	public static String calcKey( Resource res ) {

		return res.getAbsolutePath() + "@" + res.lastModified();
	}

	public static String calcKey( PageSource ps, int startPos, int endPos ) {

		return ps.getDisplayPath()   + "@" + ps.getLastAccessTime() + ":" + startPos + "-" + endPos;
	}
}
