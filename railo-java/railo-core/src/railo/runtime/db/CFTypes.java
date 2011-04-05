package railo.runtime.db;

import java.sql.Types;


public interface CFTypes {
	//public static final int BFILE=-13;//OracleTypes.BFILE;
	public static final int CURSOR=-10;//OracleTypes.CURSOR;
	public static final int OPAQUE=2007;
	public static final int BFILE=-13;
	public static final int BINARY_DOUBLE=101;
	public static final int BINARY_FLOAT=100;
	public static final int FIXED_CHAR=999;
	public static final int INTERVALDS=-104;
	public static final int INTERVALYM=-103;
	public static final int JAVA_STRUCT=2008;
	public static final int NUMBER=Types.NUMERIC;
	public static final int PLSQL_INDEX_TABLE=-14;
	public static final int RAW=-2;
	public static final int ROWID=-8;
	public static final int TIMESTAMPLTZ=-102;
	public static final int TIMESTAMPNS=-100;
	public static final int TIMESTAMPTZ=-101;
	public static final int VARCHAR2=-100;
	
	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>BIT</code>.
	 */
		public final static int BIT 		=  -7;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>TINYINT</code>.
	 */
		public final static int TINYINT 	=  -6;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>SMALLINT</code>.
	 */
		public final static int SMALLINT	=   5;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>INTEGER</code>.
	 */
		public final static int INTEGER 	=   4;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>BIGINT</code>.
	 */
		public final static int BIGINT 		=  -5;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>FLOAT</code>.
	 */
		public final static int FLOAT 		=   6;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>REAL</code>.
	 */
		public final static int REAL 		=   7;


	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>DOUBLE</code>.
	 */
		public final static int DOUBLE 		=   8;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>NUMERIC</code>.
	 */
		public final static int NUMERIC 	=   2;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>DECIMAL</code>.
	 */
		public final static int DECIMAL		=   3;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>CHAR</code>.
	 */
		public final static int CHAR		=   1;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>VARCHAR</code>.
	 */
		public final static int VARCHAR 	=  12;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>LONGVARCHAR</code>.
	 */
		public final static int LONGVARCHAR 	=  -1;


	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>DATE</code>.
	 */
		public final static int DATE 		=  91;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>TIME</code>.
	 */
		public final static int TIME 		=  92;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>TIMESTAMP</code>.
	 */
		public final static int TIMESTAMP 	=  93;


	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>BINARY</code>.
	 */
		public final static int BINARY		=  -2;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>VARBINARY</code>.
	 */
		public final static int VARBINARY 	=  -3;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>LONGVARBINARY</code>.
	 */
		public final static int LONGVARBINARY 	=  -4;

	/**
	 * <P>The constant in the Java programming language, sometimes referred
	 * to as a type code, that identifies the generic SQL type 
	 * <code>NULL</code>.
	 */
		public final static int NULL		=   0;

	    /**
	     * The constant in the Java programming language that indicates
	     * that the SQL type is database-specific and
	     * gets mapped to a Java object that can be accessed via
	     * the methods <code>getObject</code> and <code>setObject</code>.
	     */
		public final static int OTHER		= 1111;

	        

	    /**
	     * The constant in the Java programming language, sometimes referred to
	     * as a type code, that identifies the generic SQL type
	     * <code>JAVA_OBJECT</code>.
	     * @since 1.2
	     */
	        public final static int JAVA_OBJECT         = 2000;

	    /**
	     * The constant in the Java programming language, sometimes referred to
	     * as a type code, that identifies the generic SQL type
	     * <code>DISTINCT</code>.
	     * @since 1.2
	     */
	        public final static int DISTINCT            = 2001;
		
	    /**
	     * The constant in the Java programming language, sometimes referred to
	     * as a type code, that identifies the generic SQL type
	     * <code>STRUCT</code>.
	     * @since 1.2
	     */
	        public final static int STRUCT              = 2002;

	    /**
	     * The constant in the Java programming language, sometimes referred to
	     * as a type code, that identifies the generic SQL type
	     * <code>ARRAY</code>.
	     * @since 1.2
	     */
	        public final static int ARRAY               = 2003;

	    /**
	     * The constant in the Java programming language, sometimes referred to
	     * as a type code, that identifies the generic SQL type
	     * <code>BLOB</code>.
	     * @since 1.2
	     */
	        public final static int BLOB                = 2004;

	    /**
	     * The constant in the Java programming language, sometimes referred to
	     * as a type code, that identifies the generic SQL type
	     * <code>CLOB</code>.
	     * @since 1.2
	     */
	        public final static int CLOB                = 2005;

	    /**
	     * The constant in the Java programming language, sometimes referred to
	     * as a type code, that identifies the generic SQL type
	     * <code>REF</code>.
	     * @since 1.2
	     */
	        public final static int REF                 = 2006;
	        
	    /**
	     * The constant in the Java programming language, somtimes referred to
	     * as a type code, that identifies the generic SQL type <code>DATALINK</code>.
	     *
	     * @since 1.4
	     */
	    public final static int DATALINK = 70;

	    /**
	     * The constant in the Java programming language, somtimes referred to
	     * as a type code, that identifies the generic SQL type <code>BOOLEAN</code>.
	     *
	     * @since 1.4
	     */
	    public final static int BOOLEAN = 16;
	    
		public static final int IDSTAMP = CHAR;//TODO is this right?
}
