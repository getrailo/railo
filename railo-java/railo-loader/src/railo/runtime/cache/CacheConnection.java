package railo.runtime.cache;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.runtime.config.Config;
import railo.runtime.type.Struct;

public interface CacheConnection {

	/**
	 * @return the readOnly
	 */
	public abstract boolean isReadOnly();

	public abstract Cache getInstance(Config config) throws IOException;

	/**
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * @return the clazz
	 */
	public abstract Class getClazz();

	/**
	 * @return the custom
	 */
	public abstract Struct getCustom();

	public CacheConnection duplicate(Config config) throws IOException;

	public boolean isStorage();

}