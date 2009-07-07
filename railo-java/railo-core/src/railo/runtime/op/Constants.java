package railo.runtime.op;


/**
 * Constant Values
 */
public final class Constants {
	/**
	 * Field <code>INTEGER_ZERO</code> equals new Integer(0)
	 */
	public static final Integer INTEGER_ZERO=new Integer(0);
	/**
	 * Field <code>INTEGER_ONE</code> equals new Integer(1)
	 */
	public static final Integer INTEGER_ONE=new Integer(1);
	
	public static final Integer INTEGER_MINUS_ONE=new Integer(-1);
	/**
	 * Field <code>INTEGER_TWO</code> equals new Integer(8)
	 */
	public static final Integer INTEGER_TWO=new Integer(2);
	/**
	 * Field <code>INTEGER_THREE</code> equals new Integer(3)
	 */
	public static final Integer INTEGER_THREE=new Integer(3);
	/**
	 * Field <code>INTEGER_FOUR</code> equals new Integer(4)
	 */
	public static final Integer INTEGER_FOUR=new Integer(4);
	/**
	 * Field <code>INTEGER_FIVE</code> equals new Integer(5)
	 */
	public static final Integer INTEGER_FIVE=new Integer(5);
	/**
	 * Field <code>INTEGER_SIX</code> equals new Integer(6)
	 */
	public static final Integer INTEGER_SIX=new Integer(6);
	/**
	 * Field <code>INTEGER_SEVEN</code> equals new Integer(7)
	 */
	public static final Integer INTEGER_SEVEN=new Integer(7);
	/**
	 * Field <code>INTEGER_EIGHT</code> equals new Integer(8)
	 */
	public static final Integer INTEGER_EIGHT=new Integer(8);
	/**
	 * Field <code>INTEGER_NINE</code> equals new Integer(9)
	 */
	public static final Integer INTEGER_NINE=new Integer(9);
	/**
	 * Field <code>INTEGER_NINE</code> equals new Integer(9)
	 */
	public static final Integer INTEGER_TEN=new Integer(10);
	

	public static final Double DOUBLE_ZERO = new Double(0);
    
    /**
     * Field <code>INTEGER</code>
     */
    public static final Integer[] INTEGER=new Integer[]{
        INTEGER_ZERO,
        INTEGER_ONE,
        INTEGER_TWO,
        INTEGER_THREE,
        INTEGER_FOUR,
        INTEGER_FIVE,
        INTEGER_SIX,
        INTEGER_SEVEN,
        INTEGER_EIGHT,
        INTEGER_NINE,
        new Integer(10),
        new Integer(11),
        new Integer(12),
        new Integer(13),
        new Integer(14),
        new Integer(15),
        new Integer(16),
        new Integer(17),
        new Integer(18),
        new Integer(19),
        new Integer(20),
        new Integer(21),
        new Integer(22),
        new Integer(23),
        new Integer(24),
        new Integer(25),
        new Integer(26),
        new Integer(27),
        new Integer(28),
        new Integer(29),
        new Integer(30),
        new Integer(31),
        new Integer(32),
        new Integer(33),
        new Integer(34),
        new Integer(35),
        new Integer(36),
        new Integer(37),
        new Integer(38),
        new Integer(39),
        new Integer(40),
        new Integer(41),
        new Integer(42),
        new Integer(43),
        new Integer(44),
        new Integer(45),
        new Integer(46),
        new Integer(47),
        new Integer(48),
        new Integer(49),
        new Integer(50),
        new Integer(51),
        new Integer(52),
        new Integer(53),
        new Integer(54),
        new Integer(55),
        new Integer(56),
        new Integer(57),
        new Integer(58),
        new Integer(59),
        new Integer(60),
        new Integer(61),
        new Integer(62),
        new Integer(63),
        new Integer(64),
        new Integer(65),
        new Integer(66),
        new Integer(67),
        new Integer(68),
        new Integer(69),
        new Integer(70),
        new Integer(71),
        new Integer(72),
        new Integer(73),
        new Integer(74),
        new Integer(75),
        new Integer(76),
        new Integer(77),
        new Integer(78),
        new Integer(79),
        new Integer(80),
        new Integer(81),
        new Integer(82),
        new Integer(83),
        new Integer(84),
        new Integer(85),
        new Integer(86),
        new Integer(87),
        new Integer(88),
        new Integer(89),
        new Integer(90),
        new Integer(91),
        new Integer(92),
        new Integer(93),
        new Integer(94),
        new Integer(95),
        new Integer(96),
        new Integer(97),
        new Integer(98),
        new Integer(99)
    };

    /**
     * return a Integer object with same value
     * @param i
     * @return Integer Object
     */
    public static Integer Integer(int i) {
        if(i>-1 && i<100) return INTEGER[i];
        return new Integer(i);
    }

    /**
     * return a Boolean object with same value
     * @param b
     * @return Boolean Object
     */
    public static Boolean Boolean(boolean b) {
        return b?Boolean.TRUE:Boolean.FALSE;
    }
    
}