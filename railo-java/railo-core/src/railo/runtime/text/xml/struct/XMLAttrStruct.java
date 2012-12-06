package railo.runtime.text.xml.struct;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

import railo.runtime.type.Collection;

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

	@Override
	public String getName() {
		return attr.getName();
	}

	@Override
	public Element getOwnerElement() {
		return new XMLElementStruct(attr.getOwnerElement(),caseSensitive);
	}

	@Override
	public boolean getSpecified() {
		return attr.getSpecified();
	}

	@Override
	public String getValue() {
		return attr.getValue();
	}

	@Override
	public void setValue(String arg0) throws DOMException {
		attr.setValue(arg0);
	}

	public TypeInfo getSchemaTypeInfo() {
		return null;
	}

	public boolean isId() {
		return false;
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return new XMLAttrStruct((Attr)attr.cloneNode(deepCopy),caseSensitive);
	}
	

	@Override
	public Node cloneNode(boolean deep) {
		return new XMLAttrStruct((Attr)attr.cloneNode(deep),caseSensitive);
	}
}