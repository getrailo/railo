package railo.runtime.cfx;

import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

import com.allaire.cfx.Query;
import com.allaire.cfx.Request;



/**
 * Implementation of the CFX Request Interface
 */
public final class RequestImpl implements Request {
	
	private static final Collection.Key QUERY = KeyImpl.getInstance("query");
	private static final Collection.Key DEBUG = KeyImpl.getInstance("debug");
	private Struct attributes;
	private Struct settings;
	private Query query;

	/**
	 * constructor of the class
	 * @param pc
	 * @param attributes
	 * @throws PageException
	 */
	public RequestImpl(PageContext pc,Struct attributes) throws PageException {
		this.attributes=attributes;
		Object o=attributes.get(QUERY,null);
		String varName=Caster.toString(o,null);
		
		if(o!=null) {
			if(varName!=null) {
				this.query=new QueryWrap(Caster.toQuery(pc.getVariable(varName)));
				attributes.removeEL(QUERY);
			}
			else if(Decision.isQuery(o)) {
				this.query=new QueryWrap(Caster.toQuery(o));
				attributes.removeEL(QUERY);
			}
			else {
				throw new ApplicationException("Attribute query doesn't contain a Query or a Name of a Query");
			}
		}
	}
	
	/**
	 * constructor of the class
	 * @param attributes
	 * @param query
	 * @param settings
	 */
	public RequestImpl(Struct attributes,Query query, Struct settings)  {
		this.attributes=attributes;
		this.query=query;
		this.settings=settings;
	}

	/**
	 * @see com.allaire.cfx.Request#attributeExists(java.lang.String)
	 */
	public boolean attributeExists(String key) {
		return attributes.get(key,null)!=null;
	}

	/**
	 * @see com.allaire.cfx.Request#debug()
	 */
	public boolean debug() {
		Object o=attributes.get(DEBUG,Boolean.FALSE);
		if(o==null) return false;
		return Caster.toBooleanValue(o,false);
	}

	/**
	 * @see com.allaire.cfx.Request#getAttribute(java.lang.String)
	 */
	public String getAttribute(String key) {
		return getAttribute(key, "");
	}

	/**
	 * @see com.allaire.cfx.Request#getAttribute(java.lang.String, java.lang.String)
	 */
	public String getAttribute(String key, String defaultValue) {
		return Caster.toString(attributes.get(key,defaultValue),defaultValue);
	}

	/**
	 * @see com.allaire.cfx.Request#getAttributeList()
	 */
	public String[] getAttributeList() {
		return attributes.keysAsString();
	}

	/**
	 * @see com.allaire.cfx.Request#getIntAttribute(java.lang.String)
	 */
	public int getIntAttribute(String key) throws NumberFormatException {
		return getIntAttribute(key, -1);
	}

	/**
	 * @see com.allaire.cfx.Request#getIntAttribute(java.lang.String, int)
	 */
	public int getIntAttribute(String key, int defaultValue) {
		Object o=attributes.get(key,null);
		if(o==null) return defaultValue;
		try {
			return Caster.toIntValue(o);
		} catch (PageException e) {
			return defaultValue;
		}
	}

	/**
	 * @see com.allaire.cfx.Request#getQuery()
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @see com.allaire.cfx.Request#getSetting(java.lang.String)
	 */
	public String getSetting(String key) {
		return settings==null?"":Caster.toString(settings.get(key,""),"");
	}

}