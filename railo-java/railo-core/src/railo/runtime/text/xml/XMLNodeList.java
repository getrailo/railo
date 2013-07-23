package railo.runtime.text.xml;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.struct.XMLObject;
import railo.runtime.text.xml.struct.XMLStruct;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.ArraySupport;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.util.ArrayIterator;

/**
 * 
 */
public final class XMLNodeList extends ArraySupport implements NodeList, XMLObject{

	private boolean caseSensitive;
	private Document doc;
	private Node parent;
	private String filter;
	
	/**
	 * @param parent Parent Node
	 * @param caseSensitive
	 */
    public XMLNodeList(Node parent, boolean caseSensitive) {
    	this(parent,caseSensitive,null);
    }
    public XMLNodeList(Node parent, boolean caseSensitive, String filter) {
        if(parent instanceof XMLStruct) {
            XMLStruct xmlNode = ((XMLStruct)parent);
            this.parent=xmlNode.toNode();
            this.caseSensitive=xmlNode.getCaseSensitive();
        }
        else {
            this.parent=parent;
            this.caseSensitive=caseSensitive;
        }
        this.doc=this.parent.getOwnerDocument();
        this.filter=filter;
    }

	@Override
	public int getLength() {
		return XMLUtil.childNodesLength(parent,Node.ELEMENT_NODE,caseSensitive,filter);
	}

	@Override
	public Node item(int index) {
		return XMLCaster.toXMLStruct(getChildNode(index),caseSensitive);
	}

	@Override
	public int size() {
		return getLength();
	}

	@Override
	public Collection.Key[] keys() {
		Collection.Key[] keys=new Collection.Key[getLength()];
		for(int i=1;i<=keys.length;i++) {
			keys[i-1]=KeyImpl.init(i+"");
		}
		return keys;
	}
	
	@Override
	public int[] intKeys() {
		int[] keys=new int[getLength()];
		for(int i=1;i<=keys.length;i++) {
			keys[i-1]=i;
		}
		return keys;
	}
	
	public Object removeEL(Collection.Key key) {
		return removeEL(Caster.toIntValue(key.getString(),-1));
	}


	@Override
	public Object remove(Collection.Key key) throws PageException {
		return removeE(Caster.toIntValue(key.getString()));
	}

	@Override
	public Object removeEL(int index) {
		int len=size();
		if(index<1 || index>len) return null;
		try {
			return XMLCaster.toXMLStruct(parent.removeChild(XMLCaster.toRawNode(item(index-1))),caseSensitive);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Object removeE(int index) throws PageException {
		int len=size();
		if(index<1 || index>len)
			throw new ExpressionException("can't remove value form XML Node List at index "+index+
			        ", valid indexes goes from 1 to "+len);
		return XMLCaster.toXMLStruct(parent.removeChild(XMLCaster.toRawNode(item(index-1))),caseSensitive);
	}

	@Override
	public void clear() {
		Node[] nodes=getChildNodesAsArray();
		for(int i=0;i<nodes.length;i++) {
			parent.removeChild(XMLCaster.toRawNode(nodes[i]));
		}
	}


	@Override
	public Object get(String key) throws ExpressionException {
		return getE(Caster.toIntValue(key));
	}

	@Override
	public Object get(Collection.Key key) throws ExpressionException {
		return get(key.getString());
	}
	
	@Override
	public Object getE(int key) throws ExpressionException {
		Object rtn= item(key-1);
		if(rtn==null) throw new ExpressionException("invalid index ["+key+"] for XML Node List , indexes goes from [0-"+size()+"]");
		return rtn;
	}

	@Override
	public Object get(String key, Object defaultValue) {
		int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return defaultValue;
	    return get(index,defaultValue);
	}

	@Override
	public Object get(Collection.Key key, Object defaultValue) {
		return get(key.getString(),defaultValue);
	}

	@Override
	public Object get(int key, Object defaultValue) {
		Object rtn= item(key-1);
		if(rtn==null) return defaultValue;
		return rtn;
	}

	@Override
	public Object set(String key, Object value) throws PageException {
		return setE(Caster.toIntValue(key),value);
	}

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		return set(key.getString(), value);
	}
	
	@Override
	public Object setE(int index, Object value) throws PageException {
		// check min Index
			if(index<1)
				throw new ExpressionException("invalid index ["+index+"] to set a child node, valid indexes start at 1");
		
		Node[] nodes=getChildNodesAsArray();
		
		// if index Greater len append
		if(index>nodes.length) return append(value);
		
		// remove all children
		clear();
		
		// set all children before new Element
		for(int i=1;i<index;i++) {
			append(nodes[i-1]);
		}
		
		// set new Element
		append(XMLCaster.toNode(doc,value,true));
		
		// set all after new Element
		for(int i=index;i<nodes.length;i++) {
			append(nodes[i]);
		}
		
		return value;
	}

	@Override
	public Object setEL(String key, Object value) {
		int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return null;
		return setEL(index,value);
	}

	@Override
	public Object setEL(Collection.Key key, Object value) {
		return setEL(key.getString(), value);
	}
	
	@Override
	public Object setEL(int index, Object value) {
		try {
			return setE(index,value);
		} catch (PageException e) {
			return null;
		}
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
	
	public Iterator<Object> valueIterator() {
		Object[] values=new Object[getLength()];
		for(int i=0;i<values.length;i++) {
			values[i]=item(i);
		}
		return new ArrayIterator(values);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		maxlevel--;
		DumpTable table = new DumpTable("xml","#cc9999","#ffffff","#000000");
		table.setTitle("Array (XML Node List)");
		int len=size();
		
		for(int i=1;i<=len;i++) {
			table.appendRow(1,new SimpleDumpData(i),DumpUtil.toDumpData(item(i-1), pageContext,maxlevel,dp));
		}
		return table;
	}
	
	@Override
	public Object append(Object o) throws PageException {
		return parent.appendChild(XMLCaster.toNode(doc,o,true));
	}
	
	public Object appendEL(Object o) {
		try {
			return append(o);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Object clone() {
		return duplicate(true);
	}


	@Override
	public Collection duplicate(boolean deepCopy) {
		return new XMLNodeList(parent.cloneNode(deepCopy),caseSensitive);
	}
	

	@Override
	public int getDimension() {
		return 1;
	}
	
	@Override
	public boolean insert(int index, Object value) throws PageException {
		// check min Index
		if(index<1)
			throw new ExpressionException("invalid index ["+index+"] to insert a child node, valid indexes start at 1");
	
		Node[] nodes=getChildNodesAsArray();
		
		// if index Greater len append
			if(index>nodes.length) {
				append(value);
				return true;
			}
		
		// remove all children
		clear();
		
		// set all children before new Element
		for(int i=1;i<index;i++) {
			append(nodes[i-1]);
		}
		
		// set new Element
		append(XMLCaster.toNode(doc,value,true));
		
		// set all after new Element
		for(int i=index;i<=nodes.length;i++) {
			append(nodes[i-1]);
		}
		
		return true;
	}
	
	@Override
	public Object prepend(Object o) throws PageException {
		
		Node[] nodes=getChildNodesAsArray();
		
		// remove all children
		clear();
		
		// set new Element
		append(XMLCaster.toNode(doc,o,true));
		
		// set all after new Element
		for(int i=0;i<nodes.length;i++) {
			append(nodes[i]);
		}
		return o;
	}
	
	@Override
	public void resize(int to) throws ExpressionException {
		if(to>size())throw new ExpressionException("can't resize a XML Node List Array with empty Elements");
	}
	
	@Override
	public void sort(String sortType, String sortOrder)
			throws ExpressionException {
		throw new ExpressionException("can't sort a XML Node List Array","sorttype:"+sortType+";sortorder:"+sortOrder);
	}
	
	@Override
	public void sort(Comparator comp)
			throws ExpressionException {
		throw new ExpressionException("can't sort a XML Node List Array");
	}

	@Override
	public Object[] toArray() {
		return getChildNodesAsArray();
	}
	
	public ArrayList toArrayList() {
		Object[] arr=toArray();
		ArrayList list=new ArrayList();
		for(int i=0;i>arr.length;i++) {
			list.add(arr[i]);
		}
		return list;
	}
	
	/**
	 * @return returns a output from the content as plain Text
	 */
	public String toPlain() {
		StringBuffer sb=new StringBuffer();
		int length=size();
		for(int i=1;i<=length;i++) {
			sb.append(i);
			sb.append(": ");
			sb.append(get(i,null));
			sb.append("\n");
		}
		return sb.toString();
	}

    private NodeList getChildNodes() {
		return XMLUtil.getChildNodes(parent,Node.ELEMENT_NODE,caseSensitive,filter);
	}
    
    private Node getChildNode(int index) {
		return XMLUtil.getChildNode(parent,Node.ELEMENT_NODE,caseSensitive,filter,index);
	}
	
	private Node[] getChildNodesAsArray() {
		return XMLUtil.getChildNodesAsArray(parent,Node.ELEMENT_NODE,caseSensitive,filter);
	}

    @Override
    public boolean containsKey(String key) {
        return get(key,null)!=null;
    }   

	@Override
	public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
	}

    @Override
    public boolean containsKey(int key) {
        return get(key,null)!=null;
    }

    @Override
    public boolean getCaseSensitive() {
        return caseSensitive;
    }      
    
    @Override
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NodeList to String");
    }
    
	@Override
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    @Override
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NodeList to a boolean value");
    }
    
    @Override
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    @Override
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NodeList to a number value");
    }
    
    @Override
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    @Override
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NodeList to a Date");
    }
    
    @Override
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	@Override
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare XML NodeList with a boolean value");
	}

	@Override
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare XML NodeList with a DateTime Object");
	}

	@Override
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare XML NodeList with a numeric value");
	}

	@Override
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare XML NodeList with a String");
	}

	@Override
	public long sizeOf() {
		return ArrayUtil.sizeOf((List)this);
	}
}