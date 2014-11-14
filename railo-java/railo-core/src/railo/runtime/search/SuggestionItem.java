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

import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public class SuggestionItem {

	Array keywords=new ArrayImpl();
	Array keywordsScore=new ArrayImpl();
	
	public SuggestionItem(String[] arr) {
		add(arr);
	}

	public void add(String[] arr) {
		for(int i=0;i<arr.length;i++) {
			keywords.appendEL(arr[i]);
			keywordsScore.appendEL(new Double(99-i));
		}
	}

	public Array getKeywords() {
		return keywords;
	}

	public Array getKeywordScore() {
		return keywordsScore;
	}

}
