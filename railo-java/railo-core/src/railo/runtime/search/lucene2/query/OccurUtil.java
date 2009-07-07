package railo.runtime.search.lucene2.query;

import org.apache.lucene.search.BooleanClause.Occur;


public class OccurUtil {

	public static Occur toOccur(boolean required, boolean prohibited) {
		if(required && !prohibited)		return Occur.MUST;
		if(!required && !prohibited)	return Occur.SHOULD;
		if(!required && prohibited)		return Occur.MUST_NOT;
		throw new RuntimeException("invalid Occur definition (required and prohibited)");
	}

}
