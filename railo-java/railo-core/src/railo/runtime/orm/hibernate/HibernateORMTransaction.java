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

import org.hibernate.Session;
import org.hibernate.Transaction;

import railo.runtime.orm.ORMTransaction;

public class HibernateORMTransaction implements ORMTransaction {
	
	private Transaction trans;
	private Session session;
	private boolean doRollback;
	private boolean autoManage;

	public HibernateORMTransaction(Session session, boolean autoManage){
		this.session=session;
		this.autoManage=autoManage;
	}

	@Override
	public void begin() {
		if(autoManage)session.flush();
		trans=session.beginTransaction();
		
	}

	@Override
	public void commit() {
		// do nothing
	}

	@Override
	public void rollback() {
		doRollback=true;
	}

	@Override
	public void end() {
		if(doRollback){
			trans.rollback();
			if(autoManage)session.clear();
		}
		else{
			trans.commit();
			session.flush();
		}
	}
}
