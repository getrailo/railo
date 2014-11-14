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
package railo.cli.servlet;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


public class ServletConfigImpl implements ServletConfig {

	private String servletName;
	private ServletContext context;

	/**
	 * Constructor of the class
	 * @param parameters
	 * @param attrs
	 * @param servletName
	 */
	public ServletConfigImpl(ServletContextImpl context,String servletName){
		this.servletName=servletName;
		this.context=context;
	}
	
	/**
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String key) {
		return context.getInitParameter(key);
	}

	/**
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return context.getInitParameterNames();
	}

	/**
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	public String getServletName() {
		return servletName;
	}

	public ServletContext getServletContext() {
		return context;
	}
}