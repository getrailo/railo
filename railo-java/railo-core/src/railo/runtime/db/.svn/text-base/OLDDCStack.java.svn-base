package railo.runtime.db;

import java.sql.SQLException;
import java.util.Map;

import railo.runtime.op.Caster;

class OLDDCStack {

	private Item item;
	private Map transactionItem=null;
	private DatasourceConnectionPool pool;

	OLDDCStack(DatasourceConnectionPool pool) {
		this.pool=pool;
	}

	public synchronized void add(int pid,DatasourceConnection dc){
		if(pid==0)
			item=new Item(item,dc);
		else {
			
			transactionItem.put(Caster.toInteger(pid), dc);
		}
	}

	public synchronized DatasourceConnection get(int pid){
		DatasourceConnection rtn;
		if(pid!=0) {
			rtn = (DatasourceConnection) transactionItem.remove(Caster.toInteger(pid));
			if(rtn!=null) {
				try {
					if(!rtn.getConnection().isClosed())
						return rtn;
				} 
				catch (SQLException e) {}
			}
		}
		
		if(item==null) return null;
		rtn = item.dc;
		item=item.prev;
		try {
			if(!rtn.getConnection().isClosed())
				return rtn;
		} 
		catch (SQLException e) {}
		return null;
	}

	public synchronized boolean isEmpty(int pid){
		return item==null&&!transactionItem.containsKey(Caster.toInteger(pid));
	}
	
	public synchronized boolean _isEmpty(int pid){
		if(pid!=0) {
			return transactionItem.containsKey(Caster.toInteger(pid));
		}
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

	public synchronized void clear(int pid) {
		DatasourceConnection dc = (DatasourceConnection) transactionItem.remove(Caster.toInteger(pid));
		if(dc!=null)add(0, dc);
	}

	private void clear(Item current,Item next) throws SQLException {
		if(current==null) return;
		if((current.dc.isTimeout() || current.dc.getConnection().isClosed())) { 
			if(!current.dc.getConnection().isClosed()){
				try {
		            current.dc.getConnection().close();
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
	


	
	
	
	/*public int inc() {
		return ++count;
	}
	public boolean isWaiting() {
		return count>0;
	}
	public int dec() {
		return --count;
	}*/
}
