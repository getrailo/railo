package railo.runtime.type;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.util.QueryUtil;

/**
 * Recordcount Query Column
 */
public final class QueryColumnRef implements QueryColumn,Sizeable {
    
    private Query query;
    private Collection.Key columnName;
    private int type;

    /**
     * Constructor of the class
     * @param query
     * @param columnName 
     * @param type 
     */
    public QueryColumnRef(Query query, Collection.Key columnName, int type) {
        this.query=query;
        this.columnName=columnName;
        this.type=type;
    }

    /**
     * @see railo.runtime.type.QueryColumn#remove(int)
     */
    public Object remove(int row) throws DatabaseException {
        throw new DatabaseException("can't remove "+columnName+" at row "+row+" value from Query",null,null,null);
    }

    /**
     * @see railo.runtime.type.QueryColumn#removeEL(int)
     */
    public Object removeEL(int row) {
        return query.getAt(columnName,row,null);
    }

    /**
     * @see railo.runtime.type.QueryColumn#get(int)
     */
    public Object get(int row) throws PageException {
        return query.getAt(columnName,row);
    }
    
    /**
     * touch a value, means if key dosent exist, it will created
     * @param row
     * @return matching value or created value
     * @throws PageException
     */
    public Object touch(int row) throws PageException {
        Object o= query.getAt(columnName,row,null);
        if(o!=null) return o;
        return query.setAt(columnName,row,new StructImpl());
    }
    
    public Object touchEL(int row) {
        Object o= query.getAt(columnName,row,null);
        if(o!=null) return o;
        return query.setAtEL(columnName,row,new StructImpl());
    }

    /**
     * @see railo.runtime.type.QueryColumn#get(int, java.lang.Object)
     */
    public Object get(int row, Object defaultValue) {
        return query.getAt(columnName,row,defaultValue);
    }

    /**
     * @see railo.runtime.type.QueryColumn#set(int, java.lang.Object)
     */
    public Object set(int row, Object value) throws DatabaseException {
        throw new DatabaseException("can't change "+columnName+" value from Query",null,null,null);
    }

    /**
     * @see railo.runtime.type.QueryColumn#setEL(int, java.lang.Object)
     */
    public Object setEL(int row, Object value) {
        return query.getAt(columnName,row,null);
    }

    /**
     * @see railo.runtime.type.QueryColumn#add(java.lang.Object)
     */
    public void add(Object value) {}

    /**
     * @see railo.runtime.type.QueryColumn#addRow(int)
     */
    public void addRow(int count) {}

    /**
     * @see railo.runtime.type.QueryColumn#getType()
     */
    public int getType() {
        return type;
    }

    /**
     * @see railo.runtime.type.QueryColumn#getTypeAsString()
     */
    public String getTypeAsString() {
        return QueryImpl.getColumTypeName(getType());
    }

    /**
     * @see railo.runtime.type.QueryColumn#cutRowsTo(int)
     */
    public void cutRowsTo(int maxrows) {}

    /**
     *
     * @see railo.runtime.type.ContextCollection#get(railo.runtime.PageContext, java.lang.String, java.lang.Object)
     */
    public Object get(PageContext pc, String key, Object defaultValue) {
        return get(key,defaultValue);
    }

    /**
     * @throws PageException 
     * @see railo.runtime.type.ContextCollection#get(railo.runtime.PageContext, java.lang.String)
     */
    public Object get(PageContext pc, String key) throws PageException {
        return get(key);
    }

    /**
     * @see railo.runtime.type.Collection#size()
     */
    public int size() {
        return query.size();
    }

    /**
     * @see railo.runtime.type.Collection#keysAsString()
     */
    public String[] keysAsString() {
        String[] k=new String[size()];
        for(int i=1;i<=k.length;i++) {
            k[i-1]=Caster.toString(i);
        }
        return k;
    }
    
    public Collection.Key[] keys() {
    	Collection.Key[] k=new Collection.Key[size()];
        for(int i=1;i<=k.length;i++) {
            k[i-1]=KeyImpl.init(Caster.toString(i));
        }
        return k;
    }

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		throw new DatabaseException("can't remove "+key+" from Query",null,null,null);
	}

	/**
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		return get(key,null);
	}

    /**
     * @see railo.runtime.type.Collection#clear()
     */
    public void clear() {}

    /**
     * @see railo.runtime.type.Collection#get(java.lang.String)
     */
    public Object get(String key) throws PageException {
        return get(Caster.toIntValue(key));
    }

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		return get(Caster.toIntValue(key.getString()));
	}

    /**
     *
     * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
     */
    public Object get(String key, Object defaultValue) {
        return get(Caster.toIntValue(key,query.getCurrentrow()),defaultValue);
    }

	/**
	 *
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		return get(Caster.toIntValue(key,query.getCurrentrow()),defaultValue);
	}

    /**
     * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
     */
    public Object set(String key, Object value) throws PageException {
        return set(Caster.toIntValue(key),value);
    }

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		return set(Caster.toIntValue(key),value);
	}

    /**
     * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
     */
    public Object setEL(String key, Object value) {
        return setEL(Caster.toIntValue(key,query.getCurrentrow()),value);
    }

	/**
	 *
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		return setEL(Caster.toIntValue(key,query.getCurrentrow()),value);
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
        return new KeyIterator(keys());
    }
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keysAsString());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this, keys());
	}
    
	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return query.getColumn(columnName,null).valueIterator();
	}
    

	/**
	 *
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return keyIterator();
	}

    /**
     * @see railo.runtime.type.Collection#containsKey(java.lang.String)
     */
    public boolean containsKey(String key) {
        return get(key,null)!=null;
    }

	/**
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
		return get(key,null)!=null;
	}

    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    return DumpUtil.toDumpData(get(query.getCurrentrow(),null), pageContext,maxlevel,dp);
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        return Caster.toString(get(query.getCurrentrow()));
    }

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		Object value = get(query.getCurrentrow(),null);
		if(value==null)return defaultValue;
		return Caster.toString(value,defaultValue);
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        return Caster.toBooleanValue(get(query.getCurrentrow()));
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
    	Object value = get(query.getCurrentrow(),null);
		if(value==null)return defaultValue;
		return Caster.toBoolean(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
        return Caster.toDoubleValue(get(query.getCurrentrow()));
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
    	Object value = get(query.getCurrentrow(),null);
		if(value==null)return defaultValue;
		return Caster.toDoubleValue(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        return Caster.toDate(get(query.getCurrentrow()),null);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
    	Object value = get(query.getCurrentrow(),null);
		if(value==null)return defaultValue;
		return DateCaster.toDateAdvanced(value,true,null,defaultValue);
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return Operator.compare(castToBooleanValue(), b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((Date)castToDateTime(), (Date)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return Operator.compare(castToString(), str);
	}

    /**
     * @see railo.runtime.type.ref.Reference#getKeyAsString()
     */
    public String getKeyAsString() throws PageException {
        return columnName.toString();
    }

    /**
     * @see railo.runtime.type.ref.Reference#getKey()
     */
    public Collection.Key getKey() throws PageException {
        return columnName;
    }

    /**
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext)
     */
    public Object get(PageContext pc) throws PageException {
        return get(query.getCurrentrow());
    }
    
    /**
     *
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext, java.lang.Object)
     */
    public Object get(PageContext pc, Object defaultValue) {
        return get(query.getCurrentrow(),defaultValue);
    }

    /**
     *
     * @see railo.runtime.type.QueryColumn#removeRow(int)
     */
    public Object removeRow(int row) throws DatabaseException {
        throw new DatabaseException("can't remove row from Query",null,null,null);
    }

    /**
     * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
     */
    public Object touch(PageContext pc) throws PageException {
        return touch(query.getCurrentrow());
    }

    /**
     * @see railo.runtime.type.ref.Reference#touchEL(railo.runtime.PageContext)
     */
    public Object touchEL(PageContext pc) {
        return touchEL(query.getCurrentrow());
    }

    /**
     * @see railo.runtime.type.ref.Reference#set(railo.runtime.PageContext, java.lang.Object)
     */
    public Object set(PageContext pc, Object value) throws PageException {
        return set(query.getCurrentrow(),value);
    }
    
    /**
     * @see railo.runtime.type.ref.Reference#setEL(railo.runtime.PageContext, java.lang.Object)
     */
    public Object setEL(PageContext pc, Object value) {
        return setEL(query.getCurrentrow(),value);
    }

    /**
     * @see railo.runtime.type.ref.Reference#remove(railo.runtime.PageContext)
     */
    public Object remove(PageContext pc) throws PageException {
        return remove(query.getCurrentrow());
    }

    /**
     * @see railo.runtime.type.ref.Reference#removeEL(railo.runtime.PageContext)
     */
    public Object removeEL(PageContext pc) {
        return removeEL(query.getCurrentrow());
    }

    /**
     * @see railo.runtime.type.ref.Reference#getParent()
     */
    public Object getParent() {
        return query;
    }

    /**
     * @see railo.runtime.type.Collection#clone()
     */
    public Object clone() {
        QueryColumn clone=new QueryColumnRef(query,columnName,type);
        return clone;
    }

    /**
     * @see railo.runtime.type.Collection#duplicate(boolean)
     */
    public Collection duplicate(boolean deepCopy) {
//		 MUST muss deepCopy checken
        QueryColumn clone=new QueryColumnRef(query,columnName,type);
        return clone;
    }
	

	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return QueryUtil.sizeOf(this);
	}

}