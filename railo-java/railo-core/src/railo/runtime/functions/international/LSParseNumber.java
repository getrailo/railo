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
package railo.runtime.functions.international;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.WeakHashMap;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

/**
 * Implements the CFML Function lsparsecurrency
 */
public final class LSParseNumber implements Function {
	
	private static final long serialVersionUID = 2219030609677513651L;
	
	private static WeakHashMap<Locale,NumberFormat> whm=new WeakHashMap<Locale,NumberFormat>();

	public static double call(PageContext pc , String string) throws PageException {
		return toDoubleValue(pc.getLocale(),string);
	}
	
	public static double call(PageContext pc , String string,String strLocale) throws PageException {
		return toDoubleValue(strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale),string);
	}
	
	
	public synchronized static double toDoubleValue(Locale locale,String str) throws PageException {
		Object o=whm.get(locale);
		NumberFormat nf=null;
		if(o==null) {
			nf=NumberFormat.getInstance(locale);
			whm.put(locale,nf);
		}
		else {
			nf=(NumberFormat) o;
		}
		str=optimze(str.toCharArray());
		
		ParsePosition pp = new ParsePosition(0);
        Number result = nf.parse(str, pp);
		
        if (pp.getIndex() < str.length()) {
            throw new ExpressionException("can't parse String [" + str + "] against locale ["+LocaleFactory.toString(locale)+"] to a number");
        }
        if(result==null)
        	throw new ExpressionException("can't parse String [" + str + "] against locale ["+LocaleFactory.toString(locale)+"] to a number");
        return result.doubleValue();
		
	}
	
	
	private static String optimze(char[] carr) {
		StringBuilder sb=new StringBuilder();
		char c;
		for(int i=0;i<carr.length;i++){
			c=carr[i];
			if(!Character.isWhitespace(c) && c!='+')sb.append(carr[i]);
		}
		
		return sb.toString();
	}

	
	
}