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
package railo.runtime.instrumentation;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class InstrumentationUtil {

	/**
	 * redefine the class with the given byte array
	 * @param clazz
	 * @param barr
	 * @return
	 */
	public static boolean redefineClassEL(Class clazz, byte[] barr){
		Instrumentation inst = InstrumentationFactory.getInstance();
	    if(inst!=null && inst.isRedefineClassesSupported()) {
	    	try {
	        	inst.redefineClasses(new ClassDefinition(clazz,barr));
				return true;
			} 
	    	catch (Throwable t) {t.printStackTrace();}
	    }
	    return false;
	}

	public static void redefineClass(Class clazz, byte[] barr) throws ClassNotFoundException, UnmodifiableClassException{
		Instrumentation inst = InstrumentationFactory.getInstance();
	    inst.redefineClasses(new ClassDefinition(clazz,barr));
	}

	public static boolean isSupported() {
		Instrumentation inst = InstrumentationFactory.getInstance();
		return (inst!=null && inst.isRedefineClassesSupported());
	} 
}