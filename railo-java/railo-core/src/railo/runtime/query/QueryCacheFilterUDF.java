package railo.runtime.query;

import railo.commons.io.res.util.UDFFilterSupport;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.UDF;

public class QueryCacheFilterUDF extends UDFFilterSupport implements QueryCacheFilter {

	public QueryCacheFilterUDF(UDF udf) throws ExpressionException{
		super(udf);
	}
	
    @Override
    public boolean accept(String sql) {
    	args[0]=sql;
    	try {
			return Caster.toBooleanValue(udf.call(ThreadLocalPageContext.get(), args, true));
			
		} 
    	catch (PageException e) {
			throw new PageRuntimeException(e);
		}
    }
}
