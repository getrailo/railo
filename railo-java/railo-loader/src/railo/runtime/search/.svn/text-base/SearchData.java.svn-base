package railo.runtime.search;

import java.util.Map;

public interface SearchData  {

	public Map getSuggestion();
	
	public int getSuggestionMax();
	
	public void setSuggestionQuery(String suggestionQuery);
	
	public String getSuggestionQuery();
	
	/**
	 * increments the searched records
	 * @param count records searched
	 * @return all records searched
	 */
	public int addRecordsSearched(int count);

	/**
	 * return the records searched
	 */
	public int getRecordsSearched();
}