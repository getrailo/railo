package railo.runtime.sql.exp;

import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;
import railo.runtime.type.util.QueryUtil;


public class ColumnExpression extends ExpressionSupport implements Column {

	private String table;
	private String column;
	private boolean hasBracked;
	private int columnIndex;
	private QueryColumn col;

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

	@Override
	public String getFullName() {
		if(table==null) return column;
		return table+"."+column;
	}

	@Override
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
	
	public Object getValue(Query qr, int row) throws PageException {
		if(col==null)col = qr.getColumn(getColumn());
		return QueryUtil.getValue(col,row);
	}
	
	public Object getValue(Query qr, int row, Object defaultValue) {
		if(col==null){
			col = qr.getColumn(getColumn(),null);
			if(col==null) return defaultValue;
		}
		return col.get(row,defaultValue);
	}

}
