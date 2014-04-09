/**
 * Implements the CFML Function datediff
 */
package railo.runtime.functions.dateTime;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.JREDateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;
import railo.runtime.type.dt.DateTime;

/**
 * 
 */
public final class DateDiff implements Function {

	//private static final int DATEPART_S = 0;
	//private static final int DATEPART_N = 1;
	//private static final int DATEPART_H = 2;
	private static final int DATEPART_D = 3;
	private static final int DATEPART_Y = DATEPART_D;
	private static final int DATEPART_YYYY = 10;
	private static final int DATEPART_M = 11;
	private static final int DATEPART_W = 12;
	private static final int DATEPART_WW = DATEPART_W;
	private static final int DATEPART_Q = 20;
	
	//private static Calendar _cRight;
	//private static Calendar _cLeft;


	/**
	 * @param pc
	 * @param s
	 * @param date
	 * @param date1
	 * @return
	 * @throws ExpressionException
	 */
	public synchronized static double call(PageContext pc , String datePart, DateTime left, DateTime right) throws ExpressionException	{
		long msLeft = left.getTime();
		long msRight = right.getTime();
		TimeZone tz = pc.getTimeZone();
		//if(true)return 0;
		// Date Part
		datePart=datePart.toLowerCase().trim();
		int dp;
		if("s".equals(datePart))		return diffSeconds(msLeft, msRight);
		else if("n".equals(datePart))	return diffSeconds(msLeft, msRight)/60L;
		else if("h".equals(datePart))	return diffSeconds(msLeft, msRight)/3600L;
		else if("d".equals(datePart))	dp=DATEPART_D;
		else if("y".equals(datePart))	dp=DATEPART_Y;
		else if("yyyy".equals(datePart))dp=DATEPART_YYYY;
		else if("m".equals(datePart))	dp=DATEPART_M;
		else if("w".equals(datePart))	dp=DATEPART_W;
		else if("ww".equals(datePart))	dp=DATEPART_WW;
		else if("q".equals(datePart))	dp=DATEPART_Q;
		else throw new FunctionException(pc,"dateDiff",3,"datePart","invalid value ["+datePart+"], valid values has to be [q,s,n,h,d,m,y,yyyy,w,ww]");
		
		
		
		// dates
		Calendar _cLeft = JREDateTimeUtil.getThreadCalendar(tz);
		_cLeft.setTimeInMillis(msLeft);
		
		Calendar _cRight = JREDateTimeUtil.newInstance(tz,Locale.US);
		_cRight.setTimeInMillis(msRight);
			
			
			if(msLeft>msRight) 
				return -_call(pc,dp, _cRight, msRight, _cLeft, msLeft);
			
			return _call(pc,dp, _cLeft, msLeft, _cRight, msRight);
		//}
	}
	
	public static long diffSeconds(long msLeft, long msRight) {
		if(msLeft>msRight)
			return -(long)((msLeft-msRight)/1000D);
		return (long)((msRight-msLeft)/1000D);
	}
	
	/*private static long _call(int datepart, long msLeft, long msRight) throws ExpressionException {
		
		long msDiff = msRight-msLeft;
		double diff = msDiff/1000D;
		if(DATEPART_S==datepart)	{
			return (long) diff;
		}
		if(DATEPART_N==datepart)	{
			return (long)(diff/60D);
		}
		if(DATEPART_H==datepart)	{
			return (long)(diff/3600D);
		}
		return 0;
	}*/
	
	private static long _call(PageContext pc , int datepart, Calendar cLeft, long msLeft, Calendar cRight, long msRight) throws ExpressionException {
		
		//long msDiff = msRight-msLeft;
		//double diff = msDiff/1000D;
		/*if(DATEPART_S==datepart)	{
			return (long) diff;
		}
		if(DATEPART_N==datepart)	{
			return (long)(diff/60D);
		}
		if(DATEPART_H==datepart)	{
			return (long)(diff/3600D);
		}*/
		
		long dDiff = cRight.get(Calendar.DATE)-cLeft.get(Calendar.DATE);
		long hDiff = cRight.get(Calendar.HOUR_OF_DAY)-cLeft.get(Calendar.HOUR_OF_DAY);
		long nDiff = cRight.get(Calendar.MINUTE)-cLeft.get(Calendar.MINUTE);
		long sDiff = cRight.get(Calendar.SECOND)-cLeft.get(Calendar.SECOND);
		

		if(DATEPART_D==datepart || DATEPART_W==datepart)	{

			int tmp=0;
			if(hDiff<0) 	tmp=-1;
			else if(hDiff>0);
			else if(nDiff<0) 	tmp=-1;
			else if(nDiff>0);
			else if(sDiff<0) 	tmp=-1;
			else if(sDiff>0);
			long rst = dayDiff(cLeft, cRight)+tmp;
			if(DATEPART_W==datepart)rst/=7;
			return rst;
		}
		
		long yDiff = cRight.get(Calendar.YEAR)-cLeft.get(Calendar.YEAR);
		long mDiff = cRight.get(Calendar.MONTH)-cLeft.get(Calendar.MONTH);
		if(DATEPART_YYYY==datepart)	{
			int tmp=0;
			if(mDiff<0)			tmp=-1;
			else if(mDiff>0);
			else if(dDiff<0) 	tmp=-1;
			else if(dDiff>0);
			else if(hDiff<0) 	tmp=-1;
			else if(hDiff>0);
			else if(nDiff<0) 	tmp=-1;
			else if(nDiff>0);
			else if(sDiff<0) 	tmp=-1;
			else if(sDiff>0);
			return yDiff+tmp;
		}
		if(DATEPART_M==datepart || DATEPART_Q==datepart)	{

			int tmp=0;
			if(dDiff<0) 		tmp=-1;
			else if(dDiff>0);
			else if(hDiff<0) 	tmp=-1;
			else if(hDiff>0);
			else if(nDiff<0) 	tmp=-1;
			else if(nDiff>0);
			else if(sDiff<0) 	tmp=-1;
			else if(sDiff>0);
			long rst = mDiff+(yDiff*12)+tmp;
			if(DATEPART_Q==datepart)rst/=3;
			return rst;
		}
		if(DATEPART_D==datepart)	{

			long rst = dDiff;
			//if(DATEPART_Q==datepart)rst/=3;
			return rst;
		}
		
		
		throw new FunctionException(pc,"dateDiff",3,"datePart","invalid value, valid values has to be [q,s,n,h,d,m,y,yyyy,w,ww]");
		
	}
	


	private static long dayDiff(Calendar l, Calendar r) {
		int lYear = l.get(Calendar.YEAR);
		int rYear = r.get(Calendar.YEAR);
		int lDayOfYear=l.get(Calendar.DAY_OF_YEAR);
		int rDayOfYear=r.get(Calendar.DAY_OF_YEAR);

		
		// same year
		if(lYear==rYear){
			return rDayOfYear-lDayOfYear;
		}
		
		long diff=rDayOfYear;
		diff-=lDayOfYear;
		for(int year=lYear;year<rYear;year++){
			diff+=Decision.isLeapYear(year)?366L:365L;
		}
		return diff;
	}

}