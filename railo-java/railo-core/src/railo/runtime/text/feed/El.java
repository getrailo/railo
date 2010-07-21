package railo.runtime.text.feed;

public class El {

	public static short QUANTITY_0_1=0;
	public static short QUANTITY_0_N=4;
	public static short QUANTITY_1=8;
	public static short QUANTITY_1_N=16;
	public static final short QUANTITY_AUTO = QUANTITY_0_1;
	
	private Attr[] attrs;
	private short quantity;
	private boolean hasChildren;


	public El(short quantity,Attr[] attrs,boolean hasChildren) {
		this.quantity=quantity;
		this.attrs=attrs;
		this.hasChildren=hasChildren;
	}
	
	public El(short quantity,Attr[] attrs) {
		this(quantity,attrs,false);
	}
	
	public El(short quantity,Attr attr,boolean hasChildren) {
		this(quantity,new Attr[]{attr},hasChildren);
	}
	
	public El(short quantity,Attr attr) {
		this(quantity,new Attr[]{attr});
	}

	public El(short quantity,boolean hasChildren) {
		this(quantity, (Attr[])null,hasChildren);
	}
	public El(short quantity) {
		this(quantity, (Attr[])null);
	}

	/**
	 * @return the hasChildren
	 */
	public boolean isHasChildren() {
		return hasChildren;
	}
	
	/**
	 * @return the attrs
	 */
	public Attr[] getAttrs() {
		return attrs;
	}
	/**
	 * @return the quantity
	 */
	public short getQuantity() {
		return quantity;
	}
	public boolean isQuantity(short quantity) {
		return this.quantity==quantity;
	}

}
