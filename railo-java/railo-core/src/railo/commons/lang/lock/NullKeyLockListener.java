package railo.commons.lang.lock;

public final class NullKeyLockListener implements KeyLockListener {

	private final static NullKeyLockListener my=new NullKeyLockListener();
	private NullKeyLockListener() {}
	
	public static KeyLockListener getInstance() {
		return my;
	}

	@Override
	public void onEnd(String key, boolean isLast) {}

	@Override
	public void onStart(String key, boolean isFirst) {}
	

}
