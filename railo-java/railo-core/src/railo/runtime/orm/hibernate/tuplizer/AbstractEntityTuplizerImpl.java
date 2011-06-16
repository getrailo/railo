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

import railo.runtime.ComponentPro;
import railo.runtime.ComponentScope;
import railo.runtime.op.Caster;
import railo.runtime.orm.hibernate.tuplizer.accessors.CFCAccessor;
import railo.runtime.orm.hibernate.tuplizer.proxy.CFCProxyFactory;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.cfc.ComponentAccess;
import railo.runtime.type.util.ComponentUtil;

public class AbstractEntityTuplizerImpl extends AbstractEntityTuplizer {

	private static CFCAccessor accessor=new CFCAccessor();
	
	public AbstractEntityTuplizerImpl(EntityMetamodel entityMetamodel, PersistentClass persistentClass) {
		super(entityMetamodel, persistentClass);
	}

	/**
	 * @see org.hibernate.tuple.entity.AbstractEntityTuplizer#getIdentifier(java.lang.Object, org.hibernate.engine.SessionImplementor)
	 */
	public Serializable getIdentifier(Object entity, SessionImplementor arg1) {
		return toIdentifier(super.getIdentifier(entity, arg1));
	}
	
	/**
	 * @see org.hibernate.tuple.entity.AbstractEntityTuplizer#getIdentifier(java.lang.Object)
	 */
	public Serializable getIdentifier(Object entity) throws HibernateException {
		return toIdentifier(super.getIdentifier(entity));
	}

	private Serializable toIdentifier(Serializable id) {
		if(id instanceof ComponentPro) {
			HashMap<String, String> map = new HashMap<String, String>();
			ComponentPro cfc=(ComponentPro) id;
			ComponentScope scope = cfc.getComponentScope();
			railo.runtime.component.Property[] props = ComponentUtil.getIDProperties(cfc, true);
			String name,value;
			for(int i=0;i<props.length;i++){
				name=props[i].getName();
				value=Caster.toString(scope.get(KeyImpl.init(name),null),null);
				map.put(name, value);
			}
			return map;
		}
		return id;
	}


	/**
	 * @see org.hibernate.tuple.entity.AbstractEntityTuplizer#buildInstantiator(org.hibernate.mapping.PersistentClass)
	 */
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

	
	/**
	 * @see org.hibernate.tuple.entity.AbstractEntityTuplizer#buildPropertyGetter(org.hibernate.mapping.Property, org.hibernate.mapping.PersistentClass)
	 */
	protected Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity) {
		return buildPropertyAccessor(mappedProperty).getGetter( null, mappedProperty.getName() );
	}

	
	/**
	 * @see org.hibernate.tuple.entity.AbstractEntityTuplizer#buildPropertySetter(org.hibernate.mapping.Property, org.hibernate.mapping.PersistentClass)
	 */
	protected Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity) {
		return buildPropertyAccessor(mappedProperty).getSetter( null, mappedProperty.getName() );
	}
	
	/**
	 * @see org.hibernate.tuple.entity.AbstractEntityTuplizer#buildProxyFactory(org.hibernate.mapping.PersistentClass, org.hibernate.property.Getter, org.hibernate.property.Setter)
	 */
	protected ProxyFactory buildProxyFactory(PersistentClass pc, Getter arg1,Setter arg2) {
		CFCProxyFactory pf = new CFCProxyFactory();
		pf.postInstantiate(pc);
		
		return pf;
	}

	/**
	 * @see org.hibernate.tuple.entity.EntityTuplizer#determineConcreteSubclassEntityName(java.lang.Object, org.hibernate.engine.SessionFactoryImplementor)
	 */
	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory) {
		return CFCEntityNameResolver.INSTANCE.resolveEntityName(entityInstance);
	}

	/**
	 * @see org.hibernate.tuple.entity.EntityTuplizer#getEntityNameResolvers()
	 */
	public EntityNameResolver[] getEntityNameResolvers() {
		return new EntityNameResolver[] { CFCEntityNameResolver.INSTANCE };
	}

	/**
	 * @see org.hibernate.tuple.entity.EntityTuplizer#getConcreteProxyClass()
	 */
	public Class getConcreteProxyClass() {
		return ComponentAccess.class;// ????
	}

	/**
	 * @see org.hibernate.tuple.Tuplizer#getMappedClass()
	 */
	public Class getMappedClass() {
		return ComponentAccess.class; // ????
	}

	public EntityMode getEntityMode() {
		return EntityMode.MAP;
	}

	/**
	 * @see org.hibernate.tuple.entity.EntityTuplizer#isInstrumented()
	 */
	public boolean isInstrumented() {
		return false;
	}

}
