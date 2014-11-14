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
package railo.runtime.debug;

import java.io.Serializable;

public interface DebugTrace extends Serializable {

	/**
	 * @return the category
	 */
	public String getCategory();

	/**
	 * @return the line
	 */
	public int getLine();

	/**
	 * @return the template
	 */
	public String getTemplate();

	/**
	 * @return the text
	 */
	public String getText();

	/**
	 * @return the time
	 */
	public long getTime();

	/**
	 * @return the type
	 */
	public int getType();

	/**
	 * @return the var
	 */
	public String getVarName();
	/**
	 * @return the var
	 */
	public String getVarValue();
	
	public String getAction();
	
}
