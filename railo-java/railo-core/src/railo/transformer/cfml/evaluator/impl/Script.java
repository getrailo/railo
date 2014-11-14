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
package railo.transformer.cfml.evaluator.impl;

import railo.transformer.cfml.evaluator.EvaluatorSupport;



public final class Script extends EvaluatorSupport {
	
	//private static TagLibTag javaVersion;

	/**
	 * @see railo.transformer.cfml.evaluator.EvaluatorSupport#evaluate(railo.transformer.bytecode.statement.tag.Tag, railo.transformer.library.tag.TagLibTag, railo.transformer.library.function.FunctionLib[])
	 */
	/*public void evaluate(Tag tag,TagLibTag tagLibTag,FunctionLib[] flibs) throws EvaluatorException {
		Attribute attr = tag.getAttribute("language");
		if(attr!=null) {
			Expression expr = CastString.toExprString(attr.getValue());
			if(!(expr instanceof LitString))
				throw new EvaluatorException("Attribute language of the Tag script, must be a literal string value");
			String str = ((LitString)expr).getString().trim();
			if("java".equalsIgnoreCase(str)){
				if(javaVersion==null){
					javaVersion=tag.getTagLibTag().duplicate(false);
					javaVersion.setTdbtClass(JavaScriptTransformer.class.getName());
					javaVersion.setTttClass(TagJavaScript.class.getName());
					
				}
			}
			else if(!"cfml".equalsIgnoreCase(str))
				throw new EvaluatorException("invalid value for attribute language from tag script ["+str+"], valid values are [cfml,java]");
		}
	}*/
}