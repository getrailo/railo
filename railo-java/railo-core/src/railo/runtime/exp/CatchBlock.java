package railo.runtime.exp;

import java.util.Set;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.op.Castable;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.StructImpl;

public class CatchBlock extends StructImpl implements Castable{

	public static final Key MESSAGE = KeyImpl.getInstance("Message");
	public static final Key DETAIL = KeyImpl.getInstance("Detail");
	public static final Key ERROR_CODE = KeyImpl.getInstance("ErrorCode");
	public static final Key EXTENDED_INFO = KeyImpl.getInstance("ExtendedInfo");
	public static final Key TYPE = KeyImpl.getInstance("type");
	public static final Key TAG_CONTEXT = KeyImpl.getInstance("TagContext");
	public static final Key STACK_TRACE = KeyImpl.getInstance("StackTrace");
	public static final Key ADDITIONAL = KeyImpl.getInstance("additional");
	
	private Config config;// MUSTMUST remove this -> serialiable
	private PageExceptionImpl pe;
	private SpecialItem si = new SpecialItem();
	

	public CatchBlock(Config config,PageExceptionImpl pe) {
		this.config=config;
		this.pe=pe;
		
		setEL(MESSAGE,si);
		setEL(DETAIL,si);
		setEL(ERROR_CODE,si);
		setEL(EXTENDED_INFO,si);
		setEL(TYPE,si);
		setEL(TAG_CONTEXT,si);
		setEL(STACK_TRACE,si);
		setEL(ADDITIONAL,si);
        
		
	}

	/**
	 * FUTURE add to interface
	 * @return the pe
	 */
	public PageException getPageException() {
		return pe;
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#castToString()
	 */
	public String castToString() throws ExpressionException {
		return castToString(null);
	}
	
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return pe.getClass().getName();
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		initAll();
		return super.containsValue(value);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		initAll();
		CatchBlock trg = new CatchBlock(config,pe);
		trg.initAll();
		StructImpl.copy(this, trg, deepCopy);
		return trg;
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#entrySet()
	 */
	public Set entrySet() {
		initAll();
		return super.entrySet();
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object get(Key key, Object defaultValue) {
		Object value = super.get(key,defaultValue);
		if(value==si)return doSpecialItem(key);
		return value;
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#get(railo.runtime.type.Collection.Key)
	 */
	public Object get(Key key) throws PageException {
		Object value = super.get(key);
		if(value==si)return doSpecialItem(key);
		return value;
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		initAll();
		return super.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 *
	 * @see railo.runtime.type.StructImpl#values()
	 */
	public java.util.Collection values() {
		initAll();
		return super.values();
	}
	


	private Object doSpecialItem(Collection.Key key) {
		
		if(MESSAGE.equals(key)) 		return setEL(key, StringUtil.emptyIfNull(pe.getMessage()));
		if(DETAIL.equals(key)) 		return setEL(key, StringUtil.emptyIfNull(pe.getDetail()));
		if(ERROR_CODE.equals(key)) 	return setEL(key, StringUtil.emptyIfNull(pe.getErrorCode()));
		if(EXTENDED_INFO.equals(key)) 	return setEL(key, StringUtil.emptyIfNull(pe.getExtendedInfo()));
		if(TYPE.equals(key)) 			return setEL(key, StringUtil.emptyIfNull(pe.getTypeAsString()));
		if(STACK_TRACE.equals(key)) 	return setEL(key, StringUtil.emptyIfNull(pe.getStackTraceAsString()));
		if(ADDITIONAL.equals(key)) 		return setEL(key, pe.getAdditional());
		if(TAG_CONTEXT.equals(key)) 	return setEL(key, pe.getTagContext(config)); 	
		return null;
	}
	
	private void initAll() {
		get(MESSAGE,null);
		get(DETAIL,null);
		get(ERROR_CODE,null);
		get(EXTENDED_INFO,null);
		get(TYPE,null);
		get(STACK_TRACE,null);
		get(ADDITIONAL,null);
		get(TAG_CONTEXT,null);
	}
	
	class SpecialItem {
		
	}
	

}
