/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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
