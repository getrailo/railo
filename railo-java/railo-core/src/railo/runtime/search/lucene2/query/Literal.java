package railo.runtime.search.lucene2.query;



public final class Literal implements Op {
	
	String literal;

	public Literal(String literal) {
		this.literal=literal;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return literal;
	}

	public void set(String literal) {
		this.literal=literal;
	}
}
