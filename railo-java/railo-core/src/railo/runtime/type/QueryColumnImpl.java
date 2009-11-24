package railo.runtime.type;

import java.util.Date;
import java.util.Iterator;

import railo.commons.lang.SizeOf;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Duplicator;
import railo.runtime.op.Operator;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.scope.UndefinedImpl;
import railo.runtime.util.ArrayIterator;

/**
 * implementation of the query column
 */
public final class QueryColumnImpl implements QueryColumn,Sizeable,Objects {

	protected int type;
	private int size;
	protected Object[] data;
    private static final int CAPACITY=32;
    private QueryColumnUtil queryColumnUtil;
    
    protected boolean typeChecked=false;
    private QueryImpl query;
    private Collection.Key key;

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
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return size;
	}

	/**
	 * @see railo.runtime.type.Collection#keysAsString()
	 */
	public String[] keysAsString() {
	    String[] k=new String[size()];
        int len=k.length;
		for(int i=1;i<=len;i++) {
			k[i-1]=Caster.toString(i);
		}
		return k;
	}

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
        return remove(query.getCurrentrow());
	}

	public Object removeEL(PageContext pc) {
        return removeEL(query.getCurrentrow());
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
			UndefinedImpl undefined = ((UndefinedImpl)pc.undefinedScope());
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
			UndefinedImpl undefined = ((UndefinedImpl)pc.undefinedScope());
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
	    	if(size==0)throw new DatabaseException("invalid row number ["+row+"]","query is empty",null,null,null);
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
		return DumpUtil.toDumpData(get(query.getCurrentrow()), pageContext,maxlevel,dp);
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
        if(queryColumnUtil==null)
            queryColumnUtil=new QueryColumnUtil(this);
        return queryColumnUtil.reDefineType(value);
    }
    
    private synchronized void resetType() {
        //railo.print.ln("->reset");
        if(queryColumnUtil==null)
            queryColumnUtil=new QueryColumnUtil(this);
        queryColumnUtil.resetType();
    }
    
    private synchronized void reOrganizeType() {
        if(queryColumnUtil==null)
            queryColumnUtil=new QueryColumnUtil(this);
        queryColumnUtil.reOrganizeType();
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
     * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
     */
    public Object touch(PageContext pc) throws PageException {
        return touch(query.getCurrentrow());
    }

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
     * @see railo.runtime.type.ref.Reference#getParent()
     */
    public Object getParent() {
        return query;
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
		if(value==null) return defaultValue;
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
		if(value==null) return defaultValue;
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
		if(value==null) return defaultValue;
		return Caster.toDoubleValue(value,defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        return DateCaster.toDateAdvanced(get(query.getCurrentrow()),null);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
    	Object value = get(query.getCurrentrow(),null);
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
    
    public synchronized QueryColumnImpl cloneColumn(QueryImpl query, boolean deepCopy) {
        QueryColumnImpl clone=new QueryColumnImpl();

        clone.key=key;
        clone.query=query;
        clone.queryColumnUtil=queryColumnUtil;
        clone.size=size;
        clone.type=type;
        clone.key=key;
        
        clone.data=new Object[data.length];
        for(int i=0;i<data.length;i++) {
            clone.data[i]=deepCopy?Duplicator.duplicate(data[i],true):data[i];
        }
        return clone;   
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        try {
            return Caster.toString(get(query.getCurrentrow()));
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

	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator keyIterator() {
		return new KeyIterator(keys());
	}

	/**
	 * @see railo.runtime.type.Iteratorable#valueIterator()
	 */
	public Iterator valueIterator() {
		return new ArrayIterator(data,0,size);
	}
	
	/**
	 * @see railo.runtime.type.Objects#callWithNamedValues(railo.runtime.PageContext, railo.runtime.type.Collection.Key, railo.runtime.type.Struct)
	 */
	public Object callWithNamedValues(PageContext pc, Key methodName,Struct args) throws PageException {
		return pc.getFunctionWithNamedValues(get(query.getCurrentrow()), methodName, Caster.toFunctionValues(args));
	}

	/**
	 * @see railo.runtime.type.Objects#call(railo.runtime.PageContext, railo.runtime.type.Collection.Key, java.lang.Object[])
	 */
	public Object call(PageContext pc, Key methodName, Object[] arguments)throws PageException {
		return pc.getFunction(get(query.getCurrentrow()), methodName, arguments);
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

	
}