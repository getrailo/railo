 package railo.runtime.type;

import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;

/**
 * a read only Struct if flag is set to readonly 
 */
public class ReadOnlyStruct extends StructImpl {
	

	private boolean isReadOnly=false;
	
	/**
	 * sets if scope is readonly or not
	 * @param isReadOnly
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly=isReadOnly;
	}
	
	/* *
	 * @see railo.runtime.type.StructImpl#remove(java.lang.String)
	 * /
	public Object remove (String key) throws PageException {
		if(isReadOnly)throw new ExpressionException("can't remove key ["+key+"] from struct, struct is readonly");
		return super.remove (KeyImpl.init(key));
	}*/

	/**
	 *
	 * @see railo.runtime.type.StructImpl#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		if(isReadOnly)throw new ExpressionException("can't remove key ["+key.getString()+"] from struct, struct is readonly");
		return super.remove (key);
	}
	
	/**
	 *
	 * @see railo.runtime.type.StructImpl#removeEL(java.lang.String)
	 */
	public Object removeEL(Collection.Key key) {
		if(isReadOnly)return null;
		return super.removeEL (key);
	}
	
	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void removeAll() {
		if(!isReadOnly)super.clear();
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		if(isReadOnly)throw new ExpressionException("can't set key ["+key.getString()+"] to struct, struct is readonly");
		return super.set (key,value);
	}
	
	/**
	 * @see railo.runtime.type.StructImpl#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		if(!isReadOnly)super.setEL (key,value);
		return value;
	}
	

	/**
	 * @see railo.runtime.type.StructImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		ReadOnlyStruct trg=new ReadOnlyStruct();
		trg.isReadOnly=isReadOnly;
		copy(this, trg, deepCopy);
		return trg;
	}
	

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		if(isReadOnly)throw new PageRuntimeException(new ExpressionException("can't clear struct, struct is readonly"));
		super.clear();
	}
}