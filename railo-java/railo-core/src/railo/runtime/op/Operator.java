package railo.runtime.op;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import railo.commons.date.DateTimeUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.util.CollectionUtil;

/**
 * class to compare objects and primitive value types
 * 
 * 
 */
public final class Operator {


	/** 
	 * compares two Objects 
	 * @param left 
	 * @param right 
	 * @return different of objects as int
	 * @throws PageException
	 */ 
	public static int compare(Object left, Object right) throws PageException { 
		if(left instanceof String) 			return compare((String)left,right);
		else if(left instanceof Number) 	return compare(((Number)left).doubleValue(),right);
		else if(left instanceof Boolean)	return compare(((Boolean)left).booleanValue(),right);
		else if(left instanceof Date) 		return compare((Date)left ,right);
		else if(left instanceof Castable) 	return compare(((Castable)left) ,right); 
		else if(left instanceof Locale) 	return compare(((Locale)left) ,right); 
		else if(left==null) 				return compare("",right);
		/*/NICE disabled at the moment left Comparable 
		else if(left instanceof Comparable) { 
		    	return ((Comparable)left).compareTo(right);
		} */
		else if(left instanceof Character)	return compare( ((Character)left).toString() , right ); 
		else if(left instanceof Calendar)	return compare( ((Calendar)left).getTime() , right ); 
		else {
			return error(false,true); 
		}
	} 
	
	public static int compare(Locale left, Object right) throws PageException { 
		if(right instanceof String)			return compare(left,(String)right); 
		else if(right instanceof Number)	return compare(left,Caster.toString(right)); 
		else if(right instanceof Boolean)	return compare(left,Caster.toString(right)); 
		else if(right instanceof Date)		return compare(left,Caster.toString(right)); 
		else if(right instanceof Castable)	return compare(left,((Castable)right).castToString()); 
		else if(right instanceof Locale)	return left.toString().compareTo(right.toString()); 
		else if(right==null) 				return compare( left, "" ); 
		else if(right instanceof Character)	return compare(left,((Character)right).toString()); 
		else if(right instanceof Calendar)	return compare(left, Caster.toString(((Calendar)right).getTime())  ); 
		else return error(false,true); 
	} 
	
	public static int compare(Object left, Locale right) throws PageException { 
		return -compare(right,left); 
	}
	
	public static int compare(Locale left, String right) { 
		Locale rightLocale = LocaleFactory.getLocale(right, null);
		if(rightLocale==null) return LocaleFactory.toString(left).compareTo(right);
		return left.toString().compareTo(rightLocale.toString());
	}
	
	public static int compare(String left, Locale right) { 
		return -compare(right,left);
	}
	
	/** 
	 * compares a Object with a String 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(Object left, String right) throws PageException { 
		if(left instanceof String)			return compare((String)left, right ); 
		else if(left instanceof Number)		return compare( ((Number)left).doubleValue() , right ); 
		else if(left instanceof Boolean)	return compare( ((Boolean)left).booleanValue(), right ); 
		else if(left instanceof Date)		return compare( (Date)left , right ); 
		else if(left instanceof Castable)	return ((Castable)left).compareTo(right ); 
		else if(left instanceof Locale)		return compare( (Locale)left , right ); 
		else if(left==null) 				return "".compareToIgnoreCase(right);
		else if(left instanceof Character)	return compare( ((Character)left).toString() , right ); 
		else if(left instanceof Calendar)	return compare( ((Calendar)left).getTime() , right ); 
		
		else return error(false,true);
	} 

	/** 
	 * compares a String with a Object 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(String left, Object right) throws PageException { 
		if(right instanceof String)			return compare(left,(String)right); 
		else if(right instanceof Number)	return compare(left,((Number)right).doubleValue()); 
		else if(right instanceof Boolean)	return compare(left,((Boolean)right).booleanValue()?1:0); 
		else if(right instanceof Date)		return compare(left,(Date)right); 
		else if(right instanceof Castable)	return -((Castable)right).compareTo(left);//compare(left ,((Castable)right).castToString());
		else if(right instanceof Locale)	return compare(left ,(Locale)right);
		else if(right==null) 				return left.compareToIgnoreCase("");
		else if(right instanceof Character)	return compare(left ,((Character)right).toString());
		else if(right instanceof Calendar)	return compare(left, ((Calendar)right).getTime()  ); 
		else return error(false,true);  
	} 

	/** 
	 * compares a Object with a double 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(Object left, double right) throws PageException { 
		if(left instanceof Number)			return compare( ((Number)left).doubleValue() ,right ); 
		else if(left instanceof String)		return compare( (String)left, right ); 
		else if(left instanceof Boolean)	return compare( ((Boolean)left).booleanValue()?1D:0D , right ); 
		else if(left instanceof Date)		return compare( ((Date)left) ,right); 
		else if(left instanceof Castable)	return ((Castable)left).compareTo(right); 
		//else if(left instanceof Castable)	return compare(((Castable)left).castToDoubleValue() , right ); 
		else if(left instanceof Locale)		return compare( ((Locale)left), Caster.toString(right)); 
		else if(left==null) 				return -1;
		else if(left instanceof Character)	return compare(((Character)left).toString(),right);
		else if(left instanceof Calendar)	return compare( ((Calendar)left).getTime() , right ); 
		else {
			return error(false,true); 
		}
	} 

	/** 
	 * compares a double with a Object 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(double left, Object right) throws PageException { 
		if(right instanceof Number)			return compare(left,((Number)right).doubleValue()); 
		else if(right instanceof String)	return compare(left,(String)right); 
		else if(right instanceof Boolean)	return compare(left,((Boolean)right).booleanValue()?1D:0D); 
		else if(right instanceof Date)		return compare(left,((Date)right)); 
		else if(right instanceof Castable)	return -((Castable)right).compareTo(left);//compare(left ,((Castable)right).castToDoubleValue());
		else if(right instanceof Locale)	return compare(Caster.toString(left) ,((Locale)right));
		else if(right==null) 				return 1;
		else if(right instanceof Character)	return compare(left ,((Character)right).toString());
		else if(right instanceof Calendar)	return compare(left, ((Calendar)right).getTime() ); 
		else return error(true,false);  
	} 

	
	/** 
	 * compares a Object with a boolean 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(Object left, boolean right) throws PageException { 
		if(left instanceof Boolean)			return compare(((Boolean)left).booleanValue(),right); 
		else if(left instanceof String)		return compare((String)left,right); 
		else if(left instanceof Number)		return compare(((Number)left).doubleValue(),right?1D:0D); 
		else if(left instanceof Date)		return compare(((Date)left),right?1:0); 
		else if(left instanceof Castable)	return ((Castable)left).compareTo(right );
		else if(left instanceof Locale)		return compare(((Locale)left),Caster.toString(right)); 
		else if(left==null) 				return -1;
		else if(left instanceof Character)	return compare(((Character)left).toString(),right);
		else if(left instanceof Calendar)	return compare( ((Calendar)left).getTime() , right?1:0 ); 
		else return error(false,true);  
	} 

	/** 
	 * compares a boolean with a Object 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(boolean left, Object right) throws PageException { 
		if(right instanceof Boolean)		return compare(left,((Boolean)right).booleanValue()); 
		else if(right instanceof String)	return compare(left?1:0,(String)right); 
		else if(right instanceof Number)	return compare(left?1D:0D,((Number)right).doubleValue()); 
		else if(right instanceof Date)		return compare(left?1:0,((Date)right)); 
		else if(right instanceof Castable)	return -((Castable)right).compareTo(left);//compare(left ,((Castable)right).castToBooleanValue());
		else if(right instanceof Locale)	return compare(Caster.toString(left),((Locale)right)); 
		else if(right==null) 				return 1;
		else if(right instanceof Character)	return compare(left ,((Character)right).toString());
		else if(right instanceof Calendar)	return compare(left?1:0, ((Calendar)right).getTime()  ); 
		else return error(true,false);  
	}
	
	/** 
	 * compares a Object with a Date 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(Object left, Date right) throws PageException { 
		if(left instanceof String)			return compare((String)left,right); 
		else if(left instanceof Number)		return compare(((Number)left).doubleValue() ,right.getTime()/1000 ); 
		else if(left instanceof Boolean)	return compare( ((Boolean)left).booleanValue()?1D:0D , right.getTime()/1000 ); 
		else if(left instanceof Date)		return compare( ((Date)left) , right ); 
		else if(left instanceof Castable)	return ((Castable)left).compareTo(Caster.toDatetime(right,null) );
		else if(left instanceof Locale)		return compare( ((Locale)left) , Caster.toString(right)); 
		else if(left==null) 				return compare("", right);
		else if(left instanceof Character)	return compare(((Character)left).toString(),right);
		else if(left instanceof Calendar)	return compare( ((Calendar)left).getTime() , right ); 
		else return error(false,true);  
	}  

	/** 
	 * compares a Date with a Object 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(Date left, Object right) throws PageException { 
		if(right instanceof String)			return compare(left,(String)right); 
		else if(right instanceof Number)	return compare(left.getTime()/1000,((Number)right).doubleValue()); 
		else if(right instanceof Boolean)	return compare(left.getTime()/1000,((Boolean)right).booleanValue()?1D:0D); 
		else if(right instanceof Date)		return compare(left.getTime()/1000,((Date)right).getTime()/1000); 
		else if(right instanceof Castable)	return -((Castable)right).compareTo(Caster.toDate(left,null));//compare(left ,(Date)((Castable)right).castToDateTime());
		else if(right instanceof Locale)	return compare(Caster.toString(left),(Locale)right); 
		else if(right==null) 				return compare(left,"");
		else if(right instanceof Character)	return compare(left ,((Character)right).toString());
		else if(right instanceof Calendar)	return compare(left.getTime()/1000, ((Calendar)right).getTime().getTime()/1000  ); 
		else return error(true,false);  
	}
	
	public static int compare(Castable left, Object right) throws PageException { 
		if(right instanceof String)			return left.compareTo((String)right); 
		else if(right instanceof Number)	return left.compareTo(((Number)right).doubleValue()); 
		else if(right instanceof Boolean)	return left.compareTo(((Boolean)right).booleanValue()?1d:0d);
		else if(right instanceof Date)		return left.compareTo(Caster.toDate(right,null));
		else if(right instanceof Castable)	return compare(left.castToString() , ((Castable)right).castToString() ); 
		else if(right instanceof Locale)	return compare(left.castToString() , (Locale)right);
		else if(right == null) 				return compare(left.castToString(), "" ); 
		else if(right instanceof Character)	return left.compareTo(((Character)right).toString());
		else if(right instanceof Calendar)	return left.compareTo(new DateTimeImpl(((Calendar)right).getTime()) ); 
		else return error(true,false); 
	}
	
	public static int compare(Object left, Castable right) throws PageException { 
		return -compare(right,left); 
	}
		

	/** 
	 * compares a String with a String 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(String left, String right) { 
		if(Decision.isNumeric(left))
			return compare(Caster.toDoubleValue(left,Double.NaN),right);
		if(Decision.isBoolean(left))
			return compare(Caster.toBooleanValue(left,false)?1D:0D,right);
//		 NICE Date compare, perhaps datetime to double
		return left.compareToIgnoreCase(right); 
	} 

    /** 
     * compares a String with a double 
     * @param left 
     * @param right 
     * @return difference as int
     */ 
    public static int compare(String left, double right) { 
    	if(Decision.isNumeric(left))
            return compare(Caster.toDoubleValue(left,Double.NaN),right); 
        if(Decision.isBoolean(left))
            return compare(Caster.toBooleanValue(left,false),right); 
        
        if(left.length()==0) return -1;
        char leftFirst=left.charAt(0);
        if(leftFirst>='0' && leftFirst<='9')
            return left.compareToIgnoreCase(Caster.toString(right));
        return leftFirst-'0';
    }

    /** 
	 * compares a String with a boolean 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(String left, boolean right) { 
		if(Decision.isBoolean(left))
            return compare(Caster.toBooleanValue(left,false),right); 
		if(Decision.isNumeric(left))
            return compare(Caster.toDoubleValue(left,Double.NaN),right?1d:0d); 
        
        if(left.length()==0) return -1;
        char leftFirst=left.charAt(0);
        //print.ln(left+".compareTo("+Caster.toString(right)+")");
        //p(left);
        if(leftFirst>='0' && leftFirst<='9')
            return left.compareToIgnoreCase(Caster.toString(right?1D:0D));
        return leftFirst-'0';
	} 

	/** 
	 * compares a String with a Date 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(String left, Date right) throws PageException { 
		return -compare(right,left);
	} 

	/** 
	 * compares a double with a String 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
    public static int compare(double left, String right) { 
        return -compare(right,left);
    }
    
	/* * 
	 * compares a double with a double 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * / 
    public static int compare(double left, double right) { 
    	return __compare(left,right);
    }*/
    
	/** 
	 * compares a double with a double 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
    public static int compare(double left, double right) { 
    	if((left)<(right))return -1; 
        else if((left)>(right))return 1; 
        else return 0;
    }

	/** 
	 * compares a double with a boolean 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(double left, boolean right) { 
			return compare(left,right?1d:0d); 
	} 

	/** 
	 * compares a double with a Date 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(double left, Date right) { 
			return compare(DateTimeUtil.getInstance().toDateTime(left).getTime()/1000,right.getTime()/1000); 
	} 

	/** 
	 * compares a boolean with a double 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(boolean left, double right) { 
			return compare(left?1d:0d, right); 
	} 

	/** 
	 * compares a boolean with a double 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(boolean left, String right) { 
			return -compare(right,left); 
	} 

	/** 
	 * compares a boolean with a boolean 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(boolean left, boolean right) { 
			if(left)return right?0:1; 
			return right?-1:0; 
	} 

	/** 
	 * compares a boolean with a Date 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(boolean left, Date right) { 
			return compare(left?1D:0D,right); 
	} 

	/** 
	 * compares a Date with a String 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 * @throws PageException
	 */ 
	public static int compare(Date left, String right) throws PageException { 
		if(Decision.isNumeric(right)) return compare(left.getTime()/1000,Caster.toDoubleValue(right));
		DateTime dt=DateCaster.toDateAdvanced(right,true,null,null);
		if(dt!=null) {
			return compare(left.getTime()/1000,dt.getTime()/1000);  	
		}
		return Caster.toString(left).compareToIgnoreCase(right);
	} 

	/** 
	 * compares a Date with a double 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(Date left, double right) { 
			return compare(left.getTime()/1000, DateTimeUtil.getInstance().toDateTime(right).getTime()/1000); 
	} 

	/** 
	 * compares a Date with a boolean 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(Date left, boolean right) { 
			return compare(left,right?1D:0D); 
	} 

	/** 
	 * compares a Date with a Date 
	 * @param left 
	 * @param right 
	 * @return difference as int
	 */ 
	public static int compare(Date left, Date right) { 
			return compare(left.getTime()/1000,right.getTime()/1000); 
	}        

	private static int error(boolean leftIsOk, boolean rightIsOk, Object left, Object right) throws ExpressionException { 
		throw new ExpressionException("can't compare complex object types ("+Caster.toClassName(left)+" - "+Caster.toClassName(right)+") as simple value");
	}
	private static int error(boolean leftIsOk, boolean rightIsOk) throws ExpressionException { 
		// TODO remove this method
		throw new ExpressionException("can't compare complex object types as simple value");
	}

	/**
	 * Method to compare to different values, return true of objects are same otherwise false
	 * @param left left value to compare
	 * @param right right value to compare
	 * @param caseSensitive check case sensitive  or not
	 * @return is same or not
	 * @throws PageException
	 */
	public static boolean equals(Object left, Object right, boolean caseSensitive) throws PageException {
		if(caseSensitive) {
			try {
				return Caster.toString(left).equals(Caster.toString(right));
			} catch (ExpressionException e) {
				return compare(left,right)==0;
			}
		}
		return compare(left,right)==0;
	}

	public static boolean equals(Object left, Object right, boolean caseSensitive, boolean allowComplexValues) throws PageException {
		if(!allowComplexValues || (Decision.isSimpleValue(left) && Decision.isSimpleValue(right)))
			return equals(left, right, caseSensitive);
		return left.equals(right);
	}
	public static boolean equalsEL(Object left, Object right, boolean caseSensitive, boolean allowComplexValues) {
		if(!allowComplexValues || (Decision.isSimpleValue(left) && Decision.isSimpleValue(right))){
			try {
				return equals(left, right, caseSensitive);
			} catch (PageException e) {
				return false;
			}
		}
		if(left instanceof Collection && right instanceof Collection)
			return CollectionUtil.equals((Collection)left, (Collection)right);
		return left.equals(right);
	}
	
	

	/**
	 * check if left is inside right (String-> ignore case)
	 * @param left string to check
	 * @param right substring to find in string
	 * @return return if substring has been found
	 * @throws PageException
	 */
	public static boolean ct(Object left, Object right) throws PageException {
		return Caster.toString(left).toLowerCase().indexOf(Caster.toString(right).toLowerCase())!=-1;		
	} 

	/**
	 * Equivalence: Return True if both operands are True or both are False. The EQV operator is the opposite of the XOR operator. For example, True EQV True is True, but True EQV False is False.
	 * @param left value to check
	 * @param right value to check
	 * @return result of operation
	 * @throws PageException
	 */
	public static boolean eqv(Object left, Object right) throws PageException {
		return eqv(Caster.toBooleanValue(left),Caster.toBooleanValue(right));	
	}

	/**
	 * Equivalence: Return True if both operands are True or both are False. The EQV operator is the opposite of the XOR operator. For example, True EQV True is True, but True EQV False is False.
	 * @param left value to check
	 * @param right value to check
	 * @return result of operation
	 */
	public static boolean eqv(boolean left, boolean right) {
		return (left==true && right==true) || (left==false && right==false);	
	}

	/**
	 * Implication: The statement A IMP B is the equivalent of the logical statement 
	 * "If A Then B." A IMP B is False only if A is True and B is False. It is True in all other cases.
	 * @param left value to check
	 * @param right value to check
	 * @return result
	 * @throws PageException
	 */
	public static boolean imp(Object left, Object right) throws PageException {
		return imp(Caster.toBooleanValue(left),Caster.toBooleanValue(right));	
	} 

	/**
	 * Implication: The statement A IMP B is the equivalent of the logical statement 
	 * "If A Then B." A IMP B is False only if A is True and B is False. It is True in all other cases.
	 * @param left value to check
	 * @param right value to check
	 * @return result
	 */
	public static boolean imp(boolean left, boolean right) {
		return !(left==true && right==false);	
	} 

	/**
	 * check if left is not inside right (String-> ignore case)
	 * @param left string to check
	 * @param right substring to find in string
	 * @return return if substring NOT has been found
	 * @throws PageException
	 */
	public static boolean nct(Object left, Object right) throws PageException {
		return !ct(left,right);		
	}


	/**
	 * simple reference compersion
	 * @param left
	 * @param right
	 * @return
	 * @throws PageException
	 */
	public static boolean eeq(Object left, Object right) throws PageException {
		return left==right;		
	}


	/**
	 * simple reference compersion
	 * @param left
	 * @param right
	 * @return
	 * @throws PageException
	 */
	public static boolean neeq(Object left, Object right) throws PageException {
		return left!=right;		
	}
	
	/**
	 * calculate the exponent of the left value 
	 * @param left value to get exponent from
	 * @param right exponent count
	 * @return return expoinended value
	 * @throws PageException
	 */
	public static double exponent(Object left, Object right) throws PageException {
		return StrictMath.pow(Caster.toDoubleValue(left),Caster.toDoubleValue(right));
	} 
	
	public static double exponent(double left, double right) {
		return StrictMath.pow(left,right);
	} 
	
	public static double intdiv(double left, double right) {
		return ((int)left)/((int)right);
	} 
	
	public static double div(double left, double right) {
		if(right==0d)
			throw new ArithmeticException("Division by zero is not possible");
		return left/right;
	} 
	
	public static float exponent(float left, float right) {
		return (float) StrictMath.pow(left,right);
	} 
    

    /**
     * concat to Strings
     * @param left
     * @param right
     * @return concated String
     */
    public static String concat(String left,String right) {
        int ll = left.length();
        int rl = right.length();
        int i;
        
        char[] chars=new char[ll+rl];
        for(i=0;i<ll;i++)chars[i]=left.charAt(i);
        for(i=0;i<rl;i++)chars[ll+i]=right.charAt(i);
        return new String(chars);
    }

    /**
     * plus operation
     * @param left
     * @param right
     * @return result of the opertions
     */
    public final static double plus(double left, double right) {
        return left+right;
    }
    
    /**
     * minus operation
     * @param left
     * @param right
     * @return result of the opertions
     */
    public static double minus(double left, double right) {
        return left-right;
    }
    
    /**
     * modulus operation
     * @param left
     * @param right
     * @return result of the opertions
     */
    public static double modulus(double left, double right) {
        return left%right;
    }
    
    /**
     * divide operation
     * @param left
     * @param right
     * @return result of the opertions
     */
    public static double divide(double left, double right) {
        return left/right;
    }
    
    /**
     * multiply operation
     * @param left
     * @param right
     * @return result of the opertions
     */
    public static double multiply(double left, double right) {
        return left*right;
    }

    /**
     * bitand operation
     * @param left
     * @param right
     * @return result of the opertions
     */
    public static double bitand(double left, double right) {
        return (int)left&(int)right;
    }

    /**
     * bitand operation
     * @param left
     * @param right
     * @return result of the opertions
     */
    public static double bitor(double left, double right) {
        return (int)left|(int)right;
    }
}