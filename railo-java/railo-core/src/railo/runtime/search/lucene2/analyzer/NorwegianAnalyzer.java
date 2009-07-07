package railo.runtime.search.lucene2.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;


/**
 * <p>Analyzer for Norwegian language</p>
 * <p><a href="NorwegianAnalyzer.java.html"><i>View Source</i></a></p>
 * <p/>
 *
 */
public final class NorwegianAnalyzer extends Analyzer {

	private static SnowballAnalyzer analyzer;

	private String NORWEGIAN_STOP_WORDS[] = {
		"og", "i", "er", "det", "som", "å", "til", "på", "for", "av", "at", "med", "har", "en", "om", "du", "de",
		"ikke", "no", "vi", "jeg", "kan", "den", "eller", "seg", "men", "et", "dei", "skal", "ein", "blir", "så",
		"vil", "fra", "var", "alle", "andre", "dette", "hva", "år", "bla"
	};

	/**
	 * Creates new instance of SpanishAnalyzer
	 */
	public NorwegianAnalyzer() {
		analyzer = new SnowballAnalyzer("Norwegian", NORWEGIAN_STOP_WORDS);
	}

	public NorwegianAnalyzer(String stopWords[]) {
		analyzer = new SnowballAnalyzer("Norwegian", stopWords);
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {
		return analyzer.tokenStream(fieldName, reader);
	}
}