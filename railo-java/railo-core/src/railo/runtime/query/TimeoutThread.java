package railo.runtime.query;

import railo.commons.io.SystemUtil;

public class TimeoutThread extends Thread {
	

	public void run() {
		SystemUtil.sleep(2000);
	}
	
	
	public static void execute(int timeout) {
		TimeoutThread tt = new TimeoutThread();
		try {
			tt.start();
			SystemUtil.sleep(timeout);
		}
		finally{
			if(tt.isAlive())tt.stop();
		}
	}
	
	public static void main(String[] args) {
		execute(1000);
	}
}
