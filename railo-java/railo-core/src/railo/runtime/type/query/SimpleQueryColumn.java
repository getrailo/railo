package railo.runtime.type.query;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Map.Entry;

import railo.commons.sql.SQLUtil;
import railo.runtime.PageContext;
import railo.runtime.db.CFTypes;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.query.caster.Cast;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.scope.Undefined;

public class SimpleQueryColumn implements QueryColumn {

	private static final long serialVersionUID = 288731277532671308L;

	private SimpleQuery qry;
	private Key key;
	private int type;
	private ResultSet res;
	private Cast cast;
	private int index;
	private Object[] data;

	public SimpleQueryColumn(SimpleQuery qry, ResultSet res, Collection.Key key, int type, int index) {
		this.qry=qry;
		this.res=res;
		this.key=key;
		this.index=index;
		
		try {
			switch(type){
			case Types.TIMESTAMP:
				cast=Cast.TIMESTAMP;
			break;
			case Types.TIME:
				cast=Cast.TIME;
			break;
			case Types.DATE:
				cast=Cast.DATE;
			break;
			case Types.CLOB:
				cast=Cast.CLOB;
			break;
			case Types.BLOB:
				cast=Cast.BLOB;
			break;
			case Types.BIT:
				cast=Cast.BIT;
			break;
			case Types.ARRAY:
				cast=Cast.ARRAY;
			break;
			case Types.BIGINT:
				cast=Cast.BIGINT;
			break;
			
			case CFTypes.OPAQUE:
				if(SQLUtil.isOracle(res.getStatement().getConnection()))
	        		cast=Cast.ORACLE_OPAQUE;
	        	else 
	        		cast=Cast.OTHER;
			break;
			default:
				cast=Cast.OTHER;
			break;
			}
		}
		catch (Exception e) {
			throw SimpleQuery.toRuntimeExc(e);
		}
	}

	public Object get(Key key, Object defaultValue) {
		int row=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(row==Integer.MIN_VALUE) {
			Object child=getChildElement(key,null);
	    	if(child!=null) return child;
            return defaultValue;
        }
	    return get(row,defaultValue);
	}
	
	public Object get(Key key) throws PageException {
		int row=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(row==Integer.MIN_VALUE) {
			Object child=getChildElement(key,null);
	    	if(child!=null) return child;
            throw new DatabaseException("key ["+key+"] not found",null,null,null);
        }
	    return get(row);
	}
	
	
	private Object getChildElement(Key key, Object defaultValue) {
    	PageContext pc = ThreadLocalPageContext.get();
		// column and query has same name
		if(key.equals(this.key)) {
    		return get(qry.getCurrentrow(pc.getId()),defaultValue);
    	}
    	// get it from undefined scope
		if(pc!=null){
			Undefined undefined = pc.undefinedScope();
			boolean old = undefined.setAllowImplicidQueryCall(false);
			Object sister = undefined.get(this.key,null);
			undefined.setAllowImplicidQueryCall(old);
			if(sister!=null){
				try {
					return pc.get(sister, key);
				} catch (PageException e) {
					return defaultValue;
				}
			}
		}
    	return defaultValue;
	}
	
	public int size() {
		throw SimpleQuery.notSupported();
	}

	
	public Key[] keys() {
		throw SimpleQuery.notSupported();
	}

	
	public Object remove(Key key) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public Object removeEL(Key key) {
		throw SimpleQuery.notSupported();
	}

	
	public void clear() {
		throw SimpleQuery.notSupported();
	}


	
	public Object get(String key) throws PageException {
		return get(KeyImpl.init(key));
	}
	
	public Object get(String key, Object defaultValue) {
		return get(KeyImpl.init(key),defaultValue);
	}

	public Object set(String key, Object value) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public Object set(Key key, Object value) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public Object setEL(String key, Object value) {
		throw SimpleQuery.notSupported();
	}

	
	public Object setEL(Key key, Object value) {
		throw SimpleQuery.notSupported();
	}

	
	public Collection duplicate(boolean deepCopy) {
		throw SimpleQuery.notSupported();
	}

	
	public boolean containsKey(String key) {
		throw SimpleQuery.notSupported();
	}

	
	public boolean containsKey(Key key) {
		throw SimpleQuery.notSupported();
	}

	
	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel,
			DumpProperties properties) {
		throw SimpleQuery.notSupported();
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		throw SimpleQuery.notSupported();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	throw SimpleQuery.notSupported();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this,keys());
	}

	
	public Iterator<Object> valueIterator() {
		throw SimpleQuery.notSupported();
	}

	
	public String castToString() throws PageException {
		// TODO Auto-generated method stub
		return Caster.toString(get(key));
	}

	
	public String castToString(String defaultValue) {
		return Caster.toString(get(key,defaultValue),defaultValue);
	}

	
	public boolean castToBooleanValue() throws PageException {
		return Caster.toBoolean(get(key));
	}

	
	public Boolean castToBoolean(Boolean defaultValue) {
		return Caster.toBoolean(get(key,defaultValue),defaultValue);
	}

	
	public double castToDoubleValue() throws PageException {
		return Caster.toDoubleValue(get(key));
	}

	
	public double castToDoubleValue(double defaultValue) {
		return Caster.toDoubleValue(get(key,defaultValue),defaultValue);
	}

	
	public DateTime castToDateTime() throws PageException {
		return Caster.toDate(get(key), false, null);
	}

	
	public DateTime castToDateTime(DateTime defaultValue) {
		return Caster.toDate(get(key,defaultValue), false, null, defaultValue);
	}

	
	public int compareTo(String str) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public int compareTo(boolean b) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public int compareTo(double d) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public int compareTo(DateTime dt) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public String getKeyAsString() {
		return key.getString();
	}

	
	public Key getKey() {
		return key;
	}

	
	public Object get(PageContext pc) throws PageException {
		return get(key);
	}

	
	public Object get(PageContext pc, Object defaultValue) {
		return get(key,defaultValue);
	}

	
	public Object set(PageContext pc, Object value) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public Object setEL(PageContext pc, Object value) {
		throw SimpleQuery.notSupported();
	}

	
	public Object remove(PageContext pc) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public Object removeEL(PageContext pc) {
		throw SimpleQuery.notSupported();
	}

	
	public Object touch(PageContext pc) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public Object touchEL(PageContext pc) {
		throw SimpleQuery.notSupported();
	}

	
	public Object getParent() {
		return qry;
	}

	
	public Object remove(int row) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public Object removeRow(int row) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public Object removeEL(int row) {
		throw SimpleQuery.notSupported();
	}

	@Override
	public synchronized Object get(int row) throws PageException {
		Object sv = getStoredValue(row);
		if(sv!=SimpleQuery.DEFAULT_VALUE) return sv;
		
		try {
			if(row!=res.getRow()) {
				res.absolute(row);
			}
			return _get(row);
		}
		catch (Throwable t) {
			throw Caster.toPageException(t);
		}
	}

	@Override
	public synchronized Object get(int row, Object defaultValue) {
		Object sv = getStoredValue(row);
		if(sv!=SimpleQuery.DEFAULT_VALUE) return sv;
		
		try {
			if(row!=res.getRow()) {
				res.absolute(row);
			}
			return _get(row);
		}
		catch (Throwable t) {
			return defaultValue;
		}
	}
	
	private synchronized Object getStoredValue(int row) {
		if(data==null) return SimpleQuery.DEFAULT_VALUE;
		return data[row-1];
	}
	
	private synchronized Object _get(int row) throws SQLException, IOException {
		if(data==null) {
			data=new Object[qry.getRecordcount()];
			for(int i=0;i<data.length;i++){
				data[i]=SimpleQuery.DEFAULT_VALUE;
			}
			
		}
		return data[row-1]=cast.toCFType(null, type, res, index);
	}

	
	public Object set(int row, Object value) throws PageException {
		throw SimpleQuery.notSupported();
	}

	
	public void add(Object value) {
		throw SimpleQuery.notSupported();
	}

	
	public Object setEL(int row, Object value) {
		throw SimpleQuery.notSupported();
	}

	
	public void addRow(int count) {
		throw SimpleQuery.notSupported();
	}

	
	public int getType() {
		return type;
	}

	
	public String getTypeAsString() {
		return QueryImpl.getColumTypeName(type);
	}

	
	public void cutRowsTo(int maxrows) {
		throw SimpleQuery.notSupported();

	}
	
	public Object clone() {
		throw SimpleQuery.notSupported();
	}


	public int getIndex() {
		return index;
	}
	
	@Override
	public java.util.Iterator<String> getIterator() {
    	return keysAsStringIterator();
    }
}
