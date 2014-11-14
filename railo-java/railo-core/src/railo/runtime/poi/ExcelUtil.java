/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
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
