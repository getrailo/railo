package railo.commons.net;



/**
 * Name Value Pair
 */
public final class URLItem {

    private String name;
    private String value;
	private boolean urlEncoded;
    
    /**
     * @param name
     * @param value
     * @param isURLEncoded 
     */
	public URLItem(String name, String value, boolean urlEncoded) {
        this.name = name;
        this.value = value;
        this.urlEncoded=urlEncoded;
       
    }
	/*public URLItem(String name, byte[] value, boolean urlEncoded) {
        this.name = name;
        //this.value = value;
        this.urlEncoded=urlEncoded;
    }*/
    
    /**
	 * @return the urlEncoded
	 */
	public boolean isUrlEncoded() {
		return urlEncoded;
	}

	/**
	 * @param urlEncoded the urlEncoded to set
	 */
	public void setUrlEncoded(boolean urlEncoded) {
		this.urlEncoded = urlEncoded;
	}

	/**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }
    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

}