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
                    "avevate", "avevi", "avevo", "avr�", "avrai", "avranno",
                    "avrebbe", "avrebbero", "avrei", "avremmo", "avremo",
                    "avreste", "avresti", "avrete", "avr�", "avuta", "avute",
                    "avuti", "avuto", "c", "che", "chi", "ci", "coi", "come",
                    "con", "contro", "cui", "da", "dagl", "dagli", "dai",
                    "dal", "dall", "dalle", "dallo", "degl", "degli", "dei",
                    "del", "dell", "della", "delle", "dello", "di", "dov",
                    "dove", "e", "�", "ebbe", "ebbero", "ebbi", "ed", "erano",
                    "eravamo", "eravate", "eri", "ero", "essendo", "fa", "f�",
                    "facciamo", "facciano", "faccio", "facemmo", "facendo",
                    "facesse", "facessero", "facessi", "facessimo", "faceste",
                    "facesti", "faceva", "facevamo", "facevano", "facevate",
                    "facevi", "facevo", "fai", "fanno", "far�", "farai",
                    "faranno", "farebbe", "farebbero", "farei", "faremmo",
                    "faremo", "fareste", "faresti", "farete", "far�", "fece",
                    "fecero", "fossero", "fossimo", "foste", "fosti", "fu",
                    "fui", "fummo", "furono", "gli", "ha", "hai", "hanno",
                    "ho", "i", "il", "in", "io", "l", "la", "l�", "le", "lei",
                    "li", "l�", "lo", "loro", "lui", "ma", "mi", "mia", "mie",
                    "miei", "mio", "ne", "negl", "negli", "nei", "nel", "nell",
                    "nella", "nelle", "nello", "noi", "non", "nostra",
                    "nostre", "nostri", "nostro", "o", "per", "perch�", "pi�",
                    "quale", "quanta", "quante", "quanti", "quanto", "quella",
                    "quelle", "quelli", "quello", "questa", "queste", "questi",
                    "questo", "sar�", "sarai", "saranno", "sarebbe",
                    "sarebbero", "sarei", "saremmo", "saremo", "sareste",
                    "saresti", "sarete", "sar�", "se", "sei", "si", "s�",
                    "sia", "siamo", "siano", "siate", "siete", "sono", "sta",
                    "stai", "stando", "stanno", "star�", "starai", "staranno",
                    "starebbe", "starebbero", "starei", "staremmo", "staremo",
                    "stareste", "staresti", "starete", "star�", "stava",
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