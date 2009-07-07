package railo.runtime.sql.exp;

public abstract class ExpressionSupport implements Expression {

	private int index;
	private String alias;
	private boolean directionBackward;
	

	/**
	 *
	 * @see sql.exp.Expression#setIndex(int)
	 */
	public void setIndex(int index) {
		this.index=index;
	}

	/**
	 *
	 * @see sql.exp.Expression#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	/**
	 *
	 * @see sql.exp.Expression#getAlias()
	 */
	public String getAlias() {
		if(alias==null) return "column_"+(getIndex()-1);
		return alias;
	}

	/**
	 *
	 * @see sql.exp.Expression#setAlias(java.lang.String)
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 *
	 * @see sql.exp.Expression#hasAlias()
	 */
	public boolean hasAlias() {
		return alias!=null;
	}

	/**
	 *
	 * @see sql.exp.Expression#hasIndex()
	 */
	public boolean hasIndex() {
		return index!=0;
	}

	/**
	 *
	 * @see sql.exp.Expression#setDirectionBackward(boolean)
	 */
	public void setDirectionBackward(boolean b) {
		directionBackward=b;
	}

	/**
	 * @return the directionBackward
	 */
	public boolean isDirectionBackward() {
		return directionBackward;
	}
}
