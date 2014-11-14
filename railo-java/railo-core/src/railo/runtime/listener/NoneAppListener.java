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
package railo.runtime.listener;

import java.io.IOException;

import railo.runtime.CFMLFactory;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public final class NoneAppListener  extends AppListenerSupport {

	private int mode;

	
	@Override
	public void onRequest(PageContext pc,PageSource requestedPage, RequestListener rl) throws PageException {
		if(rl!=null) {
			requestedPage=rl.execute(pc, requestedPage);
			if(requestedPage==null) return;
		}
		pc.doInclude(requestedPage);
	}

	@Override
	public boolean onApplicationStart(PageContext pc) throws PageException {
		// do nothing
		return true;
	}

	@Override
	public void onSessionStart(PageContext pc) throws PageException {
		// do nothing
	}

	@Override
	public void onApplicationEnd(CFMLFactory factory, String applicationName) throws PageException {
		// do nothing	
	}

	@Override
	public void onSessionEnd(CFMLFactory cfmlFactory, String applicationName, String cfid) throws PageException {
		// do nothing
	}

	@Override
	public void onDebug(PageContext pc) throws PageException {
		try {
			if(pc.getConfig().debug())pc.getDebugger().writeOut(pc);
		} 
		catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}

	@Override
	public void onError(PageContext pc,PageException pe) {
		pc.handlePageException(pe);
	}

	public void setMode(int mode) {
		this.mode=mode;
	}

	@Override
	public int getMode() {
		return mode;
	}
	

	@Override
	public String getType() {
		return "none";
	}
}