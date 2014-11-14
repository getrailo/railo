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
package railo.runtime.search.lucene2;

import railo.commons.io.res.Resource;
import railo.runtime.search.SearchCollection;
import railo.runtime.search.SearchEngineSupport;
import railo.runtime.search.SearchException;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * 
 */
public final class LuceneSearchEngine extends SearchEngineSupport {

	public LuceneSearchEngine() {

	}
	
    @Override
    public SearchCollection _createCollection(String name, Resource path, String language) throws SearchException {
    	//SearchUtil.getAnalyzer(language);
        return new LuceneSearchCollection(this,name,path,language,new DateTimeImpl(),new DateTimeImpl());
    }

    @Override
    public void _removeCollection(SearchCollection collection) throws SearchException {
        //throw new SearchException("Lucene Search Engine not implemeted");
    }

    @Override
    public SearchCollection _readCollection(String name, Resource path, String language, DateTime lastUpdate, DateTime created) throws SearchException {
        //throw new SearchException("Lucene Search Engine not implemeted");
        return new LuceneSearchCollection(this,name,path,language,lastUpdate,created);
    }

    @Override
    public String getDisplayName() {
        return "Lucene Search Engine";
    }


}