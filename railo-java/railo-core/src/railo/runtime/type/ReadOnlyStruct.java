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
	
	@Override
	public Object remove(Collection.Key key) throws PageException {
		if(isReadOnly)throw new ExpressionException("can't remove key ["+key.getString()+"] from struct, struct is readonly");
		return super.remove (key);
	}
	
	@Override
	public Object removeEL(Collection.Key key) {
		if(isReadOnly)return null;
		return super.removeEL (key);
	}
	
	public void removeAll() {
		if(!isReadOnly)super.clear();
	}

	@Override
	public Object set(Collection.Key key, Object value) throws PageException {
		if(isReadOnly)throw new ExpressionException("can't set key ["+key.getString()+"] to struct, struct is readonly");
		return super.set (key,value);
	}
	
	@Override
	public Object setEL(Collection.Key key, Object value) {
		if(!isReadOnly)super.setEL (key,value);
		return value;
	}
	

	@Override
	public Collection duplicate(boolean deepCopy) {
		ReadOnlyStruct trg=new ReadOnlyStruct();
		trg.isReadOnly=isReadOnly;
		copy(this, trg, deepCopy);
		return trg;
	}
	

	@Override
	public void clear() {
		if(isReadOnly)throw new PageRuntimeException(new ExpressionException("can't clear struct, struct is readonly"));
		super.clear();
	}
}