package railo.commons.lang;

import railo.runtime.PageSource;

/**
 * Stack for Page Sources
 */
public final class PageSourceStack {
    
    private Entry root=new Entry(null,null);
    private Entry current=root;
    
    /**
     * adds a page to stack
     * @param ps 
     */
    public void add(PageSource ps) {
        current=new Entry(current,ps);
    }
    
    /**
     * @return removed value
     */
    public PageSource remove() {
        PageSource ps = current.ps;
        current=current.entry;
        return ps;
    }
    
    
    class Entry {
        private Entry entry;
        private PageSource ps;

        Entry(Entry entry, PageSource ps) {
            this.entry=entry;
            this.ps=ps;
        }
    }
    /**
     * clear the stack
     */
    public void clear() {
        current=root;
    }

}