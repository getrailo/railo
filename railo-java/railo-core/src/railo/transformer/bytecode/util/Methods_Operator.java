package railo.transformer.bytecode.util;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public final class Methods_Operator {

	public static final Method OPERATOR_EQV_BV_BV=new Method(
												"eqv",
												Types.BOOLEAN_VALUE,
												new Type[]{Types.BOOLEAN_VALUE,Types.BOOLEAN_VALUE}
											);

	public static final Method OPERATOR_IMP_BV_BV=new Method(
												"imp",
												Types.BOOLEAN_VALUE,
												new Type[]{Types.BOOLEAN_VALUE,Types.BOOLEAN_VALUE}
											);

	public static final Method OPERATOR_CT=new Method(
												"ct",
												Types.BOOLEAN_VALUE,
												new Type[]{Types.OBJECT,Types.OBJECT}
											);
	public static final Method OPERATOR_EEQ=new Method(
			"eeq",
			Types.BOOLEAN_VALUE,
			new Type[]{Types.OBJECT,Types.OBJECT}
		);
	public static final Method OPERATOR_NEEQ=new Method(
			"neeq",
			Types.BOOLEAN_VALUE,
			new Type[]{Types.OBJECT,Types.OBJECT}
		);

	public static final Method OPERATOR_NCT=new Method(
												"nct",
												Types.BOOLEAN_VALUE,
												new Type[]{Types.OBJECT,Types.OBJECT}
											);
	
	// double exponent(double left, double right)
	public static final Method OPERATOR_INTDIV_DOUBLE=new Method("intdiv",Types.DOUBLE_VALUE,new Type[]{Types.DOUBLE_VALUE,Types.DOUBLE_VALUE});
	public static final Method OPERATOR_EXP_DOUBLE=new Method("exponent",Types.DOUBLE_VALUE,new Type[]{Types.DOUBLE_VALUE,Types.DOUBLE_VALUE});
	public static final Method OPERATOR_EXP_FLOAT=new Method("exponent",Types.FLOAT_VALUE,new Type[]{Types.FLOAT_VALUE,Types.FLOAT_VALUE});
	
	
    public static final Method[][] OPERATORS=new Method[][]{
        // Object    
        new Method[] {
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.OBJECT,Types.OBJECT}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.OBJECT,Types.BOOLEAN_VALUE}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.OBJECT,Types.DOUBLE_VALUE}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.OBJECT,Types.STRING})
        },
        // boolean    
        new Method[] {
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.BOOLEAN_VALUE,Types.OBJECT}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.BOOLEAN_VALUE,Types.BOOLEAN_VALUE}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.BOOLEAN_VALUE,Types.DOUBLE_VALUE}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.BOOLEAN_VALUE,Types.STRING})
        },
        // double    
        new Method[] {
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.DOUBLE_VALUE,Types.OBJECT}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.DOUBLE_VALUE,Types.BOOLEAN_VALUE}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.DOUBLE_VALUE,Types.DOUBLE_VALUE}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.DOUBLE_VALUE,Types.STRING})
        },
        // String    
        new Method[] {
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.STRING,Types.OBJECT}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.STRING,Types.BOOLEAN_VALUE}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.STRING,Types.DOUBLE_VALUE}),
        		new Method("compare",Types.INT_VALUE,new Type[]{Types.STRING,Types.STRING})
        }
    };
}
