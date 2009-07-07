package railo.runtime.sql.exp;

public interface Column extends Expression {
	public String getFullName();
	public String getColumn();
	public String getTable();
	public boolean hasBracked();
	public void hasBracked(boolean b);
	public int getColumnIndex();
}
