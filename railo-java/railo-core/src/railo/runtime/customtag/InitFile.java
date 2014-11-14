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
package railo.runtime.customtag;

import railo.runtime.PageSource;

public class InitFile {

	private PageSource ps;
	private String filename;
	private boolean isCFC;

	public InitFile(PageSource ps,String filename,boolean isCFC){
		this.ps=ps;
		this.filename=filename;
		this.isCFC=isCFC;
	}
	
	public PageSource getPageSource() {
		return ps;
	}

	public String getFilename() {
		return filename;
	}

	public boolean isCFC() {
		return isCFC;
	}
}