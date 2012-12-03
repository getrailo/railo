/**
 * Implements the CFML Function createdate
 */
package railo.runtime.functions.component;

import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

/**
 * @deprecated use function GetMetaData instead
 *
 */
public final class ComponentInfo implements Function {
	public static Struct call(PageContext pc , Component component) {
		DeprecatedUtil.function(pc, "ComponentInfo", "GetMetaData");
		Struct sct=new StructImpl();
	    sct.setEL(KeyConstants._name,component.getName());
	    sct.setEL(KeyConstants._fullname,component.getCallName());
	    String extend = component.getExtends();
	    if(extend==null || extend.length()==0)extend="Component";
	    sct.setEL(KeyConstants._extends,extend);
	    sct.setEL(KeyConstants._hint,component.getHint());
		
	    return sct;
	}
}