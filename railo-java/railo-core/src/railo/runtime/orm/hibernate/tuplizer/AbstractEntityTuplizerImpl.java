package railo.runtime.orm.hibernate.tuplizer;

import java.io.Serializable;
import java.util.HashMap;

import org.hibernate.EntityMode;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.tuple.Instantiator;
import org.hibernate.tuple.entity.AbstractEntityTuplizer;
import org.hibernate.tuple.entity.EntityMetamodel;

import railo.runtime.Component;
import railo.runtime.ComponentScope;
import railo.runtime.orm.hibernate.CommonUtil;
import railo.runtime.orm.hibernate.HibernateUtil;
import railo.runtime.orm.hibernate.tuplizer.accessors.CFCAccessor;
import railo.runtime.orm.hibernate.tuplizer.proxy.CFCHibernateProxyFactory;
import railo.runtime.type.cfc.ComponentAccess;

public class AbstractEntityTuplizerImpl extends AbstractEntityTuplizer {

	private static CFCAccessor accessor=new CFCAccessor();
	
	public AbstractEntityTuplizerImpl(EntityMetamodel entityMetamodel, PersistentClass persistentClass) {
		super(entityMetamodel, persistentClass);
	}

	@Override
	public Serializable getIdentifier(Object entity, SessionImplementor arg1) {
		return toIdentifier(super.getIdentifier(entity, arg1));
	}
	
	@Override
	public Serializable getIdentifier(Object entity) throws HibernateException {
		return toIdentifier(super.getIdentifier(entity));
	}

	private Serializable toIdentifier(Serializable id) {
		if(id instanceof Component) {
			HashMap<String, String> map = new HashMap<String, String>();
			Component cfc=(Component) id;
			ComponentScope scope = cfc.getComponentScope();
			railo.runtime.component.Property[] props = HibernateUtil.getIDProperties(cfc, true,true);
			String name,value;
			for(int i=0;i<props.length;i++){
				name=props[i].getName();
				value=CommonUtil.toString(scope.get(CommonUtil.createKey(name),null),null);
				map.put(name, value);
			}
			return map;
		}
		return id;
	}


	@Override
	protected Instantiator buildInstantiator(PersistentClass persistentClass) {
		return new CFCInstantiator(persistentClass);
	}
	
	/**
	 * return accessors 
	 * @param mappedProperty
	 * @return
	 */
	private PropertyAccessor buildPropertyAccessor(Property mappedProperty) {
		if ( mappedProperty.isBackRef() ) {
			PropertyAccessor ac = mappedProperty.getPropertyAccessor(null);
			if(ac!=null) return ac;
		}
		return accessor;
	}

	
	@Override
	protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
	}

	
	@Override
	protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
	}
	
	@Override
	protected ProxyFactory buildProxyFactory(PersistentClass pc, Getter arg1,Setter arg2) {
		CFCHibernateProxyFactory pf = new CFCHibernateProxyFactory();
		pf.postInstantiate(pc);
		
		return pf;
	}

	@Override
	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
		return CFCEntityNameResolver.INSTANCE.resolveEntityName(entityInstance);
	}

	@Override
	public EntityNameResolver[] getEntityNameResolvers() {
		return new EntityNameResolver[] { CFCEntityNameResolver.INSTANCE };
	}

	@Override
	public Class getConcreteProxyClass() {
		return ComponentAccess.class;// ????
	}

	@Override
	public Class getMappedClass() {
		return ComponentAccess.class; // ????
	}

	public EntityMode getEntityMode() {
		return EntityMode.MAP;
	}

	@Override
	public boolean isInstrumented() {
		return false;
	}

}
