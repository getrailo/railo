package railo.runtime.type;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.type.Collection.Key;

/**
 * interface that define that in a class a iterator is available
 */
public interface Iteratorable {

    /**
     * @return return a Iterator for Keys as Collection.Keys
     */
    public Iterator<Collection.Key> keyIterator();
    
    /**
     * @return return a Iterator for Keys as String
     */
    public Iterator<String> keysAsStringIterator();
    
    /**
     *
     * @return return a Iterator for Values
     */
    public Iterator<Object> valueIterator();
    

    public Iterator<Entry<Key, Object>> entryIterator();
    
    /**
     * @return return a Iterator for keys
     * @deprecated use instead <code>{@link #keyIterator()}</code>
     */
    public Iterator iterator();
    
}