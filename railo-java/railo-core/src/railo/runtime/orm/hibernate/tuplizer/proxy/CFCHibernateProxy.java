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
package railo.runtime.orm.hibernate.tuplizer.proxy;

import java.io.Serializable;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import railo.runtime.Component;
import railo.runtime.ComponentPro;
import railo.runtime.exp.PageException;
import railo.runtime.type.ObjectWrap;



/**
 * Proxy for "dynamic-map" entity representations.
 * SLOW
 */
public class CFCHibernateProxy extends ComponentProProxy implements HibernateProxy, Serializable,ObjectWrap {

	private static final long serialVersionUID = 4115236247834562085L;
	
	private CFCLazyInitializer li;
	
	@Override
	public Component getComponent() {
		return li.getCFC();
	}
	
	@Override
	public ComponentPro getComponentPro() {
		return (ComponentPro) li.getCFC();
	}
	
	public CFCHibernateProxy(CFCLazyInitializer li) {
		this.li = li;
	}

	@Override
	public Object writeReplace() {
		return this;
	}

	@Override
	public LazyInitializer getHibernateLazyInitializer() {
		return li;
	}

	@Override
	public Object getEmbededObject(Object defaultValue) {
		return getComponent();
	}

	@Override
	public Object getEmbededObject() throws PageException {
		return getComponent();
	}
}