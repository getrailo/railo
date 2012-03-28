package railo.runtime.functions.poi;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.poi.Excel;
import railo.runtime.poi.ExcelUtil;

public class SpreadSheetNew {
	
	private static final String DEFAULT_NAME = "Sheet1";

	public static Object call(PageContext pc) {
		return new Excel(DEFAULT_NAME, Excel.FORMAT_HSSF, 0);
	}

	public static Object call(PageContext pc, Object sheetNameOrXMLFormat) throws PageException {
		short format=toFormat(sheetNameOrXMLFormat);
		if(format==Excel.FORMAT_UNDEFINED)
			return new Excel(Caster.toString(sheetNameOrXMLFormat), Excel.FORMAT_HSSF, 0);
		return new Excel(DEFAULT_NAME, format, 0);
	}

	public static Object call(PageContext pc, Object oSheetName, Object oXmlFormat) throws PageException {
		// name
		String sheetName;
		if(oSheetName==null) sheetName=DEFAULT_NAME;
		else sheetName=Caster.toString(oSheetName);
		
		// format
		short format=toFormat(oXmlFormat);
		if(format==Excel.FORMAT_UNDEFINED)
			throw new FunctionException(pc, "SpreadSheetNew", 2, "xmlFormat", "invalid value ["+oXmlFormat+"], valid values are [true,false,XSSF,HSSF]");
		
		return new Excel(sheetName, format, 0);
	}
	
	
	
	private static short toFormat(Object xmlFormat) {
		if(Decision.isCastableToBoolean(xmlFormat)) {
			Boolean b = Caster.toBoolean(xmlFormat,null);
			if(b==null) return Excel.FORMAT_UNDEFINED;
			return b.booleanValue()?Excel.FORMAT_XSSF:Excel.FORMAT_HSSF;
		}
		return ExcelUtil.format(Caster.toString(xmlFormat,""), Excel.FORMAT_UNDEFINED);
	}
}
