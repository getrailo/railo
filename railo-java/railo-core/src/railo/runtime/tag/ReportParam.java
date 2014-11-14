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

import javax.servlet.jsp.tagext.Tag;

import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.TagNotSupported;
import railo.runtime.ext.tag.TagImpl;

public final class ReportParam extends TagImpl {
	
	private ReportParamBean param=new ReportParamBean();
	

	public ReportParam() throws TagNotSupported {
		// TODO implement tag
		throw new TagNotSupported("ReportParam");
	}
	
	@Override
	public void release() {
		this.param=new ReportParamBean();
		super.release();
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		param.setName(name);
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		param.setValue(value);
	}
	
	public int doStartTag() throws ApplicationException {
		// check 
		
		// provide to parent
		Tag parent=this;
		do{
			parent = parent.getParent();
			if(parent instanceof Report) {
				((Report)parent).addReportParam(param);
				break;
			}
		}
		while(parent!=null);
		
		return SKIP_BODY;
	}
}

