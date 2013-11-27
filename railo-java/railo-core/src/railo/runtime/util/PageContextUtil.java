package railo.runtime.util;

import railo.commons.lang.StringUtil;
import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.listener.ApplicationListener;
import railo.runtime.op.Caster;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;

public class PageContextUtil {

	public static ApplicationListener getApplicationListener(PageContext pc) {
		PageSource ps = pc.getBasePageSource();
		if(ps!=null) {
			MappingImpl mapp=(MappingImpl) ps.getMapping();
			if(mapp!=null) return mapp.getApplicationListener();
		}
		return pc.getConfig().getApplicationListener();
	}


	public static String getCookieDomain(PageContext pc) {
		if(!pc.getApplicationContext().isSetDomainCookies()) return null;

		String result = Caster.toString(pc.cgiScope().get(KeyConstants._server_name, null),null);

		if(!StringUtil.isEmpty(result)) {

			String listLast = ListUtil.last(result, '.');
			if ( !railo.runtime.op.Decision.isNumeric(listLast) ) {    // if it's numeric then must be IP address
				int numparts = 2;
				int listLen = ListUtil.len( result, '.', true );

				if ( listLen > 2 ) {
					if ( listLast.length() == 2 || !StringUtil.isAscii(listLast) ) {      // country TLD

						int tldMinus1 = ListUtil.getAt( result, '.', listLen - 1, true, "" ).length();

						if ( tldMinus1 == 2 || tldMinus1 == 3 )                             // domain is in country like, example.co.uk or example.org.il
							numparts++;
					}
				}

				if ( listLen > numparts )
					result = result.substring( result.indexOf( '.' ) );
				else if ( listLen == numparts )
					result = "." + result;
			}
		}

		return result;
	}
}
