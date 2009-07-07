package railo.runtime.sql;

import java.util.ArrayList;
import java.util.List;

import railo.runtime.sql.exp.Column;
import railo.runtime.sql.exp.Expression;
import railo.runtime.sql.exp.op.Operation;
import railo.runtime.sql.exp.value.ValueNumber;

public class Select {
	private List selects=new ArrayList();
	private List froms=new ArrayList();
	private Operation where;
	private List groupbys=new ArrayList();
	private Operation having;
	private ValueNumber top;
	private boolean distinct;
	private boolean unionDistinct;
	
	public void addSelectExpression(Expression select) {
		selects.add(select);
		select.setIndex(selects.size());
	}

	public void addFromExpression(Column exp) {
		froms.add(exp);
		exp.setIndex(froms.size());
	}

	public void setWhereExpression(Operation where) {
		this.where=where;
	}
	
	public void addGroupByExpression(Column col) {
		this.groupbys.add(col);
	}

	public void setTop(ValueNumber top) {
		this.top=top;
	}


	/**
	 * @return the froms
	 */
	public Column[] getFroms() {
		return (Column[]) froms.toArray(new Column[froms.size()]);
	}
	
	/**
	 * @return the groupbys
	 */
	public Column[] getGroupbys() {
		if(groupbys==null) return new Column[0];
		return (Column[]) groupbys.toArray(new Column[groupbys.size()]);
	}

	/**
	 * @return the havings
	 */
	public Operation getHaving() {
		return having;
	}

	/**
	 * @return the selects
	 */
	public Expression[] getSelects() {
		return (Expression[]) selects.toArray(new Expression[selects.size()]);
	}

	/**
	 * @return the where
	 */
	public Operation getWhere() {
		return where;
	}

	public boolean isUnionDistinct() {
		return unionDistinct; 
	}
	
	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean b) {
		this.distinct=b;
	}

	public void setUnionDistinct(boolean b) {
		//print.out("-"+b);
		this.unionDistinct=b;
	}

	/**
	 * @param having the having to set
	 */
	public void setHaving(Operation having) {
		this.having = having;
	}

	/**
	 * @return the top
	 */
	public ValueNumber getTop() {
		return top;
	}


}
