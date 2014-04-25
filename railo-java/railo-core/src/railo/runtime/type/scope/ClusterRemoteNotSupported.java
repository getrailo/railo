package railo.runtime.type.scope;

import railo.runtime.config.ConfigServer;

public final class ClusterRemoteNotSupported implements ClusterRemote {

	@Override
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

	@Override
	public void init(ConfigServer configServer, Cluster cluster) {}

}
