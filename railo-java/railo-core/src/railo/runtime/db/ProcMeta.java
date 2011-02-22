package railo.runtime.db;


public class ProcMeta {
	public int columnType;
	public int dataType;
	
	public ProcMeta(int columnType, int dataType) {
		this.columnType=columnType;
		this.dataType=dataType;
	}
}