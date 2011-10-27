package railo.runtime.op;

import railo.runtime.exp.PageException;

public interface NumericOp {
    

    
    public Number exponentRef(Object left, Object right) throws PageException;
    
    public Number intdivRef(Object left, Object right) throws PageException;
    
    public Number plusRef(Object left, Object right) throws PageException;
    
    public Number minusRef(Object left, Object right) throws PageException;
    
    public Number modulusRef(Object left, Object right) throws PageException;
    
    public Number divideRef(Object left, Object right) throws PageException;
    
    public Number multiplyRef(Object left, Object right) throws PageException;
}
