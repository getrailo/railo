package railo.runtime.functions.displayFormatting;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

/**
 * Implements the CFML Function dateformat
 */
public final class DateTimeFormat extends BIF {

	private static final long serialVersionUID = 134840879454373440L;
	public static final String DEFAULT_MASK = "dd-MMM-yyyy HH:mm:ss";
	private static final String[] AP = new String[]{"A","P"};

	/**
	 * @param pc
	 * @param object
	 * @return Formated Time Object as String
	 * @throws ExpressionException
	 */
	public static String call(PageContext pc , Object object) throws ExpressionException {
		return invoke(pc,object, null,Locale.US,ThreadLocalPageContext.getTimeZone(pc));
	}
	
	/**
	 * @param pc
	 * @param object
	 * @param mask Characters that show how CFML displays a date:
	 * @return Formated Time Object as String
	 * @throws ExpressionException
	 */
	public static String call(PageContext pc , Object object, String mask) throws ExpressionException {
		return invoke(pc,object,mask,Locale.US,ThreadLocalPageContext.getTimeZone(pc));
	}

	public static String call(PageContext pc , Object object, String mask,String strTimezone) throws ExpressionException {
		return invoke(pc,object,mask, Locale.US,strTimezone==null?ThreadLocalPageContext.getTimeZone(pc):TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	public static String invoke(PageContext pc , Object object, String mask,Locale locale,TimeZone tz) throws ExpressionException {
		if(locale==null) locale=Locale.US;
		DateTime datetime = Caster.toDate(object,true,tz,null);
		if(datetime==null) {
		    if(object.toString().trim().length()==0) return "";
		    throw new ExpressionException("can't convert value "+object+" to a datetime value");
		}
		java.text.DateFormat format=null;
		
		if("short".equalsIgnoreCase(mask)) 
			format=java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT, locale);
		else if("medium".equalsIgnoreCase(mask)) 
			format=java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.MEDIUM, locale);
		else if("long".equalsIgnoreCase(mask)) 
			format=java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.LONG, java.text.DateFormat.LONG, locale);
		else if("full".equalsIgnoreCase(mask)) 
			format=java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.FULL, java.text.DateFormat.FULL, locale);
		else {
			SimpleDateFormat sdf;
			format = sdf= new SimpleDateFormat(convertMask(mask), locale);
			if(mask!=null &&  StringUtil.indexOfIgnoreCase(mask, "tt")==-1 && StringUtil.indexOfIgnoreCase(mask, "t")!=-1) {
				DateFormatSymbols dfs = new DateFormatSymbols(locale);
				dfs.setAmPmStrings(AP);
				sdf.setDateFormatSymbols(dfs);
			}
		}
		format.setTimeZone(tz);
        return format.format(datetime);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==1)return call(pc,args[0]);
		if(args.length==2)return call(pc,args[0],Caster.toString(args[1]));
		return call(pc,args[0],Caster.toString(args[1]),Caster.toString(args[2]));
	}
	


	private static String convertMask(String mask) {
		if(mask==null) return DEFAULT_MASK;
		boolean inside=false;
		char[] carr = mask.toCharArray();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<carr.length;i++){
			
			switch(carr[i]){
			case 'm': if(!inside){sb.append('M');}else{sb.append(carr[i]);} break;
			case 'S': if(!inside){sb.append('s');}else{sb.append(carr[i]);} break;
			case 't': if(!inside){sb.append('a');}else{sb.append(carr[i]);} break;
			case 'T': if(!inside){sb.append('a');}else{sb.append(carr[i]);} break;
			case 'n': if(!inside){sb.append('m');}else{sb.append(carr[i]);} break;
			case 'N': if(!inside){sb.append('m');}else{sb.append(carr[i]);} break;
			case 'l': if(!inside){sb.append('S');}else{sb.append(carr[i]);} break;
			case 'L': if(!inside){sb.append('S');}else{sb.append(carr[i]);} break;
			
			case 'f': if(!inside){sb.append("'f'");}else{sb.append(carr[i]);} break;
			case 'e': if(!inside){sb.append("'e'");}else{sb.append(carr[i]);} break;
			
			case 'G': 
			case 'y': 
			case 'M': 
			case 'W': 
			case 'w': 
			case 'D': 
			case 'd': 
			case 'F': 
			case 'E': 
			case 'a': 
			case 'H': 
			case 'h': 
			case 'K': 
			case 'k': 
			case 'Z': 
			case 'z': 
			case 's': 
			//case '.': 
					sb.append(carr[i]);
			break;
			

			case '\'':
				if(carr.length-1>i) {
					if(carr[i+1]=='\'') {
						i++;
						sb.append("''");
						break;
					}
				}
				
				
				inside=!inside;
				sb.append(carr[i]);
			break;
			/*case '\'':
				if(carr.length-1>i) {
					if(carr[i+1]=='\'') {
						i++;
						sb.append("''");
						break;
					}
				}
				sb.append("''");
			break;*/
			default:
				char c=carr[i];
				if(!inside && ((c>='a' && c<='z') || (c>='A' && c<='Z')))
					sb.append('\'').append(c).append('\'');
				else
					sb.append(c);
			}
		}
		return sb.toString();
	}
}