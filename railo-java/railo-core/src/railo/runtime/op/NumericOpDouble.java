package railo.runtime.op;

import railo.runtime.exp.PageException;

public class NumericOpDouble implements NumericOp {
    

    @Override
	public Double divideRef(Object left, Object right) throws PageException {
		double r = Caster.toDoubleValue(right);
    	if(r==0d)
			throw new ArithmeticException("Division by zero is not possible");
		return Caster.toDouble(Caster.toDoubleValue(left)/r);
	}
    
    @Override
	public Double exponentRef(Object left, Object right) throws PageException {
		return Caster.toDouble(StrictMath.pow(Caster.toDoubleValue(left),Caster.toDoubleValue(right)));
	}
    
    @Override
	public Double intdivRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toIntValue(left)/Caster.toIntValue(right));
	}
    
    @Override
	public Double plusRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)+Caster.toDoubleValue(right));
	}
    
    @Override
	public Double minusRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)-Caster.toDoubleValue(right));
	}
    
    @Override
	public Double modulusRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)%Caster.toDoubleValue(right));
	}
    
    @Override
	public Double multiplyRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)*Caster.toDoubleValue(right));
	}
}
