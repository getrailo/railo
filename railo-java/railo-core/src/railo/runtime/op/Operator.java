package railo.runtime.op;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import railo.commons.date.DateTimeUtil;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.interpreter.VariableInterpreter;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.UDFPlus;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.ref.VariableReference;
import railo.runtime.type.wrap.ListAsArray;
import railo.runtime.type.wrap.MapAsStruct;

/**
 * class to compare objects and primitive value types
 * 
 * 
 */
public final class Operator {
	
	private static final Object NULL = new Object();


	/** 
	 * compares two Objects 
	 * @param left 
	 * @param right 
	 * @return different of objects as int
	 * @throws PageException
	 */ 
	public static int compare(Object left, Object right) throws PageException { 
		//print.dumpStack();
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
		if(Decision.isNumeric(left)) {
			if(Decision.isNumeric(right)){
				// long numbers
				if(left.length()>9 || right.length()>9) {
					try{
						return new BigDecimal(left).compareTo(new BigDecimal(right));
					}
					catch(Throwable t){}
				}
				return compare(Caster.toDoubleValue(left,Double.NaN),Caster.toDoubleValue(right,Double.NaN));
			}
			
			return compare(Caster.toDoubleValue(left,Double.NaN),right);
		}
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
    	if(Decision.isNumeric(left)) {
            if(left.length()>9) {
            	try{
            		return new BigDecimal(left).compareTo(new BigDecimal(right));
            	}
            	catch(Throwable t){}
            }
    		return compare(Caster.toDoubleValue(left,Double.NaN),right); 
    	}
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
		DateTime dt=DateCaster.toDateAdvanced(right,DateCaster.CONVERTING_TYPE_OFFSET,null,null);
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
	
	public static boolean equalsEL(Object left, Object right, boolean caseSensitive, boolean allowComplexValues) {
		if(!allowComplexValues || (Decision.isSimpleValue(left) && Decision.isSimpleValue(right))){
			try {
				return equals(left, right, caseSensitive);
			} catch (PageException e) {
				return false;
			}
		}
		return equalsComplexEL(left, right, caseSensitive,false);
	}
	
	public static boolean equalsComplexEL(Object left, Object right, boolean caseSensitive, boolean checkOnlyPublicAppearance) {
		return _equalsComplexEL(null,left, right, caseSensitive,checkOnlyPublicAppearance);
	}
	
	public static boolean _equalsComplexEL(Set<Object> done,Object left, Object right, boolean caseSensitive, boolean checkOnlyPublicAppearance) {
		if(left==right) return true;
		if(Decision.isSimpleValue(left) && Decision.isSimpleValue(right)){
			try {
				return equals(left, right, caseSensitive);
			} catch (PageException e) {
				return false;
			}
		}
		if(left==null) return right==null;
		
		if(done==null)done=new HashSet<Object>();
		else if(done.contains(left) && done.contains(right)) return true;
		done.add(left);
		done.add(right);
		
		if(left instanceof Component && right instanceof Component)
			return __equalsComplexEL(done,(Component)left, (Component)right,caseSensitive,checkOnlyPublicAppearance);
		
		if(left instanceof UDFPlus && right instanceof UDFPlus)
			return __equalsComplexEL(done,(UDFPlus)left, (UDFPlus)right,caseSensitive,checkOnlyPublicAppearance);
		
		if(left instanceof Collection && right instanceof Collection)
			return __equalsComplexEL(done,(Collection)left, (Collection)right,caseSensitive,checkOnlyPublicAppearance);
		
		if(left instanceof List && right instanceof List)
			return __equalsComplexEL(done,ListAsArray.toArray((List)left), ListAsArray.toArray((List)right),caseSensitive,checkOnlyPublicAppearance);
		
		if(left instanceof Map && right instanceof Map)
			return __equalsComplexEL(done,MapAsStruct.toStruct((Map)left,true), MapAsStruct.toStruct((Map)right,true),caseSensitive,checkOnlyPublicAppearance);
		return left.equals(right);
	}
	
	private static boolean __equalsComplexEL(Set<Object> done,UDFPlus left, UDFPlus right,boolean caseSensitive, boolean checkOnlyPublicAppearance) {
		if(left==null || right==null) {
			if(left==right) return true;
			return false;
		}
		if(!left.getPageSource().equals(right.getPageSource())) return false;
		if(left.getIndex()!=right.getIndex()) return false;
		
		return true;
	}
	
	private static boolean __equalsComplexEL(Set<Object> done,Component left, Component right,boolean caseSensitive, boolean checkOnlyPublicAppearance) {
		if(left==null || right==null) {
			if(left==right) return true;
			return false;
		}
		if(!left.getPageSource().equals(right.getPageSource())) return false;
		if(!checkOnlyPublicAppearance && !__equalsComplexEL(done,left.getComponentScope(),right.getComponentScope(), caseSensitive,checkOnlyPublicAppearance)) return false;
		if(!__equalsComplexEL(done,(Collection)left,(Collection)right, caseSensitive,checkOnlyPublicAppearance)) return false;
		return true;
	}
	
	private static boolean __equalsComplexEL(Set<Object> done,Collection left, Collection right,boolean caseSensitive, boolean checkOnlyPublicAppearance) {
		if(left.size()!=right.size()) return false;
		Iterator<Key> it = left.keyIterator();
		Key k;
		Object l,r;
		while(it.hasNext()){
			k=it.next();
			l=left.get(k,NULL);
			r=right.get(k,NULL);
			if(l==NULL || r==NULL) {
				if(l==r) continue;
				return false;
			}
			
			if(!_equalsComplexEL(done,r, l, caseSensitive,checkOnlyPublicAppearance)) {
				return false;
			}
		}
		return true;
	}
	
	
	public static boolean equals(Object left, Object right, boolean caseSensitive, boolean allowComplexValues) throws PageException {
		if(!allowComplexValues || (Decision.isSimpleValue(left) && Decision.isSimpleValue(right)))
			return equals(left, right, caseSensitive);
		return equalsComplex(left, right, caseSensitive);
	}

	public static boolean equalsComplex(Object left, Object right, boolean caseSensitive) throws PageException {
		return _equalsComplex(null,left, right, caseSensitive);
	}
	

	public static boolean _equalsComplex(Set<Object> done,Object left, Object right, boolean caseSensitive) throws PageException {
		if(Decision.isSimpleValue(left) && Decision.isSimpleValue(right)){
			return equals(left, right, caseSensitive);
		}
		if(left==null) return right==null;
		if(done==null)done=new HashSet<Object>();
		else if(done.contains(left) && done.contains(right)) return true;
		done.add(left);
		done.add(right);
		
		if(left instanceof Collection && right instanceof Collection)
			return __equalsComplex(done,(Collection)left, (Collection)right,caseSensitive);
		
		if(left instanceof List && right instanceof List)
			return __equalsComplex(done,ListAsArray.toArray((List)left), ListAsArray.toArray((List)right),caseSensitive);
		
		if(left instanceof Map && right instanceof Map)
			return __equalsComplex(done,MapAsStruct.toStruct((Map)left,true), MapAsStruct.toStruct((Map)right,true),caseSensitive);
		
		return left.equals(right);
	}
	
	private static boolean __equalsComplex(Set<Object> done,Collection left, Collection right,boolean caseSensitive) throws PageException {
		if(left.size()!=right.size()) return false;
		Iterator<Key> it = left.keyIterator();
		Key k;
		Object l,r;
		while(it.hasNext()){
			k=it.next();
			r=right.get(k,NULL);
			if(r==NULL) return false;
			l=left.get(k,NULL);
			if(!_equalsComplex(done,r, l, caseSensitive)) return false;
		}
		return true;
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
     * concat 2 CharSequences
     * @param left
     * @param right
     * @return concated String
     */
	public static CharSequence concat(CharSequence left, CharSequence right) {
		if(left instanceof Appendable) {
			try {
				((Appendable)left).append(right);
				return left;
			} catch (IOException e) {}
		}
		return new StringBuilder(left).append(right);
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
    

    public static Double divRef(Object left, Object right) throws PageException {
		double r = Caster.toDoubleValue(right);
    	if(r==0d)
			throw new ArithmeticException("Division by zero is not possible");
		return Caster.toDouble(Caster.toDoubleValue(left)/r);
	}
    
    public static Double exponentRef(Object left, Object right) throws PageException {
		return Caster.toDouble(StrictMath.pow(Caster.toDoubleValue(left),Caster.toDoubleValue(right)));
	}
    
    public static Double intdivRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toIntValue(left)/Caster.toIntValue(right));
	}
    
    public static Double plusRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)+Caster.toDoubleValue(right));
	}
    
    public static Double minusRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)-Caster.toDoubleValue(right));
	}
    
    public static Double modulusRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)%Caster.toDoubleValue(right));
	}
    
    public static Double divideRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)/Caster.toDoubleValue(right));
	}
    
    public static Double multiplyRef(Object left, Object right) throws PageException {
		return Caster.toDouble(Caster.toDoubleValue(left)*Caster.toDoubleValue(right));
	}

    public static Double unaryPostPlus(PageContext pc,Collection.Key[] keys,double value) throws PageException {
    	VariableReference ref = VariableInterpreter.getVariableReference(pc, keys,true);
    	double rtn=Caster.toDoubleValue(ref.get(pc));
    	ref.set(rtn+value);
		return rtn;
	}


    public static Double unaryPostMinus(PageContext pc,Collection.Key[] keys,double value) throws PageException {
    	VariableReference ref = VariableInterpreter.getVariableReference(pc, keys,true);
    	double rtn=Caster.toDoubleValue(ref.get(pc));
    	ref.set(rtn-value);
		return rtn;
	}
    
    public static Double unaryPrePlus(PageContext pc,Collection.Key[] keys,double value) throws PageException {
    	VariableReference ref = VariableInterpreter.getVariableReference(pc, keys,true);
    	double rtn=Caster.toDoubleValue(ref.get(pc))+value;
    	ref.set(rtn);
		return rtn;
	}

    public static Double unaryPreMinus(PageContext pc,Collection.Key[] keys,double value) throws PageException {
    	VariableReference ref = VariableInterpreter.getVariableReference(pc, keys,true);
    	double rtn=Caster.toDoubleValue(ref.get(pc))-value;
    	ref.set(rtn);
		return rtn;
	}
    
    public static Double unaryPreMultiply(PageContext pc,Collection.Key[] keys,double value) throws PageException {
    	VariableReference ref = VariableInterpreter.getVariableReference(pc, keys,true);
    	double rtn=Caster.toDoubleValue(ref.get(pc))*value;
    	ref.set(rtn);
		return rtn;
	}
    
    public static Double unaryPreDivide(PageContext pc,Collection.Key[] keys,double value) throws PageException {
    	VariableReference ref = VariableInterpreter.getVariableReference(pc, keys,true);
    	double rtn=Caster.toDoubleValue(ref.get(pc))/value;
    	ref.set(rtn);
		return rtn;
	}
    
    public static String unaryPreConcat(PageContext pc,Collection.Key[] keys,String value) throws PageException {
    	VariableReference ref = VariableInterpreter.getVariableReference(pc, keys,true);
    	String rtn=Caster.toString(ref.get(pc)).concat(value);
    	ref.set(pc,rtn);
		return rtn;
	}




    public static Double unaryPostPlus(Collection coll,Collection.Key key,double value) throws PageException {
    	double rtn = Caster.toDoubleValue(coll.get(key));
    	coll.set(key, rtn+value);
    	return rtn;
	}

    public static Double unaryPostMinus(Collection coll,Collection.Key key,double value) throws PageException {
    	double rtn = Caster.toDoubleValue(coll.get(key));
    	coll.set(key, rtn-value);
    	return rtn;
	}
    
    public static Double unaryPrePlus(Collection coll,Collection.Key key,double value) throws PageException {
    	double rtn = Caster.toDoubleValue(coll.get(key))+value;
    	coll.set(key, rtn);
    	return rtn;
	}

    public static Double unaryPreMinus(Collection coll,Collection.Key key,double value) throws PageException {
    	double rtn = Caster.toDoubleValue(coll.get(key))-value;
    	coll.set(key, rtn);
    	return rtn;
	}
    
    public static Double unaryPreMultiply(Collection coll,Collection.Key key,double value) throws PageException {
    	double rtn = Caster.toDoubleValue(coll.get(key))*value;
    	coll.set(key, rtn);
    	return rtn;
	}
    
    public static Double unaryPreDivide(Collection coll,Collection.Key key,double value) throws PageException {
    	double rtn = Caster.toDoubleValue(coll.get(key))/value;
    	coll.set(key, rtn);
    	return rtn;
	}
    
    public static String unaryPreConcat(Collection coll,Collection.Key key,String value) throws PageException {
    	String rtn = Caster.toString(coll.get(key)).concat(value);
    	coll.set(key, rtn);
    	return rtn;
	}
}