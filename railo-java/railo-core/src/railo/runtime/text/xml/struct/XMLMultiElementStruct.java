package railo.runtime.text.xml.struct;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.text.xml.XMLCaster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection;

/**
 * Element that can contain more than one Element
 */
public final class XMLMultiElementStruct extends XMLElementStruct {

	private static final long serialVersionUID = -4921231279765525776L;
	private Array array;
    
    /**
     * Constructor of the class
     * @param array
     * @param caseSensitive
     * @throws PageException
     */
    public XMLMultiElementStruct(Array array, boolean caseSensitive) throws PageException {
        super(getFirstRaw(array),caseSensitive);
        this.array=array;
        
        if(array.size()==0)
            throw new ExpressionException("Array must have one Element at least");
        
        int[] ints=array.intKeys();
        for(int i=0;i<ints.length;i++) {
            Object o=array.get(ints[i],null);
            if(!(o instanceof Element)) {
                throw new ExpressionException("all Element in the Array must be of type Element");
            }
        }
    }

	private static Element getFirstRaw(Array array) throws PageException {
        if(array.size()==0)
            throw new ExpressionException("Array must have one Element at least");
        Element el=(Element) array.getE(1);
        if(el instanceof XMLElementStruct)
        	el=(Element) XMLCaster.toRawNode(((XMLElementStruct)el).getElement());
        return el;
        //return (Element)XMLCaster.toRawNode(array.getE(1));
    }

    /**
     *
     * @see railo.runtime.text.xml.struct.XMLNodeStruct#removeEL(railo.runtime.type.Collection.Key)
     */
    public Object removeEL(Collection.Key key) {
        int index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
        if(index==Integer.MIN_VALUE)return super.removeEL (key);
        return removeEL(index);
    }

	/**
	 * @see railo.runtime.type.Array#removeEL(int)
	 */
	public Object removeEL(int index) {
		Object o=array.removeEL(index);
		if(o instanceof Element) {
			Element el=(Element) o;
			//try {
				Node n = XMLCaster.toRawNode(el);
				el.getParentNode().removeChild(n);
			//} catch (PageException e) {}
		}
		return o;
	}
	
    
    /* *
     * @see railo.runtime.type.Collection#remove(java.lang.String)
     * /
    public Object remove (String key) throws PageException {
        int index=Caster.toIntValue(key,Integer.MIN_VALUE);
        if(index==Integer.MIN_VALUE)return super.remove (KeyImpl.init(key));
        return remove(index);
    }*/

    /**
     * @see railo.runtime.text.xml.struct.XMLNodeStruct#remove(railo.runtime.type.Collection.Key)
     */
    public Object remove(Collection.Key key) throws PageException {
        int index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
        if(index==Integer.MIN_VALUE)return super.remove (key);
        return remove(index);
    }

	/**
	 * @see railo.runtime.type.Array#removeE(int)
	 */
	public Object remove(int index) throws PageException {
		Object o=array.removeE(index);
		if(o instanceof Element) {
			Element el=(Element) o;
			el.getParentNode().removeChild(XMLCaster.toRawNode(el));
		}
		return o;
	}

    /**
     * @see railo.runtime.text.xml.struct.XMLNodeStruct#get(railo.runtime.type.Collection.Key)
     */
    public Object get(Collection.Key key) throws PageException  {
        int index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
        if(index==Integer.MIN_VALUE)return super.get(key);
        return get(index);
    }

	/**
	 *
	 * @see railo.runtime.type.Array#getE(int)
	 */
	public Object get(int index) throws PageException {
		return array.getE(index);
	}
    
    /**
     *
     * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
     */
    public Object get(Collection.Key key, Object defaultValue) {
        int index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
        if(index==Integer.MIN_VALUE)return super.get(key,defaultValue);
        return get(index,defaultValue);
    }

	/**
	 * @see railo.runtime.type.Array#get(int, java.lang.Object)
	 */
	public Object get(int index, Object defaultValue) {
		return array.get(index,defaultValue);
	}

    /**
     *
     * @see railo.runtime.text.xml.struct.XMLNodeStruct#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
     */
    public Object setEL(Collection.Key key, Object value) {
        try {
			return set(key,value);
		} catch (PageException e1) {
			return null;
		}
    }

	/**
	 * @param index
	 * @param value
	 * @return
	 */
	public Object setEL(int index, Object value) {
		try {
			return set(index, value);
		} catch (PageException e) {
			return null;
		}
	}

	/**
	 * @see railo.runtime.text.xml.struct.XMLNodeStruct#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
        int index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
        if(index==Integer.MIN_VALUE){
        	return super.set (key,value);
        }
        return set(index,value);
    }

	/**
	 * @see railo.runtime.type.Array#setE(int, java.lang.Object)
	 */
	public Object set(int index, Object value) throws PageException {
		Element element=XMLCaster.toElement(getOwnerDocument(),value);
    	Object obj = array.get(index,null);
        
    	if(obj instanceof Element) {
    		Element el = ((Element)obj);
    		el.getParentNode().replaceChild(XMLCaster.toRawNode(element), XMLCaster.toRawNode(el));
    	}
    	else if(array.size()+1==index) {
    		getParentNode().appendChild(XMLCaster.toRawNode(element));
    	}
    	else {
    		throw new ExpressionException("the index for child node is out of range","valid range is from 1 to "+(array.size()+1));
    	}
    	return array.setE(index,element);
	}
    

    /**
     *
     * @see railo.runtime.text.xml.struct.XMLNodeStruct#containsKey(railo.runtime.type.Collection.Key)
     */
    public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
    }

	Array getInnerArray() {
		return array;
	}
	

    public Collection duplicate(boolean deepCopy) {
        try {
            return new XMLMultiElementStruct((Array) array.duplicate(deepCopy),getCaseSensitive());
        } catch (PageException e) {
            return null;
        }
    }

	/**
	 * @see org.w3c.dom.Node#cloneNode(boolean)
	 */
	public Node cloneNode(boolean deep) {
		try {
            return new XMLMultiElementStruct((Array) array.duplicate(deep),getCaseSensitive());
        } catch (PageException e) {
            return null;
        }
	}
}