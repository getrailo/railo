package railo.transformer.cfml.expression;

import railo.runtime.exp.TemplateException;
import railo.transformer.Factory;
import railo.transformer.Position;
import railo.transformer.bytecode.Page;
import railo.transformer.cfml.ExprTransformer;
import railo.transformer.cfml.TransfomerSettings;
import railo.transformer.cfml.evaluator.EvaluatorPool;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitString;
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
	public Expression transformAsString(Factory factory,Page page,EvaluatorPool ep,TagLib[][] tld,FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings,boolean allowLowerThan) throws TemplateException {
		return transform(factory,page,ep,tld,fld,scriptTags, cfml,settings);
	}
	
	@Override
	public Expression transform(Factory factory,Page page,EvaluatorPool ep,TagLib[][] tld,FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings) throws TemplateException {
			Expression expr=null;
			// String
				if((expr=string(factory,cfml))!=null) {
					return expr;
				}
			// Simple
				return simple(factory,cfml);
	}
	/**
	 * Liest den String ein
	 * @return Element 
	 * @throws TemplateException
	 */
	public Expression string(Factory f,CFMLString cfml)
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
	
		LitString rtn = f.createLitString(str.toString(),line,cfml.getPosition());
		cfml.removeSpace();
		return rtn;
	}
	
	/**
	 * Liest ein
	 * @return Element
	 * @throws TemplateException
	 */
	public Expression simple(Factory f,CFMLString cfml) throws TemplateException {
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
		
		return f.createLitString(sb.toString(),line,cfml.getPosition());
	}
	

}