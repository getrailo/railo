

package railo.commons.lang;

import java.io.UnsupportedEncodingException;

/**
 * Name Value Pair
 */
public final class ByteNameValuePair {

    private byte[] name;
    private byte[] value;
	private boolean urlEncoded;
    
    /**
     * constructor of the class
     * @param name
     * @param value
     */
    public ByteNameValuePair(byte[] name, byte[] value,boolean urlEncoded) {
        this.name = name;
        this.value = value;
        this.urlEncoded = urlEncoded;
    }
    
    /**
     * @return Returns the name.
     */
    public byte[] getName() {
        return name;
    }
    
    /**
     * @param encoding 
     * @return Returns the name.
     * @throws UnsupportedEncodingException 
     */
    public String getName(String encoding) throws UnsupportedEncodingException {
        return new String(name,encoding);
    }
    
    /**
     * @param encoding 
     * @param defaultValue 
     * @return Returns the name.
     */
    public String getName(String encoding, String defaultValue) {
        try {
            return new String(name,encoding);
        } catch (UnsupportedEncodingException e) {
            return defaultValue;
        }
    }
    
    /**
     * @param name The name to set.
     */
    public void setName(byte[] name) {
        this.name = name;
    }
    /**
     * @return Returns the value.
     */
    public byte[] getValue() {
        return value;
    }
    
    /**
     * @param encoding 
     * @return Returns the name.
     * @throws UnsupportedEncodingException 
     */
    public String getValue(String encoding) throws UnsupportedEncodingException {
        return new String(value,encoding);
    }
    
    /**
     * @param encoding 
     * @param defaultValue 
     * @return Returns the name.
     */
    public String getValue(String encoding, String defaultValue) {
        try {
            return new String(value,encoding);
        } catch (UnsupportedEncodingException e) {
            return defaultValue;
        }
    }
    
    /**
     * @param value The value to set.
     */
    public void setValue(byte[] value) {
        this.value = value;
    }

	/**
	 * @return the urlEncoded
	 */
	public boolean isUrlEncoded() {
		return urlEncoded;
	}

}