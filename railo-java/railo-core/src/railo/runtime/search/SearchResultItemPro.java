package railo.runtime.search;


// FUTURE merege with SearchResulItem

public interface SearchResultItemPro extends SearchResulItem {


    /**
	 * @return the category
	 */
	public String getCategory();
	
	/**
	 * @return the categoryTree
	 */
	public String getCategoryTree();
	
	/**
	 * @return the mimeType
	 */
	public String getMimeType();
	/**
	 * @return the author
	 */
	public String getAuthor();

	/**
	 * @return the size
	 */
	public String getSize();
	
	
    /**
	 * @return the contextSummary
	 */
	public String getContextSummary();
}
