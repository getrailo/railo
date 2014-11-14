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
/**
 * Implements the CFML Function lsdateformat
 */
package railo.runtime.functions.international;

import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.Caster;
import railo.runtime.op.date.DateCaster;
import railo.runtime.type.dt.DateTime;

public final class LSDateFormat implements Function {

	private static final long serialVersionUID = 4720003854756942610L;
	
	public static String call(PageContext pc , Object object) throws PageException {
		return _call(pc, object, "medium", pc.getLocale(),pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask) throws PageException {
		return _call(pc, object, mask, pc.getLocale(),pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask,String strLocale) throws PageException {
		return _call(pc, object, mask, LocaleFactory.getLocale(strLocale),pc.getTimeZone());
	}
	public static synchronized String call(PageContext pc , Object object, String mask,String strLocale,String strTimezone) throws PageException {
		return _call(pc, object, mask, 
				strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale),
				strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	
	private static synchronized String _call(PageContext pc , Object object, String mask,Locale locale,TimeZone tz) throws PageException {
		if(StringUtil.isEmpty(object)) return "";
		
		return new railo.runtime.format.DateFormat(locale).
			format(toDateLS(pc ,locale,tz, object),mask,tz);
	}

	private static DateTime toDateLS(PageContext pc ,Locale locale, TimeZone timeZone, Object object) throws PageException {
		if(object instanceof DateTime) return (DateTime) object;
		else if(object instanceof CharSequence) {
			DateTime res = DateCaster.toDateTime(locale,Caster.toString(object),timeZone,null,locale.equals(Locale.US));
			if(res!=null)return res;
		}
		return DateCaster.toDateAdvanced(object,timeZone);
	}
}