package railo.runtime.search;


public final class IndexResultImpl implements IndexResult {
	
	private int countDeleted;
	private int countInserted;
	private int countUpdated;
	
	public static IndexResult EMPTY=new IndexResultImpl(0,0,0);

	public IndexResultImpl(int countDeleted,int countInserted,int countUpdated) {
		this.countDeleted=countDeleted;
		this.countInserted=countInserted;
		this.countUpdated=countUpdated;
	}
	public IndexResultImpl() {
	}

	@Override
	public int getCountDeleted() {
		return countDeleted;
	}

	@Override
	public int getCountInserted() {
		return countInserted;
	}

	@Override
	public int getCountUpdated() {
		return countUpdated;
	}

	/**
	 * @param countDeleted the countDeleted to set
	 */
	public void setCountDeleted(int countDeleted) {
		this.countDeleted = countDeleted;
	}

	/**
	 * @param countInserted the countInserted to set
	 */
	public void setCountInserted(int countInserted) {
		this.countInserted = countInserted;
	}

	/**
	 * @param countUpdated the countUpdated to set
	 */
	public void setCountUpdated(int countUpdated) {
		this.countUpdated = countUpdated;
	}
	/**
	 * @param countDeleted the countDeleted to set
	 */
	public void incCountDeleted() {
		this.countDeleted++;
	}

	/**
	 * @param countInserted the countInserted to set
	 */
	public void incCountInserted() {
		this.countInserted++;
	}

	/**
	 * @param countUpdated the countUpdated to set
	 */
	public void incCountUpdated() {
		this.countUpdated++;
	}
	
}
