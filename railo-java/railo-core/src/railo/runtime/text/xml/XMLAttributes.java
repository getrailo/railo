package railo.runtime.text.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.XMLException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.util.StructSupport;
import railo.runtime.util.ArrayIterator;

/**
 * represent a Struct and a NamedNodeMap
 */
public final class XMLAttributes extends StructSupport implements Struct,NamedNodeMap {
	

	private NamedNodeMap nodeMap;
	private Document owner;
	private boolean caseSensitive;

	/**
	 * constructor of the class
	 * @param owner
	 * @param nodeMap
	 */
	public XMLAttributes(Document owner,NamedNodeMap nodeMap,boolean caseSensitive) {
		this.owner=owner;
		this.nodeMap=nodeMap;
		this.caseSensitive=caseSensitive;
	}
	/**
	 * constructor of the class (readonly)
	 * @param nodeMap
	 */
	public XMLAttributes(NamedNodeMap nodeMap,boolean caseSensitive) {
		this.nodeMap=nodeMap;
		try {
			owner=this.nodeMap.item(0).getOwnerDocument();
		}
		catch(Exception e) {}
		this.caseSensitive=caseSensitive;
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		String[] keys=keysAsString();
		maxlevel--;
		DumpTable table = new DumpTable("xml","#999966","#cccc99","#000000");
		table.setTitle("Struct (XML Attributes)");

		int maxkeys=dp.getMaxKeys();
		int index=0;
		for(int i=0;i<keys.length;i++) {
			String key=keys[i];
			
			if(DumpUtil.keyValid(dp,maxlevel, key)){
				if(maxkeys<=index++)break;
				table.appendRow(1,new SimpleDumpData(key),DumpUtil.toDumpData(get(key,null), pageContext,maxlevel,dp));
			}
		}
		return table;
	}
	

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return nodeMap.getLength();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		int len=nodeMap.getLength();
		ArrayList list = new ArrayList();
		for(int i=0;i<len;i++) {
			Node item = nodeMap.item(i);
			if(item instanceof Attr)
				list.add(((Attr)item).getName());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/**
	 *
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		int len=nodeMap.getLength();
		ArrayList list = new ArrayList();
		for(int i=0;i<len;i++) {
			Node item = nodeMap.item(i);
			if(item instanceof Attr)
				list.add(KeyImpl.init(((Attr)item).getName()));
		}
		return (Collection.Key[]) list.toArray(new Collection.Key[list.size()]);
	}

    /* *
     * @throws ExpressionException
     * @see railo.runtime.type.Struct#remove(java.lang.String)
     * /
    public Object remove (String key) throws ExpressionException {
    	Node rtn=null;
		if(!caseSensitive){
			int len = nodeMap.getLength();
			String nn;
			for(int i=len-1;i>=0;i--) {
				nn=nodeMap.item(i).getNodeName();
				if(key.equalsIgnoreCase(nn)) rtn=nodeMap.removeNamedItem(nn);
			}
		}
		else rtn=nodeMap.removeNamedItem(toName(key));
		
		if(rtn!=null) return rtn.getNodeValue();
		throw new ExpressionException("can't remove element with name ["+key+"], element doesn't exist");
    }*/
    
	/**
	 *
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key k) throws PageException {
		String key=k.getString();
		Node rtn=null;
		if(!caseSensitive){
			int len = nodeMap.getLength();
			String nn;
			for(int i=len-1;i>=0;i--) {
				nn=nodeMap.item(i).getNodeName();
				if(key.equalsIgnoreCase(nn)) rtn=nodeMap.removeNamedItem(nn);
			}
		}
		else rtn=nodeMap.removeNamedItem(toName(key));
		
		if(rtn!=null) return rtn.getNodeValue();
		throw new ExpressionException("can't remove element with name ["+key+"], element doesn't exist");
	}	
    
    /**
     * @see railo.runtime.type.Struct#removeEL(java.lang.String)
     */
    public Object removeEL(Collection.Key k) {
    	String key=k.getString();
		Node rtn=null;
		if(!caseSensitive){
			int len = nodeMap.getLength();
			String nn;
			for(int i=len-1;i>=0;i--) {
				nn=nodeMap.item(i).getNodeName();
				if(key.equalsIgnoreCase(nn)) rtn=nodeMap.removeNamedItem(nn);
			}
		}
		else rtn=nodeMap.removeNamedItem(toName(key));
		
		if(rtn!=null) return rtn.getNodeValue();
		return null;
    }
    
	/* *
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 * /
	public Object removeEL(Collection.Key key) {
		return removeEL(key.getString());
	}*/

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		String[] keys=keysAsString();
		for(int i=0;i<keys.length;i++) {
			nodeMap.removeNamedItem(keys[i]);
		}
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(Collection.Key key) throws ExpressionException {
		Node rtn = nodeMap.getNamedItem(key.getString());
		if(rtn!=null) return rtn.getNodeValue();
		
		Collection.Key[] keys=keys();
		for(int i=0;i<keys.length;i++) {
			if(key.equalsIgnoreCase(keys[i]))
				return nodeMap.getNamedItem(keys[i].getString()).getNodeValue();
		}
		throw new ExpressionException("No Attribute "+key.getString()+" defined for tag","attributes are ["+List.arrayToList(keys,", ")+"]");
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		try {
			return get(key);
		} catch (PageException e) {
			return defaultValue;
		}
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		if(owner==null) return value;
		
		try {
			Attr attr=owner.createAttribute(toName(key.getString()));
			attr.setValue(Caster.toString(value));
			nodeMap.setNamedItem(attr);
			
		}
		catch(DOMException de) {
			throw new XMLException(de);
		}
		
		
		
		
		return value;
	}

	private String toName(String name) {
		return toName(name,name);
	}
	private String toName(String name, String defaultValue) {
		if(caseSensitive) return name;
		
		Node n = nodeMap.getNamedItem(name);
		if(n!=null) return n.getNodeName();
		
		int len = nodeMap.getLength();
		String nn;
		for(int i=0;i<len;i++) {
			nn=nodeMap.item(i).getNodeName();
			if(name.equalsIgnoreCase(nn)) return nn;
		}
		return defaultValue;
	}
	
	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		if(owner==null) return value;
		try {
			Attr attr=owner.createAttribute(toName(key.getString()));
			attr.setValue(Caster.toString(value));
			nodeMap.setNamedItem(attr);
		}
		catch(Exception e) {
			return null;
		}
		return value;
	}
	
	@Override
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys());
	}
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this,keys());
	}

	/**
	 * @see org.w3c.dom.NamedNodeMap#getLength()
	 */
	public int getLength() {
		return nodeMap.getLength();
	}

	/**
	 * @see org.w3c.dom.NamedNodeMap#item(int)
	 */
	public Node item(int index) {
		return nodeMap.item(index);
	}

	/**
	 * @see org.w3c.dom.NamedNodeMap#getNamedItem(java.lang.String)
	 */
	public Node getNamedItem(String name) {
		return nodeMap.getNamedItem(name);
	}

	/**
	 * @see org.w3c.dom.NamedNodeMap#removeNamedItem(java.lang.String)
	 */
	public Node removeNamedItem(String name) throws DOMException {
		return nodeMap.removeNamedItem(name);
	}

	/**
	 * @see org.w3c.dom.NamedNodeMap#setNamedItem(org.w3c.dom.Node)
	 */
	public Node setNamedItem(Node arg) throws DOMException {
		return nodeMap.setNamedItem(arg);
	}

	/**
	 * @see org.w3c.dom.NamedNodeMap#setNamedItemNS(org.w3c.dom.Node)
	 */
	public Node setNamedItemNS(Node arg) throws DOMException {
		return nodeMap.setNamedItemNS(arg);
	}

	/**
	 * @see org.w3c.dom.NamedNodeMap#getNamedItemNS(java.lang.String, java.lang.String)
	 */
	public Node getNamedItemNS(String namespaceURI, String localName) {
		return nodeMap.getNamedItemNS(namespaceURI,localName);
	}

	/**
	 * @see org.w3c.dom.NamedNodeMap#removeNamedItemNS(java.lang.String, java.lang.String)
	 */
	public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
		return nodeMap.removeNamedItemNS(namespaceURI, localName);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		XMLAttributes sct=new XMLAttributes(owner,nodeMap,caseSensitive);
		ThreadLocalDuplication.set(this, sct);
		try{
			String[] keys=keysAsString();
			for(int i=0;i<keys.length;i++) {
				String key=keys[i];
				sct.setEL(key,Duplicator.duplicate(get(key,null),deepCopy));
			}
		}
		finally {
			ThreadLocalDuplication.remove(this);
		}
		return sct;
	}
	
	
	/**
	 * @return returns named Node map
	 */
	public NamedNodeMap toNamedNodeMap() {
		return nodeMap;
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
	}
    
    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NamedNodeMap to String");
    }
    
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NamedNodeMap to a boolean value");
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
        throw new ExpressionException("Can't cast XML NamedNodeMap to a number value");
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
    	throw new ExpressionException("Can't cast XML NamedNodeMap to a date value");
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
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare XML NamedNodeMap with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare XML NamedNodeMap with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare XML NamedNodeMap with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare XML NamedNodeMap with a String");
	}
}