package railo.runtime.type;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.ss.formula.functions.T;

import railo.commons.lang.SizeOf;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.op.ThreadLocalDuplication;
import railo.runtime.op.date.DateCaster;
import railo.runtime.reflection.Reflector;
import railo.runtime.reflection.pairs.MethodInstance;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.scope.Undefined;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.util.ArrayIterator;

/**
 * implementation of the query column
 */
public class QueryColumnImpl implements QueryColumnPro,Sizeable,Objects {

    private static final int CAPACITY=32;
    
	protected int type;
	protected int size;
	protected Object[] data;
    
    protected boolean typeChecked=false;
    protected QueryImpl query;
    protected Collection.Key key;

	/**
	 * constructor with type
	 * @param query
	 * @param key 
	 * @param type
	 */
    public QueryColumnImpl(QueryImpl query, Collection.Key key, int type) {
		this.data=new Object[CAPACITY];
		this.type=type;
        this.key=key;
        this.query=query;
	}

	/**
	 * constructor with array
	 * @param query
	 * @param array
	 * @param type
	 */
    public QueryColumnImpl(QueryImpl query, Collection.Key key, Array array,int type) {
		data=array.toArray();
	    size=array.size();
		this.type=type;
		this.query=query;
		this.key=key;
	}

	/**
	 * @param query
	 * @param type type as (java.sql.Types.XYZ) int
	 * @param size 
	 */
    public QueryColumnImpl(QueryImpl query, Collection.Key key, int type, int size) {
		this.data=new Object[size];
		this.type=type;
		this.size=size;
		this.query=query;
		this.key=key;
	}
	

	/**
	 * Constructor of the class
	 * for internal usage only
	 */
	public QueryColumnImpl() {
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return size;
	}

	@Override
	public Collection.Key[] keys() {
		Collection.Key[] k=new Collection.Key[size()];
        int len=k.length;
		for(int i=1;i<=len;i++) {
			k[i-1]=KeyImpl.init(Caster.toString(i));
		}
		return k;
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		resetType();
		return set(Caster.toIntValue(key.getString()),"");
	}

	/**
	 * @see railo.runtime.type.QueryColumn#remove(int)
	 */
	public Object remove(int row) throws DatabaseException {
        // query.disconnectCache();
        resetType();
		return set(row,"");
	}
	

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
        // query.disconnectCache();
        resetType();
		return setEL(Caster.toIntValue(key.getString(),-1),"");
	}

	/**
	 * @see railo.runtime.type.QueryColumn#removeEL(int)
	 */
	public Object removeEL(int row) {
        // query.disconnectCache();
        resetType();
		return setEL(row,"");
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public synchronized void clear() {
        // query.disconnectCache();
        resetType();
		data=new Object[CAPACITY];
		size=0;
	}
	
	/**
	 * @see railo.runtime.type.ref.Reference#remove(PageContext pc)
	 */
	public Object remove(PageContext pc) throws PageException {
        return remove(query.getCurrentrow(pc.getId()));
	}

	public Object removeEL(PageContext pc) {
        return removeEL(query.getCurrentrow(pc.getId()));
	}
	
	/**
	 * @throws PageException
	 * @see railo.runtime.type.Collection#get(java.lang.String)
	 */
	public Object get(String key) throws PageException {
        int row=Caster.toIntValue(key,Integer.MIN_VALUE);
	    if(row==Integer.MIN_VALUE) {
            Object rtn = getChildElement(key, null);
            if(rtn!=null) return rtn;
	    	//QueryColumn cc=query.getColumn(key,null);
            //if(cc!=null) return cc;
            throw new DatabaseException("key ["+key+"] not found",null,null,null);
        }
	    return get(row);
	}

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws PageException {
		int row=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
		if(row==Integer.MIN_VALUE) {
			Object child=getChildElement(key,null);
	    	if(child!=null) return child;
            throw new DatabaseException("key ["+key+"] not found",null,null,null);
        }
	    return get(row);
	}
	
	
	

    private Object getChildElement(Key key, Object defaultValue) {
    	// column and query has same name
    	if(key.equals(this.key)) {
        	return query.get(key,defaultValue);
    	}
    	// get it from undefined scope
		PageContext pc = ThreadLocalPageContext.get();
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
    
    private Object getChildElement(String key, Object defaultValue) {
    	// column and query has same name
    	if(key.equalsIgnoreCase(this.key.getString())) {
        	return query.get(key,defaultValue);
    	}
    	// get it from undefined scope
		PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null){
			Undefined undefined = pc.undefinedScope();
			boolean old = undefined.setAllowImplicidQueryCall(false);
			Object sister = undefined.get(this.key,null);
			undefined.setAllowImplicidQueryCall(old);
			if(sister!=null)return pc.get(sister, key,defaultValue);
		}
    	return defaultValue;
	}

	/**
     * @see railo.runtime.type.ContextCollection#get(railo.runtime.PageContext, java.lang.String)
     */
    public Object get(PageContext pc, String key) throws PageException {
        int row=Caster.toIntValue(key,Integer.MIN_VALUE);
        if(row==Integer.MIN_VALUE) {
            Object rtn=getChildElement(key, null);
            //Object rtn=pc.get(get(query.getCurrentrow(),null),key,null);
            //if(rtn!=null) return rtn;
            //rtn= query.get(key,null);
            if(rtn!=null) return rtn;
            throw new ExpressionException("query column has no key with name ["+key+"]");
        }
        return get(row);
    }

    /**
     * @see railo.runtime.type.QueryColumn#get(int)
     */
    public Object get(int row){
        if(row<1 || row>size) return "";
        Object o=data[row-1];
        return o==null?"":o;
    }

    /**
     * touch the given line on the column at given row
     * @param row
     * @return new row or existing
     * @throws DatabaseException
     */
    public Object touch(int row) throws DatabaseException{
        // query.disconnectCache();
        if(row<1 || row>size) return "";
        Object o=data[row-1];
        if(o!=null) return o;
        return set(row,new StructImpl());
    }
    
    /**
     * touch the given line on the column at given row
     * @param row
     * @return new row or existing
     * @throws DatabaseException
     */
    public Object touchEL(int row) {
        // query.disconnectCache();
        if(row<1 || row>size) return "";
        Object o=data[row-1];
        if(o!=null) return o;
        return setEL(row,new StructImpl());
    }

	/**
	 * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
	    int row=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
	    if(row==Integer.MIN_VALUE) {
	    	return getChildElement(key, defaultValue);
	    	//Object rtn= query.getColumn(key,null);
	    	//if(rtn!=null)return rtn;
	    	//return defaultValue;
	    }
	    return get(row,defaultValue);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#get(java.lang.String, java.lang.Object)
	 */
	public Object get(String key, Object defaultValue) {
	    int row=Caster.toIntValue(key,Integer.MIN_VALUE);
	    if(row==Integer.MIN_VALUE) {
	    	return getChildElement(key, defaultValue);
	    	//Object rtn= query.getColumn(key,null);
	    	//if(rtn!=null)return rtn;
	    	//return defaultValue;
	    }
	    return get(row,defaultValue);
	}

    /**
     * @param pc
     * @param key
     * @param defaultValue
     * @return
     */
    public Object get(PageContext pc, String key, Object defaultValue) {
        int row=Caster.toIntValue(key,Integer.MIN_VALUE);
        if(row==Integer.MIN_VALUE) {
            return getChildElement(key, defaultValue);
        }
        return get(row,defaultValue);
    }

	/**
	 * @see railo.runtime.type.QueryColumn#get(int, java.lang.Object)
	 */
	public Object get(int row, Object defaultValue) {
	    if(row<1 || row>size) return defaultValue;
	    Object o=data[row-1];
	    return o==null?defaultValue:o;
	}

	/**
	 * @see railo.runtime.type.Collection#set(java.lang.String, java.lang.Object)
	 */
	public Object set(String key, Object value) throws PageException {
	    int row=Caster.toIntValue(key,Integer.MIN_VALUE);
	    if(row==Integer.MIN_VALUE)return query.set(key,value);
	    return set(row,value);
	}

	public Object set(Collection.Key key, Object value) throws PageException {
	    int row=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
	    if(row==Integer.MIN_VALUE)return query.set(key,value);
	    return set(row,value);
	}

    /**
	 * @see railo.runtime.type.QueryColumn#set(int, java.lang.Object)
	 */
	public synchronized Object set(int row, Object value) throws DatabaseException {
        // query.disconnectCache();
        if(row<1) throw new DatabaseException("invalid row number ["+row+"]","valid row numbers a greater or equal to one",null,null,null);
	    if(row>size) {
	    	if(size==0)throw new DatabaseException("cannot set a value to a empty query, you first have to add a row",null,null,null,null);
	    	throw new DatabaseException("invalid row number ["+row+"]","valid row numbers goes from 1 to "+size,null,null,null);
	    }
	    
	    value=reDefineType(value);
	    data[row-1]=value;
	    return value;
	}
	/**
	 * @see railo.runtime.type.Collection#setEL(java.lang.String, java.lang.Object)
	 */
	public synchronized Object setEL(String key, Object value) {
	    int index=Caster.toIntValue(key,Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) query.setEL(key,value);
	    return setEL(index, value);
		
	}

	public Object setEL(Collection.Key key, Object value) {
	    int index=Caster.toIntValue(key.getString(),Integer.MIN_VALUE);
		if(index==Integer.MIN_VALUE) query.setEL(key,value);
	    return setEL(index, value);
	}

	/**
	 * @see railo.runtime.type.QueryColumn#setEL(int, java.lang.Object)
	 */
	public synchronized Object setEL(int row, Object value) {
        // query.disconnectCache();
        if(row<1 || row>size) return value;
	    
	    value=reDefineType(value);
	    data[row-1]=value;
	    return value;
	}

    /**
	 * @see railo.runtime.type.QueryColumn#add(java.lang.Object)
	 */
	public synchronized void add(Object value) {
        // query.disconnectCache();
        if(data.length<=size) growTo(size);
	    data[size++]=value;
	}

    /**
     * @see railo.runtime.type.QueryColumn#cutRowsTo(int)
     */
    public synchronized void cutRowsTo(int maxrows) {
        // query.disconnectCache();
        if(maxrows>-1 && maxrows<size)size=maxrows;
    }

	/**
	 * @see railo.runtime.type.QueryColumn#addRow(int)
	 */
	public synchronized void addRow(int count) {	    
        // query.disconnectCache();
        if(data.length<(size+count)) growTo(size+count);
	    for(int i=0;i<count;i++)size++;
	}

    public synchronized Object removeRow(int row) throws DatabaseException {
        // query.disconnectCache();
        if(row<1 || row>size) 
            throw new DatabaseException("invalid row number ["+row+"]","valid rows goes from 1 to "+size,null,null,null);
        Object o=data[row-1];
        for(int i=row;i<size;i++) {
            data[i-1]=data[i];
        }
        size--;
        
        return o==null?"":o;
    }

	/**
	 * @param reorganize 
	 * @see railo.runtime.type.QueryColumn#getType()
	 */
	public int getType() {
	    reOrganizeType();
		return type;
	}
	
	
    /**
	 * @see railo.runtime.type.QueryColumn#getTypeAsString()
	 */
	public String getTypeAsString() {
		return QueryImpl.getColumTypeName(getType());
	}


	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return DumpUtil.toDumpData(get(query.getCurrentrow(pageContext.getId())), pageContext,maxlevel,dp);
	}	
	
    private synchronized void growTo(int row) {
        
        int newSize=(data.length+1)*2;
        while(newSize<=row) {
            //print.ln(newSize+"<="+row);
            newSize*=2;
        }
        
        Object[] newData=new Object[newSize];
        for(int i=0;i<data.length;i++) {
            newData[i]=data[i];
        }
        data=newData;
    }
    
    private Object reDefineType(Object value) {
        return QueryColumnUtil.reDefineType(this,value);
    }
    
    private synchronized void resetType() {
        QueryColumnUtil.resetType(this);
    }
    
    private synchronized void reOrganizeType() {
        QueryColumnUtil.reOrganizeType(this);
    }

    public Collection.Key getKey() {
        return key;
    }
    public void setKey(Collection.Key key) {
        this.key = key;
    }

	/**
	 * @see railo.runtime.type.ref.Reference#getKeyAsString()
	 */
	public String getKeyAsString() throws PageException {
		return key.getLowerString();// TODO ist das OK?
	}

    /**
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext)
     */
    public Object get(PageContext pc) {
        return get(query.getCurrentrow(pc.getId()));
    }
    
    /**
     *
     * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext, java.lang.Object)
     */
    public Object get(PageContext pc, Object defaultValue) {
        return get(query.getCurrentrow(pc.getId()),defaultValue);
    }

    /**
     * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
     */
    public Object touch(PageContext pc) throws PageException {
        return touch(query.getCurrentrow(pc.getId()));
    }

    public Object touchEL(PageContext pc) {
        return touchEL(query.getCurrentrow(pc.getId()));
    }

    /**
     * @see railo.runtime.type.ref.Reference#set(railo.runtime.PageContext, java.lang.Object)
     */
    public Object set(PageContext pc, Object value) throws PageException {
        return set(query.getCurrentrow(pc.getId()),value);
    }

    /**
     * @see railo.runtime.type.ref.Reference#setEL(railo.runtime.PageContext, java.lang.Object)
     */
    public Object setEL(PageContext pc, Object value) {
        return setEL(query.getCurrentrow(pc.getId()),value);
    }

    /**
     * @see railo.runtime.type.ref.Reference#getParent()
     */
    public Object getParent() {
        return query;
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        return Caster.toString(get(query.getCurrentrow(ThreadLocalPageContext.get().getId())));
    }

	/**
	 * @see railo.runtime.op.Castable#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		Object value = get(query.getCurrentrow(ThreadLocalPageContext.get().getId()),null);
		if(value==null) return defaultValue;
		return Caster.toString(value,defaultValue);
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        return Caster.toBooleanValue(get(query.getCurrentrow(ThreadLocalPageContext.get().getId())));
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
    	Object value = get(query.getCurrentrow(ThreadLocalPageContext.get().getId()),null);
		if(value==null) return defaultValue;
		return Caster.toBoolean(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
        return Caster.toDoubleValue(get(query.getCurrentrow(ThreadLocalPageContext.get().getId())));
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
    	Object value = get(query.getCurrentrow(ThreadLocalPageContext.get().getId()),null);
		if(value==null) return defaultValue;
		return Caster.toDoubleValue(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        return DateCaster.toDateAdvanced(get(query.getCurrentrow(ThreadLocalPageContext.get().getId())),null);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
    	Object value = get(query.getCurrentrow(ThreadLocalPageContext.get().getId()),null);
		if(value==null) return defaultValue;
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
     * @see railo.runtime.type.Collection#clone()
     */
    public synchronized Object clone() {
        return duplicate(true);
    }

    public synchronized Collection duplicate(boolean deepCopy) {
        return cloneColumn(query,deepCopy);
    }
    
    public synchronized QueryColumnPro cloneColumn(QueryImpl query, boolean deepCopy) {
        QueryColumnImpl clone=new QueryColumnImpl();
        populate(this, clone, deepCopy);
        return clone;
    }
    
    protected static void populate(QueryColumnImpl src,QueryColumnImpl trg, boolean deepCopy) {
        
        ThreadLocalDuplication.set(src, trg);
        try{
	        trg.key=src.key;
	        trg.query=src.query;
	        trg.size=src.size;
	        trg.type=src.type;
	        trg.key=src.key;
	        
	        trg.data=new Object[src.data.length];
	        for(int i=0;i<src.data.length;i++) {
	            trg.data[i]=deepCopy?Duplicator.duplicate(src.data[i],true):src.data[i];
	        }
        }
        finally {
        	ThreadLocalDuplication.remove(src);
        }
    }
	

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        try {
            return Caster.toString(get(query.getCurrentrow(ThreadLocalPageContext.get().getId())));
        } catch (PageException e) {
            return super.toString();
        }
    }

    /**
     * @see railo.runtime.type.Collection#containsKey(java.lang.String)
     */
    public boolean containsKey(String key) {
        return get(key,null)!=null;
    }

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
	}

	/**
	 * @see railo.runtime.type.Sizeable#sizeOf()
	 */
	public long sizeOf() {
		return SizeOf.size(key)+SizeOf.size(data);
	}
	
	/**
	 *
	 * @see railo.runtime.type.Iteratorable#iterator()
	 */
	public Iterator iterator() {
		return keyIterator();
	}

	@Override
	public Iterator<Collection.Key> keyIterator() {
		return new KeyIterator(keys());
	}
    
	@Override
	public Iterator<String> keysAsStringIterator() {
    	return new StringIterator(keys());
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return new EntryIterator(this, keys());
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator<Object> valueIterator() {
		return new ArrayIterator(data,0,size);
	}
	
	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Key methodName,Struct args) throws PageException {
		
        throw new ExpressionException("No matching Method/Function ["+methodName+"] for call with named arguments found");
		//return pc.getFunctionWithNamedValues(get(query.getCurrentrow()), methodName, Caster.toFunctionValues(args));
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		MethodInstance mi = Reflector.getMethodInstanceEL(this.getClass(), methodName, arguments);
		if(mi!=null) {
			try {
				return mi.invoke(this);
			} catch (Throwable t) {
				try {
					return pc.getFunction(get(query.getCurrentrow(pc.getId())), methodName, arguments);
				} catch (PageException pe) {
					throw Caster.toPageException(t);
				}
			}
		}
		return pc.getFunction(get(query.getCurrentrow(pc.getId())), methodName, arguments);
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, java.lang.String, java.lang.Object[])
	 */
	public Object call(PageContext pc, String methodName, Object[] arguments)throws PageException {
		return call(pc, KeyImpl.init(methodName), arguments);
	}

	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, java.lang.String, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, String methodName,Struct args) throws PageException {
		return callWithNamedValues(pc, KeyImpl.init(methodName), args);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key,defaultValue);
	}

	/**
	 * @see railo.runtime.type.Objects#get(railo.runtime.PageContext, railo.runtime.type.Collection.Key)
	 */
	public Object get(PageContext pc, Key key) throws PageException {
		return get(key);
	}

	/**
	 * @see railo.runtime.type.Objects#isInitalized()
	 */
	public boolean isInitalized() {
		return true;
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object set(PageContext pc, String propertyName, Object value)throws PageException {
		return set(propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#set(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return set(propertyName, value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, java.lang.String, java.lang.Object)
	 */
	public Object setEL(PageContext pc, String propertyName, Object value) {
		return setEL(key, value);
	}

	/**
	 * @see railo.runtime.type.Objects#setEL(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return setEL(propertyName, value);
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element) {
		throwNotAllowedToAlter();
		//setEL(index+1, element);
	}

	private void throwNotAllowedToAlter() {
		throw new PageRuntimeException(new DatabaseException(
				"Query columns do not support methods that would alter the structure of a query column" 
				,"you must use an analogous method on the query"
				,null
				,null
				,null));
		
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(java.util.Collection<? extends Object> c) {
		throwNotAllowedToAlter();
		return false;
		/*Iterator<? extends Object> it = c.iterator();
		while(it.hasNext()){
			add(it.next());
		}
		return true;*/
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, java.util.Collection<? extends Object> c) {
		throwNotAllowedToAlter();
		return false;
		/*Iterator<? extends Object> it = c.iterator();
		while(it.hasNext()){
			setEL(++index,it.next());
		}
		return true;*/
	}

	/**
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return indexOf(o)!=-1;
	}

	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(java.util.Collection<?> c) {
		Iterator<? extends Object> it = c.iterator();
		while(it.hasNext()){
			if(indexOf(it.next())==-1) return false;
		}
		return true;
	}

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		for(int i=0;i<size;i++){
			try {
				if(Operator.compare(o, data[i])==0) return i;
			} 
			catch (PageException e) {}
		}
		return -1;
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		for(int i=size-1;i>=0;i--){
			try {
				if(Operator.compare(o, data[i])==0) return i;
			} 
			catch (PageException e) {}
		}
		return -1;
	}

	/**
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return size()==0;
	}

	/*public ListIterator<Object> listIterator() {
		return null;
	}

	public ListIterator<Object> listIterator(int index) {
		return null;
	}*/

	/**
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(java.util.Collection<?> c) {
		throwNotAllowedToAlter();
		return false;
		/*boolean hasChanged=false;
		Iterator<? extends Object> it = c.iterator();
		while(it.hasNext()){
			if(remove(it.next())) {
				hasChanged=true;
			}
		}
		return hasChanged;*/
	}

	/**
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(java.util.Collection<?> c) {
		throwNotAllowedToAlter();
		return false;
		/*boolean hasChanged=false;
		Iterator it = valueIterator();
		while(it.hasNext()){
			if(!c.contains(it.next())){
				hasChanged=true;
				it.remove();
			}
		}
		return hasChanged;*/
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List<Object> subList(int fromIndex, int toIndex) {
		ArrayList<Object> list=new ArrayList<Object>();
		for(int i=fromIndex;i<toIndex;i++){
			list.add(data[i]);
		}
		return list;
	}

	/**
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return toArray(new Object[size()]);
	}

	/**
	 * @see java.util.List#toArray(T[])
	 */
	public  Object[] toArray(Object[] trg) {
		System.arraycopy(data, 0, trg, 0, data.length>trg.length?trg.length:data.length);
		return trg;
	}


	public boolean equals(Object obj){
		if(!(obj instanceof Collection)) return false;
		return CollectionUtil.equals(this,(Collection)obj);
	}

	@Override
	public QueryColumnPro toDebugColumn() {
		return new DebugQueryColumn(data,key,query,size,type,typeChecked);
	}
}