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
import railo.transformer.library.tag.TagLibTag;
import railo.transformer.util.CFMLString;

/**
 * Zum lesen von Attributen bei dem CFML expressions nicht geparst werden sollen (cfloop condition) 
 */
public final class SimpleExprTransformer implements ExprTransformer {
	
	//char specialChar=0;
	//protected CFMLString cfml;

	/* *
	 * Setzt welcher Character speziell behandelt werden soll.
	 * @param c char der speziell behandelt werden soll.
	 * /
	public void setSpecialChar(char c) {
		specialChar=c;
	}*/
	

	private char specialChar;

	public SimpleExprTransformer(char specialChar) {
		this.specialChar=specialChar;
	}

	/**
	 * @see railo.transformer.cfml.ExprTransformer#transformAsString(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.CFMLString)
	 */
	public Expression transformAsString(Page page,EvaluatorPool ep,FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings,boolean allowLowerThan) throws TemplateException {
		return transform(page,ep,fld,scriptTags, cfml,settings);
	}
	
	/**
	 * @see railo.transformer.cfml.ExprTransformer#transform(railo.transformer.library.function.FunctionLib[], org.w3c.dom.Document, railo.transformer.util.CFMLString)
	 */
	public Expression transform(Page page,EvaluatorPool ep,FunctionLib[] fld,TagLibTag[] scriptTags, CFMLString cfml, TransfomerSettings settings) throws TemplateException {
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