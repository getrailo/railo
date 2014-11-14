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
package railo.runtime.tag;

import railo.runtime.Mapping;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.customtag.InitFile;
import railo.runtime.exp.PageException;

public class CFTagCore extends CFTag {

	private String name;
	private String filename;
	private boolean isweb;


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	
	public void set__name(String name){
		this.name=name;
	}
	public void set__filename(String filename){
		this.filename=filename;
	}
	public void set__isweb(boolean isweb){
		this.isweb=isweb;
	}
	public InitFile initFile(PageContext pageContext) throws PageException {
    	return createInitFile(pageContext,isweb,filename);
     
    }
	
	public static InitFile createInitFile(PageContext pageContext,boolean isweb,String filename) {
    	ConfigWebImpl config = (ConfigWebImpl) pageContext.getConfig();
    	Mapping mapping=isweb?config.getTagMapping():config.getServerTagMapping();
    	
    	return new InitFile(
    			mapping.getPageSource(filename),
    			filename,
    			filename.endsWith('.'+config.getCFCExtension()));
     
    }
}
