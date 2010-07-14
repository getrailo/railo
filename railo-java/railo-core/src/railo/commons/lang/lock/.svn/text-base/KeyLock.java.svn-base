package railo.commons.lang.lock;

public final class KeyLock {
	
	private final Token token=new Token();
	private KeyLockListener listener;

	public KeyLock() {
		this.listener=NullKeyLockListener.getInstance();
	}
	public KeyLock(KeyLockListener listener) {
		this.listener=listener;
	}

	public void start(String key) {
		while(true) {
			// nobody inside
			
			synchronized(token) {
				if(token.value==null) {
					token.value=key;
					token.count++;
					listener.onStart(token.value,true);      
					return;
				}
				if(key.equalsIgnoreCase(token.value)) {
					token.count++;
					listener.onStart(token.value,false);      
					return;
				}
				try {
					token.wait();
				} 
				catch (InterruptedException e) {}
			}
		}
	}
	
	public void end() {
		synchronized(token) {
			if(--token.count<=0) {
				listener.onEnd(token.value,true);    
				if(token.count<0)token.count=0;
				token.value=null;
			}
			else listener.onEnd(token.value,false); 
			token.notify();
		}
	}
	
	public void setListener(KeyLockListener listener) { 
		this.listener=listener;
	}
	
	
	
	
}

class Token {
	int count=0;
	String value=null;
}

