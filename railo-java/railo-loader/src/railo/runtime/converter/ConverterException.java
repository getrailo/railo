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

package railo.runtime.converter;


/**
 * Exception throwed by a Converter
 */
public final class ConverterException extends Exception {

	private static final long serialVersionUID = -5591914619316366638L;

	/**
	 * constructor of the Exception
	 * @param message
	 */
	public ConverterException(String message) {
		super(message);
	}
}