package railo.runtime.query;

import java.io.Serializable;
import java.util.Date;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.DumpUtil;
import railo.runtime.dump.Dumpable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.op.Duplicator;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * a single entry of the query cache
 */
public final class QueryCacheEntry implements Serializable,Dumpable {
    	
	private static final long serialVersionUID = -8991589914700895254L;
	
		private Object value;
		private long creationDate;

        /**
         * constructor of the class
         * @param cacheBefore
         * @param query
         */
        QueryCacheEntry(Date cacheBefore, Object value) {
            this.value = Duplicator.duplicate(value,false);
            this.creationDate=System.currentTimeMillis();
        }

        /**
         * @param cacheAfter 
         * @return is in range or not
         */
        public boolean isCachedAfter(Date cacheAfter) {
        	if(cacheAfter==null) return true;
        	if(creationDate>=cacheAfter.getTime()){
            	return true;
            }
            return false;
        }

        /**
         * @return returns query object in entry
         */
        public Object getValue() {
            this.creationDate=System.currentTimeMillis();
        	return Duplicator.duplicate(value,false);
        }

		@Override
		public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
			DumpTable table = new DumpTable("#669999","#ccffff","#000000");
			table.setTitle("QueryCacheEntry");
			table.appendRow(1,new SimpleDumpData("Value"),DumpUtil.toDumpData(value, pageContext, maxlevel, properties));
			table.appendRow(1,new SimpleDumpData("Creation Date"),DumpUtil.toDumpData(new DateTimeImpl(creationDate,false), pageContext, maxlevel, properties));
			return table;
		}
    }