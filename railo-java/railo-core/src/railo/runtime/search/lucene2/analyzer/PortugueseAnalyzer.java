package railo.runtime.search.lucene2.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;


/**
 * <p>Analyzer for Portuguese language</p>
 * <p><a href="PortugueseAnalyzer.java.html"><i>View Source</i></a></p>
 * <p/>
 *
 * @author Andrey Grebnev <a href="mailto:andrey.grebnev@blandware.com">&lt;andrey.grebnev@blandware.com&gt;</a>
 * @version $Revision: 1.3 $ $Date: 2005/02/24 19:51:22 $
 */ 
public final class PortugueseAnalyzer extends Analyzer {

	private static SnowballAnalyzer analyzer;

	private String PORTUGUESE_STOP_WORDS[] = {

		"a", "ainda", "alem", "ambas", "ambos", "antes",
		"ao", "aonde", "aos", "apos", "aquele", "aqueles",
		"as", "assim", "com", "como", "contra", "contudo",
		"cuja", "cujas", "cujo", "cujos", "da", "das", "de",
		"dela", "dele", "deles", "demais", "depois", "desde",
		"desta", "deste", "dispoe", "dispoem", "diversa",
		"diversas", "diversos", "do", "dos", "durante", "e",
		"ela", "elas", "ele", "eles", "em", "entao", "entre",
		"essa", "essas", "esse", "esses", "esta", "estas",
		"este", "estes", "ha", "isso", "isto", "logo", "mais",
		"mas", "mediante", "menos", "mesma", "mesmas", "mesmo",
		"mesmos", "na", "nas", "nao", "nas", "nem", "nesse", "neste",
		"nos", "o", "os", "ou", "outra", "outras", "outro", "outros",
		"pelas", "pelas", "pelo", "pelos", "perante", "pois", "por",
		"porque", "portanto", "proprio", "propios", "quais", "qual",
		"qualquer", "quando", "quanto", "que", "quem", "quer", "se",
		"seja", "sem", "sendo", "seu", "seus", "sob", "sobre", "sua",
		"suas", "tal", "tambem", "teu", "teus", "toda", "todas", "todo",
		"todos", "tua", "tuas", "tudo", "um", "uma", "umas", "uns"};

	/**
	 * Creates new instance of SpanishAnalyzer
	 */
	public PortugueseAnalyzer() {
		analyzer = new SnowballAnalyzer("Portuguese", PORTUGUESE_STOP_WORDS);
	}

	public PortugueseAnalyzer(String stopWords[]) {
		analyzer = new SnowballAnalyzer("Portuguese", stopWords);
	}

	@Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
		return analyzer.tokenStream(fieldName, reader);
	}
}