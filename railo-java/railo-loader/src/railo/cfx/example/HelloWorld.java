
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