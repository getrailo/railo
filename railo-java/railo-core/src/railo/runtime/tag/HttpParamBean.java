package railo.runtime.tag;

import railo.commons.io.res.Resource;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;


/**
 * 
 */
public final class HttpParamBean {

	/** Specifies the value of the URL, FormField, Cookie, File, or CGI variable being passed. */
	private Object value;

	/** The transaction type. */
	private String type;

	/** Required for type = "File". */
	private Resource file;

	/** A variable name for the data being passed. */
	private String name;
	
	private boolean encoded=true;
    
    private String mimeType="";
    
	/** set the value value
	 *  Specifies the value of the URL, FormField, Cookie, File, or CGI variable being passed.
	 * @param value value to set
	 **/
	public void setValue(Object value) {
		this.value=value;
	}
	/** set the value type
	 *  The transaction type.
	 * @param type value to set
	 **/
	public void setType(String type)	{
		this.type=type.toLowerCase().trim();
	}
	/** set the value file
	 *  Required for type = "File".
	 * @param file value to set
	 **/
	public void setFile(Resource file) {
		this.file=file;
	}
	/** set the value name
	 *  A variable name for the data being passed.
	 * @param name value to set
	 **/
	public void setName(String name){
		this.name=name;
	}

	/**
	 * @return Returns the file.
	 */
	public Resource getFile() {
		return file;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return Returns the value.
	 * @throws PageException 
	 */
	public String getValueAsString() throws PageException {
		return Caster.toString(value);
	}
	
	/**
	 * @return Returns the value.
	 */
	public Object getValue()  {
		return value;
	}
    /**
     * Returns the value of encoded.
     * @return value encoded
     */
    public boolean getEncoded() {
        return encoded;
    }
    /**
     * sets the encoded value.
     * @param encoded The encoded to set.
     */
    public void setEncoded(boolean encoded) {
        this.encoded = encoded;
    }
    /**
     * Returns the value of mimeType.
     * @return value mimeType
     */
    public String getMimeType() {
        return mimeType;
    }
    /**
     * sets the mimeType value.
     * @param mimeType The mimeType to set.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}