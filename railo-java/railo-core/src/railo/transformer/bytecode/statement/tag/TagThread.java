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
package railo.transformer.bytecode.statement.tag;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.commons.lang.RandomUtil;
import railo.runtime.tag.ThreadTag;
import railo.transformer.bytecode.Body;
import railo.transformer.bytecode.BodyBase;
import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Page;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.literal.LitString;
import railo.transformer.bytecode.util.ASMUtil;
import railo.transformer.bytecode.util.Types;

public final class TagThread extends TagBaseNoFinal {

	
	
	public static final Type THREAD_TAG = Type.getType(ThreadTag.class);
	
	
	private static final Method REGISTER = new Method(
			"register",Types.VOID,new Type[]{Types.PAGE,Types.INT_VALUE});


	public TagThread(Position start,Position end) {
		super(start,end);
	}
	
	
	
	

	/**
	 *
	 * @see railo.transformer.bytecode.statement.tag.TagBase#_writeOut(railo.transformer.bytecode.BytecodeContext)
	 */
	public void _writeOut(BytecodeContext bc) throws BytecodeException {
		String action=ASMUtil.getAttributeString(this, "action","run");
		// no body
		if(!"run".equalsIgnoreCase(action)) {
			super._writeOut(bc);
			return;
		}
		
		Attribute name = getAttribute("name");
		if(name==null){
			addAttribute(new Attribute(false, "name",LitString.toExprString("thread"+RandomUtil.createRandomStringLC(20)), "string"));
		}

		GeneratorAdapter adapter = bc.getAdapter();
		Page page = ASMUtil.getAncestorPage(this);
		
		int index=page.addThread(this);
		super._writeOut(bc,false);
		
		adapter.loadLocal(bc.getCurrentTag());
		adapter.loadThis();
        adapter.push(index);
		adapter.invokeVirtual(THREAD_TAG, REGISTER);
		
	}





	/**
	 * @see railo.transformer.bytecode.statement.tag.TagBase#getBody()
	 */
	public Body getBody() {
		return new BodyBase();
	}
	
	public Body getRealBody() {
		return super.getBody();
	}
	
}
