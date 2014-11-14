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
package railo.runtime;

import java.io.Serializable;

import railo.commons.io.res.Resource;


/**
 * represent a cfml source file
 */
public interface SourceFile extends Serializable {

    /**
	 * return file object, based on physical path and relpath
	 * @return file Object
	 */
	public Resource getPhyscalFile();

	/**
     * @return return source path as String
     */
    public String getDisplayPath();

	/**
	 * @return returns the full classname (package and name) matching to filename (Example: my.package.test_cfm)
	 */
	public String getFullClassName();

	/**
	 * @return returns the a classname matching to filename (Example: test_cfm)
	 */
	public String getClassName();

	/**
	 * @return returns the a package matching to file (Example: railo.web)
	 */
	public String getPackageName();

	/**
	 * @return returns a variable string based on realpath and return it
	 */
	public String getRealPathAsVariableString();

	/**
	 * if the mapping physical path and archive is invalid or not defined, it is possible this method returns null
     * @return base Resource
     */
	public Resource getResource();
    
}