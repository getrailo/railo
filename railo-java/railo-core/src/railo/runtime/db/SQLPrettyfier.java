package railo.runtime.db;

import java.util.Locale;

import railo.commons.lang.ParserString;
import railo.runtime.exp.PageException;
import railo.runtime.format.DateFormat;
import railo.runtime.format.TimeFormat;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;

public class SQLPrettyfier {
	
	public static final String PLACEHOLDER_COUNT="placeholder_count";	
	public static final String PLACEHOLDER_ASTERIX="placeholder_asterix";
	public static final String PLACEHOLDER_QUESTION="QUESTION_MARK_SIGN";
	
	
	
	public static String prettyfie(String sql){
		return prettyfie(sql,false);
	}
	
	public static String prettyfie(String sql, boolean validZql){
	
		ParserString ps=new ParserString(sql.trim());
		boolean insideString=false;
		//short insideKlammer=0;
		StringBuffer sb=new StringBuffer(sql.length());
		//char last=0;
		
		outer:while(!ps.isAfterLast()) {
			if(insideString) {
		        if(ps.isCurrent('\'')) {
		            if(!ps.hasNext() || !ps.isNext('\''))insideString=false;
		        }
		    }
			else {
				if(ps.isCurrent('\''))insideString=true;
		        else if(ps.isCurrent('?')) {
		            sb.append(" "+PLACEHOLDER_QUESTION+" ");
		            ps.next();
		            continue;
		        }
		        else if(ps.isCurrent('{')) {
		        	StringBuffer date=new StringBuffer();
		        	int pos=ps.getPos();
		        	while(true) {
		        		if(ps.isAfterLast()){
		        			ps.setPos(pos);
		        			break;
		        		}
		        		else if(ps.isCurrent('}')) {
		        			date.append('}');
		        			DateTime d;
							try {
								d = DateCaster.toDateAdvanced(date.toString(), null);
							} 
							catch (PageException e) {
								ps.setPos(pos);
			        			break;
							}
							sb.append('\'');
		        			sb.append(new DateFormat(Locale.US).format(d,"yyyy-mm-dd"));
		        			sb.append(' ');
		        			sb.append(new TimeFormat(Locale.US).format(d,"HH:mm:ss"));
		        			sb.append('\'');
		        			ps.next();
		        			continue outer;
		        		}
		        		else {
		        			date.append(ps.getCurrent());
		        			ps.next();
		        		}
		        	}
		        }
		        else if(ps.isCurrent('*')) {
		        	sb.append(" "+PLACEHOLDER_ASTERIX+" ");
		        	ps.next();
	            	//last=ps.getCurrent();
				    continue;
		        }
		        else if(validZql && ps.isCurrent('a')) {
		        		if(ps.isPreviousWhiteSpace() && ps.isNext('s') && ps.isNextNextWhiteSpace())	{
			            	ps.next();
			            	ps.next();
			            	ps.removeSpace();
			            	
			            	continue;
			            }
			        
		        }
		        /*for(int i=0;i<reseved_words.length;i++) {
		        	if(ps.isCurrent(reseved_words[i])) {
			        	int pos=ps.getPos();
			        	ps.setPos(pos+4);
			        	if(ps.isCurrentWhiteSpace()) {
			        		sb.append(" placeholder_"+reseved_words[i]+" ");
			        		continue;
			        	}
			        	if(ps.isCurrent(',')) {
			        		sb.append(" placeholder_"+reseved_words[i]+",");
			        		continue;
			        	}
			        	ps.setPos(pos);
			        }
		        }*/
				/*if(ps.isCurrent("char")) {
		        	int pos=ps.getPos();
		        	ps.setPos(pos+4);
		        	if(ps.isCurrentWhiteSpace()) {
		        		sb.append(" "+PLACEHOLDER_CHAR+" ");
		        		continue;
		        	}
		        	if(ps.isCurrent(',')) {
		        		sb.append(" "+PLACEHOLDER_CHAR+",");
		        		continue;
		        	}
		        	ps.setPos(pos);
		        }*/
			}
		    sb.append(ps.getCurrent());
			ps.next();
		}
		;	
		if(!ps.isLast(';'))sb.append(';');
		
		//print.err(sb.toString());
		//print.err("---------------------------------------------------------------------------------");
		return sb.toString();
	}
	
	
}
