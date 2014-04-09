package railo.runtime.type.query;

import java.util.Iterator;

import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.util.StructSupport;
import railo.runtime.type.util.StructUtil;

public class QueryStruct extends StructSupport {

	private QueryImpl qi=null;
	private Query qry;
	private int row;

	public QueryStruct(Query qry, int row) {
		this.qry=qry;
		this.row=row;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object remove(Key key) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object removeEL(Key key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setEL(Key key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return qry.keys().length;
	}

	@Override
	public Key[] keys() {
		return qry.keys();
	}

	@Override
	public Object get(Key key) throws PageException {
		return qry.getAt(key, row);
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		return qry.getAt(key, row,defaultValue);
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		return StructUtil.duplicate(this, deepCopy);
	}

	@Override
	public boolean containsKey(Key key) {
		return qry.containsKey(key);
	}

	@Override
	public Iterator<Key> keyIterator() {
		return qry.keyIterator();
	}

	@Override
	public Iterator<Object> valueIterator() {
		return null;//new QueryValueIterator(qry, row);
	}

	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return null;//new QueryEntryItrator(qry, row);
	}

}
