package railo.runtime.text.xml.struct;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.SAXException;

import railo.commons.collections.HashTable;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.exp.XMLException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.text.xml.XMLAttributes;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.text.xml.XMLNodeList;
import railo.runtime.text.xml.XMLUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.StructSupport;
import railo.runtime.util.ArrayIterator;

/**
 * 
 */
public class XMLNodeStruct extends StructSupport implements XMLStruct {
	
	private Node node;
	protected boolean caseSensitive;
    
	/**
	 * constructor of the class 
	 * @param node Node 
	 * @param caseSensitive
	 */
	protected XMLNodeStruct(Node node, boolean caseSensitive) {
		if(node instanceof XMLStruct)node=((XMLStruct)node).toNode();
		this.node=node;
		this.caseSensitive=caseSensitive;
	}
	
	/**
	 * @see railo.runtime.type.Collection#remove(java.lang.String)
	 */
	public Object remove(String key) throws PageException {
		Object o= XMLUtil.removeProperty(node,KeyImpl.init(key),caseSensitive);
        if(o!=null)return o;           
        throw new ExpressionException("node has no child with name ["+key+"]");
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
        Object o= XMLUtil.removeProperty(node,key,caseSensitive);
        if(o!=null)return o;           
        throw new ExpressionException("node has no child with name ["+key+"]");
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
        return  XMLUtil.removeProperty(node,key,caseSensitive);
	}
	
	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		try {		    
			return XMLUtil.getProperty(node,key,caseSensitive);
		} catch (SAXException e) {
			throw new XMLException(e);
		}
	}
	
	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		return XMLUtil.setProperty(node,key,value,caseSensitive);
	}
	
	/**
	 * @return retun the inner map
	 */
	public Map getMap() {
		NodeList elements=XMLUtil.getChildNodes(node,Node.ELEMENT_NODE,false,null);// TODO ist das false hier ok?
		Map map=new HashTable();
		int len=elements.getLength();
		
		for(int i=0;i<len;i++) {
			Node node=elements.item(i);
			map.put(node.getNodeName(),node);
		}
		return map;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return new XMLNodeStruct(node.cloneNode(deepCopy),caseSensitive);
	}


	
	
	

	/**
	 * @see org.w3c.dom.Node#cloneNode(boolean)
	 */
	public Node cloneNode(boolean deep) {
		return new XMLNodeStruct(node.cloneNode(deep),caseSensitive);
	}
	
	
	/**
	 * @see org.w3c.dom.Node#getNodeType()
	 */
	public short getNodeType() {
		return node.getNodeType();
	}
	
	/**
	 * @see org.w3c.dom.Node#normalize()
	 */
	public void normalize() {
		node.normalize();
	}
	
	/**
	 * @see org.w3c.dom.Node#hasAttributes()
	 */
	public boolean hasAttributes() {
		return node.hasAttributes();
	}
	
	/**
	 * @see org.w3c.dom.Node#hasChildNodes()
	 */
	public boolean hasChildNodes() {
		return node.hasChildNodes();
	}
	
	/**
	 * @see org.w3c.dom.Node#getLocalName()
	 */
	public String getLocalName() {
		return node.getLocalName();
	}
	
	/**
	 * @see org.w3c.dom.Node#getNamespaceURI()
	 */
	public String getNamespaceURI() {
		return node.getNamespaceURI();
	}
	
	/**
	 * @see org.w3c.dom.Node#getNodeName()
	 */
	public String getNodeName() {
		return node.getNodeName();
	}
	
	/**
	 * @see org.w3c.dom.Node#getNodeValue()
	 */
	public String getNodeValue() throws DOMException {
		return node.getNodeValue();
	}
	
	/**
	 * @see org.w3c.dom.Node#getPrefix()
	 */
	public String getPrefix() {
		return node.getPrefix();
	}
	
	/**
	 * @see org.w3c.dom.Node#setNodeValue(java.lang.String)
	 */
	public void setNodeValue(String nodeValue) throws DOMException {
		node.setNodeValue(nodeValue);
	}
	
	/**
	 * @see org.w3c.dom.Node#setPrefix(java.lang.String)
	 */
	public void setPrefix(String prefix) throws DOMException {
		node.setPrefix(prefix);
	}
	
	/**
	 * @see org.w3c.dom.Node#getOwnerDocument()
	 */
	public Document getOwnerDocument() {
		if(node instanceof Document) return (Document) node;
		return node.getOwnerDocument();
	}
	
	/**
	 * @see org.w3c.dom.Node#getAttributes()
	 */
	public NamedNodeMap getAttributes() {
		return new XMLAttributes(node.getAttributes(),caseSensitive);
	}
	
	/**
	 * @see org.w3c.dom.Node#getFirstChild()
	 */
	public Node getFirstChild() {
		return node.getFirstChild();
	}
	
	/**
	 * @see org.w3c.dom.Node#getLastChild()
	 */
	public Node getLastChild() {
		return node.getLastChild();
	}
	
	/**
	 * @see org.w3c.dom.Node#getNextSibling()
	 */
	public Node getNextSibling() {
		return node.getNextSibling();
	}
	
	/**
	 * @see org.w3c.dom.Node#getParentNode()
	 */
	public Node getParentNode() {
		return node.getParentNode();
	}
	
	/**
	 * @see org.w3c.dom.Node#getPreviousSibling()
	 */
	public Node getPreviousSibling() {
		return node.getPreviousSibling();
	}
	
	
	
	/**
	 * @see org.w3c.dom.Node#getChildNodes()
	 */
	public NodeList getChildNodes() {
		return node.getChildNodes();
	}
	
	/**
	 * @see org.w3c.dom.Node#isSupported(java.lang.String, java.lang.String)
	 */
	public boolean isSupported(String feature, String version) {
		return node.isSupported(feature, version);
	}
	
	/**
	 * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node)
	 */
	public Node appendChild(Node newChild) throws DOMException {
		return node.appendChild(newChild);
	}
	
	/**
	 * @see org.w3c.dom.Node#removeChild(org.w3c.dom.Node)
	 */
	public Node removeChild(Node oldChild) throws DOMException {
		return node.removeChild(XMLCaster.toRawNode(oldChild));
	}
	
	/**
	 * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node)
	 */
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return node.insertBefore(newChild, refChild);
	}
	
	/**
	 * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node)
	 */
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return node.replaceChild(XMLCaster.toRawNode(newChild), XMLCaster.toRawNode(oldChild));
	}
	
	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		NodeList list = node.getChildNodes();
		int len=list.getLength();
		int count=0;
		for(int i=0;i<len;i++) {
			if(list.item(i) instanceof Element) count++;
		}
		return count;
	}

	public String[] keysAsString() {
		NodeList elements=XMLUtil.getChildNodes(node,Node.ELEMENT_NODE,false,null);// TODO ist das false hier OK?
		String[] arr=new String[elements.getLength()];
		for(int i=0;i<arr.length;i++) {
			arr[i]=elements.item(i).getNodeName();
		}
		return arr;
	}

	public Collection.Key[] keys() {
		NodeList elements=XMLUtil.getChildNodes(node,Node.ELEMENT_NODE,false,null);// TODO ist das false hie ok
		Collection.Key[] arr=new Collection.Key[elements.getLength()];
		for(int i=0;i<arr.length;i++) {
			arr[i]=KeyImpl.init(elements.item(i).getNodeName());
		}
		return arr;
	}
	
	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		/*NodeList elements=XMLUtil.getChildNodes(node,Node.ELEMENT_NODE);
		int len=elements.getLength();
		for(int i=0;i<len;i++) {
			node.removeChild(elements.item(i));
		}*/
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return XMLUtil.getPropertyEL(node,key,caseSensitive);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return XMLUtil.setPropertyEL(node,key,value,caseSensitive);
	}
	
	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return new ArrayIterator(keysAsString());
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return DumpUtil.toDumpData(node, pageContext,maxlevel,dp);
	}

	/**
	 * @see railo.runtime.text.xml.struct.XMLStruct#toNode()
	 */
	public final Node toNode() {
		return node;
	}
	
	/**
	 * @return Returns the caseSensitive.
	 */
	public boolean getCaseSensitive() {
		return caseSensitive;
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
	}

    /**
     * @see railo.runtime.text.xml.struct.XMLStruct#getXMLNodeList()
     */
    public XMLNodeList getXMLNodeList() {
        return new XMLNodeList(node,getCaseSensitive());
    }   

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        return XMLCaster.toString(this.node);
    }
    
    /**
     * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
        return XMLCaster.toString(this.node,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML Node to a boolean value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML Node to a number value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast XML Node to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToString(), b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(castToString(), (Date)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToString(), d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}

    /**
     * @see org.w3c.dom.Node#getBaseURI()
     */
    public String getBaseURI() {
        // not supported
        return null;
    }

    /**
     * @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node)
     */
    public short compareDocumentPosition(Node other) throws DOMException {
        // not supported
        return -1;
    }

    /* *
     * @see org.w3c.dom.Node#getTextContent()
     * /
    public String getTextContent() throws DOMException {
        switch(node.getNodeType()) {
        case ELEMENT_NODE:
        case ATTRIBUTE_NODE:
        case ENTITY_NODE:
        case ENTITY_REFERENCE_NODE:
        case DOCUMENT_FRAGMENT_NODE:
            NodeList list = node.getChildNodes();
            int len=list.getLength();
            StringBuffer sb=new StringBuffer();
            for(int i=0;i<len;i++) {
                sb.append(list.item(i).getTextContent());
            }
            return sb.toString();
        case TEXT_NODE:
        case CDATA_SECTION_NODE:
        case COMMENT_NODE:
        case PROCESSING_INSTRUCTION_NODE:
            return node.getNodeValue();
        
        }
        return null;
    }*/

    /**
     * @see org.w3c.dom.Node#setTextContent(java.lang.String)
     */
    public void setTextContent(String textContent) throws DOMException {
        //TODO  not supported
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,"this method is not supported");
    }

    /**
     * @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node)
     */
    public boolean isSameNode(Node other) {
        return this==other;
    }

    /**
     * @see org.w3c.dom.Node#lookupPrefix(java.lang.String)
     */
    public String lookupPrefix(String namespaceURI) {
//      TODO not supported
        return null;
    }

    /**
     * @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String)
     */
    public boolean isDefaultNamespace(String namespaceURI) {
//      TODO not supported
        return false;
    }

    /**
     * @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String)
     */
    public String lookupNamespaceURI(String prefix) {
//      TODO not supported
        return null;
    }

    /**
     * @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node)
     */
    public boolean isEqualNode(Node node) {
//      TODO not supported
        return this==node;
    }

    /**
     * @see org.w3c.dom.Node#getFeature(java.lang.String, java.lang.String)
     */
    public Object getFeature(String feature, String version) {
        // TODO not supported
        return null;
    }

    /**
     * @see org.w3c.dom.Node#getUserData(java.lang.String)
     */
    public Object getUserData(String key) {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = node.getClass().getMethod("getUserData", new Class[]{key.getClass()});
			return m.invoke(node, new Object[]{key});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
    }

	/**
	 * @see org.w3c.dom.Node#getTextContent()
	 */
	public String getTextContent() throws DOMException {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = node.getClass().getMethod("getTextContent", new Class[]{});
			return Caster.toString(m.invoke(node, ArrayUtil.OBJECT_EMPTY));
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	/**
	 * @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler)
	 */
	public Object setUserData(String key, Object data, UserDataHandler handler) {
    	// dynamic load to support jre 1.4 and 1.5
		try {
			Method m = node.getClass().getMethod("setUserData", new Class[]{key.getClass(),data.getClass(),handler.getClass()});
			return m.invoke(node, new Object[]{key,data,handler});
		} 
		catch (Exception e) {
			throw new PageRuntimeException(Caster.toPageException(e));
		}
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	public boolean equals(Object obj) {
		if(!(obj instanceof XMLNodeStruct)) 
			return super.equals(obj);
		XMLNodeStruct other = ((XMLNodeStruct)obj);
		return other.caseSensitive=caseSensitive && other.node.equals(node);
	}
	
	

}