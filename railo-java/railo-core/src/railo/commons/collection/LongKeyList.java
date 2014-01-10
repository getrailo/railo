package railo.commons.collection;

/**
 * class to fill objects, objetcs will be sorted by long key.
 */
public final class LongKeyList {

	private final Pair root;
	
	/**
	 * constructor of the class
	 */
	public LongKeyList() {
		root=new Pair();
	}
	
	
	/**
	 * adds a new object to the stack
	 * @param key key as long
	 * @param value object to fill
	 */
	public void add(long key, Object value) {
		add(key, value, root);
	}

	/**
	 * @param key 
	 * @param value
	 * @param parent
	 */
	private void add(long key, Object value,Pair parent) {
		if(parent.value==null) parent.setData(key,value);
		else if(key<parent.key) add(key,value,parent.left);
		else add(key,value,parent.right);
		
	}

	/**
	 * @return returns the first object in stack
	 */
	public Object shift() {
		Pair oldest=root;
		while(oldest.left!=null && oldest.left.value!=null)oldest=oldest.left;
		Object rtn=oldest.value;
		oldest.copy(oldest.right);
		return rtn;
	}
	
	/**
	 * @return returns the last object in Stack
	 */
	public Object pop() {
		Pair oldest=root;
		while(oldest.right!=null && oldest.right.value!=null)oldest=oldest.right;
		Object rtn=oldest.value;
		oldest.copy(oldest.left);
		return rtn;
	}
	
	/**
	 * @param key key to value
	 * @return returns the value to the key
	 */
	public Object get(long key) {
		Pair current=root;
		while(true) {
			if(current==null || current.key==0) {
				return null;
			}
			else if(current.key==key) return current.value;
			else if(current.key<key) current=current.right;
			else if(current.key>key) current=current.left;
		}
	}
	
	class Pair {
		/**
		 * key for value
		 */
		public long key;
		/**
		 * value object
		 */
		public Object value;
		/**
		 * left side
		 */
		public Pair left;
		/**
		 * right side
		 */
		public Pair right;
		

		/**
		 * sets data to Pair
		 * @param key
		 * @param value
		 */
		public void setData(long key, Object value) {
			this.key=key;
			this.value=value;
			left=new Pair();
			right=new Pair();
		}
		
		/**
		 * @param pair
		 */
		public void copy(Pair pair) {
			if(pair!=null) {
				this.left=pair.left;
				this.right=pair.right;
				this.value=pair.value;
				this.key=pair.key;
			}
			else {
				this.left=null;
				this.right=null;
				this.value=null;
				this.key=0;
			}
		}
	}
}