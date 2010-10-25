package railo.runtime.search.lucene2.query;


public final class And implements Op {
	private Object left;
	private Object right;

	public And(Object left, Object right) {
		this.left=left;
		this.right=right;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return left+" AND "+right;
	}
}
