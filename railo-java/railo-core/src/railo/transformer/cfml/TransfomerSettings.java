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
package railo.transformer.cfml;

import railo.runtime.Mapping;
import railo.runtime.MappingImpl;


public class TransfomerSettings {
	private static final TransfomerSettings TRANS_SETTING_DOT_NOT_UPPER = new TransfomerSettings(true);
	private static final TransfomerSettings TRANS_SETTING_DOT_NOT_ORIGINAL = new TransfomerSettings(false);
	public final boolean dotNotationUpper;

	public TransfomerSettings(boolean dotNotationUpper) {
		this.dotNotationUpper = dotNotationUpper;
	}

	public static TransfomerSettings toSetting(Mapping mapping,Boolean dotNotationUpperCase) {
		if(dotNotationUpperCase!=null) 
			return dotNotationUpperCase.booleanValue()?TRANS_SETTING_DOT_NOT_UPPER:TRANS_SETTING_DOT_NOT_ORIGINAL;
		return ((MappingImpl)mapping).getDotNotationUpperCase()?TRANS_SETTING_DOT_NOT_UPPER:TRANS_SETTING_DOT_NOT_ORIGINAL;
	}
 
}
