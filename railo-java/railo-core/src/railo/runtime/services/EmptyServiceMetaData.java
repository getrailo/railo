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
package railo.runtime.services;

import coldfusion.server.ServiceMetaData;

public class EmptyServiceMetaData implements ServiceMetaData {

	public int getPropertyCount() {
		return 0;
	}

	public String getPropertyLabel(int arg0) {
		return null;
	}

	public String getPropertyType(int arg0) {
		return null;
	}

	public boolean exists(String arg0) {
		return false;
	}

}
