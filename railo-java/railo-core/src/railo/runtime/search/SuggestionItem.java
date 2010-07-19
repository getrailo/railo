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
