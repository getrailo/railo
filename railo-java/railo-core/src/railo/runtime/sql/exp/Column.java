package railo.runtime.sql.exp;

import railo.runtime.exp.PageException;
import railo.runtime.type.Query;

public interface Column extends Expression {
	public String getFullName();
	public String getColumn();
	public String getTable();
	public boolean hasBracked();
	public void hasBracked(boolean b);
	public int getColumnIndex();
	public Object getValue(Query qry, int row) throws PageException;
	public Object getValue(Query qry, int row, Object defaultValue);
	
}
