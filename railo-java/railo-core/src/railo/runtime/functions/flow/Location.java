package railo.runtime.functions.flow;

import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;

/**
 * Decodes Binary Data that are encoded as String
 */
public final class Location implements Function {

	private static final Key URL = KeyImpl.getInstance("url");
	
	/*public static boolean call(PageContext pc, Object[] args) throws PageException {
		// TODO this code should be replaced with a more generic solution in railo 3.1
		// in other wrds it should be possible to call a tag as a function
		
		railo.runtime.tag.Location location= 
			(railo.runtime.tag.Location) pc.use("railo.runtime.tag.Location");
		 
		
		Struct sct=TagUtil.toAttributeStruct(null, args);
		
		try {
		    TagUtil.setAttributeCollection
			(pc, location,
			 (new MissingAttribute[]{ MissingAttribute.newInstance(URL, "string") }),
			  
			sct,
			TagLibTag.ATTRIBUTE_TYPE_FIXED );
		    
		    location.doStartTag();
		    if (location.doEndTag() == Tag.SKIP_PAGE)throw Abort.newInstance(0);
		} 
		finally {
		    pc.reuse(location);
		}
		
		
		return false;
	}*/
}