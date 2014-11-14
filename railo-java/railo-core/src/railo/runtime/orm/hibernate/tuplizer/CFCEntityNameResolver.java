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
import org.hibernate.EntityNameResolver;

import railo.runtime.Component;
import railo.runtime.exp.PageException;
import railo.runtime.orm.hibernate.HibernateCaster;
import railo.runtime.orm.hibernate.HibernatePageException;


public  class CFCEntityNameResolver implements EntityNameResolver {
	public static final CFCEntityNameResolver INSTANCE = new CFCEntityNameResolver();

	/**
	 * {@inheritDoc}
	 */
	public String resolveEntityName(Object entity) {
		try {
			Component cfc = HibernateCaster.toComponent(entity);
			return HibernateCaster.getEntityName(cfc);
		} catch (PageException pe) {
			//print.printST(e);
			throw new HibernatePageException(pe);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		return getClass().equals( obj.getClass() );
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return getClass().hashCode();
	}
}