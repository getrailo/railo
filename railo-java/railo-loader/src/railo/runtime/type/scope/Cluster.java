package railo.runtime.type.scope;

import railo.runtime.config.ConfigServer;
import railo.runtime.type.Scope;

public interface Cluster extends Scope {

	public void init(ConfigServer configServer);

	/**
	 * broadcast data on stack 
	 */
	public void broadcast();

	public void setEntry(ClusterEntry entry);
}
