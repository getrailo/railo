package railo.runtime.util;

import railo.runtime.MappingImpl;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.listener.ApplicationListener;

public class PageContextUtil {

	public static ApplicationListener getApplicationListener(PageContext pc) {
		PageSource ps = pc.getBasePageSource();
		if(ps!=null) {
			MappingImpl mapp=(MappingImpl) ps.getMapping();
			if(mapp!=null) return mapp.getApplicationListener();
		}
		return pc.getConfig().getApplicationListener();
	}

}
