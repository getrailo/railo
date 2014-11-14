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
package railo.runtime.exp;

import railo.runtime.PageContext;

public class RequestTimeoutException extends Abort implements Stop {

	private PageContext pc;
	private StackTraceElement[] stacktrace;

	public RequestTimeoutException(PageContext pc,String msg) {
		super(SCOPE_REQUEST,msg);
		this.pc=pc;
		this.stacktrace=pc!=null?pc.getThread().getStackTrace():null;
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		return stacktrace;
	}

}
