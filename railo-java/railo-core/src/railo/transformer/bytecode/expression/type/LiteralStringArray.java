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
package railo.transformer.bytecode.expression.type;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import railo.transformer.bytecode.BytecodeContext;
import railo.transformer.bytecode.BytecodeException;
import railo.transformer.bytecode.Position;
import railo.transformer.bytecode.expression.ExpressionBase;
import railo.transformer.bytecode.util.Types;
import railo.transformer.bytecode.visitor.ArrayVisitor;

public class LiteralStringArray extends ExpressionBase {

	private String[] arr;

	public LiteralStringArray(String[] arr){
		super(null,null);
		this.arr=arr;
	}
	public LiteralStringArray(String[] arr, Position start,Position end){
		super(start,end);
		this.arr=arr;
	}
	
	public Type _writeOut(BytecodeContext bc, int mode) throws BytecodeException {
		GeneratorAdapter adapter = bc.getAdapter();
		ArrayVisitor av=new ArrayVisitor();
        av.visitBegin(adapter,Types.STRING,arr.length);
        for(int y=0;y<arr.length;y++){
			av.visitBeginItem(adapter, y);
				adapter.push(arr[y]);
			av.visitEndItem(bc.getAdapter());
        }
        av.visitEnd();
        return Types.STRING_ARRAY;
	}
}
