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
package railo.transformer.cfml.expression;

import railo.runtime.exp.TemplateException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.library.function.FunctionLib;
import railo.transformer.library.tag.TagLib;
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 * Zum lesen von Attributen bei dem CFML expressions nicht geparst werden sollen (cfloop condition) 
 */
public final class SimpleExprTransformer implements ExprTransformer {
	
	private char specialChar;

	public SimpleExprTransformer(char specialChar) {
		this.specialChar=specialChar;
	}

	@Override
	public Expression transformAsString(Page page,EvaluatorPool ep,TagLib[][] tld,FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings,boolean allowLowerThan) throws TemplateException {
		return transform(page,ep,tld,fld,scriptTags, cfml,settings);
	}
	
	@Override
	public Expression transform(Page page,EvaluatorPool ep,TagLib[][] tld,FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings) throws TemplateException {
			Expression expr=null;
			// String
				if((expr=string(cfml))!=null) {
					return expr;
				}
			// Simple
				return simple(cfml);
	}
	/**
	 * Liest den String ein
	 * @return Element 
	 * @throws TemplateException
	 */
	public Expression string(CFMLString cfml)
		throws TemplateException {
		cfml.removeSpace();
		char quoter=cfml.getCurrentLower();
		if(quoter!='"' && quoter!='\'')
			return null;
		StringBuffer str=new StringBuffer();
		boolean insideSpecial=false;
	
		Position line = cfml.getPosition();
		while(cfml.hasNext()) {
			cfml.next();
			// check special
			if(cfml.isCurrent(specialChar)) {
				insideSpecial=!insideSpecial;
				str.append(specialChar);
							
			}
			// check quoter
			else if(!insideSpecial && cfml.isCurrent(quoter)) {
				// Ecaped sharp
				if(cfml.isNext(quoter)){
					cfml.next();
					str.append(quoter);
				}
				// finsish
				else {
					break;
				}				
			}
			// all other character
			else {
				str.append(cfml.getCurrent());
			}
		}		


		if(!cfml.forwardIfCurrent(quoter))
			throw new TemplateException(cfml,"Invalid Syntax Closing ["+quoter+"] not found");
	
		LitString rtn = new LitString(str.toString(),line,cfml.getPosition());
		cfml.removeSpace();
		return rtn;
	}
	
	/**
	 * Liest ein
	 * @return Element
	 * @throws TemplateException
	 */
	public Expression simple(CFMLString cfml) throws TemplateException {
		StringBuffer sb=new StringBuffer();
		Position line = cfml.getPosition();
		while(cfml.isValidIndex()) {
			if(cfml.isCurrent(' ') || cfml.isCurrent('>') || cfml.isCurrent("/>")) break;
			else if(cfml.isCurrent('"') || cfml.isCurrent('#') || cfml.isCurrent('\'')) {
				throw new TemplateException(cfml,"simple attribute value can't contain ["+cfml.getCurrent()+"]");
			}
			else sb.append(cfml.getCurrent());
			cfml.next();
		}
		cfml.removeSpace();
		
		return new LitString(sb.toString(),line,cfml.getPosition());
	}
	

}