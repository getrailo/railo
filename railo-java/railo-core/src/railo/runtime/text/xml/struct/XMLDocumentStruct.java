package railo.runtime.text.xml.struct;

import java.lang.reflect.Method;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;


/**
 * 
 */
public final class XMLDocumentStruct extends XMLNodeStruct implements Document {

	private Document doc;

	/**
	 * @param doc
	 * @param caseSensitive
	 */
	protected XMLDocumentStruct(Document doc, boolean caseSensitive) {
		super(doc, caseSensitive);
		this.doc=doc;
		
	}

	/**
	 * @see org.w3c.dom.Document#getImplementation()
	 */
	public DOMImplementation getImplementation() {
		return doc.getImplementation();
	}

	/**
	 * @see org.w3c.dom.Document#createDocumentFragment()
	 */
	public DocumentFragment createDocumentFragment() {
		return doc.createDocumentFragment();
	}

	/**
	 * @see org.w3c.dom.Document#getDoctype()
	 */
	public DocumentType getDoctype() {
		return doc.getDoctype();
	}

	/**
	 * @see org.w3c.dom.Document#getDocumentElement()
	 */
	public Element getDocumentElement() {
		return doc.getDocumentElement();
	}

	/**
	 * @see org.w3c.dom.Document#createAttribute(java.lang.String)
	 */
	public Attr createAttribute(String name) throws DOMException {
		return doc.createAttribute(name);
	}

	/**
	 * @see org.w3c.dom.Document#createCDATASection(java.lang.String)
	 */
	public CDATASection createCDATASection(String data) throws DOMException {
		return doc.createCDATASection(data);
	}

	/**
	 * @see org.w3c.dom.Document#createComment(java.lang.String)
	 */
	public Comment createComment(String data) {
		return doc.createComment(data);
	}

	/**
	 * @see org.w3c.dom.Document#createElement(java.lang.String)
	 */
	public Element createElement(String tagName) throws DOMException {
		return doc.createElement(tagName);
	}

	/**
	 * @see org.w3c.dom.Document#getElementById(java.lang.String)
	 */
	public Element getElementById(String elementId) {
		return doc.getElementById(elementId);
	}

	/**
	 * @see org.w3c.dom.Document#createEntityReference(java.lang.String)
	 */
	public EntityReference createEntityReference(String name) throws DOMException {
		return doc.createEntityReference(name);
	}

	/**
	 * @see org.w3c.dom.Document#importNode(org.w3c.dom.Node, boolean)
	 */
	public Node importNode(Node importedNode, boolean deep) throws DOMException {
		return doc.importNode(importedNode,deep);
	}

	/**
	 * @see org.w3c.dom.Document#getElementsByTagName(java.lang.String)
	 */
	public NodeList getElementsByTagName(String tagname) {
		return doc.getElementsByTagName(tagname);
	}

	/**
	 * @see org.w3c.dom.Document#createTextNode(java.lang.String)
	 */
	public Text createTextNode(String data) {
		return doc.createTextNode(data);
	}

	/**
	 * @see org.w3c.dom.Document#createAttributeNS(java.lang.String, java.lang.String)
	 */
	public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
		return doc.createAttributeNS(namespaceURI,qualifiedName);
	}

	/**
	 * @see org.w3c.dom.Document#createElementNS(java.lang.String, java.lang.String)
	 */
	public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
		return doc.createElementNS(namespaceURI,qualifiedName);
	}

	/**
	 * @see org.w3c.dom.Document#getElementsByTagNameNS(java.lang.String, java.lang.String)
	 */
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		return doc.getElementsByTagNameNS(namespaceURI,localName);
	}

	/**
	 * @see org.w3c.dom.Document#createProcessingInstruction(java.lang.String, java.lang.String)
	 */
	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
		return doc.createProcessingInstruction(target,data);
	}

	/**
	 * @see org.w3c.dom.Document#adoptNode(org.w3c.dom.Node)
	 */
	public Node adoptNode(Node arg0) throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("adoptNode", new Class[]{arg0.getClass()});
			return Caster.toNode(m.invoke(doc, new Object[]{arg0}));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 * @see org.w3c.dom.Document#getDocumentURI()
	 */
	public String getDocumentURI() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("getDocumentURI", new Class[]{});
			return Caster.toString(m.invoke(doc, new Object[]{}));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Document#getDomConfig()
	 */
	public DOMConfiguration getDomConfig() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("getDomConfig", new Class[]{});
			return (DOMConfiguration) m.invoke(doc, new Object[]{});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Document#getInputEncoding()
	 */
	public String getInputEncoding() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("getInputEncoding", new Class[]{});
			return Caster.toString(m.invoke(doc, new Object[]{}));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Document#getStrictErrorChecking()
	 */
	public boolean getStrictErrorChecking() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("getStrictErrorChecking", new Class[]{});
			return Caster.toBooleanValue(m.invoke(doc, new Object[]{}));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Document#getXmlEncoding()
	 */
	public String getXmlEncoding() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("getXmlEncoding", new Class[]{});
			return Caster.toString(m.invoke(doc, new Object[]{}));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Document#getXmlStandalone()
	 */
	public boolean getXmlStandalone() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("getXmlStandalone", new Class[]{});
			return Caster.toBooleanValue(m.invoke(doc, new Object[]{}));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Document#getXmlVersion()
	 */
	public String getXmlVersion() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("getXmlVersion", new Class[]{});
			return Caster.toString(m.invoke(doc, new Object[]{}));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Document#normalizeDocument()
	 */
	public void normalizeDocument() {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("normalizeDocument", new Class[]{});
			m.invoke(doc, new Object[]{});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 *
	 * @see org.w3c.dom.Document#renameNode(org.w3c.dom.Node, java.lang.String, java.lang.String)
	 */
	public Node renameNode(Node arg0, String arg1, String arg2) throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("renameNode", new Class[]{arg0.getClass(),arg1.getClass(),arg2.getClass()});
			return Caster.toNode(m.invoke(doc, new Object[]{arg0,arg1,arg2}));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 * @see org.w3c.dom.Document#setDocumentURI(java.lang.String)
	 */
	public void setDocumentURI(String arg0) {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("setDocumentURI", new Class[]{arg0.getClass()});
			m.invoke(doc, new Object[]{arg0});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
		
	}

	/**
	 * @see org.w3c.dom.Document#setStrictErrorChecking(boolean)
	 */
	public void setStrictErrorChecking(boolean arg0) {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("setStrictErrorChecking", new Class[]{boolean.class});
			m.invoke(doc, new Object[]{Caster.toBoolean(arg0)});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
		
	}

	/**
	 * @see org.w3c.dom.Document#setXmlStandalone(boolean)
	 */
	public void setXmlStandalone(boolean arg0) throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("setXmlStandalone", new Class[]{boolean.class});
			m.invoke(doc, new Object[]{Caster.toBoolean(arg0)});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
		
	}

	/**
	 * @see org.w3c.dom.Document#setXmlVersion(java.lang.String)
	 */
	public void setXmlVersion(String arg0) throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = doc.getClass().getMethod("setXmlVersion", new Class[]{arg0.getClass()});
			m.invoke(doc, new Object[]{arg0});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}
}