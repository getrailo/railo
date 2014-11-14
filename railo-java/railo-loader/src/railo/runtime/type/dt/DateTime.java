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
package railo.runtime.type.dt;

import java.io.Serializable;
import java.util.Date;

import railo.runtime.dump.Dumpable;
import railo.runtime.op.Castable;

/**
 * 
 */
public abstract class DateTime extends Date implements Dumpable,Castable,Serializable {

    /**
     * constructor of the class
     * @param time
     */
    public DateTime(long time) {
        super(time);
    }

    /**
     * constructor of the class
     */
    public DateTime() {
        super();
    }
    
    /**
     * @return returns the CFML type double value represent a date
     */
    public abstract double toDoubleValue();
}