package railo.runtime.type.scope;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.List;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.util.StructSupport;

public class RequestImplOld extends StructSupport  implements Request {
	



	private boolean init;
	private HttpServletRequest req;
	private static int _id=0;
	private int id=0;
    
	
	public RequestImplOld(){
        id=++_id;
	}
	
    /**
     * @return Returns the id.
     */
    public int _getId() {
        return id;
    }
	
	/**
	 * @see railo.runtime.type.Scope#getType()
	 */
	public int getType() {
		return SCOPE_REQUEST;
	}

	/**
	 * @see railo.runtime.type.Scope#getTypeAsString()
	 */
	public String getTypeAsString() {
		return "request";
	}

	/**
	 * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		req = pc.getHttpServletRequest();
		//new RequestImplOld().initialize(pc);
		init=true;
	}

	/**
	 * @see railo.runtime.type.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return init;
	}

	/**
	 * @see railo.runtime.type.Scope#release()
	 */
	public void release() {
		req=null;
		init=false;
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		String[] keys = keysAsString();
		for(int i=0;i<keys.length;i++){
			req.removeAttribute(keys[i]);
		}
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return get(key,null)!=null;
	}


	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		//RequestImpl other = new RequestImpl();
		Struct sct=new StructImpl();
		StructImpl.copy(this,sct,deepCopy);
		return sct;
	}


	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		Object value= req.getAttribute(key.getLowerString());
		if(value!=null) return value;
		
		Key k = getKey(key);
		if(k!=null) value=req.getAttribute(k.getString());
		
		if(value!=null) return value;
		throw invalidKey(key);
	}


	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		Object value= req.getAttribute(key.getLowerString());
		if(value!=null) return value;
		
		key=getKey(key);
		if(key!=null) value=req.getAttribute(key.getString());
		
		return value!=null?value:defaultValue;
	}

	private synchronized Key getKey(Key key) {
		Enumeration names = req.getAttributeNames();
		Key k;
		while(names.hasMoreElements()){
			if(key.equals(k=KeyImpl.toKey(names.nextElement(),null)))
				return k;
		}
		return null;
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public synchronized Key[] keys() {
		
		Enumeration names = req.getAttributeNames();
		ArrayList list=new ArrayList();
		String k;
		while(names.hasMoreElements()){
			k=Caster.toString(names.nextElement(),null);
			if(!k.startsWith("javax.servlet."))
				list.add(KeyImpl.init(k));
		}
		return (Key[]) list.toArray(new Key[list.size()]);
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public synchronized String[] keysAsString() {
		Enumeration names = req.getAttributeNames();
		ArrayList list=new ArrayList();
		String k;
		while(names.hasMoreElements()){
			k=Caster.toString(names.nextElement(),null);
			if(!k.startsWith("javax.servlet."))
				list.add(k);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/**
	 * @see railo.runtime.type.Iteratorable#keyIterator()
	 */
	public Iterator keyIterator() {
		return new KeyIterator(keys());
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		Object value= removeEL(key);
		if(value!=null) return value;
		throw new ExpressionException("can't remove key ["+key+"] from struct, key doesn't exists");
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Key key) {
		Object value= req.getAttribute(key.getLowerString());
		if(value!=null) {
			req.removeAttribute(key.getLowerString());
			return value;
		}
		key=getKey(key);
		if(key!=null) {
			value= req.getAttribute(key.getString());
			if(value!=null) {
				req.removeAttribute(key.getString());
				return value;
			}
		}
		return null;
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		req.setAttribute(key.getLowerString(), value);
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		req.setAttribute(key.getLowerString(), value);
		return value;
	}

	/**
	 * @see java.util.Map#size()
	 */
	public int size() {
		int size=0;
		Enumeration names = req.getAttributeNames();
		String k;
		while(names.hasMoreElements()){
			k=Caster.toString(names.nextElement(),null);
			if(!k.startsWith("javax.servlet."))
				size++;
		}
		return size;
	}

	
	private ExpressionException invalidKey(Key key) {
		return new ExpressionException("key ["+key.getString()+"] doesn't exist in struct (keys:"+List.arrayToList(keysAsString(), ",")+")");
	}
	

	/**
	 * @see railo.runtime.type.util.StructSupport#toDumpData(railo.runtime.PageContext, int, railo.runtime.dump.DumpProperties)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel,DumpProperties properties) {
		return ScopeSupport.toDumpData(pageContext, maxlevel, properties, this, getTypeAsString());
	}

}
