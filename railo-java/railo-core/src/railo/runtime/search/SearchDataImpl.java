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
package railo.runtime.search;

import java.util.HashMap;
import java.util.Map;
// FUTURE diese klasse entfernen, dient nur daszu die suggestion durchzuschleusen
public class SearchDataImpl implements SearchData  {

	private Map suggestion=null;
	private int suggestionMax;
	private String suggestionQuery;
	private int recordsSearched;

	public SearchDataImpl(int suggestionMax) {
		this(new HashMap(), suggestionMax);
	}
	public SearchDataImpl(Map suggestion, int suggestionMax) {
		this.suggestion=suggestion;
		this.suggestionMax=suggestionMax;
	}

	public Map getSuggestion() {
		return suggestion;
	}
	public int getSuggestionMax() {
		return suggestionMax;
	}
	public void setSuggestionQuery(String suggestionQuery) {
		this.suggestionQuery=suggestionQuery;
	}
	/**
	 * @return the suggestionQuery
	 */
	public String getSuggestionQuery() {
		return suggestionQuery;
	}
	
	@Override
	public int addRecordsSearched(int count) {
		recordsSearched+=count;
		return recordsSearched;
	}
	
	@Override
	public int getRecordsSearched() {
		return recordsSearched;
	}
	
}