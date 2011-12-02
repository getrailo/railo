package railo.runtime.format;

import java.text.DateFormatSymbols;
import java.util.Locale;

public abstract class BaseFormat implements Format {

	private Locale locale;

	public BaseFormat(Locale locale) {
		this.locale=locale;
	}
	
	protected final String getMonthAsString(int month) {
		if(getLocale().equals(Locale.US)) {
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
	
	protected final String getMonthShortAsString(int month) {
		if(getLocale().equals(Locale.US)) {
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
	
	protected final String getDayOfWeekAsString(int dayOfWeek) {
		if(getLocale().equals(Locale.US)) {
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
	
	protected final String getDayOfWeekShortAsString(int dayOfWeek) {
		if(getLocale().equals(Locale.US)) {
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
	
	protected final Locale getLocale() {
		return locale==null?Locale.US:locale;	
	}
}
