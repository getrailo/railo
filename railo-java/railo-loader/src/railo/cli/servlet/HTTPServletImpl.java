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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

public class HTTPServletImpl extends HttpServlet {
	private static final long serialVersionUID = 3270816399105433603L;
	
	private ServletConfig config;
	private ServletContext context;
	private String servletName;
	
	public HTTPServletImpl(ServletConfig config,ServletContext context, String servletName){
		this.config=config;
		this.context=context;
		this.servletName=servletName;
	}
	
	/**
	 * @see javax.servlet.GenericServlet#getServletConfig()
	 */
	public ServletConfig getServletConfig() {
		return config;
	}

	/**
	 * @see javax.servlet.GenericServlet#getServletContext()
	 */
	public ServletContext getServletContext() {
		return context;
	}

	/**
	 * @see javax.servlet.GenericServlet#getServletName()
	 */
	@Override
	public String getServletName() {
		return servletName;
	}

}
