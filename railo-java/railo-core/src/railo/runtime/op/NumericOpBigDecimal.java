package railo.runtime.op;

import java.math.BigDecimal;

import railo.commons.math.MathUtil;
import railo.runtime.exp.PageException;

public class NumericOpBigDecimal implements NumericOp {


    public BigDecimal divideRef(Object left, Object right) throws PageException {
		double r = Caster.toDoubleValue(right);
    	if(r==0d)
			throw new ArithmeticException("Division by zero is not possible");
    	return MathUtil.divide(Caster.toBigDecimal(left),Caster.toBigDecimal(right));
	}
    
    public BigDecimal exponentRef(Object left, Object right) throws PageException {
    	return MathUtil.divide(Caster.toBigDecimal(left),Caster.toBigDecimal(right));
	}
    
    public BigDecimal intdivRef(Object left, Object right) throws PageException {
    	return MathUtil.divide(Caster.toBigDecimal(left),Caster.toBigDecimal(right));
	}
    
    public BigDecimal plusRef(Object left, Object right) throws PageException {
    	return MathUtil.add(Caster.toBigDecimal(left),Caster.toBigDecimal(right));
	}
    
    public BigDecimal minusRef(Object left, Object right) throws PageException {
    	return MathUtil.subtract(Caster.toBigDecimal(left),Caster.toBigDecimal(right));
	}
    
    public BigDecimal modulusRef(Object left, Object right) throws PageException {
    	return MathUtil.multiply(Caster.toBigDecimal(left),Caster.toBigDecimal(right)); /// ????
	}
    
    
    public BigDecimal multiplyRef(Object left, Object right) throws PageException {
		return MathUtil.multiply(Caster.toBigDecimal(left),Caster.toBigDecimal(right));
	}
}
