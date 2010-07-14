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

	/**
	 * @see railo.runtime.cache.CacheConnection#duplicate(railo.runtime.config.Config)
	 */
	public CacheConnection duplicate(Config config) throws IOException {
		return new ServerCacheConnection(cs,cc.duplicate(config));
	}

	/**
	 * @see railo.runtime.cache.CacheConnection#getClazz()
	 */
	public Class getClazz() {
		return cc.getClazz();
	}

	/**
	 * @see railo.runtime.cache.CacheConnection#getCustom()
	 */
	public Struct getCustom() {
		return cc.getCustom();
	}

	/**
	 * @see railo.runtime.cache.CacheConnection#getInstance(railo.runtime.config.Config)
	 */
	public Cache getInstance(Config config) throws IOException {
		return cc.getInstance(cs);
	}

	public String getName() {
		return cc.getName();
	}

	public boolean isReadOnly() {
		return true;
	}

}
