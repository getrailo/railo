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
package railo.runtime.search.lucene2.query;

import railo.commons.lang.StringUtil;

public final class Concator implements Op {
	
	private Op left;
	private Op right;

	public Concator(Op left,Op right) {
		this.left=left;
		this.right=right;
	}

	@Override
	public String toString() {
		if(left instanceof Literal && right instanceof Literal) {
			String str=((Literal)left).literal+" "+((Literal)right).literal;
			return "\""+StringUtil.replace(str, "\"", "\"\"", false)+"\"";
		}
		return left+" "+right;
	}
	
}
