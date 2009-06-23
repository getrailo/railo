

package railo.runtime.query;

import java.util.Date;

/**
 * a single entry of the query cache
 */
public final class QueryCacheEntry {
        private Object value;
        private Date cacheBefore;

        /**
         * constructor of the class
         * @param cacheBefore
         * @param query
         */
        QueryCacheEntry(Date cacheBefore, Object value) {
            this.cacheBefore=cacheBefore;
            this.value=value;
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
        Object getValue() {
            return value;
        }
        /**
         * @return Returns the cacheBefore.
         */
        public Date getCacheBefore() {
            return cacheBefore;
        }

        /**
         * sets the query value.
         * @param query The query to set.
         */
        void setValue(Object value) {
            this.value = value;
        }
    }