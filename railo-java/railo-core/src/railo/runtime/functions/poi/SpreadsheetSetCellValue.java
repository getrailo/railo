package railo.runtime.functions.poi;

import railo.runtime.PageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.poi.Excel;

public class SpreadsheetSetCellValue {

	public static String call(PageContext pc,Excel excel, String value, double row, double column) throws PageException {
		try {
			excel.setValue(((int)row)-1,((int)column)-1,value);
		} catch (CasterException e) {
			throw Caster.toPageException(e);
		}
		
		return null;
	}
	
}
