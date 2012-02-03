package railo.runtime.op;

import java.util.Date;

import railo.runtime.exp.PageException;
import railo.runtime.util.Operation;

/**
 * oimplementation of interface Operation
 */
public final class OperationImpl implements Operation {

    private static OperationImpl singelton;

    /**
     * @see railo.runtime.util.Operation#compare(boolean, boolean)
     */
    public int compare(boolean left, boolean right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(boolean, java.util.Date)
     */
    public int compare(boolean left, Date right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(boolean, double)
     */
    public int compare(boolean left, double right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(boolean, java.lang.Object)
     */
    public int compare(boolean left, Object right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(boolean, java.lang.String)
     */
    public int compare(boolean left, String right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.util.Date, boolean)
     */
    public int compare(Date left, boolean right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.util.Date, java.util.Date)
     */
    public int compare(Date left, Date right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.util.Date, double)
     */
    public int compare(Date left, double right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.util.Date, java.lang.Object)
     */
    public int compare(Date left, Object right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.util.Date, java.lang.String)
     */
    public int compare(Date left, String right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(double, boolean)
     */
    public int compare(double left, boolean right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(double, java.util.Date)
     */
    public int compare(double left, Date right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(double, double)
     */
    public int compare(double left, double right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(double, java.lang.Object)
     */
    public int compare(double left, Object right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(double, java.lang.String)
     */
    public int compare(double left, String right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.Object, boolean)
     */
    public int compare(Object left, boolean right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.Object, java.util.Date)
     */
    public int compare(Object left, Date right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.Object, double)
     */
    public int compare(Object left, double right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object left, Object right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.Object, java.lang.String)
     */
    public int compare(Object left, String right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.String, boolean)
     */
    public int compare(String left, boolean right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.String, java.util.Date)
     */
    public int compare(String left, Date right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.String, double)
     */
    public int compare(String left, double right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.String, java.lang.Object)
     */
    public int compare(String left, Object right) throws PageException {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#compare(java.lang.String, java.lang.String)
     */
    public int compare(String left, String right) {
        return Operator.compare(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#concat(java.lang.String, java.lang.String)
     */
    public String concat(String left, String right) {
        return left.concat(right);
    }

    /**
     * @see railo.runtime.util.Operation#ct(java.lang.Object, java.lang.Object)
     */
    public boolean ct(Object left, Object right) throws PageException {
        return Operator.ct(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#divide(double, double)
     */
    public double divide(double left, double right) {
        return Operator.divide(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#equals(java.lang.Object, java.lang.Object, boolean)
     */
    public boolean equals(Object left, Object right, boolean caseSensitive) throws PageException {
        return Operator.equals(left,right,caseSensitive);
    }

    /**
     * @see railo.runtime.util.Operation#eqv(java.lang.Object, java.lang.Object)
     */
    public boolean eqv(Object left, Object right) throws PageException {
        return Operator.eqv(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#exponent(java.lang.Object, java.lang.Object)
     */
    public double exponent(Object left, Object right) throws PageException {
        return Operator.exponent(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#imp(java.lang.Object, java.lang.Object)
     */
    public boolean imp(Object left, Object right) throws PageException {
        return Operator.imp(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#minus(double, double)
     */
    public double minus(double left, double right) {
        return Operator.minus(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#modulus(double, double)
     */
    public double modulus(double left, double right) {
        return Operator.modulus(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#multiply(double, double)
     */
    public double multiply(double left, double right) {
        return Operator.multiply(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#nct(java.lang.Object, java.lang.Object)
     */
    public boolean nct(Object left, Object right) throws PageException {
        return Operator.nct(left,right);
    }

    /**
     * @see railo.runtime.util.Operation#plus(double, double)
     */
    public double plus(double left, double right) {
        return Operator.plus(left,right);
    }

    public static Operation getInstance() {
        if(singelton==null)singelton=new OperationImpl();
        return singelton;
    }

}
