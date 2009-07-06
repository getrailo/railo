package railo.commons.lang;

/**
 * Name Value Pair
 */
public final class NameValuePair {

    private String name;
    private String value;
    
    /**
     * @param name
     * @param value
     */
    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
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