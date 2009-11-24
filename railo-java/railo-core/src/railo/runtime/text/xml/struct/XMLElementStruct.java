package railo.runtime.text.xml.struct;

import java.lang.reflect.Method;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.util.ArrayUtil;


/**
 * 
 */
public class XMLElementStruct extends XMLNodeStruct implements Element {
	
	
	private Element element;
    
	/**
	 * constructor of the class
	 * @param element
	 * @param caseSensitive
	 */
	protected XMLElementStruct(Element element, boolean caseSensitive) {
		super(element, caseSensitive);
		if(element instanceof XMLElementStruct)
			element=((XMLElementStruct)element).getElement();
		this.element=element;
	}
	/**
	 * @see org.w3c.dom.Element#getTagName()
	 */
	public String getTagName() {
		return element.getTagName();
	}
	/**
	 * @see org.w3c.dom.Element#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) throws DOMException {
		element.removeAttribute(name);
	}
	/**
	 * @see org.w3c.dom.Element#hasAttribute(java.lang.String)
	 */
	public boolean hasAttribute(String name) {
		return element.hasAttribute(name);
	}
	/**
	 * @see org.w3c.dom.Element#getAttribute(java.lang.String)
	 */
	public String getAttribute(String name) {
		return element.getAttribute(name);
	}
	/**
	 * @see org.w3c.dom.Element#removeAttributeNS(java.lang.String, java.lang.String)
	 */
	public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
		element.removeAttributeNS(namespaceURI,localName);
	}
	/**
	 * @see org.w3c.dom.Element#setAttribute(java.lang.String, java.lang.String)
	 */
	public void setAttribute(String name, String value) throws DOMException {
		element.setAttribute(name,value);
	}
	/**
	 * @see org.w3c.dom.Element#hasAttributeNS(java.lang.String, java.lang.String)
	 */
	public boolean hasAttributeNS(String namespaceURI, String localName) {
		return element.hasAttributeNS(namespaceURI,localName);
	}
	/**
	 * @see org.w3c.dom.Element#getAttributeNode(java.lang.String)
	 */
	public Attr getAttributeNode(String name) {
		return element.getAttributeNode(name);
	}
	/**
	 * @see org.w3c.dom.Element#removeAttributeNode(org.w3c.dom.Attr)
	 */
	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		return element.removeAttributeNode(oldAttr);
	}
	/**
	 * @see org.w3c.dom.Element#setAttributeNode(org.w3c.dom.Attr)
	 */
	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		return element.setAttributeNode(newAttr);
	}
	/**
	 * @see org.w3c.dom.Element#setAttributeNodeNS(org.w3c.dom.Attr)
	 */
	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return element.setAttributeNodeNS(newAttr);
	}
	/**
	 * @see org.w3c.dom.Element#getElementsByTagName(java.lang.String)
	 */
	public NodeList getElementsByTagName(String name) {
		return element.getElementsByTagName(name);
	}
	/**
	 * @see org.w3c.dom.Element#getAttributeNS(java.lang.String, java.lang.String)
	 */
	public String getAttributeNS(String namespaceURI, String localName) {
		return element.getAttributeNS(namespaceURI,localName);
	}
	/**
	 * @see org.w3c.dom.Element#setAttributeNS(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void setAttributeNS(String namespaceURI, String qualifiedName,String value) throws DOMException {
		element.setAttributeNS(namespaceURI,qualifiedName,value);
	}
	/**
	 * @see org.w3c.dom.Element#getAttributeNodeNS(java.lang.String, java.lang.String)
	 */
	public Attr getAttributeNodeNS(String namespaceURI, String localName) {
		return element.getAttributeNodeNS(namespaceURI,localName);
	}
	/**
	 * @see org.w3c.dom.Element#getElementsByTagNameNS(java.lang.String, java.lang.String)
	 */
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		return element.getElementsByTagNameNS(namespaceURI,localName);
	}
	
    /**
     *
     * @see org.w3c.dom.Element#setIdAttribute(java.lang.String, boolean)
     */
    public void setIdAttribute(String name, boolean isId) throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = element.getClass().getMethod("setIdAttribute", new Class[]{name.getClass(),boolean.class});
			m.invoke(element, new Object[]{name,Caster.toBoolean(isId)});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
    }
    
    /**
     *
     * @see org.w3c.dom.Element#setIdAttributeNS(java.lang.String, java.lang.String, boolean)
     */
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = element.getClass().getMethod("setIdAttributeNS", new Class[]{namespaceURI.getClass(),localName.getClass(),boolean.class});
			m.invoke(element, new Object[]{namespaceURI,localName,Caster.toBoolean(isId)});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
    }
    
    /**
     *
     * @see org.w3c.dom.Element#setIdAttributeNode(org.w3c.dom.Attr, boolean)
     */
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = element.getClass().getMethod("setIdAttributeNode", new Class[]{idAttr.getClass(),boolean.class});
			m.invoke(element, new Object[]{idAttr,Caster.toBoolean(isId)});
		} 
		catch (Exception e) {
	        element.setAttributeNodeNS(idAttr);
		}
    }
    
	/**
	 *
	 * @see org.w3c.dom.Element#getSchemaTypeInfo()
	 */
	public TypeInfo getSchemaTypeInfo() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = element.getClass().getMethod("getSchemaTypeInfo", new Class[]{});
			return (TypeInfo) m.invoke(element, ArrayUtil.OBJECT_EMPTY);
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}
	/**
	 * @return the element
	 */
	public Element getElement() {
		return element;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return new XMLElementStruct((Element)element.cloneNode(deepCopy),caseSensitive);
	}
	

	/**
	 * @see org.w3c.dom.Node#cloneNode(boolean)
	 */
	public Node cloneNode(boolean deep) {
		return new XMLElementStruct((Element)element.cloneNode(deep),caseSensitive);
	}
}