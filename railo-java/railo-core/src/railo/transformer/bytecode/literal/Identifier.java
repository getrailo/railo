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
package railo.transformer.bytecode.literal;

import railo.transformer.bytecode.Position;


public class Identifier extends LitString {

	public static short CASE_ORIGNAL=0;
	public static short CASE_UPPER=1;
	public static short CASE_LOWER=2;
	private String raw;
	private short _case;
	


	public static Identifier toIdentifier(String str, Position start,Position end) {
		return new Identifier(str, CASE_ORIGNAL,start,end);
	}

	public static Identifier toIdentifier(String str, short _case, Position start,Position end) {
		return new Identifier(str, _case,start,end);
	}
	
	private Identifier(String str, short _case,Position start,Position end) {
		super(convert(str,_case), start,end);
		this.raw=str;
		this._case=_case;
	}

	/**
	 * @return the raw
	 */
	public String getRaw() {
		return raw;
	}

	/**
	 * @return the _case
	 */
	public short getCase() {
		return _case;
	}

	private static String convert(String str, short _case) {
		if(CASE_UPPER==_case) return str.toUpperCase();
		if(CASE_LOWER==_case) return str.toLowerCase();
		return str;
	}


	public String getUpper() {
		if(CASE_UPPER==_case)return getString();
		return raw.toUpperCase();
	}

	public String getLower() {
		if(CASE_LOWER==_case)return getString();
		return raw.toLowerCase();
	}

}
