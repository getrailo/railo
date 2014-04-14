package railo.runtime.type;

public interface QueryColumnPro extends QueryColumn,Array {
	public QueryColumnPro cloneColumn(Query query, boolean deepCopy);
	public void setKey(Collection.Key key);
	public QueryColumnPro toDebugColumn();

}
