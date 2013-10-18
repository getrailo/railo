package railo.runtime.util;

import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.exp.DeprecatedException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryColumn;

/**
 * Stack for Query Objects
 */
public final class QueryStackImpl implements QueryStack {
	Query[] queries=new Query[20];
	int start=queries.length;
	
	@Override
	public QueryStack duplicate(boolean deepCopy){
		QueryStackImpl qs=new QueryStackImpl();
		if(deepCopy) {
			qs.queries=new Query[queries.length];
			for(int i=0;i<queries.length;i++) {
				qs.queries[i]=(Query)Duplicator.duplicate(queries[i],deepCopy);
			}
		}
		else qs.queries=queries;
		
		qs.start=start;
		return qs;
	}

	@Override
	public void addQuery(Query query) {
		if(start<1)grow();
        queries[--start]= query;
	}

	@Override
	public void removeQuery() {
        //print.ln("queries["+start+"]=null;");
        queries[start++]=null;
	}
	
	@Override
	public boolean isEmpty() {
		return start==queries.length;
	}

	@Override
	public Object getDataFromACollection(String key) {
		throw new PageRuntimeException(new DeprecatedException("this method is no longer supported, use instead getDataFromACollection(PageContext pc,Key key, Object defaultValue)"));
	}
	
	@Override
	public Object getDataFromACollection(PageContext pc,String key) {
		throw new PageRuntimeException(new DeprecatedException("this method is no longer supported, use instead getDataFromACollection(PageContext pc,Key key, Object defaultValue)"));
	}

	@Override
	public Object getDataFromACollection(Key key) {
		throw new PageRuntimeException(new DeprecatedException("this method is no longer supported, use instead getDataFromACollection(PageContext pc,Key key, Object defaultValue)"));
	}
	
	@Override
	public Object getDataFromACollection(PageContext pc,Key key) {
		throw new PageRuntimeException(new DeprecatedException("this method is no longer supported, use instead getDataFromACollection(PageContext pc,Key key, Object defaultValue)"));
	}
	
	// FUTURE add to interface and set above to deprecated
	public Object getDataFromACollection(PageContext pc,Key key, Object defaultValue) {
		//Object rtn;
		QueryColumn col;
		// get data from queries
		for(int i=start;i<queries.length;i++) {
			col = queries[i].getColumn(key,null);
			if(col!=null) return col.get(queries[i].getCurrentrow(pc.getId()),NullSupportHelper.empty());
			//rtn=((Objects)queries[i]).get(pc,key,Null.NULL);
			//if(rtn!=Null.NULL) return rtn;
		}
		return defaultValue;
	}

	@Override
	public QueryColumn getColumnFromACollection(String key) {
		return getColumnFromACollection(KeyImpl.init(key));
	}

	@Override
	public QueryColumn getColumnFromACollection(Key key) {
		QueryColumn rtn=null;
		
		// get data from queries
		for(int i=start;i<queries.length;i++) {
			rtn=queries[i].getColumn(key,null);
			if(rtn!=null) {
				return rtn;
			}
		}
		return null;
	}
	
	@Override
	public void clear() {
		for(int i=start;i<queries.length;i++) {
			queries[i]=null;
		}
		start=queries.length;
	}
    
    private void grow() {
        Query[] tmp=new Query[queries.length+20];
        for(int i=0;i<queries.length;i++) {
            tmp[i+20]=queries[i];
        }
        queries=tmp;
        start+=20;
    }
    
    @Override
	public Query[] getQueries() {
		Query[] tmp=new Query[queries.length-start];
		int count=0;
		for(int i=start;i<queries.length;i++) {
			tmp[count++]=queries[i];
		}
		return tmp;
	}
}