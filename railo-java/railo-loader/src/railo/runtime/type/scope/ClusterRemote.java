package railo.runtime.type.scope;

import railo.runtime.config.ConfigServer;
import railo.runtime.exp.PageException;

public interface ClusterRemote {
	
	/**
	 * broadcast data on stack and clear stack
	 */
	public void broadcastEntries();
	
	/**
	 * set entry on stack
	 * @param entry
	 */
	public void addEntry(ClusterEntry entry);
	
	
	/**
	 * check if the value can distributed over the "cluster"
	 * @param value
	 * @throws PageException 
	 */
	public boolean checkValue(Object value);

	/**
	 * duplicate this object
	 * @return duplicated object
	 */
	public ClusterRemote duplicate();
	
	/**
	 * @param configServer
	 * @param cluster
	 */
	public void init(ConfigServer configServer,Cluster cluster);
	
}
