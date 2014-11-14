/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.orm.hibernate.tuplizer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.tuple.Instantiator;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.hibernate.CommonUtil;
import railo.runtime.orm.hibernate.HibernateCaster;
import railo.runtime.orm.hibernate.HibernateORMEngine;
import railo.runtime.orm.hibernate.HibernateORMSession;
import railo.runtime.orm.hibernate.HibernatePageException;
import railo.runtime.orm.hibernate.HibernateUtil;

public class CFCInstantiator implements Instantiator {
	
	private String entityName;
	private Set<String> isInstanceEntityNames = new HashSet<String>();
	
	public CFCInstantiator() {
		this.entityName = null;
	}

	/**
	 * Constructor of the class
	 * @param mappingInfo
	 */
	public CFCInstantiator(PersistentClass mappingInfo) {
		this.entityName = mappingInfo.getEntityName();
		isInstanceEntityNames.add( entityName );
		if ( mappingInfo.hasSubclasses() ) {
			Iterator<PersistentClass> itr = mappingInfo.getSubclassClosureIterator();
			while ( itr.hasNext() ) {
				final PersistentClass subclassInfo = itr.next();
				isInstanceEntityNames.add( subclassInfo.getEntityName() );
			}
		}
	}

	@Override
	public final Object instantiate(Serializable id) {
		return instantiate();
	}

	@Override
	public final Object instantiate() {
		try {
			PageContext pc = CommonUtil.pc();
			HibernateORMSession session=HibernateUtil.getORMSession(pc,true);
			HibernateORMEngine engine=(HibernateORMEngine) session.getEngine();
			Component c = engine.create(pc, session, entityName, true);
			CommonUtil.setEntity(c,true);
			return c;//new CFCProxy(c);
		} 
		catch (PageException pe) {
			throw new HibernatePageException(pe);
		}
	}

	@Override
	public final boolean isInstance(Object object) {
		Component cfc = CommonUtil.toComponent(object,null);
		if(cfc==null) return false;
		return isInstanceEntityNames.contains( HibernateCaster.getEntityName(cfc));
	}
}