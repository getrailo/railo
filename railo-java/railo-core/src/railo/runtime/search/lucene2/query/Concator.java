package railo.runtime.search.lucene2.query;

import railo.commons.lang.StringUtil;

public final class Concator implements Op {
	
	private Op left;
	private Op right;

	public Concator(Op left,Op right) {
		this.left=left;
		this.right=right;
	}

	@Override
	public String toString() {
		if(left instanceof Literal && right instanceof Literal) {
			String str=((Literal)left).literal+" "+((Literal)right).literal;
			return "\""+StringUtil.replace(str, "\"", "\"\"", false)+"\"";
		}
		return left+" "+right;
	}
	
}
