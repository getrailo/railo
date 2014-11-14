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
package railo.runtime.search;

public class SearchUtil {

	public static String translateLanguage(String language) {
        if(language==null)return null;
        language=language.toLowerCase().trim();
		
        if(language.equals("en")) return "english";
        if(language.equals("de")) 	return "german";
        if(language.equals("ru")) return "russian";
        if(language.equals("nl")) return "dutch";	
        if(language.equals("fr")) return "french";
        if(language.equals("it")) return "italian";	
        if(language.equals("no")) return "norwegian";
        if(language.equals("pt")) return "portuguese";	
        if(language.equals("sp")) return "spanish"; 	
        if(language.equals("br")) return "brazilian";
        if(language.equals("cn")) return "chinese";
        if(language.equals("gr"))  return "greek";
        if(language.equals("el"))  return "greek";
        if(language.equals("th")) return "thai";
        if(language.equals("dk")) return "danish";
        if(language.equals("jp")) return "japanese";
        if(language.equals("no")) return "norwegian";
        if(language.equals("kr") || language.equals("kp") || language.equals("ko")) 
        	return "korean";
        
        return language;
    }
}
