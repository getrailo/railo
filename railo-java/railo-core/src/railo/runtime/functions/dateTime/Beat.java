package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class Beat implements Function {
    private static final double day=86400000;
    private static final TimeZone BMD=TimeZone.getTimeZone("GMT+1");

    public static double call(PageContext pc) throws PageException {
    	return call(pc,null);
    }
    public static double call(PageContext pc, Object obj) throws PageException {
    	if(obj==null)obj=new DateTimeImpl(pc);
    	
    	TimeZone tz = ThreadLocalPageContext.getTimeZone(pc);
    	DateTime date = DateCaster.toDateAdvanced(obj,tz);
    	return format(date);
    }
    public static double format(DateTime date) {
    	
        long millisInDay=DateTimeUtil.getInstance().getMilliSecondsInDay(BMD,date.getTime());
        double res = (millisInDay/day)*1000;
        return ((int)(res*1000))/1000D;
    }
}