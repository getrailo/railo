package railo.runtime.config;

import railo.runtime.type.Struct;

public class AdminSyncNotSupported implements AdminSync {

	@Override
	public void broadcast(Struct attributes, Config config) {
		//print.out("AdminSync#broadcast(");
	}

}
