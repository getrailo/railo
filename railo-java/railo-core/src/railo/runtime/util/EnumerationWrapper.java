package railo.runtime.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * class to make a enumaration from a ser, map or iterator
 */
public final class EnumerationWrapper implements Enumeration {
		
		private Iterator it;

		/**
		 * @param map Constructor with a Map
		 */
		public EnumerationWrapper(Map map) {
			this(map.keySet().iterator());
		}
		
		/**
		 * @param set Constructor with a Set
		 */
		public EnumerationWrapper(Set set) {
			this(set.iterator());
		}

		/**
		 * Constructor of the class
		 * @param objs
		 */
		public EnumerationWrapper(Object[] objs) {
			this(new ArrayIterator(objs));
		}
		
		/**
		 * @param it Constructor with a iterator
		 */
		public EnumerationWrapper(Iterator it) {
			this.it=it;
		}
		

		/**
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		public boolean hasMoreElements() {
			return it.hasNext();
		}

		/**
		 * @see java.util.Enumeration#nextElement()
		 */
		public Object nextElement() {
			return it.next();
		}
		
	}