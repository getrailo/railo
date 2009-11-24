package railo.runtime.search;


/**
 * A Single Record of a Search Result
 */
public class SearchResulItemImpl implements SearchResulItem {
    
    /**
	 * @return the contextSummary
	 */
	public String getContextSummary() {
		return contextSummary;
	}
	private String title;
    private float score;
    private int recordsSearched;
    private String id;
    private String key;
    private String url;
    private String summary;
    private String custom1;
    private String custom2;
    private String custom3;
    private String custom4;
	private String categoryTree;
	private String category;
	private String mimeType;
	private String author;
	private String size;
	private String contextSummary;
    
    /**
     * @param id
     * @param title
     * @param score
     * @param key 
     * @param url 
     * @param summary
     * @param custom1 
     * @param custom2 
     * @param custom3 
     * @param custom4 
     * @param mimeType 
     * @param author 
     * @param size 
     */
    public SearchResulItemImpl(String id,String title, float score, String key, String url,String summary,String contextSummary,
    		String categoryTree,String category,
            String custom1,String custom2,String custom3,String custom4, String mimeType, String author, String size) {
        this.id = id;
        this.title = title;
        this.score = score;
        //this.recordsSearched = recordsSearched;
        this.key = key;
        this.url = url;
        this.summary = summary;
        this.contextSummary = contextSummary;

        this.categoryTree = categoryTree;
        this.category = category;
        this.custom1 = custom1;
        this.custom2 = custom2;
        this.custom3 = custom3;
        this.custom4 = custom4;
        this.mimeType = mimeType;
        this.author = author;
        this.size = size;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getRecordsSearched()
     */
    public int getRecordsSearched() {
        return recordsSearched;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getScore()
     */
    public float getScore() {
        return score;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getSummary()
     */
    public String getSummary() {
        return summary;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getTitle()
     */
    public String getTitle() {
        return title;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getId()
     */
    public String getId() {
        return id;
    }
    
    
    /**
     * @see railo.runtime.search.SearchResulItem#getKey()
     */
    public String getKey() {
        return key;
    }
    
    /**
     * @see railo.runtime.search.SearchResulItem#getUrl()
     */
    public String getUrl() {
    	return url;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getCustom1()
     */
    public String getCustom1() {
        return custom1;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getCustom2()
     */
    public String getCustom2() {
        return custom2;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getCustom3()
     */
    public String getCustom3() {
        return custom3;
    }
    /**
     * @see railo.runtime.search.SearchResulItem#getCustom4()
     */
    public String getCustom4() {
        return custom4;
    }
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @return the categoryTree
	 */
	public String getCategoryTree() {
		return categoryTree;
	}
	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
}