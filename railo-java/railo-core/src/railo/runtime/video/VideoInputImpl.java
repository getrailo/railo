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
package railo.runtime.video;

import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.type.util.ListUtil;

public class VideoInputImpl implements VideoInput {

	private Resource resource;
	private String args="";
	private String path;

	/**
	 * Constructor of the class
	 * @param resource
	 */
	public VideoInputImpl(Resource resource) {
		this.resource=resource;
	}

	/**
	 * @see railo.runtime.video.VideoInput#getResource()
	 */
	public Resource getResource() {
		return resource;
	}
	
	/**
	 * @see railo.runtime.video.VideoInput#setCommand(java.lang.String, java.util.List)
	 */
	public void setCommand(String path,java.util.List args) {
		this.path=path;
		try {
			addArgs(ListUtil.listToList(args, " "));
		} catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}
	
	public void setCommand(String path,String[] args) {
		this.path=path;
		addArgs(ListUtil.arrayToList(args, " "));
	}
	
	/**
	 * @see railo.runtime.video.VideoInput#getCommandAsString()
	 */
	public String getCommandAsString() {
		return path+" "+args;
	}
	
	private void addArgs(String args) {
		if(StringUtil.isEmpty(this.args,true))
			this.args=args;
		else 
			this.args+="; "+args;
		
	}
}
