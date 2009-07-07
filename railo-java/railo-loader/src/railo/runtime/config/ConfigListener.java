package railo.runtime.config;

public interface ConfigListener {
	
	/**
	 * this method will be caled when a web context will be loaded
	 * @param cServer
	 * @param wConfig
	 */
	public void onLoadWebContext(ConfigServer cServer,ConfigWeb wConfig);
}
