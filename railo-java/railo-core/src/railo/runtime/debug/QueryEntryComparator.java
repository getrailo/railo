package railo.runtime.debug;

import java.util.Comparator;



/**
 * 
 */
public final class QueryEntryComparator implements Comparator<QueryEntry> {

    @Override
    public int compare(QueryEntry qe1,QueryEntry qe2) {
        return (int)((qe2.getExecutionTime())-(qe1.getExecutionTime()));
    }
}