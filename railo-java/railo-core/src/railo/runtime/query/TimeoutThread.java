package railo.runtime.query;

import railo.print;
import railo.commons.io.SystemUtil;

public class TimeoutThread extends Thread {
	

	public void run() {
		SystemUtil.sleep(2000);
		print.e("end");
	}
	
	
	public static void execute(int timeout) {
		TimeoutThread tt = new TimeoutThread();
		try {
			tt.start();
			SystemUtil.sleep(timeout);
		}
		finally{
			if(tt.isAlive())tt.stop();
			print.e(tt.isAlive());
		}
	}
	
	public static void main(String[] args) {
		execute(1000);
	}
}
