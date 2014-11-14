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
package railo.runtime.orm.hibernate;

public class HibernateConstants {

	public static final int CASCADE_NONE = 0;
	public static final int CASCADE_ALL = 1;
	public static final int CASCADE_SAVE_UPDATE = 2;
	public static final int CASCADE_DELETE = 4;
	public static final int CASCADE_DELETE_ORPHAN = 8;
	public static final int CASCADE_ALL_DELETE_ORPHAN = 16;
	public static final int REFRESH = 32;

	public static final int COLLECTION_TYPE_ARRAY = 1;
	public static final int COLLECTION_TYPE_STRUCT = 2;
	
}
