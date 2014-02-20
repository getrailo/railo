package railo.transformer.cfml.script;

import railo.commons.lang.ParserString;
import railo.commons.lang.StringUtil;
import railo.transformer.Factory;
import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.expression.Expression;
import railo.transformer.expression.literal.LitBoolean;

public class DocCommentTransformer {
	
	public synchronized DocComment transform(Factory f,String str){
		try{
			DocComment dc = new DocComment();
			str=str.trim();
			if(str.startsWith("/**")) str=str.substring(3);
			if(str.endsWith("*/")) str=str.substring(0,str.length()-2);
			ParserString ps=new ParserString(str);
			transform(f,dc,ps);
			dc.getHint();// TODO do different -> make sure internal structure is valid
			return dc;
		}
		catch(Throwable t){
			return null;
		}
	}

	private void transform(Factory factory,DocComment dc, ParserString ps) {
		while(ps.isValidIndex()){
			asterix(ps);
			ps.removeSpace();
			// param
			if(ps.forwardIfCurrent('@')){
				dc.addParam(param(factory,ps));
			}
			// hint
			else {
				while(ps.isValidIndex() && ps.getCurrent()!='\n'){
					dc.addHint(ps.getCurrent());
					ps.next();
				}
				dc.addHint('\n');
			}
			ps.removeSpace();
		}
	}

	private Attribute param(Factory factory,ParserString ps) {
		String name=paramName(ps);
		if(name==null) return new Attribute(true,"@",factory.TRUE(),"boolean");
		
		// white space
		while(ps.isValidIndex() && ps.isCurrentWhiteSpace()){
			if(ps.getCurrent()=='\n')
				return new Attribute(true,name,factory.TRUE(),"boolean");
			ps.next();
		}
		Expression value = paramValue(factory,ps);
		return new Attribute(true,name, value,value instanceof LitBoolean?"boolean":"string");
	}

	private String paramName(ParserString ps) {
		StringBuilder sb=new StringBuilder();
		while(ps.isValidIndex() && !ps.isCurrentWhiteSpace()){
			sb.append(ps.getCurrent());
			ps.next();
		}
		if(sb.length()==0) return null;
		return sb.toString();
	}

	private Expression paramValue(Factory factory,ParserString ps) {
		StringBuilder sb=new StringBuilder();
		while(ps.isValidIndex() && ps.getCurrent()!='\n'){
			sb.append(ps.getCurrent());
			ps.next();
		}
		if(sb.length()==0) return factory.TRUE();
		return factory.createLitString(unwrap(sb.toString()));
	}

	public static String unwrap(String str) {
		str = str.trim();
		if(StringUtil.startsWith(str, '"') && StringUtil.endsWith(str, '"'))
			str=str.substring(1,str.length()-1);
		if(StringUtil.startsWith(str, '\'') && StringUtil.endsWith(str, '\''))
			str=str.substring(1,str.length()-1);
		return str;
	}

	private void asterix(ParserString ps) {
		do {
			ps.removeSpace();
		}while(ps.forwardIfCurrent('*'));
		
	}
	
}
