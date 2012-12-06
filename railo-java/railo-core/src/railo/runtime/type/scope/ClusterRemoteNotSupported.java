package railo.runtime.type.scope;

import railo.runtime.config.ConfigServer;

public final class ClusterRemoteNotSupported implements ClusterRemote {

	public void addEntry(ClusterEntry entry) {}

	@Override
	public void broadcastEntries() {
		//print.out("ClusterRemote#broadcastEntries()");
	}

	@Override
	public boolean checkValue(Object value) {
		return true;
	}

	@Override
	public ClusterRemote duplicate() {
		return new ClusterRemoteNotSupported();
	}

	public void init(ConfigServer configServer, Cluster cluster) {}

}
