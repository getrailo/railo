package railo.runtime.cache.tag.query;

import railo.commons.io.res.util.UDFFilterSupport;
import railo.runtime.cache.tag.CacheHandlerFilter;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.UDF;

public class QueryCacheHandlerFilterUDF extends UDFFilterSupport implements CacheHandlerFilter {
	
	private UDF udf;

	public QueryCacheHandlerFilterUDF(UDF udf) throws ExpressionException{
		super(udf);
		this.udf=udf;
	}

	@Override
    public boolean accept(Object obj) {
		if(!(obj instanceof Query)) return false;
		
    	args[0]=((Query)obj).getSql();
    	try {
			return Caster.toBooleanValue(udf.call(ThreadLocalPageContext.get(), args, true));
		} 
    	catch (PageException e) {
			throw new PageRuntimeException(e);
		}
    }

}