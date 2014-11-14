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
package railo.runtime.functions;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.reflection.Reflector;

public class BIFProxy extends BIF {

	private Class clazz;

	public BIFProxy(Class clazz) {
		this.clazz=clazz;
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		Object[] _args=new Object[args.length+1];
		_args[0]=pc;
		for(int i=0;i<args.length;i++){
			_args[i+1]=args[i];
		}
		return Reflector.callStaticMethod(clazz,"call",_args);
	}

}
