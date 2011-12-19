package railo.runtime.type.scope;

import railo.runtime.config.ConfigServer;

public final class ClusterRemoteNotSupported implements ClusterRemote {

	public void addEntry(ClusterEntry entry) {}

	/**
	 * @see railo.runtime.type.scope.ClusterRemote#broadcastEntries()
	 */
	public void broadcastEntries() {
		//print.out("ClusterRemote#broadcastEntries()");
	}

	/**
	 * @see railo.runtime.type.scope.ClusterRemote#checkValue(java.lang.Object)
	 */
	public boolean checkValue(Object value) {
		return true;
	}

	/**
	 * @see railo.runtime.type.scope.ClusterRemote#duplicate()
	 */
	public ClusterRemote duplicate() {
		return new ClusterRemoteNotSupported();
	}

	public void init(ConfigServer configServer, Cluster cluster) {}

}
