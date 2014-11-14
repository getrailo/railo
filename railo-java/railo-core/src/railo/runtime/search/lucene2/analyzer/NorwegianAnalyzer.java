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
 * <p>Analyzer for Norwegian language</p>
 * <p><a href="NorwegianAnalyzer.java.html"><i>View Source</i></a></p>
 * <p/>
 *
 * @author Andrey Grebnev <a href="mailto:andrey.grebnev@blandware.com">&lt;andrey.grebnev@blandware.com&gt;</a>
 * @version $Revision: 1.3 $ $Date: 2005/02/24 19:51:22 $
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

	@Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
		return analyzer.tokenStream(fieldName, reader);
	}
}