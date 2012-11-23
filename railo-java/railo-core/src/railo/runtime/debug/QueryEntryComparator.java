package railo.runtime.debug;

import java.util.Comparator;



/**
 * 
 */
public final class QueryEntryComparator implements Comparator<QueryEntry> {

    @Override
    public int compare(QueryEntry o1, QueryEntry o2) {
        return compare((QueryEntryPro)o1,(QueryEntryPro)o2);
    }
    
    public int compare(QueryEntryPro qe1,QueryEntryPro qe2) {
        return (int)((qe2.getExecutionTime())-(qe1.getExecutionTime()));
    }
}