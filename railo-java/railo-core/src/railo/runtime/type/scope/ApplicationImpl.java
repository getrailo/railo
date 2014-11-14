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
package railo.runtime.type.scope;

import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.functions.system.GetApplicationSettings;
import railo.runtime.listener.ApplicationContext;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;



/**
 * Session Scope
 */
public final class ApplicationImpl extends ScopeSupport implements Application,SharedScope {

	private static final long serialVersionUID = 700830188207594563L;
	
	private static final Collection.Key APPLICATION_NAME = KeyImpl.intern("applicationname");
	private long lastAccess;
	private long timeSpan;
	private long created;
	
	/**
	 * default constructor of the session scope
	 */
	public ApplicationImpl() {
		super(true,"application",SCOPE_APPLICATION);
		created = System.currentTimeMillis();
	}

	@Override
	public long getLastAccess() { 
		return lastAccess;
	}

	@Override
	public long getTimeSpan() { 
	    return timeSpan;
	}

	@Override
	public void touchBeforeRequest(PageContext pc){
	    ApplicationContext appContext = pc.getApplicationContext();
	    setEL(APPLICATION_NAME,appContext.getName());
	    timeSpan=appContext.getApplicationTimeout().getMillis();
		lastAccess=System.currentTimeMillis();
	}

	public void touchAfterRequest(PageContext pc) {
		// do nothing
	}

    @Override
    public boolean isExpired() {
        return (lastAccess+timeSpan)<System.currentTimeMillis();
    }

	/**
	 * @param lastAccess the lastAccess to set
	 */
	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	@Override
	public void touch() {
		lastAccess=System.currentTimeMillis();
	}
	
	/**
	 * undocumented Feature in ACF
	 * @return
	 */
	public Map getApplicationSettings(){
		return GetApplicationSettings.call(ThreadLocalPageContext.get());
	}

	@Override
	public long getCreated() {
		return created;
	}
}