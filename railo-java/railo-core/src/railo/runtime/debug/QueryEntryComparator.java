package railo.runtime.debug;

import java.util.Comparator;



/**
 * 
 */
public final class QueryEntryComparator implements Comparator {

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        return compare((QueryEntry)o1,(QueryEntry)o2);
    }
    
    private int compare(QueryEntry qe1,QueryEntry qe2) {
        return (qe2.getExe())-(qe1.getExe());
    }
}