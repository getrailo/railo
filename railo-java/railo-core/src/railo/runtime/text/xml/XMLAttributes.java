package railo.runtime.text.xml;

import java.util.ArrayList;
import java.util.Iterator;

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
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.it.ValueIterator;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.StructSupport;

/**
 * represent a Struct and a NamedNodeMap
 */
public final class XMLAttributes extends StructSupport implements Struct,NamedNodeMap {
	

	private final NamedNodeMap nodeMap;
	private final Document owner;
	private final Node parent;
	private final boolean caseSensitive;

	/**
	 * constructor of the class (readonly)
	 * @param nodeMap
	 */
	public XMLAttributes(Node parent, boolean caseSensitive) {
		this.owner=parent.getOwnerDocument();
		this.parent=parent;
		this.nodeMap=parent.getAttributes();
		this.caseSensitive=caseSensitive;
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		Collection.Key[] keys=keys();
		maxlevel--;
		DumpTable table = new DumpTable("xml","#999966","#cccc99","#000000");
		table.setTitle("Struct (XML Attributes)");

		int maxkeys=dp.getMaxKeys();
		int index=0;
		Collection.Key k;
		for(int i=0;i<keys.length;i++) {
			k=keys[i];
			
			if(DumpUtil.keyValid(dp,maxlevel, k)){
				if(maxkeys<=index++)break;
				table.appendRow(1,new SimpleDumpData(k.getString()),DumpUtil.toDumpData(get(k.getString(),null), pageContext,maxlevel,dp));
			}
		}
		return table;
	}
	

	@Override
	public int size() {
		return nodeMap.getLength();
	}
	
	@Override
	public Collection.Key[] keys() {
		int len=nodeMap.getLength();
		ArrayList<Collection.Key> list =new ArrayList<Collection.Key>();
		for(int i=0;i<len;i++) {
			Node item = nodeMap.item(i);
			if(item instanceof Attr)
				list.add(KeyImpl.init(((Attr)item).getName()));
		}
		return list.toArray(new Collection.Key[list.size()]);
	}
    
	@Override
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
    
    @Override
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
    
	@Override
	public void clear() {
		Collection.Key[] keys=keys();
		for(int i=0;i<keys.length;i++) {
			nodeMap.removeNamedItem(keys[i].getString());
		}
	}

	@Override
	public Object get(Collection.Key key) throws ExpressionException {
		Node rtn = nodeMap.getNamedItem(key.getString());
		if(rtn!=null) return rtn.getNodeValue();
		
		Collection.Key[] keys=keys();
		for(int i=0;i<keys.length;i++) {
			if(key.equalsIgnoreCase(keys[i]))
				return nodeMap.getNamedItem(keys[i].getString()).getNodeValue();
		}
		throw new ExpressionException("No Attribute "+key.getString()+" defined for tag","attributes are ["+ListUtil.arrayToList(keys,", ")+"]");
	}

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		Node rtn = nodeMap.getNamedItem(key.getString());
		if(rtn!=null) return rtn.getNodeValue();
		
		Collection.Key[] keys=keys();
		for(int i=0;i<keys.length;i++) {
			if(key.equalsIgnoreCase(keys[i]))
				return nodeMap.getNamedItem(keys[i].getString()).getNodeValue();
		}
		return defaultValue;
	}

	@Override
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
	
	@Override
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
	public int getLength() {
		return nodeMap.getLength();
	}

	@Override
	public Node item(int index) {
		return nodeMap.item(index);
	}

	@Override
	public Node getNamedItem(String name) {
		return nodeMap.getNamedItem(name);
	}

	@Override
	public Node removeNamedItem(String name) throws DOMException {
		return nodeMap.removeNamedItem(name);
	}

	@Override
	public Node setNamedItem(Node arg) throws DOMException {
		return nodeMap.setNamedItem(arg);
	}

	@Override
	public Node setNamedItemNS(Node arg) throws DOMException {
		return nodeMap.setNamedItemNS(arg);
	}

	@Override
	public Node getNamedItemNS(String namespaceURI, String localName) {
		return nodeMap.getNamedItemNS(namespaceURI,localName);
	}

	@Override
	public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
		return nodeMap.removeNamedItemNS(namespaceURI, localName);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return new XMLAttributes(parent.cloneNode(deepCopy),caseSensitive);
	}
	
	
	/**
	 * @return returns named Node map
	 */
	public NamedNodeMap toNamedNodeMap() {
		return nodeMap;
	}

	@Override
	public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
	}
    
    @Override
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NamedNodeMap to String");
    }
    
	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NamedNodeMap to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NamedNodeMap to a number value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws ExpressionException {
    	throw new ExpressionException("Can't cast XML NamedNodeMap to a date value");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare XML NamedNodeMap with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare XML NamedNodeMap with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare XML NamedNodeMap with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare XML NamedNodeMap with a String");
	}
}