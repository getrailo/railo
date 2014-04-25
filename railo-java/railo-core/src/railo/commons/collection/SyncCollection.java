package railo.commons.collection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public class SyncCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 3053995032091335093L;

        final Collection<E> c;  // Backing Collection
        final Object mutex;     // Object on which to synchronize

        SyncCollection(Collection<E> c) {
            if (c==null)
                throw new NullPointerException();
            this.c = c;
            mutex = this;
        }
        SyncCollection(Collection<E> c, Object mutex) {
            this.c = c;
            this.mutex = mutex;
        }

        @Override
		public int size() {
            synchronized (mutex) {return c.size();}
        }
        @Override
		public boolean isEmpty() {
            synchronized (mutex) {return c.isEmpty();}
        }
        @Override
		public boolean contains(Object o) {
            synchronized (mutex) {return c.contains(o);}
        }
        @Override
		public Object[] toArray() {
            synchronized (mutex) {return c.toArray();}
        }
        @Override
		public <T> T[] toArray(T[] a) {
            synchronized (mutex) {return c.toArray(a);}
        }

        @Override
		public Iterator<E> iterator() {
            return c.iterator(); // Must be manually synched by user!
        }

        @Override
		public boolean add(E e) {
            synchronized (mutex) {return c.add(e);}
        }
        @Override
		public boolean remove(Object o) {
            synchronized (mutex) {return c.remove(o);}
        }

        @Override
		public boolean containsAll(Collection<?> coll) {
            synchronized (mutex) {return c.containsAll(coll);}
        }
        @Override
		public boolean addAll(Collection<? extends E> coll) {
            synchronized (mutex) {return c.addAll(coll);}
        }
        @Override
		public boolean removeAll(Collection<?> coll) {
            synchronized (mutex) {return c.removeAll(coll);}
        }
        @Override
		public boolean retainAll(Collection<?> coll) {
            synchronized (mutex) {return c.retainAll(coll);}
        }
        @Override
		public void clear() {
            synchronized (mutex) {c.clear();}
        }
        @Override
		public String toString() {
            synchronized (mutex) {return c.toString();}
        }
        private void writeObject(ObjectOutputStream s) throws IOException {
            synchronized (mutex) {s.defaultWriteObject();}
        }
    }