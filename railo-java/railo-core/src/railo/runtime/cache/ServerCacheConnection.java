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
package railo.runtime.cache;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigServerImpl;
import railo.runtime.type.Struct;

public class ServerCacheConnection implements CacheConnection {

	private CacheConnection cc;
	private ConfigServerImpl cs;

	/**
	 * Constructor of the class
	 * @param configServer 
	 * @param cc
	 */
	public ServerCacheConnection(ConfigServerImpl cs, CacheConnection cc) {
		this.cs=cs;
		this.cc=cc;
	}

	@Override
	public CacheConnection duplicate(Config config) throws IOException {
		return new ServerCacheConnection(cs,cc.duplicate(config));
	}

	@Override
	public Class getClazz() {
		return cc.getClazz();
	}

	@Override
	public Struct getCustom() {
		return cc.getCustom();
	}

	@Override
	public Cache getInstance(Config config) throws IOException {
		return cc.getInstance(cs);
	}

	public String getName() {
		return cc.getName();
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isStorage() {
		return cc.isStorage();
	}

}
