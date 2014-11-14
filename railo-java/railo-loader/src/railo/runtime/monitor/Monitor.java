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
package railo.runtime.monitor;

import railo.runtime.config.ConfigServer;

public interface Monitor {

	public static final short TYPE_INTERVALL = 1;// FUTURE change to INTERVAL
	public static final short TYPE_REQUEST = 2;
	public static final short TYPE_ACTION = 4;// added with Railo 4.1
	

	public void init(ConfigServer configServer, String name, boolean logEnabled);

	public short getType();
	public String getName();
	public Class getClazz();
	public boolean isLogEnabled(); 
}
