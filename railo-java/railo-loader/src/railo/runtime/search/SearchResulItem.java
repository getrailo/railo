package railo.runtime.search;

/**
 * a singl result item
 */
public interface SearchResulItem {

    /**
     * @return Returns the recordsSearched.
     */
    public abstract int getRecordsSearched();

    /**
     * @return Returns the score.
     */
    public abstract float getScore();

    /**
     * @return Returns the summary.
     */
    public abstract String getSummary();

    /**
     * @return Returns the title.
     */
    public abstract String getTitle();

    /**
     * @return Returns the id.
     */
    public abstract String getId();

    /**
     * @return Returns the key
     */
    public abstract String getKey();

    /**
     * @return Returns the url
     */
    public abstract String getUrl();

    /**
     * @deprecated use instead <code>getCustom(int index)</code>
     * @return Returns the custom1.
     */
    public abstract String getCustom1();

    /**
     * @deprecated use instead <code>getCustom(int index)</code>
     * @return Returns the custom2.
     */
    public abstract String getCustom2();

    /**
     * @deprecated use instead <code>getCustom(int index)</code>
     * @return Returns the custom3.
     */
    public abstract String getCustom3();

    /**
     * @deprecated use instead <code>getCustom(int index)</code>
     * @return Returns the custom4.
     */
    public abstract String getCustom4();
    
    public abstract String getCustom(int index) throws SearchException;
    


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