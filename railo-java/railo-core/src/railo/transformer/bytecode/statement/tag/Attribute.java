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

import railo.transformer.bytecode.expression.Expression;

public final class Attribute {

	final String nameOC;
	final String nameLC;
	final Expression value;
	private final String type;
	private final boolean dynamicType;
	private boolean defaultAttribute;
	private String setterName;
	
	public Attribute(boolean dynamicType,String name, Expression value, String type) {
		this.dynamicType = dynamicType;
		this.nameOC = name;
		this.nameLC = name.toLowerCase();
		this.value = value;
		this.type = type;
	}
	
	public boolean isDefaultAttribute() {
		return defaultAttribute;
	}

	public void setDefaultAttribute(boolean defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return nameLC;
	}
	// TODO make this method obsolete
	public String getNameOC() {
		return nameOC;
	}

	/**
	 * @return the value
	 */
	public Expression getValue() {
		return value;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the dynamicType
	 */
	public boolean isDynamicType() {
		return dynamicType;
	}
	
	public String toString(){
		return "name:"+this.nameOC
		+";value:"+this.value
		+";type:"+this.type
		+";dynamicType:"+this.dynamicType
		+";setterName:"+this.setterName;
	}
}
