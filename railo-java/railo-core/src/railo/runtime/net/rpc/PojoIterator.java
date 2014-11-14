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
package railo.runtime.net.rpc;

import java.lang.reflect.Method;
import java.util.Iterator;

import railo.commons.lang.Pair;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

public class PojoIterator implements Iterator<Pair<Collection.Key,Object>> {
	
	private static final Object[] EMPTY_ARG = new Object[]{}; 
	
	private Pojo pojo;
	private Method[] getters;
	private Class<? extends Pojo> clazz;
	private int index=-1;

	public PojoIterator(Pojo pojo) {
		this.pojo=pojo;
		this.clazz=pojo.getClass();
		getters = Reflector.getGetters(pojo.getClass());
	}
	
	public int size() {
		return getters.length;
	}

	@Override
	public boolean hasNext() {
		return (index+1)<getters.length;
	}

	@Override
	public Pair<Collection.Key, Object> next() {
		Method g = getters[++index];
		try {
			
			return new Pair<Collection.Key, Object>(KeyImpl.init(g.getName().substring(3)), g.invoke(pojo, EMPTY_ARG));
		}
		catch (Throwable t) {
			throw new PageRuntimeException(Caster.toPageException(t));
		}
	}

	@Override
	public void remove() {
		throw new RuntimeException("method remove is not supported!");
	}

}
