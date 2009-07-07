package railo.runtime.sql.exp;


public class ColumnExpression extends ExpressionSupport implements Column {

	private String table;
	private String column;
	private boolean hasBracked;
	private int columnIndex;

	public ColumnExpression(String value, int columnIndex) {
		this.column=value;
		this.columnIndex=columnIndex;
	}

	public void setSub(String sub) {
		if(table==null) {
			table=column;
			column=sub;
		}
		else column=(column+"."+sub);
	}

	public String toString(boolean noAlias) {
		if(hasAlias() && !noAlias) return getFullName()+" as "+getAlias();
		return getFullName();
	}

	/**
	 *
	 * @see sql.exp.Column#getFullName()
	 */
	public String getFullName() {
		if(table==null) return column;
		return table+"."+column;
	}

	/**
	 *
	 * @see railo.runtime.sql.exp.ExpressionSupport#getAlias()
	 */
	public String getAlias() {
		if(!hasAlias()) return getColumn();
		return super.getAlias();
	}

	public String getColumn() {
		return column;
	}

	public String getTable() {
		return table;
	}

	public boolean hasBracked() {
		return hasBracked;
	}

	public void hasBracked(boolean b) {
		this.hasBracked=b;
	}

	/**
	 * @return the columnIndex
	 */
	public int getColumnIndex() {
		return columnIndex;
	}

}
