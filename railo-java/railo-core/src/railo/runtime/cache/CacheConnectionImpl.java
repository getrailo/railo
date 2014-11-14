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
import railo.commons.io.cache.exp.CacheException;
import railo.commons.lang.ClassUtil;
import railo.commons.net.JarLoader;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWeb;
import railo.runtime.reflection.Reflector;
import railo.runtime.tag.Admin;
import railo.runtime.type.Struct;


public class CacheConnectionImpl implements CacheConnection  {



		private String name;
		private Class clazz;
		private Struct custom;
		private Cache cache;
		private boolean readOnly;
		private boolean storage;

		public CacheConnectionImpl(Config config,String name, Class clazz, Struct custom, boolean readOnly, boolean storage) throws CacheException {
			this.name=name;
			this.clazz=clazz;
			if(!Reflector.isInstaneOf(clazz, Cache.class))
				throw new CacheException("class ["+clazz.getName()+"] does not implement interface ["+Cache.class.getName()+"]");
			this.custom=custom;
			this.readOnly=readOnly;
			this.storage=storage;
		}

		@Override
		public Cache getInstance(Config config) throws IOException  {
			if(cache==null){
				try{
				cache=(Cache) ClassUtil.loadInstance(clazz);
				}
				catch(NoClassDefFoundError e){
					if(!(config instanceof ConfigWeb)) throw e;
					if(JarLoader.changed((ConfigWeb)config, Admin.CACHE_JARS))
						throw new IOException(
							"cannot initialize Cache ["+clazz.getName()+"], make sure you have added all the required jar files. "+
							"GO to the Railo Server Administrator and on the page Services/Update, click on \"Update JARs\".");
					throw new IOException(
								"cannot initialize Cache ["+clazz.getName()+"], make sure you have added all the required jar files. "+
								"if you have updated the JARs in the Railo Administrator, please restart your Servlet Engine.");
				}
				cache.init(config,getName(), getCustom());
			}
			return cache;
		}


		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class getClazz() {
			return clazz;
		}

		@Override
		public Struct getCustom() {
			return custom;
		}

		
		public String toString(){
			return "name:"+this.name+";class:"+this.clazz.getName()+";custom:"+custom+";";
		}


		@Override
		public CacheConnection duplicate(Config config) throws IOException {
			return new CacheConnectionImpl(config,name,clazz,custom,readOnly,storage);
		}


			@Override
			public boolean isReadOnly() {
				return readOnly;
			}
			public boolean isStorage() {
				return storage;
			}
	}
