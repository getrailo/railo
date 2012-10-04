package railo.runtime.type.scope;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.util.StructSupport;

public final class RequestImpl extends StructSupport implements Request {

	
	private HttpServletRequest _req;
	private boolean init;
	private static int _id=0;
	private int id=0;

	public RequestImpl() {
        id=++_id;
		//super("request",SCOPE_REQUEST,Struct.TYPE_REGULAR);
		
	}
	
    /**
     * @return Returns the id.
     */
    public int _getId() {
        return id;
    }

	public void initialize(PageContext pc) {
		_req = pc.getHttpServletRequest();//HTTPServletRequestWrap.pure(pc.getHttpServletRequest());
		init=true;
		
	}

	@Override
	public boolean isInitalized() {
		return init;
	}

	@Override
	public void release() {
		init = false;
	}

	@Override
	public void release(PageContext pc) {
		init = false;
	}

	@Override
	public int getType() {
		return SCOPE_REQUEST;
	}

	/**
	 * @see railo.runtime.type.scope.Scope#getTypeAsString()
	 */
	public String getTypeAsString() {
		return "request";
	}

	/**
	 * @see java.util.Map#size()
	 */
	public int size() {
		int size=0;
		synchronized (_req) {
			Enumeration<String> names = _req.getAttributeNames();
			while(names.hasMoreElements()){
				names.nextElement();
				size++;
			}
		}
		return size;
	}
	
	@Override
	public Iterator<Collection.Key> keyIterator() {
		return keyList().iterator();
	}
	
	

	private List<Key> keyList() {
		synchronized (_req) {
			Enumeration<String> names = _req.getAttributeNames();
			List<Key> list=new ArrayList<Key>();
			while(names.hasMoreElements()){
				list.add(KeyImpl.getInstance(names.nextElement()));
			}
			return list;
		}
	}
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this,keys());
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		synchronized (_req) {
			Enumeration<String> names = _req.getAttributeNames();
			List<Object> list=new ArrayList<Object>();
			while(names.hasMoreElements()){
				list.add(_req.getAttribute(names.nextElement()));
			}
			return list.iterator();
		}
	}
	
	
	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Key[] keys() {
		List<Key> list = keyList();
		return list.toArray(new Key[list.size()]);
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Key key) throws PageException {
		Object value = removeEL(key);
		if(value!=null)return value;
		throw new ExpressionException("can't remove key ["+key+"] from struct, key doesn't exist");
	}

	/**
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		synchronized (_req) {
			Enumeration<String> names = _req.getAttributeNames();
			while(names.hasMoreElements()){
				_req.removeAttribute(names.nextElement());
			}
		}
	}

	public Object get(Key key) throws PageException {
		Object value = get(key,null);
		if(value==null) throw invalidKey(key);
		return value;
	}
	


	public Object removeEL(Key key) {
		synchronized (_req) {
			Object value = _req.getAttribute(key.getLowerString()); 
			if(value!=null) {
				_req.removeAttribute(key.getLowerString());
				return value;
			}
			
			Enumeration<String> names = _req.getAttributeNames();
			String k;
			while(names.hasMoreElements()){
				k=names.nextElement();
				if(k.equalsIgnoreCase(key.getString())) {
					value= _req.getAttribute(k);
					_req.removeAttribute(k);
					return value;
				}
			}
			return value;
		}
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		synchronized (_req) {
			Object value = _req.getAttribute(key.getLowerString()); 
			if(value!=null) return value;
			
			Enumeration<String> names = _req.getAttributeNames();
			Collection.Key k;
			while(names.hasMoreElements()){
				k=Caster.toKey(names.nextElement(),null);
				if(key.equals(k)) return _req.getAttribute(k.getString());
			}
			return defaultValue;
		}
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
		synchronized (_req) {
			_req.setAttribute(key.getLowerString(), value);
		}
		return value;
	}

	/**
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Key key, Object value) throws PageException {
		return setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		Struct trg=new StructImpl();
		StructImpl.copy(this, trg,deepCopy);
		return trg;
	}

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
		return get(key,null)!=null;
	}
	
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return ScopeSupport.toDumpData(pageContext, maxlevel, dp, this, getTypeAsString());
	}

}
