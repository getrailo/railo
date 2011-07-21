package railo.runtime.text.xml.struct;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.ArraySupport;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.wrap.ArrayAsArrayList;
import railo.runtime.util.ArrayIterator;

public class XMLMultiElementArray extends ArraySupport {

	private static final long serialVersionUID = -2673749147723742450L;
	private XMLMultiElementStruct struct;

	public XMLMultiElementArray(XMLMultiElementStruct struct) {
		this.struct=struct;
	}

	/**
	 * @see railo.runtime.type.Array#append(java.lang.Object)
	 */
	public Object append(Object o) throws PageException {
		return setE(size()+1,o);
	}

	/**
	 * @see railo.runtime.type.Array#appendEL(java.lang.Object)
	 */
	public Object appendEL(Object o) {
		return setEL(size()+1,o);
	}


	/**
	 * @see railo.runtime.type.Array#containsKey(int)
	 */
	public boolean containsKey(int key) {
		return get(key,null)!=null;
	}

	/**
	 *
	 * @see railo.runtime.type.Array#get(int, java.lang.Object)
	 */
	public Object get(int key, Object defaultValue) {
		return struct.get(key,defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Array#getE(int)
	 */
	public Object getE(int key) throws PageException {
		return struct.get(key);
	}


	/**
	 * @see railo.runtime.type.Array#getDimension()
	 */
	public int getDimension() {
		return struct.getInnerArray().getDimension();
	}

	/**
	 * @see railo.runtime.type.Array#insert(int, java.lang.Object)
	 */
	public boolean insert(int index, Object value) throws PageException {
    	Element element=XMLCaster.toElement(struct.getOwnerDocument(),value);
    	boolean rtn = struct.getInnerArray().insert(index, element);
    	Object obj = struct.getInnerArray().get(index,null);
        
    	if(obj instanceof Element) {
    		Element el = ((Element)obj);
    		el.getParentNode().insertBefore(XMLCaster.toRawNode(element), el);
    	}
    	else  {
    		struct.getParentNode().appendChild(XMLCaster.toRawNode(element));
    	}
    	return rtn;
	}

	/**
	 *
	 * @see railo.runtime.type.Array#intKeys()
	 */
	public int[] intKeys() {
		return struct.getInnerArray().intKeys();
	}

	/**
	 * @see railo.runtime.type.Array#prepend(java.lang.Object)
	 */
	public Object prepend(Object value) throws PageException {
    	Element element=XMLCaster.toElement(struct.getOwnerDocument(),value);
    	Object obj = struct.getInnerArray().get(1,null);
        
    	if(obj instanceof Element) {
    		Element el = ((Element)obj);
    		el.getParentNode().insertBefore(XMLCaster.toRawNode(element), el);
    	}
    	else  {
    		struct.getParentNode().appendChild(XMLCaster.toRawNode(element));
    	}
    	return struct.getInnerArray().prepend(element);
	}

	/**
	 *
	 * @see railo.runtime.type.Array#removeE(int)
	 */
	public Object removeE(int key) throws PageException {
		return struct.remove(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Array#removeEL(int)
	 */
	public Object removeEL(int key) {
		return struct.removeEL(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Array#resize(int)
	 */
	public void resize(int to) throws PageException {
		throw new PageRuntimeException("resizing of xml nodelist not allowed");
	}

	/**
	 *
	 * @see railo.runtime.type.Array#setE(int, java.lang.Object)
	 */
	public Object setE(int key, Object value) throws PageException {
		return struct.set(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Array#setEL(int, java.lang.Object)
	 */
	public Object setEL(int key, Object value) {
		return struct.setEL(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Array#sort(java.lang.String, java.lang.String)
	 */
	public void sort(String sortType, String sortOrder) throws PageException {
		if(size()<=1) return;
		
		struct.getInnerArray().sort(sortType, sortOrder);
		
		Object[] nodes = struct.getInnerArray().toArray();
		Node last=(Node) nodes[nodes.length-1],current;
		Node parent=last.getParentNode();
		for(int i=nodes.length-2;i>=0;i--) {
			current=(Node) nodes[i];
			parent.insertBefore(current, last);
			last=current;
		}// MUST testen
	}
	/**
	 *
	 * @see railo.runtime.type.Array#toArray()
	 */
	public Object[] toArray() {
		return struct.getInnerArray().toArray();
	}

	/**
	 *
	 * @see railo.runtime.type.Array#toArrayList()
	 */
	public ArrayList toArrayList() {
		return ArrayAsArrayList.toArrayList(this);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {//MUST
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(java.lang.String)
	 */
	public boolean containsKey(String key) {
		return struct.containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return struct.containsKey(key);
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		return new XMLMultiElementArray((XMLMultiElementStruct)struct.duplicate(deepCopy));
	}
	

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
		return struct.get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		return struct.get(key);
	}

	/**
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
		return struct.get(key,defaultValue);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		return struct.get(key,defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		return struct.getInnerArray().keys();
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
		return struct.getInnerArray().keysAsString();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(java.lang.String)
	 */
	public Object remove(String key) throws PageException {
		return struct.remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		return struct.remove(key);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		return struct.removeEL(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
		return struct.set(key, value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return struct.set(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public Object setEL(String key, Object value) {
		return struct.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		return struct.setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return struct.getInnerArray().size();
	}

	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return struct.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return struct.getInnerArray().iterator();
	}

	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator keyIterator() {
		return new ArrayIterator(keysAsString());
	}

	/**
	 *
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws PageException {
		return struct.castToBooleanValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return struct.castToBoolean(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() throws PageException {
		return struct.castToDateTime();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return struct.castToDateTime(defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() throws PageException {
		return struct.castToDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return struct.castToDoubleValue(defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() throws PageException {
		return struct.castToString();
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return struct.castToString(defaultValue);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return struct.compareTo(str);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return struct.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return struct.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return struct.compareTo(dt);
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return duplicate(true);
	}

	public boolean add(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return ArrayUtil.sizeOf((List)this);
	}

}
