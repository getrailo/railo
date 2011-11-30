package railo.runtime.type;

import railo.runtime.db.SQL;
import railo.runtime.exp.ExpressionException;

// FUTURE move everything to interface query and delete querypro
public interface QueryPro extends Query {
	
	//FUTURE change bytecode; set getCurrentrow() to deprecated
	public int getCurrentrow(int pid);
	
	public boolean next(int pid);
	
	public boolean previous(int pid);
	
	public void reset(int pid);
	
	public boolean go(int index, int pid);
	
	public void rename(Collection.Key columnName,Collection.Key newColumnName) throws ExpressionException;
	
	public Collection.Key[] getColumnNames();
	
	public String[] getColumnNamesAsString();
	
	public Query getGeneratedKeys();
	
	public SQL getSql();
}
