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
package railo.runtime.orm.hibernate.naming;

import railo.loader.util.Util;
import railo.runtime.orm.naming.NamingStrategy;

public class SmartNamingStrategy implements NamingStrategy {
	
	public static final NamingStrategy INSTANCE = new SmartNamingStrategy();

	@Override
	public String convertTableName(String tableName) {
        return translate(tableName);
    }

    @Override
    public String convertColumnName(String columnName) { 
        return translate(columnName);
    }

    private static String translate(String name) {
    	if(Util.isEmpty(name)) return "";
        
    	int len=name.length();
    	StringBuilder sb = new StringBuilder();
    	char c,p,n;
        for(int i=0;i<len;i++) {
        	c=name.charAt(i);
        	if(i==0 || i+1==len) {
        		sb.append(Character.toUpperCase(c));
        		 continue;
        	}
        	p=name.charAt(i-1);
        	n=name.charAt(i+1);
            
            // is Camel
        	if(Character.isLowerCase(p) && Character.isUpperCase(c) && Character.isLowerCase(n)) {
        		sb.append('_');
        		sb.append(Character.toUpperCase(c));
        		sb.append(Character.toUpperCase(n));
        		i++;
        	}
        	else
        		sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }

	@Override
	public String getType() {
		return "smart";
	}

}
