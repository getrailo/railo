package railo.runtime.type.ref;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;

public class SimpleVarRef implements Reference {

	//private PageContextImpl pc;

	public SimpleVarRef(PageContextImpl pc, String key) {
		//this.pc=pc;
	}
	
	@Override
	public Object get(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(PageContext pc, Object defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection.Key getKey() throws PageException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getKeyAsString() throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object remove(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object removeEL(PageContext pc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object set(PageContext pc, Object value) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setEL(PageContext pc, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object touch(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object touchEL(PageContext pc) {
		// TODO Auto-generated method stub
		return null;
	}

}
