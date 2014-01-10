package railo.commons.collection;

import java.util.Set;

public class SyncSet<E>
          extends SyncCollection<E>
          implements Set<E> {
        private static final long serialVersionUID = 487447009682186044L;

        public SyncSet(Set<E> s) {
            super(s);
        }
        public SyncSet(Set<E> s, Object mutex) {
            super(s, mutex);
        }

        public boolean equals(Object o) {
            if (this == o)
                return true;
            synchronized (mutex) {return c.equals(o);}
        }
        public int hashCode() {
            synchronized (mutex) {return c.hashCode();}
        }
    }