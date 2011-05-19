package railo.runtime.text.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpTablePro;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.text.xml.struct.XMLObject;
import railo.runtime.text.xml.struct.XMLStruct;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ArraySupport;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.StructUtil;
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

	/**
	 * @see org.w3c.dom.NodeList#getLength()
	 */
	public int getLength() {
		return XMLUtil.childNodesLength(parent,Node.ELEMENT_NODE,caseSensitive,filter);
	}

	/**
	 * @see org.w3c.dom.NodeList#item(int)
	 */
	public Node item(int index) {
		return XMLCaster.toXMLStruct(getChildNode(index),caseSensitive);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return getLength();
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		String[] keys=new String[getLength()];
		for(int i=1;i<=keys.length;i++) {
			keys[i-1]=i+"";
		}
		return keys;
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		return StructUtil.toCollectionKeys(keysAsString());
	}
	
	/**
	 * @see railo.runtime.type.Array#intKeys()
	 */
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


	/**
	 *
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		return removeE(Caster.toIntValue(key.getString()));
	}

	/**
	 * @see railo.runtime.type.Array#removeEL(int)
	 */
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
	
	/**
	 * @see railo.runtime.type.Array#removeE(int)
	 */
	public Object removeE(int index) throws PageException {
		int len=size();
		if(index<1 || index>len)
			throw new ExpressionException("can't remove value form XML Node List at index "+index+
			        ", valid indexes goes from 1 to "+len);
		return XMLCaster.toXMLStruct(parent.removeChild(XMLCaster.toRawNode(item(index-1))),caseSensitive);
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		Node[] nodes=getChildNodesAsArray();
		for(int i=0;i<nodes.length;i++) {
			parent.removeChild(XMLCaster.toRawNode(nodes[i]));
		}
	}


	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws ExpressionException {
		return getE(Caster.toIntValue(key));
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws ExpressionException {
		return get(key.getString());
	}
	
	/**
	 * @see railo.runtime.type.Array#getE(int)
	 */
	public Object getE(int key) throws ExpressionException {
		Object rtn= item(key-1);
		if(rtn==null) throw new ExpressionException("invalid index ["+key+"] for XML Node List , indexes goes from [0-"+size()+"]");
		return rtn;
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return defaultValue;
	    return get(index,defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return get(key.getString(),defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Array#get(int, java.lang.Object)
	 */
	public Object get(int key, Object defaultValue) {
		Object rtn= item(key-1);
		if(rtn==null) return defaultValue;
		return rtn;
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		return setE(Caster.toIntValue(key),value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		return set(key.getString(), value);
	}
	
	/**
	 * @see railo.runtime.type.Array#setE(int, java.lang.Object)
	 */
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
		append(XMLCaster.toNode(doc,value));
		
		// set all after new Element
		for(int i=index;i<nodes.length;i++) {
			append(nodes[i]);
		}
		
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) return null;
		return setEL(index,value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		return setEL(key.getString(), value);
	}
	
	/**
	 * @see railo.runtime.type.Array#setEL(int, java.lang.Object)
	 */
	public Object setEL(int index, Object value) {
		try {
			return setE(index,value);
		} catch (PageException e) {
			return null;
		}
	}

	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return new ArrayIterator(keysAsString());
	}
	
	/**
	 *
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return keyIterator();
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		maxlevel--;
		DumpTable table = new DumpTablePro("xml","#cc9999","#ffffff","#000000");
		table.setTitle("Array (XML Node List)");
		int len=size();
		
		for(int i=1;i<=len;i++) {
			table.appendRow(1,new SimpleDumpData(i),DumpUtil.toDumpData(item(i-1), pageContext,maxlevel,dp));
		}
		return table;
	}
	
	/**
	 * @see railo.runtime.type.Array#append(java.lang.Object)
	 */
	public Object append(Object o) throws PageException {
		return parent.appendChild(XMLCaster.toNode(doc,o));
	}
	
	public Object appendEL(Object o) {
		try {
			return append(o);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return Duplicator.duplicate(this,true);
	}


	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy,Map<Object, Object> done) {
		return new XMLNodeList(parent.cloneNode(deepCopy),caseSensitive);
	}
	

	/**
	 * @see railo.runtime.type.Array#getDimension()
	 */
	public int getDimension() {
		return 1;
	}
	
	/**
	 * @see railo.runtime.type.Array#insert(int, java.lang.Object)
	 */
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
		append(XMLCaster.toNode(doc,value));
		
		// set all after new Element
		for(int i=index;i<=nodes.length;i++) {
			append(nodes[i-1]);
		}
		
		return true;
	}
	
	/**
	 * @see railo.runtime.type.Array#prepend(java.lang.Object)
	 */
	public Object prepend(Object o) throws PageException {
		
		Node[] nodes=getChildNodesAsArray();
		
		// remove all children
		clear();
		
		// set new Element
		append(XMLCaster.toNode(doc,o));
		
		// set all after new Element
		for(int i=0;i<nodes.length;i++) {
			append(nodes[i]);
		}
		return o;
	}
	
	/**
	 * @see railo.runtime.type.Array#resize(int)
	 */
	public void resize(int to) throws ExpressionException {
		if(to>size())throw new ExpressionException("can't resize a XML Node List Array with empty Elements");
	}
	
	/**
	 * @see railo.runtime.type.Array#sort(java.lang.String, java.lang.String)
	 */
	public void sort(String sortType, String sortOrder)
			throws ExpressionException {
		throw new ExpressionException("can't sort a XML Node List Array","sorttype:"+sortType+";sortorder:"+sortOrder);
	}

	/**
	 * @see railo.runtime.type.Array#toArray()
	 */
	public Object[] toArray() {
		return getChildNodesAsArray();
	}
	
	/**
	 * @see railo.runtime.type.Array#toArrayList()
	 */
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

    /**
     * @see railo.runtime.type.Collection#containsKey(java.lang.String)
     */
    public boolean containsKey(String key) {
        return get(key,null)!=null;
    }   

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
	}

    /**
     * @see railo.runtime.type.Array#containsKey(int)
     */
    public boolean containsKey(int key) {
        return get(key,null)!=null;
    }

    /**
     * @see railo.runtime.text.xml.struct.XMLObject#getCaseSensitive()
     */
    public boolean getCaseSensitive() {
        return caseSensitive;
    }      
    
    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast XML NodeList to String");
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
        throw new ExpressionException("Can't cast XML NodeList to a boolean value");
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
        throw new ExpressionException("Can't cast XML NodeList to a number value");
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
        throw new ExpressionException("Can't cast XML NodeList to a Date");
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
		throw new ExpressionException("can't compare XML NodeList with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare XML NodeList with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare XML NodeList with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare XML NodeList with a String");
	}

	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return ArrayUtil.sizeOf((List)this);
	}
}