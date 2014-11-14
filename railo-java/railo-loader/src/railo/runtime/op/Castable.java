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
package railo.runtime.op;

import java.io.Serializable;

import railo.runtime.exp.PageException;
import railo.runtime.type.dt.DateTime;

/**
 * Interface to define a Object as Castable, for Railo Type Casts
 */
public interface Castable extends Serializable{

    /**
     * cast the castable value to a string, other than the Method toString, this Method can throw a Exception
     * @return String representation of the Object
     * @throws PageException
     */
	public String castToString() throws PageException; 
	
    /**
     * cast the castable value to a string, return the default value, when the method is not castable
     * @return String representation of the Object
     * @throws PageException
     */
	public String castToString(String defaultValue); 
	
	/**
	 * cast the castable value to a boolean value
	 * @return boolean Value representation of the Object
	 * @throws PageException
	 */
	public boolean castToBooleanValue() throws PageException;
	
	/**
	 * cast the castable value to a boolean value
	 * @return boolean Value representation of the Object
	 * @throws PageException
	 */
	public Boolean castToBoolean(Boolean defaultValue);
	
	/**
	 * cast the castable value to a double value
	 * @return double Value representation of the Object
	 * @throws PageException
	 */
	public double castToDoubleValue() throws PageException;
	
	/**
	 * cast the castable value to a double value
	 * @return double Value representation of the Object
	 * @throws PageException
	 */
	public double castToDoubleValue(double defaultValue);
	
	/**
	 * cast the castable value to a date time object
	 * @return date time  representation of the Object
	 * @throws PageException
	 */
	public DateTime castToDateTime() throws PageException;
	
	/**
	 * cast the castable value to a date time object
	 * @param defaultValue returned when it is not possible to cast to a dateTime object
	 * @return date time  representation of the Object
	 * @throws PageException
	 */
	public DateTime castToDateTime(DateTime defaultValue);


    public int compareTo(String str) throws PageException;
    public int compareTo(boolean b) throws PageException;
    public int compareTo(double d) throws PageException;
    public int compareTo(DateTime dt) throws PageException;


}