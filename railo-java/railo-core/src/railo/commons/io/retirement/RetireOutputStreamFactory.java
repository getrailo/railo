package railo.commons.io.retirement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.SystemUtil;

public class RetireOutputStreamFactory {
	
	static List<RetireOutputStream> list=new ArrayList<RetireOutputStream>();
	private static RetireThread thread;
	
	static void startThread() {
		if(thread==null || !thread.isAlive()) {
			thread=new RetireThread();
			thread.start();
		}
	}

	static class RetireThread extends Thread {
		
		public void run(){
			//print.e("start thread");
			while(true){
				try{
					if(list.size()==0) break;
					SystemUtil.sleep(60000);
					Iterator<RetireOutputStream> it = list.iterator();
					while(it.hasNext()){
						it.next().retire();
					}
					
				}
				catch(Throwable t){}
			}
			//print.e("stop thread");
			thread=null;
		}
	}
}
