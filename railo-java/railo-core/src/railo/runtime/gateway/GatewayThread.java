package railo.runtime.gateway;


public class GatewayThread extends Thread {

		public static final int START=0;
		public static final int STOP=1;
		public static final int RESTART=2;
		
		private GatewayEngine engine;
		private Gateway gateway;
		private int action;

		public GatewayThread(GatewayEngine engine,Gateway gateway,int action){
			this.engine=engine;
			this.gateway=gateway;
			this.action=action;
		}
		
		@Override
		public void run(){
			// MUST handle timout
			try {
			if(action==START) gateway.doStart();
			else if(action==STOP) gateway.doStop();
			else if(action==RESTART) gateway.doRestart();
			}
			catch(Throwable ge){
				engine.log(gateway,GatewayEngine.LOGLEVEL_ERROR,ge.getMessage());
			}
		}
	}