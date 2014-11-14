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

public class OpUtil {

	/*public static List getTerms(Object left, Object right) {
		List list=null;
		Op op;
		
		if(left instanceof Op) {
			op=(Op) left;
			list=op.getSearchedTerms();
		}
		else {
			if(right instanceof Op) {
				op=(Op) right;
				list=op.getSearchedTerms();
				list.add(left.toString());
				return list;
			}
			list=new ArrayList();
			list.add(left.toString());
			list.add(right.toString());
			return list;
		}
		
		if(right instanceof Op) {
			op=(Op) right;
			list.addAll(op.getSearchedTerms());
		}
		else list.add(right);
		
		
		return list;
	}
	
	public static List getTerms(Object obj) {
		if(obj instanceof Op) {
			return ((Op)obj).getSearchedTerms();
		}
		List list=new ArrayList();
		list.add(obj.toString());
		
		return list;
	}*/
}
