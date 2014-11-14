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
package railo.runtime.orm;

import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.exp.PageException;

public class ORMEngineDummy implements ORMEngine {

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ORMSession createSession(PageContext pc) throws PageException {
		return null;
	}

	public Object getSessionFactory(PageContext pc) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(PageContext pc) throws PageException {
		// TODO Auto-generated method stub

	}

	public ORMConfiguration getConfiguration(PageContext pc) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataSource getDataSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getEntityNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean reload(PageContext pc, boolean force) throws PageException {
		// TODO Auto-generated method stub
		return false;
	}

}
