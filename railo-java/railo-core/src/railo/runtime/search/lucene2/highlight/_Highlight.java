package railo.runtime.search.lucene2.highlight;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import railo.commons.lang.StringUtil;

public class _Highlight {

	public static String createContextSummary(Object highlighter, Analyzer analyzer, String text, int maxNumFragments,String defaultValue) throws IOException {
		//try {
		if(!(highlighter instanceof Highlighter) || analyzer==null || StringUtil.isEmpty(text))
			return defaultValue;
			
		TokenStream tokenStream = analyzer.tokenStream("", new StringReader(text));
			return ((Highlighter)highlighter).getBestFragments(tokenStream, text, maxNumFragments, "...");
		//}catch (Throwable t) {}
            			
	}

	public static Object createHighlighter(Query query,String highlightBegin,String highlightEnd) {
		
			return new Highlighter(
					//new SimpleHTMLFormatter("<span class=\"matching-term\">","</span>"),
					new SimpleHTMLFormatter(highlightBegin,highlightEnd),
					new QueryScorer(query));
		
	}

}
