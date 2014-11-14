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
package railo.runtime.functions.owasp;

import java.io.PrintStream;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.EncodingException;

import railo.commons.io.DevNullOutputStream;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public class ESAPIDecode implements Function {
	
	private static final long serialVersionUID = 7054200748398531363L;
	
	public static final short DEC_BASE64=1;
	public static final short DEC_URL=2;
	
	public static String decode(String item, short decFrom) throws PageException  {
		
		PrintStream out = System.out;
		try {
			 System.setOut(new PrintStream(DevNullOutputStream.DEV_NULL_OUTPUT_STREAM));
			 Encoder encoder = ESAPI.encoder();
			 switch(decFrom){
			 case DEC_URL:return encoder.decodeFromURL(item);
			 }
			 throw new ApplicationException("invalid target decoding defintion");
		}
		catch(EncodingException ee){
			throw Caster.toPageException(ee);
		}
		finally {
			 System.setOut(out);
		}
	}
	
	public static String call(PageContext pc , String strDecodeFrom, String value) throws PageException{
		short decFrom;
		strDecodeFrom=StringUtil.emptyIfNull(strDecodeFrom).trim().toLowerCase();
		if("url".equals(strDecodeFrom)) decFrom=DEC_URL;
		else 
			throw new FunctionException(pc, "ESAPIDecode", 1, "decodeFrom", "value ["+strDecodeFrom+"] is invalid, valid values are " +
					"[url]");
		return decode(value, decFrom);
	}
	
}
