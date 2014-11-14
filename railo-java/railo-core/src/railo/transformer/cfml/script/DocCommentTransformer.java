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
package railo.transformer.cfml.script;

import railo.commons.lang.ParserString;
import railo.commons.lang.StringUtil;
import railo.transformer.bytecode.expression.Expression;
import railo.transformer.bytecode.literal.LitBoolean;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.statement.tag.Attribute;

public class DocCommentTransformer {
	
	public synchronized DocComment transform(String str){
		try{
			DocComment dc = new DocComment();
			str=str.trim();
			if(str.startsWith("/**")) str=str.substring(3);
			if(str.endsWith("*/")) str=str.substring(0,str.length()-2);
			ParserString ps=new ParserString(str);
			transform(dc,ps);
			dc.getHint();// TODO do different -> make sure internal structure is valid
			return dc;
		}
		catch(Throwable t){
			return null;
		}
	}

	private void transform(DocComment dc, ParserString ps) {
		while(ps.isValidIndex()){
			asterix(ps);
			ps.removeSpace();
			// param
			if(ps.forwardIfCurrent('@')){
				dc.addParam(param(ps));
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

	private Attribute param(ParserString ps) {
		String name=paramName(ps);
		if(name==null) return new Attribute(true,"@",LitBoolean.TRUE,"boolean");
		
		// white space
		while(ps.isValidIndex() && ps.isCurrentWhiteSpace()){
			if(ps.getCurrent()=='\n')
				return new Attribute(true,name,LitBoolean.TRUE,"boolean");
			ps.next();
		}
		Expression value = paramValue(ps);
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

	private Expression paramValue(ParserString ps) {
		StringBuilder sb=new StringBuilder();
		while(ps.isValidIndex() && ps.getCurrent()!='\n'){
			sb.append(ps.getCurrent());
			ps.next();
		}
		if(sb.length()==0) return LitBoolean.TRUE;
		return LitString.toExprString(unwrap(sb.toString()));
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
