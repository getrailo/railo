package railo.runtime.orm.hibernate.tuplizer.accessors;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.property.Getter;
import org.hibernate.type.Type;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.hibernate.HibernateCaster;
import railo.runtime.orm.hibernate.HibernateORMEngine;
import railo.runtime.orm.hibernate.HibernateRuntimeException;
import railo.runtime.orm.hibernate.HibernateUtil;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.util.ComponentUtil;

public class CFCGetter implements Getter {

	private Key key;

	/**
	 * Constructor of the class
	 * @param key
	 */
	public CFCGetter(String key){
		this(KeyImpl.init(key));
	}
	
	/**
	 * Constructor of the class
	 * @param engine 
	 * @param key
	 */
	public CFCGetter( Collection.Key key){
		this.key=key;
	}
	
	/**
	 * @see org.hibernate.property.Getter#get(java.lang.Object)
	 */
	public Object get(Object trg) throws HibernateException {
		try {
			// MUST cache this, pherhaps when building xml
			HibernateORMEngine engine = getHibernateORMEngine();
			PageContext pc = ThreadLocalPageContext.get();
			Component cfc = Caster.toComponent(trg);
			String name = HibernateCaster.getEntityName(pc, cfc);
			ClassMetadata metaData = engine.getSessionFactory(pc).getClassMetadata(name);
			Type type = HibernateUtil.getPropertyType(metaData, key.getString());
			
			Object rtn = ComponentUtil.toComponentPro(cfc).getComponentScope().get(key,null);
			return HibernateCaster.toSQL(engine, type, rtn);
		} 
		catch (PageException e) {
			throw new HibernateRuntimeException(e);
		}
	}
	

	public HibernateORMEngine getHibernateORMEngine(){
		try {
			// TODO better impl
			PageContext pc = ThreadLocalPageContext.get();
			ConfigImpl config=(ConfigImpl) pc.getConfig();
			return (HibernateORMEngine) config.getORMEngine(pc);
		} 
		catch (PageException e) {}
			
		return null;
	}
	

	/**
	 * @see org.hibernate.property.Getter#getForInsert(java.lang.Object, java.util.Map, org.hibernate.engine.SessionImplementor)
	 */
	public Object getForInsert(Object trg, Map arg1, SessionImplementor arg2)throws HibernateException {
		return get(trg);// MUST better solution? this is from MapGetter
	}

	/**
	 * @see org.hibernate.property.Getter#getMember()
	 */
	public Member getMember() {
		return null;
	}

	/**
	 * @see org.hibernate.property.Getter#getMethod()
	 */
	public Method getMethod() {
		return null;
	}

	public String getMethodName() {
		return null;// MUST macht es sinn den namen zurück zu geben?
	}

	public Class getReturnType() {
		return Object.class;// MUST more concrete?
	}

}
