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

import railo.commons.collection.MapFactory;
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
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.it.ValueIterator;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.StructSupport;

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
	
	@Override
	public Object remove(Key key) throws PageException {
        Object o= XMLUtil.removeProperty(node,key,caseSensitive);
        if(o!=null)return o;           
        throw new ExpressionException("node has no child with name ["+key+"]");
	}

	@Override
	public Object removeEL(Key key) {
        return  XMLUtil.removeProperty(node,key,caseSensitive);
	}
	
	@Override
	public Object get(Collection.Key key) throws PageException {
		try {		    
			return XMLUtil.getProperty(node,key,caseSensitive);
		} catch (SAXException e) {
			throw new XMLException(e);
		}
	}
	
	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		return XMLUtil.setProperty(node,key,value,caseSensitive);
	}
	
	/**
	 * @return retun the inner map
	 */
	public Map<String,Node> getMap() {
		NodeList elements=XMLUtil.getChildNodes(node,Node.ELEMENT_NODE,false,null);// TODO ist das false hier ok?
		Map<String,Node> map=MapFactory.<String,Node>getConcurrentMap();
		int len=elements.getLength();
		
		for(int i=0;i<len;i++) {
			Node node=elements.item(i);
			map.put(node.getNodeName(),node);
		}
		return map;
	}
	
	@Override
	public Collection duplicate(boolean deepCopy) {
		return new XMLNodeStruct(node.cloneNode(deepCopy),caseSensitive);
	}


	
	
	

	@Override
	public Node cloneNode(boolean deep) {
		return new XMLNodeStruct(node.cloneNode(deep),caseSensitive);
	}
	
	
	@Override
	public short getNodeType() {
		return node.getNodeType();
	}
	
	@Override
	public void normalize() {
		node.normalize();
	}
	
	@Override
	public boolean hasAttributes() {
		return node.hasAttributes();
	}
	
	@Override
	public boolean hasChildNodes() {
		return node.hasChildNodes();
	}
	
	@Override
	public String getLocalName() {
		return node.getLocalName();
	}
	
	@Override
	public String getNamespaceURI() {
		return node.getNamespaceURI();
	}
	
	@Override
	public String getNodeName() {
		return node.getNodeName();
	}
	
	@Override
	public String getNodeValue() throws DOMException {
		return node.getNodeValue();
	}
	
	@Override
	public String getPrefix() {
		return node.getPrefix();
	}
	
	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		node.setNodeValue(nodeValue);
	}
	
	@Override
	public void setPrefix(String prefix) throws DOMException {
		node.setPrefix(prefix);
	}
	
	@Override
	public Document getOwnerDocument() {
		if(node instanceof Document) return (Document) node;
		return node.getOwnerDocument();
	}
	
	@Override
	public NamedNodeMap getAttributes() {
		return new XMLAttributes(node,caseSensitive);
	}
	
	@Override
	public Node getFirstChild() {
		return node.getFirstChild();
	}
	
	@Override
	public Node getLastChild() {
		return node.getLastChild();
	}
	
	@Override
	public Node getNextSibling() {
		return node.getNextSibling();
	}
	
	@Override
	public Node getParentNode() {
		return node.getParentNode();
	}
	
	@Override
	public Node getPreviousSibling() {
		return node.getPreviousSibling();
	}
	
	
	
	@Override
	public NodeList getChildNodes() {
		return node.getChildNodes();
	}
	
	@Override
	public boolean isSupported(String feature, String version) {
		return node.isSupported(feature, version);
	}
	
	@Override
	public Node appendChild(Node newChild) throws DOMException {
		return node.appendChild(newChild);
	}
	
	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		return node.removeChild(XMLCaster.toRawNode(oldChild));
	}
	
	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return node.insertBefore(newChild, refChild);
	}
	
	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return node.replaceChild(XMLCaster.toRawNode(newChild), XMLCaster.toRawNode(oldChild));
	}
	
	@Override
	public int size() {
		NodeList list = node.getChildNodes();
		int len=list.getLength();
		int count=0;
		for(int i=0;i<len;i++) {
			if(list.item(i) instanceof Element) count++;
		}
		return count;
	}

	public Collection.Key[] keys() {
		NodeList elements=XMLUtil.getChildNodes(node,Node.ELEMENT_NODE,false,null);// TODO ist das false hie ok
		Collection.Key[] arr=new Collection.Key[elements.getLength()];
		for(int i=0;i<arr.length;i++) {
			arr[i]=KeyImpl.init(elements.item(i).getNodeName());
		}
		return arr;
	}
	
	@Override
	public void clear() {
		/*NodeList elements=XMLUtil.getChildNodes(node,Node.ELEMENT_NODE);
		int len=elements.getLength();
		for(int i=0;i<len;i++) {
			node.removeChild(elements.item(i));
		}*/
	}
	
	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		return XMLUtil.getProperty(node,key,caseSensitive,defaultValue);
	}

	@Override
	public Object setEL(Key key, Object value) {
		return XMLUtil.setProperty(node,key,value,caseSensitive,null);
	}
	
	@Override
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys());
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this,keys());
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return new ValueIterator(this,keys());
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return DumpUtil.toDumpData(node, pageContext,maxlevel,dp);
	}

	@Override
	public final Node toNode() {
		return node;
	}
	
	/**
	 * @return Returns the caseSensitive.
	 */
	public boolean getCaseSensitive() {
		return caseSensitive;
	}
	
	@Override
	public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
	}

    @Override
    public XMLNodeList getXMLNodeList() {
        return new XMLNodeList(node,getCaseSensitive());
    }   

    @Override
    public String castToString() throws PageException {
        return XMLCaster.toString(this.node);
    }
    
    @Override
    public String castToString(String defaultValue) {
        return XMLCaster.toString(this.node,defaultValue);
    }

    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML Node to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML Node to a number value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast XML Node to a Date");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToString(), b);
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare(castToString(), (Date)dt);
	}

	@Override
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToString(), d);
	}

	@Override
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}

    public String getBaseURI() {
        // not supported
        return null;
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        // not supported
        return -1;
    }

    public void setTextContent(String textContent) throws DOMException {
        //TODO  not supported
        throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR,"this method is not supported");
    }

    public boolean isSameNode(Node other) {
        return this==other;
    }

    public String lookupPrefix(String namespaceURI) {
//      TODO not supported
        return null;
    }

    public boolean isDefaultNamespace(String namespaceURI) {
//      TODO not supported
        return false;
    }

    public String lookupNamespaceURI(String prefix) {
//      TODO not supported
        return null;
    }

    public boolean isEqualNode(Node node) {
//      TODO not supported
        return this==node;
    }

    public Object getFeature(String feature, String version) {
        // TODO not supported
        return null;
    }

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