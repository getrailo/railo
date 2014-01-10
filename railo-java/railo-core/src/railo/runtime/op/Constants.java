package railo.runtime.op;


/**
 * Constant Values
 */
public final class Constants {
	
	public static final Object[] EMPTY_OBJECT_ARRAY=new Object[0];
	
	
	/**
	 * Field <code>INTEGER_ZERO</code> equals Integer.valueOf(0)
	 */
	public static final Integer INTEGER_0=Integer.valueOf(0);
	/**
	 * Field <code>INTEGER_ONE</code> equals Integer.valueOf(1)
	 */
	public static final Integer INTEGER_1=Integer.valueOf(1);
	
	public static final Integer INTEGER_MINUS_ONE=Integer.valueOf(-1);
	/**
	 * Field <code>INTEGER_TWO</code> equals Integer.valueOf(8)
	 */
	public static final Integer INTEGER_2=Integer.valueOf(2);
	/**
	 * Field <code>INTEGER_THREE</code> equals Integer.valueOf(3)
	 */
	public static final Integer INTEGER_3=Integer.valueOf(3);
	/**
	 * Field <code>INTEGER_FOUR</code> equals Integer.valueOf(4)
	 */
	public static final Integer INTEGER_4=Integer.valueOf(4);
	/**
	 * Field <code>INTEGER_FIVE</code> equals Integer.valueOf(5)
	 */
	public static final Integer INTEGER_5=Integer.valueOf(5);
	/**
	 * Field <code>INTEGER_SIX</code> equals Integer.valueOf(6)
	 */
	public static final Integer INTEGER_6=Integer.valueOf(6);
	/**
	 * Field <code>INTEGER_SEVEN</code> equals Integer.valueOf(7)
	 */
	public static final Integer INTEGER_7=Integer.valueOf(7);
	/**
	 * Field <code>INTEGER_EIGHT</code> equals Integer.valueOf(8)
	 */
	public static final Integer INTEGER_8=Integer.valueOf(8);
	/**
	 * Field <code>INTEGER_NINE</code> equals Integer.valueOf(9)
	 */
	public static final Integer INTEGER_9=Integer.valueOf(9);
	/**
	 * Field <code>INTEGER_NINE</code> equals Integer.valueOf(9)
	 */
	public static final Integer INTEGER_10=Integer.valueOf(10);
	public static final Integer INTEGER_11=Integer.valueOf(11);
	public static final Integer INTEGER_12=Integer.valueOf(12);
	

	public static final short SHORT_VALUE_ZERO = (short)0;
	public static final Short SHORT_ZERO = Short.valueOf((short)0);
	public static final Long LONG_ZERO = Long.valueOf(0);
	public static final Double DOUBLE_ZERO = new Double(0);
    
    
	
    /**
     * return a Integer object with same value
     * @param i
     * @return Integer Object
     * @deprecated use Integer.valueOf() instead
     */
    public static Integer Integer(int i) {
        //if(i>-1 && i<100) return INTEGER[i];
        return Integer.valueOf(i);
    }

    /**
     * return a Boolean object with same value
     * @param b
     * @return Boolean Object
     * @deprecated use Boolean.valueOf() instead
     */
    public static Boolean Boolean(boolean b) {
        return b?Boolean.TRUE:Boolean.FALSE;
    }
    
}