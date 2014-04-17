package railo.commons.collection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import railo.runtime.exp.PageException;
import railo.runtime.type.util.StructUtil;


public class SyncMap<K,V>
        implements MapPro<K,V>, Serializable {
        private static final long serialVersionUID = 1978198479659022715L;

        private final MapPro<K,V> m;     // Backing Map
        final Serializable      mutex;        // Object on which to synchronize

        public SyncMap() {
            this(null);
        }
        
        public SyncMap(MapPro<K,V> m) {
            if (m==null) this.m = new HashMapPro<K, V>();
            else this.m = m;
            mutex = this;
        }

        SyncMap(MapPro<K,V> m, Serializable mutex) {
            this.m = m;
            this.mutex = mutex;
        }
        

        public int size() {
            synchronized (mutex) {return m.size();}
        }
        public boolean isEmpty() {
            synchronized (mutex) {return m.isEmpty();}
        }
        public boolean containsKey(Object key) {
            synchronized (mutex) {return m.containsKey(key);}
        }
        public boolean containsValue(Object value) {
            synchronized (mutex) {return m.containsValue(value);}
        }
        public V get(Object key) {
            synchronized (mutex) {return m.get(key);}
        }


		@Override
		public V g(K key) throws PageException {
			synchronized (mutex) {return m.g(key);}
		}

		@Override
		public V g(K key, V defaultValue) {
			synchronized (mutex) {return m.g(key,defaultValue);}
		}

		@Override
		public V r(K key) throws PageException {
			synchronized (mutex) {return m.r(key);}
		}

		@Override
		public V r(K key, V defaultValue) {
			synchronized (mutex) {return m.r(key,defaultValue);}
		}
        
        

        public V put(K key, V value) {
            synchronized (mutex) {return m.put(key, value);}
        }
        public V remove(Object key) {
            synchronized (mutex) {return m.remove(key);}
        }
        
        public void putAll(Map<? extends K, ? extends V> map) {
            synchronized (mutex) {m.putAll(map);}
        }
        public void clear() {
            synchronized (mutex) {m.clear();}
        }

        private transient Set<K> keySet = null;
        private transient Set<MapPro.Entry<K,V>> entrySet = null;
        private transient Collection<V> values = null;

        public Set<K> keySet() {
            synchronized (mutex) {
                if (keySet==null)
                    keySet = new SyncSet<K>(m.keySet(), mutex);
                return keySet;
            }
        }

        public Set<Map.Entry<K,V>> entrySet() {
            synchronized (mutex) {
                if (entrySet==null)
                    entrySet = new SyncSet<Map.Entry<K,V>>(m.entrySet(), mutex);
                return entrySet;
            }
        }

        public Collection<V> values() {
            synchronized (mutex) {
                if (values==null)
                    values = new SyncCollection<V>(m.values(), mutex);
                return values;
            }
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            synchronized (mutex) {return m.equals(o);}
        }
        public int hashCode() {
            synchronized (mutex) {return m.hashCode();}
        }
        public String toString() {
            synchronized (mutex) {return m.toString();}
        }
        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {s.defaultWriteObject();}
        }

		public int getType() {
			return StructUtil.getType(m);
		}
    }
