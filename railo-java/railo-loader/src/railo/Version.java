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
package railo;

import java.io.IOException;

import railo.loader.TP;
import railo.loader.util.Util;

/**
 * returns th current built in version
 */
public class Version {

    private static int version=-1;
    private static long created=-1;
    
    
    /**
     * @return returns the current version 
     */
    public static int getIntVersion() {
        init();
        return version;
    }
    
    /**
     * return creattion time of this version
     * @return creattion time
     */
    public static long getCreateTime() {
        init();
        return created;
    }


    private static void init() {
        if(version!=-1) return;
        String content="9000000:"+System.currentTimeMillis();
        try {
            content= Util.getContentAsString(
                    new TP().getClass().getClassLoader().getResourceAsStream("railo/version"),
                    "UTF-8");
            
            
        } 
        catch (IOException e) {} 
        
        int index=content.indexOf(':');
        version=Integer.parseInt(content.substring(0,index));
        created=Long.parseLong(content.substring(index+1));
        
    }
}
