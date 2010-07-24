package railo.runtime.config;

import railo.runtime.type.Struct;

public interface AdminSync {
	public void broadcast(Struct attributes, Config config);
}
