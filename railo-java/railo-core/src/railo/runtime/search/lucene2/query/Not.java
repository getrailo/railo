package railo.runtime.search.lucene2.query;


public final class Not implements Op {
	private Object left;
	private Object right;

	public Not(Object left, Object right) {
		this.left=left;
		this.right=right;
	}

	@Override
	public String toString() {
		return left+" NOT "+right;
	}
}
