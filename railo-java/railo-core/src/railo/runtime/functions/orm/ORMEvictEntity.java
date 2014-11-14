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
package railo.runtime.functions.orm;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class ORMEvictEntity {
	public static String call(PageContext pc,String entityName) throws PageException {
		return call(pc, entityName,null);
	}
	public static String call(PageContext pc,String entityName,String primaryKey) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		if(StringUtil.isEmpty(primaryKey))session.evictEntity(pc, entityName);
		else session.evictEntity(pc, entityName,primaryKey);
		return null;
	}
}
