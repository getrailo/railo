package railo.runtime.type.it;

import java.util.Iterator;

import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class ForEachQueryIterator implements Iterator {

	private Query qry;
	private int pid;
	private int start,current=0;


	public ForEachQueryIterator(Query qry, int pid){
		this.qry=qry;
		this.pid=pid;
		this.start=qry.getCurrentrow(pid);
	}
	
	@Override
	public boolean hasNext() {
		return current<qry.getRecordcount();
	}

	@Override
	public Object next() {
		try {
			if(qry.go(++current,pid)) {
				Struct sct=new StructImpl();
				Key[] names = qry.getColumnNames();
				for(int i=0;i<names.length;i++){
					sct.setEL(names[i], qry.get(names[i],null));
				}
				return sct;
			}
		} catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
		return null;
	}

	@Override
	public void remove() {
		try {
			qry.removeRow(current);
		} catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}

	public void reset() throws PageException {
		qry.go(start,pid);
	}

}
