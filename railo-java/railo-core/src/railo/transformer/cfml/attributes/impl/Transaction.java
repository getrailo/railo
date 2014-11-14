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
package railo.transformer.cfml.attributes.impl;

import railo.transformer.bytecode.statement.tag.Attribute;
import railo.transformer.bytecode.statement.tag.Tag;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.cfml.attributes.AttributeEvaluator;
import railo.transformer.cfml.attributes.AttributeEvaluatorException;
import railo.transformer.library.tag.TagLibTag;

public class Transaction implements AttributeEvaluator {

	public TagLibTag evaluate(TagLibTag tagLibTag, Tag tag) throws AttributeEvaluatorException {
		Attribute action = tag.getAttribute("action");
		
		if(action!=null){
			Tag parent = ASMUtil.getAncestorTag(tag, tag.getFullname());
			if(parent!=null) {
				tagLibTag=tagLibTag.duplicate(false);
				tagLibTag.setBodyContent("empty");
			}
		}
		
		return tagLibTag;
	}

}
