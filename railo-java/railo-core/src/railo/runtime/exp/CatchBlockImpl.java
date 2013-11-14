package railo.runtime.exp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Castable;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.reflection.Reflector;
import railo.runtime.reflection.pairs.MethodInstance;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Objects;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.it.EntryIterator;
import railo.runtime.type.it.KeyIterator;
import railo.runtime.type.it.StringIterator;
import railo.runtime.type.it.ValueIterator;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructUtil;

public class CatchBlockImpl extends StructImpl implements CatchBlock,Castable,Objects{
	
	private static final long serialVersionUID = -3680961614605720352L;
	
	public static final Key ERROR_CODE = KeyImpl.intern("ErrorCode");
	public static final Key EXTENDEDINFO = KeyImpl.intern("ExtendedInfo");
	public static final Key EXTENDED_INFO = KeyImpl.intern("Extended_Info");
	public static final Key TAG_CONTEXT = KeyImpl.intern("TagContext");
	public static final Key STACK_TRACE = KeyImpl.intern("StackTrace");
	public static final Key ADDITIONAL = KeyImpl.intern("additional");
	private static final Object NULL = new Object();
	
	private PageExceptionImpl exception;
	

	public CatchBlockImpl(PageExceptionImpl pe) {
		this.exception=pe;

		setEL(KeyConstants._Message, new SpecialItem(pe, KeyConstants._Message));
		setEL(KeyConstants._Detail, new SpecialItem(pe, KeyConstants._Detail));
		setEL(ERROR_CODE, new SpecialItem(pe, ERROR_CODE));
		setEL(EXTENDEDINFO, new SpecialItem(pe, EXTENDEDINFO));
		setEL(EXTENDED_INFO, new SpecialItem(pe, EXTENDED_INFO));
		setEL(ADDITIONAL, new SpecialItem(pe, ADDITIONAL));
		setEL(TAG_CONTEXT, new SpecialItem(pe, TAG_CONTEXT));
		setEL(KeyConstants._type, new SpecialItem(pe, KeyConstants._type));
		setEL(STACK_TRACE, new SpecialItem(pe, STACK_TRACE));
		
		
		if(pe instanceof NativeException){
			Throwable throwable = ((NativeException)pe).getRootCause();
			Method[] mGetters = Reflector.getGetters(throwable.getClass());
			Method getter;
			Collection.Key key;
			if(!ArrayUtil.isEmpty(mGetters)){
				for(int i=0;i<mGetters.length;i++){
					getter=mGetters[i];
					if(getter.getDeclaringClass()==Throwable.class) {
						continue;
					}
					key=KeyImpl.init(Reflector.removeGetterPrefix(getter.getName()));
					if(STACK_TRACE.equalsIgnoreCase(key)) continue;
					setEL(key,new Pair(throwable,key, getter,false));
				}
			}
		}
	}

	
	class SpecialItem {
		private PageExceptionImpl pe;
		private Key key;
		
		public SpecialItem(PageExceptionImpl pe, Key key) {
			this.pe=pe;
			this.key=key;
		}
		
		public Object get() {
			if(key==KeyConstants._Message) return StringUtil.emptyIfNull(pe.getMessage());
			if(key==KeyConstants._Detail) return StringUtil.emptyIfNull(pe.getDetail());
			if(key==ERROR_CODE) return StringUtil.emptyIfNull(pe.getErrorCode());
			if(key==EXTENDEDINFO) return StringUtil.emptyIfNull(pe.getExtendedInfo());
			if(key==EXTENDED_INFO) return StringUtil.emptyIfNull(pe.getExtendedInfo());
			if(key==KeyConstants._type) return StringUtil.emptyIfNull(pe.getTypeAsString());
			if(key==STACK_TRACE) return StringUtil.emptyIfNull(pe.getStackTraceAsString());
			if(key==ADDITIONAL) return pe.getAddional();
			if(key==TAG_CONTEXT) return pe.getTagContext(ThreadLocalPageContext.getConfig());
			return null;
		}
		
		public void set(Object o){
			try {
				if(!(o instanceof Pair)) {
					if(key==KeyConstants._Detail) {
						pe.setDetail(Caster.toString(o));
						return;
					}
					else if(key==ERROR_CODE) {
						pe.setErrorCode(Caster.toString(o));
						return;
					}
					else if(key==EXTENDEDINFO || key==EXTENDED_INFO) {
						pe.setExtendedInfo(Caster.toString(o));
						return;
					}
					else if(key==STACK_TRACE) {
						if(o instanceof StackTraceElement[]){
							pe.setStackTrace((StackTraceElement[])o);
							return;
						}
						else if(Decision.isCastableToArray(o)) {
							Object[] arr = Caster.toNativeArray(o);
							StackTraceElement[] elements=new StackTraceElement[arr.length];
							for(int i=0;i<arr.length;i++) {
								if(arr[i] instanceof StackTraceElement)
									elements[i]=(StackTraceElement) arr[i];
								else
									throw new CasterException(o, StackTraceElement[].class);
							}
							pe.setStackTrace(elements);
							return;
							
						}
					}
				}
			}
			catch(PageException pe){}
			
			superSetEL(key,o);
			
			
		}
		public Object remove(){
			Object rtn=null;
			if(key==KeyConstants._Detail) {
				rtn=pe.getDetail();
				pe.setDetail("");
			}
			else if(key==ERROR_CODE)  {
				rtn=pe.getErrorCode();
				pe.setErrorCode("0");
			}
			else if(key==EXTENDEDINFO || key==EXTENDED_INFO)  {
				rtn=pe.getExtendedInfo();
				pe.setExtendedInfo(null);
			}
			return rtn;
			
		}
	}
	
	
	/**
	 * @return the pe
	 */
	public PageException getPageException() {
		return exception;
	}

	@Override
	public String castToString() throws ExpressionException {
		return castToString(null);
	}
	
	@Override
	public String castToString(String defaultValue) {
		return exception.getClass().getName();
	}

	@Override
	public boolean containsValue(Object value) {
		Key[] keys = keys();
		for(int i=0;i<keys.length;i++){
			if(get(keys[i],null)==value) return true;
		}
		return false;
	}

	@Override
	public Collection duplicate(boolean deepCopy) {
		Struct sct=new StructImpl();
		StructUtil.copy(this, sct, true);
		return sct;
	}

	@Override
	public Set entrySet() {
		return StructUtil.entrySet(this);
	}
	
	public void print(PageContext pc){
		((PageContextImpl)pc).handlePageException(exception);
		
	}

	@Override
	public Object get(Key key, Object defaultValue) {
		Object value = super.get(key,defaultValue);
		if(value instanceof SpecialItem) {
			return ((SpecialItem)value).get();
		}
		else if(value instanceof Pair) {
			Pair pair=(Pair) value;
			try {
				Object res = pair.getter.invoke(pair.throwable, new Object[]{});
				if(pair.doEmptyStringWhenNull && res==null) return "";
				return res;
			} 
			catch (Exception e) {
				return defaultValue;
			}
		}
		return value;
	}

	@Override
	public Object set(Key key, Object value) throws PageException {
		Object curr = super.get(key,null);
		if(curr instanceof SpecialItem){
			((SpecialItem)curr).set(value);
			return value;
		}
		else if(curr instanceof Pair){
			Pair pair=(Pair) curr;
			MethodInstance setter = Reflector.getSetter(pair.throwable, pair.name.getString(), value,null);
			if(setter!=null){
				try {
					setter.invoke(pair.throwable);
					return value;
				} catch (Exception e) {
					throw Caster.toPageException(e);
				}
			}
		}
		
		return super.set(key, value);
	}

	@Override
	public Object setEL(Key key, Object value) {
		Object curr = super.get(key,null);
		if(curr instanceof SpecialItem){
			((SpecialItem)curr).set(value);
			return value;
		}
		else if(curr instanceof Pair){
			Pair pair=(Pair) curr;
			MethodInstance setter = Reflector.getSetter(pair.throwable, pair.name.getString(), value,null);
			if(setter!=null){
				try {
					setter.invoke(pair.throwable);
				} catch (Exception e) {}
				return value;
			}
		}
		return super.setEL(key, value);
	}
	
	private Object superSetEL(Key key, Object value) {
		return super.setEL(key, value);
	}

	@Override
	public int size() {
		return keys().length;
	}

	@Override
	public Key[] keys() {
		Key[] keys = super.keys();
		List<Key> list=new ArrayList<Key>();
		for(int i=0;i<keys.length;i++){
			if(get(keys[i], null)!=null)list.add(keys[i]);
		}
		return list.toArray(new Key[list.size()]);
	}

	@Override
	public Object remove(Key key) throws PageException {
		Object curr = super.get(key,null);
		if(curr instanceof SpecialItem){
			return ((SpecialItem)curr).remove();
		}
		else if(curr instanceof Pair){
			Pair pair=(Pair) curr;
			MethodInstance setter = Reflector.getSetter(pair.throwable, pair.name.getString(), null,null);
			if(setter!=null){
				try {
					Object before = pair.getter.invoke(pair.throwable, new Object[0]);
					setter.invoke(pair.throwable);
					return before;
				} catch (Exception e) {
					throw Caster.toPageException(e);
				}
			}
		}	
		return super.remove(key);
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

	@Override
	public Iterator<Object> valueIterator() {
		return new ValueIterator(this, keys());
	}

	@Override
	public java.util.Collection values() {
		return StructUtil.values(this);
	}

	@Override
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return StructUtil.toDumpTable(this,"Catch",pageContext,maxlevel,dp);
	}



	class Pair{
		Throwable throwable;
		Collection.Key name;
		Method getter;
		private boolean doEmptyStringWhenNull;
		
		public Pair(Throwable throwable,Key name, Method method,boolean doEmptyStringWhenNull) {
			this.throwable = throwable;
			this.name = name;
			this.getter = method;
			this.doEmptyStringWhenNull = doEmptyStringWhenNull;
		}
		public Pair(Throwable throwable,String name, Method method, boolean doEmptyStringWhenNull) {
			this(throwable, KeyImpl.init(name), method,doEmptyStringWhenNull);
		}
		
		public String toString(){
			try {
				return Caster.toString(getter.invoke(throwable, new Object[]{}));
			} catch (Exception e) {
				throw new PageRuntimeException(Caster.toPageException(e));
			}
		}
	}

	public Object call(PageContext pc, String methodName, Object[] arguments) throws PageException {
		Object obj=exception;
		if(exception instanceof NativeException) obj=((NativeException)exception).getRootCause();
		if("dump".equalsIgnoreCase(methodName)){
			print(pc);
			return null;
		}
		
		try{
			return Reflector.callMethod(obj, methodName, arguments);
		}
		catch(PageException e){
			return Reflector.callMethod(exception, methodName, arguments);
		}
	}

	
	
	
	public Object callWithNamedValues(PageContext pc, String methodName,Struct args) throws PageException {
		throw new ApplicationException("named arguments not supported");
	}

	public Object callWithNamedValues(PageContext pc, Key methodName,Struct args) throws PageException {
		throw new ApplicationException("named arguments not supported");
	}
	public boolean isInitalized() {
		return true;
	}

	public Object set(PageContext pc, String propertyName, Object value) throws PageException {
		return set(propertyName, value);
	}

	public Object set(PageContext pc, Key propertyName, Object value)throws PageException {
		return set(propertyName, value);
	}

	public Object setEL(PageContext pc, String propertyName, Object value) {
		return setEL(propertyName, value);
	}

	public Object setEL(PageContext pc, Key propertyName, Object value) {
		return setEL(propertyName, value);
	}
	public Object get(Key key) throws PageException {
		Object res = get(key,NULL);
		if(res!=NULL) return res;
		throw StructImpl.invalidKey(null,this, key);
	}
	public Object get(PageContext pc, String key, Object defaultValue) {
		return get(key,defaultValue);
	}

	public Object get(PageContext pc, Key key, Object defaultValue) {
		return get(key,defaultValue);
	}

	public Object get(PageContext pc, String key) throws PageException {
		return get(key);
	}

	public Object get(PageContext pc, Key key) throws PageException {
		return get(key);
	}
	public Object call(PageContext pc, Key methodName, Object[] arguments) throws PageException {
		return call(pc, methodName.getString(), arguments);
	}
	/*public Object remove (String key) throws PageException {
		return remove(KeyImpl.init(key));
	}*/
	public Object removeEL(Key key) {
		try {
			return remove(key);
		} catch (PageException e) {
			return null;
		}
	}
}
