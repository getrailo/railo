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
