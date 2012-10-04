package railo.runtime.poi;

import railo.runtime.exp.CasterException;

public class ExcelUtil {

	public static short format(String strFormat, short defaultValue){
		strFormat=strFormat.trim().toUpperCase();
		if("XSSF".equals(strFormat)) return Excel.FORMAT_XSSF;
		if("HSSF".equals(strFormat)) return Excel.FORMAT_HSSF;
		return defaultValue;
	}
	
	public static String format(short format, String defaultValue){
		if(Excel.FORMAT_XSSF==format) return "XSSF";
		if(Excel.FORMAT_HSSF==format) return "HSSF";
		return defaultValue;
	}

	public static Excel toExcel(Object obj,Excel defaultValue) {
		if(obj instanceof Excel) return (Excel) obj;
		return defaultValue;
	}

	public static Excel toExcel(Object obj) throws CasterException {
		if(obj instanceof Excel) return (Excel) obj;
		throw new CasterException(obj,"Excel");
	}
}
