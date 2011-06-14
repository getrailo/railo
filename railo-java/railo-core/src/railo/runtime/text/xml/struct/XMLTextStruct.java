package railo.runtime.text.xml.struct;

import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Collection;

/**
 * 
 */
public final class XMLTextStruct extends XMLNodeStruct implements Text {
    
    
    private Text text;

    /**
     * @param text
     * @param caseSensitive
     */
    public XMLTextStruct(Text text, boolean caseSensitive) {
        super(text,caseSensitive);
        this.text=text;
    }

    /**
     * @see org.w3c.dom.Text#splitText(int)
     */
    public Text splitText(int offset) throws DOMException {
        return text.splitText(offset);
    }

    /**
     * @see org.w3c.dom.CharacterData#getLength()
     */
    public int getLength() {
        return text.getLength();
    }

    /**
     * @see org.w3c.dom.CharacterData#deleteData(int, int)
     */
    public void deleteData(int offset, int count) throws DOMException {
        text.deleteData(offset,count);
    }

    /**
     * @see org.w3c.dom.CharacterData#getData()
     */
    public String getData() throws DOMException {
        return text.getData();
    }

    /**
     * @see org.w3c.dom.CharacterData#substringData(int, int)
     */
    public String substringData(int offset, int count) throws DOMException {
        return text.substringData(offset,count);
    }

    /**
     * @see org.w3c.dom.CharacterData#replaceData(int, int, java.lang.String)
     */
    public void replaceData(int offset, int count, String arg)
            throws DOMException {
        text.replaceData(offset,count,arg);
    }

    /**
     * @see org.w3c.dom.CharacterData#insertData(int, java.lang.String)
     */
    public void insertData(int offset, String arg) throws DOMException {
        text.insertData(offset,arg);
    }

    /**
     * @see org.w3c.dom.CharacterData#appendData(java.lang.String)
     */
    public void appendData(String arg) throws DOMException {
        text.appendData(arg);
    }

    /**
     * @see org.w3c.dom.CharacterData#setData(java.lang.String)
     */
    public void setData(String data) throws DOMException {
        text.setData(data);
    }

    /**
     * @see org.w3c.dom.Text#isElementContentWhitespace()
     */
    public boolean isElementContentWhitespace() {
        return text.getNodeValue().trim().length()==0;
    }

    /**
     * @see org.w3c.dom.Text#getWholeText()
     */
    public String getWholeText() {
        return text.getNodeValue();
    }

    /**
     * @see org.w3c.dom.Text#replaceWholeText(java.lang.String)
     */
    public Text replaceWholeText(String content) throws DOMException {
        Text oldText = text;
        Document doc = XMLUtil.getDocument(text);
        Text newText = doc.createTextNode(content);
        Node parent = oldText.getParentNode();
        parent.replaceChild(XMLCaster.toRawNode(newText),XMLCaster.toRawNode(oldText));
        return oldText;
    }
    

	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
		return new XMLTextStruct((Text)text.cloneNode(deepCopy),caseSensitive);
	}
	

	/**
	 * @see org.w3c.dom.Node#cloneNode(boolean)
	 */
	public Node cloneNode(boolean deep) {
		return new XMLTextStruct((Text)text.cloneNode(deep),caseSensitive);
	}


}