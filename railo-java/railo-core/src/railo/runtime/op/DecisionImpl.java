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

import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;


/**
 * implementation of the interface Decision
 */
public final class DecisionImpl implements railo.runtime.util.Decision {

    private static DecisionImpl singelton;

    @Override
    public boolean isArray(Object o) {
        return Decision.isArray(o);
    }

    @Override
    public boolean isBinary(Object object) {
        return Decision.isBinary(object);
    }

    @Override
    public boolean isBoolean(Object value) {
        return Decision.isBoolean(value);
    }

    @Override
    public boolean isBoolean(String str) {
        return Decision.isBoolean(str);
    }

    @Override
    public boolean isComponent(Object object) {
        return Decision.isComponent(object);
    }

    @Override
    public boolean isDate(Object value, boolean alsoNumbers) {
        return Decision.isDateAdvanced(value,alsoNumbers);
    }

    @Override
    public boolean isEmpty(String str, boolean trim) {
        return StringUtil.isEmpty(str,trim);
    }

    @Override
    public boolean isEmpty(String str) {
        return StringUtil.isEmpty(str);
    }

    @Override
    public boolean isHex(String str) {
        return Decision.isHex(str);
    }

    @Override
    public boolean isLeapYear(int year) {
        return Decision.isLeapYear(year);
    }

    @Override
    public boolean isNativeArray(Object o) {
        return Decision.isNativeArray(o);
    }

    @Override
    public boolean isNumeric(Object value) {
        return Decision.isNumeric(value);
    }

    @Override
    public boolean isNumeric(String str) {
        return Decision.isNumeric(str);
    }

    @Override
    public boolean isObject(Object o) {
        return Decision.isObject(o);
    }

    @Override
    public boolean isQuery(Object object) {
        return Decision.isQuery(object);
    }

    @Override
    public boolean isSimpleValue(Object value) {
        return Decision.isSimpleValue(value);
    }

    @Override
    public boolean isSimpleVariableName(String string) {
        return Decision.isSimpleVariableName(string);
    }

    @Override
    public boolean isStruct(Object o) {
        return Decision.isStruct(o);
    }

    @Override
    public boolean isUserDefinedFunction(Object object) {
        return Decision.isUserDefinedFunction(object);
    }

    @Override
    public boolean isUUID(String str) {
        return Decision.isUUId(str);
    }

    @Override
    public boolean isVariableName(String string) {
        return Decision.isVariableName(string);
    }

    @Override
    public boolean isWddx(Object o) {
        return Decision.isWddx(o);
    }

    @Override
    public boolean isXML(Object o) {
        return Decision.isXML(o);
    }

    @Override
    public boolean isXMLDocument(Object o) {
        return Decision.isXMLDocument(o);
    }

    @Override
    public boolean isXMLElement(Object o) {
        return Decision.isXMLElement(o);
    }

    @Override
    public boolean isXMLRootElement(Object o) {
        return Decision.isXMLRootElement(o);
    }

    public static railo.runtime.util.Decision getInstance() {
        if(singelton==null)singelton=new DecisionImpl();
        return singelton;
    }

	@Override
	public Key toKey(Object obj) throws PageException {
		return KeyImpl.toKey(obj);
	}

	@Override
	public Key toKey(Object obj, Key defaultValue) {
		return KeyImpl.toKey(obj,defaultValue);
	}

}
