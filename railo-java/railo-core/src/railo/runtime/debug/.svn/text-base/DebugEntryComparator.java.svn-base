package railo.runtime.debug;

import java.util.Comparator;



/**
 * 
 */
public final class DebugEntryComparator implements Comparator {

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        return compare((DebugEntry)o1,(DebugEntry)o2);
    }
    
    private int compare(DebugEntry de1,DebugEntry de2) {
        return (de2.getExeTime()+de2.getFileLoadTime())-(de1.getExeTime()+de1.getFileLoadTime());
    }
}