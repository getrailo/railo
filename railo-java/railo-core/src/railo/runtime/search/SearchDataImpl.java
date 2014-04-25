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

	@Override
	public Map getSuggestion() {
		return suggestion;
	}
	@Override
	public int getSuggestionMax() {
		return suggestionMax;
	}
	@Override
	public void setSuggestionQuery(String suggestionQuery) {
		this.suggestionQuery=suggestionQuery;
	}
	/**
	 * @return the suggestionQuery
	 */
	@Override
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