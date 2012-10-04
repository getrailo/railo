package railo.runtime.type.scope;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.util.CollectionUtil;
import railo.runtime.type.util.MemberUtil;
import railo.runtime.type.wrap.ArrayAsList;

/**
 * implementation of the argument scope 
 */
public final class ArgumentImpl extends ScopeSupport implements Argument {
		
	private boolean bind;
	private Set functionArgumentNames;
	//private boolean supportFunctionArguments; 
	
	/**
	 * constructor of the class
	 */
	public ArgumentImpl() {
		super("arguments",SCOPE_ARGUMENTS,Struct.TYPE_LINKED);
		//this(true);
	}


	/**
	 * @see railo.runtime.type.scope.ScopeSupport#release()
	 */
	public void release() {
		functionArgumentNames=null;
		super.release();
	}
	
	@Override
	public void release(PageContext pc) {
		functionArgumentNames=null;
		super.release(pc);
	}
	
	
	
     /**
     * @see railo.runtime.type.scope.Argument#setBind(boolean)
     */ 
    public void setBind(boolean bind) { 
            this.bind=bind; 
    }       
    
    /**
     * @see railo.runtime.type.scope.Argument#isBind()
     */ 
    public boolean isBind() { 
        return this.bind; 
    } 
    
    public Object getFunctionArgument(String key, Object defaultValue) {
		return getFunctionArgument(KeyImpl.getInstance(key), defaultValue);
	}

	public Object getFunctionArgument(Collection.Key key, Object defaultValue) {
		if((functionArgumentNames==null || !functionArgumentNames.contains(key))){
			return defaultValue;
		}
		return get(key, defaultValue);
	}
	

	/**
	 * @see railo.runtime.type.scope.ArgumentPro#containsFunctionArgumentKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsFunctionArgumentKey(Key key) {
		return functionArgumentNames!=null && functionArgumentNames.contains(key);
	}
	
	
	
	/**
	 *
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Collection.Key key, Object defaultValue) {
		Object o=super.get(key,null);
		if(o!=null)return o;
		
		o=get(Caster.toIntValue(key.getString(),-1),null);
		if(o!=null)return o;
		if(super.containsKey(key)) return null;// that is only for compatibility to neo
		return defaultValue;
	}


	/**
	 *
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Collection.Key key) throws ExpressionException {
		Object o=super.get(key,null);
		if(o!=null)return o;

		o=get(Caster.toIntValue(key.getString(),-1),null);
		if(o!=null)return o;
		if(super.containsKey(key)) return null;// that is only for compatibility to neo
		throw new ExpressionException("key ["+key.getString()+"] doesn't exist in argument scope. existing keys are ["+
				railo.runtime.type.List.arrayToList(CollectionUtil.keys(this),", ")
				+"]");
		
	}

	/**
	 *
	 * @see railo.runtime.type.Array#get(int, java.lang.Object)
	 */
	public Object get(int intKey, Object defaultValue) {
		Iterator it = valueIterator();//keyIterator();//getMap().keySet().iterator();
		int count=0;
		Object o;
		while(it.hasNext()) {
			o=it.next();
			if((++count)==intKey) {
				return o;//super.get(o.toString(),defaultValue);
			}
		}
		return defaultValue;
	}

	/**
	 * return a value matching to key
	 * @param intKey
	 * @return value matching key
	 * @throws PageException
	 */
	public Object getE(int intKey) throws PageException {
		Iterator it = valueIterator();//getMap().keySet().iterator();
		int count=0;
		Object o;
		while(it.hasNext()) {
			o=it.next();
			if((++count)==intKey) {
				return o;//super.get(o.toString());
			}
		}
		throw new ExpressionException("invalid index ["+intKey+"] for argument scope");	
	}
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		DumpTable htmlBox = new DumpTable("struct","#9999ff","#ccccff","#000000");
		htmlBox.setTitle("Scope Arguments");
		if(size()>10 && dp.getMetainfo())htmlBox.setComment("Entries:"+size());
	    
		maxlevel--;
		//Map mapx=getMap();
		Iterator it=keyIterator();//mapx.keySet().iterator();
		int count=0;
		Collection.Key key;
		int maxkeys=dp.getMaxKeys();
		int index=0;
		while(it.hasNext()) {
			key=KeyImpl.toKey(it.next(), null);//it.next();
			
			if(DumpUtil.keyValid(dp, maxlevel,key)){
				if(maxkeys<=index++)break;
				htmlBox.appendRow(3,
						new SimpleDumpData(key.getString()),
						new SimpleDumpData(++count),
						DumpUtil.toDumpData(get(key,null), 
						pageContext,maxlevel,dp));
			}
		}
		return htmlBox;
	}


	/**
	 * @see railo.runtime.type.Array#getDimension()
	 */
	public int getDimension() {
		return 1;
	}

	/**
	 * @see railo.runtime.type.Array#setEL(int, java.lang.Object)
	 */
	public Object setEL(int intKey, Object value) {
		int count=0;
		
		if(intKey>size()) {
			return setEL(Caster.toString(intKey),value);
		}
		//Iterator it = keyIterator();
		Key[] keys = keys();
		for(int i=0;i<keys.length;i++) {
			if((++count)==intKey) {
				return super.setEL(keys[i],value);
			}
		}
		return value;
	}


	/**
	 * @see railo.runtime.type.Array#setE(int, java.lang.Object)
	 */
	public Object setE(int intKey, Object value) throws PageException {

		if(intKey>size()) {
			return set(Caster.toString(intKey),value);
		}
		//Iterator it = keyIterator();
		Key[] keys = keys();
		for(int i=0;i<keys.length;i++) {
			if((i+1)==intKey) {
				return super.set(keys[i],value);
			}
		}
		throw new ExpressionException("invalid index ["+intKey+"] for argument scope");	
	}


	/**
	 * @see railo.runtime.type.Array#intKeys()
	 */
	public int[] intKeys() {
		int[] ints=new int[size()];
		for(int i=0;i<ints.length;i++)ints[i]=i+1;
		return ints;
	}


	/**
	 * @see railo.runtime.type.Array#insert(int, java.lang.Object)
	 */
	public boolean insert(int index, Object value) throws ExpressionException {
		return insert(index, ""+index, value);
	}
	
	/**
     * @see railo.runtime.type.scope.Argument#insert(int, java.lang.String, java.lang.Object)
     */
	public boolean insert(int index, String key, Object value) throws ExpressionException {
		int len=size();
		if(index<1 || index>len)
			throw new ExpressionException("invalid index to insert a value to argument scope",len==0?"can't insert in a empty argument scope":"valid index goes from 1 to "+(len-1));
		
		// remove all upper
			LinkedHashMap lhm = new LinkedHashMap();
			Collection.Key[] keys=keys();
			
			Collection.Key k;
			for(int i=1;i<=keys.length;i++) {
				if(i<index)continue;
				k=keys[i-1];
				lhm.put(k.getString(),get(k,null));
				removeEL(k);
			}
		
		// set new value
			setEL(key,value);
		
		// reset upper values
			Iterator it = lhm.entrySet().iterator();
			Map.Entry entry;
			while(it.hasNext()) {			
				entry=(Entry) it.next();
				setEL(KeyImpl.toKey(entry.getKey()),entry.getValue());
			}		
		return true;
	}


	/**
	 * @see railo.runtime.type.Array#append(java.lang.Object)
	 */
	public Object append(Object o) throws PageException {
		return set(Caster.toString(size()+1),o);
	}
	
	/**
	 * @see railo.runtime.type.Array#appendEL(java.lang.Object)
	 */
	public Object appendEL(Object o) {
		try {
			return append(o);
		} catch (PageException e) {
			return null;
		}
	}


	/**
	 * @see railo.runtime.type.Array#prepend(java.lang.Object)
	 */
	public Object prepend(Object o) throws PageException {
		for(int i=size();i>0;i--) {
			setE(i+1,getE(i));
		}
		setE(1,o);
		return o;
	}


	/**
	 * @see railo.runtime.type.Array#resize(int)
	 */
	public void resize(int to) throws PageException {
		for(int i=size(); i<to; i++) {
			append(null);
		}
		//throw new ExpressionException("can't resize this array");
	}



	/**
	 * @see railo.runtime.type.Array#sort(java.lang.String, java.lang.String)
	 */
	public void sort(String sortType, String sortOrder) throws ExpressionException {
		// TODO Impl.
		throw new ExpressionException("can't sort ["+sortType+"-"+sortOrder+"] Argument Scope","not Implemnted Yet");
	}

	public void sort(Comparator com) throws ExpressionException {
		// TODO Impl.
		throw new ExpressionException("can't sort Argument Scope","not Implemnted Yet");
	}

	/**
	 * @see railo.runtime.type.Array#toArray()
	 */
	public Object[] toArray() {
		Iterator it = keyIterator();//getMap().keySet().iterator();
		Object[] arr=new Object[size()];
		int count=0;
		
		while(it.hasNext()) {
			arr[count++]=it.next();
		}
		return arr;
	}
	
	public Object setArgument(Object obj) throws PageException {
		if(obj==this) return obj;
		
		
		if(Decision.isStruct(obj)) {
			clear(); // TODO bessere impl. anstelle vererbung wrao auf struct
			Struct sct=Caster.toStruct(obj);
			Iterator it = sct.keyIterator();
			String key;
			while(it.hasNext()) {
				key=it.next().toString();
				setEL(key, sct.get(key,null));
			}
			return obj;
		}
		throw new ExpressionException("can not overwrite arguments scope");
	}


	/**
	 * @see railo.runtime.type.Array#toArrayList()
	 */
	public ArrayList toArrayList() {
		ArrayList list = new ArrayList();
		Object[] arr = toArray();
		for(int i=0;i<arr.length;i++) {
			list.add(arr[i]);
		}
		return list;
	}

	/**
	 * @see railo.runtime.type.Array#removeE(int)
	 */
	public Object removeE(int intKey) throws PageException {
		Key[] keys = keys();
		for(int i=0;i<keys.length;i++) {
			if((i+1)==intKey) {
				return super.remove(keys[i]);
			}
		}
		throw new ExpressionException("can't remove argument number ["+intKey+"], argument doesn't exist");
	}

	/**
	 * @see railo.runtime.type.Array#removeEL(int)
	 */
	public Object removeEL(int intKey) {
		Key[] keys = keys();
		for(int i=0;i<keys.length;i++) {
			if((i+1)==intKey) {
				return super.removeEL (keys[i]);
			}
		}
		return null;
	}

    /**
     * @see railo.runtime.type.StructImpl#containsKey(railo.runtime.type.Collection.Key)
     */
    public boolean containsKey(Collection.Key key) {
    	return get(key,null)!=null && super.containsKey(key);
    }

    /**
     * @see railo.runtime.type.Array#containsKey(int)
     */
    public boolean containsKey(int key) {
        return get(key,null)!=null;
    }
    


	/**
	 * @see railo.runtime.type.Array#toList()
	 */
	public List toList() {
		return ArrayAsList.toList(this);
	}
	

	/**
	 * @see railo.runtime.type.StructImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		ArgumentImpl trg=new ArgumentImpl();
		trg.bind=false;
		trg.functionArgumentNames=functionArgumentNames;
		//trg.supportFunctionArguments=supportFunctionArguments;
		copy(this,trg,deepCopy);
		return trg;
	}

	public void setFunctionArgumentNames(Set functionArgumentNames) {// future add to interface
		this.functionArgumentNames=functionArgumentNames;
	}
/*
	public void setNamedArguments(boolean namedArguments) {
		this.namedArguments=namedArguments;
	}
	public boolean isNamedArguments() {
		return namedArguments;
	}
*/

	/**
	 * converts a argument scope to a regular struct
	 * @param arg argument scope to convert
	 * @return resulting struct
	 */
	public static Struct toStruct(Argument arg) {
		Struct trg=new StructImpl();
		StructImpl.copy(arg, trg, false);
		return trg;
	}

	/**
	 * converts a argument scope to a regular array
	 * @param arg argument scope to convert
	 * @return resulting array
	 */
	public static Array toArray(Argument arg) {
		ArrayImpl trg=new ArrayImpl();
		int[] keys = arg.intKeys();
		for(int i=0;i<keys.length;i++){
			trg.setEL(keys[i],
					arg.get(keys[i],null));
		}
		return trg;
	}

	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key, defaultValue);
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		return get(key);
	}

	@Override
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return set(propertyName, value);
	}

	@Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return setEL(propertyName, value);
	}

	@Override
	public Object call(PageContext pc, Key methodName, Object[] args) throws PageException {
		Object obj = get(methodName,null);
		if(obj instanceof UDF) {
			return ((UDF)obj).call(pc,args,false);
		}
		return MemberUtil.call(pc, this, methodName, args, CFTypes.TYPE_ARRAY, "array");
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		Object obj = get(methodName,null);
		if(obj instanceof UDF) {
			return ((UDF)obj).callWithNamedValues(pc,args,false);
		}
		return MemberUtil.callWithNamedValues(pc,this,methodName,args, CFTypes.TYPE_ARRAY, "array");
	}
}