package railo.runtime.search.lucene2.suggest;

import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public class SuggestionItem {

	ArrayImpl keywords=new ArrayImpl();
	ArrayImpl keywordsScore=new ArrayImpl();
	
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
