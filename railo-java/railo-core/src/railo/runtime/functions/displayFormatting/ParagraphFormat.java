/**
 * Implements the CFML Function paragraphformat
 */
package railo.runtime.functions.displayFormatting;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class ParagraphFormat implements Function {
	public static String call(PageContext pc , String str) {
		StringBuffer sb = new StringBuffer(str.length());
		char[] chars=str.toCharArray();
		boolean flag = false;

		for(int i=0; i<chars.length; i++)	{
			char c = chars[i];
			switch(c)	{
				case '\r':
					if(i + 1 < chars.length && chars[i+1]=='\r') flag = false;
		            sb.append(' ');
		        break;

				case '\n':
			        if(flag) {
			        	sb.append(" <P>\r\n");
			        	flag = false;
			        }
			        else	{
			        	sb.append(' ');
			        	flag = true;
			        }
		        break;
				default:
					sb.append(c);
		            flag = false;
		        break;
			}
		}
		sb.append(" <P>");
		return sb.toString();
	}
}