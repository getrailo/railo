package railo.runtime.format;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.JREDateTimeUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.engine.ThreadLocalPageContext;

public final class DateFormat implements Format {
	
	private final Calendar calendar;
	private final Locale locale;
	
	/**
	 * constructor of the class
	 * @param locale
	 */
	public DateFormat(Locale locale) {
		this.locale=locale;
		//print.ln(locale);
		calendar=JREDateTimeUtil.newInstance(locale);
	}
	

	/**
	 * formats a date to a cfml date format (short)
	 * @param date
	 * @return formated date
	 */
	public String format(Date date) {
		return format(date,"medium");
	}
	
	/**
	 * formats a date to a cfml date format
	 * @param date
	 * @param mask
	 * @return formated date as string
	 */
	public String format(Date date,String mask) {
		return format(date,mask,null);
	}
	public String format(Date date,String mask, TimeZone tz) {
		//DateUtil.setTimeZone(null,calendar,date); 
		calendar.setTimeZone(tz=ThreadLocalPageContext.getTimeZone(tz));  
		calendar.setTime(date);     
		

		String lcMask=StringUtil.toLowerCase(mask);
		if(lcMask.equals("short"))			return getAsString(java.text.DateFormat.SHORT,tz);
		else if(lcMask.equals("medium"))		return getAsString(java.text.DateFormat.MEDIUM,tz);
		else if(lcMask.equals("long")) 		return getAsString(java.text.DateFormat.LONG,tz);
		else if(lcMask.equals("full"))		return getAsString(java.text.DateFormat.FULL,tz);
		
		int len=mask.length();
		int pos=0;
		if(len==0) return "";
		
		StringBuffer formated=new StringBuffer();
		
		
		
		for(;pos<len;pos++) {
			char c=mask.charAt(pos);
			char next=(len>pos+1)?mask.charAt(pos+1):(char)0;
			switch(c) {

			// d: Day of month. Digits; no leading zero for single-digit days 
			// dd: Day of month. Digits; leading zero for single-digit days 
			// ddd: Day of week, abbreviation 
			// dddd: Day of week. Full name 
				case 'd':
				case 'D':
					char next2=(len>pos+2)?mask.charAt(pos+2):(char)0;
					char next3=(len>pos+3)?mask.charAt(pos+3):(char)0;
					
					int day=calendar.get(Calendar.DATE);
					if(next=='d' || next=='D') {
						if(next2=='d' || next2=='D') {
							if(next3=='d' || next3=='D') {
								formated.append(getDayOfWeekAsString(calendar.get(Calendar.DAY_OF_WEEK)));
								pos+=3;
							}
							else {
								formated.append(getDayOfWeekShortAsString(calendar.get(Calendar.DAY_OF_WEEK)));
								pos+=2;								
							}
						}
						else {
							formated.append(day<10?"0"+day:""+day);
							pos++;
						}
					}
					else {
						formated.append(day);
					}					
				break;

			// m: Month. Digits; no leading zero for single-digit months 
			// mm: Month. Digits; leading zero for single-digit months 
			// mmm: Month. abbreviation (if appropriate) 
			// mmmm: Month. Full name 
				case 'm':
				case 'M':
					char next_2=(len>pos+2)?mask.charAt(pos+2):(char)0;
					char next_3=(len>pos+3)?mask.charAt(pos+3):(char)0;
					
					int month=calendar.get(Calendar.MONTH)+1;
					if(next=='m' || next=='M') {
						if(next_2=='m' || next_2=='M') {
							if(next_3=='m' || next_3=='M') {
								formated.append(getMonthAsString(month));
								pos+=3;
							}
							else {
								formated.append(getMonthShortAsString(month));
								pos+=2;								
							}
						}
						else {
							formated.append(month<10?"0"+month:""+month);
							pos++;
						}
					}
					else {
						formated.append(month);
					}					
				break;

			// y: Year. Last two digits; no leading zero for years less than 10 
			// yy: Year. Last two digits; leading zero for years less than 10 
			// yyyy: Year. Four digits 
				case 'y':
				case 'Y':
					char next__2=(len>pos+2)?mask.charAt(pos+2):(char)0;
					char next__3=(len>pos+3)?mask.charAt(pos+3):(char)0;
					
					int year4=calendar.get(Calendar.YEAR);
					int year2=year4%100;
					if(next=='y' || next=='Y') {
						if((next__2=='y' || next__2=='Y') && (next__3=='y' || next__3=='Y')) {
							formated.append(year4);
							pos+=3;
						}
						else {
							formated.append(year2<10?"0"+year2:""+year2);
							pos++;
						}
					}
					else {
						formated.append(year2);
					}					
				break;
				
			// Otherwise
				default:
					formated.append(c);
			}
		}
		return formated.toString();
	}
	

	private String getAsString(int style, TimeZone tz) {
		java.text.DateFormat df = java.text.DateFormat.getDateInstance(style,getLocale());
		df.setTimeZone(tz);
		
		return df.format(calendar.getTime());	
	}
	
	private Locale getLocale() {
		return locale==null?Locale.US:locale;	
	}
	
	
	private String getMonthAsString(int month) {
		if(getLocale()==Locale.US) {
			switch(month) {
				case 1: return "January";
				case 2: return "February";
				case 3: return "March";
				case 4: return "April";
				case 5: return "May";
				case 6: return "June";
				case 7: return "July";
				case 8: return "August";
				case 9: return "September";
				case 10: return "October";
				case 11: return "November";
				case 12: return "December";
				default: return null;
			}
		}
		return new DateFormatSymbols(locale).getMonths()[month-1];
		
	}
	
	private String getMonthShortAsString(int month) {
		if(getLocale()==Locale.US) {
			switch(month) {
				case 1: return "Jan";
				case 2: return "Feb";
				case 3: return "Mar";
				case 4: return "Apr";
				case 5: return "May";
				case 6: return "Jun";
				case 7: return "Jul";
				case 8: return "Aug";
				case 9: return "Sep";
				case 10: return "Oct";
				case 11: return "Nov";
				case 12: return "Dec";
				default: return null;
			}
		}
		return new DateFormatSymbols(locale).getShortMonths()[month-1];
		
	}
	
	private String getDayOfWeekAsString(int dayOfWeek) {
		if(getLocale()==Locale.US) {
			switch(dayOfWeek) {
				case 1: return "Sunday";
				case 2: return "Monday";
				case 3: return "Tuesday";
				case 4: return "Wednesday";
				case 5: return "Thursday";
				case 6: return "Friday";
				case 7: return "Saturday";
				default:  return null;
			}
		}
		return new DateFormatSymbols(locale).getWeekdays()[dayOfWeek];
		
	}
	
	private String getDayOfWeekShortAsString(int dayOfWeek) {
		if(getLocale()==Locale.US) {
			switch(dayOfWeek) {
				case 1: return "Sun";
				case 2: return "Mon";
				case 3: return "Tue";
				case 4: return "Wed";
				case 5: return "Thu";
				case 6: return "Fri";
				case 7: return "Sat";
				default:  return null;
			}
		}
		return new DateFormatSymbols(locale).getShortWeekdays()[dayOfWeek];
		
	}
	
}