package railo.runtime.search.lucene2.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;


/**
 * <p>Analyzer for Italian language</p>
 */
public final class ItalianAnalyzer extends Analyzer {

	private static SnowballAnalyzer analyzer;

	private final static String[] STOP_WORDS = { "a", "abbia",
                    "abbiamo", "abbiano", "abbiate", "ad", "agl", "agli", "ai",
                    "al", "all", "alla", "alle", "allo", "anche", "avemmo",
                    "avendo", "avesse", "avessero", "avessi", "avessimo",
                    "aveste", "avesti", "avete", "aveva", "avevamo", "avevano",
                    "avevate", "avevi", "avevo", "avrà", "avrai", "avranno",
                    "avrebbe", "avrebbero", "avrei", "avremmo", "avremo",
                    "avreste", "avresti", "avrete", "avrò", "avuta", "avute",
                    "avuti", "avuto", "c", "che", "chi", "ci", "coi", "come",
                    "con", "contro", "cui", "da", "dagl", "dagli", "dai",
                    "dal", "dall", "dalle", "dallo", "degl", "degli", "dei",
                    "del", "dell", "della", "delle", "dello", "di", "dov",
                    "dove", "e", "è", "ebbe", "ebbero", "ebbi", "ed", "erano",
                    "eravamo", "eravate", "eri", "ero", "essendo", "fa", "fà",
                    "facciamo", "facciano", "faccio", "facemmo", "facendo",
                    "facesse", "facessero", "facessi", "facessimo", "faceste",
                    "facesti", "faceva", "facevamo", "facevano", "facevate",
                    "facevi", "facevo", "fai", "fanno", "farà", "farai",
                    "faranno", "farebbe", "farebbero", "farei", "faremmo",
                    "faremo", "fareste", "faresti", "farete", "farò", "fece",
                    "fecero", "fossero", "fossimo", "foste", "fosti", "fu",
                    "fui", "fummo", "furono", "gli", "ha", "hai", "hanno",
                    "ho", "i", "il", "in", "io", "l", "la", "là", "le", "lei",
                    "li", "lì", "lo", "loro", "lui", "ma", "mi", "mia", "mie",
                    "miei", "mio", "ne", "negl", "negli", "nei", "nel", "nell",
                    "nella", "nelle", "nello", "noi", "non", "nostra",
                    "nostre", "nostri", "nostro", "o", "per", "perché", "più",
                    "quale", "quanta", "quante", "quanti", "quanto", "quella",
                    "quelle", "quelli", "quello", "questa", "queste", "questi",
                    "questo", "sarà", "sarai", "saranno", "sarebbe",
                    "sarebbero", "sarei", "saremmo", "saremo", "sareste",
                    "saresti", "sarete", "sarò", "se", "sei", "si", "sì",
                    "sia", "siamo", "siano", "siate", "siete", "sono", "sta",
                    "stai", "stando", "stanno", "starà", "starai", "staranno",
                    "starebbe", "starebbero", "starei", "staremmo", "staremo",
                    "stareste", "staresti", "starete", "starò", "stava",
                    "stavamo", "stavano", "stavate", "stavi", "stavo",
                    "stemmo", "stesse", "stessero", "stessi", "stessimo",
                    "steste", "stesti", "stette", "stettero", "stetti", "stia",
                    "stiamo", "stiano", "stiate", "sto", "su", "sua", "sue",
                    "sugl", "sugli", "sui", "sul", "sull", "sulla", "sulle",
                    "sullo", "suo", "suoi", "ti", "tra", "tu", "tua", "tue",
                    "tuo", "tuoi", "tutti", "tutto", "un", "una", "uno", "vi",
                    "voi", "vostra", "vostre", "vostri", "vostro" };


	/**
	 * Creates new instance of SpanishAnalyzer
	 */
	public ItalianAnalyzer() {
		analyzer = new SnowballAnalyzer("Italian", STOP_WORDS);
	}

	public ItalianAnalyzer(String stopWords[]) {
		analyzer = new SnowballAnalyzer("Italian", stopWords);
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return analyzer.tokenStream(fieldName, reader);
	}
}