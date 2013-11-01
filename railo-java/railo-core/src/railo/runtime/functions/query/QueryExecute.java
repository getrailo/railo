package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.db.SQL;
import railo.runtime.exp.DatabaseException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.tag.util.QueryParamConverter;
import railo.runtime.type.Query;

public class QueryExecute extends BIF {
	
	public static Query call(PageContext pc, String strSQL, Object params, String datasource) throws PageException {
		
		railo.runtime.tag.Query qry=new railo.runtime.tag.Query();
		
		if(params!=null) {
			SQL sql;
			if(Decision.isArray(params))
				sql=QueryParamConverter.convert(strSQL, Caster.toArray(params));
			else if(Decision.isStruct(params))
				sql=QueryParamConverter.convert(strSQL, Caster.toStruct(params));
			else
				throw new DatabaseException("value of the argument [params] has to be a struct or a array",null,null,null);
			
			//new QueryImpl(pc,)
		}
		return null;
    }

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		// TODO Auto-generated method stub
		return null;
	}

}
