package railo.runtime.lock;

class ReadLockData implements LockData {

    private String name;
    private int id;

    /**
     * constructor of the class
     * @param token 
     * @param name name of the token
     * @param id id of the token
     */
    protected ReadLockData(String name, int id) {
    	this.name=name.toLowerCase();
        this.id=id;
    }

	/**
     * @see railo.runtime.lock.LockData#isReadOnly()
     */
    public boolean isReadOnly() {
        return true;
    }

    /**
     * @see railo.runtime.lock.LockData#getId()
     */
    public int getId() {
        return id;
    }

    /**
     * @see railo.runtime.lock.LockData#getName()
     */
    public String getName() {
        return name;
    }
}
