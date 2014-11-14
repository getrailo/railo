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
package railo.commons.io.log;

import railo.commons.io.log.log4j.LogAdapter;
import railo.commons.lang.ExceptionUtil;

/**
 * Helper class for the logs
 */
public final class LogUtil {
	
	public static final int LEVEL_TRACE=5; // FUTURE add to Log interface, if log interface not get removed
    

	public static void log(Log log, int level, String logName, Throwable t) { 
		log(log,level,logName,"",t);
	}   

	public static void log(Log log, int level, String logName,String msg, Throwable t) { 
		if(log instanceof LogAdapter) {
			((LogAdapter)log).log(level, logName, msg,t);
		}
		else {
			String em = ExceptionUtil.getMessage(t);
			String est = ExceptionUtil.getStacktrace(t, false);
			if(msg.equals(em)) log.log(level, logName, em+";"+est);
			else log.log(level, logName, msg+";"+em+";"+est);
		}
	}

	public static void log(Log log, int level, String logName, String msg, StackTraceElement[] stackTrace) {
		Throwable t = new Throwable();
		t.setStackTrace(stackTrace);
		log(log,level,logName,msg,t);
	}    
	
}
