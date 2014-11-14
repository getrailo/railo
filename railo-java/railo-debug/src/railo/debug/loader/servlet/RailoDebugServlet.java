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
package railo.debug.loader.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import railo.loader.engine.CFMLEngine;


public abstract class RailoDebugServlet extends HttpServlet {

    protected CFMLEngine engine;

    /** @see javax.servlet.Servlet#init(javax.servlet.ServletConfig) */
    @Override
    public void init( ServletConfig config ) throws ServletException {

        super.init( config );
        engine = CFMLEngineFactoryDummy.getInstance( config );
    }

    /** @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) */
    @Override
    abstract protected void service( HttpServletRequest req, HttpServletResponse rsp ) throws ServletException, IOException;
}
