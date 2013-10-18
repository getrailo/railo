package railo.runtime.db;

import java.sql.SQLException;

import railo.commons.lang.SystemOut;
import railo.runtime.PageContext;

class DCStack {

	private Item item;
	
	DCStack() {
	}

	public synchronized void add(DatasourceConnection dc){
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

	public synchronized DatasourceConnection get(PageContext pc){
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

	public synchronized boolean isEmpty(){
		return item==null;
	}

	public synchronized int size(){
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
