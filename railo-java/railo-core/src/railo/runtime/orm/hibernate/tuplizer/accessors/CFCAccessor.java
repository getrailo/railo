package railo.runtime.orm.hibernate.tuplizer.accessors;

import org.hibernate.PropertyNotFoundException;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;

public class CFCAccessor implements PropertyAccessor {
	
	public CFCAccessor(){
	}
	
	@Override
	public Getter getGetter(Class clazz, String propertyName) throws PropertyNotFoundException {
		return new CFCGetter(propertyName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Setter getSetter(Class clazz, String propertyName)	throws PropertyNotFoundException {
		return new CFCSetter(propertyName);
	}

}
