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

/**
 * a single entry of the query cache
 */
public final class QueryCacheEntry implements Serializable,Dumpable {
        private Object value;
        private Date cacheBefore;

        /**
         * constructor of the class
         * @param cacheBefore
         * @param query
         */
        QueryCacheEntry(Date cacheBefore, Object value) {
            this.cacheBefore=cacheBefore;
            this.value = Duplicator.duplicate(value,false);
        }

        /**
         * @param cacheAfter 
         * @return is in range or not
         */
        public boolean isInCacheRange(Date cacheAfter) {
            if(cacheBefore!=null && cacheBefore.getTime()>System.currentTimeMillis() ){
            	return true;
            }
            if(cacheAfter!=null && cacheAfter.getTime()<System.currentTimeMillis() ){
            	return true;
            }
            return false;
        }

        /**
         * @return returns query object in entry
         */
        public Object getValue() {
        	return Duplicator.duplicate(value,false);
        }
        /**
         * @return Returns the cacheBefore.
         */
        public Date getCacheBefore() {
            return cacheBefore;
        }

		public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties properties) {
			DumpTable table = new DumpTable("#669999","#ccffff","#000000");
			table.setTitle("QueryCacheEntry");
			table.appendRow(1,new SimpleDumpData("Value"),DumpUtil.toDumpData(value, pageContext, maxlevel, properties));
			table.appendRow(1,new SimpleDumpData("CacheBefore"),DumpUtil.toDumpData(cacheBefore, pageContext, maxlevel, properties));
			return table;
		}
    }