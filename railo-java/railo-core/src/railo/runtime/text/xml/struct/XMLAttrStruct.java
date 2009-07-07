package railo.runtime.text.xml.struct;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

/**
 * 
 */
public final class XMLAttrStruct extends XMLNodeStruct implements Attr {

    private Attr attr;

    /**
     * constructor of the class
     * @param section
     * @param caseSensitive
     */
    public XMLAttrStruct(Attr attr, boolean caseSensitive) {
        super(attr,caseSensitive);
        this.attr=attr;
    }

	/**
	 * @see org.w3c.dom.Attr#getName()
	 */
	public String getName() {
		return attr.getName();
	}

	/**
	 * @see org.w3c.dom.Attr#getOwnerElement()
	 */
	public Element getOwnerElement() {
		return new XMLElementStruct(attr.getOwnerElement(),caseSensitive);
	}

	/**
	 * @see org.w3c.dom.Attr#getSpecified()
	 */
	public boolean getSpecified() {
		return attr.getSpecified();
	}

	/**
	 * @see org.w3c.dom.Attr#getValue()
	 */
	public String getValue() {
		return attr.getValue();
	}

	/**
	 * @see org.w3c.dom.Attr#setValue(java.lang.String)
	 */
	public void setValue(String arg0) throws DOMException {
		attr.setValue(arg0);
	}

	/**
	 * @see org.w3c.dom.Attr#getSchemaTypeInfo()
	 */
	public TypeInfo getSchemaTypeInfo() {
		return null;
	}

	/**
	 * @see org.w3c.dom.Attr#isId()
	 */
	public boolean isId() {
		return false;
	}

}