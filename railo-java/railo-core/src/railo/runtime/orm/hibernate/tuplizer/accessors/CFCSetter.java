package railo.runtime.orm.hibernate.tuplizer.accessors;

import java.lang.reflect.Method;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.property.Setter;

import railo.runtime.Component;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMRuntimeException;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;

public final class CFCSetter implements Setter {
	
	private Key key;

	/**
	 * Constructor of the class
	 * @param key
	 */
	public CFCSetter(String key){
		this(KeyImpl.getInstance(key));
	}
	
	/**
	 * Constructor of the class
	 * @param key
	 */
	public CFCSetter(Collection.Key key){
		this.key=key;
	}

	@Override
	public String getMethodName() {
		return null;
	}

	@Override
	public Method getMethod() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void set(Object trg, Object value, SessionFactoryImplementor factory) throws HibernateException {
		try {
			Component cfc = Caster.toComponent(trg);
			cfc.getComponentScope().set(key,value);
		} 
		catch (PageException e) {
			throw new ORMRuntimeException(e);
		}
	}

}