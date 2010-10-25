package railo.runtime.type.ref;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.StructImpl;

/**
 * represent a reference to a variable
 */
public final class VariableReference implements Reference { 
        
        private Collection coll; 
        private Collection.Key key; 



        /**
         * constructor of the class
         * @param coll Collection where variable is
         * @param key key to the value inside the collection
         */
        public VariableReference(Collection coll, String key) { 
                this.coll=coll; 
                this.key=KeyImpl.init(key); 
        } 

        /**
         * constructor of the class
         * @param coll Collection where variable is
         * @param key key to the value inside the collection
         */
        public VariableReference(Collection coll, Collection.Key key) { 
                this.coll=coll; 
                this.key=key; 
        } 
        
        /**
         * constructor of the class
         * @param o Object will be casted to Collection
         * @param key key to the value inside the collection
         * @throws PageException
         */
        public VariableReference(Object o, String key) throws PageException { 
            this(Caster.toCollection(o),key); 
        } 
        
        /**
         * constructor of the class
         * @param o Object will be casted to Collection
         * @param key key to the value inside the collection
         * @throws PageException
         */
        public VariableReference(Object o, Collection.Key key) throws PageException { 
            this(Caster.toCollection(o),key); 
        } 
        
        /**
         * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext)
         */
        public Object get(PageContext pc) throws PageException { 
            return get(); 
        } 
        private Object get() throws PageException { 
            if(coll instanceof Query) {
                return ((Query)coll).getColumn(key);
            }
            return coll.get(key); 
        } 
        
        /**
         *
         * @see railo.runtime.type.ref.Reference#get(railo.runtime.PageContext, java.lang.Object)
         */
        public Object get(PageContext pc, Object defaultValue) { 
            return get(defaultValue); 
        } 
        private Object get(Object defaultValue) { 
            if(coll instanceof Query) {
            	Object rtn=((Query)coll).getColumn(key,null);
            	if(rtn!=null)return rtn;
            	return defaultValue;
            }
            return coll.get(key,defaultValue); 
        } 
        
        
		/**
		 * @see railo.runtime.type.ref.Reference#set(railo.runtime.PageContext, java.lang.Object)
		 */
		public Object set(PageContext pc, Object value) throws PageException { 
			return coll.set(key,value); 
		} 
		public void set(double value) throws PageException { 
			coll.set(key,Caster.toDouble(value)); 
		} 

		/**
		 * @see railo.runtime.type.ref.Reference#setEL(railo.runtime.PageContext, java.lang.Object)
		 */
		public Object setEL(PageContext pc, Object value) { 
				return coll.setEL(key,value); 
		} 
        
        /**
         * @see railo.runtime.type.ref.Reference#touch(railo.runtime.PageContext)
         */
        public Object touch(PageContext pc) throws PageException {
            Object o;
            if(coll instanceof Query) {
                o= ((Query)coll).getColumn(key,null);
                if(o!=null) return o;
                return set(pc,new StructImpl());
            }
            o=coll.get(key,null); 
            if(o!=null) return o;
            return set(pc,new StructImpl());
        } 
        
        /**
         * @see railo.runtime.type.ref.Reference#touchEL(railo.runtime.PageContext)
         */
        public Object touchEL(PageContext pc) {
            Object o;
            if(coll instanceof Query) {
                o= ((Query)coll).getColumn(key,null);
                if(o!=null) return o;
                return setEL(pc,new StructImpl());
            }
            o=coll.get(key,null); 
            if(o!=null) return o;
            return setEL(pc,new StructImpl());
        } 
        
        /**
         * @see railo.runtime.type.ref.Reference#remove(PageContext pc)
         */
        public Object remove(PageContext pc) throws PageException { 
                return coll.remove(key); 
        } 
        
        /**
         * @see railo.runtime.type.ref.Reference#removeEL(railo.runtime.PageContext)
         */
        public Object removeEL(PageContext pc) { 
            return coll.removeEL(key); 
        } 

        /**
         * @see railo.runtime.type.ref.Reference#getParent()
         */
        public Object getParent() { 
            return coll; 
	    }
	    
        /**
         * @return return the parent as Collection
         */
        public Collection getCollection() { 
            return coll; 
	    }

		/**
		 *
		 * @see railo.runtime.type.ref.Reference#getKeyAsString()
		 */
		public String getKeyAsString() {
			return key.getString();
		}
		
		/**
		 *
		 * @see railo.runtime.type.ref.Reference#getKey()
		 */
		public Collection.Key getKey() {
			return key;
		}

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            try {
                return Caster.toString(get());
            } 
            catch (PageException e) {
                return super.toString();
            }
        }

        

        
} 