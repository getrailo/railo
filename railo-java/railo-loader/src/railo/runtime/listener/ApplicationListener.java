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

import javax.servlet.ServletException;

import railo.runtime.CFMLFactory;
import railo.runtime.PageContext;
import railo.runtime.PageSource;
import railo.runtime.exp.PageException;

/**
 * interface for PageContext to interact with CFML
 *
 */
public interface ApplicationListener {

	public static final int MODE_CURRENT2ROOT=0;
	public static final int MODE_CURRENT=1;
	public static final int MODE_ROOT=2;
	public static final String CFC_EXTENSION="cfc";

	public void setMode(int mode);
	public int getMode();
	

	/**
	 * @return the type
	 */
	public String getType();

	/**
	 * @param type the type to set
	 *///FUTURE remove method
	public void setType(String type);
	
	/**
	 * this method will be called the application self
	 * @param pc
	 * @param requestedPage
	 * @throws PageException
	 * @throws ServletException
	 */
	public void onRequest(PageContext pc,PageSource requestedPage, RequestListener rl) throws PageException;

	/**
	 * this method will be called when a new session starts
	 * @throws PageException
	 */
	public void onSessionStart(PageContext pc) throws PageException;

	/**
	 * this method will be called when a session ends
	 * @param cfmlFactory
	 * @param applicationName
	 * @param cfid
	 * @throws PageException
	 */
	public void onSessionEnd(CFMLFactory cfmlFactory, String applicationName, String cfid) throws PageException;
  
	/**
	 * this method will be called when a new application context starts
	 * @throws PageException
	 */
	public boolean onApplicationStart(PageContext pc) throws PageException;

	/**
	 * this method will be called when a application scope ends
	 * @throws PageException 
	 */ 
	public void onApplicationEnd(CFMLFactory cfmlFactory, String applicationName) throws PageException;

	/**
	 * this method will be called when a server starts
	 * @throws PageException
	 */
	public void onServerStart() throws PageException;

	/**
	 * this method will be called when the server shutdown correctly (no crashes)
	 * @throws PageException
	 */
	public void onServerEnd() throws PageException;

	/**
	 * this method will be called if server has a error (exception) not throwed by a try-catch block
	 * @param pe PageExcpetion Exception that has been throwed
	 */
	public void onError(PageContext pc,PageException pe);

	/**
	 * called after "onRequestEnd" to generate debugging output, will only be called when debugging is enabled
	 * @throws PageException 
	 */
	public void onDebug(PageContext pc) throws PageException;

	/**
	 * will be called when server is run int a timeout
	 */
	public void onTimeout(PageContext pc);
	
	public boolean hasOnApplicationStart();
	public boolean hasOnSessionStart(PageContext pc);
}
