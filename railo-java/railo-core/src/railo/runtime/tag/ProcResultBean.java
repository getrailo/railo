package railo.runtime.tag;

public final class ProcResultBean {
	private String name;
	private int resultset=1;
	private int maxrows=-1;
	
	/**
	 * @return Returns the maxrows.
	 */
	public int getMaxrows() {
		return maxrows;
	}
	/**
	 * @param maxrows The maxrows to set.
	 */
	public void setMaxrows(int maxrows) {
		this.maxrows = maxrows;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the resultset.
	 */
	public int getResultset() {
		return resultset;
	}
	/**
	 * @param resultset The resultset to set.
	 */
	public void setResultset(int resultset) {
		this.resultset = resultset;
	}
	
}