package railo.runtime.sql.exp;

public abstract class ExpressionSupport implements Expression {

	private int index;
	private String alias;
	private boolean directionBackward;
	

	@Override
	public void setIndex(int index) {
		this.index=index;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public String getAlias() {
		if(alias==null) return "column_"+(getIndex()-1);
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public boolean hasAlias() {
		return alias!=null;
	}

	@Override
	public boolean hasIndex() {
		return index!=0;
	}

	@Override
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
