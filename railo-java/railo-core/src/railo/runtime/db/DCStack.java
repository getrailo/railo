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
package railo.runtime.db;

import java.sql.SQLException;

import railo.commons.lang.SystemOut;
import railo.runtime.PageContext;

class DCStack {

	private Item item;
	
	DCStack() {
	}

	public void add(DatasourceConnection dc){
		// make sure the connection is not already in stack, this can happen when the conn is released twice
		Item test = item;
		while(test!=null){
			if(test.dc==dc) {
				SystemOut.print("a datasource connection was released twice!");
				return;
			}
			test=test.prev;
		}
		
		item=new Item(item,dc);
	}

	public DatasourceConnection get(PageContext pc){
		if(item==null) return null;
		DatasourceConnection rtn = item.dc;
		item=item.prev;
		try {
			
			if(!rtn.getConnection().isClosed()){
				return rtn;
			}
			return get(pc);
		} 
		catch (SQLException e) {}
		return null;
	}

	public boolean isEmpty(){
		return item==null;
	}

	public int size(){
		int count=0;
		Item i = item;
		while(i!=null){
			count++;
			i=i.prev;
		}
		return count;
	}
	
	public int openConnections(){
		int count=0;
		Item i = item;
		while(i!=null){
			try {
				if(!i.dc.getConnection().isClosed())count++;
			} 
			catch (SQLException e) {}
			i=i.prev;
		}
		return count;
	}
	
	class Item {
		private DatasourceConnection dc;
		private Item prev;
		private int count=1;
		
		public Item(Item item,DatasourceConnection dc) {
			this.prev=item;
			this.dc=dc;
			if(prev!=null)count=prev.count+1;
		}

		public String toString(){
			return "("+prev+")<-"+count;
		}
	}

	public synchronized void clear() {
		try {
			clear(item,null);
		} 
		catch (SQLException e) {}
	}

	private void clear(Item current,Item next) throws SQLException {
		if(current==null) return;
		if((current.dc.isTimeout() || current.dc.getConnection().isClosed())) { 
			if(!current.dc.getConnection().isClosed()){
				try {
					current.dc.close();
		        } 
		        catch (SQLException e) {}
			}
	        
	        if(next==null)item=current.prev;
	        else {
	        	next.prev=current.prev;
	        }
	        clear(current.prev,next);
		}
		else clear(current.prev,current);
	}
}
