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

package railo.cfx.example;

import com.allaire.cfx.CustomTag;
import com.allaire.cfx.Request;
import com.allaire.cfx.Response;

/**
 * CFX Hello World Example
 */
public final class HelloWorld implements CustomTag{

    /**
     * @see com.allaire.cfx.CustomTag#processRequest(com.allaire.cfx.Request, com.allaire.cfx.Response)
     */
    public void processRequest(Request request, Response response) throws Exception {
        
        if(request.attributeExists("name"))
            response.write("hello "+request.getAttribute("name"));
        else
            response.write("hello");
    }
}