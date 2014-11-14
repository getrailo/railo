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

import javax.servlet.jsp.JspEngineInfo;



/**
 * implementation of the javax.servlet.jsp.JspEngineInfo interface, 
 * return information to JSP Engine, 
 * railo is no JSP Engine but compatible to the most j2ee specification for wen applications, also the most jsp specification
 */
public final class JspEngineInfoImpl extends JspEngineInfo {

	private String version;

	/**
	 * constructor of the JSPEngineInfo
	 * @param version railo version Information
	 */
	public JspEngineInfoImpl(String version) {
		this.version=version;
	}

	@Override
	public String getSpecificationVersion() {
		// Railo Version
		return version;
	}

}